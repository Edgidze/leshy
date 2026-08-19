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
- **`RegionPickerMap.kt`** («Подготовка», офлайн-скачивание карты по
  территории) — та же база (пиннутый `baseStyle` из `MapStyleCacheRepository`,
  `mapRenderOptions`+`mapOrnamentOptions` — см. «Пиннинг стиля карты» ниже),
  плюс оверлей уже скачанных/скачиваемых регионов как
  `LineLayer`-прямоугольники, построенные из bounds `OfflineRegionInfo`
  (домен-модель), не из `Set<OfflinePack>` библиотеки напрямую — сознательно
  не используется готовый `rememberOfflinePacksSource`, чтобы тип
  `OfflinePack` не утекал в UI-слой мимо `OfflineRegionRepository`.

## Офлайн-скачивание («Подготовка», `data/repository/OfflineRegionRepositoryImpl.kt`)

- **`OfflineManager` (библиотека `maplibre-compose`, `org.maplibre.compose.
  offline`) сам является persistent-каталогом** (переживает перезапуск
  приложения на обеих платформах) — отдельной Room-таблицы под список
  скачанных регионов нет и не нужно, иначе получился бы второй источник
  истины, требующий синхронизации.
- **Имя региона хранится в `OfflinePack.metadata` как сырой UTF-8**
  (`name.encodeToByteArray()`/`ByteArray.decodeToString()`) — у `OfflinePack`
  нет собственного id, `resume`/`pause`/`delete` в `OfflineRegionRepositoryImpl`
  ищут нужный pack в `OfflineManager.packs` по совпадению декодированного
  имени.
- **`OfflineManager.packs` — `Compose State`, не `Flow`** — мост в
  `Flow<List<OfflineRegionInfo>>` через `snapshotFlow { offlineManager.packs }`
  (`androidx.compose.runtime.snapshotFlow`), вызывается из
  `OfflineRegionRepositoryImpl.observeRegions()` вне `@Composable`-контекста;
  работает, пока жив глобальный snapshot-наблюдатель Compose-рантайма (уже
  верно на обоих хостах приложения после первой композиции).
- **Диапазон zoom при скачивании — автоматический**, без отдельного UI:
  пользователь выбирает область (рамка на карте, `PreparationScreen.kt`), а
  `estimateOfflineRegion` (`domain/util/OfflineRegionEstimator.kt`) сам
  подбирает `minZoom`/`maxZoom` под бюджет тайлов (`TILE_BUDGET`) — чем
  крупнее область, тем грубее детализация. Числа зума нигде не показываются
  пользователю, только оценка размера в МБ.
- На Android `getOfflineManager(context)` сам вызывает
  `MapLibre.getInstance(context)` при первом обращении (внутри библиотечного
  `AndroidOfflineManager`) — отдельная инициализация нативной библиотеки в
  `PlatformModule.android.kt` не нужна.

## Пиннинг стиля карты (`data/repository/MapStyleCacheRepository.kt`)

**Инцидент, из-за которого это появилось:** пользователь сообщил белый фон
вместо тайлов на всех картах. Причина оказалась двойной: (1) ISP
блокировал `tiles.openfreemap.org` на уровне сети (вне контроля
приложения), и (2) — куда важнее для кода — URL тайлов OpenFreeMap несёт
версионированный таймстемп снапшота планеты
(`.../planet/20260816_080001_pt/{z}/{x}/{y}.pbf`), приходящий из
`style.json`. Раньше `style.json` перезапрашивался заново при КАЖДОМ
маунте экрана с картой (`MapStyle.kt` хранил только голый URL, каждый
`AndroidMapAdapter`/`IosMapAdapter` — новый инстанс на каждый заход на
экран) — когда OpenFreeMap ротирует снапшот, таймстемп в URL меняется, и
ВСЕ ранее закэшированные (ambient-кэш MapLibre) и скачанные офлайн (native
`OfflinePack`, см. выше) тайлы оказываются пиннуты под СТАРЫМ URL, а живая
карта после ротации просит НОВЫЙ — кэш/пак промахивается, идёт в сеть, и
если сеть недоступна (как в исходном инциденте) — белый фон, причём даже
для региона, который пользователь явно скачал офлайн заранее.

