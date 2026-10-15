package io.github.ntvien.liquidglass.sample

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ntvien.liquidglass.GlassDefaults
import io.github.ntvien.liquidglass.LiquidGlassScene
import io.github.ntvien.liquidglass.LiquidGlassTheme
import io.github.ntvien.liquidglass.component.LiquidSegmentedControl
import io.github.ntvien.liquidglass.component.LiquidSurface
import io.github.ntvien.liquidglass.component.LiquidTab
import io.github.ntvien.liquidglass.component.LiquidTabBar
import io.github.ntvien.liquidglass.component.LiquidTabBarDefaults
import io.github.ntvien.liquidglass.component.LiquidTabLabels
import kotlinx.coroutines.launch

/** One screen that shows every public piece of the library. */
@Composable
fun CatalogScreen() {
    val dark = isSystemInDarkTheme()
    val colors = LiquidGlassTheme.colors
    val tabs = listOf(
        LiquidTab("home", "Home", SampleIcons.Home),
        LiquidTab("explore", "Explore", SampleIcons.Compass, badge = true),
        LiquidTab("saved", "Saved", SampleIcons.Heart),
        LiquidTab("me", "Me", SampleIcons.Person),
    )
    var tab by remember { mutableStateOf("home") }
    var labels by remember { mutableIntStateOf(0) }
    var range by remember { mutableIntStateOf(0) }
    var likes by remember { mutableIntStateOf(0) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val title = TextStyle(fontSize = 32.sp, lineHeight = 38.sp, fontWeight = FontWeight.Bold, color = colors.content)
    val body = TextStyle(fontSize = 15.sp, lineHeight = 21.sp, color = colors.content)
    val muted = TextStyle(fontSize = 13.sp, lineHeight = 18.sp, color = colors.contentMuted)

    LiquidGlassScene(
        background = { GradientBackground(dark) },
        overlay = {
            LiquidTabBar(
                tabs = tabs,
                selectedKey = tab,
                onSelect = { tab = it },
                onReselect = { scope.launch { listState.animateScrollToItem(0) } },
                labels = if (labels == 0) LiquidTabLabels.Stacked else LiquidTabLabels.SelectedOnly,
                action = {
                    LiquidSurface(
                        modifier = Modifier.size(LiquidTabBarDefaults.Height),
                        style = GlassDefaults.chrome(),
                        shape = GlassDefaults.PillShape,
                        contentPadding = PaddingValues(20.dp),
                        onClick = { likes++ },
                        onClickLabel = "Add",
                    ) { SampleIcon(SampleIcons.Plus, colors.accent) }
                },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        },
    ) {
        val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = top + 16.dp, bottom = LiquidTabBarDefaults.contentBottomPadding()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { BasicText("Liquid Glass", style = title) }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    BasicText("Tab bar labels", style = muted)
                    LiquidSegmentedControl(listOf("Stacked", "Selected only"), labels, { labels = it })
                }
            }
            item {
                LiquidSurface(Modifier.fillMaxWidth(), onClick = { likes++ }, onClickLabel = "Like") {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        BasicText("Card glass", style = body.copy(fontWeight = FontWeight.SemiBold))
                        BasicText("Tap me: the glass swells and glints. Likes: $likes", style = muted)
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    LiquidSurface(
                        modifier = Modifier.weight(1f).height(56.dp),
                        style = GlassDefaults.control(),
                        shape = GlassDefaults.PillShape,
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        onClick = {},
                        onClickLabel = "Control",
                    ) { BasicText("Control", style = body, modifier = Modifier.align(Alignment.Center)) }
                    LiquidSurface(
                        modifier = Modifier.weight(1f).height(56.dp),
                        style = GlassDefaults.accent(),
                        shape = GlassDefaults.PillShape,
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        onClick = {},
                        onClickLabel = "Accent",
                    ) {
                        BasicText(
                            "Accent",
                            style = body.copy(color = GlassDefaults.accent().contentColor, fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    BasicText("Range", style = muted)
                    LiquidSegmentedControl(listOf("Day", "Week", "Month"), range, { range = it })
                }
            }
            item { BasicText("Quality: ${LiquidGlassTheme.quality} · Look: ${LiquidGlassTheme.look}", style = muted) }
            items(List(12) { it }) { i ->
                LiquidSurface(
                    Modifier.fillMaxWidth(),
                    style = GlassDefaults.frost(),
                    shape = GlassDefaults.ControlShape,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SampleIcon(SampleIcons.Heart, colors.contentMuted)
                        Spacer(Modifier.size(12.dp))
                        BasicText("Frost row ${i + 1}: lists never use real glass", style = body)
                    }
                }
            }
        }
    }
}

@Composable
private fun SampleIcon(icon: ImageVector, tint: Color) {
    Image(
        painter = rememberVectorPainter(icon),
        contentDescription = null,
        modifier = Modifier.size(24.dp),
        colorFilter = ColorFilter.tint(tint),
    )
}
