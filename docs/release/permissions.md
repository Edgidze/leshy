# Аудит разрешений — merged manifest (playRelease)

Источник: `androidApp/build/intermediates/merged_manifests/playRelease/processPlayReleaseManifest/AndroidManifest.xml`
(сгенерирован `./gradlew :androidApp:processPlayReleaseManifest`), источник каждого
разрешения сверен по `androidApp/build/intermediates/manifest_merge_blame_file/playRelease/processPlayReleaseMainManifest/manifest-merger-blame-play-release-report.txt`.

Задача 6 релизного плана (`docs/release/android-release-setup.md`). Дата аудита: 2026-08-26.

## Разрешения

| Разрешение | Источник | Зачем | Вердикт |
|---|---|---|---|
| `ACCESS_FINE_LOCATION` | `androidApp/src/main/AndroidManifest.xml` (проект) | точный GPS-трек прогулки — основная функция приложения. Запрашивается проактивно в `MainActivity.onCreate` | оставить |
| `ACCESS_COARSE_LOCATION` | проект | стандартная пара к `ACCESS_FINE_LOCATION` — не самостоятельная функция, но её отсутствие при заявленном `FINE` выглядит подозрительно для ревью Play и не даёт системе выдать грубую геопозицию, если пользователь на диалоге разрешений выберет её вместо точной | оставить |
| `CAMERA` | проект | фото находки прямо в приложении (`CameraLauncher.android.kt`, `AddPlaceDialog`). Запрашивается лениво по клику (`rememberCameraPermissionRequester`), не в общем батче с локацией | оставить |
| `FOREGROUND_SERVICE` | проект | база для `WalkRecordingService` — держит процесс живым, пока экран заблокирован/приложение свёрнуто во время записи прогулки (см. `shared/src/androidMain/CLAUDE.md`) | оставить |
| `FOREGROUND_SERVICE_LOCATION` | проект | обязательная декларация типа `location` для foreground-сервиса начиная с targetSdk 34 — без неё `WalkRecordingService` не стартует на Android 14+ | оставить |
| `POST_NOTIFICATIONS` | проект | уведомление foreground-сервиса на Android 13+. Запрашивается только на `SDK_INT >= TIRAMISU`, без него сервис работает, просто без видимого уведомления | оставить |
| `INTERNET` | `org.maplibre.gl:android-sdk:13.0.2` | загрузка векторных тайлов OpenFreeMap | оставить (транзитивное от карты, без него приложение бесполезно) |
| `ACCESS_NETWORK_STATE` | `org.maplibre.gl:android-sdk:13.0.2` | MapLibre следит за сменой сети для кэширования/повторных запросов тайлов | оставить (транзитивное) |
| `ACCESS_WIFI_STATE` | `org.maplibre.gl:android-sdk:13.0.2` | тянется тем же SDK вместе с `uses-feature android.hardware.wifi` (см. комментарий `Implied by ACCESS_WIFI_STATE` в самом merged manifest) | оставить (транзитивное) |
| `leshy.mushrooms.map.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` | `androidx.core:core:1.18.0` | self-signature permission, автоматически добавляемый AndroidX для незарегистрированных вручную (`registerReceiver` в коде) broadcast receiver'ов на Android 14+; не разрешение в обычном смысле — не показывается пользователю, не требует обоснования в Play Console | оставить (служебное, вырезать нельзя без риска сломать динамические receiver'ы AndroidX-библиотек) |

## Красные флаги из чек-листа — ни один не найден

- **`ACCESS_BACKGROUND_LOCATION`** — отсутствует. Трекинг прогулки идёт только пока
  сервис в foreground (`WalkRecordingService`, `foregroundServiceType="location"`);
  фоновой геолокации без активной прогулки в приложении нет.
- **`READ_MEDIA_IMAGES` / `READ_EXTERNAL_STORAGE`** — отсутствуют. Выбор фото из галереи
  сделан через `ActivityResultContracts.PickVisualMedia` (`GalleryPicker.android.kt`) —
  системный Photo Picker, разрешений на доступ к хранилищу не требует в принципе.
- **`MANAGE_EXTERNAL_STORAGE`** — отсутствует, нигде не запрашивается.
- **`QUERY_ALL_PACKAGES`** — отсутствует, нигде не запрашивается.

`grep` по `*.kt`/`*.xml` вне `build/` на эти четыре разрешения также не находит совпадений
(в коде их нет и в виде строк для рантайм-запроса).

## Итог

Правок манифеста в этой задаче не потребовалось — `tools:node="remove"` не понадобился,
лишних разрешений не найдено. Список из шести собственных разрешений и трёх транзитивных
от MapLibre полностью соответствует функциям приложения (GPS-трек, фото находок, карта).
