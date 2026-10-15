package io.github.ntvien.liquidglass.sample.guide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.ntvien.liquidglass.GlassColors
import io.github.ntvien.liquidglass.GlassDefaults
import io.github.ntvien.liquidglass.GlassLook
import io.github.ntvien.liquidglass.GlassQuality
import io.github.ntvien.liquidglass.LiquidGlassScene
import io.github.ntvien.liquidglass.LiquidGlassTheme
import io.github.ntvien.liquidglass.component.LiquidMenuButton
import io.github.ntvien.liquidglass.component.LiquidMenuDivider
import io.github.ntvien.liquidglass.component.LiquidMenuItem
import io.github.ntvien.liquidglass.component.LiquidSegmentedControl
import io.github.ntvien.liquidglass.component.LiquidSurface
import io.github.ntvien.liquidglass.component.LiquidTab
import io.github.ntvien.liquidglass.component.LiquidTabBar
import io.github.ntvien.liquidglass.component.LiquidTabBarDefaults
import io.github.ntvien.liquidglass.component.LiquidTabLabels
import io.github.ntvien.liquidglass.liquidGlass
import io.github.ntvien.liquidglass.rememberGlassInteraction
import io.github.ntvien.liquidglass.sample.GradientBackground
import io.github.ntvien.liquidglass.sample.SampleIcons
import kotlinx.coroutines.launch

/*
 * Every code example of README.md / README.vi.md lives here so it is compiled with the sample.
 * Section names match the README headings. Not shown in the sample UI.
 */

private val body = TextStyle(color = Color(0xFF1D1B24))
private val homeIcon: ImageVector = SampleIcons.Home
private val searchIcon: ImageVector = SampleIcons.Compass
private val heartIcon: ImageVector = SampleIcons.Heart
private val personIcon: ImageVector = SampleIcons.Person
private val plusIcon: ImageVector = SampleIcons.Plus

// ---------------------------------------------------------------- 1. Set up the theme

class GuideActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge() // content draws behind the system bars; the glass refracts it
        super.onCreate(savedInstanceState)
        setContent {
            LiquidGlassTheme { // light/dark colors follow the system by default
                AppRoot()
            }
        }
    }
}

@Composable
private fun AppRoot() = BasicScreen()

// ---------------------------------------------------------------- 2. Brand colors

@Composable
fun BrandTheme(dark: Boolean, content: @Composable () -> Unit) {
    val brandBlue = Color(0xFF0A84FF)
    val colors = if (dark) {
        GlassColors.dark().copy(accent = brandBlue, accentSoft = brandBlue.copy(alpha = 0.2f))
    } else {
        GlassColors.light().copy(
            accent = brandBlue,
            accentSoft = brandBlue.copy(alpha = 0.16f),
            selectedFill = brandBlue,
            selectedContent = Color.White,
        )
    }
    LiquidGlassTheme(colors = colors, content = content)
}

// ---------------------------------------------------------------- 3. Look and "Reduce effects"

@Composable
fun SettingsAwareTheme(reduceEffects: Boolean, content: @Composable () -> Unit) {
    LiquidGlassTheme(
        look = GlassLook.Auto,          // Rich on high-end devices, Optimized elsewhere
        reduceEffects = reduceEffects,  // from your settings screen: true = frosted glass everywhere
        overlaySamplesContent = null,   // null: tab bar and menus refract the scrolling content on Android 13+
        content = content,
    )
}

// ---------------------------------------------------------------- 4. A screen with glass

@Composable
fun BasicScreen() {
    LiquidGlassScene(
        background = { GradientBackground(dark = false) }, // see sample/SampleArt.kt
    ) {
        Column(
            Modifier.fillMaxSize().statusBarsPadding().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LiquidSurface(Modifier.fillMaxWidth()) {
                BasicText("Hello glass", style = body)
            }
        }
    }
}

