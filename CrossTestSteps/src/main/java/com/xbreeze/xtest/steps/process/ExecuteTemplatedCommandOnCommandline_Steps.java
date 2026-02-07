package com.xbreeze.xtest.steps.process;
import com.xbreeze.xtest.modules.process.Process_Helper;

import io.cucumber.datatable.DataTable;

import io.cucumber.java.en.When;
import io.cucumber.java.nl.Wanneer;

public class ExecuteTemplatedCommandOnCommandline_Steps
{

    private Process_Helper _Process_helper;

    public ExecuteTemplatedCommandOnCommandline_Steps(Process_Helper Process_helper) {
        _Process_helper = Process_helper;
    }

    @When("^I execute the ([a-zA-Z0-9_@$#]+) process on ([a-zA-Z0-9_@$#]+) with the following arguments:$")
    public void When_EN_ExecuteTemplatedCommandOnCommandline(
        String process_config,
        String commandline_config,
        DataTable parameter_table
    ) throws Throwable
    {
        _Process_helper.ExecuteTemplatedCommandProcesWithParameters(
            process_config,
            parameter_table,
            commandline_config
        );
    }

    @Wanneer("^ik het ([a-zA-Z0-9_@$#]+) proces uitvoer op ([a-zA-Z0-9_@$#]+) met de volgende argumenten:$")
    public void When_NL_ExecuteTemplatedCommandOnCommandline(
        String process_config,
        String commandline_config,
        DataTable parameter_table
    ) throws Throwable
    {
        _Process_helper.ExecuteTemplatedCommandProcesWithParameters(
            process_config,
            parameter_table,
            commandline_config
        );
    }

}
