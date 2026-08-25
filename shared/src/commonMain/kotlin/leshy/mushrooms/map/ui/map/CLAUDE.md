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

## Маркер пользовательского вида (`MushroomMarkerIcon.kt`, `user-mushrooms.md`)

`rememberMushroomMarkerPainter(source: CategoryIconSource)` — единственная
точка, где карта резолвит иконку маркера что для каталожного (`Bundled`),
что для своего (`UserFile`) вида; оба `LiveTrackMap`/`ClusteredFindsLayers`
зовут её, а не `CategoryIcon` (тот — обычный Compose UI, MapLibre нужен
именно `Painter` для бейкинга в бренд). Идентификатор слоя строится из
`CategoryIconSource.key`, а не из голого `iconRef`/`nameKey` — `Bundled` и
`UserFile` с одинаковым `nameKey` в принципе невозможны (одна и та же
категория не бывает сразу каталожной и своей), но `key` всё равно различает
их по конструкции типа, а не по строковому совпадению.

`rememberUserIconPainter` — намеренно тот же паттерн, что
`rememberPlaceMarkerPainter` (`PlaceMarkerIcon.kt`, см. «Грабли» ниже, не
дублирую здесь): забирает `Painter` из `AsyncImagePainter.State.Success` в
`onSuccess`, а не передаёт `model` напрямую в `image(...)`, задаёт
`ImageRequest.size(...)` явно и `disallowHardwareBitmaps()`. Все три причины
теми же, что там — идентичность пейнтера для MapLibre, отсутствие
draw-time size inference (пейнтер никогда реально не рисуется на экран),
Android hardware bitmaps роняют софт-канвас бейкинга. `MushroomMarkerPainter`
поверх (fit-scale + центрирование в запрошенный размер бейка) — своя
обёртка, потому что маркер places квадратной иконкой без circle-кропа/фона
(в отличие от `PlaceMarkerPainter`, который делает center-crop в круг), и
исходное фото своего вида почти никогда не квадратное.

**Не проверено живьём на карте в Phase 7** — симуляторный прогон (см. план)
дошёл до формы создания вида (фото из галереи через `PHPickerViewController`
загружается и превьюшка рендерится корректно), но не до сохранения вида и
появления его маркера на карте: автоматизация тапов через `cliclick`/
System Events в этой сессии смогла кликать и фокусировать поля (курсор,
подсветка рамки), но не смогла доставить текст в Compose-текстовое поле на
iOS-симуляторе (ни `cliclick t:`, ни `osascript keystroke` — текст не
появлялся, хотя фокус визуально брался), так что вид без названия сохранить
через форму не вышло. Путь `rememberUserIconPainter`/`MushroomMarkerPainter`
на живой карте (маркер своего вида, кластеризация наравне с каталожными,
краш на Android hardware bitmaps из «Грабли» ниже) остаётся непроверенным
живьём — на пользователе (см. `feedback_no_self_testing`).

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
  Возврат к голому URL реоткрывал узкое окно дрейфа: если сервер
  ротировал снапшот именно между последним пиннингом и текущим
  скачиванием, новый регион скачался бы под ДРУГИМ URL-шаблоном тайлов,
  чем показывает живая карта — и оставался бы так навсегда, «Complete» от
  нативного SDK при этом абсолютно честен (все тайлы, которые пак
  запрашивал, реально скачаны, просто по чужому шаблону). **Закрыто
  структурно** (не просто задетектировано) — см. «Перехват HTTP-клиента»
  ниже: с этого момента `downloadRegion()`/`onRetryClicked()`/
  `RefreshMapDataUseCase` резолвят `OPEN_FREE_MAP_STYLE_URL` в те же самые
  байты, что уже использует живая карта, а не в свежий сетевой фетч —
  дрейф между запиненным и скачанным физически не может возникнуть для
  всего, что скачано/перекачано ПОСЛЕ этого фикса. Отдельный баннер-
  предупреждение о дрейфе (`OfflineRegionRepository.isStyleDrifted()`,
  `MapStyleCacheRepository.isRemoteStyleDrifted()`, `PreparationUiState.
  styleDriftWarningVisible`, `StyleDriftWarningBanner`) существовал раньше
  как путь обнаружения/починки для уже скачанных регионов, но **убран
  целиком** по решению владельца продукта — раз сам класс бага для новых
  скачиваний закрыт структурно, а старые регионы просто удаляются и
  перекачиваются заново вручную, второй (детектирующий) уровень защиты
  стал чистым мёртвым кодом, а не подстраховкой на будущее.

### Перехват HTTP-клиента нативного SDK (`data/platform/PinnedStyleInterceptor.kt`)

Раз нативный офлайн-загрузчик умеет резолвить `styleUrl` только через свой
собственный сетевой HTTP-клиент (не `file://`, см. выше, и без параметра
«вот тебе готовый JSON» в его API), а нам нужно, чтобы он читал ровно те
байты, что уже запинены локально — единственный способ дать ему это без
реального похода в сеть — перехватить сам HTTP-клиент SDK, а не пытаться
передать что-то через `styleUrl`.

