echo 'Starting deployment...'

# 1) setting up the environment
aws ecr get-login-password --region eu-west-2 | docker login --username AWS --password-stdin 746669226532.dkr.ecr.eu-west-2.amazonaws.com

# 2) creating and uploading image
docker build --platform linux/amd64 -t 746669226532.dkr.ecr.eu-west-2.amazonaws.com/elephant:latest /Users/michaelartemyev/Desktop/ClojureDepsProject/src/UI
docker push 746669226532.dkr.ecr.eu-west-2.amazonaws.com/elephant:latest

# 3) cleanup
docker image rm 746669226532.dkr.ecr.eu-west-2.amazonaws.com/elephant:latest
