# Второй источник карты: OpenStreetMap (vector.openstreetmap.org) рядом с OpenFreeMap

## Контекст

Сейчас всё приложение жёстко привязано к одному векторному стилю
OpenFreeMap (`OPEN_FREE_MAP_STYLE_URL` в `ui/map/MapStyle.kt`) — и как
живая подложка карты, и как единственный `styleUrl` для офлайн-скачивания
регионов. Пользователь попросил добавить переключаемый второй источник —
OpenStreetMap — в раздел «Выбор источника карты» в Настройках сразу после
«Данные карты», тем же переключателем (`SingleChoiceSegmentedButtonRow`),
что уже используется для языка.

По уточнению пользователя: «OpenStreetMap» — это не другой стиль на тех же
тайлах OpenFreeMap, а **другой провайдер**: `vector.openstreetmap.org` —
официальная векторная инфраструктура самого OpenStreetMap Foundation
(Shortbread-схема тайлов). Проверено (веб-поиск/фетч 21.08.2026): готовый
самодостаточный MapLibre-стиль без API-ключа лежит по адресу
`https://vector.openstreetmap.org/styles/shortbread/colorful.json`
(`version: 8`, `sources`/`layers`/`sprite`/`glyphs` — всё на том же домене,
никаких `{key}`-плейсхолдеров). Это именно тот тип провайдера, который
раньше уже разбирался в проекте — но тогда речь шла о СТАРЫХ растровых
тайлах `tile.openstreetmap.org` (отклонены, см. `ui/map/CLAUDE.md`, раздел
«Стиль карты и превью»: размытость на HiDPI и нарушение usage policy
прямого хотлинка). `vector.openstreetmap.org` — современная официальная
замена именно под MapLibre/векторный рендеринг, не тот же самый растровый
сервис.

**Важная новая деталь для учёта, не блокер:** policy-страница OSMF для
этого сервиса (`operations.osmfoundation.org/policies/vector/`) явно
запрещает «bulk downloading/scraping» и требует валидный User-Agent,
обязательную атрибуцию и локальное кеширование ≥7 дней по HTTP-заголовкам
экспирации. Офлайн-скачивание региона («Подготовка») по своей природе —
именно массовое скачивание тайловой пирамиды для площади, т.е. формально
похоже на то, что policy просит не делать без разбора. Явных упоминаний
API-ключа/квоты/блокировки для мобильных офлайн-скачиваний в политике нет,
но это стоит держать в уме как потенциальный риск на будущее (по аналогии
с тем, как для OpenFreeMap уже случался инцидент блокировки ISP) — не
меняет план реализации, просто учитывать при живом тестировании
офлайн-скачивания под этим источником.

Главный источник риска для самой фичи — офлайн-скачанные регионы
(«Подготовка»). Каждый
скачанный `OfflinePack` намертво привязан к тому `styleUrl`, под которым
его скачали (нативный MapLibre SDK хранит `styleUrl` как часть
`OfflinePackDefinition`, тайлы кэшируются по URL). Простое переключение
активного слоя не «переносит» уже скачанные тайлы на новый стиль — они
статистически останутся невидимы офлайн под новым слоем. По решению
пользователя: при переключении слоя, если есть регионы, скачанные под
ПРЕДЫДУЩИМ (сейчас активным) слоем, показывать предупреждение со списком
этих регионов (имя + размер) и тремя действиями — «Перезагрузить»
(удалить и перекачать под новым слоем), «Удалить», «Отмена». Это
переиспользует ровно ту же связку delete+redownload, что уже есть в
`RefreshMapDataUseCase` для сценария дрейфа контента стиля.

## Ключевое архитектурное решение

**`OfflinePackDefinition.TilePyramid.styleUrl` уже хранится по каждому
паку нативно** (подтверждено чтением `OfflineRegionRepositoryImpl.
toRegionInfo`, строка `pack.definition as? OfflinePackDefinition.
TilePyramid`) — отдельное поле/метаданные для тега «под каким слоем
скачан регион» заводить не нужно. `OfflineRegionInfo` получает новое поле
`basemapSource: MapBasemapSource`, которое `toRegionInfo()` вычисляет,
сравнивая `definition.styleUrl` с известными URL из `MapBasemapSource`
(старые паки, скачанные до этой фичи, имеют `styleUrl ==
OPEN_FREE_MAP.styleUrl` и корректно распознаются как `OPEN_FREE_MAP` без
всякой миграции — офлайн-регионы и так не в Room, отдельная Room-миграция
не нужна вообще, `OfflineManager` сам persistent-каталог).

