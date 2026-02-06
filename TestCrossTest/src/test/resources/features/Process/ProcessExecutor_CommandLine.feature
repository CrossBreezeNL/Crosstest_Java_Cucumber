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
    When I execute the following command
    """
    echo %USERNAME%
    """

  Scenario: Run a command with command substitution (Unix-like)
    When I execute the following command
    """
    echo The date is: $(date)
    """

  Scenario: Run a command with a background process (Unix-like)
    When I execute the following command
    """
    sleep 5 &
    echo Done
    """    

  Scenario: Run a command with chained commands using &&
    When I execute the following command
    """
    echo First && echo Second
    """

  Scenario: Run a command with a pipe - NL
    When ik het volgende commando uitvoer
    """
    echo Hello World | find "World"
    """
