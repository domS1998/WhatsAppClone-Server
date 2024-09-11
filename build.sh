#!/bin/bash

#mvn clean
#mvn compile
#mvn package # compile and build jar
docker compose rm -stop
docker image rm -f  webservice-1.0 # delete old image with old jar
docker image build -t webservice-1.0 . # build image with new jar
docker-compose up # build and start new containers with new image





