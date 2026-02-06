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
Execute a command using commandline. On windows it will run using cmd, while on other OS types it will use bash.


### Sentences
| Type          | Language         | Sentence      |
|:---           |:---              |:---           |
| When | en | I execute the following command |
| When | nl | ik het volgende commando uitvoer |


### Arguments
The details of every argument of the step are listed below.

| Parameter    | Datatype          | Description          |
|:---          |:---               |:---                  |
|command text | String | Command to be executed. It can be written as a multiline and multi-statement command, and will be executed at once. |

### Examples


```gherkin
 When I execute the following command
```


```gherkin
 Wanneer ik het volgende commando uitvoer
```

## Execute commandline with arguments
Execute a commandline process with configurable arguments. The command is assembled from a ProcessConfig and a table of arguments provided in the feature file. On Windows it will run using cmd, while on other OS types it will use bash.

This step does not require a ProcessServerConfig binding. The CommandLineProcessExecutor is used directly.

### Sentences
| Type          | Language         | Sentence      |
|:---           |:---              |:---           |
| When | en | ^I execute the ([a-zA-Z0-9_@$#]+) process using commandline with the following arguments:$ |
| When | nl | ^ik het ([a-zA-Z0-9_@$#]+) proces uitvoer via commandline met de volgende argumenten:$ |

### Arguments
The details of every argument of the step are listed below.

| Parameter    | Datatype          | Description          |
|:---          |:---               |:---                  |
| process config | String | Name of the ProcessConfig to use |
| arguments table | DataTable | Table with `args` and `value` columns containing the arguments |

### Command assembly
The command is assembled from four segments in this order:

```
{command} {starting_args} {feature_args} {ending_args}
```

Empty segments are omitted. Each segment can be set in the ProcessConfig parameters and optionally overridden from the feature file arguments table.

| Segment | Description |
|:--- |:--- |
| command | The base command to execute (e.g., `dbt run`, `helm upgrade --install my-release my-chart`). |
| starting_args | Arguments placed between the command and the feature args (e.g., `--full-refresh`). |
| feature_args | The main arguments, built from the non-special rows in the arguments table. If no non-special rows are present, the default from the config is used. |
| ending_args | Arguments appended at the end (e.g., `--target dev`). |

### Arguments table
Each row in the arguments table has an `args` column and a `value` column. Rows are processed as follows:

| args value | Behavior |
|:--- |:--- |
| `command` | Overrides the command segment from config. |
| `starting_args` | Overrides the starting_args segment from config. |
| `ending_args` | Overrides the ending_args segment from config. |
| Name with a dot (e.g., `vars.db`) | Grouped argument. See [Grouped arguments](#grouped-arguments). |
| Any other name (e.g., `select`) | Regular feature argument. Formatted as `{arg_key_prefix}{name}{arg_key_value_separator}{value}`. |

### ProcessConfig parameters
The following parameters can be set in the ProcessConfig to control the command assembly and argument formatting.

| Parameter | Default | Description |
|:--- |:--- |:--- |
| `command` | _(empty)_ | The base command to execute. |
| `starting_args` | _(empty)_ | Arguments placed before the feature args. |
| `feature_args` | _(empty)_ | Default feature args, used when no non-special args are in the table. |
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
When I execute the dbt process using commandline with the following arguments:
  | args   | value    |
  | select | my_model |
```
Result: `dbt run --select my_model --target dev`

#### Multiple arguments
```gherkin
When I execute the dbt process using commandline with the following arguments:
  | args    | value          |
  | select  | my_model       |
  | exclude | my_other_model |
```
Result: `dbt run --select my_model --exclude my_other_model --target dev`

#### Overriding ending_args
```gherkin
When I execute the dbt process using commandline with the following arguments:
  | args        | value         |
  | select      | my_model      |
  | ending_args | --target prod |
```
Result: `dbt run --select my_model --target prod`

#### With starting_args flag
```gherkin
When I execute the dbt process using commandline with the following arguments:
  | args          | value          |
  | starting_args | --full-refresh |
  | select        | my_model       |
```
Result: `dbt run --full-refresh --select my_model --target dev`

#### Grouped arguments (dbt vars)
```gherkin
When I execute the dbt process using commandline with the following arguments:
  | args      | value                |
  | vars.db   | database_op_teradata |
  | vars.ldts | 2025-07-26 00:00:00  |
  | select    | view_met_logica      |
```
Result: `dbt run --vars "{'db': 'database_op_teradata', 'ldts': '2025-07-26 00:00:00'}" --select view_met_logica --target dev`

#### Multiple groups (Helm set + set-string)
```gherkin
When I execute the helm process using commandline with the following arguments:
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
When I execute the dbt_with_defaults process using commandline with the following arguments:
  | args      | value           |
  | vars.db   | other_database  |
  | vars.user | myuser          |
  | select    | view_met_logica |
```
Result: `dbt run --vars "{'db': 'other_database', 'ldts': '2025-01-01 00:00:00', 'user': 'myuser'}" --select view_met_logica --target dev`

The `vars.db` default is overridden, `vars.ldts` is kept from config, and `vars.user` is added.

#### Java-style arguments
```gherkin
When I execute the java_app process using commandline with the following arguments:
  | args | value      |
  | env  | production |
  | port | 8080       |
```
Result: `java -jar app.jar -Denv=production -Dport=8080`

#### Dutch (NL) variant
```gherkin
Wanneer ik het dbt proces uitvoer via commandline met de volgende argumenten:
  | args   | value    |
  | select | my_model |
```
Result: `dbt run --select my_model --target dev`
