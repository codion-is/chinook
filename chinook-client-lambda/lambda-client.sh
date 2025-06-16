#!/bin/bash

# Chinook Lambda Client Helper Script
# ===================================
# This script provides convenient commands for managing the Chinook Lambda client

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Print colored output
print_error() { echo -e "${RED}❌ $1${NC}"; }
print_success() { echo -e "${GREEN}✅ $1${NC}"; }
print_warning() { echo -e "${YELLOW}⚠️  $1${NC}"; }
print_info() { echo -e "${BLUE}ℹ️  $1${NC}"; }

show_usage() {
    cat << EOF
Chinook Lambda Client Helper

USAGE:
    $0 <command> [options]

COMMANDS:
    check           Check AWS configuration and Lambda status
    list            List available Chinook Lambda functions
    url             Get the current Lambda URL
    deploy          Deploy the Lambda function
    run             Run the client (auto-detects Lambda URL)
    deploy-run      Deploy Lambda and run client
    help            Show this help message

EXAMPLES:
    $0 check                    # Check AWS setup
    $0 deploy                   # Deploy Lambda function
    $0 run                      # Run client with auto-detected URL
    $0 deploy-run               # Deploy then run
    
    # Override hostname manually
    $0 run --hostname my-lambda.amazonaws.com

ENVIRONMENT VARIABLES:
    AWS_PROFILE                 AWS profile to use
    CODION_CLIENT_LAMBDA_HOSTNAME  Override Lambda hostname

EOF
}

check_prerequisites() {
    # Check if we're in the right directory
    if [[ ! -f "$SCRIPT_DIR/build.gradle.kts" ]]; then
        print_error "This script must be run from the chinook-client-lambda directory"
        exit 1
    fi
    
    # Check if AWS CLI is available
    if ! command -v aws &> /dev/null; then
        print_error "AWS CLI is not installed. Please install it first:"
        echo "  https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2.html"
        exit 1
    fi
}

run_gradle_task() {
    local task="$1"
    shift
    print_info "Running gradle task: $task"
    cd "$PROJECT_ROOT"
    ./gradlew ":chinook-client-lambda:$task" "$@"
}

case "${1:-help}" in
    check)
        check_prerequisites
        print_info "Checking AWS configuration and Lambda status..."
        run_gradle_task "checkAwsConfig"
        echo
        run_gradle_task "listLambdaFunctions"
        ;;
    
    list)
        check_prerequisites
        print_info "Listing Chinook Lambda functions..."
        run_gradle_task "listLambdaFunctions"
        ;;
    
    url)
        check_prerequisites
        print_info "Getting Lambda URL..."
        run_gradle_task "getLambdaUrl"
        ;;
    
    deploy)
        check_prerequisites
        print_info "Deploying Chinook Lambda function..."
        run_gradle_task "deployLambda"
        ;;
    
    run)
        check_prerequisites
        shift # Remove 'run' from arguments
        if [[ "$1" == "--hostname" && -n "$2" ]]; then
            print_info "Running client with hostname: $2"
            run_gradle_task "run" "-Pcodion.client.lambda.hostname=$2"
        else
            print_info "Running client with auto-detected Lambda URL..."
            run_gradle_task "runWithLambda" "$@"
        fi
        ;;
    
    deploy-run)
        check_prerequisites
        print_info "Deploying Lambda and running client..."
        run_gradle_task "deployAndRun"
        ;;
    
    help|*)
        show_usage
        ;;
esac