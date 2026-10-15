/*
 * Added in liquid-glass (not in the original library). See NOTICE.
 */
package io.github.ntvien.liquidglass.internal.backdrop.internal

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.LayoutDirection

/**
 * Tells whether a recorded shadow / highlight layer can be reused. The original re-recorded the layer on every draw
 * (a blurred shadow costs both when recording and when the GPU rebuilds it); now it records only when the size,
 * shape or parameters change.
 */
internal data class RecordKey(
    val size: Size,
    val density: Float,
    val layoutDirection: LayoutDirection,
    val shape: Shape,
    val params: Any,
)
