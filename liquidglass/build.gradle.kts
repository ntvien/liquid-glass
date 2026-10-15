import kotlinx.validation.api.dump
import kotlinx.validation.api.filterOutNonPublic
import kotlinx.validation.api.loadApiFromJvmClasses

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
}

group = "io.github.ntvien"
version = "0.1.0"

android {
    namespace = "io.github.ntvien.liquidglass"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

kotlin {
    explicitApi()
}

// Public API dump (binary-compatibility-validator format), read from the output of compileReleaseKotlin.
// apiDump writes api/liquidglass.api; apiCheck (part of `check`) fails when the public API changed without a new dump.
// Resolved lazily: AGP registers compileReleaseKotlin after this script runs.
val releaseClasses: FileCollection = files({ tasks.named("compileReleaseKotlin").get().outputs.files }).builtBy("compileReleaseKotlin")
val apiFile = layout.projectDirectory.file("api/liquidglass.api")

fun renderApi(outputs: FileCollection): String = outputs.files.asSequence()
    .flatMap { it.walk() }
    .filter { it.isFile && it.extension == "class" }
    .sortedBy { it.invariantSeparatorsPath }
    .map { it.readBytes().inputStream() }
    .loadApiFromJvmClasses()
    .filterOutNonPublic()
    .dump(StringBuilder())
    .toString()

val apiDump = tasks.register("apiDump") {
    group = "verification"
    description = "Writes the public API of the library to api/liquidglass.api."
    inputs.files(releaseClasses)
    outputs.file(apiFile)
    doLast {
        val out = apiFile.asFile
        out.parentFile.mkdirs()
        out.writeText(renderApi(releaseClasses))
    }
}

val apiCheck = tasks.register("apiCheck") {
    group = "verification"
    description = "Fails when the public API differs from api/liquidglass.api."
    inputs.files(releaseClasses)
    inputs.file(apiFile)
    doLast {
        if (apiFile.asFile.readText() != renderApi(releaseClasses)) {
            throw GradleException("Public API changed. Review it, run ./gradlew :liquidglass:apiDump and commit api/liquidglass.api.")
        }
    }
}

tasks.named("check") { dependsOn(apiCheck) }

dependencies {
    api(platform(libs.compose.bom))
    api(libs.compose.ui)
    api(libs.compose.ui.graphics)
    api(libs.compose.foundation)
    implementation(libs.compose.animation)
    implementation(libs.kyant.shapes)
    // BackHandler: the system Back gesture closes LiquidMenu.
    implementation(libs.androidx.activity.compose)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "io.github.ntvien"
            artifactId = "liquidglass"
            version = project.version.toString()
            afterEvaluate { from(components["release"]) }
            pom {
                name.set("Liquid Glass for Jetpack Compose")
                description.set("Liquid glass surfaces, tab bar and segmented control for Jetpack Compose, with device-aware quality tiers.")
                url.set("https://github.com/ntvien/liquid-glass")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
            }
        }
    }
}
