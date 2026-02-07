Feature: ProcessExecutor - Command line execution
  I want to execute arbitrary command line commands

  Scenario: Run a single-line echo command that creates a file
    When I execute the following command
    """
    echo Single Line
    """

  Scenario: Run a chained command (triple-quoted)
    When I execute the following command
    """
    echo First Line
    java -version
    """

  Scenario: Run a command with double quotes in the argument
    When I execute the following command
    """
    echo "This is a quoted string"
    """

  Scenario: Run a command with single quotes in the argument
    When I execute the following command
    """
    echo 'This is a single-quoted string'
    """

  Scenario: Run a command with special characters
    When I execute the following command
    """
    echo Special characters: !@#$%^&*()_+
    """

  Scenario: Run a command with a parameter containing spaces
    When I execute the following command
    """
    echo "Parameter with spaces"
    """

  Scenario: Run a command with environment variable
    When I execute the following command on windows os:
    """
    echo %USERNAME%
    """

  Scenario: Run a command with command substitution (Unix-like)
    When I execute the following command on non-windows os:
    """
    echo The date is: $(date)
    """

  Scenario: Run a command with chained commands using &&
    When I execute the following command
    """
    echo First && echo Second
    """

  Scenario: Run a command with a pipe on windows
    When I execute the following command on windows os:
    """
    echo Hello World | findstr "World"
    """

  Scenario: Run a command with a pipe on other
    When I execute the following command on non-windows os:
    """
    echo Hello World | grep "World"
    """

  Scenario: Run a command with a pipe on windows - NL
    Wanneer ik het volgende commando uitvoer op windows os:
    """
    echo Hello World | findstr "World"
    """

  Scenario: Run a command with a pipe on other - NL
    Wanneer ik het volgende commando uitvoer op niet-windows os:
    """
    echo Hello World | grep "World"
    """

  # ==========================================================================
  # Execute command with a specific CommandLineConfig
  # ==========================================================================

  Scenario: Run a command using powershell config
    When I execute the following powershell command:
    """
    echo PowerShell test
    """

  Scenario: Run a command using powershell config on windows
    When I execute the following powershell command on windows os:
    """
    Write-Output 'PowerShell on Windows'
    """

  Scenario: Run a command using powershell config on other
    When I execute the following powershell command on non-windows os:
    """
    echo 'PowerShell on other'
    """

  Scenario: Run a command using powershell config - NL
    Wanneer ik het volgende powershell commando uitvoer:
    """
    echo PowerShell NL test
    """
