cp build/libs/shopping_cart_demo.war docker
cd docker
# because lightsail is amd64 architecture
docker buildx build --platform=linux/amd64 --no-cache -t shoppingcart .