package com.xbreeze.xtest.steps.process;
import com.xbreeze.xtest.modules.process.Process_Helper;

import io.cucumber.datatable.DataTable;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.nl.Gegeven;

public class ExecuteCommand_Steps
{

    private Process_Helper _Process_helper;
    
    public ExecuteCommand_Steps(Process_Helper Process_helper) {
        _Process_helper = Process_helper;
    }

    @When("I execute the following command")
    public void Given_EN_ExecuteCommand(
        String command_text
    ) throws Throwable
    {
        _Process_helper.ExecuteCommand(
            command_text
        );
    }

    @Gegeven("ik het volgende commando uitvoer")
    public void Given_NL_ExecuteCommand(
        String command_text
    ) throws Throwable
    {
        _Process_helper.ExecuteCommand(
            command_text
        );
    }


}