package io.github.ntvien.liquidglass.sample

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Background the glass refracts: a base color and soft color blobs, drawn ONCE into a quarter-resolution bitmap
 * (smooth gradients upscale invisibly). Redrawing a full-screen gradient every frame would repaint it behind every glass element.
 */
@Composable
fun GradientBackground(dark: Boolean, modifier: Modifier = Modifier) {
    val base = if (dark) Color(0xFF121119) else Color(0xFFF4F1EB)
    // Strong enough blobs (35–60 %) that the lens has something to bend; a pale background makes glass look like plastic.
    val blobs = if (dark) {
        listOf(
            Blob(Color(0x738EA2FF), 0.08f, 0.1f, 0.85f),
            Blob(Color(0x66FFC24D), 0.95f, 0.32f, 0.7f),
            Blob(Color(0x59FF7A6B), 0.2f, 0.62f, 0.75f),
            Blob(Color(0x4D40C29B), 0.85f, 0.92f, 0.8f),
        )
    } else {
        listOf(
            Blob(Color(0x8C3B57D6), 0.08f, 0.1f, 0.85f),
            Blob(Color(0x99FFC24D), 0.95f, 0.32f, 0.7f),
            Blob(Color(0x73FF7A6B), 0.2f, 0.62f, 0.75f),
            Blob(Color(0x5940C29B), 0.85f, 0.92f, 0.8f),
        )
    }
    Spacer(
        modifier
            .fillMaxSize()
            .drawWithCache {
                val w = (size.width / DOWNSCALE).roundToInt().coerceAtLeast(1)
                val h = (size.height / DOWNSCALE).roundToInt().coerceAtLeast(1)
                val bitmap = ImageBitmap(w, h)
                CanvasDrawScope().draw(this, layoutDirection, Canvas(bitmap), Size(w.toFloat(), h.toFloat())) {
                    drawRect(base)
                    blobs.forEach { b ->
                        val center = Offset(b.x * w, b.y * h)
                        val radius = b.radius * w
                        val brush = Brush.radialGradient(
                            0f to b.color,
                            0.55f to b.color.copy(alpha = b.color.alpha * 0.6f),
                            1f to b.color.copy(alpha = 0f),
                            center = center,
                            radius = radius,
                        )
                        drawCircle(brush, radius, center)
                    }
                }
                val dst = IntSize(size.width.roundToInt(), size.height.roundToInt())
                onDrawBehind { drawImage(bitmap, IntOffset.Zero, IntSize(w, h), dstSize = dst, filterQuality = FilterQuality.Low) }
            },
    )
}

private class Blob(val color: Color, val x: Float, val y: Float, val radius: Float)

private const val DOWNSCALE = 4f

/** Minimal 24 dp outline icons so the sample does not depend on Material icons. */
object SampleIcons {
    val Home: ImageVector = icon("home") {
        moveTo(4f, 10.5f); lineTo(12f, 4f); lineTo(20f, 10.5f)
        moveTo(6f, 9f); lineTo(6f, 20f); lineTo(18f, 20f); lineTo(18f, 9f)
    }
    val Compass: ImageVector = icon("compass") {
        moveTo(21f, 12f); arcTo(9f, 9f, 0f, true, true, 3f, 12f); arcTo(9f, 9f, 0f, true, true, 21f, 12f); close()
        moveTo(15.5f, 8.5f); lineTo(13.5f, 13.5f); lineTo(8.5f, 15.5f); lineTo(10.5f, 10.5f); close()
    }
    val Heart: ImageVector = icon("heart") {
        moveTo(12f, 20f)
        curveTo(5f, 15.5f, 3f, 12f, 3f, 8.8f)
        curveTo(3f, 6.2f, 5f, 4.5f, 7.3f, 4.5f)
        curveTo(9.2f, 4.5f, 11f, 5.7f, 12f, 7.4f)
        curveTo(13f, 5.7f, 14.8f, 4.5f, 16.7f, 4.5f)
        curveTo(19f, 4.5f, 21f, 6.2f, 21f, 8.8f)
        curveTo(21f, 12f, 19f, 15.5f, 12f, 20f)
        close()
    }
    val Person: ImageVector = icon("person") {
        moveTo(16f, 8f); arcTo(4f, 4f, 0f, true, true, 8f, 8f); arcTo(4f, 4f, 0f, true, true, 16f, 8f); close()
        moveTo(4.5f, 20f); curveTo(5.5f, 16.5f, 8.5f, 14.5f, 12f, 14.5f); curveTo(15.5f, 14.5f, 18.5f, 16.5f, 19.5f, 20f)
    }
    val Plus: ImageVector = icon("plus") {
        moveTo(12f, 5f); lineTo(12f, 19f)
        moveTo(5f, 12f); lineTo(19f, 12f)
    }
    val More: ImageVector = icon("more") {
        moveTo(6f, 12f); lineTo(6.01f, 12f)
        moveTo(12f, 12f); lineTo(12.01f, 12f)
        moveTo(18f, 12f); lineTo(18.01f, 12f)
    }
    val Edit: ImageVector = icon("edit") {
        moveTo(4f, 20f); lineTo(8f, 19f); lineTo(19f, 8f); lineTo(16f, 5f); lineTo(5f, 16f); close()
    }
    val Share: ImageVector = icon("share") {
        moveTo(12f, 15f); lineTo(12f, 4f)
        moveTo(8f, 8f); lineTo(12f, 4f); lineTo(16f, 8f)
        moveTo(6f, 12f); lineTo(6f, 20f); lineTo(18f, 20f); lineTo(18f, 12f)
    }
    val Trash: ImageVector = icon("trash") {
        moveTo(4f, 7f); lineTo(20f, 7f)
        moveTo(9f, 7f); lineTo(9f, 4f); lineTo(15f, 4f); lineTo(15f, 7f)
        moveTo(6f, 7f); lineTo(7f, 20f); lineTo(17f, 20f); lineTo(18f, 7f)
    }

    private fun icon(name: String, block: PathBuilder.() -> Unit): ImageVector =
        ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                pathBuilder = block,
            )
        }.build()
}
