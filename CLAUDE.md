# CLAUDE.md — «Леший: карта грибов» / "Leshy: mushrooms map"

Мобильное приложение для записи «тихой охоты» — трекинг прогулок за грибами
(трек + отметки находок + фото), архив прошлых прогулок, агрегированная карта
находок со статистикой. Полное ТЗ — `SPEC.md` (источник истины по
функционалу); этот файл — по архитектуре и правилам разработки.

**Держи этот файл коротким.** Структура каталогов, схема БД, история решений
по каждой части сюда не идут — это всё выводимо из кода. Вместо нарративных
логов используй вложенные `CLAUDE.md` в директориях, где живёт предметная
область (`ui/map/`, `ui/navigation/`, `data/`, `presentation/`, `i18n/`,
`androidMain/`, `iosMain/`, `iosApp/`) — туда терпимы (только) неочевидные
грабли/причины конкретных решений по теме этой директории. В корневой файл —
только то, что нужно знать независимо от того, какой файл сейчас
редактируется.

**Исключение — `composeResources/`:** туда нельзя класть `CLAUDE.md`
напрямую, Compose Resources генерирует аксессоры по содержимому этой
директории и падает на любом файле верхнего уровня, не являющемся
распознаваемой typed-папкой (`drawable`, `values`, `font`, ...). Заметки по
этой директории — в `.claude/rules/compose-resources.md` (path-scoped
правило с `paths:` фронтматтером, грузится точно так же, но не живёт внутри
сканируемой Gradle-директории).

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
   почему). Никаких хардкод-строк в UI. **Исключение — данные каталога**
   (названия 408 видов грибов и 40 стран, свои виды пользователя): это не
   интерфейсные строки, а контент, который не влезает в exhaustive `when`
   по конечному `enum` — правило для них другое, см. `i18n/CLAUDE.md`,
   разделы «Имена грибов каталога» и «Названия стран».
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
- **iOS: главный поток может зависнуть внутри `MapLibre.framework` на уходе
  приложения в фон** (`std::future::get()` без символов внутри SDK) —
  воспроизведено трижды на реальном устройстве, дважды закончилось полной
  перезагрузкой телефона (watchdog не дождался ответа от системных
  демонов). Не починено, фактура и план расследования —
  `.claude/investigations/ios-maplibre-background-watchdog/README.md`,
  короткая версия — `ui/map/CLAUDE.md`.
- **Навигация: все top-level разделы (пункты бокового выдвижного меню)
  обязаны идти через `navigateToTopLevel()`** (`ui/navigation/Destinations.kt`)
  — подмена на голый `navigate()` для одного из них ломает
  `saveState`/`restoreState` для остальных (несколько реальных краш-багов в
  истории). Домашний экран — «Запись» (`Destination.Record`, единственный
  `startDestination` графа); боковая панель открывается только кнопкой-
  гамбургером слева сверху (свайп от края отключён — конфликтует с
  панорамированием карты на «Записи»). Подробности — `ui/navigation/CLAUDE.md`.
- **iOS: делегат `CLLocationManager` внутри `callbackFlow` — только `var` на
  классе, не локальный `val`.** ARC освобождает объект без сильных ссылок за
  пределами функции, обрывая колбэки после первого события.
- **Android MapLibre: `RenderOptions.RenderMode.TextureView`**, не дефолтный
  `SurfaceView` — иначе карта не участвует в alpha-переходах Compose
  Navigation и «просвечивает» через fade между экранами.
- **Compose Multiplatform Resources не декодирует SVG на Android**
  (`painterResource` — только Skia, т.е. iOS/Desktop). Любой drawable-ресурс
  для показа в приложении — растеризовать в PNG/WebP заранее.
- **iOS-тесты сейчас не линкуются вообще** (проверено 2026-09-03):
  `:shared:linkDebugTestIosSimulatorArm64` падает на `Failed to build cache
  for okio-fakefilesystem-iosSimulatorArm64`, а настоящая причина глубже —
  `IrTypeAliasSymbolImpl is already bound. Signature: kotlinx.datetime/Clock`:
  typealias `kotlinx.datetime.Clock` схлопывается с появившимся в stdlib
  `kotlin.time.Clock` под Kotlin 2.4.0. Обход, который предлагает сам
  компилятор (отключить нативные кеши), через `-Pkotlin.native.cacheKind=none`
  не подхватывается — не пробовали через `gradle.properties`. Следствие:
  `CatalogSourceTest` и `MushroomNamesTest` не выполняются НИГДЕ — на
  `:shared:testAndroidHostTest` они падают с `android.util.Log not mocked`
  (они рассчитаны на нативную цель), а нативная цель не собирается. Остальные
  наборы `commonTest` на Android-хосте проходят.
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
Android + iOS. Каталог с тех пор вырос до 408 видов, подборок по странам 40,
интерфейс переведён на 42 языка (`.claude/plans/countries-and-languages.md`,
`.claude/plans/post-soviet-countries.md`,
`.claude/plans/europe-15-countries.md`). Числа проверяются по данным, а не по
этому файлу: страны — `composeResources/files/catalog/countries.json`, языки
интерфейса — `domain/model/AppLanguage.kt`.
Текущий прогресс — смотри `git log`, не этот файл.

**Партия `europe-15` не закончена:** девять новых языков интерфейса заведены и
переведены, но 15 подборок стран ждут браузерного исследования
(`docs/research/europe-15/`, Фазы 1–2 плана). До него подборки этих стран не
существуют, а названия грибов на новых языках показываются латынью — это
ожидаемое состояние, не дефект.

## Перед публикацией

Перед финальным шагом публикации (Google Play / App Store) — обязателен supply-chain аудит
зависимостей. Полный чеклист — навык `pre-release-audit`
(`.claude/skills/pre-release-audit/SKILL.md`).
