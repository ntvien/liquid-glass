plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "io.github.ntvien.liquidglass.sample"
    compileSdk = 37

    defaultConfig {
        applicationId = "io.github.ntvien.liquidglass.sample"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
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
}
