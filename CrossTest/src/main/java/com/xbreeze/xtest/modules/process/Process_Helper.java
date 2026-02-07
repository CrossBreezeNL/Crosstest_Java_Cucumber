package com.xbreeze.xtest.modules.process;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.xbreeze.xtest.config.CommandLineConfig;
import com.xbreeze.xtest.config.ConfigProperty;
import com.xbreeze.xtest.config.ProcessConfig;
import com.xbreeze.xtest.config.ProcessServerConfig;
import com.xbreeze.xtest.config.SecurableConfigProperty;
import com.xbreeze.xtest.config.XTestConfig;
import com.xbreeze.xtest.database.helpers.DataHelper;
import com.xbreeze.xtest.exception.XTestException;
import com.xbreeze.xtest.exception.XTestProcessException;
import com.xbreeze.xtest.modules.security.CredentialProvider_Helper;
import com.xbreeze.xtest.process.execution.CommandLineProcessExecutor;
import com.xbreeze.xtest.process.execution.ProcessExecutor;

import io.cucumber.datatable.DataTable;

public class Process_Helper {

	private XTestConfig _config;
	private CredentialProvider_Helper _credentialProviderHelper;
	static final Logger logger = Logger.getLogger(DataHelper.class.getName());
	private HashMap<String, ProcessExecutor> _executors;

	public Process_Helper(CredentialProvider_Helper credentialProviderHelper, XTestConfig cfg) throws XTestException{
		this._config = cfg.getConfig();
		this._credentialProviderHelper = credentialProviderHelper;
		this._executors = new HashMap<>();
	}
	
	public void RunTemplatedProcess(String processConfig, String processName) throws Throwable{	
		getProcessExecutor(processConfig).runProcess(_config.getProcessConfig(processConfig), processName);		
	}

	/**
	 * Executes a raw command string on the commandline.
	 * Uses CommandLineProcessExecutor directly, so no ProcessServerConfig binding is required in the XML config.
	 * @param command_text The command to execute.
	 */
	public void ExecuteCommand(String command_text) throws Throwable{
		getCommandLineExecutor().runProcess(null, command_text);
	}

	/**
	 * Executes a raw command string on the commandline, but only if the current OS matches the given osType.
	 * If the OS does not match, the command is silently skipped.
	 * @param command_text The command to execute.
	 * @param osType "windows" or "non-windows".
	 */
	public void ExecuteCommandForOs(String command_text, String osType) throws Throwable{
		if (isCurrentOs(osType)) {
			ExecuteCommand(command_text);
		}
	}

	/**
	 * Executes a raw command string on the commandline using a specific CommandLineConfig,
	 * but only if the current OS matches the given osType.
	 * If the OS does not match, the command is silently skipped.
	 * @param command_text The command to execute.
	 * @param commandLineConfigName The name of the CommandLineConfig to use.
	 * @param osType "windows" or "non-windows".
	 */
	public void ExecuteCommandForOs(String command_text, String commandLineConfigName, String osType) throws Throwable{
		if (isCurrentOs(osType)) {
			ExecuteCommand(command_text, commandLineConfigName);
		}
	}

	/**
	 * Executes a raw command string on the commandline using a specific CommandLineConfig.
	 * @param command_text The command to execute.
	 * @param commandLineConfigName The name of the CommandLineConfig to use.
	 */
	public void ExecuteCommand(String command_text, String commandLineConfigName) throws Throwable{
		CommandLineConfig clConfig = _config.getCommandLineConfig(commandLineConfigName);
		getCommandLineExecutor().runProcess(null, command_text, clConfig);
	}