Оба источника — векторные тайлы (не растр), поэтому архитектурно
переиспользуют один и тот же путь пиннинга/офлайн-скачивания без
отдельного «растрового» кода. Байтовый размер тайла у Shortbread-схемы
OSM может отличаться от OpenFreeMap — `OfflineRegionEstimator.
AVG_TILE_BYTES` пока остаётся общей константой на оба источника
(меняться не будет в рамках этой правки), но оценка размера при скачивании
под OpenStreetMap может быть менее точной, чем под OpenFreeMap, пока не
откалибрована отдельно — не блокер, отметить как известное ограничение,
поправимо позже по факту реальных цифр с устройства.

## Домен-модель

**`domain/model/MapBasemapSource.kt`** (новый файл, паттерн — точная копия
`AppLanguage.kt`):
```kotlin
enum class MapBasemapSource(val styleUrl: String, val displayName: String) {
    OPEN_FREE_MAP("https://tiles.openfreemap.org/styles/liberty", "OpenFreeMap"),
    OPEN_STREET_MAP("https://vector.openstreetmap.org/styles/shortbread/colorful.json", "OpenStreetMap"),
}
```
`displayName` — хардкод-литерал на записи enum, не `StringKey` — то же
решение, что уже принято для `AppLanguage.displayName` («Русский»/
«English» не переводятся, это имена собственные).

`ui/map/MapStyle.kt` — старый `OPEN_FREE_MAP_STYLE_URL` top-level const
удаляется, все 8 текущих потребителей (`MapStyleCacheRepository`,
`OfflineRegionRepositoryImpl`, оба `PinnedStyleInterceptor`, оба
`WalkThumbnailRenderer`, `MapLoadFailedBanner`) переключаются на
`MapBasemapSource.OPEN_FREE_MAP.styleUrl` / параметр текущего источника.
Док-комментарий про причину выбора вектора вместо растра из этого файла
переезжает в KDoc над `MapBasemapSource`, с уточнением, что
`vector.openstreetmap.org` — современный официальный векторный сервис
OSMF (Shortbread), отдельный от старого отклонённого растрового
`tile.openstreetmap.org`.

**`domain/model/OfflineRegionInfo.kt`** — добавить поле
`val basemapSource: MapBasemapSource`.

**`domain/repository/SettingsRepository.kt`** — добавить пару, тем же
паттерном, что `observeLanguage`/`setLanguage`:
```kotlin
fun observeMapBasemapSource(): Flow<MapBasemapSource>
suspend fun setMapBasemapSource(source: MapBasemapSource)
```

**`domain/repository/OfflineRegionRepository.kt`** — `downloadRegion(...)`
получает новый обязательный параметр `basemapSource: MapBasemapSource`.

## Персистентность (DataStore)

**`data/repository/SettingsRepositoryImpl.kt`** — новый ключ, ровно
паттерн `LANGUAGE_KEY`/`MUSHROOM_SORT_ORDER_KEY`:
```kotlin
private val MAP_BASEMAP_SOURCE_KEY = stringPreferencesKey("map_basemap_source")
```
`observeMapBasemapSource()` читает по `.name`, фоллбэк
`MapBasemapSource.OPEN_FREE_MAP` (сохраняет текущее поведение для уже
установленных копий — дефолт не меняется).

## Пиннинг стиля — переход на per-source (`data/repository/MapStyleCacheRepository.kt`)

