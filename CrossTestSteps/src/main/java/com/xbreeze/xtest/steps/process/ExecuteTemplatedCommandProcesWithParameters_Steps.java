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

    @When("^I execute the ([a-zA-Z0-9_@$#.]+) commandline process using the following arguments:$")
    public void When_EN_ExecuteTemplatedCommandProcesWithParameters_A04F1B71_77FF_4150_8AD7_01C4DA86614A(
        String process_name,
        DataTable parameter_table
    ) throws Throwable
    {
        _Process_helper.ExecuteTemplatedCommandProcesWithParameters(
            process_name,
            parameter_table
        );
    }

    @Wanneer("^ik het ([a-zA-Z0-9_@$#.]+) commandline proces uitvoer met de volgende argumenten:$")
    public void When_NL_ExecuteTemplatedCommandProcesWithParameters_B24DFA00_D88F_4AFD_BC49_C5252A8BB5AD(
        String process_name,
        DataTable parameter_table
    ) throws Throwable
    {
        _Process_helper.ExecuteTemplatedCommandProcesWithParameters(
            process_name,
            parameter_table
        );
    }


}