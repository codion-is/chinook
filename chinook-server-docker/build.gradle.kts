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
        } ?: throw GradleException("Could not find jlink image directory in ${serverBuildDir}")

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