Вместо одного `_baseStyle: MutableStateFlow<BaseStyle>` — по одному
`MutableStateFlow<BaseStyle>` на каждый `MapBasemapSource`
(`Map<MapBasemapSource, MutableStateFlow<BaseStyle>>`, оба
инициализированы `BaseStyle.Uri(source.styleUrl)`, тот же бутстрап-дефолт,
что и сейчас). Публичный доступ — `fun baseStyle(source: MapBasemapSource):
StateFlow<BaseStyle>`. `ensureLoaded`/`refreshFromNetwork`/
`isTileHostReachable` получают параметр `source: MapBasemapSource` и
оперируют файлом кэша, специфичным для источника
(`storage.resolvePath("style_${source.name}.json")` вместо общего
`style.json`) — иначе переключение слоя стирало бы уже запиненную копию
другого. Вся остальная логика (freeze-not-autoupdate, `Mutex`-guard,
сравнение previous/new для сигнала «контент реально изменился») остаётся
как есть, просто параметризуется источником — **сознательно НЕ через
собственный `CoroutineScope`/реактивную подписку на настройки внутри
репозитория** (это добавило бы незнакомый проекту паттерн
долгоживущего фонового коллектора в plain-классе); вместо этого
реактивность — на стороне трёх карт-потребителей (см. ниже), тем же
`collectAsState`-паттерном, что они уже используют.

**`data/platform/PinnedStyleInterceptor.kt`** (+ `AndroidPinnedStyleInterceptor`/
`IosPinnedStyleInterceptor`) — сейчас перехватчик хранит и матчит ОДНУ
запиненную пару (URL, JSON). Меняется на `Map<String, String>` (URL → JSON),
`setPinnedStyle(url: String, json: String)` — короткое замыкание срабатывает
на любой из уже запиненных URL, не только «текущий» (это важно: нативный
офлайн-загрузчик может в любой момент попросить URL источника, который
сейчас НЕ активен как живая подложка — например, при перекачке региона под
старым слоем после диалога «Перезагрузить», пока живая карта уже
переключена на новый).

## Офлайн-скачивание (`data/repository/OfflineRegionRepositoryImpl.kt`)

- `downloadRegion(..., basemapSource: MapBasemapSource)` — передаёт
  `basemapSource.styleUrl` в `OfflinePackDefinition.TilePyramid.styleUrl`
  вместо хардкод-константы.
- `toRegionInfo(pack, progress)` — резолвит `basemapSource` из
  `definition?.styleUrl` через `MapBasemapSource.entries.find { it.styleUrl
  == definition?.styleUrl } ?: MapBasemapSource.OPEN_FREE_MAP` (фоллбэк на
  случай гипотетического рассинхрона — не должен срабатывать на практике).

## Переключение слоя с предупреждением (новый юзкейс)

**`domain/usecase/SwitchMapBasemapSourceUseCase.kt`** (новый, паттерн —
как `RefreshMapDataUseCase`):
- `affectedRegions(newSource): List<OfflineRegionInfo>` — сравнивает с
  ТЕКУЩИМ активным источником (`settingsRepository.observeMapBasemapSource()
  .first()`), возвращает регионы, скачанные под ним же (пустой список — если
  выбор совпадает с текущим или скачанных регионов нет вообще).
- `applyWithoutRegions(newSource)` — просто `setMapBasemapSource`.
- `applyReload(newSource, regions)` — `setMapBasemapSource`, затем для
  каждого региона `delete(name)` + `downloadRegion(..., basemapSource =
  newSource)` с теми же bounds/zoom — **тот же порядок delete-затем-create**,
  что и в `RefreshMapDataUseCase` (не апдейт на месте — та же причина:
  `findPack` ищет по имени, оставленный старый пак поймал бы дубликат).
- `applyDelete(newSource, regions)` — `setMapBasemapSource`, затем
  `delete(name)` по каждому региону, без перекачки.

**`domain/usecase/RefreshMapDataUseCase.kt`** — правится под
множественность источников: читает текущий `MapBasemapSource`, фильтрует
`regionsBefore` только регионами ЭТОГО источника (регионы другого слоя,
который сейчас не активен, не трогает — «Обновить данные карты» относится
к тому, что сейчас реально показывается), вызывает `mapStyleCacheRepository
.refreshFromNetwork(source)`/`downloadRegion(..., basemapSource = source)`.

## Presentation (`presentation/settings/`)

