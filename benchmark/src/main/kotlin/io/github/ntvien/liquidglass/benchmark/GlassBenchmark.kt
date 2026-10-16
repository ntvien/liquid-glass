package io.github.ntvien.liquidglass.benchmark

import android.os.Build
import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Smoothness of the glass on the sample catalog (frame timing): scrolling under the tab bar, switching tabs and
 * dragging the droplet, switching segments. Target: under 10 % janky frames with GlassQuality.Full on a mid-range
 * device. Janky = frameOverrunMs > 0 (the frame missed its deadline).
 */
@RunWith(AndroidJUnit4::class)
class GlassBenchmark {
    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun scrollCatalog() = measure { scrollCatalog() }

    @Test
    fun switchTabsAndDragDroplet() = measure { switchTabs() }

    @Test
    fun switchSegments() = measure { switchSegments() }

    // Android 13+ only (GlassQuality.Full): separate the cost of the Rich look from the cost of the tab bar
    // refracting the scrolling content. Auto resolves to Rich only when the device declares Media Performance
    // Class 13+, so both looks are forced here to compare them on any device.

    @Test
    fun scrollCatalogWithoutContentSampling() = full { measure(contentSampling = false) { scrollCatalog() } }

    @Test
    fun switchTabsWithoutContentSampling() = full { measure(contentSampling = false) { switchTabs() } }

    @Test
    fun switchSegmentsWithoutContentSampling() = full { measure(contentSampling = false) { switchSegments() } }

    @Test
    fun scrollCatalogOptimizedLook() = full { measure(look = "Optimized") { scrollCatalog() } }

    @Test
    fun switchTabsOptimizedLook() = full { measure(look = "Optimized") { switchTabs() } }

    @Test
    fun switchSegmentsOptimizedLook() = full { measure(look = "Optimized") { switchSegments() } }

    @Test
    fun scrollCatalogRichLook() = full { measure(look = "Rich") { scrollCatalog() } }

    @Test
    fun switchTabsRichLook() = full { measure(look = "Rich") { switchTabs() } }

    @Test
    fun switchSegmentsRichLook() = full { measure(look = "Rich") { switchSegments() } }

    // Control: the same screens with glass off (frosted fallback), Android 12+ where glass is otherwise drawn. The
    // difference to the default cases is the cost of the glass itself.

    @Test
    fun scrollCatalogGlassOff() = glassDrawn { measure(glassOff = true) { scrollCatalog() } }

    @Test
    fun switchTabsGlassOff() = glassDrawn { measure(glassOff = true) { switchTabs() } }

    @Test
    fun switchSegmentsGlassOff() = glassDrawn { measure(glassOff = true) { switchSegments() } }

    private fun glassDrawn(block: () -> Unit) {
        assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        block()
    }

    private fun full(block: () -> Unit) {
        assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        block()
    }

    private fun measure(
        contentSampling: Boolean? = null,
        look: String? = null,
        glassOff: Boolean = false,
        block: MacrobenchmarkScope.() -> Unit,
    ) = rule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.Partial(BaselineProfileMode.Require),
        startupMode = StartupMode.WARM,
        iterations = 5,
        setupBlock = { launchCatalog(contentSampling, look, glassOff) },
        measureBlock = block,
    )
}
