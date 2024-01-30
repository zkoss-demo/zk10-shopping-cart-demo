# ZK 10 shopping cart demo
An example application that demonstrates basic stateless component usages. See [Building Stateless UI](https://www.zkoss.org/wiki/ZK_Developer%27s_Reference/Stateless_Components/Building_Stateless_UI).

# Prerequisites to run with Docker
### Install Docker and Docker Compose
* [Docker installation guide](https://docs.docker.com/desktop/)
* [Docker Compose installation guide](https://docs.docker.com/compose/install/)

# How to run
## Run locally without a database
1. set `debug=true` in [config.properties](src/main/resources/config.properties)
2. run with `./gradlew appRun`

visit http://localhost:8080/zk10-shopping-cart-demo/shoppingCart

## Run War
Run with `./gradlew war` in terminal

Note: make sure ./build/libs/shopping_cart_demo.war exist.

## Run docker containers for development
Run with ` cd ./docker` in terminal
* `$ docker-compose up -d`: create and start the containers in background
* `$ docker-compose down -v`: stop and remove the containers with all its volumes

After docker compose start, application can be accessed in http://localhost/shoppingCart


# Publish as Demo
1. build with `gradle clean war`
2. publish with [update_war](http://jenkins2/jenkins2/job/update_war/)
# Licence
* Demo code: MIT
* [ZK Framework](https://github.com/zkoss/zk): [LGPL](http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html)
