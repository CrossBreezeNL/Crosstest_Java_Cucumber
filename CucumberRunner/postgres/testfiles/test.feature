Feature: a first testfeature
    Scenario: Running a crosstest scenario
        When I execute the following query on demo:
            """
            SELECT 'Hello World' AS "FirstResult"
            """

        Then I expect the following result:
            | FirstResult |
            | Hello World |
