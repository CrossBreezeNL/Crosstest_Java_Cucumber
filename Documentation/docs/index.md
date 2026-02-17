# Introduction
This wiki contains the documentation of the Java/Cucumber version of CrossTest.
Documentation for the .Net/Specflow version can be found [here](../DotNet)

See the [getting started](./GettingStarted.md) page for more information on how to configure CrossTest.

## Example test scenario
Below is a simple example of a test scenario where data is inserted into a table, then a ETL proces is run and finally the contents of a table is compared with an expected result.

```gherkin
Feature: A simple demo of CrossTest
  I want to create a simple test scenario using CrossTest

  Scenario: Simple scenario
   Given the source table Customer is empty
   And the target table Customer is empty
   
   When I insert the following data in source table Customer:
      | Customer_ID | Customer_Name | Country |
      |        1234 | Henk          | NL      |
      |         431 | Harry         | USA     |
   
   And I run the demo process load_Customer  
   
   And I retrieve the contents of the target Customer table
   Then I expect the following result:
      | Customer_ID | Customer_Name | Country |
      |        1234 | Henk          | NL      |
      |         431 | Harry         | USA     |
```

