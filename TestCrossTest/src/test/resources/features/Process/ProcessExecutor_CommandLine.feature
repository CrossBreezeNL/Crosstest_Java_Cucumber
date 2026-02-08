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

  Scenario: Run a command with chained commands using &&
    When I execute the following command
    """
    echo First && echo Second
    """

  # ==========================================================================
  # Execute command with a specific CommandLineConfig
  # ==========================================================================

  Scenario: Run a command using powershell config
    When I execute the following powershell command:
    """
    echo PowerShell test
    """

  Scenario: Run a command using powershell config - NL
    Wanneer ik het volgende powershell commando uitvoer:
    """
    echo PowerShell NL test
    """
