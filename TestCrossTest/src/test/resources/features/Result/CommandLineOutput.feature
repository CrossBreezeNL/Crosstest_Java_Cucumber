Feature: ProcessExecutor - Command line output assertions
  I want to verify the output of executed command line commands

  Scenario: Output should contain a substring
    When I execute the following command
    """
    echo Hello World
    """
    Then the commandline output must contain:
    """
    Hello World
    """

  Scenario: Output should be an exact match
    When I execute the following command
    """
    echo Hello World
    """
    Then the commandline output must be:
    """
    Hello World
    """

  Scenario: Output contains text from chained commands
    When I execute the following command
    """
    echo First && echo Second
    """
    Then the commandline output must contain:
    """
    First
    """

  Scenario: Multiline output should be an exact match
    When I execute the following command
    """
    echo First && echo Second
    """
    Then the commandline output must be:
    """
    First
    Second
    """

  Scenario: Multiline output should contain multiline substring
    When I execute the following command
    """
    echo Line1 && echo Line2 && echo Line3
    """
    Then the commandline output must contain:
    """
    Line1
    Line2
    """
