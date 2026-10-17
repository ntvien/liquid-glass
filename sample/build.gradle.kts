plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "io.github.ntvien.liquidglass.sample"
    compileSdk = 37

    defaultConfig {
        applicationId = "io.github.ntvien.liquidglass.sample"
        minSdk = 26
        targetSdk = 36
        versionCode = 3
        versionName = "0.3.0"
    }

    buildTypes {
        // R8 like a real app. Signed with the debug key: the sample is never published, and the benchmark build types
        // that the baselineprofile plugin derives from release (benchmarkRelease, nonMinifiedRelease) must be installable.
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":liquidglass"))
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.activity.compose)
    // Installs the library's baseline profile when the sample is sideloaded (benchmarks, testers).
    implementation(libs.androidx.profileinstaller)
    "baselineProfile"(project(":benchmark"))
    // Only for the Navigation example in guide/GuideSnippets.kt.
    implementation(libs.androidx.navigation.compose)
}
