# data/ — Room + DataStore

## Схема Room (актуальная — версия 12, `data/local/`)

6 таблиц, точные поля — смотри `*Entity.kt` напрямую, они компактны и
самодокументируемы. Кратко:
- **`categories`** — виды грибов (408 штук, весь каталог из
  `composeResources/files/catalog/catalog.json`) + предустановленный, видимый
  `category_unknown_mushroom` (Phase 10 `user-mushrooms.md` — приёмник
  находок удалённых пользовательских видов, см. ниже) + служебная неактивная
  `category_misc` (для FK у отметок PHOTO/POI — `objects.categoryId` не
  nullable, полностью скрыта от UI, не путать с `category_unknown_mushroom`).
  `nameKey`/`iconRef` — ключи (локализация / `Res.
  allDrawableResources` lookup), не готовые строки. `isActive` —
  пользовательское поле (что показывать на Карте/в ленте Записи, экран
  Фильтра). `isPicked`/`isFilterEligible` (v5) — модель подборок по странам,
  см. `.claude/plans/mushroom-collections.md` и раздел про `collections`
  ниже. `source`/`customNames`/`scientificName`/`iconFile` (v6) — свои виды
  грибов, см. `.claude/plans/user-mushrooms.md`; у каталожных строк
  `source = APP`, `customNames`/`iconFile` пусты, а вот **`scientificName` с
  v11 заполнен и у них** (латынь из `catalog.json`) — из-за этого
  `customDisplayName` (`i18n/CategoryNames.kt`) обязан отсекать
  `source == APP` первой же строкой, иначе все 408 каталожных видов
  показывались бы латынью вместо локализованного имени. Имя каталожной
  строки приходит из `files/catalog/names/<lang>.json` (не из `StringKey` —
  см. `i18n/CLAUDE.md`), картинка — из `composeResources` по `iconRef`.
  Остальное — источник истины сам каталог (см.
  `EnsureDefaultCategoriesUseCase` ниже).
- **`walks`** — прогулки, `mushroomCount` денормализован для ленты Архива,
  `thumbnailPath` (v3) — кэшированный PNG-снапшот карты, см.
  `ui/map/CLAUDE.md`. `description` (v9) — свободный многострочный текст,
  редактируется с отдельного экрана (не диалога — см. doc-комментарий
  `WalkDescriptionEditScreen`), идёт в export/import вместе с прогулкой
  (`WalkExportDto`).
- **`objects`** (домен-модель — `FieldMark`, не `Object`, зарезервировано в
  Kotlin) — находки/фото/ориентиры, `ON DELETE CASCADE` от `walks` **и** (с
  v8) от `categories` — см. миграцию v7→v8 ниже. `name`/`description` (v4) —
  подпись места для POI-отметок.
- **`track_points`** — точки трека, тоже каскадом от `walks`.
- **`collections`** (v5) — подборки грибов по странам; `category_collections`
  (v5) — связь много-ко-многим с `categories` (один гриб может входить в
  несколько подборок), `ON DELETE CASCADE` в обе стороны. Сама подборка не
  хранит «выбрана ли» — это производное состояние от `Category.isPicked` её
  участников (выбор подборки целиком в UI — просто bulk-запись `isPicked` по
  всем участникам). **С Фазы 3 `countries-and-languages.md` сидируются 33
  реальные страны** из `composeResources/files/catalog/countries.json`
  (`nameKey` вида `collection_country_<CC>`, например
  `collection_country_RU`) — старые 3 демо-подборки (`collection_demo_*`,
  `.claude/plans/mushroom-collections.md`) удалены `MIGRATION_11_12`
  (`DELETE FROM collections WHERE nameKey IN (...)`, каскад по
  `category_collections` срабатывает сам). Схему это не тронуло — только
  сид-данные, `MIGRATION_11_12` чисто на удаление, без единого `ALTER`.

  **`EnsureDefaultCollectionsUseCase` — тот же батч/гейт-паттерн, что
  `EnsureDefaultCategoriesUseCase` (см. ниже), с одной сознательной
  асимметрией.** Сами 33 строки `collections` диффятся честно (`getAll()` →
  сравнение по `nameKey`/`order` → `upsertAll` только для новых/изменённых).
  А вот ~1650 строк `category_collections` **не диффятся — пересобираются и
  вставляются заново при каждом полном проходе**, батчем через
  `CollectionDao.insertMembers` (`OnConflictStrategy.IGNORE`): раз стратегия
  конфликта — IGNORE, повторная вставка уже существующей пары
  `(categoryId, collectionId)` — no-op, так что можно каждый раз слать полный
  желаемый список без вычисления дельты. Обратная сторона — **устаревшее
  членство не убирается**: если следующая перегенерация `countries.json`
  уберёт вид из подборки страны, старая строка `category_collections`
  переживёт это неограниченно (тот же принцип, каким
  `EnsureDefaultCategoriesUseCase` никогда не делает `DELETE` каталожных
  строк — обе реконсиляции только досеивают, никогда не убирают). Гейт —
  версия `countries.json` (`CountriesSource.version`, тот же
  `contentHashCode`-приём, что у `CatalogSource.version`) в
  `CatalogStateRepository.getSeededCountriesVersion`/
  `setSeededCountriesVersion`, отдельный ключ DataStore от версии каталога.

