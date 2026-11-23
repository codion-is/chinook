plugins {
    `java-library`
    id("chinook.spotless.plugin")
}

dependencies {
    api(libs.codion.framework.domain)
    api(libs.codion.framework.db)
    api(libs.codion.plugin.jasperreports) {
        exclude(group = "org.apache.xmlgraphics")
    }
}