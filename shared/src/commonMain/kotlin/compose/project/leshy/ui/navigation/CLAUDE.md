# ui/navigation/ — Compose Navigation

`Destination` — sealed interface маршрутов. `Record` — домашний экран
(запись прогулки) И единственный `startDestination` графа одновременно —
отдельного `Home`-маршрута с кнопками больше нет (было до
2026-08-21, см. git-историю `74ca94f`/ревёрт этой правки — качели между
«домашняя страница с кнопками» и «боковая выдвижная панель» уже случались
дважды, второй раз с намеренным возвратом к панели). Разделы открываются
через `ModalNavigationDrawer` в `App.kt` (не через `NavHost`-маршрут —
драйвер такой же, как у `OnboardingScreen`, см. секцию ниже: панель — не
экран, а оверлей поверх текущего). Системное «назад» (и `navigateToTopLevel`
из drawer) возвращают на `Record`, потому что он анкерован внизу бэкстека —
никакого явного `onHomeClick`/`popBackStack(Record, ...)` не требуется,
это следствие `inclusive = false` в `navigateToTopLevel` ниже. Top-level
экраны (разделы: Запись/Архив/Карта/Подготовка/Настройки/Мои
грибы/Данные — пункты бокового меню) обязаны переходить через один и тот же
хелпер:

```kotlin
fun NavHostController.navigateToTopLevel(destination: Destination) {
    navigate(destination) {
        popUpTo(graph.findStartDestination().id) { inclusive = false; saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
```

`inclusive = false` — намеренно: `graph.findStartDestination()` теперь
всегда `Record`, и он должен остаться внизу бэкстека (не вытесняться), иначе
«назад» с раздела не будет попадать на домашний экран записи, а сразу
выходить из приложения. Именно это (пустой бэкстек под `Record`) и даёт
«выход из приложения по «назад»» на самом домашнем экране — бесплатно, через
дефолтное поведение `NavHost`/`ComponentActivity`, без кастомного
`BackHandler` или `exitProcess`.

## Почему `navigateToTopLevel` — жёсткое правило, а не стиль

Асимметрия здесь уже ломала навигацию дважды:

1. **Кнопка-шестерёнка когда-то делала голый `navigate()` без `popUpTo`**,
   а нижняя навигация — с `popUpTo(findStartDestination){saveState=true}` +
   `restoreState=true`. Сохранённое под route-ключом состояние «Настроек»
   (не входивших в нижнюю навигацию, но в том же бэкстеке) путало
   восстановление при следующих переходах — после ЛЮБОГО захода в «Настройки»
   вкладка «Запись» переставала открываться ОТКУДА УГОДНО. Промежуточные
   попытки чинить только `popUpTo(graph.id)` вместо
   `findStartDestination().id`, или вовсе убрать `saveState`/`restoreState`,
   каждая чинила один сценарий и ломала другой (второй вариант ронял таймер
   активной записи при каждом уходе с «Записи», потому что
   `RecordViewModel` держится на `viewModelStoreOwner = backStackEntry`
   экрана `Record`, а `inclusive=true` без `saveState` уничтожает эту запись
   бэкстека). Итоговый фикс — единая функция для ВСЕХ top-level переходов,
   без исключений.
2. Тот же класс бага в других формах повторялся при каждом новом top-level
   входе (кнопка «?», позже кнопка «назад» на «Настройках» и т.д.) — решение
   каждый раз одно: звать `navigateToTopLevel`, не изобретать локальный
   `navigate()`.
