# language: nl
@Windows
Functionaliteit: Command assembly - CommandLineConfig execution tool prefix (NL)

  # ==========================================================================
  # NL steps (Dutch Gherkin variant)
  # ==========================================================================

  Scenario: dbt - NL step with execution-time CommandLineConfig override
    Wanneer ik het dbt commandline proces uitvoer via powershell met de volgende argumenten:
      | args   | value    |
      | select | my_model |
    Dan moet het samengestelde commando als volgt zijn:
      """
      echo dbt run --select my_model --target dev
      """
    En moet de gebruikte executie tool vooraf aan het commando als volgt zijn:
      """
      powershell.exe -Command
      """
