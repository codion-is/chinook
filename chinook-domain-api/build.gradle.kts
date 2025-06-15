plugins {
    `java-library`
    id("chinook.jasperreports.modules")
    id("chinook.spotless.plugin")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    api(libs.codion.framework.domain)
    api(libs.codion.framework.db.core)
    api(libs.codion.plugin.jasperreports) {
        exclude(group = "org.apache.xmlgraphics")
    }
}