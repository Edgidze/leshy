# androidMain/ — Android-специфичные `actual`

## Фоновая запись трека

`WalkRecordingService` (`data/platform/`) — foreground-сервис,
`foregroundServiceType="location"`, запускается на время активной записи
прогулки. **Не дублирует GPS-подписку** — точки как и раньше пишет
`RecordViewModel`/`RecordTrackPointUseCase` в `viewModelScope`; единственная
роль сервиса — держать приложение (не только сервис) вне фоновых лимитов на
доставку геолокации Android'а, пока экран заблокирован/приложение свёрнуто.
Без него GPS-колбэки глохнут через некоторое время после ухода с экрана.
Постоянное уведомление, без кнопки остановки (осознанно вне scope — чинили
именно блокировку экрана, полный force-quit/swipe из Recents — см. ниже
`onTaskRemoved`).

**`onTaskRemoved` останавливает сервис явно.** Свайп таска из Recents
процесс НЕ убивает (сам foreground-сервис и держит процесс живым — в этом
его смысл), но по-настоящему уничтожает `MainActivity`, а с ней и
`ViewModelStore`, которому принадлежит `RecordViewModel` — `viewModelScope`
(и GPS-коллектор внутри) отменяется безвозвратно. Сам сервис при этом
остаётся жить независимо — по умолчанию `onTaskRemoved` ничего не делает, а
единственное, что раньше умело его останавливать (`RecordViewModel.
finish()`/`startupHealJob`), никогда не срабатывает, пока приложение не
переоткрыли заново. Итог без переопределения — «Идёт запись прогулки»
висит сколько угодно долго после свайпа, хотя запись уже физически не
идёт (воспроизведено и подтверждено `adb shell dumpsys activity services`:
процесс жив, сервис жив, `RecordViewModel` уже нет). Фикс — `stopForeground
(STOP_FOREGROUND_REMOVE)` + `stopSelf()` прямо в `onTaskRemoved`. Саму
прогулку это не закрывает (в Room она остаётся с `endTime = null` до
следующего запуска) — это по-прежнему забота `HealOrphanedWalksUseCase` при
следующем открытии «Записи», см. `presentation/CLAUDE.md`; здесь чинится
только уведомление, не данные.

**`onStartCommand` возвращает `START_NOT_STICKY`, не `START_STICKY`.**
Раньше было наоборот — и после того как ОС убивала процесс посреди записи
(GPS-подписка живёт в `viewModelScope`, гибнет вместе с процессом),
`START_STICKY` пересоздавал именно этот сервис с `null`-intent, который
безусловно звал `startForeground(...)` заново — уведомление «Идёт запись
прогулки» воскресало само по себе, без единой активной прогулки за душой
(диагностировано `adb shell dumpsys activity services` + прямым чтением
Room через `run-as`: `createTime` сервиса совпадал с `startTime` прогулки,
которая так и осталась висеть с `endTime = null`). `START_NOT_STICKY` —
сервис просто не переживает смерть процесса, воскрешать нечему. Закрытие
самой осиротевшей прогулки — отдельный self-heal,
`HealOrphanedWalksUseCase` (см. `presentation/CLAUDE.md`).

Манифест: `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_LOCATION` (обязательно
для типа `location` на targetSdk 34+), `POST_NOTIFICATIONS` (запрашивается
в `MainActivity.onCreate` только на `SDK_INT >= TIRAMISU` — без него сервис
и трекинг работают, просто уведомление не видно).

## GPS

`android.location.LocationManager` напрямую, не Play Services/Fused
Location (не тянуть лишнюю зависимость). Эмитит `getLastKnownLocation
(provider)` сразу при подписке (не только будущие апдейты) — иначе карта
показывает `(0,0)` до первого реального фикса GPS-провайдера.

## Разрешения

`MainActivity.onCreate` — единственное место, где `ACCESS_FINE_LOCATION`
(+`POST_NOTIFICATIONS` на 13+) запрашиваются проактивно, одним батчем, т.к.
GPS нужен сразу. **`CAMERA` туда намеренно не входит** — единственный
потребитель (плейсхолдер фото в `AddPlaceDialog`) запрашивает его лениво,
только по клику, через `rememberCameraPermissionRequester`
(`data/platform/CameraPermission.kt`) — проверяет текущий статус и просит
систему только если разрешения ещё нет.

## Экспорт/импорт (`DataLocationPicker.android.kt`)

Экспорт — один системный диалог `CreateDocument("application/zip")` вместо
старой связки «выбрать папку + поле имени»: пользователь получает `Uri` сразу
готовый к записи, `ContentResolver.openOutputStream(uri)` оборачивается в
`okio.Sink` (`.sink().buffer()`) прямо внутри пикера — `ExportDataUseCase` ни
разу не видит `Uri`/`Context`, только `BufferedSink`. `.use { }` на
`BufferedSink` тут работает (JVM `Closeable`↔`AutoCloseable`), в отличие от
iOS — см. `iosMain/CLAUDE.md`.

## Прочее

- `RenderOptions.RenderMode.TextureView` — см. `ui/map/CLAUDE.md`.
- `MapSnapshotter`/`org.maplibre.gl:android-sdk` для миниатюр прогулок —
  `@UiThread`-класс, вызов через `Dispatchers.Main` +
  `suspendCancellableCoroutine`. **`MapLibre.getInstance(context)` перед
  созданием `MapSnapshotter`, каждый раз** (`AndroidWalkThumbnailRenderer.
  takeSnapshot`) — идемпотентно (no-op, если уже инициализирован), но без
  него снапшот молча ничего не отдавал (проглатывалось общим `catch` в
  `render()`), если это первое обращение к MapLibre в процессе. Обычно
  инициализация уже произошла неявно — любой экран с живой картой
  (`Record`/`Map`/`Preparation`) или первое обращение к `OfflineManager`
  сам вызывает `MapLibre.getInstance` (см. `ui/map/CLAUDE.md`) — но
  `BackfillWalkThumbnailsUseCase` для только что импортированных прогулок
  (`DataViewModel`) может сработать даже раньше, чем пользователь хоть
  раз открыл экран с картой в этом запуске приложения, и тогда без
  явного вызова здесь миниатюра так и оставалась `null` навсегда.
- Полный `./gradlew build` может OOM при параллельной линковке iOS-таргетов
  вместе с запущенным Android-эмулятором — см. корневой `CLAUDE.md`.
