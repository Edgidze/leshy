# Слой токенов: таблица соответствия «старое значение → токен → где стояло»

Приложение к задаче 2 `track-app.md`. Нужна ровно для одного: **глазами проверить, что ни одно
число не поехало**. Мировая редакция после введения токенов обязана быть пиксельно той же,
и единственный способ в этом убедиться без скриншот-дифа — сверить значения построчно.

Замер до работы: 74 обращения к `RoundedCornerShape`/`CircleShape` в `shared/src` против
**одного** обращения к `MaterialTheme.shapes`. После — ноль и ноль: оба грепа из приёмки
пустые, все величины приходят из `ui/theme/LeshyTokens.kt`.

Файл токенов — единственное место во всём `shared`, где эти числа теперь написаны.

## Скругления

| Токен | Значение (мир) | Что стояло раньше | Мест | Где |
|---|---|---|---|---|
| `shapeDialog` | `RoundedCornerShape(24.dp)` | `RoundedCornerShape(24.dp)` | 10 | `RecordScreen` ×2, `SpeciesFormDialog`, `WalkShareDialog`, `PlaceViewDialog`, `CollectionNameDialog`, `WalksPickerDialog`, `AddPlaceDialog`, `CatalogPhotoPickerDialog`, `MapFilterDialog` |
| `shapeHeroMap` | `RoundedCornerShape(16.dp)` | `HERO_CORNER_RADIUS = 16.dp` — **два одинаковых private val в разных файлах** | 2 | `WalkDetailScreen:371`, `MapScreen:228` |
| `shapeMapChrome` | `RoundedCornerShape(16.dp)` | `MaterialTheme.shapes.large` — единственное обращение к `MaterialTheme.shapes` в проекте; 16.dp это его дефолт в Material3, тема `shapes` не переопределяет | 1 | `FullScreenMapScaffold:71` |
| `shapeMapBadge` | `CircleShape` | `CircleShape` | 2 | `WalkDetailScreen:395`, `MapScreen:253` |
| `shapeWalkCard` | `RoundedCornerShape(12.dp)` | `RoundedCornerShape(12.dp)` | 1 | `WalkCard:199` |
| `shapeRouteThumbnail` | `RoundedCornerShape(12.dp)` | `RoundedCornerShape(12.dp)` | 2 | `WalkRouteThumbnail:56,154` |
| `shapeSpeciesTile` | `RoundedCornerShape(12.dp)` | `RoundedCornerShape(12.dp)` | 4 | `RecordScreen:1218,1219`, `CatalogPhotoPickerDialog:110,111` |
| `shapePhotoPreview` | `RoundedCornerShape(12.dp)` | `RoundedCornerShape(12.dp)` | 2 | `SpeciesFormDialog:272`, `IconEditorDialog:367` |
| `shapePlaceThumbnail` | `RoundedCornerShape(8.dp)` | `RoundedCornerShape(8.dp)` | 2 | `WalkDetailScreen:562,568` |
| `shapeListRow` | `RoundedCornerShape(8.dp)` | `RoundedCornerShape(8.dp)` | 2 | `WelcomeScreen:275`, `PrivacyPolicyLink:49` |
| `shapeAppIcon` | `RoundedCornerShape(25.dp)` | `APP_ICON_CORNER = 25.dp` | 1 | `WelcomeScreen:196` |
| `shapeActionButton` | `RoundedCornerShape(20.dp)` | `ACTION_BUTTON_SHAPE = RoundedCornerShape(20.dp)` | 4 | `RecordScreen:702,726,744,755` |
| `shapeRoundButton` | `CircleShape` | `CircleShape` | 5 | `RecordScreen:883,885`, `HelpIllustrations:230,232`, `WelcomeVignettes:203` |
| `shapeCountButton` | `CircleShape` | `CircleShape` | 1 | `MushroomTile:285` |
| `shapePill` | `CircleShape` | `CircleShape` (6 мест) и `RoundedCornerShape(SPECTRUM_TRACK_HEIGHT / 2)` (1 место) | 7 | `HelpIllustrations:198,200,1000,1001`, `SpeciesFormDialog:367`, `WelcomeVignettes:217`, `MiniMockups:78` |
| `shapeNavigationOverlay` | `RoundedCornerShape(bottomStart = 20.dp)` | то же | 1 | `NavigationOverlayPanel:40` |
| `shapeColorSwatch` | `CircleShape` | `CircleShape` | 4 | `SpeciesFormDialog:350,352,375,378` |
| `shapeMagnifier` | `CircleShape` | `CircleShape` | 2 | `IconEditorDialog:579,580` |

**Единственное место, где значение не переносилось механически, — `shapeMapChrome`.** Там стоял
не литерал, а `MaterialTheme.shapes.large`; 16.dp — задокументированный дефолт Material3 для
`large`, и тема `shapes` нигде не переопределяет. Если это когда-нибудь окажется неверно,
проявится именно здесь и только здесь.

**Замена `RoundedCornerShape(SPECTRUM_TRACK_HEIGHT / 2)` на `CircleShape` — тождество, а не
приближение.** `CircleShape` это `RoundedCornerShape(50)`, а процентный размер угла считается как
`size.minDimension * процент / 100`. Дорожка ползунка высотой 28dp и во всю ширину: минимальное
измерение 28dp, половина 14dp — ровно `SPECTRUM_TRACK_HEIGHT / 2`.

## Скругления иллюстраций

Иллюстрации подсказок и виньетки приветствия — это **нарисованный интерфейс**, уменьшенные копии
настоящих экранов. Их величины подобраны под масштаб рисунка и живут отдельным семейством
токенов намеренно: приравнять их к боевым значило бы сделать рисунок неузнаваемым, а не
согласованным.

