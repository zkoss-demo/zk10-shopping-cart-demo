aws lightsail push-container-image    \
    --region eu-west-3                \
    --service-name shoppingcart \
    --label latest                    \
    --image shoppingcart:latest
# deploy
aws lightsail create-container-service-deployment \
    --region eu-west-3                            \
    --cli-input-json file://docker/lc.json