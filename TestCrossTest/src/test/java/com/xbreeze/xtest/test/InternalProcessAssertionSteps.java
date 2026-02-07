package com.xbreeze.xtest.test;

import com.xbreeze.xtest.modules.process.Process_Helper;

import io.cucumber.java.en.Then;
import io.cucumber.java.nl.Dan;

/**
 * Internal step definitions for testing CrossTest's command assembly logic.
 * These steps are NOT part of the published CrossTest library.
 */
public class InternalProcessAssertionSteps {

    private Process_Helper _Process_helper;

    public InternalProcessAssertionSteps(Process_Helper Process_helper) {
        _Process_helper = Process_helper;
    }

    @Then("^the assembled commandline should be:$")
    public void Then_EN_AssembledCommandlineShouldBe(String expectedCommand) throws Throwable {
        String actual = _Process_helper.getLastAssembledCommand();
        String expected = expectedCommand.trim();
        if (!expected.equals(actual)) {
            throw new AssertionError(String.format(
                "Assembled command mismatch.%nExpected: %s%nActual:   %s", expected, actual
            ));
        }
    }

    @Dan("^moet het samengestelde commando als volgt zijn:$")
    public void Then_NL_AssembledCommandlineShouldBe(String expectedCommand) throws Throwable {
        Then_EN_AssembledCommandlineShouldBe(expectedCommand);
    }

    @Then("^the execution tool prefix on (windows|non-windows) os should be:$")
    public void Then_EN_ExecutionToolPrefixShouldBe(String osType, String expectedPrefix) throws Throwable {
        String actual = _Process_helper.getLastExecutionToolPrefix(osType);
        String expected = expectedPrefix.trim();
        if (!expected.equals(actual)) {
            throw new AssertionError(String.format(
                "Execution tool prefix mismatch for %s.%nExpected: %s%nActual:   %s", osType, expected, actual
            ));
        }
    }

    @Dan("^moet de gebruikte executie tool vooraf aan het commando op (windows|niet-windows) os als volgt zijn:$")
    public void Then_NL_ExecutionToolPrefixShouldBe(String osType, String expectedPrefix) throws Throwable {
        String nlToEn = "niet-windows".equals(osType) ? "non-windows" : osType;
        Then_EN_ExecutionToolPrefixShouldBe(nlToEn, expectedPrefix);
    }

}
