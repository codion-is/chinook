#!/bin/bash

# Deploy Lambda with RDS PostgreSQL

# 1. Create RDS instance (smallest possible for testing)
echo "Creating RDS PostgreSQL instance..."
aws rds create-db-instance \
  --db-instance-identifier chinook-db \
  --db-instance-class db.t3.micro \
  --engine postgres \
  --engine-version 15.4 \
  --master-username chinook_admin \
  --master-user-password ChinookPass123! \
  --allocated-storage 20 \
  --no-publicly-accessible \
  --backup-retention-period 0 \
  --no-multi-az

echo "Waiting for RDS instance to be available (this takes 5-10 minutes)..."
aws rds wait db-instance-available --db-instance-identifier chinook-db

# Get the endpoint
ENDPOINT=$(aws rds describe-db-instances \
  --db-instance-identifier chinook-db \
  --query 'DBInstances[0].Endpoint.Address' \
  --output text)

echo "RDS instance created at: $ENDPOINT"

# 2. Build Lambda package
echo "Building Lambda deployment package..."
cd ..
./gradlew :chinook-lambda:shadowJar
cd deployment

# 3. Create Lambda function
echo "Creating Lambda function..."
aws lambda create-function \
  --function-name chinook-entity-server \
  --runtime java21 \
  --role arn:aws:iam::$(aws sts get-caller-identity --query Account --output text):role/chinook-lambda-role \
  --handler is.codion.framework.lambda.LambdaEntityHandler::handleRequest \
  --zip-file fileb://../build/libs/chinook-lambda.jar \
  --timeout 30 \
  --memory-size 1024 \
  --environment Variables="{\
    JAVA_TOOL_OPTIONS=\"-Dcodion.db.url=jdbc:postgresql://$ENDPOINT:5432/postgres?user=chinook_admin&password=ChinookPass123! -Dcodion.db.countQueries=true -Dcodion.server.connectionPoolUsers=scott:tiger -Dcodion.server.objectInputFilterFactoryClassName=is.codion.common.rmi.server.SerializationFilterFactory -Dcodion.server.serialization.filter.patternFile=classpath:serialization-filter-patterns.txt -Dcodion.server.idleConnectionTimeout=10\"\
  }"

# 4. Create Lambda Function URL
echo "Creating Lambda Function URL..."
aws lambda create-function-url-config \
  --function-name chinook-entity-server \
  --auth-type NONE \
  --cors '{
    "AllowOrigins": ["*"],
    "AllowMethods": ["GET", "POST", "OPTIONS"],
    "AllowHeaders": ["*"],
    "ExposeHeaders": ["*"],
    "MaxAge": 3600
  }'

# Grant public access permission
aws lambda add-permission \
  --function-name chinook-entity-server \
  --statement-id FunctionURLAllowPublicAccess \
  --action lambda:InvokeFunctionUrl \
  --principal "*" \
  --function-url-auth-type NONE

# Get the URL
FUNCTION_URL=$(aws lambda get-function-url-config \
  --function-name chinook-entity-server \
  --query FunctionUrl \
  --output text)

echo "Lambda Function URL created!"
echo "Your endpoint is:"
echo "$FUNCTION_URL"