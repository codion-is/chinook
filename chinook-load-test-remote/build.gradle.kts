plugins {
    id("org.beryx.jlink")
    id("chinook.jasperreports.pdf.modules")
}

dependencies {
    implementation(project(":chinook-load-test"))

    runtimeOnly(libs.codion.framework.db.rmi)
}

val serverHost: String by project
val serverRegistryPort: String by project

application {
    mainModule.set("is.codion.demos.chinook.client.loadtest")
    mainClass.set("is.codion.demos.chinook.client.loadtest.ChinookLoadTest")

    applicationDefaultJvmArgs = listOf(
        "-Xmx1024m",
        "-Dcodion.client.connectionType=remote",
        "-Dcodion.server.hostname=${serverHost}",
        "-Dcodion.server.registryPort=${serverRegistryPort}",
        "-Dcodion.client.connectionType=remote",
        "-Dcodion.client.trustStore=truststore.jks",
        "-Dcodion.client.trustStorePassword=crappypass"
    )
}

jlink {
    imageName.set(project.name)
    moduleName.set(application.mainModule)
    options.set(
        listOf(
            "--strip-debug",
            "--no-header-files",
            "--no-man-pages",
            "--add-modules",
            "is.codion.framework.db.rmi,is.codion.plugin.logback.proxy"
        )
    )

    jpackage {
        imageName = "Chinook-Load-Test-Remote"
        if (org.gradle.internal.os.OperatingSystem.current().isLinux) {
            installerType = "deb"
            icon = "../chinook.png"
            installerOptions = listOf(
                "--resource-dir",
                "build/jpackage/Chinook-Load-Test-Remote/lib",
                "--linux-shortcut"
            )
        }
        if (org.gradle.internal.os.OperatingSystem.current().isWindows) {
            installerType = "msi"
            icon = "../chinook.ico"
            installerOptions = listOf(
                "--win-menu",
                "--win-shortcut"
            )
        }
    }
}