@Composable
fun PhotoScreen(wallpaper: Painter) {
    LiquidGlassScene(
        background = {
            Image(wallpaper, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        },
    ) {
        LiquidSurface(Modifier.statusBarsPadding().padding(16.dp).fillMaxWidth()) {
            BasicText("Glass over a photo", style = body)
        }
    }
}

// ---------------------------------------------------------------- 5. Glass blocks

@Composable
fun GlassBlocks(onOpen: () -> Unit, onSave: () -> Unit, onAdd: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Card (static)
        LiquidSurface(Modifier.fillMaxWidth()) {
            BasicText("Today", style = body)
        }

        // Pressable card: swells, tilts and glints on touch
        LiquidSurface(Modifier.fillMaxWidth(), onClick = onOpen, onClickLabel = "Open details") {
            BasicText("Tap me", style = body)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Secondary button
            LiquidSurface(
                modifier = Modifier.weight(1f).height(52.dp),
                style = GlassDefaults.control(),
                shape = GlassDefaults.PillShape,
                contentPadding = PaddingValues(horizontal = 20.dp),
                onClick = onOpen,
            ) { BasicText("Later", style = body, modifier = Modifier.align(Alignment.Center)) }

            // Primary button in the accent color
            val accent = GlassDefaults.accent()
            LiquidSurface(
                modifier = Modifier.weight(1f).height(52.dp),
                style = accent,
                shape = GlassDefaults.PillShape,
                contentPadding = PaddingValues(horizontal = 20.dp),
                onClick = onSave,
            ) {
                BasicText("Save", style = TextStyle(color = accent.contentColor), modifier = Modifier.align(Alignment.Center))
            }

            // Round icon button (48 dp touch target)
            LiquidSurface(
                modifier = Modifier.size(52.dp),
                style = GlassDefaults.control(),
                shape = GlassDefaults.PillShape,
                contentPadding = PaddingValues(14.dp),
                onClick = onAdd,
                onClickLabel = "Add",
            ) { Image(plusIcon, contentDescription = null) }
        }
    }
}

@Composable
fun CustomStyle() {
    // Start from a preset and change only what you need.
    val thickGlass = GlassDefaults.card().copy(lensHeight = 20.dp, lensAmount = 32.dp)
    LiquidSurface(Modifier.fillMaxWidth(), style = thickGlass) {
        BasicText("Thicker lens", style = body)
    }
}

@Composable
fun AnyComposableAsGlass() {
    // Any element can be glass: the modifier draws glass behind it.
    Box(
        Modifier
            .size(120.dp)
            .liquidGlass(GlassDefaults.control(), GlassDefaults.CardShape, rememberGlassInteraction()),
    )
}

@Composable
fun FrostList(items: List<String>) {
    // Long lists: frosted rows (never sample the background), one real glass block at most around the group.
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items) { item ->
            LiquidSurface(Modifier.fillMaxWidth(), style = GlassDefaults.frost(), shape = GlassDefaults.ControlShape) {
                BasicText(item, style = body)
            }
        }
    }
}

// ---------------------------------------------------------------- 6. Tab bar

