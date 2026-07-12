plugins {
    `java-library`
    id("chinook.spotless.plugin")
}

dependencies {
    api(project(":chinook-domain-api"))

    implementation(libs.codion.common.rmi)
    implementation(libs.codion.framework.db.local)

    testImplementation(libs.codion.framework.domain.test)
    testRuntimeOnly(libs.codion.dbms.h2)
    testRuntimeOnly(libs.h2)
}
