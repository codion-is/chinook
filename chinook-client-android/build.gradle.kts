plugins {
    // AGP 9 has built-in Kotlin support, so org.jetbrains.kotlin.android is not applied.
    id("com.android.application") version "9.1.0"
    // The compose compiler, versioned with Kotlin. Not org.jetbrains.compose: this app is Android-only, and its Compose
    // artifacts come from the compose-bom that codion-android-framework-ui exports, as they do in that library itself.
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.10"
}

android {
    namespace = "is.codion.demos.chinook.android"
    // 37 because codion-android-framework-ui's AAR now declares minCompileSdk=37, which AGP enforces at
    // checkDebugAarMetadata. Unrelated to minSdk below: this is only what the app compiles against.
    compileSdk = 37

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

// The chinook project's daemon runs JDK 26, which AGP 9 doesn't support (its jlink-based JdkImageTransform
// fails). Pin this module's Java/Kotlin toolchain to 21 so AGP runs its tooling on a supported JDK.
kotlin {
    jvmToolchain(21)
}

dependencies {
    // The codion-* catalog entries carry no version of their own, the BOM supplies them. The root build applies it
    // to every other module, but has to skip this one — AGP creates the configurations it would attach to only once
    // this script is evaluated — so it is applied here instead.
    implementation(platform(libs.codion.framework.bom))

    // Deliberately versionless: codion-android-framework-ui api-exports androidx.compose:compose-bom (scope=import in
    // its POM), so the app resolves the same Compose set the library was built against without repeating the number.
    // Declared rather than inherited because this app imports all four directly.
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    // Outside the BOM's scope (it covers androidx.compose.* only), so these carry their own versions. Used directly:
    // setContent, SystemBarStyle and enableEdgeToEdge from activity; FragmentActivity from fragment, which
    // BiometricPrompt needs as its host.
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.fragment:fragment:1.9.0")

    implementation(project(":chinook-domain"))
    implementation(project(":chinook-domain-json"))
    implementation(project(":chinook-client-common"))
    implementation(libs.codion.android.framework.ui)
    implementation(libs.codion.framework.db.http)
    implementation(libs.codion.framework.db.local)
    // The on-device database (DeviceDatabase) with the H2 dialect; the driver is the app's, as with any Codion dbms module.
    implementation(libs.codion.android.dbms.h2)
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
