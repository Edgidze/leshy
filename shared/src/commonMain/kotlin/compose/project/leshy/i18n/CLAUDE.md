# i18n/ — своя локализация, не Compose Resources

**`Res.string` не используется** — в `components-resources-1.11.1`
`ResourceEnvironment`/`LocalComposeEnvironment`/`LanguageQualifier`
`internal`/`@InternalResourceApi`, переопределить локаль в рантайме из кода
приложения невозможно. Свой слой:

- `StringKey` — плоский `enum` всех строк.
- `Strings.kt` — `russianStrings(key)`/`englishStrings(key)`, exhaustive
  `when` по `StringKey` (компилятор ловит забытый перевод при добавлении
  нового ключа — **не обходить** `else ->`, смысл конструкции именно в
  принудительной полноте).
- `LocalAppLanguage` (`CompositionLocal`) + `@Composable fun stringResource
  (key)`; для не-composable контекста (например, `RecordViewModel.start()`) —
  чистая функция `string(key, language)`.
- Текущий язык — в DataStore (см. `data/CLAUDE.md`), прокидывается в `App()`
  через `CompositionLocalProvider` — переключение в «Настройках» применяется
  мгновенно во всём приложении без перезапуска.
- `categoryDisplayName(nameKey)` — отдельный хелпер для имён категорий
  (`Category.nameKey` резолвится тем же механизмом).
- **`categoryDisplayName(category)` (перегрузка по `Category`) — то, что должен
  звать UI, а не `nameKey`-версия.** Пользовательские виды
  (`.claude/plans/user-mushrooms.md`) — единственное исключение из правила
  «локализация только через ключи»: их имя вводит пользователь и оно живёт в
  `Category.customNames` (`Map<AppLanguage, String>`), никакого `StringKey` под
  него нет и быть не может. Перегрузка сначала смотрит туда (с фолбэком на
  второй язык, затем на `scientificName`) и только потом — в `nameKey`.
  `nameKey`-версии остались как низкоуровневые, для каталожных строк.

Добавление новой строки: новый `StringKey` + обе ветки `when` в
`Strings.kt`. Никаких хардкод-строк в UI.

## Имена грибов каталога — данные, не `StringKey`

`.claude/plans/countries-and-languages.md`, Фаза 1: 408 названий × 36 языков
не влезает в exhaustive `when` по `StringKey` (правило выше про «только
ключи» тут перестаёт масштабироваться — ровно как уже было для
`customNames`, см. выше). Источник — `composeResources/files/catalog/`
(`catalog.json` + `names/<lang>.json`, сгенерированы `tools/build_catalog.py`,
описание — `docs/catalog/CLAUDE.md`), два новых Koin-синглтона:
`data/catalog/CatalogSource.kt` (парсит `catalog.json`, кэширует) и
`MushroomNames.kt` (парсит `names/<lang>.json` **лениво по языку** — при
обращении к `namesFor(language)`, а не все 36 сразу).

`categoryNameStringKey(nameKey)` в `CategoryNames.kt` остаётся, но обслуживает
только два служебных ключа — `category_misc`/`category_unknown_mushroom`, они
не из каталога. Любой другой `nameKey` идёт по цепочке `catalogDisplayName`:
`MushroomNames.namesFor(language)[nameKey]` → `CatalogSource.scientificName
(nameKey)` → сам `nameKey` как последний резерв.

**`CatalogSource`/`MushroomNames` резолвятся через `getKoin()` (`org.koin.mp
.KoinPlatform`) прямо внутри `categoryDisplayName(nameKey, language)`, не
параметром.** Эта non-composable перегрузка зовётся из чистых функций без
доступа к DI (`presentation/CategorySorting.kt` — `sortCategories`/
`searchOrderedCategories`), и протаскивать `CatalogSource`/`MushroomNames`
параметром через них и дальше во все `RecordViewModel`/`MapFilterViewModel`
означало бы менять сигнатуры далеко за пределами каталожного слоя. Синглтоны
уже стартуют до первого экрана (`di/InitKoin.kt`), так что `getKoin()` здесь
безопасен — тот же паттерн, каким сам Koin документирует доступ к контейнеру
из кода, не управляемого DI напрямую.

