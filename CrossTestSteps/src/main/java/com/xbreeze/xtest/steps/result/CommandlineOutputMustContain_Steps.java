package com.xbreeze.xtest.steps.result;
import com.xbreeze.xtest.modules.result.Result_Helper;

import io.cucumber.datatable.DataTable;

import io.cucumber.java.en.Then;
import io.cucumber.java.nl.Dan;

public class CommandlineOutputMustContain_Steps
{

    private Result_Helper _Result_helper;
    
    public CommandlineOutputMustContain_Steps(Result_Helper Result_helper) {
        _Result_helper = Result_helper;
    }

    @Then("^the commandline output must contain:$")
    public void Then_EN_CommandlineOutputMustContain_3A385C74_8E7C_41B7_889D_D5B081B6603B(
        String command_text
    ) throws Throwable
    {
        _Result_helper.CommandlineOutputMustContain(
            command_text
        );
    }

    @Dan("^de commandline uitvoer het volgende moet bevatten:$")
    public void Then_NL_CommandlineOutputMustContain_85FDB1C9_DD0C_4B97_8589_5C4AB255568C(
        String command_text
    ) throws Throwable
    {
        _Result_helper.CommandlineOutputMustContain(
            command_text
        );
    }


}