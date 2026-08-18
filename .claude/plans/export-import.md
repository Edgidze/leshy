# План: экспорт / импорт данных (zip)

Статус: план готов, реализация не начата. Отмечать шаги `[x]` по мере
выполнения. Каждый шаг — отдельная сессия/коммит. Не переносить сюда код/
детали реализации после того, как шаг сделан — итоговые неочевидные решения
идут в `data/CLAUDE.md`/`androidMain/CLAUDE.md`/`iosMain/CLAUDE.md`, этот
файл только про план вперёд. Когда все шаги закрыты — файл можно удалить.

## Идея

Выгрузить прогулки (треки, находки, фото) из приложения в zip-архив и
загрузить такой архив обратно — как бэкап/перенос между устройствами.
Экран-заглушка (`DataScreen`/`DataViewModel`, кнопки выбора папки/файла) уже
существует (`ui/screens/DataScreen.kt`, `presentation/data/DataViewModel.kt`,
`data/platform/DataLocationPicker.kt`), но обработка не реализована —
placeholder'ы (`onClick = {}`). Категории/подборки грибов в архив не входят:
это фиксированный каталог, который на любой установке уже засеян
`EnsureDefaultCategoriesUseCase`; импорт лишь сопоставляет находки с уже
существующими категориями по `nameKey` (`CategoryDao.getByNameKey` уже
есть). Импорт — не merge/upsert, а всегда **добавление новых прогулок**
(новые id), с опциональной припиской к названию (поле уже есть в UI:
`DataImportLabelFieldLabel` = "Tag added to imported walk names").

Zip реализуем сами, без сторонних библиотек — решение подтверждено
пользователем: найденные KMP-zip-библиотеки (`kmp-zip`, `KmpIO`, `kzip`) —
малой аудитории, один мейнтейнер, не всегда зрелая поддержка iOS-таргетов;
риск перед будущим `pre-release-audit` не оправдан. `okio`, уже
транзитивно в проекте (виден в `PlatformModule.*.kt` как `okio.Path`), для
самого zip не подходит — его `openZip()` только для чтения и только JVM
(обёртка над `java.util.zip`); используем только его кроссплатформенные
`BufferedSink`/`BufferedSource`/`FileSystem.SYSTEM` как I/O-слой. Формат —
только `STORED` (без сжатия): фото уже JPEG, JSON-метаданные малы
относительно них.

## Формат архива

```
leshy-export-<timestamp>.zip
├── manifest.json
└── walks/
    └── walk-<originalId>/
        ├── walk.json
        ├── track.json
        ├── objects.json
        └── photos/
            └── <originalObjectId>.<ext>
```

- `manifest.json` — `{ schemaVersion, appVersion, exportedAt, walkCount }`.
  `schemaVersion` — отдельная версия формата архива, не привязана к версии
  Room-схемы (сейчас 5) — так формат переживёт будущие миграции БД без
  изменений.
- `walk.json` — поля `WalkEntity` кроме `id`/`thumbnailPath`.
- `track.json` — массив точек трека (`lat`, `lon`, `timestamp`, `elevation`,
  `sequence`), в порядке `sequence`.
- `objects.json` — массив находок; `categoryId` заменён на
  `categoryNameKey` (стабилен между установками, в отличие от
  автогенерируемого id); `photoPath` заменён на относительный путь внутри
  архива (`photos/<id>.<ext>`) или `null`.
- **`thumbnailPath` не экспортируется.** Миниатюра — производный от трека
  PNG-снапшот карты (см. `ui/map/CLAUDE.md`), а не исходные данные;
  дублировать логику копирования файла ради неё не нужно — импортированные
  прогулки получают `thumbnailPath = null` и досчитываются уже существующим
  `BackfillWalkThumbnailsUseCase`.

## Шаги

- [x] **Шаг 1 — Zip-кодек.** Новый пакет `data/export/zip/` (`commonMain`,
  без Room/платформенных зависимостей): `Crc32.kt` (табличная чистая Kotlin
  реализация — нет `java.util.zip` на Kotlin/Native), `ZipWriter` (пишет
  STORED-записи в `okio.BufferedSink`, копит central directory, дописывает
  его + EOCD в конце), `ZipReader` (читает EOCD+central directory с конца
  потока, отдаёт список записей и байты по имени — принимает `ByteArray`
  целиком, не стрим; для реалистичных объёмов данных этого приложения
  архив читается за один проход при импорте, доп. сложность с random-access
  чтением по частям не оправдана). Версия `okio` зафиксирована явно в
  `libs.versions.toml` (`3.17.0`, была транзитивной через DataStore).
  Юнит-тесты (`ZipCodecTest.kt`, `commonTest`) — round-trip запись/чтение
  текста и бинарных данных на вложенных путях, порядок записей, известный
  тестовый вектор CRC-32, отказ на архиве без EOCD. Зелено:
  `:shared:testAndroidHostTest` (4/4 теста), `:shared:compileAndroidMain`,
  `:shared:compileKotlinIosSimulatorArm64`.

