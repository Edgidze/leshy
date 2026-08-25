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

## `AppLanguage` × 26 и `LanguagePickerScreen` (Фаза 4)

`AppLanguage` — 26 значений (`code`/`endonym`/`englishName`; `displayName`
убран, он путал эндоним с общим лейблом). `ru`/`en` остаются exhaustive
`when`-ветками (`russianStrings`/`englishStrings`) — компилятор ловит
забытый перевод для них, как и раньше. Остальные 24 идут через `internal val
uiTranslations: Map<AppLanguage, Map<StringKey, String>>` (`Strings.kt`,
пока пустая карта — заполняется по языку за сессию в Фазах 6–11) и общий
резолвер `string()`: `uiTranslations[language]?.get(key) ?: englishStrings
(key)` — пропущенный ключ или вовсе отсутствующая карта языка молча
деградируют до английского, а не роняют сборку. `StringsTest.kt`
(`commonTest`) — тест этой деградации плюс тест полноты каждой непустой
карты (все ключи, значения не пустые); он реально выполняется в этом
окружении (не завязан на `Res.readBytes`/Android `Context`, в отличие от
`CatalogSourceTest`/`MushroomNamesTest` выше) — прогнан, зелёный.

Три плюральные функции (`mushroomsUnitLabel`/`walksUnitLabel`/
`regionsUnitLabel`) получили `else ->` вместо отдельной ветки `AppLanguage.EN`
— до CLDR-правил Фазы 5 все 25 не-русских языков читали тот же
one/many-сплит, что и английский (правильное приближение для большинства:
двузначный plural — обычный случай, русский трёхзначный — исключение).
С Фазы 5 это заменено настоящими правилами — см. ниже.

**`CountryNames.namesFor` обёрнут в `runCatching`.** До Фазы 4 у каждого
значения `AppLanguage` был свой `countries/<lang>.json` (только `ru`/`en`
существовали, и оба входили в `AppLanguage`) — теперь 24 новых языка
интерфейса читают файл, которого физически нет на диске (только `en`/`ru`
сгенерированы, Фаза 3 §3). Без `runCatching` `Res.readBytes` на
несуществующий файл падает, а не возвращает пусто — `collectionDisplayName`
рассчитывает именно на пустую карту, чтобы отработал её собственный фолбэк
на `EN`.

**`searchOrdered<T>(items, query, label)` — обобщение `searchOrderedCategories`
(`presentation/CategorySorting.kt`).** Та же ранжировка (`startsWith` →
`contains` → нечёткий префикс), но по индексам, а не по `Category.id` —
общей функции неоткуда взять поле идентичности для произвольного `T`.
`searchOrderedCategories` теперь однострочный вызов через
`categoryDisplayName`; `LanguagePickerScreen` — второй потребитель, ранжирует
по `"${endonym} ${englishName}"`, чтобы поиск находил язык что по эндониму
(«Deutsch»), что по английскому имени («German») — интерфейс в момент поиска
вполне может быть на третьем, ещё третьем языке.

**`LanguagePickerScreen` — не top-level, обычный `navigate()`/`popBackStack()`,
без своего `ViewModel`.** `TopAppBar` со стрелкой назад (отмена, как есть) и
галочкой (подтвердить и выйти) — тот же cancel/confirm-паттерн, что
`WalkDescriptionEditScreen` использует для своего двухкнопочного
`TopAppBar`, и по той же причине: тап по строке списка двигает только
локальный `selected`, реальное сохранение — только по галочке, иначе
исследовательский скролл длинного списка на 26 языков перещёлкивал бы
интерфейс на каждый тап. Экран получает текущий язык через
`LocalAppLanguage.current` (уже реактивен, провайдится один раз в
`App()`), а подтверждение пишет напрямую в `koinInject<SettingsRepository>()`
(`ui/navigation/LeshyNavHost.kt`) — тот же приём, что `App.kt` уже
использует для этого репозитория, не через `SettingsViewModel.setLanguage`
(метод удалён как мёртвый код: `SettingsScreen` больше не вызывает `set
Language` сам, вместо сегментированной кнопки — кликабельная строка «Язык
интерфейса → `<эндоним>`», ведущая на этот экран).

**Побочная находка — тестовые фейки `CategoryRepository` не собирались.**
`DeleteUserSpeciesUseCaseTest.kt`/`ExportImportRoundTripTest.kt` (оба вне
области Фазы 4) не реализовывали `getAll()`/`upsertAll()` — добавлены
Фазой 2 в интерфейс, но эти два фейка остались не обновлены, и
`compileAndroidHostTest` не собирался вовсе (ни один тест не мог быть
запущен ни в одной прошлой фазе — Фазы 1–3 проверяли только
`compileAndroidMain`, не тестовый компайл). Починено тут же двумя
однострочными `override`, потому что иначе было невозможно верифицировать
`StringsTest.kt` через `testAndroidHostTest`.

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

## Множественные числа — CLDR на 26 языков (`Plurals.kt`, Фаза 5)

`PluralCategory` (`Zero`, `One`, `Two`, `Few`, `Many`, `Other`) +
`pluralCategory(language, count)` — **exhaustive `when` по `AppLanguage`, не
`Map`**: 27-й язык должен ронять компиляцию, а не молча наследовать
английский двузначный сплит. Это ровно тот класс ошибки, которого никто не
заметит (план, §7).

Правила выписаны из CLDR 46 `plurals.xml` и **специализированы под
неотрицательные целые** — единственное, что приложение считает (находки,
прогулки, области). Специализация схлопывает операнды CLDR до двух: при
`v = f = t = 0` операнды `n` и `i` совпадают, поэтому каждое правило читает
только `n`, `n % 10`, `n % 100`.

**Отклонение от плана — категория `Zero`.** План (§5) перечисляет пять
категорий без неё, но латышскому она нужна по-настоящему: `lv` кладёт 0,
каждое кратное десяти и весь диапазон 11–19 в форму, отличную от `other`
(«10 sēņu» против «2 sēnes»). Свернуть латышский `zero` в `other` значило бы
выдавать неправильное слово на большой доле реальных значений. Из 26 языков
интерфейса `zero` есть только у `lv`.

**Часть категорий недостижима** — и это нормально, резолвер обязан быть
тотальным: CLDR отдаёт `many` дробям в `cs`/`sk`/`lt` и `other` дробям в
`be`/`ru`/`uk`/`pl`, а `two` есть только у словенского. У каждого юнита всё
равно шесть `StringKey` (`PluralForms`), недостижимая форма просто повторяет
соседнюю (в русском — родительный падеж множественного, в английском —
обычное множественное). Дословно: русский достигает `One`/`Few`/`Many`,
английский — `One`/`Other`.

Ловушка, о которой стоит помнить при переводах (Фазы 6–11): **английские
формы — это то, во что деградирует непереведённый язык, форма за формой**
(`string()` → `englishStrings(key)`). Поэтому у английского заполнены все
шесть ключей юнита, а не два достижимых.

`PluralsTest.kt` (`commonTest`) — таблица «язык → счёт → ожидаемая
категория», выписанная из sample-значений CLDR, а не выведенная из
реализации: явно перечислены все граничные значения (подростковые числа,
кратные десяти, вторая и третья сотни) и в первую очередь те, где
однокоренные языки расходятся — польское 21 против русского 21, чешское 22
против польского 22, хорватское 5 против русского 5. Плюс проверка, что ни
один `AppLanguage` не забыт в самой таблице. Тест реально прогоняется
(`testAndroidHostTest`), как и `StringsTest` — от `Res.readBytes` не зависит.
