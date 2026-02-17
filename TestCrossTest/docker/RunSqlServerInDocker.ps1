# NOTE: Save this script with Unix (LF) line endings for bash compatibility!

docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=Cr0sZ!Br33Ze" -e 'MSSQL_PID=Developer' -p 1433:1433 --name=MsSql2019 -d mcr.microsoft.com/mssql/server:2019-latest

# Wait for SQL Server to start
Write-Host "Waiting for SQL Server to start..."
Start-Sleep -Seconds 10

# Install mssql-tools inside the container if not present (run as root)
docker exec -u 0 MsSql2019 bash -c "if [ ! -f /opt/mssql-tools/bin/sqlcmd ]; then \
  apt-get update && \
  ACCEPT_EULA=Y apt-get install -y unixodbc-dev curl gnupg && \
  curl -sSL https://packages.microsoft.com/keys/microsoft.asc | apt-key add - && \
  curl -sSL https://packages.microsoft.com/config/ubuntu/20.04/prod.list > /etc/apt/sources.list.d/mssql-release.list && \
  apt-get update && \
  ACCEPT_EULA=Y apt-get install -y mssql-tools; \
fi"

# Run sqlcmd (interactive)
docker exec -it MsSql2019 /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P Cr0sZ!Br33Ze