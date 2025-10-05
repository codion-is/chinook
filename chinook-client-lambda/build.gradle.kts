import org.gradle.internal.os.OperatingSystem

plugins {
    id("org.beryx.jlink")
    id("chinook.jasperreports.pdf.modules")
    id("com.github.breadmoirai.github-release")
}

dependencies {
    implementation(project(":chinook-client"))

    runtimeOnly(libs.codion.framework.db.http)
}

application {
    mainModule = "is.codion.demos.chinook.client"
    mainClass = "is.codion.demos.chinook.ui.ChinookAppPanel"
}

// Configure JVM args lazily in the run task, in order to not trigger
// the lambda hostname lookup during the configuration phase
tasks.named<JavaExec>("run") {
    jvmArgs = listOf(
        "-Xmx64m",
        "-Dcodion.client.connectionType=http",
        "-Dcodion.client.http.hostname=${resolveLambdaHostname()}",
        "-Dcodion.client.http.secure=true",
        "-Dcodion.client.http.securePort=443",
        "-Dcodion.client.http.connectTimeout=30000",
        "-Dcodion.client.http.socketTimeout=30000",
        "-Dcodion.client.http.disconnectOnClose=false",
        "-Dlogback.configurationFile=src/main/config/logback.xml",
        "-Dsun.awt.disablegrab=true"
    )
}

jlink {
    imageName = project.name + "-" + project.version + "-" +
            OperatingSystem.current().familyName.replace(" ", "").lowercase()
    moduleName = application.mainModule
    options = listOf(
        "--strip-debug",
        "--no-header-files",
        "--no-man-pages",
        "--add-modules",
        "is.codion.framework.db.http,is.codion.plugin.logback.proxy"
    )

    // Due to a transitive io.modelcontextprotocol.sdk:mcp dependency
    mergedModule {
        excludeProvides(mapOf("service" to "io.micrometer.context.ContextAccessor"))
        excludeProvides(mapOf("service" to "reactor.blockhound.integration.BlockHoundIntegration"))
    }

    jpackage {
        if (OperatingSystem.current().isLinux) {
            icon = "../chinook.png"
            installerType = "deb"
            installerOptions = listOf(
                "--linux-shortcut"
            )
        }
        if (OperatingSystem.current().isWindows) {
            icon = "../chinook.ico"
            installerType = "msi"
            installerOptions = listOf(
                "--win-menu",
                "--win-shortcut"
            )
        }
        if (OperatingSystem.current().isMacOsX) {
            icon = "../chinook.icns"
            installerType = "dmg"
        }
    }
}

tasks.prepareMergedJarsDir {
    doLast {
        copy {
            from("src/main/resources")
            into("build/jlinkbase/mergedjars")
        }
    }
}

// AWS Lambda management functions and tasks
fun isAwsCliConfigured(): Boolean {
    return try {
        val process = ProcessBuilder("aws", "sts", "get-caller-identity")
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()
        process.waitFor() == 0
    } catch (e: Exception) {
        false
    }
}

fun getLambdaFunctionUrl(functionName: String): String? {
    return try {
        val process = ProcessBuilder(
            "aws", "lambda", "get-function-url-config",
            "--function-name", functionName,
            "--query", "FunctionUrl",
            "--output", "text"
        ).redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        if (process.waitFor() == 0) {
            val url = process.inputStream.bufferedReader().readText().trim()
            if (url.isNotEmpty() && url != "None") {
                // Remove https:// and trailing slash to get hostname
                url.removePrefix("https://").removeSuffix("/")
            } else null
        } else null
    } catch (e: Exception) {
        null
    }
}

fun findLambdaFunctions(): List<String> {
    return try {
        val process = ProcessBuilder(
            "aws", "lambda", "list-functions",
            "--query", "Functions[?contains(FunctionName, 'chinook')].FunctionName",
            "--output", "text"
        ).redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        if (process.waitFor() == 0) {
            val output = process.inputStream.bufferedReader().readText().trim()
            if (output.isNotEmpty()) {
                output.split("\t", "\n").filter { it.isNotEmpty() }
            } else emptyList()
        } else emptyList()
    } catch (e: Exception) {
        emptyList()
    }
}

fun resolveLambdaHostname(): String {
    // Check if hostname is explicitly provided
    val explicitHostname = project.findProperty("codion.client.lambda.hostname") as String?
    if (!explicitHostname.isNullOrEmpty()) {
        return explicitHostname
    }

    // Check if AWS CLI is configured
    if (!isAwsCliConfigured()) {
        throw GradleException(
            "AWS CLI is not configured and no explicit hostname provided. " +
                    "Either configure AWS CLI with 'aws configure' or provide hostname with " +
                    "-Pcodion.client.lambda.hostname=your-lambda-url.amazonaws.com"
        )
    }

    // Try to find and use a Chinook Lambda function
    val functions = findLambdaFunctions()
    when {
        functions.isEmpty() -> throw GradleException(
            "No Chinook Lambda functions found. " +
                    "Deploy a Lambda function first or provide hostname with " +
                    "-Pcodion.client.lambda.hostname=your-lambda-url.amazonaws.com"
        )

        functions.size == 1 -> {
            val functionName = functions.first()
            val hostname = getLambdaFunctionUrl(functionName)
            if (hostname != null) {
                println("Using Lambda function: $functionName")
                println("Lambda URL hostname: $hostname")
                return hostname
            } else {
                throw GradleException(
                    "Lambda function '$functionName' exists but has no Function URL configured. " +
                            "Deploy with Function URL or provide hostname with " +
                            "-Pcodion.client.lambda.hostname=your-lambda-url.amazonaws.com"
                )
            }
        }

        else -> {
            val functionList = functions.joinToString(", ")
            throw GradleException(
                "Multiple Chinook Lambda functions found: $functionList. " +
                        "Specify which one to use with -Pcodion.client.lambda.function=function-name " +
                        "or provide hostname directly with -Pcodion.client.lambda.hostname=your-lambda-url.amazonaws.com"
            )
        }
    }
}

