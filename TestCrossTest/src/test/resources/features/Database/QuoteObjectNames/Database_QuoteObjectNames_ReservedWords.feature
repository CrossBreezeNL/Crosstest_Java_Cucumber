@Unit @Database
Feature: Quote object names - reserved words
  With quoteObjectNames="true" on the database config, CrossTest quotes the table and column names
  in the statements it generates, so tables and columns named with reserved words can be used.
  The ORDER and GROUP tables and the CHECK, USER and GROUP columns are reserved words in
  SQL Server, PostgreSQL and Teradata.

  @Positive
  Scenario Outline: Empty, insert into and retrieve from a reserved word table on <database>
    Given the <db> table ORDER is empty
    When I insert the following data in <db> table ORDER:
      | CHECK | USER  | GROUP  |
      |     1 | Smith | Admins |
      |     2 | Jones |        |
    And I retrieve the contents of the <db> ORDER table
    Then I expect the following result:
      | CHECK | USER  | GROUP  |
      |     1 | Smith | Admins |
      |     2 | Jones |        |

    Examples:
      | database   | db     |
      | SQL Server | source |

    @Postgres
    Examples:
      | database   | db        |
      | PostgreSQL | pg_source |

  @Positive
  Scenario Outline: Insert using a template with a default value for a reserved word column on <database>
    Given the <db> table ORDER is empty
    When I insert the following data using template reserved_words in <db> table ORDER:
      | CHECK | USER  |
      |     1 | Smith |
    And I retrieve the contents of the <db> ORDER table
    Then I expect the following result:
      | CHECK | USER  | GROUP         |
      |     1 | Smith | Default group |

    Examples:
      | database   | db     |
      | SQL Server | source |

    @Postgres
    Examples:
      | database   | db        |
      | PostgreSQL | pg_source |

  @Positive
  Scenario Outline: Empty and insert into a composite object with reserved word tables and key field on <database>
    Given the object <object> is empty
    When I insert the following data for object <object>:
      | CHECK | USER  | GROUP  |
      |     1 | Smith | Admins |
      |     1 | Smith | Users  |
      |     2 | Jones | Users  |
    And I retrieve the contents of the <db> GROUP table
    Then I expect the following result:
      | CHECK |
      |     1 |
      |     2 |
    And I retrieve the contents of the <db> ORDER table
    Then I expect the following result:
      | CHECK | USER  | GROUP  |
      |     1 | Smith | Admins |
      |     1 | Smith | Users  |
      |     2 | Jones | Users  |

    Examples:
      | database   | db     | object         |
      | SQL Server | source | ReservedWords  |

    @Postgres
    Examples:
      | database   | db        | object          |
      | PostgreSQL | pg_source | PgReservedWords |

  # Proves the tables above really need quoting: the same statement fails on a config without quoteObjectNames.
  # SQL Server only: PostgreSQL accepts reserved words after a schema qualifier (source.ORDER); unquoted names
  # fail there because PostgreSQL folds them to lower case, which is not what this scenario is about.
  @Negative
  Scenario: Reserved word table names fail without quoteObjectNames on SQL Server
    Given emptying the source_unquoted table ORDER should fail with "Incorrect syntax near the keyword 'ORDER'"
