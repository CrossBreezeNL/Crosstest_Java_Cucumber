# Getting started

On this wiki you will find all needed documentation to get started with the Java/Cucumber version of CrossTest.

## How to get CrossTest
CrossTest is available as a [Maven package](https://mvnrepository.com/artifact/com.x-breeze.test/CrossTest).
If you are new to Maven, you can find information about it on the [Apache website](https://maven.apache.org/what-is-maven.html).
When you want to setup a new testing project using CrossTest, follow the instructions on the remainder of this page. 

Specifics about how to incorporate Crosstest in your environment may vary based on your setup. For example what IDE is being used, what type of CI/CD pipeline etc. But generally speaking it comes down to

 * including the required dependencies in a maven pom.xml file
 * use the Cucumber runner or write a java class to run the tests in a framework such as JUnit

Some IDEs such as Eclipse or IntelliJ come with a bundled Maven installation, but in other cases you might have to [install Maven](https://maven.apache.org/install.html) first.

## How to use CrossTest with the Cucumber runner
To run tests locally on your computer, using the Cucumber cli runner you need the following setup:
A folder containing:

 * The maven pom file (pom.xml)
 * A basis CrossTest configuration file (XTestConfig.xml)
 * A cmd or script file to invoke maven
 * A subfolder containing the feature testfiles

### Maven Pom file
The pom.xml should have the following contents:
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>CrossTestRunner</groupId>
  <artifactId>CrossTestRunner</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <properties>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
	<crosstest.version>1.0.22</crosstest.version>
  </properties>
  <dependencies>  		
  		<dependency>
			<groupId>com.x-breeze.test</groupId>
			<artifactId>CrossTest</artifactId>
			<version>${crosstest.version}</version>
		</dependency>		
		<!-- https://mvnrepository.com/artifact/com.microsoft.sqlserver/mssql-jdbc -->
		<!-- change dependency below to the correct JDBC driver for the specific RDBMS you are using -->
		<dependency>
		    <groupId>com.microsoft.sqlserver</groupId>
		    <artifactId>mssql-jdbc</artifactId>
		    <version>7.4.1.jre8</version>
		</dependency>
  </dependencies>

	<build>
     <plugins>
       <plugin>
         <groupId>org.apache.maven.plugins</groupId>
         <artifactId>maven-compiler-plugin</artifactId>
         <version>3.7.0</version>
         <configuration>
           <source>1.8</source>
           <target>1.8</target>
            <encoding>UTF-8</encoding>          
         </configuration>
       </plugin>                
	</plugins>
  	</build>
</project>
```

### Basic CrossTest configuration
The XTestConfig.xml should have the following content. Update the DatabaseServerConfig values so it corresponds to an existing database connection in your environment.
If you are using a different JDBC driver than SQL Server, also update the dependency in the pom.xml (previous section).
```xml
<?xml version="1.0" encoding="UTF-8"?>
<XTestConfig>	
	<DatabaseConfigs>
		<DatabaseConfig 
			name="demo" 
			databaseServerConfigName="demoserver"						
		/>			
    </DatabaseConfigs>
	<DatabaseServerConfigs>		
		<DatabaseServerConfig 
            name="demoserver" 
            JDBCUrl="jdbc:sqlserver://localhost;instanceName=localDev2017;databaseName=TestDB"				
            username="username" 
            password ="password"            
        />   
	</DatabaseServerConfigs>
</XTestConfig>
```

This is all configuration needed for running the first test. For an explanation of all configuration options see the [Configuration](./Configuration/index.md) section.

### Script file to run the tests
The script should contain the following statement. The example below will run all the feature files that are stored in the testfiles subfolder.
```
mvn exec:java -Dexec.classpathScope=test -Dexec.mainClass=io.cucumber.core.cli.Main -Dexec.args="./testfiles --glue com.xbreeze.xtest"
```

### Create and run your first test

Add a new feature file in the testfiles folder, be sure to have a file name that ends with the .feature extension and insert the following code:
```gherkin
Feature: a first testfeature
    Scenario: Running a crosstest scenario
        When I execute the following query on demo:
            """
            SELECT 'Hello World' AS FirstResult
            """

        Then I expect the following result:
            | FirstResult |
            | Hello World |
```

Now execute the script file from the previous section and the output should finish with something like
```
INFO: Loading ObjectFactory via service loader: io.cucumber.picocontainer.PicoFactory
..

1 Scenarios (1 passed)
2 Steps (2 passed)
0m1.620s
```

That's it! you have successfully run a first Cucumber/CrossTest feature.
For more information about how to use Crosstest look at the [Configuration](./Configuration/index.md) and [Step](./Steps/index.md) documentation.


## Bugs & issues
When you encounter an issue while using CrossTest please report it by sending an e-mail to [info@x-breeze.com](mailto:info@x-breeze.com?SUBJECT=CrossTest%20Cucumber%20-%20Bug%20report) with the subject 'CrossTest Cucumber - Bug report'.

Please provide the following information:

- Steps to reproduce
- Expected behaviour
- CrossTest version used
