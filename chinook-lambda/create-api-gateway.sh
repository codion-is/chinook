#!/bin/bash

# Create API Gateway v1 (REST API) for Lambda with binary support
echo "Creating API Gateway v1 (REST API) with binary media type support..."

ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
REGION=$(aws configure get region)

# Create REST API Gateway with binary support
API_ID=$(aws apigateway create-rest-api \
  --name chinook-lambda-api \
  --binary-media-types "application/octet-stream" "*/*" \
  --query 'id' --output text)

echo "✓ Created API Gateway: $API_ID"

# Get root resource
ROOT_ID=$(aws apigateway get-resources \
  --rest-api-id $API_ID \
  --query 'items[0].id' --output text)

# Create proxy resource for all paths
RESOURCE_ID=$(aws apigateway create-resource \
  --rest-api-id $API_ID \
  --parent-id $ROOT_ID \
  --path-part '{proxy+}' \
  --query 'id' --output text)

echo "✓ Created proxy resource: $RESOURCE_ID"

# Create ANY method
aws apigateway put-method \
  --rest-api-id $API_ID \
  --resource-id $RESOURCE_ID \
  --http-method ANY \
  --authorization-type NONE > /dev/null

echo "✓ Created ANY method"

# Configure Lambda integration with binary content handling
aws apigateway put-integration \
  --rest-api-id $API_ID \
  --resource-id $RESOURCE_ID \
  --http-method ANY \
  --type AWS_PROXY \
  --integration-http-method POST \
  --uri "arn:aws:apigateway:$REGION:lambda:path/2015-03-31/functions/arn:aws:lambda:$REGION:$ACCOUNT_ID:function:chinook-lambda/invocations" \
  --content-handling CONVERT_TO_BINARY > /dev/null

echo "✓ Configured Lambda integration with binary content handling"

# Deploy API
aws apigateway create-deployment \
  --rest-api-id $API_ID \
  --stage-name prod > /dev/null

echo "✓ Deployed API to prod stage"

# Grant permission for API Gateway to invoke Lambda
aws lambda add-permission \
  --function-name chinook-lambda \
  --statement-id api-gateway-invoke \
  --action lambda:InvokeFunction \
  --principal apigateway.amazonaws.com \
  --source-arn "arn:aws:execute-api:$REGION:$ACCOUNT_ID:$API_ID/*/*" 2>/dev/null || true

echo "✓ Granted API Gateway permission to invoke Lambda"

echo ""
echo "🎉 API Gateway created successfully!"
echo "Your API endpoint is:"
echo "https://$API_ID.execute-api.$REGION.amazonaws.com/prod/"
echo ""
echo "Test endpoints:"
echo "  Health: https://$API_ID.execute-api.$REGION.amazonaws.com/prod/health"
echo "  Entities: https://$API_ID.execute-api.$REGION.amazonaws.com/prod/entities"