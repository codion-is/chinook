import org.gradle.internal.os.OperatingSystem

plugins {
    id("org.beryx.jlink")
    id("chinook.jasperreports.modules")
    id("chinook.spotless.plugin")
    id("com.github.breadmoirai.github-release")
}

dependencies {
    implementation(project(":chinook-domain"))

    implementation(libs.codion.framework.db.local)
    implementation(libs.codion.plugin.hikari.pool)
    implementation(libs.jackson.databind)
    implementation(libs.javalin) {
        exclude(group = "org.jetbrains")
    }

    runtimeOnly(libs.codion.plugin.logback.proxy)
    runtimeOnly(libs.codion.dbms.h2)
    runtimeOnly(libs.h2)
}

tasks.test {
    systemProperty("chinook.service.port", "8899")
}

val servicePort: String by project

application {
    mainModule = "is.codion.demos.chinook.service"
    mainClass = "is.codion.demos.chinook.service.ChinookService"
    applicationDefaultJvmArgs = listOf(
        "-Xmx512m",
        "-Dcodion.db.url=jdbc:h2:mem:h2db",
        "-Dcodion.db.initScripts=classpath:create_schema.sql",
        "-Dcodion.db.countQueries=true",
        "-Dchinook.service.port=${servicePort}"
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
        "is.codion.framework.db.local,is.codion.dbms.h2," +
                "is.codion.plugin.logback.proxy,is.codion.plugin.hikari.pool"
    )

    addExtraDependencies("slf4j-api", "jetty-jakarta-servlet-api")
}

githubRelease {
    token(properties["githubAccessToken"] as String)
    owner = "codion-is"
    repo = "chinook"
    allowUploadToExisting = true
    releaseAssets.from(tasks.named("jlinkZip").get().outputs.files)
}