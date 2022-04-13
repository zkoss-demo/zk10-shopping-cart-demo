# ZK 10 shopping cart demo

## Prerequisites
### Install Docker and Docker Compose
* [Docker installation guide](https://docs.docker.com/desktop/)
* [Docker Compose installation guide](https://docs.docker.com/compose/install/)

## How to run
### Run War
Run with `./gradlew war` in terminal

Note: make sure ./build/libs/shopping_cart_demo.war exist.

### Run docker containers for development
Run with ` cd ./docker` in terminal
* `$ docker-compose up -d`: create and start the containers in background
* `$ docker-compose down -v`: stop and remove the containers with all its volumes

After docker compose start, application can be accessed in http://localhost/shoppingCart

## Licence
Demo code: MIT
* [ZK Framework](https://github.com/zkoss/zk): [LGPL](http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html)
