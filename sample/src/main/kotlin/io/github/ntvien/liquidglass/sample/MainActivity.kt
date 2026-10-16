package io.github.ntvien.liquidglass.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.ntvien.liquidglass.GlassLook
import io.github.ntvien.liquidglass.LiquidGlassTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        // Benchmarks can force the glass look, the content sampling of floating glass and glass off (default: library
        // decides).
        val overlaySamplesContent = if (intent.hasExtra(EXTRA_OVERLAY_SAMPLES_CONTENT)) {
            intent.getBooleanExtra(EXTRA_OVERLAY_SAMPLES_CONTENT, true)
        } else {
            null
        }
        val look = intent.getStringExtra(EXTRA_LOOK)?.let { GlassLook.valueOf(it) } ?: GlassLook.Auto
        val reduceEffects = intent.getBooleanExtra(EXTRA_REDUCE_EFFECTS, false)
        setContent {
            LiquidGlassTheme(
                look = look,
                reduceEffects = reduceEffects,
                overlaySamplesContent = overlaySamplesContent,
            ) {
                CatalogScreen()
            }
        }
    }
}

/** Intent extra read by [MainActivity]; set by the benchmark module. */
const val EXTRA_OVERLAY_SAMPLES_CONTENT = "overlaySamplesContent"

/** Intent extra read by [MainActivity]: a [GlassLook] name (Rich, Optimized); absent = Auto. */
const val EXTRA_LOOK = "look"

/** Intent extra read by [MainActivity]: `true` draws the frosted fallback (GlassQuality.Off), a benchmark control. */
const val EXTRA_REDUCE_EFFECTS = "reduceEffects"