3. Более старая версия этой же асимметрии жила в явной кнопке «домой»
   (`onHomeClick` в `SectionScaffold`, ещё до того как её сменили на
   кнопку-гамбургер бокового меню — см. заголовок файла): голый
   `popBackStack(Destination.Home, false)` без `saveState` уничтожал запись
   уходящего раздела вместо `navigateToTopLevel`-совместимого сохранения.
   Симптом воспроизводился только на `Record` во время активной записи:
   `Запись → домой → Подготовка → домой → Запись` — `RecordViewModel`
   уничтожался при первом уходе на `Home` (GPS-коллектор в `viewModelScope`
   обрывался вместе с ним), при возврате создавался новый экземпляр, ничего
   не знающий об активной прогулке (не перегидратируется из Room), а
   foreground-сервис с уведомлением продолжал висеть, потому что его
   останавливает только явный `finish()`. Прогулка так и оставалась в
   архиве «Не завершена». Фикс тогда — `saveState = true` на каждом
   `onHomeClick`. Сейчас явной кнопки «домой» вообще нет: `Record` —
   `startDestination`, возврат на него — следствие `inclusive = false` выше,
   а кнопка слева сверху (`onMenuClick` в `SectionScaffold`) только
   открывает боковую панель, никуда не навигирует сама — тот класс бага
   структурно недостижим, пока `onMenuClick` не начнёт вызывать `navigate`
   напрямую.

## Общий `viewModelStoreOwner` для вложенных экранов карты

`WalkMapScreen`/детальные карты берут `koinViewModel` с тем же
`viewModelStoreOwner`, что и родительский экран (`backStackEntry` через
`navController.getBackStackEntry(Destination.WalkDetail(...))`), чтобы не
плодить второй ViewModel и не терять состояние.

**Обязательно оборачивать в `runCatching { ... }.getOrNull()`.** Переход по
`navigateToTopLevel` выталкивает записи из бэкстека СИНХРОННО
(`popUpTo(...){inclusive=false}` всё равно вытесняет предыдущий раздел, если
он не `Record`), до того как успевает закончиться
recomposition уходящего дочернего экрана во время exit-анимации — голый
`getBackStackEntry(...)` на этом кадре бросает `IllegalArgumentException` и
крашит приложение. С guard'ом composable просто ничего не рендерит на этот
короткий кадр (экран и так анимированно исчезает).

## Экран первого запуска — намеренно НЕ NavHost-маршрут

`OnboardingScreen` (`.claude/plans/mushroom-collections.md`, Phase 3) рендерится
`App()` ВМЕСТО всего `LeshyNavHost`/`ModalNavigationDrawer` (пока флаг
`OnboardingRepository` не `true`), не через `composable<Destination.X>`
внутри графа. Соблазн завести `Destination.Onboarding` и сделать его
условным `startDestination` — именно та ошибка, от которой предостерегает
секция выше: `navigateToTopLevel` у ВСЕХ топ-level разделов держится на том,
что `Record` навсегда единственный `startDestination` графа
(`graph.findStartDestination()`). Если бы `Onboarding` хоть раз стал
стартовым `Destination` (единственный способ показать экран НАСТОЯЩИМ
маршрутом до `Record`), `popUpTo(graph.findStartDestination().id)` у всех
разделов стал бы целиться в `Onboarding`, а не в `Record`, ломая
save/restore state ровно как в инцидентах №1/№2 выше. Держи `Record`
единственным `startDestination` навсегда — новый экран «до Record» встраивай
условным рендером в `App()`, не в граф.

**Как следствие — свой safe-area инсет, не бесплатный.** Все top-level
экраны, включая `Record`, получают инсет статус-бара бесплатно через
`Scaffold`/`TopAppBar` (`SectionScaffold`). `OnboardingScreen` не внутри
NavHost и без `Scaffold` — просто `Column` с фиксированным `.padding(16.dp)`.
На Android это визуально не било (статус-бар либо просвечивал, либо
эмулятор не показывал проблему), на iOS заголовок «Welcome!» реально
наезжал на часы в статус-баре/чёлку — найдено и исправлено в Phase 4
(`.claude/plans/mushroom-collections.md`) живым прогоном на симуляторе.
Фикс — `Modifier.windowInsetsPadding(WindowInsets.safeDrawing)` перед
`.padding(16.dp)`. Любой будущий НЕ-NavHost экран «до Record» унаследует то
же самое, если скопирует голый `Column` вместо `Scaffold` — не забывать
про инсет вручную.

## Боковая панель (`App.kt`, не `ui/navigation/`)

