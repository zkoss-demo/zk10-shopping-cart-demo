FROM tomcat:8.5.76 as tomcat
COPY shopping_cart_demo.war /usr/local/tomcat/webapps/
EXPOSE 8080
RUN apt-get update && apt-get install -y postgresql-client
# add initialization scripts
COPY init.sql /docker-entrypoint-initdb.d
ENV POSTGRES_USER zephyr_admin
ENV POSTGRES_DB zephyr_db
ENV POSTGRES_PASSWORD zephyr_pwd
ENV POSTGRES_HOST_AUTH_METHOD trust
EXPOSE 5432