**Явные `Migration`-объекты с v1, экспорт схемы включён — никакого
`fallbackToDestructiveMigration`.** v1→v2 добавила `edibilityStatus` и
**переименовала** (не удалила) 4 старые дефолтные категории в их аналоги из
30-видового каталога — `UPDATE`, не `DELETE`+реинсерт, иначе каскад снёс бы
уже отмеченные пользователем находки. v2→v3 — чисто аддитивная колонка
`thumbnailPath`. v3→v4 — аддитивные `objects.name`/`description`. v4→v5 —
аддитивные `categories.isPicked`/`isFilterEligible` (default `1`, чтобы уже
установленные копии продолжали видеть весь ранее засеянный каталог на экране
Фильтра без изменений) + новые таблицы `collections`/`category_collections`.
v5→v6 — аддитивные `categories.source` (`DEFAULT 'APP'` — уже засеянные
строки продолжают читаться как каталожные), `customNames`
(`NOT NULL DEFAULT '{}'`, JSON-объект по `AppLanguage.code` через
`Converters`, а не nullable-колонка — так у поля нет двух пустых состояний),
`scientificName`, `iconFile`.

**v6→v7 — не аддитивная.** `EdibilityStatus` схлопнут с трёх значений
(`EDIBLE`/`CONDITIONALLY_EDIBLE`/`INEDIBLE`) до двух
(`NOT_SPECIFIED`/`POISONOUS`) — деление на съедобные/условно-съедобные
признано слишком относительным (готовка/регион/индивидуальная переносимость),
чтобы оставаться заявлением от лица приложения. Раз старые имена констант
перестали существовать, старые строки `edibilityStatus` в БД стали невалидны
для `EdibilityStatus.valueOf` — миграция обязана переписать значения сразу,
иначе упадёт чтение первой же строки, раньше, чем
`EnsureDefaultCategoriesUseCase` успеет что-то поправить. Миграция не
пытается угадать правильное значение по старому — она сбрасывает **все**
строки в `NOT_SPECIFIED`: для 30 каталожных `nameKey` это временное состояние
(следующий же вызов `EnsureDefaultCategoriesUseCase` доводит их до истинной
классификации по `docs/mushrooms_catalog.json` — 9 видов со статусом
`poisonous`/`deadly_poisonous`/`mixed` там уходят в `POISONOUS`, остальные 21
остаются `NOT_SPECIFIED`), а для пользовательских/импортированных видов —
финальное состояние, сознательно: `EnsureDefaultCategoriesUseCase` не знает о
них ничего, и приписать пользовательскому виду опасность или безопасность без
всякого основания хуже, чем промолчать.

