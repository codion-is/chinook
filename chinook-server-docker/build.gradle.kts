plugins {
    id("application")
    id("com.gradleup.shadow") version "8.3.8"
}

val serverHost: String = "0.0.0.0"
val serverRegistryPort: String by project
val serverPort: String by project
val serverHttpPort: String by project
val serverAdminPort: String by project

application {
    mainClass.set("is.codion.framework.server.EntityServer")
    applicationDefaultJvmArgs = listOf(
        "-Xmx512m",
        "-Dlogback.configurationFile=logback.xml",
        //RMI configuration
        "-Djava.rmi.server.hostname=${serverHost}",
        "-Dcodion.server.registryPort=${serverRegistryPort}",
        "-Djava.rmi.server.randomIDs=true",
        "-Djava.rmi.server.useCodebaseOnly=true",
        //The serialization whitelist
        "-Dcodion.server.objectInputFilterFactory=is.codion.common.rmi.server.SerializationFilterFactory",
        "-Dcodion.server.serialization.filter.patternFile=classpath:serialization-filter-patterns.txt",
        //SSL configuration
        "-Dcodion.server.classpathKeyStore=keystore.jks",
        "-Djavax.net.ssl.keyStorePassword=crappypass",
        //The port used by clients
        "-Dcodion.server.port=${serverPort}",
        //The servlet server
        "-Dcodion.server.auxiliaryServerFactories=is.codion.framework.servlet.EntityServiceFactory",
        "-Dcodion.server.http.secure=false",
        "-Dcodion.server.http.port=${serverHttpPort}",
        "-Dcodion.server.http.useVirtualThreads=true",
        //The port for the admin interface, used by the server monitor
        "-Dcodion.server.admin.port=${serverAdminPort}",
        //The admin user credentials, used by the server monitor application
        "-Dcodion.server.admin.user=scott:tiger",
        //Database configuration
        "-Dcodion.db.url=jdbc:h2:mem:h2db",
        "-Dcodion.db.initScripts=classpath:create_schema.sql",
        "-Dcodion.db.countQueries=true",
        //A connection pool based on this user is created on startup
        "-Dcodion.server.connectionPoolUsers=scott:tiger",
        //Client logging disabled by default
        "-Dcodion.server.clientLogging=false"
    )
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

tasks.register("dockerBuild") {
    group = "docker"
    dependsOn("shadowJar")
    doLast {
        providers.exec {
            workingDir = projectDir
            commandLine("docker", "build", "-t", "chinook-server:latest", "-f", "src/docker/Dockerfile", ".")
        }.result.get()
    }
}