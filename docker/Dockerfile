# Use a base image that includes Java
FROM openjdk:8-jdk

# Install required packages
RUN apt-get update && apt-get install -y curl wget gnupg2 ca-certificates lsb-release


# Add the PostgreSQL GPG key
RUN wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add -

# Add PostgreSQL's repository
RUN echo "deb http://apt.postgresql.org/pub/repos/apt/ `lsb_release -cs`-pgdg main" > /etc/apt/sources.list.d/pgdg.list

# Install PostgreSQL 12
RUN apt-get update && apt-get install -y postgresql-12 postgresql-client-12

# Set PostgreSQL environment variables
ENV POSTGRES_USER zephyr_admin
ENV POSTGRES_DB zephyr_db
ENV POSTGRES_PASSWORD zephyr_pwd
ENV POSTGRES_HOST_AUTH_METHOD trust

# Add initialization scripts
COPY init.sql /docker-entrypoint-initdb.d/

# Install Tomcat
ENV TOMCAT_VERSION 8.5.76
ENV CATALINA_HOME /opt/tomcat
RUN mkdir -p "$CATALINA_HOME"
WORKDIR $CATALINA_HOME
RUN curl -O https://archive.apache.org/dist/tomcat/tomcat-8/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz \
    && tar -xvf apache-tomcat-${TOMCAT_VERSION}.tar.gz --strip-components=1 \
    && rm apache-tomcat-${TOMCAT_VERSION}.tar.gz*

# Copy war file to Tomcat webapps
COPY shopping_cart_demo.war /opt/tomcat/webapps/

# Expose ports
EXPOSE 8080

# Add a script to start Tomcat and PostgreSQL
COPY start.sh /start.sh
RUN chmod +x /start.sh

CMD ["/start.sh"]
