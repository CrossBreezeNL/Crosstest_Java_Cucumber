#!/usr/bin/env bash
# Creates the CrossTest test databases (TestCrossTest/testdb.sql) on the dev container SQL Server.
# Runs on every container start, but only executes the script when TestDB does not exist yet.
set -euo pipefail

SQLCMD=(/opt/mssql-tools18/bin/sqlcmd -C -b -S "localhost,${MSSQL_TCP_PORT}" -U sa -P "${MSSQL_SA_PASSWORD}")

exists=$("${SQLCMD[@]}" -h -1 -W -Q "SET NOCOUNT ON; SELECT COUNT(*) FROM sys.databases WHERE name = 'TestDB';")
if [ "$(echo "${exists}" | tr -d '[:space:]')" = "1" ]; then
	echo "TestDB already exists, skipping initialisation."
	exit 0
fi

echo "Creating test databases from testdb.sql..."
"${SQLCMD[@]}" -i /scripts/testdb.sql
echo "Test databases created."
