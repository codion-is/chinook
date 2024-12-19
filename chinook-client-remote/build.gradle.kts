import org.gradle.internal.os.OperatingSystem

plugins {
    id("org.beryx.jlink")
    id("chinook.jasperreports.pdf.modules")
}

dependencies {
    implementation(project(":chinook-client"))

    runtimeOnly(libs.codion.framework.db.rmi)
}

val serverHost: String by project
val serverRegistryPort: String by project

application {
    mainModule = "is.codion.demos.chinook.client"
    mainClass = "is.codion.demos.chinook.ui.ChinookAppPanel"
    applicationDefaultJvmArgs = listOf(
        "-Xmx64m",
        "-Dcodion.client.connectionType=remote",
        "-Dcodion.server.hostname=${serverHost}",
        "-Dcodion.server.registryPort=${serverRegistryPort}",
        "-Dcodion.client.trustStore=truststore.jks",
        "-Dcodion.client.trustStorePassword=crappypass",
        "-Dsun.awt.disablegrab=true"
    )
}

jlink {
    imageName = project.name
    moduleName = application.mainModule
    options = listOf(
        "--strip-debug",
        "--no-header-files",
        "--no-man-pages",
        "--add-modules",
        "is.codion.framework.db.rmi,is.codion.plugin.logback.proxy"
    )

    jpackage {
        imageName = "Chinook-Remote"
        if (OperatingSystem.current().isLinux) {
            icon = "../chinook.png"
            installerOptions = listOf(
                "--resource-dir",
                "build/jpackage/Chinook-Remote/lib",
                "--linux-shortcut"
            )
        }
        if (OperatingSystem.current().isWindows) {
            icon = "../chinook.ico"
            installerOptions = listOf(
                "--win-menu",
                "--win-shortcut"
            )
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