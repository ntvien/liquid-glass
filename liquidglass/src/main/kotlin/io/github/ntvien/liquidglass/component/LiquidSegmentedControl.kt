package io.github.ntvien.liquidglass.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ntvien.liquidglass.DropletState
import io.github.ntvien.liquidglass.GlassDefaults
import io.github.ntvien.liquidglass.LiquidGlassTheme
import io.github.ntvien.liquidglass.droplet
import io.github.ntvien.liquidglass.liquidGlass
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * In-screen tabs for 2–4 short options ("Day · Week · Month").
 *
 * A frosted pill track (cheap, it lives in the content) with the selected option shown as the same water droplet as
 * [LiquidTabBar]: tap and it slides over with a small bounce, drag sideways and it follows the finger. Labels
 * cross-fade by droplet position, a light haptic tick confirms the change. Accessibility services see a group of
 * selectable tabs.
 *
 * For more than 4 options or long labels use a horizontally scrolling row of chips instead.
 *
 * @param options visible labels, also read by accessibility services.
 * @param selectedIndex index of the selected option.
 * @param onSelect called with the new index (not called when the selected option is tapped again).
 * @param textStyle label style; color comes from [LiquidGlassTheme.colors].
 */
@Composable
public fun LiquidSegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(fontSize = 13.sp, lineHeight = 17.sp, fontWeight = FontWeight.SemiBold),
) {
    val colors = LiquidGlassTheme.colors
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val currentSelected by rememberUpdatedState(selectedIndex)
    val currentOnSelect by rememberUpdatedState(onSelect)
    val scope = rememberCoroutineScope()
    val droplet = remember(scope, options.size) {
        DropletState(scope, selectedIndex.toFloat(), 0f..(options.size - 1).coerceAtLeast(0).toFloat(), pressedScale = PRESS_SCALE)
    }
    LaunchedEffect(selectedIndex) {
        if (droplet.targetValue.roundToInt() != selectedIndex) droplet.animateTo(selectedIndex.toFloat())
    }
    // Only reads state through rememberUpdatedState: the droplet's pointerInput keeps this lambda.
    val choose: (Int) -> Unit = { i ->
        if (i != currentSelected) {
            haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
            currentOnSelect(i)
        }
    }
    val slot = remember { FloatArray(1) }
    var slotPx by remember { mutableFloatStateOf(0f) }

    Box(
        modifier
            .fillMaxWidth()
            .height(SEGMENTED_HEIGHT)
            .liquidGlass(GlassDefaults.frost(), GlassDefaults.PillShape)
            .padding(4.dp)
            .onSizeChanged {
                slot[0] = it.width.toFloat() / options.size.coerceAtLeast(1)
                slotPx = slot[0]
            }
            .then(droplet.dragModifier(slotWidthPx = { slot[0] }, onSettle = choose))
            .selectableGroup(),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            Modifier
                .offset { IntOffset((droplet.value * slot[0]).roundToInt(), 0) }
                .width(with(density) { slotPx.toDp() })
                .fillMaxHeight()
                .droplet(droplet, GlassDefaults.PillShape, idleColor = colors.selectedFill),
        )
        Row(Modifier.fillMaxSize()) {
            options.forEachIndexed { index, label ->
                val highlight = { (1f - abs(droplet.value - index)).coerceIn(0f, 1f) }
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .selectable(
                            selected = index == selectedIndex,
                            interactionSource = null,
                            indication = null,
                            role = Role.Tab,
                            onClick = { choose(index) },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    // Two label layers cross-fade with the droplet; the second is hidden from accessibility services.
                    BasicText(
                        label,
                        style = textStyle.copy(color = colors.content),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.graphicsLayer { alpha = 1f - highlight() },
                    )
                    BasicText(
                        label,
                        style = textStyle.copy(color = colors.selectedContent),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.graphicsLayer { alpha = highlight() }.clearAndSetSemantics {},
                    )
                }
            }
        }
    }
}

private val SEGMENTED_HEIGHT = 40.dp
private const val PRESS_SCALE = 1.06f
