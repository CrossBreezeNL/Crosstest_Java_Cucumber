
#SQLServer does not support setting a default schema per query, so this will not work
@InProgress
Feature: Test default database or schema setting

  Background: 

  Scenario: Insert data in table, update without fully qualified tablename and check result
    Given the source table CUST_HUB is empty
    
    When I insert the following data in source table CUST_HUB:
      | CUST_ID | CREATE_DD  |
      |    1234 | 2019-11-01 |
      |     431 |            |
    
    And I execute the following statement on source:
      """
      	UPDATE CUST_HUB
      	SET CREATE_DD = '2020-01-01'
      	WHERE CUST_ID = 1234
      """
      
    And I retrieve the contents of the source CUST_HUB table
    
    Then I expect the following result:
      | CUST_ID | CREATE_DD  |
      |    1234 | 2020-01-01 |
      |     431 |            |
