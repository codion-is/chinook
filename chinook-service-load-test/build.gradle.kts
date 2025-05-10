import org.gradle.internal.os.OperatingSystem

plugins {
    id("org.beryx.jlink")
    id("chinook.jasperreports.modules")
    id("chinook.spotless.plugin")
    id("com.github.breadmoirai.github-release")
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
    imageName = project.name + "-" + project.version + "-" +
            OperatingSystem.current().familyName.replace(" ", "").lowercase()
    moduleName = application.mainModule
    options = listOf(
        "--strip-debug",
        "--no-header-files",
        "--no-man-pages"
    )

    jpackage {
        if (OperatingSystem.current().isLinux) {
            icon = "../chinook.png"
            installerType = "deb"
            installerOptions = listOf(
                "--linux-shortcut"
            )
        }
        if (OperatingSystem.current().isWindows) {
            icon = "../chinook.ico"
            installerType = "msi"
            installerOptions = listOf(
                "--win-menu",
                "--win-shortcut"
            )
        }
        if (OperatingSystem.current().isMacOsX) {
            icon = "../chinook.icns"
            installerType = "dmg"
        }
    }
}

githubRelease {
    token(properties["githubAccessToken"] as String)
    owner = "codion-is"
    repo = "chinook"
    allowUploadToExisting = true
    releaseAssets.from(tasks.named("jlinkZip").get().outputs.files)
    releaseAssets.from(fileTree(tasks.named("jpackage").get().outputs.files.singleFile) {
        exclude(project.name + "/**")
    })
}