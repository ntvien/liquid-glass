# Liquid Glass cho Jetpack Compose

Thư viện Android (Jetpack Compose) gồm các khối kính lỏng, thanh tab nổi và tab chọn trong màn.
- **Hiệu ứng:** kính khúc xạ những gì ở phía sau (loe ở mép, tách màu cầu vồng, viền sáng) và phản ứng khi chạm (phồng, nghiêng theo ngón tay, loé sáng).
- **Tự điều chỉnh theo máy:**
  - Android 13+: kính đầy đủ;
  - Android 12: kính nhẹ hơn;
  - máy cũ, máy ít RAM, hoặc khi bật Tiết kiệm pin: kính mờ giả, rất rẻ.

English: [README.md](README.md)

| Nhãn tab kiểu Stacked | Nhãn tab kiểu SelectedOnly | Menu giọt nước |
|---|---|---|
| ![Stacked](docs/sample-stacked.png) | ![Selected only](docs/sample-selected-only.png) | ![Menu](docs/sample-menu.png) |

*App mẫu chạy trên emulator Android 16.*

## Cài đặt

Thêm JitPack vào `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

Rồi thêm thư viện:

```kotlin
dependencies {
    implementation("com.github.ntvien:liquid-glass:v0.1.0")
}
```

JitPack lấy tên tag git làm phiên bản, nên phải giữ chữ `v`. Các bản phát hành: [tag trên GitHub](https://github.com/ntvien/liquid-glass/tags).

Yêu cầu: minSdk 26, compileSdk 37, Jetpack Compose (BOM 2026.09.00 trở lên), Kotlin 2.4 trở lên.

## Dùng nhanh

Ba bước:
1. Bọc app trong `LiquidGlassTheme`.
2. Mỗi màn đặt trong một `LiquidGlassScene`.
3. Dùng các khối kính bên trong.

```kotlin
LiquidGlassTheme {
    LiquidGlassScene(background = { MyBackground() }) {
        LiquidSurface(Modifier.statusBarsPadding().padding(16.dp).fillMaxWidth()) {
            BasicText("Hello glass")
        }
    }
}
```

Module `sample` có một màn chứa đủ mọi thứ. Mọi đoạn code trong hướng dẫn này đều được biên dịch thật trong
[`sample/…/guide/GuideSnippets.kt`](sample/src/main/kotlin/io/github/ntvien/liquidglass/sample/guide/GuideSnippets.kt).

## Hướng dẫn

### 1. Cài theme

Gọi `enableEdgeToEdge()` để nội dung vẽ tràn dưới thanh trạng thái và thanh điều hướng, rồi bọc nội dung **một lần**:

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            LiquidGlassTheme { // màu sáng/tối tự theo cài đặt của máy
                AppRoot()
            }
        }
    }
}
```

`LiquidGlassTheme`:
- đo máy **một lần**: phiên bản Android, máy ít RAM hay không, máy cao cấp hay không;
- theo dõi Tiết kiệm pin ngay khi người dùng bật/tắt;
- dùng chung được với Material hay theme riêng của bạn: đặt nó bên trong theme đó.

### 2. Màu thương hiệu

Lấy `GlassColors.light()` / `dark()` rồi đổi màu cần thiết:

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

| Màu | Dùng cho |
|---|---|
| `content`, `contentMuted` | chữ và icon trên kính, tab chưa chọn |
| `accent`, `accentSoft`, `onAccent` | nút màu nhấn, kính màu nhấn |
| `tabSelectedFill`, `tabSelectedContent` | tab đang chọn của thanh tab Stacked: giọt, icon và chữ |
| `chromeFill` | lớp phủ của kính nổi (thanh tab): kính tối ở giao diện tối |
| `selectedFill`, `selectedContent` | viên đang chọn của thanh tab SelectedOnly |
| `raisedFill` | viên kính sáng nổi trên rãnh: ô đang chọn của tab trong màn |
| `badge` | chấm báo trên tab |
| `glassFill`, `glassFillStrong`, `glassFillOpaque`, `glassStroke`, `glassHighlight`, `glassShadow` | bản thân tấm kính và kính mờ dự phòng |

Đọc màu ở bất kỳ đâu bằng `LiquidGlassTheme.colors`.

### 3. Phong cách kính và "Giảm hiệu ứng"

```kotlin
@Composable
fun SettingsAwareTheme(reduceEffects: Boolean, content: @Composable () -> Unit) {
    LiquidGlassTheme(
        look = GlassLook.Auto,          // máy cao cấp: Rich, máy khác: Optimized
        reduceEffects = reduceEffects,  // lấy từ màn Cài đặt: true = kính mờ ở mọi nơi
        overlaySamplesContent = null,   // null: thanh tab, menu khúc xạ cả nội dung đang cuộn trên Android 13+
        content = content,
    )
}
```

