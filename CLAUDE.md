# CrossTest Java Cucumber

## Project Overview

CrossTest is an open-source (GPL v3) test framework for data platform solutions built on Java and Cucumber. It allows writing Gherkin-based test scenarios that interact with databases, run ETL processes, and compare expected vs actual data results. Developed by CrossBreeze (x-breeze.com).

Website: http://x-test.nl

## Tech Stack

- **Java 8** (source/target 1.8)
- **Cucumber 7.8.1** with JUnit 5 (5.9.1)
- **Maven** multi-module build
- **CI/CD**: Azure Pipelines
- **Deployment**: Sonatype / Maven Central

## Repository Structure

```
├── CrossTest/                  Core framework library (artifactId: CrossTestCore)
│   └── com.xbreeze.xtest.*     Config, database helpers, modules, process execution
├── CrossTestSteps/             Cucumber step definitions (artifactId: CrossTest)
│   └── com.xbreeze.xtest.steps.*  Step classes for config, data, process, result
├── CucumberDataComparison/     Data comparison utility
├── CucumberRunner/             Example runner with sample XTestConfig.xml and test.feature
├── Execution/
│   └── InformaticaPowerCenter/ Informatica PowerCenter SOAP integration (mostly WSDL-generated)
├── TeradataCustomDataTypes/    Teradata JDBC custom data type support
├── TestCrossTest/              Integration tests (feature files + test config)
├── Documentation/              MkDocs-based documentation site
├── pom.xml                     Parent/aggregator POM
├── azure-pipelines.yml         CI pipeline (SpotBugs check + package)
├── DeployToMaven.cmd           Windows deploy script
└── MvnDeployOnLinux.sh         Linux deploy script
```

## Module Dependency Graph

```
TestCrossTest  -->  CrossTest (Steps)  -->  CrossTestCore  -->  CucumberDataComparison
               -->  CrossTestCore
               -->  CrossTestInformaticaPowerCenter  -->  CrossTestCore
```

## Build & Run

```bash
# Build all modules from root
mvn clean install

# Run tests (requires database config in TestCrossTest/XTestServerConfig.xml)
cd TestCrossTest
mvn test

# Deploy to Maven Central (requires GPG + Sonatype credentials)
# Windows:
DeployToMaven.cmd
# Linux:
./MvnDeployOnLinux.sh
```

## Test Configuration

- **`TestCrossTest/XTestConfig.xml`** - Main config: composite objects, database configs, object templates, process configs, credential providers
- **`TestCrossTest/XTestServerConfig.xml`** - Server connection details (included via XInclude, not committed - environment specific)
- **`TestCrossTest/src/test/resources/junit-platform.properties`** - Cucumber/JUnit platform settings
- Feature files are in `TestCrossTest/src/test/resources/features/`

## Test Runner

Tests use JUnit 5 Platform Suite (`@Suite` + `@SelectClasspathResource("features")`). The Cucumber glue package is `com.xbreeze.xtest`. Test results output to `target/TestResults.xml` and HTML reports in `target/cucumber-html-reports/`.

## Key Packages

| Package | Location | Purpose |
|---------|----------|---------|
| `com.xbreeze.xtest.config` | CrossTest | XML config parsing (JAXB) for databases, processes, templates, credentials |
| `com.xbreeze.xtest.database.helpers` | CrossTest | JDBC connection management and data helpers |
| `com.xbreeze.xtest.process.execution` | CrossTest | ProcessExecutor interface + CommandLineProcessExecutor |
| `com.xbreeze.xtest.modules.data.database` | CrossTest | Database context, table operations, query execution |
| `com.xbreeze.xtest.modules.result` | CrossTest | Result comparison logic |
| `com.xbreeze.xtest.steps.*` | CrossTestSteps | All Cucumber step definitions (EN + NL) |

## Step Definitions

Step definitions support both English and Dutch (NL) Gherkin keywords. They are **generated from a PowerDesigner model** — the Java files in `CrossTestSteps` are generated code and should not be manually edited. The core helper classes in `CrossTest` (e.g., `Process_Helper`, `Result_Helper`) contain the actual logic and are manually maintained.

Step definitions cover:
- **Database context**: transactions, connections
- **Database tables**: insert, retrieve, empty, delete operations
- **Queries**: execute SQL statements
- **Object templates**: attribute configuration
- **Process config**: parameter configuration
- **Process execution**: run ETL processes, command-line execution, command-line with configurable arguments
- **Result comparison**: compare actual vs expected data tables, commandline output assertions

### Commandline execution

