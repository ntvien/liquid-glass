// Macrobenchmarks of the sample app (startup, scrolling, tab bar, segmented control) and the generator of the
// library's baseline profile. Runs on a connected device or emulator; see README "Performance".
plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "io.github.ntvien.liquidglass.benchmark"
    compileSdk = 37

    defaultConfig {
        minSdk = 28
        targetSdk = 36
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Emulator numbers are only a smoke test; real measurements come from a physical mid-range device.
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "EMULATOR,LOW-BATTERY"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    targetProjectPath = ":sample"
    // The test runs in its own process, so Macrobenchmark can kill and cold-start the sample.
    experimentalProperties["android.experimental.self-instrumenting"] = true
}

baselineProfile {
    // Use the device(s) selected with ANDROID_SERIAL instead of Gradle managed devices.
    useConnectedDevices = true
}

dependencies {
    implementation(libs.androidx.test.ext.junit)
    implementation(libs.androidx.test.uiautomator)
    implementation(libs.androidx.benchmark.macro.junit4)
}