**v7→v8 — не аддитивная, меняет FK.** `objects.categoryId → categories.id`
получил `ON DELETE CASCADE` (был `NO ACTION`) — изначально заведено под
настоящее удаление пользовательских видов (`DeleteUserSpeciesUseCase`,
`.claude/plans/user-mushrooms.md`, Phase 9). SQLite не умеет `ALTER TABLE`
существующий `FOREIGN KEY` — миграция пересоздаёт `objects` целиком (`CREATE
objects_new` с нужным FK → `INSERT ... SELECT` → `DROP` → `RENAME` →
пересоздать оба индекса `index_objects_walkId`/`index_objects_categoryId` под
именами, которые ожидает Room). Единственная в истории проекта миграция,
трогающая FK — остальные не годятся образцом для следующей такой правки.
`category_misc`/`category_unknown_mushroom` (обе `source = APP`) в опасность
не попадают — удаление доступно только из списка «Мои грибы» (`WHERE source
!= 'APP'`), каскад до каталожных строк физически не дотягивается.

**v8→v9 — снова чисто аддитивная**, `walks.description TEXT` (nullable, без
`DEFAULT`) — существующие прогулки получают `NULL`, что и рендерится как
пустое описание.

**v9→v10 — удаляет `categories.edibilityStatus` целиком.** Продуктовое
решение: определение съедобности/ядовитости гриба полностью выходит за
рамки функционала приложения (юридический риск + приложение не может нести
ответственность за такую классификацию). `EdibilityStatus`,
`MushroomSortOrder.POISONOUS_LAST` и все производные UI (бейдж на плитке
находки, поле в форме своего вида, выбор сортировки в Настройках) убраны из
кода вместе с колонкой. `MushroomSortOrder` как enum убран целиком —
сортировка каталога отныне ровно одна (по алфавиту, `sortCategories`
больше не принимает параметр порядка), выбирать больше нечего, поэтому и
DataStore-ключ `mushroom_sort_order` (`SettingsRepository`) исчез — старое
значение в уже установленных копиях просто перестаёт читаться, без миграции
(Preferences DataStore не требует явной чистки неиспользуемых ключей).
Чекбоксы «Неподвижный порядок грибов»/«Сбрасывать порядок в конце прогулки»
не тронуты — они не имели отношения к самой сортировке, только к
порядку внутри ленты. `ALTER TABLE ... DROP COLUMN` — без пересборки
таблицы (в отличие от v7→v8): колонка ни в одном FK/индексе не участвует,
и `sqlite-bundled` достаточно новый для `DROP COLUMN` (SQLite ≥ 3.35).

**v10→v11 — единственная миграция без единого изменения схемы.**
`11.json` побайтово совпадает с `10.json` (тот же `identityHash`), номер
версии поднят только ради того, чтобы у переименования ключей было где
жить. Переименование: 30 каталожных строк переезжают со старых ключей
(`category_boletus_edulis`) на ключи 408-категорийного каталога
(`boletus_edulis`) — 23 простым снятием префикса, 7 явной таблицей
`LEGACY_CATEGORY_KEY_REMAP` (`data/catalog/LegacyCategoryKeys.kt`; это
бывшие «групповые» строки старого каталога, цели выбраны владельцем проекта
по GC-кодам, см. `.claude/plans/countries-and-languages.md` §3.3). Только
`UPDATE`, ни одного `DELETE`: `objects.categoryId` каскадится от
`categories` с v7→v8, так что удаление унесло бы находки пользователя —
переименование на месте оставляет каждую находку на той же строке.
`isActive`/`isPicked`/`isFilterEligible` не трогаются вовсе. Оба `UPDATE`
ограничены `source = 'APP'` и обходят два служебных ключа (`category_misc`/
`category_unknown_mushroom` — они не из каталога и остаются на `StringKey`).
Порядок важен: сперва 7 явных (после переименования они уже без префикса),
потом общий `substr(nameKey, 10)`.

**Та же таблица переиспользуется импортом архива** (`ImportDataUseCase`) —
архив, выгруженный до v11, помнит вид каждой находки под старым ключом, и
без второго прохода через `catalogKeyForLegacy` все находки из него легли бы
в `category_misc`. Это единственная причина, по которой таблица лежит в
`data/catalog/`, а не внутри `Migrations.kt`.

