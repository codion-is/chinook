#!/bin/bash

# Build the JAR and Docker container
echo "Building chinook-server-docker shadow JAR..."
(cd .. && ./gradlew :chinook-server-docker:shadowJar)

echo "Building Docker image..."
(cd .. && ./gradlew :chinook-server-docker:dockerBuild)

echo "Docker image built successfully!"
echo "Run with: docker-compose up -d"
echo "Or use: ./src/docker/run.sh"