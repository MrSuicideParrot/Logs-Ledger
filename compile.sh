#!/bin/sh

mvn clean package
docker build . -t log-node