**v11→v12 — тоже без изменения схемы**, `12.json` отличается от `11.json`
только `version`/`identityHash` (проверено побайтовым JSON-сравнением после
пересборки). Единственное действие — `DELETE FROM collections WHERE nameKey
IN ('collection_demo_north', 'collection_demo_south', 'collection_demo_east')`
(Фаза 3 `countries-and-languages.md`, раздел про `collections` выше). Ни
одного `ALTER`/`categories`-`UPDATE`: `categories.isPicked`/`isActive`
пользователя эта миграция не трогает вовсе — удаляются только сами
строки-контейнеры демо-подборок, `category_collections` подчищается за счёт
уже существующего с v4→v5 `ON DELETE CASCADE` на `collectionId`. Проверено
прогоном по настоящей SQLite (тем же приёмом, что и v10→v11 — засеять
таблицы вручную, выполнить `DELETE`, сравнить до/после): 5 подборок → 2,
6 строк membership → 3, уцелевшие — ровно две не-демо, membership на демо-id
пропал полностью.

**С Phase 10 этот CASCADE на штатном пути больше никогда не срабатывает —
и это осознанно оставлено так, а не откачено на `NO ACTION`.**
`DeleteUserSpeciesUseCase` теперь сперва **переносит** находки удаляемого
вида на предустановленный `category_unknown_mushroom`
(`FieldMarkRepository.reassignCategory`, один `UPDATE objects SET
categoryId = ...`), и только потом удаляет строку категории — к моменту
`DELETE` на неё уже не ссылается ни одна находка, каскадить нечему. Из
файлов на диске трогается только собственная иконка удаляемого вида
(`Category.iconFile` через `PhotoStorage.resolvePath`, `runCatching`) —
находки не удаляются, значит и их фото (`FieldMark.photoPath`) никто не
трогает; версия Phase 9, что вычищала эти фото как «осиротевшие», удаляла бы
теперь ЖИВЫЕ фото прямо под перенесённой находкой. `WalkDetailViewModel.
onDeleteConfirm` — отдельный случай, находки там действительно удаляются
вместе с прогулкой, и там по-прежнему нужно вручную подчищать
`photoPath`/`Walk.thumbnailPath` тем же `runCatching`-паттерном (пути
берутся из уже загруженного `uiState`, каскадный `DELETE` их не вернёт).

**`iconFile` хранит имя файла, а не абсолютный путь** — намеренно иначе, чем
`objects.photoPath`/`walks.thumbnailPath`, из-за которых пришлось заводить
`RepairPhotoPathsUseCase` (на iOS UUID контейнера песочницы меняется, и
абсолютные пути протухают разом). Путь собирается при чтении через
`PhotoStorage.resolvePath`, чинить нечего по построению.

**Имя файла иконки несёт таймстемп (`catimg_<nameKey>_<millis>.png`), и старый
файл удаляется, а не перезаписывается** (`SaveCategoryIconUseCase`) — Coil
кэширует по строке модели, и запись новых байтов по тому же пути продолжала бы
показывать СТАРУЮ картинку из памяти/диска до перезапуска приложения. К
детерминированному имени записи в архиве (`categories/<nameKey>.png`, Phase 6
плана) это отношения не имеет — там имя нужно ради того, чтобы повторный импорт
не плодил файлы.

**`EnsureDefaultCategoriesUseCase` — upsert-diff, не «insert только если
таблица пуста».** Для каждой категории каталога: если строки ещё нет —
insert; если есть, но `order`/`colorHex`/`iconRef`/`scientificName`
отличаются от канонических — update по `id`; `isActive`/`isPicked`/
`isFilterEligible` сохраняются как есть (все три — пользовательские поля).
Так правки каталога (например, переупорядочивание) доезжают до уже
установленных копий приложения без новой `Migration` — `order` чисто
визуальное поле, заводить под него миграцию было бы overkill.

