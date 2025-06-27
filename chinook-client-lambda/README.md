# Chinook Client Lambda

This module provides a Chinook client configured to connect to AWS Lambda Function URLs with automatic URL discovery and deployment capabilities.

## Quick Start

Use the helper scripts for the easiest experience:

### Linux/macOS:
```bash
# Check AWS setup and available Lambda functions
./lambda-client.sh check

# Deploy Lambda and run client
./lambda-client.sh deploy-run

# Just run the client (auto-detects Lambda URL)
./lambda-client.sh run
```

### Windows:
```batch
REM Check AWS setup and available Lambda functions
lambda-client.bat check

REM Deploy Lambda and run client
lambda-client.bat deploy-run

REM Just run the client (auto-detects Lambda URL)
lambda-client.bat run
```

## Configuration

The client now automatically discovers your Lambda Function URL, but you can still override it:

### Automatic Discovery (Recommended)
The client will automatically:
1. Check if AWS CLI is configured
2. Find Chinook Lambda functions in your account
3. Retrieve the Function URL
4. Configure the client to connect

### Manual Override
```bash
# Using a specific Lambda hostname
./gradlew :chinook-client-lambda:run -Pcodion.client.lambda.hostname=your-lambda-url.lambda-url.region.on.aws

# Or using the helper script
./lambda-client.sh run --hostname your-lambda-url.lambda-url.region.on.aws
```

## AWS Requirements

- AWS CLI installed and configured (`aws configure`)
- Appropriate permissions to list Lambda functions and get Function URLs
- A deployed Chinook Lambda function with Function URL enabled

## Available Gradle Tasks

### AWS Management Tasks
```bash
# Check AWS CLI configuration and account info
./gradlew :chinook-client-lambda:checkAwsConfig

# List available Chinook Lambda functions with their URLs
./gradlew :chinook-client-lambda:listLambdaFunctions

# Get the Lambda URL that will be used by the client
./gradlew :chinook-client-lambda:getLambdaUrl

# Deploy the Lambda function (calls deploy.sh)
./gradlew :chinook-client-lambda:deployLambda

# Deploy Lambda and run client automatically
./gradlew :chinook-client-lambda:deployAndRun

# Run client with auto-detected Lambda URL
./gradlew :chinook-client-lambda:runWithLambda
```

### Standard Build Tasks
```bash
# Build the client
./gradlew :chinook-client-lambda:build

# Run the client (uses automatic hostname detection)
./gradlew :chinook-client-lambda:run

# Create native installer
./gradlew :chinook-client-lambda:jpackage
```

## Error Handling

The client provides clear error messages for common scenarios:

- **AWS CLI not configured**: Prompts to run `aws configure`
- **No Lambda functions found**: Suggests deploying first or providing hostname manually
- **Multiple Lambda functions**: Lists them and asks to specify which one to use
- **Lambda without Function URL**: Indicates the function needs Function URL configuration

## Advanced Usage

### Multiple Lambda Functions
If you have multiple Chinook Lambda functions, specify which one to use:
```bash
./gradlew :chinook-client-lambda:run -Pcodion.client.lambda.function=my-specific-lambda
```

### Environment Variables
Set these environment variables for additional control:
```bash
export AWS_PROFILE=myprofile                    # Use specific AWS profile
export CODION_CLIENT_LAMBDA_HOSTNAME=my.host   # Override hostname detection
```

### Integration with CI/CD
The tasks can be used in automated deployments:
```bash
# In your deployment script
./gradlew :chinook-client-lambda:deployLambda
./gradlew :chinook-client-lambda:getLambdaUrl > lambda-url.txt
```

## Packaging

The module supports creating native installers using jpackage:

```bash
./gradlew :chinook-client-lambda:jpackage
```

This will create a platform-specific installer (DEB on Linux, MSI on Windows, DMG on macOS) with the Lambda URL automatically configured.