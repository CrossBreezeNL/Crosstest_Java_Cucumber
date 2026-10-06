@InProgress
Feature: Policy Life Cycle Status

  Scenario: 1. Functional test of the business rule
    When I insert the following data for object insurancePolicy:
      | INS_POLCY_SQN | INS_POLCY_BK | INS_POLCY_ST_CODE |
      |           101 |         1001 |               001 |
      |           102 |         1002 |               906 |
      |           103 |         1003 |               901 |
      |           104 |         1004 |               002 |
      |           105 |         1005 |               500 |
      |           106 |         1006 |               905 |
      |           107 |         1007 |               902 |
      |           108 |         1008 |               312 |
      |           109 |         1009 |                   |
    And I insert the following data for object insuranceCCSPolicy:
      | INS_POLCY_SQN | INS_POLCY_BK | INS_POLCY_EXPEL_F |
      |           110 |         1010 | N                 |
      |           111 |         1011 | Y                 |
      |           112 |         1012 | X                 |
      |           113 |         1013 |                   |
