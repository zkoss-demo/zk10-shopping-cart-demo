# push local image to lightsail container
aws lightsail push-container-image    \
    --region eu-west-3                \
    --service-name shoppingcart \
    --label latest                    \
    --image shoppingcart:latest