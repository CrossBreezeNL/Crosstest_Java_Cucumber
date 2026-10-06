#!/usr/bin/env bash
# Runs the example feature against the PostgreSQL database from the dev container.
# XTestConfig.xml is read from the working directory, so run from this folder.
cd "$(dirname "$0")"
mvn -f ../pom.xml exec:java -Dexec.classpathScope=test -Dexec.mainClass=io.cucumber.core.cli.Main -Dexec.args="./testfiles --glue com.xbreeze.xtest"
