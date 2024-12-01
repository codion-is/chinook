plugins {
    id("org.beryx.jlink")
    id("chinook.jasperreports.modules")
    id("chinook.spotless.plugin")
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

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}

tasks.test {
    useJUnitPlatform()
    systemProperty("codion.db.url", "jdbc:h2:mem:h2db")
    systemProperty("codion.db.initScripts", "classpath:create_schema.sql")
    systemProperty("chinook.service.port", "8899")
}

val servicePort: String by project

application {
    mainModule.set("is.codion.demos.chinook.service")
    mainClass.set("is.codion.demos.chinook.service.ChinookService")

    applicationDefaultJvmArgs = listOf(
        "-Xmx512m",
        "-Dcodion.db.url=jdbc:h2:mem:h2db",
        "-Dcodion.db.initScripts=classpath:create_schema.sql",
        "-Dcodion.db.countQueries=true",
        "-Dchinook.service.port=${servicePort}"
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
        "is.codion.framework.db.local,is.codion.dbms.h2," +
                "is.codion.plugin.logback.proxy,is.codion.plugin.hikari.pool," +
                "is.codion.demos.chinook.domain"
    )

    addExtraDependencies("slf4j-api", "jetty-jakarta-servlet-api")
}