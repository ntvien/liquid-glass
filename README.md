# Liquid Glass for Jetpack Compose

Liquid glass surfaces, a floating tab bar and a segmented control for Android, built on Jetpack Compose. The glass
refracts what is behind it (lens, chromatic aberration, highlight), reacts to touch (swell, tilt, glint) and adapts to
the device: full glass on Android 13+, lighter glass on Android 12, a cheap frosted look on older or low-RAM devices and
in Battery Saver.

Tiếng Việt: [README.vi.md](README.vi.md)

| Stacked tab labels | Selected-only tab labels |
|---|---|
| ![Stacked](docs/sample-stacked.png) | ![Selected only](docs/sample-selected-only.png) |

*Sample app on the Android 16 emulator.*

- [Install](#install)
- [Quick start](#quick-start)
- [Guide](#guide): theme · brand colors · reduce effects · screens · glass blocks · tab bar · segmented control · device tiers · do and don't · troubleshooting
- [API](#api) · [Device tiers](#device-tiers) · [Performance rules](#performance-rules) · [Accessibility](#accessibility)

## Install

Add JitPack to `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

Then the dependency:

```kotlin
dependencies {
    implementation("com.github.ntvien:liquid-glass:v0.1.0")
}
```

JitPack uses the git tag as the version, so the `v` is part of it. Releases: [GitHub tags](https://github.com/ntvien/liquid-glass/tags).

Requirements: minSdk 26, compileSdk 37, Jetpack Compose (BOM 2026.09.00 or newer), Kotlin 2.4 or newer.

## Quick start

Three steps: wrap the app in `LiquidGlassTheme`, put each screen in a `LiquidGlassScene`, use glass components inside.

```kotlin
LiquidGlassTheme {
    LiquidGlassScene(background = { MyBackground() }) {
        LiquidSurface(Modifier.statusBarsPadding().padding(16.dp).fillMaxWidth()) {
            BasicText("Hello glass")
        }
    }
}
```

The `sample` module shows every piece on one screen. Every snippet in this guide is compiled in
[`sample/…/guide/GuideSnippets.kt`](sample/src/main/kotlin/io/github/ntvien/liquidglass/sample/guide/GuideSnippets.kt).

## Guide

### 1. Set up the theme

Call `enableEdgeToEdge()` so content draws behind the system bars, then wrap your content once:

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            LiquidGlassTheme { // light/dark colors follow the system by default
                AppRoot()
            }
        }
    }
}
```

`LiquidGlassTheme` measures the device once (Android version, low-RAM, high-end or not) and follows Battery Saver live.
It works next to Material or your own theme: put it inside them.

### 2. Brand colors

Start from `GlassColors.light()` / `dark()` and change what your brand needs:

```kotlin
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
```

| Color | Used for |
|---|---|
| `content`, `contentMuted` | text and icons on glass, unselected tabs |
| `accent`, `accentSoft`, `onAccent` | selected stacked tab, accent button |
| `selectedFill`, `selectedContent` | selected pill (selected-only tab bar, segmented control) |
| `badge` | tab badge dot |
| `glassFill`, `glassFillStrong`, `glassFillOpaque`, `glassStroke`, `glassHighlight`, `glassShadow` | the glass itself and its frosted fallback |

Read them anywhere with `LiquidGlassTheme.colors`.

### 3. Look and "Reduce effects"

```kotlin
@Composable
fun SettingsAwareTheme(reduceEffects: Boolean, content: @Composable () -> Unit) {
    LiquidGlassTheme(
        look = GlassLook.Auto,          // Rich on high-end devices, Optimized elsewhere
        reduceEffects = reduceEffects,  // from your settings screen: true = frosted glass everywhere
        content = content,
    )
}
```

Offer a "Reduce effects" switch in your settings for people who prefer less transparency or want to save battery.

### 4. A screen with glass

Each screen has exactly one `LiquidGlassScene`. The `background` is what the glass refracts; the content goes inside.

```kotlin
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
                BasicText("Hello glass")
            }
        }
    }
}
```

A photo works well as a background:

```kotlin
LiquidGlassScene(
    background = {
        Image(wallpaper, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
    },
) { /* content */ }
```

Background rules:
- Draw it **once**. A bitmap or a photo is ideal. A gradient drawn every frame costs ~7 ms on mid-range devices;
  `GradientBackground` in the sample draws its blobs once into a quarter-resolution bitmap.
- Keep it smooth (gradients, blurred photos). Then the glass needs no blur and stays cheap.
- Glass placed in the content refracts the background. Glass placed in `overlay` (the tab bar) also refracts the
  scrolling content on capable devices.

### 5. Glass blocks

```kotlin
// Card (static)
LiquidSurface(Modifier.fillMaxWidth()) {
    BasicText("Today")
}

// Pressable card: swells, tilts and glints on touch
LiquidSurface(Modifier.fillMaxWidth(), onClick = onOpen, onClickLabel = "Open details") {
    BasicText("Tap me")
}

// Secondary button
LiquidSurface(
    modifier = Modifier.height(52.dp),
    style = GlassDefaults.control(),
    shape = GlassDefaults.PillShape,
    contentPadding = PaddingValues(horizontal = 20.dp),
    onClick = onOpen,
) { BasicText("Later", modifier = Modifier.align(Alignment.Center)) }

// Primary button in the accent color
val accent = GlassDefaults.accent()
LiquidSurface(
    modifier = Modifier.height(52.dp),
    style = accent,
    shape = GlassDefaults.PillShape,
    contentPadding = PaddingValues(horizontal = 20.dp),
    onClick = onSave,
) { BasicText("Save", style = TextStyle(color = accent.contentColor), modifier = Modifier.align(Alignment.Center)) }

// Round icon button
LiquidSurface(
    modifier = Modifier.size(52.dp),
    style = GlassDefaults.control(),
    shape = GlassDefaults.PillShape,
    contentPadding = PaddingValues(14.dp),
    onClick = onAdd,
    onClickLabel = "Add",
) { Image(plusIcon, contentDescription = null) }
```

Which preset where:

| Preset | Use for | Avoid for |
|---|---|---|
| `GlassDefaults.chrome()` | tab bar, floating header, floating button | cards in a list |
| `GlassDefaults.card()` (default) | hero and large cards, a few per screen | every list row |
| `GlassDefaults.control()` | glass buttons, round buttons | many repeated small items |
| `GlassDefaults.accent()` | the primary button | — |
| `GlassDefaults.frost()` | list rows, chips, input backgrounds | the tab bar |

Shapes: `GlassDefaults.CardShape` (24 dp), `ControlShape` (16 dp), `PillShape` (fully rounded).

**Your own recipe**: copy a preset and change only what you need:

```kotlin
val thickGlass = GlassDefaults.card().copy(lensHeight = 20.dp, lensAmount = 32.dp)
LiquidSurface(Modifier.fillMaxWidth(), style = thickGlass) { BasicText("Thicker lens") }
```

**Any composable as glass**: the modifier draws glass behind it:

```kotlin
Box(
    Modifier
        .size(120.dp)
        .liquidGlass(GlassDefaults.control(), GlassDefaults.CardShape, rememberGlassInteraction()),
)
```

**Long lists**: use frosted rows; they never sample the background, so scrolling stays smooth:

```kotlin
LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    items(items) { item ->
        LiquidSurface(Modifier.fillMaxWidth(), style = GlassDefaults.frost(), shape = GlassDefaults.ControlShape) {
            BasicText(item)
        }
    }
}
```

### 6. Tab bar

Put the tab bar in the `overlay` of the scene, aligned to the bottom:

```kotlin
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
                // Tapping the open tab again scrolls its list to the top.
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
```

Give scrolling content room so its last item ends above the floating bar:

```kotlin
val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
LazyColumn(
    state = state,
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = top + 16.dp, bottom = LiquidTabBarDefaults.contentBottomPadding()),
) { /* … */ }
```

**Selected-only labels with a round action button:**

```kotlin
LiquidTabBar(
    tabs = listOf(LiquidTab("home", "Home", homeIcon), LiquidTab("saved", "Saved", heartIcon), LiquidTab("me", "Me", personIcon)),
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
```

| Labels | Looks like | Good for |
|---|---|---|
| `LiquidTabLabels.Stacked` (default) | every tab: icon above label, soft accent droplet behind the selected one | 4–5 tabs |
| `LiquidTabLabels.SelectedOnly` | selected tab grows into a pill with icon + label, others icon only | 3–5 tabs with clear icons and short labels |

**With Navigation Compose:**

```kotlin
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
        composable("home") { HomeScreen() }
        composable("me") { ProfileScreen() }
    }
}
```

Built-in behavior: the droplet slides and bounces, you can drag it across the bar, a light haptic tick confirms the
change, the bar slides away while the keyboard is open (`hideWhenImeVisible = false` to keep it), and with a system font
scale of 130 % or more only icons are shown. Use 24 dp outline icons and one-word labels.

### 7. Segmented control

For 2–4 short options inside a screen. Animate the content in the direction of the change:

```kotlin
var range by rememberSaveable { mutableIntStateOf(0) }
LiquidSegmentedControl(listOf("Day", "Week", "Month"), range, { range = it })
AnimatedContent(
    targetState = range,
    transitionSpec = {
        val direction = if (targetState > initialState) 1 else -1
        (slideInHorizontally(tween(280)) { it / 6 * direction } + fadeIn(tween(280)))
            .togetherWith(fadeOut(tween(160)))
    },
    label = "range",
) { selected -> RangeContent(selected) }
```

More than 4 options or long labels: use a horizontally scrolling row of chips (`LiquidSurface` with
`GlassDefaults.frost()` and `PillShape`) instead.

### 8. Adapt to the device

Read the chosen tier when your own decorations should follow it:

```kotlin
val showSparkles = LiquidGlassTheme.quality != GlassQuality.Off
```

`LiquidGlassTheme.look` tells whether the device got `Rich` or `Optimized`.

### Do and don't

| Do | Don't |
|---|---|
| One `LiquidGlassTheme` for the app, one `LiquidGlassScene` per screen | Nest scenes, or put glass outside a scene and expect refraction |
| Draw the background once (bitmap, photo) | Animate a full-screen gradient every frame |
| ≤ 6 real glass elements per screen, frost for repeated items | Real glass on every list row |
| `RoundedCornerShape` (`GlassDefaults` shapes) | Path-based or custom shapes on glass |
| Keep text on glass at 4.5:1 contrast against the brightest part of the background | Put long text on a busy photo |
| Measure a release build | Judge smoothness on a debug build |

### Troubleshooting

| Problem | Cause and fix |
|---|---|
| Glass looks flat and frosted, no lens | No `LiquidGlassTheme` around it, the element is outside a `LiquidGlassScene`, the device runs Android 12 or lower, Battery Saver is on, or `reduceEffects = true` |
| Lens but no blur behind the tab bar | Expected with `GlassLook.Optimized`: the bar refracts only the background. `look = GlassLook.Rich` forces content sampling (costs ~10 ms/frame on mid-range devices) |
| Last list item hidden behind the tab bar | Add `LiquidTabBarDefaults.contentBottomPadding()` to the list's bottom padding |
| Tab bar disappears when typing | Intended. Pass `hideWhenImeVisible = false` to keep it |
| Scrolling stutters | Too much real glass on screen, background redrawn every frame, or a debug build. See [Performance rules](#performance-rules) |
| Text on glass is hard to read | Use `GlassStyle.contentColor` of the style, tint the glass (`style.copy(surface = …)`), or calm the background |

## API

| API | What it does |
|---|---|
| `LiquidGlassTheme(colors, look, reduceEffects)` | Root. Chooses `GlassQuality` and `GlassLook` once, follows Battery Saver. Read values with `LiquidGlassTheme.colors / quality / look` |
| `GlassColors.light()` / `dark()` / `copy(...)` | Colors of glass and components |
| `LiquidGlassScene(background, overlay, content)` | Frame of a screen with glass. One per screen |
| `Modifier.liquidGlass(style, shape, interaction)` | Draws glass behind any element |
| `GlassDefaults.chrome() / card() / control() / accent() / frost()` | Presets, plus `CardShape`, `ControlShape`, `PillShape` |
| `GlassStyle`, `GlassKind` | Make your own recipe (start from a preset and `copy`) |
| `rememberGlassInteraction()` | Press effect: swell, tilt toward the finger, glint |
| `LiquidSurface` | A glass block; with `onClick` it is a button |
| `LiquidTabBar` + `LiquidTab` + `LiquidTabLabels` + `LiquidTabBarDefaults` | Floating tab bar with a water-droplet indicator, `Stacked` or `SelectedOnly` labels, haptics, reselect, badge, hides with the keyboard |
| `LiquidSegmentedControl` | In-screen tabs for 2–4 short options, same droplet |

## Device tiers

| `GlassQuality` | When | What is drawn |
|---|---|---|
| `Full` | Android 13+ | blur, lens refraction (AGSL), chromatic aberration, highlight, shadow |
| `BlurOnly` | Android 12 | blur, highlight, shadow, no lens |
| `Off` | Android ≤ 11, low-RAM devices, Battery Saver, `reduceEffects = true` | frosted fill + sheen + stroke, never samples the background |

| `GlassLook` | Default for | Difference |
|---|---|---|
| `Rich` | high-end devices (Media Performance Class ≥ 13) | shadows on every block, accent buttons are real glass, the tab bar refracts scrolling content |
| `Optimized` | every other device | shadows only on floating chrome and accent buttons, accent buttons are solid with a sheen, the tab bar refracts only the background |

`GlassLook.Auto` (default) picks between them.

## Performance rules

Measured on a mid-range device (Redmi Note 13; catalog screen: 100 % janky frames → ~12 % with full glass, ~3 % with
glass off):

- At most **6 real glass elements per screen**, the tab bar included. Repeated items (list rows, chips) use
  `GlassDefaults.frost()`, which never samples the background.
- Only `RoundedCornerShape`: path-based shapes cost ~9 ms per frame.
- Draw the `LiquidGlassScene` background **once** (for example into a quarter-resolution bitmap, see
  `sample/SampleArt.kt`). Redrawing a full-screen gradient every frame costs ~7 ms. A smooth background also means the
  glass needs no blur.
- Shadows cost ~10 % janky frames: keep them on floating chrome and the primary button (the presets already do).
- Real glass on a small button costs ~3 ms each: the `Optimized` look draws accent buttons solid.
- Never judge smoothness on a debug build; measure a release (R8) build with Macrobenchmark and aim for under 10 %
  janky frames with `GlassQuality.Full` on a mid-range device.

## Accessibility

- Tabs and segments are announced as tabs with their selected state; icon-only tabs keep their label.
- With a system font scale of 130 % or more the tab bar shows icons only.
- Content on glass should keep 4.5:1 contrast against the brightest point of the background.
- Animations follow the system "Remove animations" setting.

## Public API stability

The public API is recorded in `liquidglass/api/liquidglass.api` (binary-compatibility-validator format).
`./gradlew :liquidglass:apiCheck` fails when it changes; after an intended change run `./gradlew :liquidglass:apiDump`
and commit the file. While the version is 0.x the API may still change between minor versions.

## License and credits

Apache License 2.0, see [LICENSE](LICENSE).

The glass engine is copied from [Kyant0/AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass) (`backdrop`
2.0.1, Apache 2.0) with performance changes listed in [NOTICE](NOTICE). The press effect and the droplet indicator are
ported from the AndroidLiquidGlass catalog. Thank you, Kyant.
