plugins {
    // AGP 9.0 has built-in Kotlin support, so org.jetbrains.kotlin.android is not applied.
    id("com.android.application") version "9.0.0"
    id("org.jetbrains.compose") version "1.9.3"
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.0"
}

// Matches gradle/libs.versions.toml `codion` and the codion-android-* libs published to mavenLocal.
val codionVersion = "0.18.80"

android {
    namespace = "is.codion.demos.chinook.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "is.codion.demos.chinook.android"
        minSdk = 26 // java.time native from 26, avoids desugaring
        targetSdk = 35
        versionCode = 1
        versionName = codionVersion
    }

    compileOptions {
        // Built-in Kotlin derives jvmTarget from targetCompatibility.
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false // R8/ServiceLoader keep-rules are a later concern
        }
    }
}

// The chinook project's daemon runs JDK 26, which AGP 9.0 doesn't support (its jlink-based JdkImageTransform
// fails). Pin this module's Java/Kotlin toolchain to 17 so AGP runs its tooling on a supported JDK.
kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material3)
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.fragment:fragment:1.8.5")

    implementation(project(":chinook-domain"))
    implementation(project(":chinook-domain-json"))
    implementation(project(":chinook-client-common"))
    implementation("is.codion:codion-android-framework-ui:$codionVersion")
    implementation("is.codion:codion-framework-db-http:${codionVersion}")
    implementation("is.codion:codion-framework-db-local:${codionVersion}")
    implementation("is.codion:codion-dbms-h2:${codionVersion}")
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
