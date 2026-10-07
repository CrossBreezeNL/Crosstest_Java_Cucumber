package com.xbreeze.xtest.test;

import com.xbreeze.xtest.exception.XTestDatabaseException;
import com.xbreeze.xtest.exception.XTestException;
import com.xbreeze.xtest.modules.data.database.dbtable.DbTable_Helper;
import com.xbreeze.xtest.modules.data.database.query.Query_Helper;
import com.xbreeze.xtest.modules.result.Result_Helper;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Internal step definitions for testing that CrossTest reports failures correctly.
 * Each step runs a regular CrossTest action, expects it to fail and checks the failure message,
 * so negative scenarios pass when CrossTest fails for the expected reason.
 * These steps are NOT part of the published CrossTest library.
 */
public class InternalNegativeAssertionSteps {

    private Result_Helper _Result_helper;
    private Query_Helper _Query_helper;
    private DbTable_Helper _DbTable_helper;

    public InternalNegativeAssertionSteps(Result_Helper Result_helper, Query_Helper Query_helper, DbTable_Helper DbTable_helper) {
        _Result_helper = Result_helper;
        _Query_helper = Query_helper;
        _DbTable_helper = DbTable_helper;
    }

    @Given("^emptying the ([a-zA-Z0-9_@$#-]+) table (.+) should fail with \"(.*)\"$")
    public void Given_EN_DeleteTemplatedTableDataExpectingFailure(String databaseConfigName, String tableName, String expectedMessage) throws Throwable {
        try {
            _DbTable_helper.DeleteTemplatedTableData(databaseConfigName, tableName);
        } catch (XTestException e) {
            assertMessageContains(e, expectedMessage);
            return;
        }
        throw new AssertionError(String.format(
            "Expected emptying table %s to fail with '%s', but it succeeded.", tableName, expectedMessage
        ));
    }

    @Then("^I expect the following result to fail with \"(.*)\":$")
    public void Then_EN_CompareExpectedAndActualResultExpectingFailure(String expectedMessage, DataTable expectedResults) throws Throwable {
        try {
            _Result_helper.CompareExpectedAndActualResult(expectedResults);
        } catch (AssertionError | XTestException e) {
            assertMessageContains(e, expectedMessage);
            return;
        }
        throw new AssertionError(String.format(
            "Expected the result comparison to fail with '%s', but it succeeded.", expectedMessage
        ));
    }

    @When("^I execute the following query on ([a-zA-Z0-9_@$#-]+) expecting a timeout:$")
    public void When_EN_ExecuteQueryExpectingTimeout(String databaseConfigName, String queryText) throws Throwable {
        try {
            _Query_helper.ExecuteTheFollowingQueryOnConnection(databaseConfigName, queryText);
        } catch (XTestDatabaseException e) {
            assertMessageContains(e, "timed out");
            return;
        }
        throw new AssertionError("Expected the query to time out, but it completed successfully.");
    }

    private static void assertMessageContains(Throwable actualFailure, String expectedMessage) {
        String actualMessage = actualFailure.getMessage();
        if (actualMessage == null || !actualMessage.contains(expectedMessage)) {
            throw new AssertionError(String.format(
                "Expected a failure containing '%s', but got: %s", expectedMessage, actualFailure
            ), actualFailure);
        }
    }

}
