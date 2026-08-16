# data/ — Room + DataStore

## Схема Room (актуальная — версия 3, `data/local/`)

4 таблицы, точные поля — смотри `*Entity.kt` напрямую, они компактны и
самодокументируемы. Кратко:
- **`categories`** — виды грибов (30 штук) + служебная неактивная
  `category_misc` (для FK у отметок PHOTO/POI — `objects.categoryId` не
  nullable). `nameKey`/`iconRef` — ключи (локализация / `Res.
  allDrawableResources` lookup), не готовые строки. `isActive` — единственное
  пользовательское поле, всё остальное — источник истины сам каталог (см.
  `EnsureDefaultCategoriesUseCase` ниже).
- **`walks`** — прогулки, `mushroomCount` денормализован для ленты Архива,
  `thumbnailPath` (v3) — кэшированный PNG-снапшот карты, см.
  `ui/map/CLAUDE.md`.
- **`objects`** (домен-модель — `FieldMark`, не `Object`, зарезервировано в
  Kotlin) — находки/фото/ориентиры, `ON DELETE CASCADE` от `walks`.
- **`track_points`** — точки трека, тоже каскадом от `walks`.

**Явные `Migration`-объекты с v1, экспорт схемы включён — никакого
`fallbackToDestructiveMigration`.** v1→v2 добавила `edibilityStatus` и
**переименовала** (не удалила) 4 старые дефолтные категории в их аналоги из
30-видового каталога — `UPDATE`, не `DELETE`+реинсерт, иначе каскад снёс бы
уже отмеченные пользователем находки. v2→v3 — чисто аддитивная колонка
`thumbnailPath`.

**`EnsureDefaultCategoriesUseCase` — upsert-diff, не «insert только если
таблица пуста».** Для каждой категории каталога: если строки ещё нет —
insert; если есть, но `order`/`colorHex`/`iconRef`/`edibilityStatus`
отличаются от канонических — update по `id`, `isActive` сохраняется как есть
(пользовательское поле). Так правки каталога (например, переупорядочивание)
доезжают до уже установленных копий приложения без новой `Migration` —
`order` чисто визуальное поле, заводить под него миграцию было бы overkill.

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
