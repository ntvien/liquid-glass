buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        // Core of binary-compatibility-validator, used by :liquidglass:apiDump / apiCheck. The Gradle plugin itself
        // does not see AGP 9's built-in Kotlin, so the tasks are registered in liquidglass/build.gradle.kts.
        classpath(libs.binary.compatibility.validator)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
