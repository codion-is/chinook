plugins {
    id("org.beryx.jlink")
    id("chinook.jasperreports.pdf.modules")
}

dependencies {
    implementation(project(":chinook-client"))

    runtimeOnly(project(":chinook-domain"))

    runtimeOnly(libs.codion.framework.db.local)
    runtimeOnly(libs.codion.dbms.h2)
    runtimeOnly(libs.h2)
}

application {
    mainModule.set("is.codion.framework.demos.chinook.client")
    mainClass.set("is.codion.framework.demos.chinook.ui.ChinookAppPanel")

    applicationDefaultJvmArgs = listOf(
        "-Xmx64m",
        "-Dcodion.client.connectionType=local",
        "-Dcodion.db.url=jdbc:h2:mem:h2db",
        "-Dcodion.db.initScripts=classpath:create_schema.sql",
        "-Dsun.awt.disablegrab=true"
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
            "is.codion.framework.db.local,is.codion.dbms.h2," +
                    "is.codion.plugin.logback.proxy,is.codion.framework.demos.chinook.domain"
        )
    )

    addExtraDependencies("slf4j-api")

    jpackage {
        imageName = "Chinook-Local"
        if (org.gradle.internal.os.OperatingSystem.current().isLinux) {
            installerType = "deb"
            icon = "../chinook.png"
            installerOptions = listOf(
                "--resource-dir",
                "build/jpackage/Chinook-Local/lib",
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