**Фикс — заморозка, не автообновление.** `MapStyleCacheRepository` фетчит
`style.json` только ОДИН раз (при первом запуске, когда ещё нет локально
запиненной копии), пишет сырой JSON-текст на диск (`MapStyleStorage`,
`expect`/`actual`-путь тем же паттерном, что `PhotoStorage`) и отдаёт его
всем трём живым картам как `BaseStyle.Json(...)` (`koinInject` внутри
каждого composable — не через ViewModel, эти три экрана раньше не имели
ViewModel-зависимости на стиль вообще). Дальше пиннутая копия НИКОГДА не
меняется сама — только явным нажатием «Обновить данные карты» в
Настройках (`SettingsViewModel.refreshMapData`). Сознательный компромисс:
держать всё приложение (не только офлайн-области) на одном «поколении»
тайлов, пока пользователь сам не попросит свежее — это гарантирует, что
скачанный регион переживает и блокировку сети, и ротацию снапшота на
сервере, вплоть до удаления пользователем, за счёт того, что живая карта
просто никогда сама не меняет URL, под которым что-то ищет.

- **`OfflineRegionRepositoryImpl.downloadRegion` передаёт в `OfflineManager.
  create()` голый `OPEN_FREE_MAP_STYLE_URL`, а не запиненный локальный
  файл** — попытка передать `file://...` на запиненную копию (первая версия
  этой фичи, ровно с той же мотивацией, что описана параграфом выше — не
  резолвить отдельный от живой карты снапшот) реально сломала скачивание
  ПОЛНОСТЬЮ: подтверждено на устройстве логом `Mbgl-HttpRequest: [HTTP]
  Unable to parse resourceUrl file:///.../style.json` — нативный офлайн-
  загрузчик резолвит `styleUrl` только через свой HTTP-клиент, `file://`
  он не умеет вообще, и пак молча зависает на 0 байт/бесконечном
  индикаторе прогресса. Живая карта эту проблему никогда не ловила, потому
  что вообще не резолвит стиль через нативный URL-loader — читает
  `style.json` в память сама и отдаёт как `BaseStyle.Json(...)`.
  Возврат к голому URL реоткрывает узкое окно дрейфа: если сервер
  ротировал снапшот именно между последним пиннингом и текущим
  скачиванием, новый регион скачается под ДРУГИМ URL-шаблоном тайлов, чем
  показывает живая карта, пока пользователь не нажмёт «Обновить данные
  карты». Это обнаруживается (не молча) —
  `OfflineRegionRepository.isStyleDrifted()` /
  `MapStyleCacheRepository.isRemoteStyleDrifted()` сравнивает текущий
  remote `style.json` с запиненной копией read-only (без побочных
  эффектов, в отличие от `refreshFromNetwork()`) сразу после
  `downloadRegion()`/`onRetryClicked()`, и `PreparationViewModel`
  показывает баннер (`PreparationUiState.styleDriftWarningVisible`,
  `StringKey.PreparationStyleDriftWarning`) с просьбой обновить данные
  карты в Настройках.
- **«Обновить данные карты» сам проверяет, изменилось ли реально
  содержимое `style.json`, и если да — автоматически перекачивает ВСЕ уже
  скачанные регионы** (`RefreshMapDataUseCase`, `domain/usecase/`), без
  отдельного подтверждения пользователя — по явному запросу продукта:
  раз цель заморозки в том, чтобы пользователь вообще не думал про эту
  проблему, второй ручной шаг после нажатия «Обновить» был бы тем же самым
  трением. Механизм: `MapStyleCacheRepository.refreshFromNetwork()`
  сравнивает старое содержимое пиненного файла с новым перед перезаписью
  и возвращает `Result<Boolean>` (изменилось ли реально) — `false`, если
  контент байт-в-байт совпал (частый случай: OpenFreeMap ротирует
  таймстемп, но конкретно эта территория физически не менялась) или если
  предыдущей копии не было вовсе. `RefreshMapDataUseCase` при `true` и
  непустом списке регионов делает `delete(name)` +
  `downloadRegion(...)` с теми же bounds/zoom для каждого — старый пак
  специально удаляется первым, не просто пересоздаётся поверх, иначе
  `OfflineRegionRepositoryImpl.findPack` (поиск по имени, у `OfflinePack`
  нет id) поймал бы дубликаты по одинаковому имени. Реальная закачка
  байтов идёт в фоне как обычно — прогресс виден в «Подготовке», а в
  Настройках просто показывается одноразовая надпись со счётчиком
  перекачиваемых регионов (`SettingsUiState.mapDataRegionsRedownloading`).