@Composable
fun TabBarScreen() {
    var tab by rememberSaveable { mutableStateOf("home") }
    val homeList = rememberLazyListState()
    val scope = rememberCoroutineScope()
    LiquidGlassScene(
        background = { GradientBackground(dark = false) },
        overlay = {
            LiquidTabBar(
                tabs = listOf(
                    LiquidTab("home", "Home", homeIcon),
                    LiquidTab("search", "Search", searchIcon),
                    LiquidTab("saved", "Saved", heartIcon, badge = true),
                    LiquidTab("me", "Me", personIcon),
                ),
                selectedKey = tab,
                onSelect = { tab = it },
                onReselect = { if (it == "home") scope.launch { homeList.animateScrollToItem(0) } },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        },
    ) {
        when (tab) {
            "home" -> HomeList(homeList)
            else -> Box(Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun HomeList(state: LazyListState) {
    val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    LazyColumn(
        state = state,
        // The last item ends above the floating bar.
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = top + 16.dp, bottom = LiquidTabBarDefaults.contentBottomPadding()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(List(30) { "Item $it" }) { BasicText(it, style = body) }
    }
}

@Composable
fun TabBarSelectedOnlyWithAction(onAdd: () -> Unit) {
    var tab by rememberSaveable { mutableStateOf("home") }
    LiquidGlassScene(
        background = { GradientBackground(dark = false) },
        overlay = {
            LiquidTabBar(
                tabs = listOf(
                    LiquidTab("home", "Home", homeIcon),
                    LiquidTab("saved", "Saved", heartIcon),
                    LiquidTab("me", "Me", personIcon),
                ),
                selectedKey = tab,
                onSelect = { tab = it },
                labels = LiquidTabLabels.SelectedOnly,
                action = {
                    LiquidSurface(
                        modifier = Modifier.size(LiquidTabBarDefaults.Height),
                        style = GlassDefaults.chrome(),
                        shape = GlassDefaults.PillShape,
                        contentPadding = PaddingValues(20.dp),
                        onClick = onAdd,
                        onClickLabel = "Add",
                    ) { Image(plusIcon, contentDescription = null) }
                },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        },
    ) {}
}

@Composable
fun TabBarWithNavigation() {
    val nav = rememberNavController()
    val route = nav.currentBackStackEntryAsState().value?.destination?.route ?: "home"
    LiquidGlassScene(
        background = { GradientBackground(dark = false) },
        overlay = {
            LiquidTabBar(
                tabs = listOf(LiquidTab("home", "Home", homeIcon), LiquidTab("me", "Me", personIcon)),
                selectedKey = route,
                onSelect = { key ->
                    nav.navigate(key) {
                        popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        },
    ) {
        NavHost(nav, startDestination = "home") {
            composable("home") { Box(Modifier.fillMaxSize()) }
            composable("me") { Box(Modifier.fillMaxSize()) }
        }
    }
}

// ---------------------------------------------------------------- 7. Segmented control

@Composable
fun SegmentedWithContent() {
    var range by rememberSaveable { mutableIntStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        LiquidSegmentedControl(listOf("Day", "Week", "Month"), range, { range = it })
        AnimatedContent(
            targetState = range,
            transitionSpec = {
                val direction = if (targetState > initialState) 1 else -1
                (slideInHorizontally(tween(280)) { it / 6 * direction } + fadeIn(tween(280)))
                    .togetherWith(fadeOut(tween(160)))
            },
            label = "range",
        ) { selected ->
            BasicText(listOf("Today", "This week", "This month")[selected], style = body)
        }
    }
}

@Composable
fun AccentSegmented(options: List<String>) {
    var range by rememberSaveable { mutableIntStateOf(0) }
    // An accent pill instead of the default light glass pill.
    LiquidSegmentedControl(
        options, range, { range = it },
        indicatorColor = LiquidGlassTheme.colors.accent,
        selectedTextColor = LiquidGlassTheme.colors.onAccent,
    )
}

// ---------------------------------------------------------------- 8. Adapt to the device

@Composable
fun QualityAware() {
    // Example: skip a decorative animation when the device draws frosted glass only.
    val showSparkles = LiquidGlassTheme.quality != GlassQuality.Off
    if (showSparkles) {
        Box(Modifier.size(24.dp))
    }
}

// ---------------------------------------------------------------- 7b. Menu

@Composable
fun MoreMenu(onRename: () -> Unit, onShare: () -> Unit, onDelete: () -> Unit) {
    LiquidMenuButton(
        items = listOf(
            LiquidMenuItem("Rename", SampleIcons.Edit, onClick = onRename),
            LiquidMenuItem("Share", SampleIcons.Share, onClick = onShare),
            LiquidMenuDivider,
            LiquidMenuItem("Delete", SampleIcons.Trash, destructive = true, onClick = onDelete),
        ),
        contentDescription = "More",
        modifier = Modifier.size(44.dp),
    ) { Image(SampleIcons.More, contentDescription = null) }
}
