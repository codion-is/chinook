#!/bin/bash

# Build the Lambda package first
echo "Building Lambda package..."
./gradlew :chinook-lambda:shadowJar

# Check if SAM CLI is installed
if ! command -v sam &> /dev/null; then
    echo "SAM CLI is not installed. Please install it first:"
    echo "https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html"
    exit 1
fi

# Start SAM local API using the template in src/main/config
echo "Starting SAM local API..."
echo "API will be available at http://localhost:3000"
echo "Press Ctrl+C to stop"
cd chinook-lambda
sam local start-api --template src/main/config/template.yaml --warm-containers EAGER