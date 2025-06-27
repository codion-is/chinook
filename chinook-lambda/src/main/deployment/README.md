# Advanced Lambda Deployment Scripts

This directory contains scripts for advanced Lambda deployment scenarios beyond the simple in-memory H2 setup.

## Scripts Overview

### create-lambda-role.sh
Creates an IAM role with permissions for:
- Basic Lambda execution (CloudWatch logs)
- VPC access (required for RDS)
- EFS access (for persistent file storage)

Run this once before using the advanced deployment options.

### deploy-with-efs.sh
Deploys Lambda with Amazon EFS for persistent H2 file database:
- Creates VPC infrastructure
- Sets up EFS filesystem
- Mounts EFS to Lambda at `/mnt/efs`
- Database persists between Lambda invocations

Use when you need data persistence in serverless environment.

### deploy-with-rds.sh
Deploys Lambda with Amazon RDS PostgreSQL:
- Creates managed PostgreSQL instance
- Configures Lambda to connect to RDS
- Production-like database setup

Use for production testing or when you need a real database.

### init-database.sh
Initializes database schema after deployment:
- `./init-database.sh rds` - For RDS deployments
- `./init-database.sh efs` - For EFS deployments

### test-deployment.sh
Tests the deployed Lambda function (needs updating for current setup).

## Note
For simple demos with in-memory H2 database, use the `deploy.sh` script in the module root instead.