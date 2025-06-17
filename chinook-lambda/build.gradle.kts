plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation(project(":chinook-domain"))
    implementation(libs.codion.framework.lambda)

    // AWS Lambda runtime
    implementation(libs.aws.lambda.core)
    implementation(libs.aws.lambda.events)
    
    // Database drivers
    implementation(libs.codion.dbms.h2)
    implementation(libs.h2)
    
    // Connection pooling
    implementation(libs.codion.plugin.hikari.pool)
    
    testImplementation(libs.codion.framework.db.http)
}

tasks.shadowJar {
    archiveBaseName.set("chinook-lambda")
    archiveClassifier.set("")
    archiveVersion.set("")

    // Exclude conflicting files
    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")

    // Merge service files
    mergeServiceFiles()
}

tasks.register<Zip>("lambdaZip") {
    dependsOn(tasks.shadowJar)
    from(tasks.shadowJar.get().outputs.files.singleFile)
    archiveFileName.set("chinook-lambda-deployment.zip")
    destinationDirectory.set(layout.buildDirectory.dir("distributions"))
}

tasks.register("deployLambda") {
    dependsOn("lambdaZip")
    group = "aws"
    description = "Build Lambda deployment package"

    doLast {
        println("Lambda deployment package created: build/distributions/chinook-lambda-deployment.zip")
        println("Deploy with: aws lambda update-function-code --function-name chinook-api --zip-file fileb://build/distributions/chinook-lambda-deployment.zip")
    }
}