Commandline steps use `CommandLineProcessExecutor` (wraps Java `ProcessBuilder`) directly — no `ProcessServerConfig` binding is required.

#### Step sentences (EN)

| Step | Description |
|:--- |:--- |
| `I execute the following command` | Execute a raw command using the default shell |
| `I execute the following {config} command:` | Execute a raw command using a specific `CommandLineConfig` |
| `I execute the {process} commandline process using the following arguments:` | Assemble and execute from ProcessConfig + args table |
| `I execute the {process} commandline process with {config} using the following arguments:` | Same, with explicit CommandLineConfig override |
| `the commandline output must be:` | Assert exact match on command output |
| `the commandline output must contain:` | Assert substring match on command output |

#### Command assembly

The `ExecuteTemplatedCommandProcesWithParameters` method assembles commands from four segments: `{command} {starting_args} {feature_args} {ending_args}`.

Key design decisions:
- **`command`** should contain the tool and subcommand (e.g., `dbt run`, `helm upgrade --install my-release my-chart`). `starting_args` is for flags only (e.g., `--full-refresh`), not tool names.
- **`feature_args` takes precedence** — if `feature_args` is non-empty (from config or table), it is used as-is and individual args from the table are ignored. Setting `feature_args` to empty in the table clears the config default and allows individual arg construction.
- **Dot-notation grouping** — args like `vars.db`, `vars.ldts` are grouped by prefix and formatted using `group_format`, `group_entry_format`, and `group_entry_separator` config parameters.
- **Config-level defaults** — both dot-notation parameters and regular arguments in the ProcessConfig serve as defaults that feature-level entries can override or extend.
- **Arg formatting** — customizable via `arg_key_prefix` (default `--`), `arg_key_value_separator` (default space), and `arg_value_format` (default `{value}`).

#### CommandLineConfig

Optional XML config for non-default shells. Attributes: `tool`, `toolFlags`, `workingDirectory`. If no config is specified, the OS default is used (`cmd.exe /c` on Windows, `bash -c` on other).

```xml
<CommandLineConfig name="powershell" tool="powershell.exe" toolFlags="-Command"/>
<CommandLineConfig name="custom_workdir" workingDirectory="C:\temp"/>
```

#### Helper architecture

- **`Process_Helper`** — contains all commandline execution logic (`ExecuteCommand`, `ExecutedTemplatedCommand`, `ExecuteTemplatedCommandProcesWithParameters`, `ExecuteTemplatedCommandWithTemplatedProcessWithParameters`) and command output retrieval (`getLastCommandOutput`, `getLastAssembledCommand`, `getLastExecutionToolPrefix`).
- **`Result_Helper`** — extends `Database_Helper` for result comparison, and delegates to `Process_Helper` (injected via PicoContainer) for commandline output assertions (`CommandlineOutputMustBe`, `CommandlineOutputMustContain`). Output is normalized for cross-platform consistency (line endings, trailing whitespace).

### Test-only assertion steps

Assertion steps like "the assembled commandline should be" and "the execution tool prefix should be" belong in `TestCrossTest` (class `InternalProcessAssertionSteps`), not in `CrossTestSteps`, since they test internal assembly logic and are not for end users.

### Documentation

- `Documentation/docs/Steps/Process.md` — user-facing documentation for process/commandline steps
- `Documentation/docs/Steps/Result.md` — user-facing documentation for result/output assertion steps

## Coding Conventions

- Java 8 compatibility required (no newer Java features)
- Source encoding: UTF-8
- Package root: `com.xbreeze.xtest`
- Configuration is XML-based using JAXB unmarshalling
- Process executors implement the `ProcessExecutor` interface
- Step definition classes use Cucumber PicoContainer for dependency injection
- Step definition files in `CrossTestSteps` are **generated** — do not edit manually
- Current version: `1.0.22` (defined as `crosstest.version` property in pom.xml files)

## CI Pipeline (Azure Pipelines)

Two stages on `master`, `develop`, `features/*`, `hotfix/*` branches:
1. **Check** - SpotBugs static analysis
2. **Package** - Maven package

## Testing Notes

- PowerCenter tests require a server connection and will fail locally — skip with: `"-Dcucumber.features=classpath:features/Process/CommandAssembly_BasicArgs.feature,..."` targeting specific feature files
- Run specific tagged tests: `mvn test -f TestCrossTest\pom.xml -Dtest=TestCrossTest "-Dcucumber.filter.tags=@Debug"`
- Use `-f TestCrossTest\pom.xml` instead of `cd TestCrossTest` to avoid Windows path issues in bash
