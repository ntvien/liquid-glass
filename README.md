# Liquid Glass for Jetpack Compose

Liquid glass surfaces, a floating tab bar and a segmented control for Android, built on Jetpack Compose. The glass
refracts what is behind it (lens, chromatic aberration, highlight), reacts to touch (swell, tilt, glint) and adapts to
the device: full glass on Android 13+, lighter glass on Android 12, a cheap frosted look on older or low-RAM devices and
in Battery Saver.

| Stacked tab labels | Selected-only tab labels |
|---|---|
| ![Stacked](docs/sample-stacked.png) | ![Selected only](docs/sample-selected-only.png) |

*Sample app on the Android 16 emulator.*

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
    implementation("com.github.ntvien.liquid-glass:liquidglass:0.1.0")
}
```

Requirements: minSdk 26, compileSdk 37, Jetpack Compose (BOM 2026.09.00 or newer).

## Quick start

```kotlin
import io.github.ntvien.liquidglass.GlassDefaults
import io.github.ntvien.liquidglass.GlassScene
import io.github.ntvien.liquidglass.LiquidGlassTheme
import io.github.ntvien.liquidglass.component.LiquidSegmentedControl
import io.github.ntvien.liquidglass.component.LiquidSurface
import io.github.ntvien.liquidglass.component.LiquidTab
import io.github.ntvien.liquidglass.component.LiquidTabBar

setContent {
    LiquidGlassTheme {                       // measures the device once; wrap it inside your own theme
        var tab by remember { mutableStateOf("home") }
        var range by remember { mutableIntStateOf(0) }
        GlassScene(
            background = { MyBackground() },  // what the glass refracts: draw it once, keep it smooth
            overlay = {
                LiquidTabBar(
                    tabs = listOf(
                        // your 24 dp outline ImageVectors
                        LiquidTab("home", "Home", homeIcon),
                        LiquidTab("saved", "Saved", heartIcon, badge = true),
                        LiquidTab("me", "Me", personIcon),
                    ),
                    selectedKey = tab,
                    onSelect = { tab = it },
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            },
        ) {
            Column(Modifier.statusBarsPadding().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                LiquidSegmentedControl(listOf("Day", "Week", "Month"), range, { range = it })
                LiquidSurface(Modifier.fillMaxWidth(), onClick = { /* … */ }, onClickLabel = "Open") {
                    BasicText("Hello glass", style = TextStyle(color = GlassDefaults.card().contentColor))
                }
            }
        }
    }
}
```

Any element can be glass with the modifier:

```kotlin
Box(Modifier.liquidGlass(GlassDefaults.control(), GlassDefaults.PillShape, rememberGlassInteraction()))
```

The `sample` module shows every piece on one screen.

## API

| API | What it does |
|---|---|
| `LiquidGlassTheme(colors, look, reduceEffects)` | Root. Chooses `GlassQuality` and `GlassLook` once, follows Battery Saver. Read values with `LiquidGlassTheme.colors / quality / look` |
| `GlassColors.light()` / `dark()` / `copy(...)` | Colors of glass and components |
| `GlassScene(background, overlay, content)` | Frame of a screen with glass. One per screen |
| `Modifier.liquidGlass(style, shape, interaction)` | Draws glass behind any element |
| `GlassDefaults.chrome() / card() / control() / accent() / frost()` | Presets, plus `CardShape`, `ControlShape`, `PillShape` |
| `GlassStyle`, `GlassKind` | Make your own recipe (start from a preset and `copy`) |
| `rememberGlassInteraction()` | Press effect: swell, tilt toward the finger, glint |
| `LiquidSurface` | A glass block; with `onClick` it is a button |
| `LiquidTabBar` + `LiquidTab` + `LiquidTabLabels` | Floating tab bar with a water-droplet indicator, `Stacked` or `SelectedOnly` labels, haptics, reselect, badge, hides with the keyboard |
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

`GlassLook.Auto` (default) picks between them. Wire a "Reduce effects" switch in your settings to `reduceEffects`.

## Performance rules

Measured on a mid-range device (Redmi Note 13; catalog screen: 100 % janky frames → ~12 % with full glass, ~3 % with
glass off):

- At most **6 real glass elements per screen**, the tab bar included. Repeated items (list rows, chips) use
  `GlassDefaults.frost()`, which never samples the background.
- Only `RoundedCornerShape`: path-based shapes cost ~9 ms per frame.
- Draw the `GlassScene` background **once** (for example into a quarter-resolution bitmap, see `sample/SampleArt.kt`).
  Redrawing a full-screen gradient every frame costs ~7 ms. A smooth background also means the glass needs no blur.
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
