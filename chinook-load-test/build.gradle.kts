plugins {
    id("java-library")
    id("chinook.jasperreports.pdf.modules")
    id("chinook.spotless.plugin")
}

dependencies {
    implementation(project(":chinook-domain-api"))
    implementation(project(":chinook-client"))

    implementation(libs.codion.tools.loadtest.ui)
    implementation(libs.codion.swing.framework.ui)
    implementation(libs.codion.tools.generator.model)
}