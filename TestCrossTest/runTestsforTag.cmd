set TAGS=%1
SET TAGS=%TAGS:"=%
mvn clean test ^
  -Dcucumber.filter.tags="(%TAGS%)"
REM For some reason the command below is not executed when REM is removed.
REM start chrome ./target/cucumber-report.html