plugins {
    `java-library`
    id("chinook.jasperreports.modules")
    id("chinook.spotless.plugin")
}

java {
    toolchain {
        // Since aws-lambda only supports 21
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