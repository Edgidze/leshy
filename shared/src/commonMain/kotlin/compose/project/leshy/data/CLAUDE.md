# data/ — Room + DataStore

## Схема Room (актуальная — версия 8, `data/local/`)

6 таблиц, точные поля — смотри `*Entity.kt` напрямую, они компактны и
самодокументируемы. Кратко:
- **`categories`** — виды грибов (30 штук) + предустановленный, видимый
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
  `source = APP` и остальные три пусты (имя приходит из `StringKey`,
  картинка — из `composeResources` по `iconRef`). Остальное — источник
  истины сам каталог (см. `EnsureDefaultCategoriesUseCase` ниже).
- **`walks`** — прогулки, `mushroomCount` денормализован для ленты Архива,
  `thumbnailPath` (v3) — кэшированный PNG-снапшот карты, см.
  `ui/map/CLAUDE.md`.
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
  всем участникам). Пока сидируются демо-подборки, нарезанные из текущих 30
  видов (`EnsureDefaultCollectionsUseCase`) — финальные по странам приходят
  Phase 5 плана, схему это не тронет, только сид-данные.

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
insert; если есть, но `order`/`colorHex`/`iconRef`/`edibilityStatus`
отличаются от канонических — update по `id`; `isActive`/`isPicked`/
`isFilterEligible` сохраняются как есть (все три — пользовательские поля).
Так правки каталога (например, переупорядочивание) доезжают до уже
установленных копий приложения без новой `Migration` — `order` чисто
визуальное поле, заводить под него миграцию было бы overkill.
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
