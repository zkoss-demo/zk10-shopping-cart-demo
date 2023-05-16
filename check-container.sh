aws lightsail get-container-services  \
    --region eu-west-3                \
    --service-name shoppingcart \
    --query "containerServices[].state"