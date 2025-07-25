plugins {
    java
    id("com.gradleup.shadow") version "8.3.7"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation(project(":chinook-domain"))
    implementation(libs.codion.framework.lambda)

    implementation(libs.aws.lambda.core)
    implementation(libs.aws.lambda.events)

    implementation(libs.codion.dbms.h2)
    implementation(libs.h2)
    implementation(libs.codion.plugin.hikari.pool)

    testImplementation(libs.codion.framework.db.http)
}

tasks.shadowJar {
    archiveBaseName.set("chinook-lambda")
    archiveClassifier.set("")
    archiveVersion.set("")

    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")

    mergeServiceFiles()
}