- **`MapLoadFailedBanner.kt`** (`ui/components/`) — плавающий оверлей,
  подписанный на `onMapLoadFailed`/`onMapLoadFinished` `MaplibreMap(...)`
  (параметры библиотеки, раньше нигде не использовались) — единственный
  способ, которым MapLibre сигнализирует о неудачной загрузке; сам движок
  просто рисует пустой фон без собственной ошибки.
  **Этого одного сигнала оказалось недостаточно** — подтверждено живым
  репортом пользователя (ISP блокирует `tiles.openfreemap.org`, VPN
  выключен): баннер не появлялся вообще, хотя ни один тайл не грузился.
  Причина: `onMapLoadFailed`/`onMapLoadFinished` — сигнал уровня
  style/карты в целом, не отдельных тайлов. После пиннинга стиля (см. ниже)
  `style.json` грузится из локального файла мгновенно и без сети, поэтому
  `onMapLoadFinished` срабатывает сразу и «успешно» независимо от того,
  доступна ли сеть — а именно ДО пиннинга сетевой сбой хотя бы иногда ронял
  саму загрузку style.json и таким образом попадал в `onMapLoadFailed`.
  У `maplibre-compose` нет публичного per-tile колбэка ошибки (см.
  исследование в пункте про офлайн-паки выше). Фикс —
  `MapStyleCacheRepository.isTileHostReachable()`: независимый
  connectivity-пробник, переиспользующий тот же `HttpTextFetcher`, что и
  пиннинг — лёгкий фетч `style.json` (не пишет в пиненный файл, чисто
  read-only проверка) с таймаутом 8с, вызывается из `LaunchedEffect`
  каждой из трёх карт ПОСЛЕ `ensureLoaded()`, параллельно с попыткой
  MapLibre сама загрузить тайлы. Баннер показывается, если сработал ЛИБО
  `onMapLoadFailed`, ЛИБО пробник — не дожидаясь второго, если первый уже
  подтвердил проблему.
  **Первая версия пробника не работала вообще** — подтверждено вторым живым
  репортом (баннер по-прежнему не появлялся, хотя нужный код уже стоял на
  устройстве — проверено вытаскиванием установленного APK и поиском
  символа `isTileHostReachable` в dex). Причина —
  `AndroidHttpTextFetcher` (`data/platform/`) не задавал
  `connectTimeout`/`readTimeout` на `HttpURLConnection` (по умолчанию —
  без таймаута вообще). ISP, тихо роняющий пакеты (не сбрасывающий
  соединение активно), подвешивал блокирующий сетевой вызов НАВСЕГДА —
  а `withTimeoutOrNull` вокруг него в `isTileHostReachable` НЕ спасает:
  отмена корутины кооперативная, блокирующий `java.net`-вызов внутри
  `withContext(Dispatchers.IO)` её не видит и не проверяет. Реальным
  таймаутом обязано быть свойство самого соединения
  (`connection.connectTimeout`/`readTimeout`, сейчас 5с), не внешний
  `withTimeoutOrNull` — тот в лучшем случае подстраховка. На iOS этой
  проблемы нет: `IosHttpTextFetcher` — на `suspendCancellableCoroutine`+
  `NSURLSessionDataTask`, отмена которого (`task.cancel()`) честно
  кооперативная.
- **`Dispatchers.IO` — `internal` на Kotlin/Native**, не резолвится в
  `commonMain` (в отличие от JVM/Android) — `MapStyleCacheRepository`
  использует `Dispatchers.Default` для файлового I/O и сетевого запроса
  вместо него.
