package io.github.ntvien.liquidglass.benchmark

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until

/** Package of the sample app that the benchmarks drive. */
internal const val TARGET_PACKAGE = "io.github.ntvien.liquidglass.sample"

// Generous: a cold start without compilation is slow on emulators and low-end devices.
private const val TIMEOUT_MS = 15_000L

/**
 * Cold start from the launcher, waiting until the catalog title is drawn.
 * [contentSampling] forces whether floating glass refracts the scrolling content; [look] forces the glass look
 * ("Rich", "Optimized"). null = library default. [glassOff] draws the frosted fallback instead of glass.
 */
internal fun MacrobenchmarkScope.launchCatalog(
    contentSampling: Boolean? = null,
    look: String? = null,
    glassOff: Boolean = false,
) {
    // A freshly booted device may sit on the lock screen; the activity would start behind it.
    device.wakeUp()
    device.executeShellCommand("wm dismiss-keyguard")
    pressHome()
    startActivityAndWait { intent ->
        contentSampling?.let { intent.putExtra("overlaySamplesContent", it) }
        look?.let { intent.putExtra("look", it) }
        if (glassOff) intent.putExtra("reduceEffects", true)
    }
    find(By.text("Liquid Glass"))
}

internal fun MacrobenchmarkScope.find(selector: BySelector): UiObject2 =
    device.wait(Until.findObject(selector), TIMEOUT_MS) ?: error("Not found on screen: $selector")

/** A control by its accessibility label (content description), falling back to its visible text. */
internal fun MacrobenchmarkScope.findLabeled(label: String): UiObject2 =
    device.wait(Until.findObject(By.desc(label)), TIMEOUT_MS / 3)
        ?: device.wait(Until.findObject(By.text(label)), TIMEOUT_MS / 3)
        ?: error("Not found on screen: $label")

/** Scrolls the catalog list down and back up, like a person reading it. */
internal fun MacrobenchmarkScope.scrollCatalog(times: Int = 2) {
    val list = find(By.scrollable(true))
    // Keep gestures away from the system gesture areas and the floating tab bar.
    list.setGestureMarginPercentage(0.2f)
    repeat(times) {
        list.fling(Direction.DOWN)
        device.waitForIdle()
    }
    repeat(times) {
        list.fling(Direction.UP)
        device.waitForIdle()
    }
}

/** Taps every tab of the tab bar, then drags the droplet back across the bar. */
internal fun MacrobenchmarkScope.switchTabs() {
    listOf("Explore", "Saved", "Me", "Home").forEach { label ->
        find(By.desc(label)).click()
        device.waitForIdle()
    }
    val first = find(By.desc("Home")).visibleCenter
    val last = find(By.desc("Me")).visibleCenter
    device.swipe(first.x, first.y, last.x, last.y, 40)
    device.waitForIdle()
    device.swipe(last.x, last.y, first.x, first.y, 40)
    device.waitForIdle()
}

/** Switches the tab bar label style and the range segmented control. */
internal fun MacrobenchmarkScope.switchSegments() {
    listOf("Selected only", "Stacked", "Week", "Month", "Day").forEach { label ->
        findLabeled(label).click()
        device.waitForIdle()
    }
}