**С v11 источник — `catalog.json` (408 строк), а не список в коде, и проход
по нему батчевый и гейтованный.** Один `getAll()` → диф в памяти → два
батч-запроса (`insertAll`/`updateAll`, каждый одной транзакцией) вместо 408
`getByNameKey`+upsert по одному. Сверху — гейт по «версии» каталога
(`CatalogStateRepository`, DataStore): версия — это `contentHashCode` байтов
самого `catalog.json` (`CatalogSource.version`), а не константа в коде,
которую можно забыть поднять при перегенерации. Гейт дополнительно
проверяет число строк в таблице (`count() >= 410`) — версия живёт в
DataStore, а строки в Room, и эти два хранилища расходятся (восстановление
бэкапа, очистка данных), тогда полный проход обязан отработать несмотря на
совпадающую версию.

**Новые каталожные виды сеются выключенными** —
`isPicked`/`isFilterEligible`/`isActive` = `false`. Иначе обновление
приложения превратило бы у существующего пользователя выверенную ленту из
30 плиток в 408; что показывать — решает пикер подборок, ровно как и
задумано (`.claude/plans/countries-and-languages.md` §4.3). Исключение —
две служебные строки: `category_unknown_mushroom` остаётся видимым
(`isActive = true`), `category_misc` — как был, скрытым по `nameKey`.
`EnsureDefaultCollectionsUseCase` — тот же паттерн для `collections`, но
поверх (вызывается после — сидирует членство по `nameKey` категорий, которые
уже должны существовать). Оба вызываются из каждого экрана, чей ViewModel их
использует (`RecordViewModel.init`, `SettingsViewModel.init`), не один раз
при старте приложения — идемпотентны, безопасно звать из нескольких мест.

**Две грабли Phase 1, обе — «пользовательское поле» не значит «безопасно
трогать откуда угодно»:**
1. `CategoryRepositoryImpl.upsert`/`CollectionRepositoryImpl.upsert`
   раньше всегда звали `@Insert(OnConflictStrategy.REPLACE)`, даже для
   апдейта существующей строки. Для SQLite REPLACE при конфликте PK — это
   DELETE + INSERT, не UPDATE. Пока у `categories`/`collections` не было
   входящих `ON DELETE CASCADE` FK, это было безобидно; как только Phase 0
   завела `category_collections` с CASCADE в обе стороны, тот же REPLACE стал
   молча стирать membership-строки при каждом обычном тумблере
   `isActive`/`isPicked` (Filter, пикер подборок). Фикс — настоящий `@Update`
   по `id` для существующих строк, `@Insert` только для новых (`id == 0`).
2. `EnsureDefaultCategoriesUseCase`'ный diff явно оберегал только `isActive`
   от перезаписи каноническими данными каталога — свежедобавленные
   `isPicked`/`isFilterEligible` в этот список не попали и откатывались на
   дефолт `true` при каждом повторном вызове (т.е. на каждом заходе на
   Запись/Настройки). Фикс — добавить оба поля в список сохраняемых при
   diff'е, тем же способом, что и `isActive`.

## Настройки — DataStore (Preferences), не Room-таблица

Выбрано вместо Room-таблицы и стороннего `multiplatform-settings`: не нужна
миграция схемы на каждую новую настройку, и не тянет non-Google/JetBrains
зависимость (тот же принцип, что и в `i18n/CLAUDE.md`). Путь к файлу — через
`expect/actual platformModule`, тем же паттерном, что `RoomDatabase.Builder`
(Android — `filesDir`, iOS — `NSFileManager` documents dir), общий
`DataStore<Preferences>` singleton переиспользуется всеми репозиториями
настроек (`SettingsRepository` — язык; `MapFilterRepository` — фильтр карты,
Часть 7) — новый репозиторий настроек не требует новой платформенной
регистрации, только `get()` того же singleton.

Формат ключа — один `xxxPreferencesKey(...)` на поле, `null`/отсутствие
ключа = «не задано». Для очистки значения — `prefs.remove(KEY)`, не
присвоение `null` (сеттер `MutablePreferences` non-null).
