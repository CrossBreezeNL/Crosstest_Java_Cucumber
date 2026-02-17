package com.xbreeze.xtest.steps.process;
import com.xbreeze.xtest.modules.process.Process_Helper;

import io.cucumber.datatable.DataTable;

import io.cucumber.java.en.When;
import io.cucumber.java.nl.Wanneer;

public class ExecuteTemplatedCommandWithTemplatedProcessWithParameters_Steps
{

    private Process_Helper _Process_helper;
    
    public ExecuteTemplatedCommandWithTemplatedProcessWithParameters_Steps(Process_Helper Process_helper) {
        _Process_helper = Process_helper;
    }

    @When("^I execute the ([a-zA-Z0-9_@$#.]+) commandline process with ([a-zA-Z0-9_@$#.]+) using the following arguments:$")
    public void When_EN_ExecuteTemplatedCommandWithTemplatedProcessWithParameters_CC75A0EF_F47A_4CAF_9885_192CE9B58E5F(
        String process_name,
        String commandconfig_name,
        DataTable parameter_table
    ) throws Throwable
    {
        _Process_helper.ExecuteTemplatedCommandWithTemplatedProcessWithParameters(
            process_name,
            commandconfig_name,
            parameter_table
        );
    }

    @Wanneer("^ik het ([a-zA-Z0-9_@$#.]+) commandline proces uitvoer via ([a-zA-Z0-9_@$#.]+) met de volgende argumenten:$")
    public void When_NL_ExecuteTemplatedCommandWithTemplatedProcessWithParameters_26CABE72_3ECF_472E_9097_A48DC60C7478(
        String process_name,
        String commandconfig_name,
        DataTable parameter_table
    ) throws Throwable
    {
        _Process_helper.ExecuteTemplatedCommandWithTemplatedProcessWithParameters(
            process_name,
            commandconfig_name,
            parameter_table
        );
    }


}