Feature: ProcessExecutor - Command line output assertions
  I want to verify the output of executed command line commands

  Scenario: Output should contain a substring
    When I execute the following command
    """
    echo Hello World
    """
    Then the commandline output should contain:
    """
    Hello World
    """

  Scenario: Output should be an exact match
    When I execute the following command
    """
    echo Hello World
    """
    Then the commandline output should be:
    """
    Hello World
    """

  Scenario: Output contains text from chained commands
    When I execute the following command
    """
    echo First && echo Second
    """
    Then the commandline output should contain:
    """
    First
    """

  Scenario: Multiline output should be an exact match
    When I execute the following command
    """
    echo First && echo Second
    """
    Then the commandline output should be:
    """
    First
    Second
    """

  Scenario: Multiline output should contain multiline substring
    When I execute the following command
    """
    echo Line1 && echo Line2 && echo Line3
    """
    Then the commandline output should contain:
    """
    Line1
    Line2
    """

  Scenario: Multiline output exact match - NL variant
    Wanneer ik het volgende commando uitvoer
    """
    echo Eerste && echo Tweede
    """
    Dan de commandline uitvoer als volgt moet zijn:
    """
    Eerste
    Tweede
    """

  Scenario: Output should contain - NL variant
    Wanneer ik het volgende commando uitvoer
    """
    echo Hallo
    """
    Dan de commandline uitvoer het volgende moet bevatten:
    """
    Hallo
    """
