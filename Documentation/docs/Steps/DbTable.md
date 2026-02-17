# DbTable
This page describes the DbTable steps.

## Delete templated table data
Deletes data from a table


### Sentences
| Type          | Language         | Sentence      |
|:---           |:---              |:---           |
| Given | en | ^the ([a-zA-Z0-9_@$#-]+) table (.+) is empty$ |


### Arguments
The details of every argument of the step are listed below.

| Parameter    | Datatype          | Description          |
|:---          |:---               |:---                  |
|database config | String | The name of the database config |
|table name | String | The name of the table to delete the data from |

### Examples


```gherkin
 Given the demo table CUST_SAT is empty
```

## Retrieve data of templated table
Retrieves data from a table or view


### Sentences
| Type          | Language         | Sentence      |
|:---           |:---              |:---           |
| When | en | ^I retrieve the contents of the ([a-zA-Z0-9_@$#-]+) (.+) (?:table|view)$ |


### Arguments
The details of every argument of the step are listed below.

| Parameter    | Datatype          | Description          |
|:---          |:---               |:---                  |
|database config | String | The name of the database config |
|table name | String | The name of the table to retrieve the data from |

### Examples


```gherkin
 When I retrieve the contents of the demo CUST_SAT table
```

## Insert data into table
Loads data into a table


### Sentences
| Type          | Language         | Sentence      |
|:---           |:---              |:---           |
| When | en | ^I insert the following data in ([a-zA-Z0-9_@$#-]+) table (.+):$ |


### Arguments
The details of every argument of the step are listed below.

| Parameter    | Datatype          | Description          |
|:---          |:---               |:---                  |
|database config | String | The name of the database config |
|table name | String | The name of the table to insert the data into |
|data to insert | DataTable | The data to load into the table. See [TestDataTable](Tables.md#testdatatable). |

### Examples


```gherkin
 When I insert the following data in demo table CUST_SAT:
  | Id | Description    |
  | 1  | 'FirstRow'       |
  | 2  | 'SecondRow' |
```

## Add key table to composite object
Adds a key table to a composite object. 'Key' means inserts into this table will be made distinct


### Sentences
| Type          | Language         | Sentence      |
|:---           |:---              |:---           |
| Given | en | ^I have a key table named (.+) in ([a-zA-Z0-9_@$#-]+) for object (.+)$ |


### Arguments
The details of every argument of the step are listed below.

| Parameter    | Datatype          | Description          |
|:---          |:---               |:---                  |
|table name | String | The name of the key table to add to the composite object |
|database config | String | The name of the database config that applies to the key table |
|composite object | String | The name of the composite object the key table is added to. This can be a new composite object or one that is configured in the CrossTest configuration. |

### Examples


```gherkin
 Given I have a key table named CUST_HUB in demo for object Customer
```

## Add context table to composite object
Adds a context table to a composite object. 'Context' means inserts into this table will not be made distinct


### Sentences
| Type          | Language         | Sentence      |
|:---           |:---              |:---           |
| Given | en | ^I have a context table named (.+) in ([a-zA-Z0-9_@$#-]+) for object (.+)$ |


### Arguments
The details of every argument of the step are listed below.

| Parameter    | Datatype          | Description          |
|:---          |:---               |:---                  |
|table name | String | The name of the context table to add to the composite object |
|database config | String | The name of the database config that applies to the context table |
|composite object | String | The name of the composite object the context table is added to. This can be a new composite object or one that is configured in the CrossTest configuration. |

### Examples


```gherkin
 Given I have a context table named CUST_SAT in demo for object Customer
```

## Delete data from composite object
Deletes data from all tables belonging to a composite object


### Sentences
| Type          | Language         | Sentence      |
|:---           |:---              |:---           |
| Given | en | ^the object (.+) is empty$ |


### Arguments
The details of every argument of the step are listed below.

| Parameter    | Datatype          | Description          |
|:---          |:---               |:---                  |
|composite object | String | The name of the composite object |

### Examples


```gherkin
 Given the object Customer is empty
```

## Load data for composite object
Loads data into the tables that are configured for the composite object, either via step sentences or through the configuration.


### Sentences
| Type          | Language         | Sentence      |
|:---           |:---              |:---           |
| When | en | ^I insert the following data for object (.+):$ |


### Arguments
The details of every argument of the step are listed below.

| Parameter    | Datatype          | Description          |
|:---          |:---               |:---                  |
|composite object | String | The name of the composite object to insert the data for. |
|data to insert | DataTable | The data to load into the tables |

### Examples


```gherkin
 When I insert the following data for object Customer:
  | Id | Description    |
  | 1  | 'FirstRow'       |
  | 2  | 'SecondRow' |
```

## Insert data into table using template
Insert data into a table and overrule the template that is defined on the databaseconfig


### Sentences
| Type          | Language         | Sentence      |
|:---           |:---              |:---           |
| When | en | ^I insert the following data using template ([a-zA-Z0-9_@$#-]+) in ([a-zA-Z0-9_@$#-]+) table (.+):$ |


### Arguments
The details of every argument of the step are listed below.

| Parameter    | Datatype          | Description          |
|:---          |:---               |:---                  |
|object template | String | The name of the object template to apply |
|database config | String | The name of the database config |
|table name | String | The name of the table to insert data into |
|data to insert | DataTable | The data to load into the table. See [TestDataTable](Tables.md#testdatatable). |

### Examples


```gherkin
 When I insert the following data using template newdemo in demo table CUST_SAT:
  | Id | Description    |
  | 1  | 'FirstRow'       |
  | 2  | 'SecondRow' |
```

## Set key fields for composite object
Defines the key fields for a composite object, overruling the key fields that might be set in the configuration.


### Sentences
| Type          | Language         | Sentence      |
|:---           |:---              |:---           |
| When | en | ^I set (\(.*\)) as key for object (.+)$ |


### Arguments
The details of every argument of the step are listed below.

| Parameter    | Datatype          | Description          |
|:---          |:---               |:---                  |
|key fields | String |  |
|composite object | String |  |

### Examples


```gherkin
 When I set (CUST_ID, SYSTEM_CODE) as key for object Customer
```


