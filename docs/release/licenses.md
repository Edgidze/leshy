# Лицензии — картографические данные и OSS-зависимости

Задача 7 релизного плана (`docs/release/android-release-setup.md`), пункт 1 (атрибуция
карты) и пункт 4 (список OSS-зависимостей) — **вместо экрана «Лицензии» в приложении**,
по решению владельца продукта, этот список ведётся как md-файл в репозитории. Пункты 2
(дисклеймер о съедобности), 3 (аудит CC-BY-NC изображений) — **не в скоупе этого прохода**,
не проверялись и не трогались.

Дата: 2026-08-26.

## 1. Атрибуция картографических данных (OSM / OpenFreeMap)

- Источник тайлов — `https://tiles.openfreemap.org/styles/liberty`
  (`OPEN_FREE_MAP_STYLE_URL`, `ui/map/MapStyle.kt`) — стиль на основе данных
  **OpenStreetMap**, распространяемых под **ODbL 1.0** (Open Database License).
  Условие лицензии: видимая атрибуция «© OpenStreetMap contributors» + указание, что
  данные доступны по ODbL (обычно ссылкой на `openstreetmap.org/copyright`).
- Атрибуция рисуется штатным ornament'ом MapLibre (`isAttributionEnabled = true` —
  библиотечное значение по умолчанию, нигде в проекте не переопределяется на `false`;
  проверено `grep` по `isAttributionEnabled`/`AllDisabled`/`OnlyLogo` — совпадений нет).
  Текст атрибуции приходит из самого `style.json`, который приложение само не
  редактирует и не подменяет (`MapStyleCacheRepository` пиннит его как есть, см.
  `ui/map/CLAUDE.md`, раздел «Пиннинг стиля карты»).
- **Найдена и исправлена реальная угроза перекрытия.** `mapOrnamentOptions`
  (`ui/map/MapRenderOptions.kt`) не переопределяет `attributionAlignment`
  (библиотечный дефолт — `BottomEnd`) ни на одном из четырёх экранов с картой.  На
  экране «Подготовка» (`PreparationScreen.kt`) ровно в этом же углу висела
  `FloatingActionButton` с обычным `padding(16.dp)` — стандартный Material FAB
  (56.dp) с таким отступом от края целиком накрывает угол, где по дефолтной
  раскладке рисуется маленькая кнопка атрибуции. На остальных трёх картах
  (`MapScreen`, `RecordScreen`/`WalkMapScreen`, `AggregatedFindsMap`) в `BottomEnd`
  ничего не размещено — там атрибуция ничем не закрыта.
  **Фикс:** у FAB на «Подготовке» отступ снизу увеличен с `16.dp` до `64.dp`
  (`bottom = 64.dp`, `end = 16.dp` оставлен как был) — кнопка полностью уходит
  выше строки орнаментов, не занимая угол атрибуции. Стеки орнаментов друг под
  другом MapLibre не умеет (см. корневой `CLAUDE.md`, «Известные грабли»), поэтому
  свободного соседнего угла для самой атрибуции нет — все четыре угла уже заняты
  компасом/линейкой масштаба/лого/атрибуцией на остальных экранах, так что
  единственный работающий вариант — не пускать интерактивные элементы экрана в тот
  же угол, а не двигать саму атрибуцию.
- **Известное и осознанно не исправленное ограничение** (см. `ui/map/CLAUDE.md`):
  миниатюры прогулок в «Архиве» — статичный PNG-снапшот
  (`MapSnapshotter`/`MLNMapSnapshotter`), и на Android этот снапшоттер не
  дорисовывает атрибуцию на 240×240 превью (на iOS — дорисовывает сам). Вне скоупа
  задачи 7 в этом проходе (пункт 1 просил проверить именно «экран карты» —
  интерактивные полноэкранные карты, не превью в списке), но формально это тоже
  рендер данных OSM без подписи — стоит учитывать при следующем release-аудите,
  если Android-миниатюры когда-нибудь подключат к публичному функционалу шеринга.

## 2. OSS-зависимости и их лицензии

Полный рантайм-набор согласно `gradle/libs.versions.toml` (KMP `commonMain`/`androidMain`)
плюс нативный MapLibre SDK, подтягиваемый транзитивно и через SPM на iOS. Только
рантайм-зависимости — build-time инструменты (Kotlin-компилятор, KSP, AGP, Gradle,
плагин `spmForKmp`) в поставку приложения не попадают и здесь не перечислены.

| Библиотека | Версия | Лицензия | Источник данных о лицензии |
|---|---|---|---|
| Kotlin stdlib / kotlinx-coroutines / kotlinx-serialization / kotlinx-datetime | 2.4.0 / 1.11.0 / 1.11.0 / 0.8.0 | Apache License 2.0 | JetBrains, `github.com/Kotlin/kotlinx.*` |
| Compose Multiplatform (`org.jetbrains.compose.*`: runtime, foundation, ui, material3, components-resources) | 1.11.1 (material3 1.11.0-alpha07) | Apache License 2.0 | JetBrains, `github.com/JetBrains/compose-multiplatform` |
| Compose Material Icons (core + extended) | 1.7.3 | Apache License 2.0 | тот же проект Compose Multiplatform |
| AndroidX (core-ktx, appcompat, activity-compose, lifecycle-*, navigation-compose, room-runtime/compiler, sqlite-bundled, datastore-preferences-core) | см. `libs.versions.toml` | Apache License 2.0 | Google, `source.android.com`/AndroidX AOSP |
| Koin (core, compose, compose-viewmodel, android) | 4.1.1 (BOM) | Apache License 2.0 | `github.com/InsertKoinIO/koin` |
| MapLibre Compose (`org.maplibre.compose:maplibre-compose`) | 0.13.0 | **BSD 3-Clause** | `github.com/maplibre/maplibre-compose` |
| MapLibre Native (Android `org.maplibre.gl:android-sdk`, iOS `maplibre-native` через SPM) | Android SDK 13.0.2, iOS 6.25.1 | **BSD 2-Clause** | `github.com/maplibre/maplibre-native`, `LICENSE.md` |
| Coil 3 (`io.coil-kt.coil3:coil-compose`) | 3.5.0 | Apache License 2.0 | `github.com/coil-kt/coil` |
| Okio | 3.17.0 | Apache License 2.0 | `github.com/square/okio` |
| OkHttp | 4.12.0 | Apache License 2.0 | `github.com/square/okhttp` |
| JUnit (тесты, не входит в релизный APK/AAB) | 4.13.2 | Eclipse Public License 1.0 | `github.com/junit-team/junit4` |

**Итог:** ни одной copyleft/NC-лицензии среди зависимостей не найдено — весь набор
это Apache-2.0/BSD (плюс EPL-1.0 только у тестовой, не поставляемой в релиз, JUnit).
Требование чек-листа «ни одной NC-лицензии в поставке» (в части OSS-зависимостей)
выполнено. Картиночный каталог грибов (потенциальный источник NC-лицензии, пункт 3)
в этом проходе не проверялся — см. преамбулу.

Лицензии BSD (MapLibre) и Apache 2.0 в общем случае требуют сохранения текста
лицензии/копирайта при распространении — при появлении экрана «О приложении»/
«Лицензии» в UI этот файл можно использовать как готовый источник для его контента,
дополнив полными текстами лицензий (`NOTICE`/`LICENSE` файлы апстримов) вместо
одной строки на зависимость.
