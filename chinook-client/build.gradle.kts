plugins {
    `java-library`
    id("chinook.jasperreports.pdf.modules")
    id("chinook.spotless.plugin")
}

dependencies {
    implementation(project(":chinook-domain-api"))

    implementation(libs.codion.swing.framework.ui)
    implementation(libs.codion.plugin.flatlaf)
    implementation(libs.codion.plugin.flatlaf.intellij.themes)
    implementation(libs.flatlaf.extras)
    implementation(libs.ikonli.foundation)
    implementation(libs.codion.tools.swing.mcp)

    implementation(libs.jasperreports.pdf) {
        exclude(group = "net.sf.jasperreports")
    }
    implementation(libs.jfreechart)

    runtimeOnly(libs.codion.plugin.logback.proxy)

    testImplementation(project(":chinook-domain"))
    testImplementation(libs.codion.framework.db.local)
    testRuntimeOnly(libs.codion.dbms.h2)
    testRuntimeOnly(libs.h2)
}

tasks.register<WriteProperties>("writeVersion") {
    destinationFile = file("${temporaryDir.absolutePath}/version.properties")
    property("version", libs.versions.codion.get().replace("-SNAPSHOT", ""))
}

tasks.processResources {
    from(tasks.named("writeVersion"))
}