package com.xbreeze.xtest.steps.result;

import com.xbreeze.xtest.modules.process.Process_Helper;

import io.cucumber.java.en.Then;
import io.cucumber.java.nl.Dan;

public class CommandlineOutputAssertion_Steps {

    private Process_Helper _Process_helper;

    public CommandlineOutputAssertion_Steps(Process_Helper Process_helper) {
        _Process_helper = Process_helper;
    }

    @Then("^the commandline output should contain:$")
    public void Then_EN_CommandlineOutputShouldContain(String expectedText) throws Throwable {
        String actual = normalize(_Process_helper.getLastCommandOutput());
        String expected = normalize(expectedText);
        if (!actual.contains(expected)) {
            throw new AssertionError(String.format(
                "Commandline output does not contain expected text.%nExpected to contain:%n%s%nActual output:%n%s", expected, actual
            ));
        }
    }

    @Dan("^de commandline uitvoer het volgende moet bevatten:$")
    public void Then_NL_CommandlineOutputShouldContain(String expectedText) throws Throwable {
        Then_EN_CommandlineOutputShouldContain(expectedText);
    }

    @Then("^the commandline output should be:$")
    public void Then_EN_CommandlineOutputShouldBe(String expectedText) throws Throwable {
        String actual = normalize(_Process_helper.getLastCommandOutput()).trim();
        String expected = normalize(expectedText).trim();
        if (!expected.equals(actual)) {
            throw new AssertionError(String.format(
                "Commandline output mismatch.%nExpected:%n%s%nActual:%n%s", expected, actual
            ));
        }
    }

    @Dan("^de commandline uitvoer als volgt moet zijn:$")
    public void Then_NL_CommandlineOutputShouldBe(String expectedText) throws Throwable {
        Then_EN_CommandlineOutputShouldBe(expectedText);
    }

    /**
     * Normalizes text for cross-platform consistency:
     * - Replaces \r\n with \n (Windows line endings)
     * - Strips trailing whitespace from each line (e.g., Windows cmd adds trailing spaces when chaining with &&)
     */
    private String normalize(String text) {
        if (text == null) {
            return "";
        }
        String[] lines = text.replace("\r\n", "\n").split("\n", -1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                sb.append("\n");
            }
            sb.append(trimTrailing(lines[i]));
        }
        return sb.toString();
    }

    private String trimTrailing(String line) {
        int end = line.length();
        while (end > 0 && line.charAt(end - 1) <= ' ') {
            end--;
        }
        return line.substring(0, end);
    }

}
