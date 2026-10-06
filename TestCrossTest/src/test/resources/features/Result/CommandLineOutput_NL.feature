# language: nl
Functionaliteit: ProcessExecutor - Command line output assertions (NL)

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
