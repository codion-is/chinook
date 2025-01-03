import org.gradle.internal.os.OperatingSystem

plugins {
    id("org.beryx.jlink")
    id("chinook.jasperreports.modules")
    id("chinook.spotless.plugin")
}

dependencies {
    implementation(project(":chinook-domain-api"))
    implementation(libs.codion.tools.loadtest.ui)
    implementation(libs.jackson.databind)
}

application {
    mainModule = "is.codion.demos.chinook.service.loadtest"
    mainClass = "is.codion.demos.chinook.service.loadtest.ChinookServiceLoadTest"
    applicationDefaultJvmArgs = listOf(
        "-Xmx512m"
    )
}

jlink {
    imageName = project.name
    moduleName = application.mainModule
    options = listOf(
        "--strip-debug",
        "--no-header-files",
        "--no-man-pages"
    )

    jpackage {
        if (OperatingSystem.current().isLinux) {
            icon = "../chinook.png"
            installerOptions = listOf(
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