# Changelog

All notable changes to this project are documented here. The project follows [Semantic Versioning](https://semver.org/);
while the version is 0.x the public API may still change between minor versions.

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
