package com.xbreeze.xtest.modules.result;

import com.xbreeze.xtest.config.XTestConfig;
import com.xbreeze.xtest.database.helpers.DataHelper;
import com.xbreeze.xtest.exception.XTestException;
import com.xbreeze.xtest.modules.data.database.Database_Helper;
// Besides database process, we also verify output from commandline execution which lives in Process_Helper
// TODO: In the future it might be cleaner to refactor such that the Result_Helper is split into database_result_helper and process_result_helper
import com.xbreeze.xtest.modules.process.Process_Helper;
import com.xbreeze.xtest.result.ResultContext;

import io.cucumber.datatable.DataTable;

public class Result_Helper extends Database_Helper{

	private Process_Helper _processHelper;

	public Result_Helper(ResultContext resultContext, DataHelper dataHelper, XTestConfig config, Process_Helper processHelper) throws XTestException {
		super(resultContext, dataHelper, config);
		this._processHelper = processHelper;
	}

	public void CompareExpectedAndActualResult(DataTable results) throws Throwable{
		//Transform expected results to row set and feed it to compare
		_resultContext.compareResult(results);
	}

	public void StoreContentsOfFieldInVariable(String fieldName, String variableName) throws Throwable{
		_resultContext.setVariable(fieldName, variableName);
	}

	/**
	 * Asserts that the commandline output matches the expected text exactly (after normalization).
	 * @param expectedText The expected output text.
	 */
	public void CommandlineOutputMustBe(String expectedText) throws Throwable {
		String actual = normalizeOutput(_processHelper.getLastCommandOutput()).trim();
		String expected = normalizeOutput(expectedText).trim();
		if (!expected.equals(actual)) {
			throw new AssertionError(String.format(
				"Commandline output mismatch.%nExpected:%n%s%nActual:%n%s", expected, actual
			));
		}
	}

	/**
	 * Asserts that the commandline output contains the expected text (after normalization).
	 * @param expectedText The expected text that should be contained in the output.
	 */
	public void CommandlineOutputMustContain(String expectedText) throws Throwable {
		String actual = normalizeOutput(_processHelper.getLastCommandOutput());
		String expected = normalizeOutput(expectedText);
		if (!actual.contains(expected)) {
			throw new AssertionError(String.format(
				"Commandline output does not contain expected text.%nExpected to contain:%n%s%nActual output:%n%s", expected, actual
			));
		}
	}

	/**
	 * Normalizes text for cross-platform consistency:
	 * - Replaces \r\n with \n (Windows line endings)
	 * - Strips trailing whitespace from each line
	 */
	private String normalizeOutput(String text) {
		if (text == null) {
			return "";
		}
		String[] lines = text.replace("\r\n", "\n").split("\n", -1);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lines.length; i++) {
			if (i > 0) {
				sb.append("\n");
			}
			// Trim trailing whitespace from each line
			int end = lines[i].length();
			while (end > 0 && lines[i].charAt(end - 1) <= ' ') {
				end--;
			}
			sb.append(lines[i].substring(0, end));
		}
		return sb.toString();
	}

}
