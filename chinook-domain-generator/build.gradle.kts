plugins {
    id("application")
}

dependencies {
    runtimeOnly(libs.codion.tools.generator.ui)
    runtimeOnly(libs.codion.dbms.h2)
    runtimeOnly(libs.h2)
}

application {
    mainModule = "is.codion.tools.generator.ui"
    mainClass = "is.codion.tools.generator.ui.DomainGeneratorPanel"
    val scriptPaths = listOf(
        project(":chinook-domain").file("src/main/resources/create_schema.sql"),
        project(":chinook-domain").file("src/main/resources/db/migration/V2__Add_track_play_count.sql"),
        project(":chinook-domain").file("src/main/resources/db/migration/V3__Add_preferences.sql")
    ).joinToString(",") { it.absolutePath }
    applicationDefaultJvmArgs = listOf(
        "-Xmx64m",
        "-Dcodion.db.url=jdbc:h2:mem:h2db",
        "-Dcodion.db.initScripts=$scriptPaths",
        "-Dcodion.domain.generator.domainPackage=is.codion.demos.chinook.domain",
        "-Dcodion.domain.generator.user=sa",
        "-Dsun.awt.disablegrab=true"
    )
}