Nên có công tắc **"Giảm hiệu ứng"** trong Cài đặt cho người thích ít trong suốt hoặc muốn tiết kiệm pin.

**Kính nổi khúc xạ nội dung:** mặc định trên mọi máy Android 13+, thanh tab và menu khúc xạ cả nội dung đang cuộn phía dưới (chữ, ảnh bị bẻ cong ở mép), không chỉ nền. Đây là thứ làm kính trông như kính thật thay vì nhựa trắng.
- **Cái giá:** tốn nhất trong toàn bộ kính. Trên Galaxy S23 Ultra, khung giật khi cuộn là 71% khi bật, 22% khi tắt (xem
  [Performance rules](README.md#performance-rules)).
- **Cách tắt:** nếu benchmark của app vượt ngưỡng, truyền `overlaySamplesContent = false`.

### 4. Một màn có kính

Mỗi màn có đúng **một** `LiquidGlassScene`. `background` là thứ kính khúc xạ; nội dung đặt bên trong.

```kotlin
@Composable
fun BasicScreen() {
    LiquidGlassScene(
        background = { GradientBackground(dark = false) }, // xem sample/SampleArt.kt
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

Ảnh chụp làm nền rất hợp:

```kotlin
LiquidGlassScene(
    background = {
        Image(wallpaper, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
    },
) { /* nội dung */ }
```

Luật cho nền:
- **Vẽ một lần.** Ảnh bitmap hay ảnh chụp là tốt nhất. Gradient vẽ lại mỗi khung thì cả màn sau mọi khối kính phải vẽ lại;
  `GradientBackground` trong app mẫu vẽ các đốm màu một lần thành bitmap 1/4 độ phân giải.
- **Giữ nền mịn** (gradient, ảnh đã làm mờ): khi đó kính không cần blur nên rẻ.
- **Kính lấy mẫu từ đâu:** kính đặt trong nội dung khúc xạ nền. Kính đặt trong `overlay` (thanh tab) còn khúc xạ cả nội dung đang cuộn, nếu máy đủ mạnh.

### 5. Các khối kính

```kotlin
// Thẻ (đứng yên)
LiquidSurface(Modifier.fillMaxWidth()) {
    BasicText("Today")
}

// Thẻ bấm được: phồng, nghiêng, loé sáng khi chạm
LiquidSurface(Modifier.fillMaxWidth(), onClick = onOpen, onClickLabel = "Open details") {
    BasicText("Tap me")
}

// Nút phụ
LiquidSurface(
    modifier = Modifier.height(52.dp),
    style = GlassDefaults.control(),
    shape = GlassDefaults.PillShape,
    contentPadding = PaddingValues(horizontal = 20.dp),
    onClick = onOpen,
) { BasicText("Later", modifier = Modifier.align(Alignment.Center)) }

// Nút chính màu nhấn
val accent = GlassDefaults.accent()
LiquidSurface(
    modifier = Modifier.height(52.dp),
    style = accent,
    shape = GlassDefaults.PillShape,
    contentPadding = PaddingValues(horizontal = 20.dp),
    onClick = onSave,
) { BasicText("Save", style = TextStyle(color = accent.contentColor), modifier = Modifier.align(Alignment.Center)) }

// Nút tròn chỉ có icon
LiquidSurface(
    modifier = Modifier.size(52.dp),
    style = GlassDefaults.control(),
    shape = GlassDefaults.PillShape,
    contentPadding = PaddingValues(14.dp),
    onClick = onAdd,
    onClickLabel = "Add",
) { Image(plusIcon, contentDescription = null) }
```

Dùng kiểu kính nào ở đâu:

| Kiểu kính | Dùng cho | Tránh dùng cho |
|---|---|---|
| `GlassDefaults.chrome()` | thanh tab, header nổi, nút nổi | thẻ trong danh sách |
| `GlassDefaults.card()` (mặc định) | thẻ lớn, hero, vài thẻ mỗi màn | từng dòng danh sách |
| `GlassDefaults.control()` | nút kính, nút tròn | nhiều phần tử nhỏ lặp lại |
| `GlassDefaults.accent()` | nút chính | — |
| `GlassDefaults.frost()` | dòng danh sách, chip, nền ô nhập | thanh tab |

Hình: `GlassDefaults.CardShape` (bo 24 dp), `ControlShape` (bo 16 dp), `PillShape` (bo tròn hẳn), và góc liên tục kiểu
iOS `ContinuousCardShape` (24 dp), `ContinuousPillShape`. Góc liên tục vẽ bằng path: chỉ dùng cho vài khối lớn (thanh
tab và giọt của nó, thẻ lớn). `LiquidSurface` mặc định dùng `ContinuousCardShape` cho kính thật, `CardShape` cho frost.

**Tự làm kiểu kính:** chép một kiểu có sẵn rồi đổi phần cần đổi:

```kotlin
val thickGlass = GlassDefaults.card().copy(lensHeight = 20.dp, lensAmount = 32.dp)
LiquidSurface(Modifier.fillMaxWidth(), style = thickGlass) { BasicText("Thicker lens") }
```

**Biến bất kỳ thành phần nào thành kính:** modifier vẽ kính phía sau nó:

```kotlin
Box(
    Modifier
        .size(120.dp)
        .liquidGlass(GlassDefaults.control(), GlassDefaults.CardShape, rememberGlassInteraction()),
)
```

**Danh sách dài:** dùng dòng kính mờ. Nó không lấy mẫu nền nên cuộn vẫn mượt:

```kotlin
LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    items(items) { item ->
        LiquidSurface(Modifier.fillMaxWidth(), style = GlassDefaults.frost(), shape = GlassDefaults.ControlShape) {
            BasicText(item)
        }
    }
}
```

### 6. Thanh tab

Đặt thanh tab trong `overlay` của scene, căn đáy:

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
                // Chạm lại tab đang mở thì cuộn danh sách về đầu.
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

Chừa đáy cho nội dung cuộn để mục cuối nằm trên thanh tab:

```kotlin
val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
LazyColumn(
    state = state,
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = top + 16.dp, bottom = LiquidTabBarDefaults.contentBottomPadding()),
) { /* … */ }
```

**Kiểu SelectedOnly, có nút tròn bên phải:**

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

| Kiểu nhãn | Trông thế nào | Hợp với |
|---|---|---|
| `LiquidTabLabels.Stacked` (mặc định) | mọi tab: icon trên, chữ dưới; giọt màu nhạt sau tab đang chọn | 4–5 tab |
| `LiquidTabLabels.SelectedOnly` | tab đang chọn nở thành viên có icon + chữ, tab khác chỉ icon | 3–5 tab, icon rõ nghĩa, nhãn ngắn |

**Dùng với Navigation Compose:**

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

Có sẵn, không cần làm thêm:
- **Giọt chỉ báo:** trượt rồi nảy nhẹ, kéo ngang trên thanh được.
- **Rung:** "tích" nhẹ khi đổi tab.
- **Bàn phím mở:** thanh tab trượt xuống ẩn đi. Muốn giữ thanh tab thì truyền `hideWhenImeVisible = false`.
- **Chữ hệ thống ≥ 130%:** thanh tab chỉ còn icon.

Nên dùng icon viền 24 dp, nhãn một từ.

### 7. Tab trong màn (segmented)

Cho 2–4 lựa chọn ngắn trong một màn. Nội dung nên trượt theo hướng đổi:

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

Mặc định: rãnh kính trong, ô đang chọn là **viên kính sáng nổi lên** (màu `GlassColors.raisedFill`) như iOS 26, chữ ô chọn đậm hơn. Kiểu khác đổi qua tham số màu:

```kotlin
// Viên màu nhấn
LiquidSegmentedControl(
    options, range, { range = it },
    indicatorColor = LiquidGlassTheme.colors.accent,
    selectedTextColor = LiquidGlassTheme.colors.onAccent,
)
```

Hơn 4 lựa chọn hoặc chữ dài: dùng hàng chip cuộn ngang (`LiquidSurface` với `GlassDefaults.frost()` và `PillShape`).

### 8. Menu giọt nước

Nút kính mở một menu kiểu iOS 26:
- **Mở:** menu mọc ra từ nút như giọt kính, phồng quá đà một chút rồi nảy về; các mục hiện lần lượt.
- **Chọn:** chạm một mục, hoặc giữ nút rồi kéo ngón tay xuống mục và thả. Đi qua mỗi mục máy rung nhẹ.
- **Đóng:** chạm ra ngoài, bấm Back, hoặc chọn xong; menu co về nút.

```kotlin
LiquidMenuButton(
    items = listOf(
        LiquidMenuItem("Rename", editIcon, onClick = onRename),
        LiquidMenuItem("Share", shareIcon, onClick = onShare),
        LiquidMenuDivider,
        LiquidMenuItem("Delete", trashIcon, destructive = true, onClick = onDelete),
    ),
    contentDescription = "More",
    modifier = Modifier.size(44.dp),
) { Image(moreIcon, contentDescription = null) }
```

| Mục | Ghi chú |
|---|---|
| `LiquidMenuItem(label, icon, destructive, enabled, onClick)` | `destructive = true` vẽ màu đỏ (xóa); `enabled = false` làm mờ, không chọn được |
| `LiquidMenuDivider` | đường kẻ ngăn nhóm |
| `menuWidth` | bề rộng menu, mặc định 250 dp |

Vị trí:
- Menu mở xuống dưới nút; nếu hết chỗ thì mở lên trên.
- Menu căn theo phía màn hình mà nút đang nằm.

Đặt nút ở đâu cũng được trong `LiquidGlassScene`: menu tự vẽ lên lớp trên cùng (phủ cả thanh tab) và là kính thật. Ngoài scene, menu mở trong popup với kiểu kính mờ.

Nút nên có kích thước từ 44 dp trở lên.

### 9. Điều chỉnh theo máy

Đọc mức kính máy đang dùng khi trang trí riêng của bạn cũng cần theo:

```kotlin
val showSparkles = LiquidGlassTheme.quality != GlassQuality.Off
```

`LiquidGlassTheme.look` cho biết máy đang dùng `Rich` hay `Optimized`.

### Nên và không nên

| Nên | Không nên |
|---|---|
| Một `LiquidGlassTheme` cho cả app, một `LiquidGlassScene` mỗi màn | Lồng scene, hoặc đặt kính ngoài scene mà mong có khúc xạ |
| Vẽ nền một lần (bitmap, ảnh chụp) | Animate gradient toàn màn mỗi khung |
| ≤ 6 khối kính thật mỗi màn, phần tử lặp lại dùng frost | Kính thật cho từng dòng danh sách |
| `RoundedCornerShape` cho phần tử lặp; góc liên tục chỉ cho vài khối lớn | Góc liên tục hay hình path khác cho dòng danh sách, chip |
| Chữ trên kính đạt tương phản 4.5:1 so với điểm sáng nhất của nền | Đặt đoạn chữ dài trên ảnh rối |
| Đo trên bản release | Đánh giá độ mượt trên bản debug |

### Xử lý sự cố

| Hiện tượng | Nguyên nhân và cách sửa |
|---|---|
| Kính phẳng, mờ, không có loe | Thiếu `LiquidGlassTheme`, khối kính nằm ngoài `LiquidGlassScene`, máy Android 12 trở xuống, đang bật Tiết kiệm pin, hoặc `reduceEffects = true` |
| Thanh tab trông như nhựa trắng, không thấy loe | Nền quá nhạt (dùng đốm màu đậm 35–60%, xem `sample/SampleArt.kt`); máy Android 12 (không có loe); hoặc đã truyền `overlaySamplesContent = false` |
| Mục cuối danh sách bị thanh tab che | Thêm `LiquidTabBarDefaults.contentBottomPadding()` vào padding đáy của danh sách |
| Thanh tab biến mất khi gõ phím | Đúng thiết kế. Truyền `hideWhenImeVisible = false` để giữ lại |
| Cuộn bị giật | Quá nhiều kính thật trên màn, nền vẽ lại mỗi khung, hoặc đang chạy bản debug. Xem phần hiệu năng trong [README.md](README.md#performance-rules) |
| Chữ trên kính khó đọc | Dùng `contentColor` của kiểu kính, phủ màu cho kính (`style.copy(surface = …)`), hoặc làm nền dịu lại |

## Bảng API, mức kính, hiệu năng, giấy phép

Xem các mục [API](README.md#api), [Device tiers](README.md#device-tiers), [Performance rules](README.md#performance-rules),
[Accessibility](README.md#accessibility) và [License and credits](README.md#license-and-credits) trong README tiếng Anh.

Tóm tắt:
- **Mức kính:**
  - `Full`: Android 13+;
  - `BlurOnly`: Android 12;
  - `Off`: Android ≤ 11, máy ít RAM, Tiết kiệm pin, hoặc `reduceEffects`.
- **Hiệu năng:**
  - tối đa 6 khối kính thật mỗi màn;
  - `RoundedCornerShape` cho phần tử lặp; góc liên tục chỉ cho vài khối lớn (thanh tab, thẻ lớn);
  - nền vẽ một lần;
  - bóng chỉ cho thanh tab và nút chính;
  - thanh tab khúc xạ nội dung đang cuộn là phần tốn nhất: nếu vượt ngân sách, truyền `overlaySamplesContent = false`
    trước;
  - đo bằng bản release, so với chính màn đó khi `reduceEffects = true`: kính chỉ nên thêm khoảng 10 điểm % khung
    giật. Số đo thật trên S23 Ultra, A52s, A51 nằm ở mục Performance rules.
- **Giấy phép:** Apache 2.0. Engine kính chép từ Kyant0/AndroidLiquidGlass, các chỗ đã sửa ghi trong [NOTICE](NOTICE).
