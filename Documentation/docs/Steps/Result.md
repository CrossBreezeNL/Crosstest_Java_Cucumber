# Result
This page describes the Result steps.

## Compare expected and actual result
Compare expected and actual result


### Sentences
| Type          | Language         | Sentence      |
|:---           |:---              |:---           |
| Then | en | I expect the following results: |
| Then | nl | verwacht ik het volgende resultaat: |
| Then | en | I expect the following result: |


### Arguments
The details of every argument of the step are listed below.

| Parameter    | Datatype          | Description          |
|:---          |:---               |:---                  |
|expected results | DataTable | The table with the expected results. See [TestDataTable](../Tables#testdatatable). |

### Examples


```gherkin
 Then I expect the following results:
  | Id | Description    |
  | 1  | 'FirstRow'       |
  | 2  | 'SecondRow' |
```


```gherkin
 Dan verwacht ik het volgende resultaat:         
  | Id | Description    |
  | 1  | 'FirstRow'       |
  | 2  | 'SecondRow' |
```


```gherkin
 Then I expect the following result:
  | Id | Description    |
  | 1  | 'FirstRow'       |
  | 2  | 'SecondRow' |
```

## Commandline output
After executing a command, the output can be verified using assertion steps. These steps compare the output of the last executed command against an expected value provided as a docstring.

The command output is also logged at INFO level, so it is visible when `debug="true"` is set in the XTestConfig.

**Note:** Both the actual output and expected text are normalized before comparison: line endings are unified (`\r\n` → `\n`) and trailing whitespace is stripped from each line. This ensures assertions work consistently across Windows and non-Windows platforms.

### Commandline output should contain
Assert that the output of the last executed command contains the expected text (substring match).

#### Sentences
| Type          | Language         | Sentence      |
|:---           |:---              |:---           |
| Then | en | `^the commandline output should contain:$` |
| Then | nl | `^de commandline uitvoer het volgende moet bevatten:$` |

#### Arguments
| Parameter    | Datatype          | Description          |
|:---          |:---               |:---                  |
| expected text | String | The text that the command output should contain (docstring). |

#### Examples

```gherkin
When I execute the following command
"""
echo Hello World
"""
Then the commandline output should contain:
"""
Hello World
"""
```

```gherkin
Wanneer ik het volgende commando uitvoer
"""
echo Hallo Wereld
"""
Dan de commandline uitvoer het volgende moet bevatten:
"""
Hallo Wereld
"""
```

### Commandline output should be
Assert that the output of the last executed command matches the expected text exactly (after trimming leading/trailing whitespace on both sides).

#### Sentences
| Type          | Language         | Sentence      |
|:---           |:---              |:---           |
| Then | en | `^the commandline output should be:$` |
| Then | nl | `^de commandline uitvoer als volgt moet zijn:$` |

#### Arguments
| Parameter    | Datatype          | Description          |
|:---          |:---               |:---                  |
| expected text | String | The exact expected command output (docstring). Leading and trailing whitespace is trimmed before comparison. |

#### Examples

```gherkin
When I execute the following command
"""
echo Hello World
"""
Then the commandline output should be:
"""
Hello World
"""
```

```gherkin
Wanneer ik het volgende commando uitvoer
"""
echo Hallo Wereld
"""
Dan de commandline uitvoer als volgt moet zijn:
"""
Hallo Wereld
"""
```

## Store contents of field in variable
Fetch the value of a column in the result and store it in a variable. The result should only contain one record. Variables can be used in data tables using the name prefixed with XTestVariabes.


### Sentences
| Type          | Language         | Sentence      |
|:---           |:---              |:---           |
| Given | en | ^I store the contents of the field (.+) into variable ([a-zA-Z0-9_@$#]+)$ |


### Arguments
The details of every argument of the step are listed below.

| Parameter    | Datatype          | Description          |
|:---          |:---               |:---                  |
|field name | String | Name of the field that holds the value to store |
|variable name | String | Name of the variable used to store the value |

### Examples


```gherkin
 Given I execute the following query on source:
    """
     SELECT TOP 1 CUST_ID FROM CUST_HUB ORDER BY CUST_ID 
    """ 
   And I store the contents of the field Cust_ID into variable CustomerID
   And I insert the following data in demo table CUST_SAT:
  | Id                         | Description |
  | XTestVariables.CustomerID  | &apos;FirstRow&apos;  |
 
 Given I execute the following query on source:
    """
     SELECT CUST_ID FROM CUST_HUB ORDER BY CUST_ID 
    """ 
   And I store the contents of the field Cust_ID into variable CustomerID
   And I insert the following data in demo table CUST_SAT:
  | Id                            | Description |
  | XTestVariables.CustomerID[0]  | &apos;FirstRow&apos;  |
  | XTestVariables.CustomerID[1]  | &apos;SecondRow&apos;  |
```