**`SettingsUiState.kt`** — новые поля:
```kotlin
val mapBasemapSource: MapBasemapSource = MapBasemapSource.OPEN_FREE_MAP,
val pendingBasemapSwitch: PendingBasemapSwitch? = null,
```
```kotlin
data class PendingBasemapSwitch(val newSource: MapBasemapSource, val affectedRegions: List<OfflineRegionInfo>)
```

**`SettingsViewModel.kt`** — `init` получает ещё один `viewModelScope.launch
{ settingsRepository.observeMapBasemapSource().collect { ... } }` (та же
форма, что и `observeLanguage`). Новые обработчики:
- `onMapBasemapSourceSelected(source)` — считает `affectedRegions`; если
  пусто — сразу `applyWithoutRegions`; если нет — кладёт
  `pendingBasemapSwitch` в `uiState` (диалог).
- `onBasemapSwitchReload()` / `onBasemapSwitchDelete()` / `onBasemapSwitchCancel()`
  — читают `pendingBasemapSwitch`, зовут соответствующий метод юзкейса,
  сбрасывают `pendingBasemapSwitch = null`.

## UI (`ui/screens/SettingsScreen.kt`)

Новая секция сразу после конца «Данные карты» (после текущего блока
`if (uiState.mapCacheCleared) { ... }`, строка 156, до закрывающей скобки
`Column` на строке 157) — тот же паттерн, что языковой переключатель
(строки 71–82):
```kotlin
SettingsSectionTitle(stringResource(StringKey.SettingsMapSourceTitle))
SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
    MapBasemapSource.entries.forEachIndexed { index, source ->
        SegmentedButton(
            selected = uiState.mapBasemapSource == source,
            onClick = { viewModel.onMapBasemapSourceSelected(source) },
            shape = SegmentedButtonDefaults.itemShape(index = index, count = MapBasemapSource.entries.size),
        ) { Text(source.displayName) }
    }
}
```

Новый `AlertDialog` рядом с двумя существующими (после строки 197) — три
действия при двух штатных слотах Material3 `AlertDialog`
(`confirmButton`/`dismissButton`): `confirmButton` = «Перезагрузить»,
`dismissButton` = `Row` из «Удалить» + «Отмена» (тот же
`fillMaxWidth(0.9f)`+`DialogProperties(usePlatformDefaultWidth = false)`,
что у существующих двух диалогов). Список затронутых регионов — вертикально
внутри `text`, именем и размером через уже существующий
`formatMegabytes()` (`ui/util/Formatting.kt`), тот же, что использует чип
региона на «Подготовке»:
```kotlin
if (pending != null) {
    AlertDialog(
        onDismissRequest = viewModel::onBasemapSwitchCancel,
        modifier = Modifier.fillMaxWidth(0.9f),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        title = { Text(stringResource(StringKey.SettingsBasemapSwitchConfirmTitle)) },
        text = {
            Column {
                Text(stringResource(StringKey.SettingsBasemapSwitchConfirmMessage))
                Spacer(Modifier.height(8.dp))
                pending.affectedRegions.forEach { region ->
                    Text("• ${region.name} — ${formatMegabytes(region.completedBytes)} ${stringResource(StringKey.UnitMegabytes)}")
                }
            }
        },
        confirmButton = { TextButton(onClick = viewModel::onBasemapSwitchReload) {
            Text(stringResource(StringKey.SettingsBasemapSwitchReload)) } },
        dismissButton = {
            Row {
                TextButton(onClick = viewModel::onBasemapSwitchDelete) {
                    Text(stringResource(StringKey.SettingsBasemapSwitchDelete)) }
                TextButton(onClick = viewModel::onBasemapSwitchCancel) {
                    Text(stringResource(StringKey.SettingsBasemapSwitchCancel)) }
            }
        },
    )
}
```

## Три карты-потребителя (`ui/map/LiveTrackMap.kt`, `AggregatedFindsMap.kt`, `RegionPickerMap.kt`)

