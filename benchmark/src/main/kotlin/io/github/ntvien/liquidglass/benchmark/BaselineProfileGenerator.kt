package io.github.ntvien.liquidglass.benchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generates the baseline profile shipped in the library AAR:
 * `ANDROID_SERIAL=<device> ./gradlew :liquidglass:generateBaselineProfile`
 * (the library keeps only `io.github.ntvien.liquidglass.**`, see liquidglass/build.gradle.kts).
 * Covers startup and every component of the catalog, so their code is compiled ahead of time on install.
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {
    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() = rule.collect(packageName = TARGET_PACKAGE, includeInStartupProfile = true) {
        launchCatalog()
        scrollCatalog(times = 1)
        switchTabs()
        switchSegments()
    }
}