| Токен | Значение (мир) | Что стояло раньше | Мест | Где |
|---|---|---|---|---|
| `shapeIllustrationFrame` | `RoundedCornerShape(12.dp)` | `FRAME_CORNER = 12.dp` | 2 | `HelpIllustrations:157,162` |
| `shapeVignetteFrame` | `RoundedCornerShape(10.dp)` | `VIGNETTE_CORNER = 10.dp` | 2 | `WelcomeVignettes:65,70` |
| `shapeIllustrationPanel` | `RoundedCornerShape(10.dp)` | `RoundedCornerShape(10.dp)` | 1 | `HelpIllustrations:710` |
| `shapeIllustrationCard` | `RoundedCornerShape(8.dp)` | `RoundedCornerShape(8.dp)` | 7 | `HelpIllustrations:267,413,451,528,862,969,971` |
| `shapeIllustrationTile` | `RoundedCornerShape(6.dp)` | `RoundedCornerShape(6.dp)`, у `MiniMap` — дефолт параметра `corner: Dp = 6.dp` | 11 | `HelpIllustrations:334,336,421,638`, `WelcomeVignettes:123,151,169,181`, `MiniMockups:93,196,241` |
| `shapeIllustrationThumbnail` | `RoundedCornerShape(4.dp)` | `RoundedCornerShape(4.dp)` | 1 | `MiniMockups:210` |

У `MiniMap` параметр сменил тип — `corner: Dp` → `corner: Shape`, дефолт берётся из токенов
(в `@Composable`-функции значения по умолчанию вычисляются в композиции, поэтому композишн-локал
там читается штатно). Ни один вызывающий этот параметр не передаёт — проверено, все семь вызовов
идут с дефолтом.

## Толщины, отступы и не-геометрические величины

| Токен | Значение (мир) | Что стояло раньше | Мест |
|---|---|---|---|
| `widthSpeciesTileBorder` | `2.dp` | `2.dp` | 2 (`RecordScreen:1219`, `CatalogPhotoPickerDialog:111`) |
| `photoAspectRatio` | `MUSHROOM_PHOTO_ASPECT_RATIO` (1.26f) | та же константа напрямую | 4 (`RecordScreen:1057,1217`, `StatsBlocks:265`, `MushroomTile:213`) |
| `findTileMinColumns` | `2` | `FIND_TILE_MIN_COLUMNS = 2` | 1 (`StatsBlocks:221`) |
| `frameWidth` | `0.dp` | — | 0 |
| `framePhotoWidth` | `0.dp` | — | 0 |
| `matPadding` | `0.dp` | — | 0 |
| `surfaceStyle` | `SurfaceStyle.FLAT` | — | 0 |
| `groundTexture` | `null` | — | 0 |
| `typeScaleStep` | `1f` | — | 0 |

Последние шесть — **новые токены без единого места применения в мировой редакции, и это не
недоделка**. Мировая редакция не рисует ни рам, ни матов, ни текстуры; их нулевые значения —
честное описание того, что есть. Применяются они вместе с обёртками поверхностей, то есть в
работе по оформлению (`design.md`, разделы 6 и 15), а не в этом, ничего не меняющем, переносе.

## Мина, которую стоит знать заранее: `photoAspectRatio`

Это же число участвует в `RECORD_TILE_HEIGHT` — верхнеуровневом `val` в `MushroomTile.kt`,
который считается **вне композиции** и потому токена не видит. Пока значение токена равно
`MUSHROOM_PHOTO_ASPECT_RATIO`, расхождения нет. Развести редакции по этому токену можно только
вместе с переносом `RECORD_TILE_HEIGHT` в композицию — иначе высота ленты на «Записи» перестанет
соответствовать высоте плиток в ней. Предупреждение продублировано в KDoc самого токена.

## Токен, добавленный позже: `shapeButton`

Заведён вместе с российским набором, а не при переносе, и потому в таблицах выше его нет.
Причина, по которой он вообще понадобился: кнопки брали форму из Material
(`ButtonDefaults.shape`), то есть в сотне захардкоженных мест не участвовали, — а расквадратить
их подменой `MaterialTheme.shapes` нельзя: `ButtonDefaults.shape` приходит не оттуда, а из жёстко
зашитого `CornerFull`.

| Токен | Значение (мир) | Что стояло раньше | Где |
|---|---|---|---|
| `shapeButton` | `CircleShape` | `ButtonDefaults.shape` (он же `CircleShape`) | дефолт `LeshyButton`, плюс восемь `OutlinedButton` явным параметром |

Равенство `CircleShape` и `ButtonDefaults.shape` не взято на веру — проверено скриншот-дифом
мировой сборки после правки: 0 различающихся пикселей.

`TextButton` намеренно оставлены как есть: у них нет ни заливки, ни обводки, и форма видна только
в момент нажатия.

## Что специально НЕ сделано

**Обёрток поверхностей (`LeshyCard`/`FramedSurface`) нет.** `design.md`, раздел 12, пункт 4
перечисляет их вместе с точками вставки, но в мировой редакции такая обёртка ничего не меняет
(`frameWidth = 0`, `matPadding = 0`), то есть её нельзя ни увидеть, ни проверить. Заводить её
имеет смысл там, где сразу видно, что она делает, — в шаге 3 порядка работ раздела 15
(«формы, рамы, маты, таблички»).

**Второго набора токенов нет.** `leshyTokensFor(Edition.RUSSIA)` сознательно отдаёт мировой
набор: перенос величин обязан не менять ни одного пикселя НИ В ОДНОЙ редакции, иначе непонятно,
что именно проверять глазами.
