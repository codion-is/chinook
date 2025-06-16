@echo off
REM Chinook Lambda Client Helper Script (Windows)
REM ===============================================
REM This script provides convenient commands for managing the Chinook Lambda client

setlocal enabledelayedexpansion

set SCRIPT_DIR=%~dp0
set PROJECT_ROOT=%SCRIPT_DIR%..

if "%1"=="" goto :show_usage
if "%1"=="help" goto :show_usage

REM Check prerequisites
if not exist "%SCRIPT_DIR%build.gradle.kts" (
    echo ERROR: This script must be run from the chinook-client-lambda directory
    exit /b 1
)

where aws >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: AWS CLI is not installed. Please install it first:
    echo   https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2.html
    exit /b 1
)

REM Parse command
if "%1"=="check" goto :check
if "%1"=="list" goto :list
if "%1"=="url" goto :url
if "%1"=="deploy" goto :deploy
if "%1"=="run" goto :run
if "%1"=="deploy-run" goto :deploy_run

goto :show_usage

:check
echo Checking AWS configuration and Lambda status...
cd /d "%PROJECT_ROOT%"
call gradlew :chinook-client-lambda:checkAwsConfig
echo.
call gradlew :chinook-client-lambda:listLambdaFunctions
goto :end

:list
echo Listing Chinook Lambda functions...
cd /d "%PROJECT_ROOT%"
call gradlew :chinook-client-lambda:listLambdaFunctions
goto :end

:url
echo Getting Lambda URL...
cd /d "%PROJECT_ROOT%"
call gradlew :chinook-client-lambda:getLambdaUrl
goto :end

:deploy
echo Deploying Chinook Lambda function...
cd /d "%PROJECT_ROOT%"
call gradlew :chinook-client-lambda:deployLambda
goto :end

:run
if "%2"=="--hostname" (
    if "%3"=="" (
        echo ERROR: --hostname requires a value
        exit /b 1
    )
    echo Running client with hostname: %3
    cd /d "%PROJECT_ROOT%"
    call gradlew :chinook-client-lambda:run -Pcodion.client.lambda.hostname=%3
) else (
    echo Running client with auto-detected Lambda URL...
    cd /d "%PROJECT_ROOT%"
    call gradlew :chinook-client-lambda:runWithLambda
)
goto :end

:deploy_run
echo Deploying Lambda and running client...
cd /d "%PROJECT_ROOT%"
call gradlew :chinook-client-lambda:deployAndRun
goto :end

:show_usage
echo.
echo Chinook Lambda Client Helper
echo.
echo USAGE:
echo     %0 ^<command^> [options]
echo.
echo COMMANDS:
echo     check           Check AWS configuration and Lambda status
echo     list            List available Chinook Lambda functions
echo     url             Get the current Lambda URL
echo     deploy          Deploy the Lambda function
echo     run             Run the client (auto-detects Lambda URL)
echo     deploy-run      Deploy Lambda and run client
echo     help            Show this help message
echo.
echo EXAMPLES:
echo     %0 check                    # Check AWS setup
echo     %0 deploy                   # Deploy Lambda function
echo     %0 run                      # Run client with auto-detected URL
echo     %0 deploy-run               # Deploy then run
echo.
echo     # Override hostname manually
echo     %0 run --hostname my-lambda.amazonaws.com
echo.
echo ENVIRONMENT VARIABLES:
echo     AWS_PROFILE                 AWS profile to use
echo     CODION_CLIENT_LAMBDA_HOSTNAME  Override Lambda hostname
echo.

:end