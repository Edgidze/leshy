# ui/navigation/ — Compose Navigation

`Destination` — sealed interface маршрутов. `Home` — стартовый экран
(домашняя страница с крупными кнопками разделов), к нему возвращаются через
`popBackStack(Destination.Home, inclusive = false, saveState = true)` из
`SectionScaffold` (`ui/components/SectionScaffold.kt`) или системным
«назад» — сам `Home` никогда не является целью `navigateToTopLevel`.
**`saveState = true` здесь обязателен** — без него уход на `Home` уничтожает
бэкстек-запись раздела, а с ней и его ViewModel; для `Record` это рвёт
GPS-подписку (`viewModelScope`, см. `data/CLAUDE.md`/`androidMain`), при
этом фоновый foreground-сервис/уведомление не останавливается (он привязан
только к явной кнопке «Завершить»), и прогулка в архиве зависает
«Не завершена» — см. пункт 3 ниже. Остальные top-level экраны
(разделы: Запись/Архив/Карта/Настройки, кнопки на домашней странице) обязаны
переходить через один и тот же хелпер:

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
всегда `Home`, и он должен остаться внизу бэкстека (не вытесняться), иначе
«назад» с раздела не будет попадать на домашнюю страницу, а сразу выходить
из приложения. Именно это (пустой бэкстек под `Home`) и даёт «выход из
приложения по «назад»» на самой домашней странице — бесплатно, через
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
3. Кнопка «домой» (`onHomeClick` в `SectionScaffold`) — асимметрия в
   ОБРАТНУЮ сторону: `popBackStack(Destination.Home, false)` без
   `saveState` изначально уничтожала запись уходящего раздела вместо
   `navigateToTopLevel`-совместимого сохранения. Симптом воспроизводился
   только на `Record` во время активной записи: `Запись → домой → Подготовка
   → домой → Запись` — `RecordViewModel` уничтожался при первом уходе на
   `Home` (GPS-коллектор в `viewModelScope` обрывался вместе с ним), при
   возврате создавался новый экземпляр, ничего не знающий об активной
   прогулке (не перегидратируется из Room), а foreground-сервис с
   уведомлением продолжал висеть, потому что его останавливает только явный
   `finish()`. Прогулка так и оставалась в архиве «Не завершена». Фикс —
   `saveState = true` на всех 6 местах `onHomeClick` (не только у `Record`,
   для единообразия схемы).

## Общий `viewModelStoreOwner` для вложенных экранов карты

`WalkMapScreen`/детальные карты берут `koinViewModel` с тем же
`viewModelStoreOwner`, что и родительский экран (`backStackEntry` через
`navController.getBackStackEntry(Destination.WalkDetail(...))`), чтобы не
плодить второй ViewModel и не терять состояние.

**Обязательно оборачивать в `runCatching { ... }.getOrNull()`.** Переход по
`navigateToTopLevel` выталкивает записи из бэкстека СИНХРОННО
(`popUpTo(...){inclusive=false}` всё равно вытесняет предыдущий раздел, если
он не `Home`), до того как успевает закончиться
recomposition уходящего дочернего экрана во время exit-анимации — голый
`getBackStackEntry(...)` на этом кадре бросает `IllegalArgumentException` и
крашит приложение. С guard'ом composable просто ничего не рендерит на этот
короткий кадр (экран и так анимированно исчезает).

## Экран первого запуска — намеренно НЕ NavHost-маршрут

`OnboardingScreen` (`.claude/plans/mushroom-collections.md`, Phase 3) рендерится
`App()` ВМЕСТО всего `LeshyNavHost` (пока флаг `OnboardingRepository` не
`true`), не через `composable<Destination.X>` внутри графа. Соблазн завести
`Destination.Onboarding` и сделать его условным `startDestination` —
именно та ошибка, от которой предостерегает секция выше:
`navigateToTopLevel` у ВСЕХ топ-level разделов держится на том, что `Home`
навсегда единственный `startDestination` графа
(`graph.findStartDestination()`). Если бы `Onboarding` хоть раз стал
стартовым `Destination` (единственный способ показать экран НАСТОЯЩИМ
маршрутом до `Home`), `popUpTo(graph.findStartDestination().id)` у всех
разделов стал бы целиться в `Onboarding`, а не в `Home`, ломая
save/restore state ровно как в инцидентах №1/№2 выше. Держи `Home`
единственным `startDestination` навсегда — новый экран «до Home» встраивай
условным рендером в `App()`, не в граф.

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
