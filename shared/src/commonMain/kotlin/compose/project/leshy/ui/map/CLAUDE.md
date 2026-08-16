# ui/map/ — MapLibre-карты

Два несвязанных composable, оба `commonMain` (не `expect`/`actual` — API
`maplibre-compose` идентичен на Android/iOS с Части 4):
- **`LiveTrackMap.kt`** — трек+маркеры одной прогулки (текущей записи или уже
  сохранённой в Архиве), точка геолокации. Маркеры находок — `SymbolLayer`
  per вид (`iconRef`), без кластеризации; PHOTO/POI без иконки — `CircleLayer`
  per `colorHex`.
- **`AggregatedFindsMap.kt`** — вся история сразу: маршруты как приглушённые
  `LineLayer`, находки — кластеризованные по виду через общий
  `ClusteredFindsLayers.kt`.

## Грабли

- **`markers.groupBy{...}.forEach{...}` без `key()` — маркеры пропадают
  между рекомпозициями**, когда состав групп меняется (новый вид гриба на
  сцене). Compose не гарантирует, что слот `forEach` остаётся привязан к той
  же логической группе. Всегда оборачивать тело в `key(iconRef)`/
  `key(colorHex)`/`key(walkId)`.
- **`HeatmapLayer` крашит нативно** (SIGSEGV в `mbgl::android::MapRenderer::
  render`, type confusion — байты строки `"-heatmap"` разыменовывались как
  указатель) — воспроизводилось и на эмуляторе, и на реальном Pixel 4a после
  нескольких минут использования, с краш-лупом из-за `restoreState=true` в
  навигации. Заменено на кластеризованный `CircleLayer`/`SymbolLayer`
  (`GeoJsonOptions(cluster = true)`, `point_count` через `step()`-выражение
  на `color`/`radius`/`iconSize`) — тот же тип слоя, что и обычные маркеры,
  без единого краша с тех пор. Больше `HeatmapLayer` в проекте не
  используется нигде — не возвращать без явного запроса.
- **`sortKey` сортирует только внутри одного `SymbolLayer`**, не между
  разными слоями — при раздельной кластеризации по виду (свой
  `GeoJsonSource`+`SymbolLayer` на вид) это значит, что порядок отрисовки
  между РАЗНЫМИ видами не гарантирован, только внутри одного вида. Осознанно
  принятое ограничение, не баг.
- **`Expression<V>.plus` — top-level extension в `org.maplibre.compose.
  expressions.dsl`**, не резолвится без явного `import ...dsl.plus` (звёздный
  импорт молча подставляет несвязанный `plus` из stdlib, напр.
  `ByteArray.plus`).
- **`RenderOptions.RenderMode.TextureView`** (не дефолтный `SurfaceView`) на
  Android обязателен — `SurfaceView` не участвует в alpha-`graphicsLayer`,
  который Compose Navigation накладывает при fade-переходах, поэтому карта
  «просвечивает» непрозрачным поверх следующего экрана. `iOS` — `RenderOptions
  .Standard` (`RenderMode` не существует). Настраивается в
  `MapRenderOptions.kt` (`expect val mapRenderOptions`), подключается через
  `MapOptions(renderOptions = ...)` в обоих `MaplibreMap(...)`.
- **`OrnamentOptions` не умеет стекать орнаменты друг под другом** — компас/
  линейка масштаба/лого/атрибуция делят один `padding` + одинаковый
  фиксированный 8dp/8pt инсет, вычисляемый НЕЗАВИСИМО для каждого (не от
  размера соседей). Своп компаса (`TopEnd`) и линейки масштаба (`TopStart`,
  раньше был компас) — в `mapOrnamentOptions` (`MapRenderOptions.kt`).
  Линейка масштаба — именно на `TopStart` (не `TopEnd`): на Android
  `ScaleBarWidget.onDraw()` (`android-plugin-scalebar-v9`) всегда рисует
  слева направо от фиксированного `marginLeft`, посчитанного один раз по
  МАКСИМАЛЬНОЙ возможной ширине бара (`AndroidScaleBar.updateLayout()`,
  `internal` в `maplibre-compose`, недоступен для патча снаружи) — то есть
  визуально закреплён именно левый край, каким бы ни было выравнивание
  слота. На iOS `MLNScaleBar` — обычный `UIView` на Auto Layout, leading-
  constraint аналогично держит левый край неподвижным при сжатии/росте
  ширины. `TopStart` совмещает эту левую фиксацию с реальным левым краем
  экрана на обеих платформах вместо того, чтобы она болталась внутри
  TopEnd-слота, недотягивая до правого края.
  Побочный эффект: линейка масштаба видна всегда (не только компас при
  повороте) — кнопка «Filters: N» на «Карте»/«Записи» сдвинута с 16.dp до
  31.dp (было 40.dp, зазор под линейкой сокращён вдвое), посчитано из
  дефолтных dimen-ресурсов `android-plugin-scalebar-v9`
  (`barHeight+textSize+textBarMargin+2×borderWidth=14dp` + фиксированные
  8dp ≈ 22dp занимаемой высоты).
- **`WalkMapScreen.kt`: `Scaffold`'ный `padding` должен применяться к
  карте** — если забыть (`Modifier.fillMaxSize()` без `.padding(padding)`),
  карта рисуется от `y=0`, и всё, что MapLibre позиционирует «у верхнего
  края» (компас/линейка масштаба), визуально прячется под непрозрачным
  `TopAppBar`.

## Стиль карты и превью

- **OpenFreeMap** (векторный стиль, `MapStyle.kt`, `BaseStyle.Uri(...)`), не
  растровые тайлы `tile.openstreetmap.org` — растровые были размыты на HiDPI
  (не retina/`@2x`) и формально нарушали usage policy прямого хотлинка.
- **Миниатюры прогулок в «Архиве» — кэшированный PNG-снапшот, не живая
  карта.** Десятки живых `MaplibreMap` одновременно в списке — риск по
  памяти/стабильности (см. историю HeatmapLayer выше). Рендерится ОДИН раз
  при завершении прогулки: `MapSnapshotter` (Android, `org.maplibre.gl:
  android-sdk`) / `MLNMapSnapshotter` (iOS) → PNG в
  `filesDir/thumbnails`/`Documents/thumbnails`, путь — в `Walk.thumbnailPath`.
  Рендер — в независимой корутине, никогда не блокирует навигацию после
  `Finish`. `WalkCard.kt` показывает снапшот через `coil3.compose.AsyncImage`
  (`model = "file://$path"`), при ошибке/`null` — фоллбэк на лёгкий
  `Canvas`-полилиния-превью (`WalkRouteThumbnail.kt`).
  Известное ограничение: `MapSnapshotter` на Android не помещает обязательную
  атрибуцию OpenFreeMap на 240×240-снапшоте (на iOS помещается сама) — не
  исправлено, вне текущего скоупа.
- Камера при вырожденном bounding box (все точки почти совпадают) —
  `MIN_BOUNDS_SPAN_DEGREES`-guard, иначе `jumpTo` зумит до предела тайл-сервера
  и рендерит размытые апскейленные тайлы.
