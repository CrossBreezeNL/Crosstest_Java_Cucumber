package com.xbreeze.xtest.steps.process;
import com.xbreeze.xtest.modules.process.Process_Helper;

import io.cucumber.datatable.DataTable;

import io.cucumber.java.en.When;
import io.cucumber.java.nl.Wanneer;

public class ExecutedTemplatedCommand_Steps
{

    private Process_Helper _Process_helper;
    
    public ExecutedTemplatedCommand_Steps(Process_Helper Process_helper) {
        _Process_helper = Process_helper;
    }

    @When("^I execute the following ([a-zA-Z0-9_@$#.]+) command:$")
    public void When_EN_ExecutedTemplatedCommand_225F7653_8054_44F3_9D48_F66C0C0E47D5(
        String process_name,
        String command_text
    ) throws Throwable
    {
        _Process_helper.ExecutedTemplatedCommand(
            process_name,
            command_text
        );
    }

    @Wanneer("^ik het volgende ([a-zA-Z0-9_@$#.]+) commando uitvoer:$")
    public void When_NL_ExecutedTemplatedCommand_C49E4B70_4F39_4787_9DA4_28F8FABC47A8(
        String process_name,
        String command_text
    ) throws Throwable
    {
        _Process_helper.ExecutedTemplatedCommand(
            process_name,
            command_text
        );
    }


}