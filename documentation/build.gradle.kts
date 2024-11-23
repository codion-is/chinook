plugins {
    id("org.asciidoctor.jvm.convert") version "4.0.3"
}

version = libs.versions.codion.get().replace("-SNAPSHOT", "")

tasks.asciidoctor {
    inputs.dir("../chinook-domain-api/src/main/java")
    inputs.dir("../chinook-domain/src/main/java")
    inputs.dir("../chinook-client/src/main/java")
    inputs.dir("../chinook-load-test/src/main/java")
    inputs.dir("../chinook-service/src/main/java")
    inputs.dir("../chinook-service-load-test/src/main/java")
    inputs.dir("../chinook-domain/src/main/resources")
    inputs.dir("../chinook-domain/src/test/java")
    inputs.dir("../chinook-client/src/test/java")
    inputs.dir("../chinook-service/src/test/java")

    baseDirFollowsSourceFile()

    attributes(
        mapOf(
            "codion-version" to project.version,
            "source-highlighter" to "prettify",
            "tabsize" to 2
        )
    )
    asciidoctorj {
        setVersion("2.5.13")
    }
}

tasks.register<Sync>("copyToGitHubPages") {
    dependsOn(tasks.asciidoctor)
    group = "documentation"
    val documentationDir = project.version
    from(project.layout.buildDirectory.dir("docs/asciidoc"))
    into("../../codion-pages/doc/${documentationDir}/tutorials/chinook")
}