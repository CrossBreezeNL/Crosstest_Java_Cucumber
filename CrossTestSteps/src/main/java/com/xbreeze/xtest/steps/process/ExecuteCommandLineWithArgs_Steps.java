package com.xbreeze.xtest.steps.process;

import com.xbreeze.xtest.modules.process.Process_Helper;

import io.cucumber.datatable.DataTable;

import io.cucumber.java.en.When;
import io.cucumber.java.nl.Wanneer;

public class ExecuteCommandLineWithArgs_Steps
{

    private Process_Helper _Process_helper;

    public ExecuteCommandLineWithArgs_Steps(Process_Helper Process_helper) {
        _Process_helper = Process_helper;
    }

    @When("^I execute the ([a-zA-Z0-9_@$#]+) process using commandline with the following arguments:$")
    public void When_EN_ExecuteCommandLineWithArgs(
        String processConfigName,
        DataTable argsTable
    ) throws Throwable
    {
        _Process_helper.ExecuteCommandLineWithArgs(
            processConfigName,
            argsTable.asMaps()
        );
    }

    @Wanneer("^ik het ([a-zA-Z0-9_@$#]+) proces uitvoer via commandline met de volgende argumenten:$")
    public void When_NL_ExecuteCommandLineWithArgs(
        String processConfigName,
        DataTable argsTable
    ) throws Throwable
    {
        _Process_helper.ExecuteCommandLineWithArgs(
            processConfigName,
            argsTable.asMaps()
        );
    }

}
