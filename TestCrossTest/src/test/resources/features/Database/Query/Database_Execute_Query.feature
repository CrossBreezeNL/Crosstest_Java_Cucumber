@Unit @Database
Feature: Execute a query or statement on a database

  @Positive
  Scenario: Test insert on composite object
    Given the object Customer is empty
    When I insert the following data for object Customer:
      | CUST_ID | Cust_Name | CUST_DOB   |
      |    1234 | Mr. Smith | 1970-10-13 |
      |     431 | Ms. Jones | 1985-12-01 |
    And I execute the following query on source:
      """
      SELECT CUST_ID, CREATE_DD
      FROM source.CUST_HUB
      """
    Then I expect the following result:
      | CUST_ID | CREATE_DD |
      |    1234 |           |
      |     431 |           |

  @Positive
  Scenario: Test query with update stament
    Given the source table CUST_HUB is empty
    When I insert the following data in source table CUST_HUB:
      | CUST_ID |
      |    1234 |
      |     431 |
    And I execute the following statement on source:
      """
      UPDATE source.CUST_HUB
      SET CUST_ID = 444
      WHERE CUST_ID = 431
      """
    And I retrieve the contents of the source CUST_HUB table
    Then I expect the following result:
      | CUST_ID |
      |    1234 |
      |     444 |

  @Positive
  Scenario: Only one column with NULL
    When I execute the following query on source:
      """
      SELECT NULL AS FieldA
      """
    Then I expect the following result:
      | FieldA |
      |        |

  @Positive
  Scenario: One column with no name
    When I execute the following query on source:
      """
      SELECT 'Test' 
      """
    Then I expect the following result:
      |      |
      | Test |

  @Positive
	Scenario: One column with whitespace as name
    When I execute the following query on source:
      """
      SELECT 'Test' AS ' '
      """
    Then I expect the following result:
      |      |
      | Test |
      
  @Positive
  Scenario: Compare on partial result of select
    When I execute the following query on source:
      """
      SELECT 1 as firstField, 2 as secondField
      """
    Then I expect the following result:
      | firstField |
      |          1 |

  @Positive
  Scenario: Compare on empty result of select
    When I execute the following query on source:
      """
      SELECT 1 as firstField
      WHERE 1 = 0

      """
    Then I expect the following result:
      | firstField |

  @Negative
  Scenario: Expect empty result but get data back
    When I execute the following query on source:
      """
      SELECT 1 as firstField      

      """
    Then I expect the following result to fail with "Expected 0 unique records but found 1 unique records":
      | firstField |

  @Negative
  Scenario: Test the command timeout
    When I execute the following query on timeout_source expecting a timeout:
      """
      WAITFOR DELAY '00:00:05'
      SELECT *
      FROM [Source].[CUST_HUB]

      """

  # Note that for this scenario to succeed, when editing in Eclipse the Eclipse file encoding needs to be UTF-8
  # https://stackoverflow.com/questions/9180981/how-to-support-utf-8-encoding-in-eclipse
  @Positive
  Scenario: Insert and select data using a update statement
    Given the source table Table with strangé character$ is empty
    When I execute the following statement on source:
      """
      	INSERT INTO [Source].[Table with strangé character$]
      	SELECT 1, 'Test'
      """
    And I execute the following query on source:
      """
      	SELECT *
      	FROM [Source].[Table with strangé character$]
      """
    Then I expect the following result:
      | ID | Fie#ld with \\Strange namë |
      |  1 | Test                        |
