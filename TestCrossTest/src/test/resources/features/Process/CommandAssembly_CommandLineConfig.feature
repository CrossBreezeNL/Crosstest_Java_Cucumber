Feature: Command assembly - CommandLineConfig execution tool prefix
  I want to verify that the correct shell tool and flag are used
  based on the CommandLineConfig.

  # ==========================================================================
  # Execution tool prefix verification
  # ==========================================================================

  Scenario: Default shell - execution tool prefix
    When I execute the dbt process using commandline with the following arguments:
      | args   | value    |
      | select | my_model |
    Then the execution tool prefix should be:
      """
      cmd.exe /c
      """

  Scenario: Powershell config - execution tool prefix via step override
    When I execute the dbt process on powershell with the following arguments:
      | args   | value    |
      | select | my_model |
    Then the execution tool prefix should be:
      """
      powershell.exe -Command
      """

  Scenario: Config-time CommandLineConfig binding - execution tool prefix
    When I execute the dbt_with_clconfig process using commandline with the following arguments:
      | args   | value    |
      | select | my_model |
    Then the execution tool prefix should be:
      """
      cmd.exe /c
      """

  Scenario: Execution-time override replaces config-time binding - execution tool prefix
    When I execute the dbt_with_clconfig process on powershell with the following arguments:
      | args   | value    |
      | select | my_model |
    Then the execution tool prefix should be:
      """
      powershell.exe -Command
      """

  # ==========================================================================
  # NL steps (Dutch Gherkin variant)
  # ==========================================================================

  Scenario: dbt - NL step with execution-time CommandLineConfig override
    Wanneer ik het dbt proces uitvoer op powershell met de volgende argumenten:
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
