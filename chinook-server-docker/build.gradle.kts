plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    runtimeOnly(libs.codion.framework.server)
    runtimeOnly(libs.codion.framework.servlet)
    runtimeOnly(libs.codion.plugin.hikari.pool)
    runtimeOnly(libs.codion.dbms.h2)
    runtimeOnly(libs.h2)

    runtimeOnly(project(":chinook-domain"))

    //logging library, skipping the email stuff
    runtimeOnly(libs.codion.plugin.logback.proxy) {
        exclude(group = "com.sun.mail", module = "javax.mail")
    }
}


// Task to build Docker image
tasks.register("dockerBuild") {
    dependsOn("shadowJar")
    doLast {
        exec {
            workingDir = projectDir
            commandLine("docker", "build", "-t", "chinook-server:latest", "-f", "src/docker/Dockerfile", ".")
        }
    }
}