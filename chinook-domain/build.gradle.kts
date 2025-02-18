plugins {
    `java-library`
    id("chinook.jasperreports.modules")
    id("chinook.spotless.plugin")
    id("io.github.f-cramer.jasperreports") version "0.0.4"
}

dependencies {
    api(project(":chinook-domain-api"))

    implementation(libs.codion.common.rmi)
    implementation(libs.codion.framework.i18n)
    implementation(libs.codion.framework.db.local)

    jasperreportsClasspath(libs.jasperreports.jdt) {
        exclude(group = "net.sf.jasperreports")
    }

    testImplementation(libs.codion.framework.domain.test)
    testRuntimeOnly(libs.codion.dbms.h2)
    testRuntimeOnly(libs.h2)
}

jasperreports {
    classpath.from(project.sourceSets.main.get().compileClasspath)
}

sourceSets.main {
    resources.srcDir(tasks.compileAllReports)
}