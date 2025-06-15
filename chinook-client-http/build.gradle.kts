import org.gradle.internal.os.OperatingSystem

plugins {
    id("org.beryx.jlink")
    id("chinook.jasperreports.pdf.modules")
    id("com.github.breadmoirai.github-release")
}

dependencies {
    implementation(project(":chinook-client"))

    runtimeOnly(libs.codion.framework.db.http)
}

val serverHost: String by project
val serverHttpPort: String by project

application {
    mainModule = "is.codion.demos.chinook.client"
    mainClass = "is.codion.demos.chinook.ui.ChinookAppPanel"
    applicationDefaultJvmArgs = listOf(
        "-Xmx64m",
        "-Dcodion.client.connectionType=http",
        "-Dcodion.client.http.json=false",
        "-Dcodion.client.http.hostname=vf5lwi7pgci5g32fw3drtsbovm0vbmsu.lambda-url.eu-north-1.on.aws",
        "-Dcodion.client.http.secure=true",
        "-Dcodion.client.http.securePort=443",
        "-Dcodion.client.http.connectTimeout=30000",
        "-Dcodion.client.http.socketTimeout=30000",
        "-Dlogback.configurationFile=src/main/config/logback.xml",
        "-Dsun.awt.disablegrab=true"
    )
}

jlink {
    imageName = project.name + "-" + project.version + "-" +
            OperatingSystem.current().familyName.replace(" ", "").lowercase()
    moduleName = application.mainModule
    options = listOf(
        "--strip-debug",
        "--no-header-files",
        "--no-man-pages",
        "--add-modules",
        "is.codion.framework.db.http,is.codion.plugin.logback.proxy"
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

tasks.prepareMergedJarsDir {
    doLast {
        copy {
            from("src/main/resources")
            into("build/jlinkbase/mergedjars")
        }
    }
}

if (properties.containsKey("githubAccessToken")) {
    githubRelease {
        token(properties["githubAccessToken"] as String)
        owner = "codion-is"
        repo = "chinook"
        allowUploadToExisting = true
        releaseAssets.from(tasks.named("jlinkZip").get().outputs.files)
        releaseAssets.from(fileTree(tasks.named("jpackage").get().outputs.files.singleFile) {
            exclude(project.name + "/**", project.name + ".app/**")
        })
    }
}