- [x] **Шаг 2 — DTO и сериализация.** Добавлена `kotlinx-serialization-json`
  в `libs.versions.toml`/`shared/build.gradle.kts` (плагин `kotlinSerialization`
  уже был подключён, ядро-либа уже была в зависимостях). Новый пакет
  `data/export/dto/`: `@Serializable ExportManifestDto`, `WalkExportDto`,
  `TrackPointExportDto`, `ObjectExportDto` — отдельные от Room-сущностей
  типы (id локальны для устройства, не сериализуются напрямую).
  `ObjectExportDto.type` — `String` (имя `ObjectType`), не сам enum, чтобы
  DTO-слой не тянул зависимость на Room-сущность. `categoryId` заменён на
  `categoryNameKey`. Общий `ExportJson` (`data/export/dto/ExportJson.kt`,
  `ignoreUnknownKeys = true` — вперёдсовместимость: старая версия
  приложения не упадёт на архиве, написанном более новой). Имена файлов
  архива и путь к фото находки — `WALK_ENTRY_NAME`/`TRACK_ENTRY_NAME`/
  `OBJECTS_ENTRY_NAME`/`walkDirectory()`/`photoEntryName()`, там же, где
  DTO. Юнит-тесты (`ExportDtoSerializationTest.kt`) — round-trip каждого
  DTO (включая nullable-поля), список точек трека/находок, толерантность к
  незнакомым полям. Зелено: `:shared:testAndroidHostTest` (5/5 новых +
  прежние 4/4 zip-тестов), `:shared:compileAndroidMain`,
  `:shared:compileKotlinIosSimulatorArm64`.

- [ ] **Шаг 3 — One-shot DAO-запросы.** Добавить `WalkDao.getAll()`,
  `TrackPointDao.getByWalkId(walkId): List<TrackPointEntity>`,
  `ObjectDao.getByWalkId(walkId): List<ObjectEntity>` — по образцу уже
  существующего `WalkDao.getById` (сейчас у `TrackPointDao`/`ObjectDao`
  только `Flow`-варианты `observeByWalkId`).

- [ ] **Шаг 4 — `ExportDataUseCase`.** Строит `id → nameKey` карту категорий
  один раз (`CategoryDao.observeAll().first()` или отдельный one-shot
  запрос), затем для каждой прогулки (`WalkDao.getAll()`) маппит в DTO и
  пишет в `ZipWriter`: `manifest.json`, затем на каждую прогулку
  `walk.json`/`track.json`/`objects.json` и байты фото (читаются через
  `okio.FileSystem.SYSTEM.read(path.toPath())` из `ObjectEntity.photoPath`).
  Результат — поток байт в переданный `okio.BufferedSink` (не готовый
  `ByteArray` в памяти — архивы с фото могут быть большими).

- [ ] **Шаг 5 — `ImportDataUseCase`.** Читает `manifest.json` (проверка
  `schemaVersion` — отклонить незнакомую будущую версию с понятной
  ошибкой). На каждую `walks/walk-*/`: парсит DTO, резолвит категорию по
  `categoryNameKey` (`CategoryDao.getByNameKey`), при отсутствии (рассинхрон
  каталога) — fallback на служебную `category_misc` (уже используется как
  FK-safe заглушка, см. `data/CLAUDE.md`), вставляет новую `WalkEntity`
  (id=0, имя = оригинал + приписка пользователя, `thumbnailPath=null`),
  новые `TrackPointEntity` с новым `walkId`, новые `ObjectEntity` — фото
  копируются из архива в локальную директорию фото под новым именем (без
  коллизий с уже существующими файлами), путь пишется в новый `photoPath`.
  Всё — в одной транзакции (`RoomDatabase.withTransaction`), чтобы битый/
  частично прочитанный архив не оставил половинчатые записи в БД.

- [ ] **Шаг 6 — Android: доступ к файлу назначения.** Заменить связку
  «выбрать папку (`OpenDocumentTree`) + отдельное поле имени» на единый
  системный диалог `ActivityResultContracts.CreateDocument("application/zip")`
  с именем по умолчанию (из поля `exportArchiveName`) → `Uri`, запись через
  `ContentResolver.openOutputStream(uri)`. Импорт (`OpenDocument`) уже даёт
  читаемый `Uri` — не менять режим, только прокинуть реальный `Uri` в
  `PickedLocation` рядом с отображаемым именем (сейчас там только имя).

- [ ] **Шаг 7 — iOS: доступ к файлу назначения.** Экспорт: сначала полностью
  собрать архив во временный файл (`NSTemporaryDirectory()`), затем
  показать `UIDocumentPickerViewController(forExporting: [tempURL])` (режим
  экспорта, не `.Open`) — iOS сам копирует файл в выбранное место, не нужны
  security-scoped bookmarks для папки, выбранной заранее. Импорт (`.Import`
  mode) уже копирует файл в песочницу приложения — прокинуть путь в
  `PickedLocation`, режим не менять.

- [ ] **Шаг 8 — UI-провода.** `DataViewModel`/`DataScreen.kt`: заменить
  `onClick = {}` заглушки на вызовы `ExportDataUseCase`/`ImportDataUseCase`,
  добавить в `DataUiState` состояние процесса (`isProcessing`,
  `errorMessage`/`resultMessage`) — простой `MutableStateFlow`, как уже
  написано, без `combine()`-паттерна (`presentation/CLAUDE.md`), экран не
  стримит данные из Room.

## Проверка на каждом шаге

- Шаги 1–5 — чистая логика, без железа: юнит-тесты (`Crc32`/`ZipWriter`/
  `ZipReader` round-trip, маппинг DTO↔Entity) прогонять самостоятельно.
  Компиляция — `:shared:compileAndroidMain` +
  `:shared:compileKotlinIosSimulatorArm64` после каждого шага.
- Шаги 6–8 (реальный SAF-диалог на Android, `UIDocumentPicker` на iOS,
  реальные фото) — **не тестировать самостоятельно**, отдавать пользователю
  на проверку на устройстве. Сценарий: записать тестовую прогулку → Экспорт
  → Импорт того же архива (с меткой) → сверить, что прогулка появилась в
  Архиве с приписанным именем, фото открываются, трек на карте совпадает с
  оригиналом.
