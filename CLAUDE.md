# CLAUDE.md — «Леший: карта грибов» / "Leshy: mushrooms map"

Мобильное приложение для записи «тихой охоты» — трекинг прогулок за грибами
(трек + отметки находок + фото), архив прошлых прогулок, агрегированная карта
находок со статистикой. Полное ТЗ — `SPEC.md` (источник истины по
функционалу); этот файл — по архитектуре и правилам разработки.

**Держи этот файл коротким.** Структура каталогов, схема БД, история решений
по каждой части сюда не идут — это всё выводимо из кода. Вместо нарративных
логов используй вложенные `CLAUDE.md` в директориях, где живёт предметная
область (`ui/map/`, `ui/navigation/`, `data/`, `presentation/`, `i18n/`,
`composeResources/`, `androidMain/`, `iosMain/`, `iosApp/`) — туда терпимы
(только) неочевидные грабли/причины конкретных решений по теме этой
директории. В корневой файл — только то, что нужно знать независимо от того,
какой файл сейчас редактируется.

## Стек (зафиксирован — не менять без явного запроса)

| Область | Выбор |
|---|---|
| Платформы | Android + iOS |
| UI | Kotlin Multiplatform + Compose Multiplatform |
| Карты | MapLibre Compose + OpenFreeMap (векторные тайлы, без API-ключей) |
| Навигация | Compose Navigation (JetBrains KMP) |
| Асинхронность | Kotlin Coroutines + Flow |
| DI | Koin |
| БД | Room (KMP), явные миграции с v1 |
| Настройки | AndroidX DataStore (Preferences) |
| Архитектура | MVVM + domain layer, вся логика и UI в `shared` |

## Правила разработки

1. **Каждая находка коммитится в Room немедленно** при нажатии «+»/«−» — не
   буферизировать в памяти. Процесс может быть прерван в любой момент, всё
   успевшее записаться должно сохраниться без штатного завершения прогулки.
2. **Локализация только через ключи** — `i18n/StringKey` + `Strings.kt`
   (свой слой, не Compose Resources `Res.string` — см. `i18n/CLAUDE.md`
   почему). Никаких хардкод-строк в UI.
3. **Миграции Room с версии 1**, явные `Migration`-объекты, экспорт схемы
   включён — никакого `fallbackToDestructiveMigration`.
4. **Вся логика и UI — в `shared`.** `androidApp`/`iosApp` — тонкие хосты.
   Платформенный код — только за `expect`/`actual`.
5. **Immutable UiState + StateFlow** на каждый экран, ViewModel без
   Android-зависимостей.
6. **Кроссплатформенная библиотека — всегда первый вариант.**
   `expect`/`actual` с нативным кодом — только когда единого решения для
   Android+iOS объективно не существует (GPS, камера, файловые пути,
   MapLibre-снапшоттеры) — и то, только проверив сначала, что готовой
   KMP-библиотеки для этого случая правда нет.

## Известные грабли

- **MapLibre `HeatmapLayer` крашит нативно** (SIGSEGV, type confusion в JNI)
  на части реальных устройств — используй кластеризованный
  `CircleLayer`/`SymbolLayer` (`GeoJsonOptions(cluster = true)`) вместо него.
  Подробности — `ui/map/CLAUDE.md`.
- **Навигация: все top-level экраны (пункты drawer) обязаны идти через
  `navigateToTopLevel()`** (`ui/navigation/Destinations.kt`) — подмена на
  голый `navigate()` для одного из них ломает `saveState`/`restoreState` для
  остальных (несколько реальных краш-багов в истории). Подробности —
  `ui/navigation/CLAUDE.md`.
- **iOS: делегат `CLLocationManager` внутри `callbackFlow` — только `var` на
  классе, не локальный `val`.** ARC освобождает объект без сильных ссылок за
  пределами функции, обрывая колбэки после первого события.
- **Android MapLibre: `RenderOptions.RenderMode.TextureView`**, не дефолтный
  `SurfaceView` — иначе карта не участвует в alpha-переходах Compose
  Navigation и «просвечивает» через fade между экранами.
- **Compose Multiplatform Resources не декодирует SVG на Android**
  (`painterResource` — только Skia, т.е. iOS/Desktop). Любой drawable-ресурс
  для показа в приложении — растеризовать в PNG/WebP заранее.
- **Полный `./gradlew build` может упасть `OutOfMemoryError`** при
  параллельной линковке `iosArm64`+`iosSimulatorArm64` вместе с запущенным
  Android-эмулятором — останови эмулятор и/или собирай с `--max-workers=1`.
  Для рутинной проверки iOS достаточно
  `:shared:compileKotlinIosArm64`/`:shared:compileKotlinIosSimulatorArm64`,
  полная release-линковка не нужна.
- **MapLibre `OrnamentOptions` не умеет стекать орнаменты друг под другом** —
  у компаса/линейки масштаба/лого/атрибуции один общий `padding` на всех
  плюс независимый одинаковый фиксированный инсет у каждого. Подробности —
  `ui/map/CLAUDE.md`.

## Команды

```bash
./gradlew build                                  # полная сборка (см. грабли про OOM)
./gradlew :shared:compileAndroidMain             # быстрая проверка Android
./gradlew :shared:compileKotlinIosArm64          # быстрая проверка iOS (устройство)
./gradlew :shared:compileKotlinIosSimulatorArm64 # быстрая проверка iOS (симулятор)
./gradlew :androidApp:assembleDebug              # APK
```

## Статус

Части 1–7 плана (скелет, Запись, Архив, Карта, Настройки+лого, каталог из
30 видов грибов, единый фильтр карты) реализованы и проверены вживую на
Android + iOS. Текущий прогресс — смотри `git log`, не этот файл.

## Перед публикацией

Перед финальным шагом публикации (Google Play / App Store) — обязательна широкая проверка
зависимостей на предмет supply-chain рисков:

1. Прогнать все версии из `gradle/libs.versions.toml` через OSV.dev
   (`https://api.osv.dev/v1/querybatch`, ecosystem `Maven`) на предмет известных CVE.
2. Убедиться, что `settings.gradle.kts` по-прежнему ограничивает разрешение зависимостей только
   `google()`/`mavenCentral()` — никаких сторонних/приватных репозиториев не добавилось.
3. Точечно проверить репутацию/происхождение любых новых или менее устоявшихся зависимостей
   (не от `androidx.*`/`org.jetbrains.*`/крупных организаций) — кто мейнтейнер, публикуется ли
   через официальный канал (Maven Central/Gradle Plugin Portal), нет ли признаков компрометации.

Это осознанная альтернатива постоянно поддерживаемому Gradle dependency locking/verification
(`gradle/verification-metadata.xml`) — для этого проекта решили не тащить их ongoing maintenance
cost, а вместо этого делать разовую широкую проверку прямо перед релизом. Причина и когда стоит
пересмотреть это решение — см. память `feedback-dependency-locking-decision` (общая, не
привязанная к этому проекту).
