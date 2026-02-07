Feature: Command assembly - CommandLineConfig execution tool prefix
  I want to verify that the correct shell tool and flag are used
  for both Windows and other OS, based on the CommandLineConfig.

  # ==========================================================================
  # Execution tool prefix verification
  # ==========================================================================

  Scenario: Default shell - execution tool prefix
    When I execute the dbt process using commandline with the following arguments:
      | args   | value    |
      | select | my_model |
    Then the execution tool prefix on windows os should be:
      """
      cmd.exe /c
      """
    And the execution tool prefix on non-windows os should be:
      """
      bash -c
      """

  Scenario: Powershell config - execution tool prefix via step override
    When I execute the dbt process on powershell with the following arguments:
      | args   | value    |
      | select | my_model |
    Then the execution tool prefix on windows os should be:
      """
      powershell.exe -Command
      """
    And the execution tool prefix on non-windows os should be:
      """
      pwsh -Command
      """

  Scenario: Config-time CommandLineConfig binding - execution tool prefix
    When I execute the dbt_with_clconfig process using commandline with the following arguments:
      | args   | value    |
      | select | my_model |
    Then the execution tool prefix on windows os should be:
      """
      cmd.exe /c
      """
    And the execution tool prefix on non-windows os should be:
      """
      bash -c
      """

  Scenario: Execution-time override replaces config-time binding - execution tool prefix
    When I execute the dbt_with_clconfig process on powershell with the following arguments:
      | args   | value    |
      | select | my_model |
    Then the execution tool prefix on windows os should be:
      """
      powershell.exe -Command
      """
    And the execution tool prefix on non-windows os should be:
      """
      pwsh -Command
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
    En moet de gebruikte executie tool vooraf aan het commando op windows os als volgt zijn:
      """
      powershell.exe -Command
      """
    En moet de gebruikte executie tool vooraf aan het commando op niet-windows os als volgt zijn:
      """
      pwsh -Command
      """
