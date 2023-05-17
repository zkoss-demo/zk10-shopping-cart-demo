# check lc.json before deploying because image name changes for each push
aws lightsail create-container-service-deployment \
    --region eu-west-3                            \
    --cli-input-json file://docker/lc.json