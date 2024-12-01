plugins {
    id("org.beryx.jlink")
    id("chinook.jasperreports.pdf.modules")
}

dependencies {
    implementation(project(":chinook-load-test"))

    runtimeOnly(libs.codion.framework.db.http)
}

val serverHost: String by project
val serverHttpPort: String by project

application {
    mainModule.set("is.codion.demos.chinook.client.loadtest")
    mainClass.set("is.codion.demos.chinook.client.loadtest.ChinookLoadTest")

    applicationDefaultJvmArgs = listOf(
        "-Xmx1024m",
        "-Dcodion.client.connectionType=http",
        "-Dcodion.client.http.secure=false",
        "-Dcodion.client.http.hostname=$serverHost",
        "-Dcodion.client.http.port=$serverHttpPort"
    )
}

jlink {
    imageName.set(project.name)
    moduleName.set(application.mainModule)
    options = listOf(
        "--strip-debug",
        "--no-header-files",
        "--no-man-pages",
        "--add-modules",
        "is.codion.framework.db.http,is.codion.plugin.logback.proxy"
    )

    jpackage {
        imageName = "Chinook-Load-Test-Http"
        if (org.gradle.internal.os.OperatingSystem.current().isLinux) {
            installerType = "deb"
            icon = "../chinook.png"
            installerOptions = listOf(
                "--resource-dir",
                "build/jpackage/Chinook-Load-Test-Http/lib",
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