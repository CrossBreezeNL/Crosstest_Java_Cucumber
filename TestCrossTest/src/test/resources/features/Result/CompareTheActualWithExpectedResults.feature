@Unit
Feature: Compare the actual with expected results

  Scenario Outline: Insert data in table and check result <scenario>
    Given the source table CUST_HUB is empty
    When I insert the following data in source table CUST_HUB:
      | CUST_ID | CREATE_DD  |
      |    1234 | 2019-11-01 |
      |     431 |            |
    And I retrieve the contents of the source CUST_HUB table
    Then I expect the following result:
      | CUST_ID          | CREATE_DD  |
      |             1234 | 2019-11-01 |
      | <expectedOutput> |            |

    @Positive
    Examples: 
      | scenario | expectedOutput |
      | No error |            431 |

  @Negative
  Scenario: Insert data in table and check result With error
    Given the source table CUST_HUB is empty
    When I insert the following data in source table CUST_HUB:
      | CUST_ID | CREATE_DD  |
      |    1234 | 2019-11-01 |
      |     431 |            |
    And I retrieve the contents of the source CUST_HUB table
    Then I expect the following result to fail with "expected record 2 not found in actual result":
      | CUST_ID | CREATE_DD  |
      |    1234 | 2019-11-01 |
      |     432 |            |

  @Positive
  Scenario: Ignore unspecified column in output
    Given the source table CUST_HUB is empty
    When I insert the following data in source table CUST_HUB:
      | CUST_ID | CREATE_DD  |
      |    1234 | 2019-11-01 |
    And I retrieve the contents of the source CUST_HUB table
    Then I expect the following result:
      | CUST_ID |
      |    1234 |

  @Negative
  Scenario: Fail on invalid expected column
    Given the source table CUST_HUB is empty
    When I insert the following data in source table CUST_HUB:
      | CUST_ID | CREATE_DD  |
      |    1234 | 2019-11-01 |
    And I retrieve the contents of the source CUST_HUB table
    Then I expect the following result to fail with "Invalid column name 'CREATE_D'":
      | CUST_ID | CREATE_D   |
      |    1234 | 2019-11-01 |

  @Positive
  Scenario: Compare expected duplicate records
    Given the source table CUST_HUB is empty
    When I insert the following data in source table CUST_HUB:
      | CUST_ID | CREATE_DD  |
      |    1234 | 2019-11-01 |
      |    1234 | 2019-11-01 |
      |    1234 | 2019-11-01 |
      |    1235 | 2019-11-01 |
    And I retrieve the contents of the source CUST_HUB table
    Then I expect the following result:
      | CUST_ID | CREATE_DD  |
      |    1234 | 2019-11-01 |
      |    1234 | 2019-11-01 |
      |    1234 | 2019-11-01 |
      |    1235 | 2019-11-01 |

  @Negative
  Scenario: Compare non-expected duplicate records
    Given the source table CUST_HUB is empty
    When I insert the following data in source table CUST_HUB:
      | CUST_ID | CREATE_DD  |
      |    1234 | 2019-11-01 |
      |    1234 | 2019-11-01 |
    And I retrieve the contents of the source CUST_HUB table
    Then I expect the following result to fail with "different number of duplicate rows":
      | CUST_ID | CREATE_DD  |
      |    1234 | 2019-11-01 |
