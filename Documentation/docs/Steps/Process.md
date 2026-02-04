# Process
This page describes the Process steps.

## Run templated process
Execute an (ETL) process using the engine configured in a CrossTest process server configuration


### Sentences
| Type          | Language         | Sentence      |
|:---           |:---              |:---           |
| When | en | ^I run the ([a-zA-Z0-9_@$#]+) process ([a-zA-Z0-9_@$#.]+)$ |


### Arguments
The details of every argument of the step are listed below.

| Parameter    | Datatype          | Description          |
|:---          |:---               |:---                  |
|process config | String | Name of the process config |
|process name | String | Name of the process |

### Examples


```gherkin
 When I run the demo process load_Customer
```

## Execute command
Execute a command using commandline.          On windows it will run using cmd, while on other OS types it will use bash.


### Sentences
| Type          | Language         | Sentence      |
|:---           |:---              |:---           |
| When | en | I execute the following command |
| When | nl | ik het volgende commando uitvoer |


### Arguments
The details of every argument of the step are listed below.

| Parameter    | Datatype          | Description          |
|:---          |:---               |:---                  |
|command text | String | Command to be executed.                It can be written as a multiline and multi-statement command, and will be executed at once. |

### Examples


```gherkin
 When I execute the following command
```


```gherkin
 Wanneer ik het volgende commando uitvoer
```


