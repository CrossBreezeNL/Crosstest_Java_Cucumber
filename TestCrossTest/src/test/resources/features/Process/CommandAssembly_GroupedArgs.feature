Feature: Command assembly - dot-notation grouped arguments and group defaults
  I want to verify that dot-notation arguments are correctly grouped
  and that config-level group defaults are properly merged with feature entries.

  # ==========================================================================
  # Dot-notation grouped args (dbt config with group_format)
  # Groups share: group_format, group_entry_format, group_entry_separator
  # ==========================================================================

  Scenario: dbt - vars only (grouped args)
    When I execute the dbt commandline process using the following arguments:
      | args      | value                |
      | vars.db   | database_op_teradata |
      | vars.ldts |  2025-07-26 00:00:00 |
    Then the assembled commandline should be:
      """
      echo dbt run --vars "{'db': 'database_op_teradata', 'ldts': '2025-07-26 00:00:00'}" --target dev
      """

  Scenario: dbt - vars with regular args (mixed ordering)
    When I execute the dbt commandline process using the following arguments:
      | args      | value                |
      | vars.db   | database_op_teradata |
      | vars.ldts |  2025-07-26 00:00:00 |
      | select    | view_met_logica      |
    Then the assembled commandline should be:
      """
      echo dbt run --vars "{'db': 'database_op_teradata', 'ldts': '2025-07-26 00:00:00'}" --select view_met_logica --target dev
      """

  Scenario: dbt - vars with starting_args flag
    When I execute the dbt commandline process using the following arguments:
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
    When I execute the dbt commandline process using the following arguments:
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
    When I execute the helm commandline process using the following arguments:
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
    When I execute the dbt_with_default_vars commandline process using the following arguments:
      | args   | value           |
      | select | view_met_logica |
    Then the assembled commandline should be:
      """
      echo dbt run --select view_met_logica --vars "{'db': 'default_database', 'ldts': '2025-01-01 00:00:00'}" --target dev
      """

  Scenario: dbt_with_default_vars - config defaults with feature addition
    When I execute the dbt_with_default_vars commandline process using the following arguments:
      | args      | value           |
      | vars.user | myuser          |
      | select    | view_met_logica |
    Then the assembled commandline should be:
      """
      echo dbt run --vars "{'db': 'default_database', 'ldts': '2025-01-01 00:00:00', 'user': 'myuser'}" --select view_met_logica --target dev
      """

  Scenario: dbt_with_default_vars - config defaults with feature override
    When I execute the dbt_with_default_vars commandline process using the following arguments:
      | args    | value           |
      | vars.db | other_database  |
      | select  | view_met_logica |
    Then the assembled commandline should be:
      """
      echo dbt run --vars "{'db': 'other_database', 'ldts': '2025-01-01 00:00:00'}" --select view_met_logica --target dev
      """

  Scenario: dbt_with_default_vars - config defaults with override and addition
    When I execute the dbt_with_default_vars commandline process using the following arguments:
      | args      | value           |
      | vars.db   | other_database  |
      | vars.user | myuser          |
      | select    | view_met_logica |
    Then the assembled commandline should be:
      """
      echo dbt run --vars "{'db': 'other_database', 'ldts': '2025-01-01 00:00:00', 'user': 'myuser'}" --select view_met_logica --target dev
      """

  # ==========================================================================
  # NL steps (Dutch Gherkin variants)
  # ==========================================================================

  Scenario: dbt - NL step with vars (grouped args)
    Wanneer ik het dbt commandline proces uitvoer met de volgende argumenten:
      | args      | value                |
      | vars.db   | database_op_teradata |
      | vars.ldts | 2025-07-26 00:00:00  |
    Dan moet het samengestelde commando als volgt zijn:
      """
      echo dbt run --vars "{'db': 'database_op_teradata', 'ldts': '2025-07-26 00:00:00'}" --target dev
      """

  Scenario: dbt_with_default_vars - NL step with config defaults
    Wanneer ik het dbt_with_default_vars commandline proces uitvoer met de volgende argumenten:
      | args      | value           |
      | vars.user | myuser          |
      | select    | view_met_logica |
    Dan moet het samengestelde commando als volgt zijn:
      """
      echo dbt run --vars "{'db': 'default_database', 'ldts': '2025-01-01 00:00:00', 'user': 'myuser'}" --select view_met_logica --target dev
      """