Одинаковая правка во всех трёх (сейчас — `MapStyleCacheRepository.
baseStyle` без параметра, строки ~173–184 в `LiveTrackMap.kt`, аналогично
в двух других): добавить `koinInject<SettingsRepository>()`,
`val mapBasemapSource by settingsRepository.observeMapBasemapSource()
.collectAsState(initial = MapBasemapSource.OPEN_FREE_MAP)`, заменить
`baseStyle` на `mapStyleCacheRepository.baseStyle(mapBasemapSource)
.collectAsState()`, `LaunchedEffect(Unit)` → `LaunchedEffect(mapBasemapSource)`
с `ensureLoaded(mapBasemapSource)`/`isTileHostReachable(mapBasemapSource)`
внутри — переключение слоя в Настройках подхватывается живой картой сразу
при следующей рекомпозиции, без перезахода на экран.

## Прочие точки, где зашит единственный источник

- **`App.kt`** (бутстрап-`LaunchedEffect` с `mapStyleCacheRepository.
  ensureLoaded()`) — читает текущий источник один раз
  (`settingsRepository.observeMapBasemapSource().first()`) и прогревает
  только его — не оба слоя сразу.
- **`data/platform/AndroidWalkThumbnailRenderer.kt`/`IosWalkThumbnailRenderer.kt`**
  — снапшоттер прогулки в архиве сейчас берёт голый
  `OPEN_FREE_MAP_STYLE_URL`; должен читать текущий `MapBasemapSource` через
  `SettingsRepository` в момент рендера снапшота (однократно, не
  реактивно — снапшот и так рендерится один раз при завершении прогулки).
- **`ui/screens/PreparationScreen.kt`/её ViewModel** — вызов
  `offlineRegionRepository.downloadRegion(...)` при старте скачивания
  региона должен передавать `basemapSource` — текущий выбранный слой
  (читается тем же способом, что уже используется в этом экране для других
  настроек, либо через `SettingsRepository` напрямую).
- **`ui/components/MapLoadFailedBanner.kt`** — текст баннера сейчас
  упоминает голый `OPEN_FREE_MAP_STYLE_URL`; переключить на текущий
  активный источник (или обобщить формулировку без конкретного URL —
  решить по месту при реализации, не архитектурный вопрос).

## i18n (`i18n/StringKey.kt` + `i18n/Strings.kt`)

Новые ключи после блока `SettingsMapData*`/`SettingsMapCacheCleared`
(строка 271 `StringKey.kt`): `SettingsMapSourceTitle` (RU «Выбор источника
карты», EN «Map source»),
`SettingsBasemapSwitchConfirmTitle`, `SettingsBasemapSwitchConfirmMessage`,
`SettingsBasemapSwitchReload`, `SettingsBasemapSwitchDelete`,
`SettingsBasemapSwitchCancel` — по одной строке в обеих ветках `when`
(`russianStrings`/`englishStrings`), компилятор форсирует полноту.
Формулировка сообщения (RU) — по тексту пользователя: «Для предыдущего
варианта карты уже скачаны следующие области: … которые не смогут
отображаться после переключения. Необходимо либо удалить их, либо
перезагрузить для использования с новым вариантом карты.» (список регионов
рендерится отдельно под этим текстом, не встраивается в саму строку).

## Проверка

- `./gradlew :shared:compileAndroidMain`,
  `:shared:compileKotlinIosSimulatorArm64` — компиляция обеих платформ.
- Полный `./gradlew build`/`:androidApp:assembleDebug` — по необходимости,
  с учётом граблей про OOM при параллельной линковке iOS (см. корневой
  `CLAUDE.md`).
- **Живое поведение — только на устройстве пользователя** (см.
  `feedback_no_self_testing`):
  1. Переключение слоя в Настройках без скачанных регионов — применяется
     сразу, без диалога, карта визуально меняет стиль.
  2. Переключение при наличии скачанных регионов — появляется диалог со
     списком (имя+размер), «Отмена» ничего не меняет, «Удалить» стирает
     перечисленные регионы, «Перезагрузить» удаляет и запускает перекачку
     под новым слоем (прогресс виден на «Подготовке»).
  3. Работоспособность офлайн: скачать регион под одним слоем, включить
     авиарежим, убедиться что регион рендерится; вернуться в сеть,
     переключить слой и «Перезагрузить» — проверить, что регион скачался
     заново и корректно виден офлайн уже под новым слоем.
  4. Миниатюра прогулки в «Архиве» соответствует текущему выбранному слою.
  5. iOS — то же самое, отдельно (два независимых нативных перехватчика).
