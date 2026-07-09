plugins {
    `java-library`
    id("chinook.spotless.plugin")
}

dependencies {
    implementation(project(":chinook-domain-api"))

    implementation(libs.codion.framework.domain)
    implementation(libs.codion.framework.json.domain)
}