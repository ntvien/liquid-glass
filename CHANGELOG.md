# Changelog

All notable changes to this project are documented here. The project follows [Semantic Versioning](https://semver.org/);
while the version is 0.x the public API may still change between minor versions.

## 0.3.0 — 2026-10-17

Performance only: the public API and the look are unchanged. Galaxy S23 Ultra, default settings, 0.2.0 and 0.3.0
measured back to back (janky frames, in parentheses late by more than 4 ms): switch segments 40 % (6 %) → 11 % (2 %),
switch tabs and drag the droplet 55 % (2 %) → 34 % (0 %). Galaxy A51 (no real glass), CPU frame time P50: segments
19.5 → 17.1 ms, tabs 20.2 → 18.1 ms.

- Droplet indicator (`LiquidTabBar`, `LiquidSegmentedControl`): at rest it no longer draws the glass hidden under an
  opaque pill (backdrop, blur, rim, shadow and inner shadow), so a resting or sliding droplet costs about as much as a
  plain pill. Looks the same. This includes the light segmented pill (`raisedFill`, 97 % opaque).
- Droplet press animation: the lens grows in 8 steps, so the blur + lens chain is rebuilt about 8 times per press
  instead of on every frame of the press spring; the inner shadow fades instead of growing (a new radius re-blurred
  it every frame), and the last invisible frames of the press spring no longer draw the glass.
- Shadow, inner shadow and rim layers are not re-recorded when only their alpha changes (the droplet's press
  animation re-recorded the blurred shadow and the rim shader every frame), nor on every recomposition (each tap on
  a segment or tab re-recorded them on all glass it recomposed). The inner shadow is now cached like the others;
  before, it was re-recorded on every draw.
- Tab and segment labels cross-fade without an offscreen layer per label.
- `LiquidTabBar` with `LiquidTabLabels.SelectedOnly`: while the droplet slides between tabs of different widths it is
  no longer rebuilt (lens, blur, shadow, rim) on every frame. It is measured at a few fixed widths and squeezed the
  rest of the way (at most 6 %, only while it moves); on a tab it has its exact width. Looks the same. Galaxy S23
  Ultra, switching tabs (together with the droplet changes above): frames late by more than 4 ms 24 → 16 %, CPU
  frame time P50 8.4 → 7.8 ms.
- Glass with a lens applies its vibrancy (saturation) inside the lens shader instead of a separate color filter pass
  before it. Looks the same (with `overlaySamplesContent` the saturation now follows the blur: at most a few levels
  of difference along strong color edges). Galaxy S23 Ultra, scrolling with the `Rich` look: janky 85 → 78 %; with
  the default look no measurable change.
- `benchmark`: tab switching with labels on the selected tab only, menu open/close, each with a glass-off control.
- Baseline profile regenerated: it no longer matched the library's code after the droplet, tab bar and lens changes
  above, so their per-frame code ran without ahead-of-time compilation.

## 0.2.0 — 2026-10-16

- Baseline profile shipped in the AAR (`baseline-prof.txt`, library classes only): the library's code is compiled ahead
  of time when an app is installed from Google Play or through ProfileInstaller.
- `benchmark` module: macrobenchmarks of the sample (cold startup with and without the profile, scrolling under the tab
  bar, switching tabs and dragging the droplet, switching segments; on Android 13+ also with the `Rich` look, without
  content sampling, and a glass-off control) and the baseline profile generator.
- Sample: release build minified with R8 and signed with the debug key so benchmark builds can be installed; it reads
  the look, content sampling and "reduce effects" from intent extras set by the benchmarks.
- README: measured numbers of the library itself in "Performance rules".
- `LiquidSegmentedControl`: selected segment is a raised light glass pill (new `GlassColors.raisedFill`), new
  `indicatorColor` / `selectedTextColor` / `textColor` parameters.
- Fix: the selected segment of `LiquidSegmentedControl` had no accessibility name (label layer at alpha 0 is dropped
  from the tree).
- `LiquidMenuButton`, `LiquidMenuItem`, `LiquidMenuDivider`: iOS 26 style droplet menu. It grows out of the button with
  a small bounce, items appear one after another, drag the finger across the items to choose, tap outside or Back
  closes it by shrinking back into the button. Inside a `LiquidGlassScene` it is drawn as real glass above the tab bar;
  outside a scene it opens in a popup with the frosted look.
- Tab bar glass after iOS 26: thicker lens band (24/48 dp) with chromatic aberration, 1 dp bright rim, 8 dp blur over
  the content behind it, new `GlassColors.chromeFill` tint (dark glass in the dark theme). Brighter rims on all glass
  (`glassStroke`, `glassHighlight`), lighter shadow in the light theme.
- `GlassDefaults.ContinuousCardShape` and `ContinuousPillShape`: iOS-style continuous corners. The tab bar, its droplets
  and `LiquidSurface` with real glass use them by default; frost surfaces keep `CardShape`.
- Stacked tab bar: the selected tab uses the new `GlassColors.tabSelectedFill` / `tabSelectedContent`; in the dark theme
  the selected label in the accent color was hard to read.
- Fix: on glass with continuous corners the bright rim was drawn as a capsule.
- `LiquidGlassTheme(overlaySamplesContent)`: choose whether floating glass (tab bar, menus) refracts the scrolling
  content regardless of the look. The default is unchanged: only with `GlassLook.Rich` and on high-end devices. It is
  the most expensive part of the glass (Galaxy S23 Ultra, janky frames while scrolling: 22 % → 71 %; see README,
  "Performance rules"), so turn it on with `true` only after measuring.
- Sample: stronger background blobs and colored rows so the refraction is visible; a menu in the title bar.

## 0.1.0 — 2026-10-15

First release.

- `LiquidGlassTheme`, `GlassColors` (`light()` / `dark()`), `GlassLook` (`Auto` / `Rich` / `Optimized`).
- Device tiers with `GlassQuality` (`Full` on Android 13+, `BlurOnly` on Android 12, `Off` on older or low-RAM devices
  and in Battery Saver).
- `LiquidGlassScene` and `Modifier.liquidGlass`, presets in `GlassDefaults` (`chrome`, `card`, `control`, `accent`, `frost`).
- `rememberGlassInteraction` press effect (swell, tilt, glint).
- Components: `LiquidSurface`, `LiquidTabBar` (`Stacked` and `SelectedOnly` labels, water-droplet indicator, haptics,
  reselect, hides with the keyboard) and `LiquidSegmentedControl`.
- `LiquidTabBarDefaults` (`Height`, `contentBottomPadding()`) to lay out content around the tab bar.
- Usage guide in `README.md` and `README.vi.md`; every snippet is compiled in `sample/…/guide/GuideSnippets.kt`.
- Glass engine copied from Kyant0/AndroidLiquidGlass `backdrop` 2.0.1 with three performance changes (see `NOTICE`).
