@Unit @Database
Feature: Write to and retrieve from database tables

  @Positive
  Scenario: Insert data in table
    Given the source table CUST_HUB is empty
    When I insert the following data in source table CUST_HUB:
      | CUST_ID | CREATE_DD  |
      |    1234 | 2019-11-01 |
    And I retrieve the contents of the source CUST_HUB table
    Then I expect the following result:
      | CUST_ID | CREATE_DD  |
      |    1234 | 2019-11-01 |

  @Positive
  Scenario: Insert and retrieve a null
    Given the source table CUST_HUB is empty
    When I insert the following data in source table CUST_HUB:
      | CUST_ID | CREATE_DD |
      |     431 |           |
    And I retrieve the contents of the source CUST_HUB table
    Then I expect the following result:
      | CUST_ID | CREATE_DD |
      |     431 |           |

  @Positive
  Scenario: Do select on empty table
    Given the source table CUST_HUB is empty
    When I retrieve the contents of the source CUST_HUB table
    Then I expect the following result:
      | CUST_ID | CREATE_DD |

  @Negative
  Scenario: Expect empty result but get a record back
    Given the source table CUST_HUB is empty
    When I insert the following data in source table CUST_HUB:
      | CUST_ID | CREATE_DD |
      |     431 |           |
    And I retrieve the contents of the source CUST_HUB table
    Then I expect the following result to fail with "Expected 0 unique records but found 1 unique records":
      | CUST_ID | CREATE_DD |

  # Note that for this scenario to succeed, when editing in Eclipse the Eclipse file encoding needs to be UTF-8
  # https://stackoverflow.com/questions/9180981/how-to-support-utf-8-encoding-in-eclipse
  @Positive
  Scenario: Insert data into a table with strange characters
    Given the source table Table with strangé character$ is empty
    When I insert the following data in source table Table with strangé character$:
      | ID   | Fie#ld with \\Strange namë |
      | 1234 | Test value                  |
    And I retrieve the contents of the source Table with strangé character$ table
    Then I expect the following result:
      | ID   | Fie#ld with \\Strange namë |
      | 1234 | Test value                  |

  Scenario Outline: Test with <Datatype> field
    Given the source table DataTypeTest is empty
    When I insert the following data in source table DataTypeTest:
      | Test_<Datatype> |
      | <Value>         |
    And I retrieve the contents of the source DataTypeTest table
    Then I expect the following result:
      | Test_<Datatype> |
      | <Value>         |

    @Positive
    Examples: 
      | Datatype   | Value                    |
      | BigInt     |      1234567891234512345 |
      | Boolean    |                        1 |
      | Varchar    | Some text                |
      | Char       | Not25Chars               |
      | Date       | 2019-11-01               |
      | Decimal    | 123456789012345678901.02 |
      | BigDecimal |                     0.55 |
      | FloatSmall |                       12 |
      | FloatBig   |                       12 |
      | FloatSmall |                   12.501 |
      | FloatBig   |                   12.399 |
      | Real       |                   12.399 |
      | Real       |                       12 |

  @Negative
  Scenario Outline: Test with Bigdecimal field <Scenario>
    Given the source table DataTypeTest is empty
    When I insert the following data in source table DataTypeTest:
      | Test_BigDecimal |
      | <Value>         |
    And I retrieve the contents of the source DataTypeTest table
    Then I expect the following result to fail with "expected record 1 not found in actual result":
      | Test_BigDecimal |
      | <OutValue>      |

    Examples: 
      | Scenario  | Value | OutValue |
      # The example below is actually negative, should result in error
      | Incorrect |  0.55 |     0.78 |

  @Negative
  Scenario: Char unexpected result comparison
    Given the source table DataTypeTest is empty
    When I insert the following data in source table DataTypeTest:
      | Test_Char |
      | Input     |
    And I retrieve the contents of the source DataTypeTest table
    Then I expect the following result to fail with "expected record 1 not found in actual result":
      | Test_Char |
      | Output    |

  @Negative
  Scenario: Shifting empty field should be detected as difference
    Given the source table Customer is empty
    When I insert the following data in source table Customer:
      | Customer_ID | Customer_Name | Country | IsActive |
      |           1 | Smith         |         |        1 |
    And I retrieve the contents of the source Customer table
    Then I expect the following result to fail with "expected record 1 not found in actual result":
      | Customer_ID | Customer_Name | Country | IsActive |
      |           1 |               | Smith   |        1 |

  @Positive
  Scenario: Set empty string
    Given the source table Customer is empty
    When I insert the following data in source table Customer:
      | Customer_ID | Customer_Name | Country | IsActive |
      |           1 | Smith         | ''      |        1 |
    And I retrieve the contents of the source Customer table
    Then I expect the following result:
      | Customer_ID | Customer_Name | Country | IsActive |
      |           1 | Smith         | ''      |        1 |

  @Positive
  Scenario Outline: Trailing zeroes make no difference <scenario>
    Given the source table DataTypeTest is empty
    When I insert the following data in source table DataTypeTest:
      | Test_Decimal |
      | <InputValue> |
    And I retrieve the contents of the source DataTypeTest table
    Then I expect the following result:
      | Test_Decimal  |
      | <OutputValue> |

    Examples: 
      | scenario      | InputValue | OutputValue |
      | same decimals |      25.00 |       25.00 |
      | Less decimals |      25.00 |        25.0 |
      | No decimals   |      25.00 |          25 |
      | More decimals |       25.0 |       25.00 |
