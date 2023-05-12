#!/bin/bash
# Start PostgreSQL
service postgresql start

# allow trusted hosts, https://www.postgresql.org/docs/current/auth-pg-hba-conf.html
cp /pg_hba.conf /etc/postgresql/12/main/pg_hba.conf
chown postgres:postgres /etc/postgresql/12/main/pg_hba.conf
chmod 600 /etc/postgresql/12/main/pg_hba.conf
service postgresql restart

# Initialize DB
su - postgres -c "psql -f /docker-entrypoint-initdb.d/init.sql"

# Start Tomcat
/opt/tomcat/bin/catalina.sh run
