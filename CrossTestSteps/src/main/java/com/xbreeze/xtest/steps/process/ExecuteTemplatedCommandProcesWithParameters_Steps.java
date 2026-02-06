package com.xbreeze.xtest.steps.process;
import com.xbreeze.xtest.modules.process.Process_Helper;

import io.cucumber.datatable.DataTable;

import io.cucumber.java.en.When;
import io.cucumber.java.nl.Wanneer;

public class ExecuteTemplatedCommandProcesWithParameters_Steps
{

    private Process_Helper _Process_helper;
    
    public ExecuteTemplatedCommandProcesWithParameters_Steps(Process_Helper Process_helper) {
        _Process_helper = Process_helper;
    }

    @When("^I execute the ([a-zA-Z0-9_@$#]+) process using commandline with the following arguments:$")
    public void When_EN_ExecuteTemplatedCommand_D63315D3_F17C_4C05_BD7A_49A30F26A734(
        String process_config,
        DataTable parameter_table
    ) throws Throwable
    {
        _Process_helper.ExecuteTemplatedCommandProcesWithParameters(
            process_config,
            parameter_table
        );
    }

    @Wanneer("^ik het ([a-zA-Z0-9_@$#]+) proces uitvoer via commandline met de volgende argumenten:$")
    public void When_NL_ExecuteTemplatedCommand_86E23373_26FB_411C_90A0_1A397CC88524(
        String process_config,
        DataTable parameter_table
    ) throws Throwable
    {
        _Process_helper.ExecuteTemplatedCommandProcesWithParameters(
            process_config,
            parameter_table
        );
    }


}