- **Сетевой запрос `style.json` — свой `expect`/`actual` `HttpTextFetcher`
  (`data/platform/`), не Ktor**, хотя Ktor был первой попыткой (правило
  проекта — кроссплатформенная библиотека вперёд `expect`/`actual`).
  Пришлось откатить по двум независимым причинам, каждая — реальная,
  подтверждённая компиляцией/сборкой, не гипотетическая:
  - **`ktor-client-core` (Android-артефакт, 3.2.0) не дексуется на
    `minSdk = 24`.** Библиотека содержит метод, буквально названный
    `` `use streaming syntax` `` (Kotlin-идиома: скрытый deprecated-стаб,
    чьё ИМЯ — это и есть текст ошибки компилятора для тех, кто дёрнет
    старый API) — легальное имя в `.class`, но DEX-формат ниже версии 040
    (её требует `minSdk 24`) запрещает пробелы в `SimpleName`. Падало на
    `:androidApp:mergeExtDexDebug` с `com.android.tools.r8.internal.wx:
    Space characters in SimpleName 'use streaming syntax' are not allowed
    prior to DEX version 040` — не баг в коде приложения, чисто
    несовместимость версии этой конкретной джарки с `minSdk` проекта.
  - **`NSURLSession.dataTaskWithURL(url:completionHandler:)` не резолвится
    из Kotlin/Native в этом биндинге Foundation** (подтверждено пробным
    вызовом с разной arity/типами — компилятор видит только
    однопараметрический `dataTaskWithURL(url:): NSURLSessionDataTask`,
    completion-handler-перегрузка не экспонирована как отдельный
    Kotlin-оверлоад, хотя сам селектор `dataTaskWithURL:completionHandler:`
    есть в строковой таблице klib). Рабочий обход — **делегат-based API**:
    свой `NSURLSessionDataDelegateProtocol` (`URLSession(session:dataTask:
    didReceiveData:)`/`URLSession(session:task:didCompleteWithError:)`),
    сессия создаётся через `sessionWithConfiguration(configuration:delegate:
    delegateQueue:)`, `dataTaskWithURL(url:)` (без хендлера) + `.resume()`.
    **Делегат — `private var` НА КЛАССЕ** `IosHttpTextFetcher`, не локальная
    переменная внутри `fetchText` — тот же ARC-баг, что и `CLLocationManager`
    (`iosMain/CLAUDE.md`): без внешней сильной ссылки делегат освобождается
    до того, как успеет прийти колбэк.
  - `NSData → String`: `NSString.create(data:encoding:)` даёт warning
    «cast only succeeds when null» в этом биндинге — вместо нативного
    NSString-бриджинга декодировать вручную: `NSData` → `ByteArray` через
    `usePinned`+`memcpy` (`bytes`/`length`) → `ByteArray.decodeToString()`.

## Грабли

- **`LiveTrackMap` раньше центрировала камеру на КАЖДЫЙ новый GPS-фикс
  безусловно** — на «Записи» это означало рекадрирование каждые несколько
  секунд под ЛЮБЫМ жестом пользователя (пан/зум). Фикс — `followEnabled`
  (внутреннее состояние `LiveTrackMap`) гасится на любой жест и
  автоматически возвращается через `FOLLOW_RESUME_DELAY` (10с) простоя без
  новых жестов — первая версия фикса гасила его насовсем (one-way), что на
  длинной прогулке оставляло пользователя навсегда «потерянным» на карте
  после случайного/намеренного пана; таймер перезапускается на КАЖДЫЙ новый
  жест, не только на первый.
  Значение читается через `isCameraMoving`, а не через сам `moveReason`:
  `moveReason` — sticky (никогда не сбрасывается обратно в `NONE` между
  жестами), поэтому повторная запись того же `GESTURE` — no-op под
  структурным equality Compose и не эмитится из `snapshotFlow`; второй/
  третий жест подряд был бы попросту невидим. `isCameraMoving` — честно
  чередующийся `true`/`false` на каждый дискретный жест (нативные
  `onCameraMoveStarted`/`onCameraIdle`), поэтому именно за ним следит
  `snapshotFlow`, а `moveReason` в момент каждого такого edge просто
  ЧИТАЕТСЯ (не собирается), чтобы отличить жест пользователя от наших же
  программных прыжков (`cameraState.position = ...`, `jumpTo` на bounds —
  репортятся библиотекой как `PROGRAMMATIC`, не `GESTURE`, и не трогают ни
  выключатель, ни таймер).
