import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Writes one Markdown summary for the test runs of several matrix jobs (e.g. one per Java version),
 * for use as a GitHub Actions job summary.
 *
 * Each argument is "label=directory". The directory holds the files the job uploaded:
 *  - TestResults.xml: the Cucumber JUnit XML report (missing when the tests did not run);
 *  - job-status.txt:  the GitHub Actions job status (success, failure or cancelled).
 *
 * Usage: java .github/scripts/TestSummary.java "Java 17=results/java-17" ... >> "$GITHUB_STEP_SUMMARY"
 */
public class TestSummary {

    /** Maximum number of message lines shown per failed scenario. */
    private static final int MAX_MESSAGE_LINES = 25;

    private static final String PASSED = ":white_check_mark:";
    private static final String FAILED = ":x:";
    private static final String SKIPPED = ":fast_forward:";

    private static class Counts {
        int passed;
        int failed;
        int skipped;
        double seconds;

        int total() {
            return passed + failed + skipped;
        }
    }

    /** The results of one matrix job. */
    private static class Run {
        final String label;
        /** The job status, or null when the job did not record it. */
        final String jobStatus;
        /** Whether the job produced a test report. */
        final boolean hasReport;
        final Counts totals = new Counts();
        final Map<String, Counts> features = new LinkedHashMap<>();
        /** Failure message per "feature\0scenario" key. */
        final Map<String, String> failures = new LinkedHashMap<>();

        Run(String label, String jobStatus, boolean hasReport) {
            this.label = label;
            this.jobStatus = jobStatus;
            this.hasReport = hasReport;
        }

        boolean passed() {
            return hasReport && totals.failed == 0 && (jobStatus == null || "success".equals(jobStatus));
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Usage: java TestSummary.java <label=directory>...");
            System.exit(2);
        }
        List<Run> runs = new ArrayList<>();
        for (String arg : args) {
            int separator = arg.indexOf('=');
            if (separator < 1) {
                System.err.println("Expected label=directory, got: " + arg);
                System.exit(2);
            }
            runs.add(readRun(arg.substring(0, separator), new File(arg.substring(separator + 1))));
        }
        System.out.print(render(runs));
    }

    private static Run readRun(String label, File directory) throws Exception {
        File statusFile = new File(directory, "job-status.txt");
        String jobStatus = statusFile.isFile()
            ? new String(Files.readAllBytes(statusFile.toPath()), StandardCharsets.UTF_8).trim()
            : null;
        File reportFile = new File(directory, "TestResults.xml");
        Run run = new Run(label, jobStatus, reportFile.isFile());
        if (!run.hasReport) {
            return run;
        }

        NodeList testCases = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .parse(reportFile).getElementsByTagName("testcase");
        for (int i = 0; i < testCases.getLength(); i++) {
            Element testCase = (Element) testCases.item(i);
            String feature = testCase.getAttribute("classname");
            Counts featureCounts = run.features.computeIfAbsent(feature, k -> new Counts());
            double seconds = parseSeconds(testCase.getAttribute("time"));
            run.totals.seconds += seconds;
            featureCounts.seconds += seconds;

            Element failure = firstChild(testCase, "failure", "error");
            if (failure != null) {
                run.totals.failed++;
                featureCounts.failed++;
                run.failures.put(feature + '\0' + testCase.getAttribute("name"), failureMessage(failure));
            } else if (firstChild(testCase, "skipped") != null) {
                run.totals.skipped++;
                featureCounts.skipped++;
            } else {
                run.totals.passed++;
                featureCounts.passed++;
            }
        }
        return run;
    }

