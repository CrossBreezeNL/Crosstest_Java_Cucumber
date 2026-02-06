package com.xbreeze.xtest.modules.process;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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

public class Process_Helper {

	private XTestConfig _config;
	private CredentialProvider_Helper _credentialProviderHelper;
	static final Logger logger = Logger.getLogger(DataHelper.class.getName());
	private HashMap<String, ProcessExecutor> _executors;
	// Stores the last assembled command text from ExecuteCommandLineWithArgs, used for test assertions.
	private String _lastAssembledCommand;

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
	 * Assembles and executes a commandline process using a ProcessConfig and a table of arguments.
	 * The command is built from four segments: {command} {starting_args} {feature_args} {ending_args}.
	 *
	 * Special argument names in the table (command, starting_args, ending_args) override the corresponding
	 * config values. All other arguments become feature_args, replacing the config default.
	 *
	 * Dot-notation arguments (e.g., vars.db, vars.ldts) are grouped together and formatted using the
	 * group_format, group_entry_format, and group_entry_separator config parameters.
	 * Config-level dot-notation parameters serve as group defaults that can be overridden or extended
	 * by feature-level entries.
	 *
	 * Uses CommandLineProcessExecutor directly, so no ProcessServerConfig binding is required in the XML config.
	 *
	 * @param processConfigName The name of the ProcessConfig to use.
	 * @param argsTable The table of arguments with "args" and "value" columns.
	 */
	public void ExecuteCommandLineWithArgs(String processConfigName, List<Map<String, String>> argsTable) throws Throwable {
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

		// Process table rows, supporting dot-notation grouped args (e.g., vars.db, vars.ldts)
		ArrayList<String> featureArgParts = new ArrayList<>();
		LinkedHashMap<String, ArrayList<String[]>> groups = new LinkedHashMap<>();
		for (Map<String, String> row : argsTable) {
			String argName = row.get("args");
			String argValue = row.get("value") != null ? row.get("value") : "";
			if ("command".equals(argName)) {
				commandParam = argValue;
			} else if ("starting_args".equals(argName)) {
				startingArgs = argValue;
			} else if ("ending_args".equals(argName)) {
				endingArgs = argValue;
			} else if (argName.contains(".")) {
				// Dot-notation: split on first dot into group prefix and key
				int dotIndex = argName.indexOf('.');
				String prefix = argName.substring(0, dotIndex);
				String key = argName.substring(dotIndex + 1);
				if (!groups.containsKey(prefix)) {
					groups.put(prefix, new ArrayList<String[]>());
					// Insert placeholder at the position of the first entry for this group
					featureArgParts.add("__GROUP__" + prefix);
				}
				groups.get(prefix).add(new String[]{key, argValue});
			} else {
				String formattedValue = argValueFormat.replace("{value}", argValue);
				featureArgParts.add(argKeyPrefix + argName + argKeyValueSeparator + formattedValue);
			}
		}

		// Check for config-level group defaults (parameters with dot-notation names)
		for (ConfigProperty param : processConfig.getParameters()) {
			String paramName = param.getName();
			if (paramName.contains(".")) {
				int dotIndex = paramName.indexOf('.');
				String prefix = paramName.substring(0, dotIndex);
				if (!groups.containsKey(prefix)) {
					groups.put(prefix, new ArrayList<String[]>());
					featureArgParts.add("__GROUP__" + prefix);
				}
			}
		}

		// Resolve group placeholders
		for (int i = 0; i < featureArgParts.size(); i++) {
			String part = featureArgParts.get(i);
			if (part.startsWith("__GROUP__")) {
				String prefix = part.substring("__GROUP__".length());
				String groupValue = buildGroupArg(processConfig, prefix, groups.get(prefix));
				featureArgParts.set(i, argKeyPrefix + prefix + argKeyValueSeparator + groupValue);
			}
		}

		// If any non-special args found in table, replace feature_args with the concatenated result
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
		// Store the assembled command so it can be verified in test assertions via getLastAssembledCommand().
		_lastAssembledCommand = commandText;
		logger.info(String.format("Executing commandline with args for process '%s': %s", processConfigName, commandText));
		getCommandLineExecutor().runProcess(processConfig, commandText);
	}

	/**
	 * Returns the last assembled command text from ExecuteCommandLineWithArgs.
	 * This is intended for test assertions to verify that the command was assembled correctly.
	 * @return The last assembled command string, or null if ExecuteCommandLineWithArgs has not been called.
	 */
	public String getLastAssembledCommand() {
		return _lastAssembledCommand;
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
	 * Returns a cached CommandLineProcessExecutor instance, creating one if needed.
	 * This executor is used by ExecuteCommand and ExecuteCommandLineWithArgs to run commandline processes
	 * directly, without requiring a ProcessServerConfig binding in the XML config.
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
