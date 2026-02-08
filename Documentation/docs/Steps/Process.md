# Process
This page describes the Process steps.

## Run templated process
Execute an (ETL) process using the engine configured in a CrossTest process server configuration


### Sentences
| Type          | Language         | Sentence      |
|:---           |:---              |:---           |
| When | en | ^I run the ([a-zA-Z0-9_@$#]+) process ([a-zA-Z0-9_@$#.]+)$ |


### Arguments
The details of every argument of the step are listed below.

| Parameter    | Datatype          | Description          |
|:---          |:---               |:---                  |
|process config | String | Name of the process config |
|process name | String | Name of the process |

### Examples


```gherkin
 When I run the demo process load_Customer
```

## Execute command
Execute a command using commandline. On Windows it will run using `cmd.exe /c`, while on other OS types it will use `bash -c`.


### Sentences
| Type          | Language         | Sentence      |
|:---           |:---              |:---           |
| When | en | I execute the following command |
| When | nl | ik het volgende commando uitvoer |


### Arguments
The details of every argument of the step are listed below.

| Parameter    | Datatype          | Description          |
|:---          |:---               |:---                  |
| command text | String | Command to be executed. It can be written as a multiline and multi-statement command, and will be executed at once. |

### Examples

```gherkin
When I execute the following command
"""
echo Hello World
"""
```

```gherkin
Wanneer ik het volgende commando uitvoer
"""
echo Hello World
"""
```

## Execute command on specific commandline
Execute a command using a specific [CommandLineConfig](#commandlineconfig) instead of the default shell.


### Sentences
| Type          | Language         | Sentence      |
|:---           |:---              |:---           |
| When | en | `^I execute the following ([a-zA-Z0-9_@$#]+) command:$` |
| When | nl | `^ik het volgende ([a-zA-Z0-9_@$#]+) commando uitvoer:$` |


### Arguments
The details of every argument of the step are listed below.

| Parameter    | Datatype          | Description          |
|:---          |:---               |:---                  |
| command text | String | Command to be executed. It can be written as a multiline and multi-statement command, and will be executed at once. |
| commandline config | String | Name of a [CommandLineConfig](#commandlineconfig) to use instead of the default shell. |

### Examples

```gherkin
When I execute the following powershell command:
"""
Write-Output 'Hello from PowerShell'
"""
```

```gherkin
Wanneer ik het volgende powershell commando uitvoer:
"""
Write-Output 'Hello from PowerShell'
"""
```

## Execute commandline with arguments
Execute a commandline process with configurable arguments. The command is assembled from a ProcessConfig and a table of arguments provided in the feature file. By default, on Windows it will run using cmd, while on other OS types it will use bash. This can be customized using a [CommandLineConfig](#commandlineconfig).

This step does not require a ProcessServerConfig binding. The CommandLineProcessExecutor is used directly.

### Sentences
| Type          | Language         | Sentence      |
|:---           |:---              |:---           |
| When | en | ^I execute the ([a-zA-Z0-9_@$#]+) commandline process using the following arguments:$ |
| When | nl | ^ik het ([a-zA-Z0-9_@$#]+) commandline proces uitvoer met de volgende argumenten:$ |

### Arguments
The details of every argument of the step are listed below.

| Parameter    | Datatype          | Description          |
|:---          |:---               |:---                  |
| process config | String | Name of the ProcessConfig to use |
| arguments table | DataTable | Two-column table where the first column is the argument name and the second column is the value. Column headers can be freely chosen. |

### Command assembly
The command is assembled from four segments in this order:

```
{command} {starting_args} {feature_args} {ending_args}
```

Empty segments are omitted. Each segment can be set in the ProcessConfig parameters and optionally overridden from the feature file arguments table.

| Segment | Description |
|:--- |:--- |
| command | The base command to execute (e.g., `dbt run`, `helm upgrade --install my-release my-chart`). |
| starting_args | Arguments placed between the command and the feature args (e.g., `--full-refresh` , `--install my-release my-chart`). |
| feature_args | The main arguments, built from regular arguments (see [Argument defaults](#argument-defaults)) and the non-special rows in the arguments table. If no individual arguments exist (neither from config nor from the table), the `feature_args` string default from config is used as a fallback. |
| ending_args | Arguments appended at the end (e.g., `--target dev`). |

### Arguments table
Each row in the arguments table has two columns: the first column is the argument name and the second column is the value. Rows are processed as follows:

| First column | Behavior |
|:--- |:--- |
| `command` | Overrides the command segment from config. |
| `starting_args` | Overrides the starting_args segment from config. |
| `ending_args` | Overrides the ending_args segment from config. |
| Name with a dot (e.g., `vars.db`) | Grouped argument. See [Grouped arguments](#grouped-arguments). |
| Any other name (e.g., `select`) | Regular feature argument. Overrides a config default with the same name, or adds a new argument. Formatted as `{arg_key_prefix}{name}{arg_key_value_separator}{value}`. |

**Note:** The special segment parameters (`command`, `starting_args`, `ending_args`) and regular argument defaults (see [Argument defaults](#argument-defaults)) can be overridden from the feature table. However, formatting parameters (`arg_key_prefix`, `arg_key_value_separator`, `arg_value_format`, `group_format`, `group_entry_format`, `group_entry_separator`) can only be set in the ProcessConfig. Using a formatting parameter name in the feature table will not change the formatting — it will be treated as a regular argument.

### Argument defaults
ProcessConfig parameters that are not reserved names and do not contain a dot are treated as **regular argument defaults**. These defaults are merged with the feature table entries:

- Feature table entries with the same name **override** the config default value (keeping the feature table position).
- Config defaults not mentioned in the feature table are **appended** after all feature table entries.
- New feature table entries not in the config are placed in their table order.

This allows you to define common arguments once in the config and override only specific values per scenario. See [Configuration examples](#configuration-examples) for a practical example.

**Important:** Do not combine `feature_args` with argument defaults in the same ProcessConfig. When argument defaults are present, they take precedence and the `feature_args` fallback string is ignored. For example:

```xml
<!-- Do NOT do this: feature_args will never be used because select is an argument default -->
<ProcessConfig name="broken_example">
    <Parameters>
        <Parameter name="command" value="dbt run"/>
        <Parameter name="feature_args" value="--select my_default_model"/>
        <Parameter name="select" value="other_model"/>
    </Parameters>
</ProcessConfig>
```

The `select` argument default makes the feature args segment non-empty, so `feature_args` is ignored entirely. The result would be `dbt run --select other_model`, not `dbt run --select my_default_model --select other_model`.

Use either `feature_args` (a single pre-formatted string fallback) **or** argument defaults (individual named parameters), not both.

### ProcessConfig parameters
The following reserved parameters can be set in the ProcessConfig to control the command assembly and argument formatting. Any parameter not listed here and not containing a dot is treated as a regular argument default (see [Argument defaults](#argument-defaults)).

| Parameter | Default | Description |
|:--- |:--- |:--- |
| `command` | _(empty)_ | The base command to execute. |
| `starting_args` | _(empty)_ | Arguments placed before the feature args. |
| `feature_args` | _(empty)_ | Fallback feature args string, used only when no individual arguments exist (neither from config defaults nor from the table). |
| `ending_args` | _(empty)_ | Arguments appended at the end. |
| `arg_key_prefix` | `--` | Prefix for argument keys (e.g., `--` produces `--select`, `-D` produces `-Denv`). |
| `arg_key_value_separator` | ` ` (space) | Separator between key and value (e.g., space produces `--select my_model`, `=` produces `-Denv=production`). |
| `arg_value_format` | `{value}` | Format for the value. Use `"{value}"` to quote values (produces `--name "Alice"`). |
| `group_format` | `{entries}` | Format for grouped argument values. See [Grouped arguments](#grouped-arguments). |
| `group_entry_format` | `{key}={value}` | Format for each entry within a group. |
| `group_entry_separator` | `,` | Separator between entries within a group. |

### Grouped arguments
Arguments with a dot in their name (e.g., `vars.db`, `vars.ldts`) are grouped by the prefix before the dot. All entries sharing the same prefix are combined into a single argument using the group formatting parameters.

The group is formatted as: `{arg_key_prefix}{prefix}{arg_key_value_separator}{group_value}`, where `{group_value}` is built by applying `group_format` to the concatenated entries.

Config-level parameters with dot-notation names (e.g., `vars.db=default_database`) serve as group defaults. Feature-level entries merge with these defaults: new keys are added and existing keys are overridden.

### Configuration examples

#### dbt with vars
```xml
<ProcessConfig name="dbt">
    <Parameters>
        <Parameter name="command" value="dbt run"/>
        <Parameter name="ending_args" value="--target dev"/>
        <Parameter name="group_format" value="&quot;{{entries}}&quot;"/>
        <Parameter name="group_entry_format" value="'{key}': '{value}'"/>
        <Parameter name="group_entry_separator" value=", "/>
    </Parameters>
</ProcessConfig>
```

#### Java application with system properties
```xml
<ProcessConfig name="java_app">
    <Parameters>
        <Parameter name="command" value="java -jar app.jar"/>
        <Parameter name="arg_key_prefix" value="-D"/>
        <Parameter name="arg_key_value_separator" value="="/>
    </Parameters>
</ProcessConfig>
```

#### Helm with multiple grouped arguments
```xml
<ProcessConfig name="helm">
    <Parameters>
        <Parameter name="command" value="helm upgrade --install my-release my-chart"/>
        <Parameter name="group_entry_format" value="{key}={value}"/>
        <Parameter name="group_entry_separator" value=","/>
    </Parameters>
</ProcessConfig>
```

#### Tool with regular argument defaults
```xml
<ProcessConfig name="mytool">
    <Parameters>
        <Parameter name="command" value="mytool export"/>
        <Parameter name="path" value="c:\data"/>
        <Parameter name="format" value="csv"/>
    </Parameters>
</ProcessConfig>
```

#### dbt with default group values
```xml
<ProcessConfig name="dbt_with_defaults">
    <Parameters>
        <Parameter name="command" value="dbt run"/>
        <Parameter name="ending_args" value="--target dev"/>
        <Parameter name="group_format" value="&quot;{{entries}}&quot;"/>
        <Parameter name="group_entry_format" value="'{key}': '{value}'"/>
        <Parameter name="group_entry_separator" value=", "/>
        <Parameter name="vars.db" value="default_database"/>
        <Parameter name="vars.ldts" value="2025-01-01 00:00:00"/>
    </Parameters>
</ProcessConfig>
```

### Feature file examples

#### Basic arguments
```gherkin
When I execute the dbt commandline process using the following arguments:
  | args   | value    |
  | select | my_model |
```
Result: `dbt run --select my_model --target dev`

#### Multiple arguments
```gherkin
When I execute the dbt commandline process using the following arguments:
  | args    | value          |
  | select  | my_model       |
  | exclude | my_other_model |
```
Result: `dbt run --select my_model --exclude my_other_model --target dev`

#### Overriding ending_args
```gherkin
When I execute the dbt commandline process using the following arguments:
  | args        | value         |
  | select      | my_model      |
  | ending_args | --target prod |
```
Result: `dbt run --select my_model --target prod`

#### With starting_args flag
```gherkin
When I execute the dbt commandline process using the following arguments:
  | args          | value          |
  | starting_args | --full-refresh |
  | select        | my_model       |
```
Result: `dbt run --full-refresh --select my_model --target dev`

#### Grouped arguments (dbt vars)
```gherkin
When I execute the dbt commandline process using the following arguments:
  | args      | value                |
  | vars.db   | database_op_teradata |
  | vars.ldts | 2025-07-26 00:00:00  |
  | select    | view_met_logica      |
```
Result: `dbt run --vars "{'db': 'database_op_teradata', 'ldts': '2025-07-26 00:00:00'}" --select view_met_logica --target dev`

#### Multiple groups (Helm set + set-string)
```gherkin
When I execute the helm commandline process using the following arguments:
  | args              | value      |
  | set.replicas      | 3          |
  | set.port          | 8080       |
  | set-string.env    | production |
  | set-string.region | eu-west-1  |
```
Result: `helm upgrade --install my-release my-chart --set replicas=3,port=8080 --set-string env=production,region=eu-west-1`

#### Group defaults with override and addition
Using the `dbt_with_defaults` config (which has `vars.db=default_database` and `vars.ldts=2025-01-01 00:00:00`):

```gherkin
When I execute the dbt_with_defaults commandline process using the following arguments:
  | args      | value           |
  | vars.db   | other_database  |
  | vars.user | myuser          |
  | select    | view_met_logica |
```
Result: `dbt run --vars "{'db': 'other_database', 'ldts': '2025-01-01 00:00:00', 'user': 'myuser'}" --select view_met_logica --target dev`

The `vars.db` default is overridden, `vars.ldts` is kept from config, and `vars.user` is added.

#### Regular argument defaults from config
Using the `mytool` config (which has `path=c:\data` and `format=csv` as defaults):

```gherkin
When I execute the mytool commandline process using the following arguments:
  | args   | value      |
  | output | result.txt |
```
Result: `mytool export --output result.txt --path c:\data --format csv`

The `output` argument is from the feature table. The `path` and `format` defaults from config are appended because they were not overridden.

#### Overriding a config argument default
```gherkin
When I execute the mytool commandline process using the following arguments:
  | args   | value      |
  | path   | d:\other   |
  | output | result.txt |
```
Result: `mytool export --path d:\other --output result.txt --format csv`

The `path` default is overridden by the feature table value. The `format` default is kept from config.

#### Java-style arguments
```gherkin
When I execute the java_app commandline process using the following arguments:
  | args | value      |
  | env  | production |
  | port | 8080       |
```
Result: `java -jar app.jar -Denv=production -Dport=8080`

#### Dutch (NL) variant
```gherkin
Wanneer ik het dbt commandline proces uitvoer met de volgende argumenten:
  | args   | value    |
  | select | my_model |
```
Result: `dbt run --select my_model --target dev`

## Execute commandline with arguments on specific commandline
Execute a commandline process with configurable arguments on a specific commandline configuration. This step is identical to [Execute commandline with arguments](#execute-commandline-with-arguments), but allows specifying a [CommandLineConfig](#commandlineconfig) to override the shell, tool flags, and working directory at execution time.

The commandline config specified in the step sentence overrides any `commandLineConfigName` set on the ProcessConfig.

### Sentences
| Type          | Language         | Sentence      |
|:---           |:---              |:---           |
| When | en | ^I execute the ([a-zA-Z0-9_@$#]+) commandline process with ([a-zA-Z0-9_@$#]+) using the following arguments:$ |
| When | nl | ^ik het ([a-zA-Z0-9_@$#]+) commandline proces uitvoer via ([a-zA-Z0-9_@$#]+) met de volgende argumenten:$ |

### Arguments
| Parameter    | Datatype          | Description          |
|:---          |:---               |:---                  |
| process config | String | Name of the ProcessConfig to use |
| commandline config | String | Name of the CommandLineConfig to use |
| arguments table | DataTable | Two-column table where the first column is the argument name and the second column is the value. Column headers can be freely chosen. |

### Examples

#### Execution-time override
```gherkin
When I execute the dbt commandline process with powershell using the following arguments:
  | args   | value    |
  | select | my_model |
```

#### Dutch (NL) variant
```gherkin
Wanneer ik het dbt commandline proces uitvoer via powershell met de volgende argumenten:
  | args   | value    |
  | select | my_model |
```

## CommandLineConfig
The `CommandLineConfig` element allows you to configure the shell, tool flags, and working directory used by commandline execution steps. By default, `cmd.exe /c` is used on Windows and `bash -c` on other platforms (detected at runtime). A CommandLineConfig lets you override these defaults.

### XML configuration

CommandLineConfigs are defined inside a `CommandLineConfigs` wrapper element in the XTestConfig:

```xml
<XTestConfig>
  <CommandLineConfigs>
    <CommandLineConfig name="powershell"
      tool="powershell.exe" toolFlags="-Command"/>
    <CommandLineConfig name="custom_workdir"
      workingDirectory="C:\workdir"/>
  </CommandLineConfigs>
  ...
</XTestConfig>
```

### Attributes

| Attribute | Required | Default | Description |
|:--- |:--- |:--- |:--- |
| `name` | Yes | | Unique name to reference this config. |
| `tool` | No | `cmd.exe` (Windows) / `bash` (other) | The shell executable to use. |
| `toolFlags` | No | `/c` (Windows) / `-c` (other) | The flag(s) passed to the shell to execute a command string. |
| `workingDirectory` | No | _(inherit)_ | Working directory for command execution. |

### Binding a CommandLineConfig to a ProcessConfig

You can bind a CommandLineConfig to a ProcessConfig at config time using the `commandLineConfigName` attribute:

```xml
<ProcessConfig name="dbt_custom" commandLineConfigName="powershell">
  <Parameters>
    <Parameter name="command" value="dbt run"/>
    <Parameter name="ending_args" value="--target dev"/>
  </Parameters>
</ProcessConfig>
```

When this ProcessConfig is used in an "execute commandline with arguments" step, it will automatically use the referenced CommandLineConfig.

### Execution-time override

The "with {commandline config}" step sentence allows overriding the CommandLineConfig at execution time, regardless of what is configured on the ProcessConfig:

```gherkin
When I execute the dbt commandline process with powershell using the following arguments:
  | args   | value    |
  | select | my_model |
```

This is useful when the same ProcessConfig needs to run with different shell configurations in different scenarios.