	/**
	 * Assembles and executes a commandline process using a ProcessConfig and a table of arguments.
	 * The command is built from four segments: {command} {starting_args} {feature_args} {ending_args}.
	 *
	 * Special argument names in the table (command, starting_args, ending_args) override the corresponding
	 * config values. All other arguments become feature_args, replacing the feature_args config default.
	 *
	 * Config-level parameters that are not reserved names (command, starting_args, feature_args, ending_args,
	 * arg_key_prefix, arg_key_value_separator, arg_value_format, group_format, group_entry_format,
	 * group_entry_separator) and do not contain a dot are treated as regular argument defaults. These are
	 * merged with feature table entries: feature entries override config defaults with the same name, and
	 * new feature entries are appended. Config defaults not overridden are kept.
	 *
	 * Dot-notation arguments (e.g., vars.db, vars.ldts) are grouped together and formatted using the
	 * group_format, group_entry_format, and group_entry_separator config parameters.
	 * Config-level dot-notation parameters serve as group defaults that can be overridden or extended
	 * by feature-level entries.
	 *
	 * Uses CommandLineProcessExecutor directly, so no ProcessServerConfig binding is required in the XML config.
	 *
	 * @param processConfigName The name of the ProcessConfig to use.
	 * @param dataTable The table of arguments. The first column is the argument name, the second column is the value.
	 */
	public void ExecuteTemplatedCommandProcesWithParameters(String processConfigName, DataTable dataTable) throws Throwable {
		List<List<String>> argsTable = dataTable.asLists();
		ProcessConfig processConfig = _config.getProcessConfig(processConfigName);
		if (processConfig == null) {
			throw new XTestProcessException(String.format("ProcessConfig '%s' not found", processConfigName));
		}

		// Read command, starting_args, feature_args, ending_args from config parameters (default to empty)
		String commandParam = getParameterValue(processConfig, "command", "");
		String startingArgs = getParameterValue(processConfig, "starting_args", "");
		String featureArgs = getParameterValue(processConfig, "feature_args", "");
		String endingArgs = getParameterValue(processConfig, "ending_args", "");

		// Read general arg formatting parameters
		String argKeyPrefix = getParameterValue(processConfig, "arg_key_prefix", "--");
		String argKeyValueSeparator = getParameterValue(processConfig, "arg_key_value_separator", " ");
		String argValueFormat = getParameterValue(processConfig, "arg_value_format", "{value}");

		// Reserved parameter names that control command assembly and formatting, not treated as regular arguments
		Set<String> reservedParams = new HashSet<>(Arrays.asList(
			"command", "starting_args", "feature_args", "ending_args",
			"arg_key_prefix", "arg_key_value_separator", "arg_value_format",
			"group_format", "group_entry_format", "group_entry_separator"
		));

		// slotNames tracks the ordering of arguments in the final command.
		// It contains either regular arg names (e.g., "select") or group placeholders prefixed
		// with "__GROUP__" (e.g., "__GROUP__vars"). These placeholders are resolved later into
		// formatted group strings like: --vars "{'db': 'my_db', 'ldts': '2025-01-01'}"
		// Feature table entries are processed first and define the initial order. Config-level defaults
		// that are not referenced in the feature table are appended after all feature entries.
		ArrayList<String> slotNames = new ArrayList<>();
		// regularArgs maps argument names to their values (from feature table and/or config defaults)
		LinkedHashMap<String, String> regularArgs = new LinkedHashMap<>();
		// groups maps group prefixes (e.g., "vars") to their feature-level key-value entries
		LinkedHashMap<String, ArrayList<String[]>> groups = new LinkedHashMap<>();

		// Process feature table rows first (skip header row), establishing feature-defined order.
		// Each row is classified as: a special segment override, a grouped (dot-notation) arg, or a regular arg.
		for (int i = 1; i < argsTable.size(); i++) {
			List<String> row = argsTable.get(i);
			String argName = row.get(0);
			String argValue = row.size() > 1 && row.get(1) != null ? row.get(1) : "";

			// Special segment overrides: these replace the corresponding config values directly
			if ("command".equals(argName)) {
				commandParam = argValue;
			} else if ("starting_args".equals(argName)) {
				startingArgs = argValue;
			} else if ("ending_args".equals(argName)) {
				endingArgs = argValue;
			} else if (argName.contains(".")) {
				// Dot-notation: split on first dot into group prefix and key.
				// For example, "vars.db" becomes prefix="vars", key="db".
				int dotIndex = argName.indexOf('.');
				String prefix = argName.substring(0, dotIndex);
				String key = argName.substring(dotIndex + 1);
				// If this is the first entry for this group prefix, register a placeholder in the
				// slot order so the group appears at this position in the final command.
				if (!groups.containsKey(prefix)) {
					groups.put(prefix, new ArrayList<String[]>());
					slotNames.add("__GROUP__" + prefix);
				}
				// Add the key-value pair to the group. Multiple entries with the same prefix
				// (e.g., vars.db and vars.ldts) are collected together and later formatted by buildGroupArg.
				groups.get(prefix).add(new String[]{key, argValue});
			} else {
				// Regular arg from feature table.
				// If this arg name already exists (e.g., overriding a config default), update the value
				// but keep its existing position. If it is new, register it in the slot order.
				if (!regularArgs.containsKey(argName)) {
					slotNames.add(argName);
				}
				regularArgs.put(argName, argValue);
			}
		}

		// Append config-level defaults not already provided by the feature table.
		// This ensures that config arguments the feature table didn't mention are still included.
		for (ConfigProperty param : processConfig.getParameters()) {
			String name = param.getName();
			// Skip reserved parameters (they control formatting, not arguments)
			if (reservedParams.contains(name)) {
				continue;
			}
			if (name.contains(".")) {
				// Dot-notation config parameter (e.g., vars.db=default_database).
				// Register the group if the feature table didn't already reference it.
				// The actual config default values are merged later by buildGroupArg.
				int dotIndex = name.indexOf('.');
				String prefix = name.substring(0, dotIndex);
				if (!groups.containsKey(prefix)) {
					groups.put(prefix, new ArrayList<String[]>());
					slotNames.add("__GROUP__" + prefix);
				}
			} else {
				// Regular arg config default (e.g., select=my_default_model).
				// Only add if the feature table didn't already provide a value for this arg.
				if (!regularArgs.containsKey(name)) {
					regularArgs.put(name, param.getValue() != null ? param.getValue() : "");
					slotNames.add(name);
				}
			}
		}

		// Resolve all slots into formatted argument strings, preserving the established order.
		// Group placeholders are resolved by buildGroupArg which merges config defaults with
		// feature entries. Regular args are formatted using the arg_key_prefix, separator, and value format.
		ArrayList<String> featureArgParts = new ArrayList<>();
		for (String slotName : slotNames) {
			if (slotName.startsWith("__GROUP__")) {
				// Resolve group placeholder into a formatted group argument
				// e.g., "__GROUP__vars" -> --vars "{'db': 'my_db', 'ldts': '2025-01-01'}"
				String prefix = slotName.substring("__GROUP__".length());
				String groupValue = buildGroupArg(processConfig, prefix, groups.get(prefix));
				featureArgParts.add(argKeyPrefix + prefix + argKeyValueSeparator + groupValue);
			} else {
				// Format a regular argument, e.g., "select" with value "my_model" -> --select my_model
				String value = regularArgs.get(slotName);
				String formattedValue = argValueFormat.replace("{value}", value);
				featureArgParts.add(argKeyPrefix + slotName + argKeyValueSeparator + formattedValue);
			}
		}

		// If any individual args found (from config or table), they replace the feature_args string default
		if (!featureArgParts.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < featureArgParts.size(); i++) {
				if (i > 0) {
					sb.append(" ");
				}
				sb.append(featureArgParts.get(i));
			}
			featureArgs = sb.toString();
		}

