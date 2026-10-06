# language: nl
Functionaliteit: Command assembly - dot-notation grouped arguments and group defaults (NL)

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