- **Долгий тап по маркеру места («навигация к месту») сначала был сделан
  Compose-оверлеем поверх карты (`MarkerLongPressOverlay.kt`, удалён) — на
  iOS не срабатывал НИКОГДА, вне зависимости от длительности удержания.**
  Причина не в рекадрировании выше (это лишь усугубляло проблему на
  Android), а в том, что `IosMapView.kt` (`maplibre-compose`) встраивает
  `MLNMapView` через `UIKitInteropProperties(interactionMode =
  UIKitInteropInteractionMode.NonCooperative)` — под этим режимом касания
  внутри границ interop-вью в принципе не доходят до соседнего Compose
  composable-а, независимо от z-order и размера hit-таргета; маленькие
  (2×`hitRadius`) хотспоты не помогали, потому что проблема не в размере, а
  в том, что Compose эту область вообще не хит-тестит. Фикс — отказ от
  Compose-жеста в пользу НАТИВНОГО `onLongClick` у `SymbolLayer`
  (`PlaceMarkersLayer.kt`) — то же самое `addOnMapLongClickListener`/
  `UILongPressGestureRecognizer`, которым уже отдан обычный `onClick`
  (проверенно рабочий на обеих платформах). Цена — длительность удержания
  теперь дефолтная (OS ~500мс), не подконтрольная нам, библиотека не даёт
  способа её настроить; это сознательный компромисс, не задел на будущее.
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
- **Оборачивание `MaplibreMap(...) { ... }` в локальный `Box` (для оверлея вроде
  `MapLoadFailedBanner`) даёт безобидный warning компилятора** — `Calling a
  MapLibre Composable composable function where a UI Composable composable
  was expected`, на самом `MaplibreMap(...)` и на `LineLayer`/др. вызовах
  внутри его `content`-лямбды. Причина — инференс `@ComposableTarget`
  (`@MaplibreComposable`, `org.maplibre.compose.util.MaplibreComposable`)
  путается, когда `MaplibreMap` — не единственное выражение тела функции, а
  вложено в лямбду `Box.content` (у неё самой никакого target-маркера нет).
  На рантайм не влияет: `MaplibreMap`, вызванный из `Box` НА УРОВЕНЬ ВЫШЕ
  (в вызывающем экране, не в теле функции с самим `MaplibreMap`), уже
  повсеместно использовался так и раньше без единого warning'а — эффект
  чисто про то, где ТЕКСТУАЛЬНО лежит вызов, не про итоговое дерево.
  Проверено: подтверждено сравнением warning-листа до/после (`git stash`).
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
- **Карта под `Scaffold`+`TopAppBar` — не заводить.** `WalkMapScreen.kt`
  раньше оборачивала карту в `Scaffold(topBar = TopAppBar(...))` и применяла
  `Modifier.fillMaxSize().padding(padding)` — на Android работало, но на
  iOS весь вьюпорт (включая орнаменты) визуально проседал на величину
  сильно больше реальной высоты `TopAppBar` (похоже на баг подсчёта инсетов
  в CMP `Scaffold`/`TopAppBar` на iOS, не воспроизводится на Android).
  Заменено на full-bleed карту (`Modifier.fillMaxSize()` без чужого
  `padding`) с плавающей кнопкой «назад» поверх, тем же паттерном, что уже
  использовали `RecordScreen.kt`/`MapScreen.kt` для своих плавающих кнопок
  (`Modifier.align(Alignment.TopStart).padding(top = 31.dp, start = 16.dp)`
  — фиксированный отступ, не `WindowInsets`/`Scaffold`-паддинг). Других карт
  под `Scaffold`+`TopAppBar` в проекте больше нет — не заводить новую без
  проверки именно на iOS.
