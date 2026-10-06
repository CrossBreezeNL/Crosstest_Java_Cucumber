# language: nl
Functionaliteit: Command assembly - basic arguments and special argument overrides (NL)

  # ==========================================================================
  # NL steps (Dutch Gherkin variant)
  # ==========================================================================

  Scenario: dbt - NL step with feature args
    Wanneer ik het dbt commandline proces uitvoer met de volgende argumenten:
      | args   | value    |
      | select | my_model |
    Dan moet het samengestelde commando als volgt zijn:
      """
      echo dbt run --select my_model --target dev
      """
