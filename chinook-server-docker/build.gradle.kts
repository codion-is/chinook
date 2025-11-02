tasks.register("prepareJlinkImage") {
    group = "docker"
    description = "Copy jlink image from chinook-server for Docker build"
    dependsOn(":chinook-server:jlink")

    doLast {
        val serverProject = project(":chinook-server")
        val serverBuildDir = serverProject.layout.buildDirectory.get().asFile

        // Find the jlink directory (chinook-server-VERSION-linux/)
        val jlinkDir = serverBuildDir.listFiles()?.find {
            it.isDirectory && it.name.startsWith("chinook-server-") && it.name.endsWith("-linux")
        } ?: throw GradleException("Could not find a linux based jlink image directory in ${serverBuildDir}")

        val targetDir = project.layout.buildDirectory.dir("jlink-image").get().asFile
        delete(targetDir)
        copy {
            from(jlinkDir)
            into(targetDir)
        }

        println("Copied jlink image from: ${jlinkDir}")
        println("To: ${targetDir}")
    }
}

tasks.register("dockerBuild") {
    group = "docker"
    description = "Build Docker image using jlink runtime from chinook-server"
    dependsOn("prepareJlinkImage")

    doLast {
        providers.exec {
            workingDir = projectDir
            commandLine("docker", "build", "-t", "chinook-server:latest", "-f", "src/docker/Dockerfile", "build")
        }.result.get()
    }
}

tasks.register("dockerDeploy") {
    group = "docker"
    description = "Build and deploy the Docker container (restart with updated image)"
    dependsOn("dockerBuild")

    doLast {
        println("Stopping existing container...")
        providers.exec {
            workingDir = projectDir
            commandLine("docker", "compose", "down")
        }.result.get()

        println("Starting container with updated image...")
        providers.exec {
            workingDir = projectDir
            commandLine("docker", "compose", "up", "-d")
        }.result.get()

        println("Container deployed successfully!")
    }
}