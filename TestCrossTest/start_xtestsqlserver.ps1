#installing SQL Server 2019 docker
#https://docs.microsoft.com/en-us/sql/linux/quickstart-install-connect-docker?view=sql-server-ver15&pivots=cs1-bash
# sudo docker pull mcr.microsoft.com/mssql/server:2019-latest

#Run the container
docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=Test1234" -p 1533:1433 --name xtestsqlserver -h xtestsqlserver -v ${PWD}:/scripts -d mcr.microsoft.com/mssql/server:2019-latest

#wait for docker to be running
sleep 15

# Install mssql-tools inside the container if not present (run as root)
docker exec -u 0 xtestsqlserver bash -c "if [ ! -f /opt/mssql-tools/bin/sqlcmd ]; then \
  apt-get update && \
  ACCEPT_EULA=Y apt-get install -y unixodbc-dev curl gnupg && \
  curl -sSL https://packages.microsoft.com/keys/microsoft.asc | apt-key add - && \
  curl -sSL https://packages.microsoft.com/config/ubuntu/20.04/prod.list > /etc/apt/sources.list.d/mssql-release.list && \
  apt-get update && \
  ACCEPT_EULA=Y apt-get install -y mssql-tools; \
fi"

#Create database TestDB
docker exec xtestsqlserver /opt/mssql-tools/bin/sqlcmd -S localhost -U SA -P Test1234 -i /scripts/testdb.sql