- **`rememberPlaceMarkerPainter` (`PlaceMarkerIcon.kt`): Android hardware
  bitmaps крашат MapLibre-бейкинг — подтверждено по logcat реального
  устройства (Pixel 4a).** `ImageManager.drawToBitmap` (`maplibre-compose`)
  рисует маркерный `Painter` на ПРОГРАММНОМ `Canvas`/`ImageBitmap`; Coil3 на
  Android по умолчанию декодирует фото в `Bitmap.Config.HARDWARE` (GPU-only
  текстура) везде, где это возможно — рисование такого bitmap на
  программный canvas кидает `IllegalArgumentException: Software rendering
  doesn't support hardware bitmaps` (`BaseCanvas.throwIfHwBitmapInSwMode`),
  роняя приложение мгновенно при сохранении места с фото. На iOS этой
  проблемы нет в принципе — там у Coil3 нет понятия hardware bitmap. Фикс —
  `ImageRequest.Builder.disallowHardwareBitmaps()` (expect/actual,
  `data/platform/CoilRequestConfig.kt`, на Android — `allowHardware(false)`,
  на iOS — no-op) на каждый запрос фото для маркера.
- **Тот же `ImageRequest` также обязан задавать `size(...)` явно** —
  отдельная, не связанная с крашем выше оптимизация. Painter, полученный из
  `rememberAsyncImagePainter`, сам никогда не рисуется (наружу отдаётся
  только `state.painter` из `onSuccess` — см. doc-комментарий там же про
  идентичность пейнтера для MapLibre) — значит Coil-овский
  `DrawScopeSizeResolver`, который вычисляет размер декодирования из
  реального места отрисовки (`AsyncImagePainter.onDraw`), никогда не
  срабатывает, и Coil молча откатывается на `SizeResolver.ORIGINAL` —
  декодирует фото с камеры (часто 10+ МП) в ПОЛНОМ разрешении на КАЖДУЮ
  загрузку. Фикс — собирать `ImageRequest` вручную с `.size(width, height)`
  в px под размер маркера (`PLACE_MARKER_WIDTH`/`HEIGHT`), а не передавать
  голый путь/URL строкой.

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
- **iOS: `SIGKILL` (`0x8BADF00D`, watchdog) на уходе в фон, если в этот
  момент MapLibre грузит тайл, а сеть внезапно полностью пропала**
  (подтверждено краш-репортом с реального устройства, iOS 15.8.8). Главный
  поток блокируется внутри нативного MapLibre на `std::future<void>::get()`,
  вызванного из обработчика `UIApplicationDidEnterBackgroundNotification` —
  ждёт завершения какой-то фоновой операции (похоже на паузу
  рендеринга/отмену загрузки тайла), которая должна быть быстрой, но
  зависает, пока не сработает сетевой таймаут. Если это совпадает с уходом
  в фон, 5-секундный сторожевой таймер убивает приложение раньше. Баг сидит
  в самом нативном SDK (`maplibre-native-ios 6.25.1` через
  `maplibre-compose 0.13.0`), не в коде приложения — фикса на нашей стороне
  нет, воспроизводится редко (нужно поймать точный момент: активная загрузка
  тайла + мгновенное отключение и мобильного интернета, и Wi-Fi + уход в
  фон).
- **iOS: одиночный наблюдавшийся случай — маркер-иконка места (кружок с
  center-crop фото, `PlaceMarkerPainter` в `PlaceMarkerIcon.kt`) отрисовалась
  на весь экран растянутым квадратом вместо крошечного пина**, поверх
  всего интерфейса (не краш — приложение продолжало отвечать), само
  прошло после ухода в фон и возврата (пересоздание Metal-дровейбла). По
  краш-репортам/`idevicesyslog` на устройстве — ничего (ни креша, ни
  относящихся к делу строк лога от самого приложения на iOS 15.8.8). Форма
  артефакта (именно center-crop круглой иконки, не полное фото) указывает,
  что тянулась именно баунченная `SymbolLayer`-иконка места, не
  Compose-слой `AddPlaceDialog`. Рабочая гипотеза — гонка между ребейком
  иконки в `rememberPlaceMarkerPainter` (новый `Painter` при каждом
  `onSuccess`, см. doc-комментарий там же про identity-кэш) и релейаутом
  interop `MLNMapView` в момент открытия `AddPlaceDialog` поверх карты по
  тапу на маркер — не подтверждено логами, не воспроизведено намеренно.
  Если повторится — зафиксировать, был ли это давно закэшированный маркер
  или только что созданный, и шла ли в этот момент активная GPS-запись.
