#!/bin/bash

./gradlew :clean
./gradlew :bootJar
docker-compose up