- **`PinnedStyleInterceptor`** (`commonMain`, интерфейс) — `MapStyleCacheRepository`
  зовёт `setPinnedStyle(json)` при каждом обновлении `_baseStyle` (и в
  `ensureLoaded()`, и в `refreshFromNetworkLocked()`), так что перехватчик
  всегда отдаёт актуальную запиненную копию.
- **Android (`AndroidPinnedStyleInterceptor`)** — на конструкторе один раз
  ставит `HttpRequestUtil.setOkHttpClient(...)` (`org.maplibre.android.
  module.http`, публичный API нативного `org.maplibre.gl:android-sdk`) с
  собственным `OkHttpClient` + `Interceptor`: запрос на
  `OPEN_FREE_MAP_STYLE_URL` — синтетический `Response` из текущих
  запиненных байт БЕЗ `chain.proceed()` (реального сетевого похода не
  происходит вообще), любой другой URL — `chain.proceed()` без изменений.
  **`setOkHttpClient` подменяет клиент для ВСЕГО сетевого трафика SDK**
  (тайлы живой карты, ambient-кэш, офлайн-скачивание) — короткое замыкание
  специально узкое (точное совпадение URL), чтобы не задеть ничего, кроме
  резолва стиля. `okhttp3` не был виден на compile classpath напрямую (SDK
  тянет его только как собственную `implementation`-зависимость, видна
  только в `androidRuntimeClasspath`) — пришлось добавить явную
  `implementation(libs.okhttp)` в `androidMain`, версия синхронизирована с
  уже резолвящейся рантайм-версией (`4.12.0`, проверено
  `:shared:dependencies --configuration androidRuntimeClasspath`).
- **iOS (`IosPinnedStyleInterceptor`)** — свой `NSURLProtocol`-наследник
  (`PinnedStyleURLProtocol`), зарегистрированный через `NSURLSessionConfiguration
  .defaultSessionConfiguration` (каждый доступ отдаёт новый, независимо
  мутируемый объект — копировать не нужно) с добавленным в
  `protocolClasses` нашим классом, присвоенную в `MLNNetworkConfiguration.
  sharedManager.sessionConfiguration` — заголовок `MLNNetworkConfiguration.h`
  прямо предписывает делать это «before instantiating any MLNMapView, or
  using MLNOfflineStorage». Первый в проекте случай переопределения
  ObjC-метода КЛАССА (`canInitWithRequest:`, `+`, не `-`) из Kotlin/Native —
  паттерн: `companion object : NSURLProtocolMeta() { override fun ... }`
  (K/N генерирует `<Class>Meta` под метакласс ObjC-типа). Требует явного
  `@OverrideInit`-конструктора, форвардящего в `super(request, cachedResponse,
  client)` — без него компилятор требует инициализатор
  («This type has a constructor, so it must be initialized here»), т.к. у
  `NSURLProtocol` нет беспараметрового `init`. Компилятор попутно ругается на
  сам `@OverrideInit`-конструктор (`CONFLICTING_OVERLOADS`, suppressed) и на
  `(json as NSString)` перед `dataUsingEncoding` («This cast can never
  succeed» — `kotlin.String`/`NSString` toll-free bridged, но перегрузка
  резолвится только через явно NSString-типизированный ресивер) — оба
  warning'а безобидны и не найдены применимыми альтернативами при отладке
  (см. коммит, добавивший этот файл, если пригодится история проб).
- Оба перехватчика регистрируются в Koin как `single(createdAtStart = true)`
  — должны установиться ДО того, как что-либо (`OfflineManager`, любая
  `MaplibreMap`) успеет коснуться сети первым запросом; `createdAtStart`
  гарантирует это, инстанцируясь сразу по завершении `startKoin { ... }`
  в `initKoin()`, до первой Compose-композиции. **Само по себе это гарантирует
  только, что перехватчик УСТАНОВЛЕН — не то, что ему есть чем ответить.**
  Байты в него кладёт `MapStyleCacheRepository.ensureLoaded()`, а он раньше
  звался только лениво, из composable конкретного экрана с картой
  (`RegionPickerMap.kt` и др.). На «Подготовке» это гонка без гарантии
  порядка с `PreparationViewModel.init` — а тот первым создаёт нативный
  `OfflineManager` (`getOfflineManager()`/`MapLibre.getInstance()`), что
  может само по себе разбудить нативный автовозврат к докачке пака,
  оставшегося `ACTIVE` после убитого процесса — если это случится раньше,
  чем `ensureLoaded()` успеет положить байты, докачка в этом узком окне
  снова уйдёт в реальную сеть мимо перехвата. Закрыто — `App.kt` теперь
  тоже зовёт `mapStyleCacheRepository.ensureLoaded()` (параллельным
  `LaunchedEffect`, рядом с `RepairPhotoPathsUseCase`) сразу при первой
  композиции после онбординга, до того как пользователь вообще может
  куда-то перейти — вызов из самих экранов остался (безвредно, идемпотентно
  по документации самого `ensureLoaded()`).
