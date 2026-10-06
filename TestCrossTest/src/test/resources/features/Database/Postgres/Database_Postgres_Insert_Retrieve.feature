@Unit @Database @Postgres
Feature: Write to and retrieve from PostgreSQL tables
  Requires the PostgreSQL database from the dev container (.devcontainer/docker-compose.yml).

  @Positive
  Scenario: Insert data in a PostgreSQL table
    Given the pg_source table CUST_HUB is empty
    When I insert the following data in pg_source table CUST_HUB:
      | CUST_ID | CREATE_DD  |
      |    1234 | 2019-11-01 |
    And I retrieve the contents of the pg_source CUST_HUB table
    Then I expect the following result:
      | CUST_ID | CREATE_DD  |
      |    1234 | 2019-11-01 |

  @Positive
  Scenario: Insert and retrieve a null in a PostgreSQL table
    Given the pg_source table CUST_HUB is empty
    When I insert the following data in pg_source table CUST_HUB:
      | CUST_ID | CREATE_DD |
      |     431 |           |
    And I retrieve the contents of the pg_source CUST_HUB table
    Then I expect the following result:
      | CUST_ID | CREATE_DD |
      |     431 |           |
