# Changelog

All notable changes to this project are documented here. The project follows [Semantic Versioning](https://semver.org/);
while the version is 0.x the public API may still change between minor versions.

## Unreleased

- `LiquidMenuButton`, `LiquidMenuItem`, `LiquidMenuDivider`: iOS 26 style droplet menu. It grows out of the button with
  a small bounce, items appear one after another, drag the finger across the items to choose, tap outside or Back
  closes it by shrinking back into the button. Inside a `LiquidGlassScene` it is drawn as real glass above the tab bar;
  outside a scene it opens in a popup with the frosted look.
- `LiquidGlassTheme(overlaySamplesContent)`: floating glass (tab bar, menus) now refracts the scrolling content on every
  Android 13+ device by default, not only with `GlassLook.Rich`. Pass `false` to sample only the background
  (~10 ms/frame cheaper while scrolling on mid-range devices).
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
