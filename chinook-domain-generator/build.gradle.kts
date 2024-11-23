plugins {
    id("application")
}

dependencies {
    runtimeOnly(libs.codion.tools.generator.ui)
    runtimeOnly(libs.codion.dbms.h2)
    runtimeOnly(libs.h2)
}

application {
    mainModule.set("is.codion.tools.generator.ui")
    mainClass.set("is.codion.tools.generator.ui.DomainGeneratorPanel")

    applicationDefaultJvmArgs = listOf(
        "-Xmx64m",
        "-Dcodion.db.url=jdbc:h2:mem:h2db",
        "-Dcodion.db.initScripts=../chinook-domain/src/main/resources/create_schema.sql,",
        "-Dcodion.domain.generator.defaultDomainPackage=is.codion.framework.demos.chinook.domain.api",
        "-Dcodion.domain.generator.defaultUser=sa",
        "-Dsun.awt.disablegrab=true"
    )
}