    private static String render(List<Run> runs) {
        StringBuilder out = new StringBuilder();
        out.append("## Test results\n\n");

        long passedRuns = runs.stream().filter(Run::passed).count();
        out.append(passedRuns == runs.size()
            ? String.format(Locale.ROOT, "%s **Passed on all %d versions**%n%n", PASSED, runs.size())
            : String.format(Locale.ROOT, "%s **Failed on %d of %d versions**%n%n", FAILED, runs.size() - passedRuns, runs.size()));

        // One row per run.
        out.append("| Version | Result | Scenarios | Passed | Failed | Skipped | Duration |\n");
        out.append("|:--|:--|--:|--:|--:|--:|--:|\n");
        for (Run run : runs) {
            if (!run.hasReport) {
                out.append(String.format(Locale.ROOT, "| %s | %s %s | | | | | |%n",
                    escapeTableCell(run.label), FAILED,
                    run.jobStatus == null ? "No test report" : "No test report (job " + run.jobStatus + ")"));
                continue;
            }
            Counts c = run.totals;
            String result = run.passed() ? PASSED + " Passed"
                : c.failed > 0 ? FAILED + " Failed" : FAILED + " Job " + run.jobStatus;
            out.append(String.format(Locale.ROOT, "| %s | %s | %d | %d | %d | %d | %s |%n",
                escapeTableCell(run.label), result, c.total(), c.passed, c.failed, c.skipped, formatSeconds(c.seconds)));
        }
        out.append('\n');

        // Failed scenarios, each listed once with the versions it failed on.
        Map<String, List<Run>> failedOn = new LinkedHashMap<>();
        for (Run run : runs) {
            for (String key : run.failures.keySet()) {
                failedOn.computeIfAbsent(key, k -> new ArrayList<>()).add(run);
            }
        }
        if (!failedOn.isEmpty()) {
            out.append("### Failed scenarios\n\n");
            for (Map.Entry<String, List<Run>> entry : failedOn.entrySet()) {
                String[] featureAndScenario = entry.getKey().split("\0", 2);
                List<String> labels = new ArrayList<>();
                for (Run run : entry.getValue()) {
                    labels.add(run.label);
                }
                Run first = entry.getValue().get(0);
                out.append("<details><summary>").append(FAILED).append(" <b>").append(escapeHtml(featureAndScenario[0]))
                    .append("</b> &rsaquo; ").append(escapeHtml(featureAndScenario[1]))
                    .append(" (").append(escapeHtml(String.join(", ", labels))).append(")</summary>\n\n");
                out.append("<pre>").append(escapeHtml(first.failures.get(entry.getKey()))).append("</pre>\n</details>\n\n");
            }
        }

        // One row per feature, one column per run.
        Map<String, Boolean> featureNames = new LinkedHashMap<>();
        for (Run run : runs) {
            for (String feature : run.features.keySet()) {
                featureNames.put(feature, Boolean.TRUE);
            }
        }
        if (!featureNames.isEmpty()) {
            out.append("<details><summary><b>Results per feature</b></summary>\n\n");
            out.append("| Feature |");
            for (Run run : runs) {
                out.append(' ').append(escapeTableCell(run.label)).append(" |");
            }
            out.append("\n|:--|");
            for (int i = 0; i < runs.size(); i++) {
                out.append(":-:|");
            }
            out.append('\n');
            for (String feature : featureNames.keySet()) {
                out.append("| ").append(escapeTableCell(feature)).append(" |");
                for (Run run : runs) {
                    out.append(' ').append(featureCell(run, feature)).append(" |");
                }
                out.append('\n');
            }
            out.append("\n</details>\n");
        }
        return out.toString();
    }

    /** A feature's result for one run: the status icon, followed by passed/total scenarios. */
    private static String featureCell(Run run, String feature) {
        Counts c = run.features.get(feature);
        if (c == null) {
            return "&ndash;";
        }
        String icon = c.failed > 0 ? FAILED : (c.passed > 0 ? PASSED : SKIPPED);
        return String.format(Locale.ROOT, "%s %d/%d", icon, c.passed, c.total());
    }

    private static Element firstChild(Element parent, String... tagNames) {
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof Element) {
                for (String tagName : tagNames) {
                    if (tagName.equals(child.getNodeName())) {
                        return (Element) child;
                    }
                }
            }
        }
        return null;
    }

    /** The failure text, without the stack trace lines, limited to MAX_MESSAGE_LINES lines. */
    private static String failureMessage(Element failure) {
        String text = failure.getTextContent().trim();
        if (text.isEmpty()) {
            text = failure.getAttribute("message");
        }
        StringBuilder message = new StringBuilder();
        int lines = 0;
        for (String line : text.split("\\r?\\n")) {
            if (line.trim().startsWith("at ")) {
                continue;
            }
            if (lines == MAX_MESSAGE_LINES) {
                message.append("...\n");
                break;
            }
            message.append(line).append('\n');
            lines++;
        }
        return message.toString().trim();
    }

    private static double parseSeconds(String value) {
        try {
            return Double.parseDouble(value.replace(",", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String formatSeconds(double seconds) {
        return seconds < 60
            ? String.format(Locale.ROOT, "%.1fs", seconds)
            : String.format(Locale.ROOT, "%dm %02ds", (int) seconds / 60, (int) seconds % 60);
    }

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String escapeTableCell(String text) {
        return escapeHtml(text).replace("|", "\\|");
    }
}