- **Android: `AndroidPinnedStyleInterceptor` крашил приложение на старте
  насмерть** — подтверждённый живой краш при первом запуске
  (`ExceptionInInitializerError` → `MapLibreConfigurationException:
  Using MapView requires calling MapLibre.getInstance(...) before
  inflating or creating the view`). Причина: `HttpRequestUtil.
  setOkHttpClient(...)` сам триггерит статическую инициализацию
  `HttpRequestImpl` (нужна для сборки User-Agent строки через
  `MapLibre.getApplicationContext()`), которая падает, если нативный SDK
  ещё ни разу не инициализирован — а этот перехватчик `createdAtStart`,
  то есть он самый первый код в приложении, который вообще трогает
  MapLibre, раньше обычного первого вызова (`AndroidOfflineManager`'s
  `MapLibre.getInstance(context)`, который до этого момента и был неявно
  первым). Фикс — `AndroidPinnedStyleInterceptor` теперь сам принимает
  `Context` и явно зовёт `MapLibre.getInstance(context)` ПЕРЕД
  `setOkHttpClient(...)`; `getInstance` — synchronized singleton-геттер,
  повторный вызов из `AndroidOfflineManager` позже безопасен. Проверено
  живьём на Pixel 4a (`adb logcat` до и после фикса).
- **iOS-сторона этой проблеме не подвержена** — у `MLNNetworkConfiguration`
  нет аналога Android-контекста/статического юзер-агента, который надо
  было бы предварительно инициализировать; `IosPinnedStyleInterceptor`
  компилируется и устанавливается без него. Живьём на iOS-устройстве
  фактическая офлайн-отрисовка региона (не только отсутствие краша) пока
  не подтверждена — см. `feedback_no_self_testing`.
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
- **Баннер-предупреждение о дрейфе стиля существовал и был убран в ту же
  сессию, что и перехват HTTP-клиента.** Короткая история для контекста
  (искать в `git log`, если понадобятся детали): изначально проверка дрейфа
  запускалась только сразу после `downloadRegion()`/`onRetryClicked()` —
  подтверждённый живой баг показал, что этого недостаточно (сервер может
  ротировать снапшот уже ПОСЛЕ того, как регион успешно докачался, и старая
  проверка это никогда не ловила), поэтому её сначала расширили на разовый
  прогон при каждом заходе на «Подготовку». Затем появился перехват
  HTTP-клиента (см. выше), который делает сам дрейф структурно невозможным
  для новых/перекачанных регионов — и в этот момент вся
  детектирующая машинерия (`isStyleDrifted()`, `isRemoteStyleDrifted()`,
  `styleDriftWarningVisible`, `StyleDriftWarningBanner`) стала не нужна и
  была удалена целиком, а не оставлена «на всякий случай» — по явному
  решению владельца продукта: старые (потенциально уже сдрейфовавшие)
  регионы просто удаляются и перекачиваются вручную, отдельный путь
  обнаружения для них не поддерживается.
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
- **iOS: главный поток блокируется внутри `MapLibre.framework` на уходе в
  фон (`std::future<void>::get()`, вызванный из обработчика
  `UIApplicationDidEnterBackgroundNotification`) — воспроизведено ДВАЖДЫ на
  реальном устройстве (iPhone 8,4, iOS 15.8.8) в пределах 5 дней, не
  «редкий» случай.** Первый раз (2026-08-17) закончился штатным
  `SIGKILL`/`0x8BADF00D` самого приложения (watchdog не дождался
  завершения за 5с, ~80с приложение до этого держало ~100% CPU). Второй раз
  (2026-08-22) — тяжелее: зависание было настолько полным, что утащило за
  собой ping-мониторинг ПЯТИ системных демонов (`logd`/`mediaserverd`/
  `runningboardd`/`thermalmonitord`/`wifid`, все — `WATCHDOG`/"5 monitored
  services unresponsive" одновременно) и закончилось полной перезагрузкой
  устройства (чёрный экран), не просто убийством приложения. Встроенный в
  эти watchdog-репорты общесистемный стекшот показал, что в этот момент
  два потока процесса `leshy` с `QOS_CLASS_USER_INTERACTIVE` держали
  ~106 из 112с общего CPU-времени процесса в `TH_RUN`, пока все
  `org.maplibre.mbgl.*`-воркеры простаивали в `TH_WAIT` — та же картина
  (что-то высокоприоритетное крутится/ждёт вечно, пул MapLibre бездействует),
  что и в первом инциденте. Гипотеза «сеть пропала во время загрузки тайла»
  как конкретный триггер зависания внутри MapLibre — правдоподобна, но НЕ
  подтверждена логами напрямую ни разу; не считать её единственной. Баг
  сидит в самом нативном SDK (`maplibre-native-ios 6.25.1` через
  `maplibre-compose 0.13.0`), фикса на нашей стороне пока нет. **Полная
  фактура, разобранные crash-репорты, методика вытаскивания логов с
  устройства через `libimobiledevice` и план дальнейшего расследования —
  `.claude/investigations/ios-maplibre-background-watchdog/README.md`.**
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
