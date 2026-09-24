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

## `uses-feature` — кто вообще увидит приложение в Play

Разрешения определяют, что приложение может делать; `uses-feature` с
`required="true"` определяет, **кому Google Play вообще предложит его установить**.
Устройства без заявленной возможности отфильтровываются из выдачи молча — ни отказа,
ни сообщения, приложение просто не находится. Найдено предрелизным аудитом 2026-09-24,
до этого нигде не разбиралось.

| Возможность | `required` | Источник | Следствие |
|---|---|---|---|
| `android.hardware.camera` | `false` | проект | фильтра нет |
| `android.hardware.location.gps` | `false` | проект | фильтра нет |
| `android.hardware.wifi` | `false` | `org.maplibre.gl:android-sdk:13.0.2` | фильтра нет |
| **`android.hardware.vulkan.version` `0x400003`** | **`true`** | **`org.maplibre.gl:android-sdk:13.0.2`** | **устройства без Vulkan 1.0.3 приложение не увидят** |
| `android.hardware.faketouch` | `true` (подразумевается) | все приложения по умолчанию | практического фильтра нет |
| `android.hardware.location` | `true` (подразумевается) | выведено из `ACCESS_*_LOCATION` | практического фильтра нет |

**Vulkan — единственный настоящий фильтр, и он не наш.** Объявлен самим MapLibre
(`android-sdk-13.0.2/AndroidManifest.xml:8-10`, в манифесте библиотеки стоит комментарий
`Vulkan 1.0 required`), проект его не добавлял и не переопределял.

Тут есть противоречие, которое стоит понимать. `minSdk` проекта — 24, то есть
заявлена поддержка Android 7.0 (2016). Но Vulkan 1.0 на бюджетных устройствах тех лет
есть далеко не везде, и для них заявленная поддержка фактически не действует: Play их
отфильтрует. **Это не регрессия** — так было и в `versionCode 4`, уже раздававшемся
закрытому тесту, с той же версией MapLibre.

**Практическое следствие:** если тестировщица сообщает «не нахожу приложение в Play» —
причину искать здесь в первую очередь. Сколько устройств реально отсекается, показывает
сам Play Console (каталог устройств, число поддерживаемых) — это измеримая величина, и
гадать о ней не нужно.

**Снимать ли фильтр — решение не косметическое.** Переопределить его в своём манифесте
(`tools:replace` + `required="false"`) технически можно, но библиотека объявила его
обязательным не случайно: если рендерер MapLibre 13 действительно требует Vulkan, то на
отфильтрованных сейчас устройствах приложение установится и покажет вместо карты в
лучшем случае пустоту. Проверять такое надо на устройстве без Vulkan, которого у проекта
нет. Поэтому оставлено как есть и вынесено в `.claude/plans/after-release.md`.

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
