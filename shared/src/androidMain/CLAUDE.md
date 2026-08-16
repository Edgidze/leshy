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
именно блокировку экрана, не полный force-quit/swipe из Recents).

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

## Прочее

- `RenderOptions.RenderMode.TextureView` — см. `ui/map/CLAUDE.md`.
- `MapSnapshotter`/`org.maplibre.gl:android-sdk` для миниатюр прогулок —
  `@UiThread`-класс, вызов через `Dispatchers.Main` +
  `suspendCancellableCoroutine`.
- Полный `./gradlew build` может OOM при параллельной линковке iOS-таргетов
  вместе с запущенным Android-эмулятором — см. корневой `CLAUDE.md`.
