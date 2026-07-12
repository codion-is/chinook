plugins {
    id("org.asciidoctor.jvm.convert") version "4.0.4"
}

version = libs.versions.codion.get()

tasks.asciidoctor {
    val documented = rootProject.subprojects.filter { it.plugins.hasPlugin("java") }
    dependsOn(documented.map { it.tasks.build })
    documented.forEach { subproject ->
        inputs.file(subproject.buildFile)
        inputs.files(subproject.sourceSets.main.get().allSource)
        inputs.files(subproject.sourceSets.test.get().allSource)
    }

    baseDirFollowsSourceFile()

    attributes(
        mapOf(
            "codion-version" to project.version,
            "source-highlighter" to "rouge",
            "tabsize" to 2
        )
    )
    asciidoctorj {
        setVersion("2.5.13")
    }
}