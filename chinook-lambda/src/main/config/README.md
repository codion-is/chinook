# Lambda Configuration Files

This directory contains reference configuration files for the Chinook Lambda deployment:

- **lambda-env.json** - Reference for Lambda environment variables configuration
- **update-env.sh** - Script to update Lambda environment variables without redeploying code
- **template.yaml** - AWS SAM template (alternative deployment method)

For actual deployment, use the `deploy.sh` script in the module root directory.