@Debug
Feature: ProcessExecutor - Commandline with arguments
  I want to execute commandline processes with configurable arguments
  # ==========================================================================
  # Basic args and special args overrides (dbt config, default formatting)
  # Config: command="echo dbt run", ending_args="--target dev"
  # ==========================================================================

  Scenario: dbt - single feature arg
    When I execute the dbt process using commandline with the following arguments:
      | args   | value    |
      | select | my_model |
    Then the assembled commandline should be:
      """
      echo dbt run --select my_model --target dev
      """

  Scenario: dbt - multiple feature args
    When I execute the dbt process using commandline with the following arguments:
      | args    | value          |
      | select  | my_model       |
      | exclude | my_other_model |
    Then the assembled commandline should be:
      """
      echo dbt run --select my_model --exclude my_other_model --target dev
      """

  Scenario: dbt - with starting_args flag
    When I execute the dbt process using commandline with the following arguments:
      | args          | value          |
      | starting_args | --full-refresh |
      | select        | my_model       |
    Then the assembled commandline should be:
      """
      echo dbt run --full-refresh --select my_model --target dev
      """

  Scenario: dbt - override ending_args
    When I execute the dbt process using commandline with the following arguments:
      | args        | value         |
      | select      | my_model      |
      | ending_args | --target prod |
    Then the assembled commandline should be:
      """
      echo dbt run --select my_model --target prod
      """

  Scenario: dbt - override command
    When I execute the dbt process using commandline with the following arguments:
      | args    | value          |
      | command | echo dbt build |
      | select  | my_model       |
    Then the assembled commandline should be:
      """
      echo dbt build --select my_model --target dev
      """

  Scenario: dbt - override command, starting_args, and ending_args
    When I execute the dbt process using commandline with the following arguments:
      | args          | value            |
      | command       | echo dbt build   |
      | starting_args | --full-refresh   |
      | select        | my_custom_model  |
      | ending_args   | --target staging |
    Then the assembled commandline should be:
      """
      echo dbt build --full-refresh --select my_custom_model --target staging
      """
  # ==========================================================================
  # Other dbt configs (various command/starting_args/feature_args combinations)
  # ==========================================================================

  Scenario: dbt_seed - with feature args and no ending_args in config
    When I execute the dbt_seed process using commandline with the following arguments:
      | args         | value |
      | full_refresh |       |
    Then the assembled commandline should be:
      """
      echo dbt seed --full_refresh
      """

  Scenario: dbt_seed - with select arg
    When I execute the dbt_seed process using commandline with the following arguments:
      | args   | value         |
      | select | my_seed_model |
    Then the assembled commandline should be:
      """
      echo dbt seed --select my_seed_model
      """

  Scenario: dbt_test - with select arg
    When I execute the dbt_test process using commandline with the following arguments:
      | args   | value      |
      | select | test_model |
    Then the assembled commandline should be:
      """
      echo dbt test --select test_model --target dev --store-failures
      """

  Scenario: dbt_test - override ending_args to remove store-failures
    When I execute the dbt_test process using commandline with the following arguments:
      | args        | value        |
      | select      | test_model   |
      | ending_args | --target dev |
    Then the assembled commandline should be:
      """
      echo dbt test --select test_model --target dev
      """

  Scenario: dbt_with_default_args - no feature args uses default feature_args from config
    When I execute the dbt_with_default_args process using commandline with the following arguments:
      | args        | value            |
      | ending_args | --target staging |
    Then the assembled commandline should be:
      """
      echo dbt run --select my_default_model --target staging
      """

  Scenario: dbt_with_default_args - feature args override default feature_args
    When I execute the dbt_with_default_args process using commandline with the following arguments:
      | args   | value            |
      | select | overridden_model |
    Then the assembled commandline should be:
      """
      echo dbt run --select overridden_model --target dev
      """

  Scenario: dbt_command_only - command only config with feature args
    When I execute the dbt_command_only process using commandline with the following arguments:
      | args   | value      |
      | select | some_model |
    Then the assembled commandline should be:
      """
      echo --select some_model
      """

  Scenario: dbt_command_only - command only config with all args from table
    When I execute the dbt_command_only process using commandline with the following arguments:
      | args        | value        |
      | command     | echo dbt run |
      | select      | some_model   |
      | ending_args | --target dev |
    Then the assembled commandline should be:
      """
      echo dbt run --select some_model --target dev
      """

  Scenario: dbt_no_params - config without parameters, all from table
    When I execute the dbt_no_params process using commandline with the following arguments:
      | args        | value        |
      | command     | echo dbt run |
      | select      | some_model   |
      | ending_args | --target dev |
    Then the assembled commandline should be:
      """
      echo dbt run --select some_model --target dev
      """
  # ==========================================================================
  # Dot-notation grouped args (dbt config with group_format)
  # Groups share: group_format, group_entry_format, group_entry_separator
  # ==========================================================================

  Scenario: dbt - vars only (grouped args)
    When I execute the dbt process using commandline with the following arguments:
      | args      | value                |
      | vars.db   | database_op_teradata |
      | vars.ldts |  2025-07-26 00:00:00 |
    Then the assembled commandline should be:
      """
      echo dbt run --vars "{'db': 'database_op_teradata', 'ldts': '2025-07-26 00:00:00'}" --target dev
      """

  Scenario: dbt - vars with regular args (mixed ordering)
    When I execute the dbt process using commandline with the following arguments:
      | args      | value                |
      | vars.db   | database_op_teradata |
      | vars.ldts |  2025-07-26 00:00:00 |
      | select    | view_met_logica      |
    Then the assembled commandline should be:
      """
      echo dbt run --vars "{'db': 'database_op_teradata', 'ldts': '2025-07-26 00:00:00'}" --select view_met_logica --target dev
      """

  Scenario: dbt - vars with starting_args flag
    When I execute the dbt process using commandline with the following arguments:
      | args          | value                |
      | starting_args | --full-refresh       |
      | vars.db       | database_op_teradata |
      | vars.ldts     |  2025-07-26 00:00:00 |
      | select        | view_met_logica      |
    Then the assembled commandline should be:
      """
      echo dbt run --full-refresh --vars "{'db': 'database_op_teradata', 'ldts': '2025-07-26 00:00:00'}" --select view_met_logica --target dev
      """

  Scenario: dbt - vars with command override
    When I execute the dbt process using commandline with the following arguments:
      | args      | value                |
      | command   | echo dbt build       |
      | vars.db   | database_op_teradata |
      | vars.ldts |  2025-07-26 00:00:00 |
      | select    | view_met_logica      |
    Then the assembled commandline should be:
      """
      echo dbt build --vars "{'db': 'database_op_teradata', 'ldts': '2025-07-26 00:00:00'}" --select view_met_logica --target dev
      """

  Scenario: helm - multiple groups (set + set-string)
    When I execute the helm process using commandline with the following arguments:
      | args              | value      |
      | set.replicas      |          3 |
      | set.port          |       8080 |
      | set-string.env    | production |
      | set-string.region | eu-west-1  |
    Then the assembled commandline should be:
      """
      echo helm upgrade --install my-release my-chart --set replicas=3,port=8080 --set-string env=production,region=eu-west-1
      """
  # ==========================================================================
  # Config-level group defaults (dbt_with_default_vars)
  # Config has: vars.db=default_database, vars.ldts=2025-01-01 00:00:00
  # Feature file entries merge with config defaults (add new, overwrite existing)
  # ==========================================================================

  Scenario: dbt_with_default_vars - config defaults only (no vars in feature, only regular args)
    When I execute the dbt_with_default_vars process using commandline with the following arguments:
      | args   | value           |
      | select | view_met_logica |
    Then the assembled commandline should be:
      """
      echo dbt run --select view_met_logica --vars "{'db': 'default_database', 'ldts': '2025-01-01 00:00:00'}" --target dev
      """

  Scenario: dbt_with_default_vars - config defaults with feature addition
    When I execute the dbt_with_default_vars process using commandline with the following arguments:
      | args      | value           |
      | vars.user | myuser          |
      | select    | view_met_logica |
    Then the assembled commandline should be:
      """
      echo dbt run --vars "{'db': 'default_database', 'ldts': '2025-01-01 00:00:00', 'user': 'myuser'}" --select view_met_logica --target dev
      """

  Scenario: dbt_with_default_vars - config defaults with feature override
    When I execute the dbt_with_default_vars process using commandline with the following arguments:
      | args    | value           |
      | vars.db | other_database  |
      | select  | view_met_logica |
    Then the assembled commandline should be:
      """
      echo dbt run --vars "{'db': 'other_database', 'ldts': '2025-01-01 00:00:00'}" --select view_met_logica --target dev
      """

  Scenario: dbt_with_default_vars - config defaults with override and addition
    When I execute the dbt_with_default_vars process using commandline with the following arguments:
      | args      | value           |
      | vars.db   | other_database  |
      | vars.user | myuser          |
      | select    | view_met_logica |
    Then the assembled commandline should be:
      """
      echo dbt run --vars "{'db': 'other_database', 'ldts': '2025-01-01 00:00:00', 'user': 'myuser'}" --select view_met_logica --target dev
      """
  # ==========================================================================
  # Custom arg_key_prefix and arg_key_value_separator (java_style config)
  # Config: arg_key_prefix="-D", arg_key_value_separator="="
  # Result: -Dkey=value instead of --key value
  # ==========================================================================

  Scenario: java_style - single arg
    When I execute the java_style process using commandline with the following arguments:
      | args | value      |
      | env  | production |
    Then the assembled commandline should be:
      """
      echo java -jar app.jar -Denv=production
      """

  Scenario: java_style - multiple args
    When I execute the java_style process using commandline with the following arguments:
      | args | value      |
      | env  | production |
      | port |       8080 |
    Then the assembled commandline should be:
      """
      echo java -jar app.jar -Denv=production -Dport=8080
      """

  Scenario: java_style - with starting_args override
    When I execute the java_style process using commandline with the following arguments:
      | args          | value      |
      | starting_args | -server    |
      | env           | production |
    Then the assembled commandline should be:
      """
      echo java -jar app.jar -server -Denv=production
      """
  # ==========================================================================
  # Custom arg_value_format (quoted_args config)
  # Config: arg_value_format='"{value}"'
  # Result: --key "value" instead of --key value
  # ==========================================================================

  Scenario: quoted_args - single arg
    When I execute the quoted_args process using commandline with the following arguments:
      | args | value |
      | name | Alice |
    Then the assembled commandline should be:
      """
      echo mytool --name "Alice"
      """

  Scenario: quoted_args - multiple args
    When I execute the quoted_args process using commandline with the following arguments:
      | args   | value       |
      | input  | data.csv    |
      | output | result.json |
    Then the assembled commandline should be:
      """
      echo mytool --input "data.csv" --output "result.json"
      """
  # ==========================================================================
  # NL steps (Dutch Gherkin variants)
  # ==========================================================================

  Scenario: dbt - NL step with feature args
    Wanneer ik het dbt proces uitvoer via commandline met de volgende argumenten:
      | args   | value    |
      | select | my_model |
    Dan moet het samengestelde commando als volgt zijn:
      """
      echo dbt run --select my_model --target dev
      """

  Scenario: dbt - NL step with vars (grouped args)
    Wanneer ik het dbt proces uitvoer via commandline met de volgende argumenten:
      | args      | value                |
      | vars.db   | database_op_teradata |
      | vars.ldts | 2025-07-26 00:00:00  |
    Dan moet het samengestelde commando als volgt zijn:
      """
      echo dbt run --vars "{'db': 'database_op_teradata', 'ldts': '2025-07-26 00:00:00'}" --target dev
      """

  Scenario: dbt_with_default_vars - NL step with config defaults
    Wanneer ik het dbt_with_default_vars proces uitvoer via commandline met de volgende argumenten:
      | args      | value           |
      | vars.user | myuser          |
      | select    | view_met_logica |
    Dan moet het samengestelde commando als volgt zijn:
      """
      echo dbt run --vars "{'db': 'default_database', 'ldts': '2025-01-01 00:00:00', 'user': 'myuser'}" --select view_met_logica --target dev
      """
