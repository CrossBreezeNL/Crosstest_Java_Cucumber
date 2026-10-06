import java.io.File;
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
 * Writes a Markdown summary of a Cucumber JUnit XML report (TestCrossTest/target/TestResults.xml),
 * for use as a GitHub Actions job summary.
 *
 * Usage: java .github/scripts/TestSummary.java <TestResults.xml> [heading] >> "$GITHUB_STEP_SUMMARY"
 */
public class TestSummary {

    /** Maximum number of message lines shown per failed scenario. */
    private static final int MAX_MESSAGE_LINES = 25;

    private static class Counts {
        int passed;
        int failed;
        int skipped;
        double seconds;

        int total() {
            return passed + failed + skipped;
        }
    }

    private static class Failure {
        final String feature;
        final String scenario;
        final String message;

        Failure(String feature, String scenario, String message) {
            this.feature = feature;
            this.scenario = scenario;
            this.message = message;
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1 || args.length > 2) {
            System.err.println("Usage: java TestSummary.java <TestResults.xml> [heading]");
            System.exit(2);
        }
        File reportFile = new File(args[0]);
        String heading = args.length == 2 ? args[1] : "Test results";
        if (!reportFile.isFile()) {
            System.out.println("## " + heading);
            System.out.println();
            System.out.println(":warning: No test report found at `" + args[0] + "`; the tests did not run.");
            return;
        }

        NodeList testCases = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .parse(reportFile).getElementsByTagName("testcase");

        Counts totals = new Counts();
        Map<String, Counts> features = new LinkedHashMap<>();
        List<Failure> failures = new ArrayList<>();

        for (int i = 0; i < testCases.getLength(); i++) {
            Element testCase = (Element) testCases.item(i);
            String feature = testCase.getAttribute("classname");
            Counts featureCounts = features.computeIfAbsent(feature, k -> new Counts());
            double seconds = parseSeconds(testCase.getAttribute("time"));
            totals.seconds += seconds;
            featureCounts.seconds += seconds;

            Element failure = firstChild(testCase, "failure", "error");
            if (failure != null) {
                totals.failed++;
                featureCounts.failed++;
                failures.add(new Failure(feature, testCase.getAttribute("name"), failureMessage(failure)));
            } else if (firstChild(testCase, "skipped") != null) {
                totals.skipped++;
                featureCounts.skipped++;
            } else {
                totals.passed++;
                featureCounts.passed++;
            }
        }

        StringBuilder out = new StringBuilder();
        out.append("## ").append(heading).append("\n\n");
        out.append(totals.failed == 0 ? ":white_check_mark: " : ":x: ")
            .append(String.format(Locale.ROOT, "**%d scenarios: %d passed, %d failed, %d skipped** in %s%n%n",
                totals.total(), totals.passed, totals.failed, totals.skipped, formatSeconds(totals.seconds)));

        if (!failures.isEmpty()) {
            out.append("### Failed scenarios\n\n");
            for (Failure failure : failures) {
                out.append("<details><summary>:x: <b>").append(escapeHtml(failure.feature)).append("</b> &rsaquo; ")
                    .append(escapeHtml(failure.scenario)).append("</summary>\n\n");
                out.append("<pre>").append(escapeHtml(failure.message)).append("</pre>\n</details>\n\n");
            }
        }

        out.append("### Features\n\n");
        out.append("| | Feature | Passed | Failed | Skipped | Duration |\n");
        out.append("|:-:|:--|--:|--:|--:|--:|\n");
        for (Map.Entry<String, Counts> entry : features.entrySet()) {
            Counts c = entry.getValue();
            out.append(String.format(Locale.ROOT, "| %s | %s | %d | %d | %d | %s |%n",
                c.failed > 0 ? ":x:" : (c.passed > 0 ? ":white_check_mark:" : ":fast_forward:"),
                escapeTableCell(entry.getKey()), c.passed, c.failed, c.skipped, formatSeconds(c.seconds)));
        }

        System.out.print(out);
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