		// Build final command: {command} {starting_args} {feature_args} {ending_args}
		StringBuilder command = new StringBuilder();
		if (!commandParam.isEmpty()) {
			command.append(commandParam);
		}
		if (!startingArgs.isEmpty()) {
			if (command.length() > 0) {
				command.append(" ");
			}
			command.append(startingArgs);
		}
		if (!featureArgs.isEmpty()) {
			if (command.length() > 0) {
				command.append(" ");
			}
			command.append(featureArgs);
		}
		if (!endingArgs.isEmpty()) {
			if (command.length() > 0) {
				command.append(" ");
			}
			command.append(endingArgs);
		}

		String commandText = command.toString().trim();
		logger.info(String.format("Executing commandline with args for process '%s': %s", processConfigName, commandText));
		// Use the CommandLineConfig from the ProcessConfig if available
		CommandLineConfig clConfig = processConfig.getCommandLineConfig();
		getCommandLineExecutor().runProcess(processConfig, commandText, clConfig);
	}

	/**
	 * Assembles and executes a commandline process using a ProcessConfig, a table of arguments,
	 * and an explicit CommandLineConfig name that overrides the ProcessConfig's binding.
	 *
	 * @param processConfigName The name of the ProcessConfig to use.
	 * @param dataTable The table of arguments with "args" and "value" columns.
	 * @param commandLineConfigName The name of the CommandLineConfig to use (overrides config-time binding).
	 */
	public void ExecuteTemplatedCommandProcesWithParameters(String processConfigName, DataTable dataTable, String commandLineConfigName) throws Throwable {
		// Resolve the explicit CommandLineConfig before delegating to the main method,
		// then temporarily set it on the ProcessConfig so the main method picks it up.
		ProcessConfig processConfig = _config.getProcessConfig(processConfigName);
		CommandLineConfig originalConfig = processConfig.getCommandLineConfig();
		CommandLineConfig overrideConfig = _config.getCommandLineConfig(commandLineConfigName);
		try {
			// Temporarily override
			processConfig.setCommandLineConfig(overrideConfig);
			ExecuteTemplatedCommandProcesWithParameters(processConfigName, dataTable);
		} finally {
			// Restore original
			processConfig.setCommandLineConfig(originalConfig);
		}
	}

	/**
	 * Returns the assembled command text without the shell tool prefix.
	 * For example: "echo dbt run --select my_model --target dev"
	 * This is intended for test assertions to verify the command assembly logic.
	 * @return The command text, or null if no command has been executed.
	 */
	public String getLastAssembledCommand() {
		return getCommandLineExecutor().getLastCommandText();
	}

	/**
	 * Returns the execution tool prefix for the specified OS type.
	 * For "windows": e.g., "cmd.exe /c"
	 * For "non-windows": e.g., "bash -c"
	 * This is intended for test assertions to verify the execution tool configuration.
	 * @param osType "windows" or "non-windows"
	 * @return The tool prefix string, or null if no command has been executed.
	 */
	public String getLastExecutionToolPrefix(String osType) {
		return getCommandLineExecutor().getLastToolPrefix(osType);
	}

	/**
	 * Reads a named parameter from the ProcessConfig's parameter list.
	 * @return The parameter value, or defaultValue if the parameter is not found or its value is null.
	 */
	private String getParameterValue(ProcessConfig processConfig, String paramName, String defaultValue) {
		for (ConfigProperty param : processConfig.getParameters()) {
			if (paramName.equals(param.getName())) {
				return param.getValue() != null ? param.getValue() : defaultValue;
			}
		}
		return defaultValue;
	}

	/**
	 * Builds a single grouped argument string from dot-notation entries.
	 * Merges config-level defaults (e.g., vars.db=default_database) with feature-level entries,
	 * then formats them using the group_format, group_entry_format, and group_entry_separator
	 * config parameters.
	 *
	 * For example, with dbt-style formatting:
	 *   group_format = "{{entries}}", group_entry_format = "'{key}': '{value}'", group_entry_separator = ", "
	 *   entries: db=my_db, ldts=2025-01-01
	 *   result: "{'db': 'my_db', 'ldts': '2025-01-01'}"
	 *
	 * @param processConfig The ProcessConfig containing formatting parameters and defaults.
	 * @param prefix The group prefix (e.g., "vars" for vars.db, vars.ldts).
	 * @param featureEntries The feature-level key-value pairs for this group, may be null.
	 * @return The formatted group argument value.
	 */
	private String buildGroupArg(ProcessConfig processConfig, String prefix, ArrayList<String[]> featureEntries) {
		String format = getParameterValue(processConfig, "group_format", "{entries}");
		String entryFormat = getParameterValue(processConfig, "group_entry_format", "{key}={value}");
		String entrySeparator = getParameterValue(processConfig, "group_entry_separator", ",");

		// Collect default entries from config parameters matching {prefix}.{key}
		LinkedHashMap<String, String> mergedEntries = new LinkedHashMap<>();
		for (ConfigProperty param : processConfig.getParameters()) {
			if (param.getName().startsWith(prefix + ".")) {
				String key = param.getName().substring(prefix.length() + 1);
				mergedEntries.put(key, param.getValue() != null ? param.getValue() : "");
			}
		}

		// Overlay feature entries (add new, overwrite existing)
		if (featureEntries != null) {
			for (String[] entry : featureEntries) {
				mergedEntries.put(entry[0], entry[1]);
			}
		}

		// Build formatted string from merged entries
		StringBuilder entriesBuilder = new StringBuilder();
		int i = 0;
		for (Map.Entry<String, String> entry : mergedEntries.entrySet()) {
			if (i > 0) {
				entriesBuilder.append(entrySeparator);
			}
			String entryStr = entryFormat.replace("{key}", entry.getKey()).replace("{value}", entry.getValue());
			entriesBuilder.append(entryStr);
			i++;
		}

		return format.replace("{entries}", entriesBuilder.toString());
	}

	public void CloseProcessConnections() throws Throwable {
		logger.info("Closing process connections");
		for (ProcessExecutor pe :_executors.values()) {			
			pe.cleanUp();
		}
	}
	
	private String getResolvedCredential(String credentialProvider, String credential) throws XTestProcessException {
		//If no credential provider was given, return the credential unprocessed
		if (credentialProvider == null || credentialProvider.isEmpty()) {
			return credential;
		} else {
			//Resolve the credential via the credentialprovider
			try {
				return this._credentialProviderHelper.resolveCredential(credentialProvider, credential);
			}catch(XTestException exc) {
				throw new XTestProcessException(String.format("Could not resolve %s using credential provider %s: %s", credential, credentialProvider, exc.getMessage()));
			}
		}
	}
		
	/**
	 * Checks whether the current OS matches the given osType.
	 * @param osType "windows" or "non-windows"
	 * @return true if the current OS matches the given type
	 */
	private boolean isCurrentOs(String osType) {
		String os = System.getProperty("os.name").toLowerCase();
		boolean isWindows = os.contains("win");
		return (isWindows && "windows".equals(osType)) || (!isWindows && "non-windows".equals(osType));
	}

	/**
	 * Returns a cached CommandLineProcessExecutor instance, creating one if needed.
	 * This executor is used by ExecuteCommand and ExecuteTemplatedCommandProcesWithParameters to run
	 * commandline processes directly, without requiring a ProcessServerConfig binding in the XML config.
	 * The instance is stored in the _executors map so it is cleaned up by CloseProcessConnections.
	 */
	private CommandLineProcessExecutor getCommandLineExecutor() {
		String key = CommandLineProcessExecutor.class.getName();
		if (!_executors.containsKey(key)) {
			_executors.put(key, new CommandLineProcessExecutor());
		}
		return (CommandLineProcessExecutor) _executors.get(key);
	}

	/**
	 * Returns a cached ProcessExecutor for a given process config, using the ProcessServerConfig's
	 * execution class to dynamically instantiate the executor via reflection.
	 * Used by RunTemplatedProcess for non-commandline process types (e.g., Informatica PowerCenter).
	 */
	private ProcessExecutor getProcessExecutor(String processConfig) throws XTestProcessException{
		String executorClass = _config.getProcessConfig(processConfig).getProcessServerConfig().getExecutionClass();
		if (!_executors.containsKey(executorClass)) {
			//Initialize executor and add to collection
			try {
				ProcessServerConfig pc = _config.getProcessConfig(processConfig).getProcessServerConfig();
				//Process all properties via the credentialprovider if specified
				for(SecurableConfigProperty cp:pc.getProperties()) {
					cp.setValue(getResolvedCredential(cp.getCredentialProvider(), cp.getValue()));
				}				
				Class<?> c = this.getClass().getClassLoader().loadClass(_config.getProcessConfig(processConfig).getProcessServerConfig().getExecutionClass());
				ProcessExecutor pe = (ProcessExecutor)c.getDeclaredConstructor().newInstance();
				_executors.put(executorClass, pe);
			} catch (ClassNotFoundException e) {			
				throw new XTestProcessException(String.format("Execution class %s for process config %s was not found",_config.getProcessConfig(processConfig).getProcessServerConfig().getExecutionClass(), processConfig));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new XTestProcessException(String.format("Could not instantiate execution class %s for process config %s: %s",_config.getProcessConfig(processConfig).getProcessServerConfig().getExecutionClass(), processConfig, e.getMessage()));
			}
			
		}
		return _executors.get(executorClass);
	}
	
}