`ModalNavigationDrawer` живёт в `App.kt`, не здесь — она оборачивает
`LeshyNavHost`, а не является его частью, поскольку сама не маршрут (см.
секцию выше про `OnboardingScreen`: то же рассуждение — оверлей поверх
текущего экрана, а не экран графа). Пункты панели — `drawerNavEntries` в
`App.kt`, один на каждый top-level `Destination`, каждый вызывает
`navController.navigateToTopLevel(...)`.

- `gesturesEnabled = false` — свайп от левого края конфликтует с
  панорамированием карты на `Record` (тот же исторический фикс, что был
  до ревёрта на домашнюю страницу).
- Заголовок панели — `StringKey.AppName`, а не отдельный
  «Выберите раздел»/`NavDrawerHeader` — по явному запросу пользователя
  панель и домашний экран (`Record`) единообразно показывают название
  приложения сверху. Строка пункта `Record` внутри панели при этом
  остаётся `StringKey.NavRecord` («Новая запись») — тайтл в самой
  `SectionScaffold` над `Record` и подпись пункта в списке панели — два
  разных места, не путать при правке одного без другого.
- **Закрытие по системному «назад», пока панель открыта, НЕ бесплатное** —
  KMP-версия `ModalNavigationDrawer` (`org.jetbrains.compose.material3`, в
  отличие от Android-only `androidx.compose.material3`) не регистрирует
  собственный back-handler вообще: закрывается только по тапу на scrim,
  Escape (desktop) или свайпу (у нас отключён `gesturesEnabled = false`).
  Явный `BackHandler(enabled = drawerState.isOpen) { drawerState.close() }`
  в `App.kt` — обязателен.
  **Порядок композиции этого `BackHandler` относительно
  `ModalNavigationDrawer` — не косметика.** `NavHost` внутри
  `LeshyNavHost` сам регистрирует `PredictiveBackHandler(currentBackStack.size
  > 1)` (`navigation-compose`, `NavHost.kt`). Диспетчер back-обработчиков
  (общий и для `androidx.compose.ui.backhandler.BackHandler`, и для
  `PredictiveBackHandler`) отдаёт приоритет ПОСЛЕДНЕМУ зарегистрированному
  enabled-колбэку — LIFO, не FIFO. Если наш `BackHandler` скомпонован ДО
  `ModalNavigationDrawer(...) { LeshyNavHost(...) }`, `NavHost`
  регистрируется позже и перехватывает «назад» первым: на любом
  не-стартовом разделе (например «Настройки») с открытой панелью системное
  «назад» реально попадало на `NavHost` — экран под панелью переключался на
  `Record`, а сама панель НЕ закрывалась (симптом воспроизведён живьём на
  Android-телефоне). Фикс — держать этот `BackHandler` последней инструкцией
  в `true ->` ветке `App()`, ПОСЛЕ вызова `ModalNavigationDrawer`, чтобы он
  регистрировался позже `NavHost` и получал приоритет, пока `drawerState.
  isOpen`. Панель не экран и не запись в бэкстеке `NavHostController`,
  поэтому такое «назад» просто закрывает её, оставляя видимым тот раздел,
  что был открыт до этого — не обязательно `Record`.
- Кнопка слева сверху на каждом top-level экране (`onMenuClick` в
  `SectionScaffold`) только открывает панель — сама никуда не навигирует
  (см. инцидент №3 выше про то, почему это важно).

## Прочее

- `NAV_TRANSITION_DURATION_MS = 200` (не библиотечный дефолт 700ms) —
  быстрее ощущается, и меньше шанс поймать «зависшую» карту поверх
  следующего экрана при более долгом fade (см. `ui/map/CLAUDE.md` про
  `TextureView`).
- **Одноразовые UI-сигналы** (`justFinished` в `RecordUiState`, `deleted` в
  `WalkDetailUiState`) — держать в `UiState`, не в локальном `remember`
  (правило проекта об immutable UiState), потреблять через
  `LaunchedEffect(uiState.flag)` + сразу вызывать `consumeXxx()` во
  ViewModel, сбрасывающий флаг обратно в `false`. Без сброса эффект
  зациклится при следующем восстановлении экрана через `restoreState=true`.
