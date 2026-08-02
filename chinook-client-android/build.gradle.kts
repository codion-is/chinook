plugins {
    // AGP 9.0 has built-in Kotlin support, so org.jetbrains.kotlin.android is not applied.
    id("com.android.application") version "9.0.0"
    id("org.jetbrains.compose") version "1.9.3"
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.0"
}

android {
    namespace = "is.codion.demos.chinook.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "is.codion.demos.chinook.android"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = version.toString()
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false // R8/ServiceLoader keep-rules are a later concern
        }
    }
}

// The chinook project's daemon runs JDK 26, which AGP 9.0 doesn't support (its jlink-based JdkImageTransform
// fails). Pin this module's Java/Kotlin toolchain to 21 so AGP runs its tooling on a supported JDK.
kotlin {
    jvmToolchain(21)
}

dependencies {
    // The codion-* catalog entries carry no version of their own, the BOM supplies them. The root build applies it
    // to every other module, but has to skip this one — AGP creates the configurations it would attach to only once
    // this script is evaluated — so it is applied here instead.
    implementation(platform(libs.codion.framework.bom))

    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material3)
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.fragment:fragment:1.8.5")

    implementation(project(":chinook-domain"))
    implementation(project(":chinook-domain-json"))
    implementation(project(":chinook-client-common"))
    implementation(libs.codion.android.framework.ui)
    implementation(libs.codion.framework.db.http)
    implementation(libs.codion.framework.db.local)
    implementation(libs.codion.dbms.h2)
    runtimeOnly(libs.h2)
}

// Convenience: `./gradlew :chinook-client-android:pushToPhone`
// Builds + installs the debug APK on the connected device and launches it (installDebug is provided by AGP).
tasks.register<Exec>("pushToPhone") {
    group = "android"
    description = "Build + install the debug APK on the connected device and launch it"
    dependsOn("installDebug")

    val sdkDir = System.getenv("ANDROID_HOME")
        ?: rootProject.file("local.properties").takeIf { it.exists() }
            ?.readLines()?.firstOrNull { it.startsWith("sdk.dir=") }
            ?.substringAfter("=")?.trim()
        ?: error("Android SDK not found (set ANDROID_HOME or sdk.dir in local.properties)")
    commandLine(
        "$sdkDir/platform-tools/adb",
        "shell", "am", "start", "-n", "is.codion.demos.chinook.android/.MainActivity",
    )
}
