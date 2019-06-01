#!/bin/sh

mvn clean package
docker build . -f Dockerfile.node -t log-node
docker build . -f Dockerfile.tracker -t log-tracker