// AWS Lambda management tasks
tasks.register("checkAwsConfig") {
    group = "aws"
    description = "Check if AWS CLI is properly configured"

    doLast {
        if (isAwsCliConfigured()) {
            println("✓ AWS CLI is configured")

            // Get account info
            val accountProcess =
                ProcessBuilder("aws", "sts", "get-caller-identity", "--query", "Account", "--output", "text")
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .start()
            if (accountProcess.waitFor() == 0) {
                val account = accountProcess.inputStream.bufferedReader().readText().trim()
                println("  Account: $account")
            }

            // Get region
            val regionProcess = ProcessBuilder("aws", "configure", "get", "region")
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .start()
            if (regionProcess.waitFor() == 0) {
                val region = regionProcess.inputStream.bufferedReader().readText().trim()
                println("  Region: $region")
            }
        } else {
            println("❌ AWS CLI is not configured")
            println("Run 'aws configure' to set up your credentials")
        }
    }
}

tasks.register("listLambdaFunctions") {
    group = "aws"
    description = "List available Chinook Lambda functions"

    doLast {
        if (!isAwsCliConfigured()) {
            println("❌ AWS CLI is not configured. Run 'aws configure' first.")
            return@doLast
        }

        val functions = findLambdaFunctions()
        if (functions.isEmpty()) {
            println("No Chinook Lambda functions found")
        } else {
            println("Found Chinook Lambda functions:")
            functions.forEach { functionName ->
                val url = getLambdaFunctionUrl(functionName)
                if (url != null) {
                    println("  ✓ $functionName -> $url")
                } else {
                    println("  ⚠ $functionName (no Function URL configured)")
                }
            }
        }
    }
}

tasks.register("getLambdaUrl") {
    group = "aws"
    description = "Get the Lambda Function URL for the client"

    doLast {
        try {
            val hostname = resolveLambdaHostname()
            println("Lambda hostname: $hostname")
            println("Full URL: https://$hostname/")
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }
}

tasks.register("deployLambda") {
    group = "aws"
    description = "Deploy the Chinook Lambda function"

    doLast {
        if (!isAwsCliConfigured()) {
            throw GradleException("AWS CLI is not configured. Run 'aws configure' first.")
        }

        println("Deploying Chinook Lambda function...")
        val deployScript = file("../chinook-lambda/deploy.sh")
        if (!deployScript.exists()) {
            throw GradleException("Deploy script not found: ${deployScript.absolutePath}")
        }

        val processBuilder = ProcessBuilder("bash", deployScript.absolutePath)
            .directory(file("../chinook-lambda"))
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)

        // Pass JAVA_HOME from Gradle's environment
        val javaHome = System.getProperty("java.home")
        if (javaHome != null) {
            processBuilder.environment()["JAVA_HOME"] = javaHome
        }

        val process = processBuilder.start()

        // Capture output and error streams
        val output = process.inputStream.bufferedReader().use { it.readText() }
        val error = process.errorStream.bufferedReader().use { it.readText() }

        // Print output as it would appear normally
        if (output.isNotEmpty()) {
            println(output)
        }

        val exitCode = process.waitFor()
        if (exitCode != 0) {
            if (error.isNotEmpty()) {
                println("Error output:")
                println(error)
            }
            throw GradleException("Lambda deployment failed with exit code: $exitCode")
        }

        println("Lambda deployment completed successfully")
    }
}

tasks.register("deployAndRun") {
    group = "aws"
    description = "Deploy Lambda function and run the client"

    dependsOn("deployLambda")
    finalizedBy("run")

    doLast {
        println("Lambda deployed, starting client...")
    }
}

tasks.register("runWithLambda") {
    group = "aws"
    description = "Run the client with automatic Lambda URL detection"

    dependsOn("run")

    doFirst {
        // This will trigger hostname resolution before running
        val hostname = resolveLambdaHostname()
        println("Running client with Lambda hostname: $hostname")
    }
}

if (properties.containsKey("githubAccessToken")) {
    githubRelease {
        token(properties["githubAccessToken"] as String)
        owner = "codion-is"
        repo = "chinook"
        allowUploadToExisting = true
        releaseAssets.from(tasks.named("jlinkZip").get().outputs.files)
        releaseAssets.from(fileTree(tasks.named("jpackage").get().outputs.files.singleFile) {
            exclude(project.name + "/**", project.name + ".app/**")
        })
    }
}