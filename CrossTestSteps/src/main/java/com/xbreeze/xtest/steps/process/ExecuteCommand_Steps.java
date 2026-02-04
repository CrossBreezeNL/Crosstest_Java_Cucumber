package com.xbreeze.xtest.steps.process;
import com.xbreeze.xtest.modules.process.Process_Helper;

import io.cucumber.datatable.DataTable;

import io.cucumber.java.en.When;
import io.cucumber.java.nl.Wanneer;

public class ExecuteCommand_Steps
{

    private Process_Helper _Process_helper;
    
    public ExecuteCommand_Steps(Process_Helper Process_helper) {
        _Process_helper = Process_helper;
    }

    @When("I execute the following command")
    public void When_EN_ExecuteCommand_D63315D3_F17C_4C05_BD7A_49A30F26A734(
        String command_text
    ) throws Throwable
    {
        _Process_helper.ExecuteCommand(
            command_text
        );
    }

    @Wanneer("ik het volgende commando uitvoer")
    public void When_NL_ExecuteCommand_86E23373_26FB_411C_90A0_1A397CC88524(
        String command_text
    ) throws Throwable
    {
        _Process_helper.ExecuteCommand(
            command_text
        );
    }


}