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
    mainModule.set("is.codion.framework.demos.chinook.service.loadtest")
    mainClass.set("is.codion.framework.demos.chinook.service.loadtest.ChinookServiceLoadTest")

    applicationDefaultJvmArgs = listOf(
        "-Xmx512m"
    )
}

jlink {
    imageName.set(project.name)
    moduleName.set(application.mainModule)
    options.set(listOf("--strip-debug", "--no-header-files", "--no-man-pages"))

    jpackage {
        imageName = "Chinook-Service-Load-Test"
        if (org.gradle.internal.os.OperatingSystem.current().isLinux) {
            installerType = "deb"
            icon = "../chinook.png"
            installerOptions = listOf(
                "--resource-dir",
                "build/jpackage/Chinook-Service-Load-Test/lib",
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