**`Res.readBytes` — suspend, а `categoryDisplayName`/сортировка — нет** (тот
же чистый non-composable контекст, что и выше). Оба синглтона читают файл
`runBlocking`'ом при первом обращении к своим `by lazy`/`getOrPut` полям —
файлы маленькие (катал `catalog.json` ~60 КБ, один `names/<lang>.json`
1–15 КБ), так что блокировка первого вызова практически не ощущается, а всё
последующее — обычный синхронный доступ к уже разобранной `Map`.

**`customDisplayName` обязан отсекать `source == CategorySource.APP` первой
строкой (Фаза 2).** С Room v11 каталожные строки несут заполненный
`scientificName` (латынь из `catalog.json`), а `customDisplayName` падает на
него последним фолбэком — без этой проверки все 408 каталожных видов
показывались бы латынью вместо локализованного имени, во всех списках сразу.
`customNames`/`scientificName` по смыслу и так только про некаталожные виды
(см. KDoc `Category.source`), проверка просто делает это явным.

**Старые ключи (`category_boletus_edulis`) больше не встречаются** — Фаза 2
переименовала их в Room (миграция v10→v11, см. `data/CLAUDE.md`). Обратная
совместимость осталась ровно в одном месте: `catalogKeyForLegacy`
(`data/catalog/LegacyCategoryKeys.kt`), которым импорт архива переводит ключи
из выгрузок до v11.

## Названия стран (`collection_country_<CC>`) — тот же приём, ещё один слой данных

Фаза 3: 33 подборки по странам заменили 3 демо-подборки
(`.claude/plans/countries-and-languages.md`, `data/CLAUDE.md` — раздел про
`collections`). Их `nameKey` (`collection_country_RU` и т.д.) резолвится
`i18n/CollectionNames.kt` тем же паттерном, что и имена грибов, но с ещё
одним уровнем фолбэка, потому что языков с готовым переводом названия
страны меньше, чем языков интерфейса: `CountryNames.namesFor(активный
язык)[код]` → `CountryNames.namesFor(EN)[код]` → сам код страны как
последний резерв. Два новых Koin-синглтона, оба зеркалят
`CatalogSource`/`MushroomNames` буква-в-букву: `data/catalog/
CountriesSource.kt` (парсит `countries.json`, даёт `version` для гейта
`EnsureDefaultCollectionsUseCase`) и `CountryNames.kt` (парсит
`files/catalog/countries/<lang>.json`, лениво по языку). **Отличие от
`MushroomNames`: сейчас `en`/`ru` — единственные языки с файлом
(`countries/en.json`/`countries/ru.json`)**, но `AppLanguage` пока и есть
ровно `{RU, EN}` (Фаза 4 расширит до 26), так что `namesFor` не нуждается ни
в каком `runCatching` — файл на диске есть под каждое текущее значение
`AppLanguage`, как и у `MushroomNames`. Двухуровневый фолбэк в
`collectionDisplayName` — задел именно под Фазу 4, когда появятся языки
интерфейса без своего `countries/<lang>.json`.

`countryCollectionNameKey(code)`/`countryCodeForCollectionNameKey(nameKey)`
(`data/catalog/CountriesSource.kt`) — единственное место, где зашита
строка-префикс `collection_country_`; и сидирование, и резолвинг имени, и
предвыбор подборки по региону устройства (`OnboardingViewModel`) идут через
них, а не через собственные `"collection_country_" + code`.

**Тесты каталожного слоя (`data/catalog/CatalogSourceTest.kt`,
`i18n/MushroomNamesTest.kt`) написаны, но не запускаются в этом окружении** —
два независимых и не связанных с этой фичей пробела инфраструктуры:
Android host-тесты не могут прочитать Compose-ресурсы без Robolectric
(`DefaultAndroidResourceReader` требует живой Android `Context`, которого нет
в чистом JVM unit-тесте — `isReturnDefaultValues=true` эту ошибку не решает,
только маскирует `Log.d`), а линковка `iosSimulatorArm64Test` падает на не
связанной с этим PR ошибке тулчейна Kotlin/Native (`IrTypeAliasSymbolImpl is
already bound` на `kotlinx.datetime/Clock` — коллизия с `kotlin.time.Clock`,
воспроизводится и без изменений в каталоге). Данные проверены отдельно
(Python-скрипт по тем же JSON: 408 записей, все ключи уникальны, `sci` нигде
не пустой, ни один `names/<lang>.json` не содержит ключей вне каталога) — сама
логика верна, но тесты нужно будет прогнать по-настоящему после того, как
кто-то заведёт Robolectric или починит линковку `iosSimulatorArm64Test`.
