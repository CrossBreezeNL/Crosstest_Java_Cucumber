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

    @When("^I execute the following command on (windows|non-windows) os:$")
    public void When_EN_ExecuteCommandOnOs(
        String osType,
        String command_text
    ) throws Throwable
    {
        _Process_helper.ExecuteCommandForOs(
            command_text,
            osType
        );
    }

    @Wanneer("^ik het volgende commando uitvoer op (windows|niet-windows) os:$")
    public void When_NL_ExecuteCommandOnOs(
        String osType,
        String command_text
    ) throws Throwable
    {
        String nlToEn = "niet-windows".equals(osType) ? "non-windows" : osType;
        _Process_helper.ExecuteCommandForOs(
            command_text,
            nlToEn
        );
    }

    @When("^I execute the following ([a-zA-Z0-9_@$#]+) command:$")
    public void When_EN_ExecuteCommandWithConfig(
        String commandLineConfigName,
        String command_text
    ) throws Throwable
    {
        _Process_helper.ExecuteCommand(
            command_text,
            commandLineConfigName
        );
    }

    @Wanneer("^ik het volgende ([a-zA-Z0-9_@$#]+) commando uitvoer:$")
    public void When_NL_ExecuteCommandWithConfig(
        String commandLineConfigName,
        String command_text
    ) throws Throwable
    {
        _Process_helper.ExecuteCommand(
            command_text,
            commandLineConfigName
        );
    }

    @When("^I execute the following ([a-zA-Z0-9_@$#]+) command on (windows|non-windows) os:$")
    public void When_EN_ExecuteCommandWithConfigOnOs(
        String commandLineConfigName,
        String osType,
        String command_text
    ) throws Throwable
    {
        _Process_helper.ExecuteCommandForOs(
            command_text,
            commandLineConfigName,
            osType
        );
    }

    @Wanneer("^ik het volgende ([a-zA-Z0-9_@$#]+) commando uitvoer op (windows|niet-windows) os:$")
    public void When_NL_ExecuteCommandWithConfigOnOs(
        String commandLineConfigName,
        String osType,
        String command_text
    ) throws Throwable
    {
        String nlToEn = "niet-windows".equals(osType) ? "non-windows" : osType;
        _Process_helper.ExecuteCommandForOs(
            command_text,
            commandLineConfigName,
            nlToEn
        );
    }

}