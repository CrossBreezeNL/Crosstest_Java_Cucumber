Feature: Command assembly - basic arguments and special argument overrides
  I want to verify that commandline processes are assembled correctly
  from ProcessConfig parameters and feature file argument tables.

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
  # NL steps (Dutch Gherkin variant)
  # ==========================================================================

  Scenario: dbt - NL step with feature args
    Wanneer ik het dbt proces uitvoer via commandline met de volgende argumenten:
      | args   | value    |
      | select | my_model |
    Dan moet het samengestelde commando als volgt zijn:
      """
      echo dbt run --select my_model --target dev
      """
