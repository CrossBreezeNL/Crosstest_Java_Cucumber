# Release notes

Here you can find the release notes of all Java CrossTest releases up till the release this documentation applies to.

[//]: # (Use the following example to create the release notes for a new release.)
[//]: # ()
[//]: # (## Version X.X <sup>[docs](../X.X/)</sup>)
[//]: # ()
[//]: # (- [ ] x.x.x <sup>xx-xx-xxxx</sup>)
[//]: # (>)
[//]: # (> !!! success "New features")
[//]: # (>     * [ ] Template)
[//]: # (>         * [ ] XML Template)
[//]: # (>             - [X] Some new feature...)
[//]: # (> !!! "Enhanced features")
[//]: # (>     * [ ] Model)
[//]: # (>         - [X] Some enhanced feature...)
[//]: # (> !!! warning "Bug fixes")
[//]: # (>     * [ ] Binding)
[//]: # (>         * [X] Some issue...)

## Version 1.0
- [ ] 1.0.22 <sup>12-01-2026</sup>
> New release
> !!! success "New features"    
>     * [ ] Config
>         - [X] Added CommandLineConfig support with tool, flags, timeout and working dir parameters
>         - [X] Support for default parameters and dot notation array definitions for commandline processes
>         - [X] Support for parameter and array formatting options for commandline processes
>     * [ ] Process execution
>         - [X] Execution of commandline commands
>         - [X] Execution of (templated) commandline processes
>     * [ ] Result
>         - [X] Output verification of commandline process (must contain, must be)

- [ ] 1.0.21 <sup>18-11-2022</sup>
> New release
> !!! warning "Bug fixes"
>     * [ ] Process execution 
>         - [X] Support security domain for PowerCenter login
>         - [X] Support having a process config without prefix
>         - [X] Apply prefix properly on worklet/session execution
>         - [X] Check on workflow completion status and not only on thrown errors
>     * [ ] Database
>         - [X] Support float and real datatypes
>         - [X] Support empty column name in expected and actual result

- [ ] 1.0.20 <sup>21-10-2022</sup>
> New release
> !!! warning "Bug fixes"
>     * [ ] General 
>         - [X] Updated Cucumber dependencies to latest version (7.8.1)
>         - [X] Updated JUnit dependencies to latest version (5.9.1)
>         - [X] Updated commons dependencies to latest versions.

- [ ] 1.0.19 <sup>20-6-2022</sup>
> New release
> !!! warning "Bug fixes"
>     * [ ] General 
>         - [X] Updated Cucumber dependencies to a single version (4.8.1)

- [ ] 1.0.18 <sup>16-6-2022</sup>
> New release
> !!! warning "Bug fixes"
>     * [ ] Process execution 
>         - [X] Fix issue that caused PowerCenter executor to use too many connections and leave them open.

- [ ] 1.0.17 <sup>1-12-2021</sup>
> New release
> !!! success "New features"         
>     * [ ] Database
>         - [X] Added @Transactional hook for transactional tests.
>         - [X] Added option to enable transactions for all tests on a certain database server configuration.
>         - [X] Added option to specify catalog (next to schema) on database configuration.
> !!! warning "Bug fixes"
>     * [ ] Database 
>         - [X] Added missing after hook for closing all database connections (and performing rollback if needed).

- [ ] 1.0.16 <sup>3-11-2021</sup>
> New release
> !!! success "New features"         
>     * [ ] Database
>         - [X] Enable running tests in a transaction.
> !!! success "Enhanced features"
>     * [ ] Database 
>         - [X] Reduced no of open connections used in database test steps

- [ ] 1.0.15 <sup>13-8-2021</sup>
> New release
> !!! success "New features"         
>     * [ ] Composite objects
>         - [X] Explicitly define key fields for the composite object, either in the config or in a test scenario with a new step definition.
>     * [ ] Database
>         - [X] Set a constant in the config that will be used as empty string value to explicitly set and test for empty strings. Without this setting empty string and NULL is treated as being equal.
> !!! warning "Bug fixes"
>     * [ ] Database steps /expected results
>         - [X] Fixed rounding issue in expected results that resulted in false positives

- [ ] 1.0.14 <sup>31-3-2021</sup>
> New release
> !!! success "Enhanced features"         
>     * [ ] Config
>         * [ ] Includes              
>              - [X] Add public method to XTestConfig class to get the XTestConfig xml file with all includes resolved, in case additional preprocessing is needed.

- [ ] 1.0.13 <sup>9-3-2021</sup>
> New release
> !!! success "Enhanced features"         
>     * [ ] Process
>         * [ ] PowerCenter engine
>              - [X] Add or modify parameters within a test scenario using XTest variables
> !!! warning "Bug fixes"
>     * [ ] Database steps /expected results
>         - [X] Fixed comparison issue with decimal datatypes that have trailing zeroes.
>         - [X] Fixed comparison issue with adjecant empty/null field differences not being detected.   

- [ ] 1.0.12 <sup>22-2-2021</sup>
> New release
> !!! success "Enhanced features"
>         * [ ] General
>             - [X] Use multi-record variables in tables and statements
>     * [ ] Process
>         * [ ] PowerCenter engine
>              - [X] Add parameters to a process config
>              - [X] Add or modify parameters within a test scenario


- [ ] 1.0.11 <sup>11-2-2021</sup>
> New release
> !!! success "New features"
>     * [ ] Process
>         * [ ] PowerCenter engine
>              - [X] Run individual tasks in a workflow
> !!! warning "Bug fixes"
>     * [ ] Database steps
>         - [X] Fixed issue with decimal datatypes, large numbers where rounded. Now using a bigger Java datatype to prevent this from happening.

- [ ] 1.0.10 <sup>5-12-2020</sup>
> New release
> !!! success "New features"
>     * [ ] Configuration
>         * [ ] ObjectTemplate
>              - [X] Option to perform auto increment inserts on fields by setting seed and increment for Attributes.
>              - [X] Override or add Attribute defaults for a object template in a specific scenario using a Gherkin sentence.
>     * [ ] Database
>         - [X] Apply a specific object template in a scenario when inserting data, overriding the object template setting of the detabase config.

- [ ] 1.0.9 <sup>4-11-2020</sup>
> New release
> !!! success "Enhanced features"
>     * [ ] Configuration
>         - [X] Configuration file is no longer statically loaded on class load but loads as instance singleton. This mainly affects setups where CrossTest is used within a Java server based test framework such as FitNesse.

- [ ] 1.0.8 <sup>14-10-2020</sup>
> New release
> !!! warning "Bug fixes"
>     * [ ] Result
>         - [X] Fixed issue when comparing to an empty result set

- [ ] 1.0.7 <sup>08-09-2020</sup>
> New release
> !!! success "Enhanced features"
>     * [ ] Database and Process server configuration
>         - [X] Added support for configuring a custom credential provider

- [ ] 1.0.6 <sup>26-08-2020</sup>
> New release
> !!! success "Enhanced features"
>     * [ ] Database server configuration
>         - [X] Added support for configuring custom data types.
>     * [ ] Object template configuration
>         - [X] Implemented support for configuring prefix and suffix on table names
>     * [ ] Database steps
>         - [X] Implemented support for Teradata custom data type Period (Date) and Period (Timestamp)
> !!! warning "Bug fixes"
>     * [ ] Database steps
>         - [X] Fixed issue with timestamp datatype that in case of a time of 00:00:00 in actual or expected result the time was incorrectly interpreted as 12:00pm
>     * [ ] Result
>         - [X] Trimmed result in case of actual/expected differences on char fields to prevent padding to max length in output

- [ ] 1.0.5 <sup>28-07-2020</sup>
> New release
> !!! warning "Bug fixes"
>     * [ ] Result
>         - [X] Improved output in case where expected result has records that are duplicate in the actal result

- [ ] 1.0.4 <sup>02-07-2020</sup>
> New release
> !!! success "Enhanced features"
>     * [ ] Database server configuration
>         - [X] set SQL command template on server connection to set current database, this since not all JDBC drivers and RDBMS platforms implement this the same
>     * [ ] Database steps
>         - [X] Trim char fields to be able to compare result without trailing spaces
>         - [X] Output SQL exception on data insert e.g. PK violations

- [ ] 1.0.3 <sup>17-06-2020</sup>
> New release
> !!! success "Enhanced features"
>     * [ ] Database steps
>         - [X] Support for boolean datatype on SQL Server
>         - [X] Support for bigint datatype
> !!! warning "Bug fixes"
>     * [ ] Result steps
>         - [X] Only evaluate columns that are in expected results when comparing against a SQL query output

- [ ] 1.0.2 <sup>06-05-2020</sup>
> New release
> !!! success "Enhanced features"
>     * [ ] Database steps
>         - [X] Support for boolean datatype

- [ ] 1.0.1 <sup>05-05-2020</sup>
> New release
> !!! success "New features"
>     * [ ] Configuration
>         * [ ] Database
>             - [X] Configure quote object names so table and column names are enclosed in double quotes to support special characters.
> !!! warning "Bug fixes"
>     * [ ] Configuration
>         * [ ] Database server configuration
>              - [X] Username and password are no longer required, since not needed when using integrated security

- [ ] 1.0.0 <sup>31-03-2020</sup>
> New release
> !!! success "New features"
>     * [ ] Configuration
>         * [ ] General
>             - [X] Set debug from config.
>         * [ ] Database
>             - [X] Configure command timeout (seconds) to prevent queries from running forever.
> !!! warning "Bug fixes"
>     * [ ] Test execution
>         * [ ] Result
>              - [X] Fail when the expected results contain a column that is not found in the actual result.

## Version 0.9
- [ ] 0.9.9 <sup>09-03-2020</sup>
> New release
> !!! success "New features"
>     * [ ] Test execution
>         * [ ] General
>             - [X] Use variables in tables and statements

