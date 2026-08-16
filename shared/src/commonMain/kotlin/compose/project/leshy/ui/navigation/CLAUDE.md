# ui/navigation/ — Compose Navigation

`Destination` — sealed interface маршрутов. Все top-level экраны (пункты
drawer: Запись/Архив/Карта/Настройки) обязаны переходить через один и тот же
хелпер:

```kotlin
fun NavHostController.navigateToTopLevel(destination: Destination) {
    navigate(destination) {
        popUpTo(graph.findStartDestination().id) { inclusive = true; saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
```

## Почему это жёсткое правило, а не стиль

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

## Общий `viewModelStoreOwner` для вложенных экранов карты

`WalkMapScreen`/детальные карты берут `koinViewModel` с тем же
`viewModelStoreOwner`, что и родительский экран (`backStackEntry` через
`navController.getBackStackEntry(Destination.WalkDetail(...))`), чтобы не
плодить второй ViewModel и не терять состояние.

**Обязательно оборачивать в `runCatching { ... }.getOrNull()`.** Переход по
нижней/drawer-навигации выталкивает записи из бэкстека СИНХРОННО
(`popUpTo(...){inclusive=true}`), до того как успевает закончиться
recomposition уходящего дочернего экрана во время exit-анимации — голый
`getBackStackEntry(...)` на этом кадре бросает `IllegalArgumentException` и
крашит приложение. С guard'ом composable просто ничего не рендерит на этот
короткий кадр (экран и так анимированно исчезает).

## Прочее

- `NAV_TRANSITION_DURATION_MS = 200` (не библиотечный дефолт 700ms) —
  быстрее ощущается, и меньше шанс поймать «зависшую» карту поверх
  следующего экрана при более долгом fade (см. `ui/map/CLAUDE.md` про
  `TextureView`).
- `ModalNavigationDrawer(gesturesEnabled = false, ...)` — свайп-открытие
  отключено намеренно: конфликтовало с панорамированием карты на «Записи».
- **Одноразовые UI-сигналы** (`justFinished` в `RecordUiState`, `deleted` в
  `WalkDetailUiState`) — держать в `UiState`, не в локальном `remember`
  (правило проекта об immutable UiState), потреблять через
  `LaunchedEffect(uiState.flag)` + сразу вызывать `consumeXxx()` во
  ViewModel, сбрасывающий флаг обратно в `false`. Без сброса эффект
  зациклится при следующем восстановлении экрана через `restoreState=true`.
