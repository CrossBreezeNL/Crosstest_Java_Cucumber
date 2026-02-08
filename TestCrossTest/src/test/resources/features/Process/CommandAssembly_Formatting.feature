Feature: Command assembly - custom formatting and argument defaults
  I want to verify that custom argument formatting (prefix, separator, value format)
  and config-level regular argument defaults work correctly.

  # ==========================================================================
  # Custom arg_key_prefix and arg_key_value_separator (java_style config)
  # Config: arg_key_prefix="-D", arg_key_value_separator="="
  # Result: -Dkey=value instead of --key value
  # ==========================================================================

  Scenario: java_style - single arg
    When I execute the java_style commandline process using the following arguments:
      | args | value      |
      | env  | production |
    Then the assembled commandline should be:
      """
      echo java -jar app.jar -Denv=production
      """

  Scenario: java_style - multiple args
    When I execute the java_style commandline process using the following arguments:
      | args | value      |
      | env  | production |
      | port |       8080 |
    Then the assembled commandline should be:
      """
      echo java -jar app.jar -Denv=production -Dport=8080
      """

  Scenario: java_style - with starting_args override
    When I execute the java_style commandline process using the following arguments:
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
    When I execute the quoted_args commandline process using the following arguments:
      | args | value |
      | name | Alice |
    Then the assembled commandline should be:
      """
      echo mytool --name "Alice"
      """

  Scenario: quoted_args - multiple args
    When I execute the quoted_args commandline process using the following arguments:
      | args   | value       |
      | input  | data.csv    |
      | output | result.json |
    Then the assembled commandline should be:
      """
      echo mytool --input "data.csv" --output "result.json"
      """

  # ==========================================================================
  # Config-level regular argument defaults
  # Non-reserved, non-dot-notation parameters in the config are treated as
  # regular argument defaults. Feature table entries override or extend them.
  # ==========================================================================

  Scenario: dbt_with_default_regular_args - config defaults used (no regular args in table)
    When I execute the dbt_with_default_regular_args commandline process using the following arguments:
      | args        | value            |
      | ending_args | --target staging |
    Then the assembled commandline should be:
      """
      echo dbt run --select my_default_model --target staging
      """

  Scenario: dbt_with_default_regular_args - feature arg overrides config default
    When I execute the dbt_with_default_regular_args commandline process using the following arguments:
      | args   | value            |
      | select | overridden_model |
    Then the assembled commandline should be:
      """
      echo dbt run --select overridden_model --target dev
      """

  Scenario: tool_with_defaults - config defaults with feature addition
    When I execute the tool_with_defaults commandline process using the following arguments:
      | args   | value      |
      | output | result.txt |
    Then the assembled commandline should be:
      """
      echo mytool --output result.txt --path c:\data --format csv
      """

  Scenario: tool_with_defaults - override one config default and add new
    When I execute the tool_with_defaults commandline process using the following arguments:
      | args   | value      |
      | path   | d:\other   |
      | output | result.txt |
    Then the assembled commandline should be:
      """
      echo mytool --path d:\other --output result.txt --format csv
      """

  Scenario: tool_with_defaults - override all config defaults
    When I execute the tool_with_defaults commandline process using the following arguments:
      | args   | value     |
      | path   | d:\output |
      | format | json      |
    Then the assembled commandline should be:
      """
      echo mytool --path d:\output --format json
      """

  # ==========================================================================
  # Mixed regular argument defaults + group (vars) defaults
  # ==========================================================================

  Scenario: dbt_with_default_regular_and_vars - regular arg + group defaults, override both
    When I execute the dbt_with_default_regular_and_vars commandline process using the following arguments:
      | args    | value          |
      | select  | custom_model   |
      | vars.db | other_database |
    Then the assembled commandline should be:
      """
      echo dbt run --select custom_model --vars "{'db': 'other_database', 'ldts': '2025-01-01 00:00:00'}" --target dev
      """

  Scenario: dbt_with_default_regular_and_vars - no overrides, all defaults used
    When I execute the dbt_with_default_regular_and_vars commandline process using the following arguments:
      | args        | value            |
      | ending_args | --target staging |
    Then the assembled commandline should be:
      """
      echo dbt run --select my_default_model --vars "{'db': 'default_database', 'ldts': '2025-01-01 00:00:00'}" --target staging
      """
