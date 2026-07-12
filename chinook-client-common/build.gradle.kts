plugins {
    id("java-library")
    id("chinook.spotless.plugin")
}

dependencies {
    implementation(project(":chinook-domain-api"))

    implementation(libs.codion.framework.model)
}