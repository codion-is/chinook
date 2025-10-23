#!/bin/bash

# Build the jlink image and Docker container
echo "Building chinook-server jlink image..."
(cd .. && ./gradlew :chinook-server:jlink)

echo "Preparing jlink image for Docker..."
(cd .. && ./gradlew :chinook-server-docker:prepareJlinkImage)

echo "Building Docker image..."
(cd .. && ./gradlew :chinook-server-docker:dockerBuild)

echo "Docker image built successfully!"
echo "Run with: docker compose up -d"
echo "Or use: ./src/docker/run.sh"