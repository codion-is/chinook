#!/bin/bash

echo "🚀 Simple Chinook Lambda Deployment"
echo "=================================="

# Check if AWS CLI is configured
if ! aws sts get-caller-identity &> /dev/null; then
    echo "❌ AWS CLI not configured. Run 'aws configure' first"
    exit 1
fi

ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
REGION=$(aws configure get region)

echo "✓ Using AWS Account: $ACCOUNT_ID"
echo "✓ Region: $REGION"

# Step 1: Build the Lambda JAR
echo -e "\n📦 Building Lambda package..."
(cd .. && ./gradlew :chinook-lambda:shadowJar)
if [ ! -f "build/libs/chinook-lambda.jar" ]; then
    echo "❌ Build failed!"
    exit 1
fi
echo "✓ Build successful"

# Step 2: Create simple IAM role (if it doesn't exist)
echo -e "\n🔐 Setting up IAM role..."
if ! aws iam get-role --role-name chinook-simple-lambda-role &> /dev/null; then
    aws iam create-role \
        --role-name chinook-simple-lambda-role \
        --assume-role-policy-document '{
            "Version": "2012-10-17",
            "Statement": [{
                "Effect": "Allow",
                "Principal": {"Service": "lambda.amazonaws.com"},
                "Action": "sts:AssumeRole"
            }]
        }' > /dev/null
    
    # Attach basic execution policy
    aws iam attach-role-policy \
        --role-name chinook-simple-lambda-role \
        --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole
    
    echo "✓ IAM role created"
    sleep 10  # Give AWS time to propagate the role
else
    echo "✓ IAM role already exists"
fi

# Step 3: Create or update Lambda function
echo -e "\n☁️  Deploying Lambda function..."
if aws lambda get-function --function-name chinook-lambda &> /dev/null; then
    # Update existing function
    aws lambda update-function-code \
        --function-name chinook-lambda \
        --zip-file fileb://build/libs/chinook-lambda.jar > /dev/null
    echo "✓ Lambda function updated"
else
    # Create new function
    aws lambda create-function \
        --function-name chinook-lambda \
        --runtime java21 \
        --role arn:aws:iam::$ACCOUNT_ID:role/chinook-simple-lambda-role \
        --handler is.codion.framework.lambda.LambdaEntityHandler::handleRequest \
        --zip-file fileb://chinook-lambda/build/libs/chinook-lambda.jar \
        --timeout 30 \
        --memory-size 512 \
        --environment 'Variables={"JAVA_TOOL_OPTIONS":"-Dcodion.db.url=jdbc:h2:mem:h2db -Dcodion.db.initScripts=classpath:create_schema.sql -Dcodion.db.countQueries=true -Dcodion.server.connectionPoolUsers=scott:tiger -Dcodion.server.objectInputFilterFactory=is.codion.common.rmi.server.SerializationFilterFactory -Dcodion.server.serialization.filter.patternFile=classpath:serialization-filter-patterns.txt -Dcodion.server.idleConnectionTimeout=600000"}' > /dev/null
    echo "✓ Lambda function created"
fi

# Step 4: Create Function URL
echo -e "\n🌐 Creating public endpoint..."
if ! aws lambda get-function-url-config --function-name chinook-lambda &> /dev/null 2>&1; then
    aws lambda create-function-url-config \
        --function-name chinook-lambda \
        --auth-type NONE \
        --cors '{"AllowOrigins":["*"],"AllowMethods":["GET","POST","OPTIONS"],"AllowHeaders":["*"],"ExposeHeaders":["*"],"MaxAge":3600}' > /dev/null
else
    # Update CORS configuration on existing function URL
    aws lambda update-function-url-config \
        --function-name chinook-lambda \
        --cors '{"AllowOrigins":["*"],"AllowMethods":["*"],"AllowHeaders":["*"],"ExposeHeaders":["*"],"MaxAge":3600}' > /dev/null 2>&1 || true
fi

# Add permissions
aws lambda add-permission \
    --function-name chinook-lambda \
    --statement-id FunctionURLAllowPublicAccess \
    --action lambda:InvokeFunctionUrl \
    --principal "*" \
    --function-url-auth-type NONE &> /dev/null 2>&1

# Get the URL
FUNCTION_URL=$(aws lambda get-function-url-config \
    --function-name chinook-lambda \
    --query FunctionUrl \
    --output text)

echo "✓ Public endpoint created"

# Step 5: Test it
echo -e "\n🧪 Testing deployment..."
echo "Testing health endpoint..."
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" "${FUNCTION_URL}health")
HTTP_CODE=$(echo "$RESPONSE" | tail -1 | cut -d: -f2)
BODY=$(echo "$RESPONSE" | head -n -1)
if [ "$HTTP_CODE" = "200" ]; then
    echo "$BODY" | jq . 2>/dev/null || echo "Response: $BODY"
else
    echo "HTTP $HTTP_CODE - Response might be binary/error"
fi

echo -e "\n✅ Deployment complete!"
echo "========================================"
echo "Your Chinook Lambda URL is:"
echo "$FUNCTION_URL"
echo ""
echo "To use with HttpEntityConnection:"
echo "  .hostname(\"$(echo $FUNCTION_URL | sed 's|https://||' | sed 's|/||')\")"
echo "  .https(true)"
echo "  .securePort(443)"
echo ""
echo "Note: Using in-memory H2 database (data won't persist between cold starts)"
echo "========================================"