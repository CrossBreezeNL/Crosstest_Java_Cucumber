package com.xbreeze.xtest.steps.result;
import com.xbreeze.xtest.modules.result.Result_Helper;

import io.cucumber.datatable.DataTable;

import io.cucumber.java.en.Then;
import io.cucumber.java.nl.Dan;

public class CommandlineOutputMustBe_Steps
{

    private Result_Helper _Result_helper;
    
    public CommandlineOutputMustBe_Steps(Result_Helper Result_helper) {
        _Result_helper = Result_helper;
    }

    @Then("^the commandline output must be:$")
    public void Then_EN_CommandlineOutputMustBe_CB727430_7890_4FDA_ADD1_0000AD6BC08D(
        String command_text
    ) throws Throwable
    {
        _Result_helper.CommandlineOutputMustBe(
            command_text
        );
    }

    @Dan("^de commandline uitvoer als volgt moet zijn:$")
    public void Then_NL_CommandlineOutputMustBe_F0545E9F_190F_4F49_A501_B7C2E116EE08(
        String command_text
    ) throws Throwable
    {
        _Result_helper.CommandlineOutputMustBe(
            command_text
        );
    }


}