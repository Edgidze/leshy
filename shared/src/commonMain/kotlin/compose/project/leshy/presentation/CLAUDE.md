# presentation/ — ViewModel'и

## Паттерн: сырые Flow → `combine()` → чисто-UI state → `buildUiState()`

Используется в `MapViewModel`, `RecordViewModel`, `WalkDetailViewModel`,
`ArchiveViewModel`: репозитории комбинируются в приватный `RawXxxData` одним
`combine()`, затем ЕЩЁ раз комбинируются с чисто-UI-состоянием (режим,
фильтр, диалоги — свои `MutableStateFlow`) во втором `combine()`, который
зовёт `buildUiState(raw, ...)`. Разделение важно: если UI-флаги (например,
`showDeleteConfirmation`/`deleted`) держать в том же потоке, что и Room-данные,
любая новая эмиссия от БД (например, `observeById` вернёт `null` после
удаления) молча сбросит эти флаги.

Одноразовые сигналы (`justFinished`, `deleted`) — см.
`ui/navigation/CLAUDE.md`.

## `RecordViewModel` — общий между «Записью» и вложенными картами

Живёт в `viewModelStoreOwner = backStackEntry` экрана `Record` — см.
`ui/navigation/CLAUDE.md` про `runCatching`-guard и почему `inclusive=true`
без `saveState` роняет активную запись.

**Прыжок плитки в начало ленты после «+»/«−» — отложенный, с окном тишины
5с (`TILE_REORDER_QUIET_WINDOW`).** Раньше `addMushroom`/`removeMushroom`
звали `bringCategoryToFront` немедленно — плитка переставлялась (и лента
скроллилась к началу) прямо во время тапа, «выскакивая из-под пальца».
Теперь оба зовут `scheduleFrontBump`: id кладётся в `pendingFrontBumps`, и
запускается/перезапускается 5-секундный таймер (`frontBumpFlushJob`);
только когда лента простояла без действий все 5с подряд, `categoryOrder`
реально переставляется одним махом (`flushPendingFrontBumps`, последний
тап — самый первый в ленте). **Окно тишины — не «5с после конкретного
тапа», а «5с после последнего действия в ленте вообще»**: любой
последующий тап (`scheduleFrontBump` того же или другого вида) и ручной
скролл ленты (`RecordScreen` шлёт `notifyTileFeedInteraction()` через
`snapshotFlow { tileListState.isScrollInProgress }`) заново взводят один и
тот же таймер. `notifyTileFeedInteraction()` — no-op, пока
`pendingFrontBumps` пуст, поэтому безопасно вызывается и на программном
`animateScrollToItem(0)` самого флаша (к этому моменту очередь уже пуста —
повторного взвода не происходит).

Уход с экрана «Запись» таймер не приостанавливает — `viewModelScope` живёт
дольше композабла (см. выше), так что вид, отмеченный прямо перед тем как
уйти в другой раздел, почти наверняка уже окажется в начале ленты к
возврату (5с — короткое окно). `finish()` явно отменяет
`frontBumpFlushJob`/чистит `pendingFrontBumps` — иначе отложенный флаш мог
бы всплыть уже после `resetOrderOnWalkFinish` и молча отменить сброс
порядка.

**Отложенный механизм — только для «+»/«−» на самой плитке.**
`bringCategoryToFront` (немедленный, без окна) остаётся как есть и
по-прежнему вызывается напрямую из диалога поиска (`onSelect`) и после
создания вида со «Записи» (`saveNewSpecies`) — оба это не «пальцы на
ленте», а осознанный переход к конкретной плитке, там мгновенный прыжок
ожидаем и уместен.

**Сам флаш (`flushPendingFrontBumps`) — плавный и растянутый на 1с
(`TILE_REORDER_SCROLL_DURATION_MILLIS`), а не мгновенная перестановка.**
`scrollToStartSignal`/`scrollToStartDurationMillis` (`RecordUiState`) —
одна пара полей, всегда обновляется вместе одним `it.copy(...)`, чтобы
`RecordScreen` читал согласованное значение для конкретного события.
`bringCategoryToFront` пишет `scrollToStartDurationMillis = null`
(прежнее поведение — `animateScrollToItem(0)`, мгновенный по ощущению
скролл, плитки переставляются телепортом); `flushPendingFrontBumps` пишет
`TILE_REORDER_SCROLL_DURATION_MILLIS` — тогда `RecordScreen` вместо
`animateScrollToItem` считает точную пиксельную дистанцию до начала ленты
(все плитки одной ширины `TILE_WIDTH` + `TILE_SPACING`, дистанция —
`firstVisibleItemIndex * extent + firstVisibleItemScrollOffset`, без
надобности в уже измеренных оффскрин-айтемах) и скроллит
`animateScrollBy(-distancePx, tween(1000))`. Плитки при этом сами не
телепортируются в новую позицию — `MushroomTile` в `LazyRow` получает
`Modifier.animateItem(placementSpec = scrollToStartDurationMillis?.let
{ tween(it) })`: `null` (путь `bringCategoryToFront`) явно отключает
анимацию плейсмента (совпадает со старым мгновенным поведением), non-null
(путь флаша) — та же секунда, что и у скролла, так что взлёт плитки к
началу ленты и скролл к ней визуально завершаются синхронно.

## MapFilter (Часть 7) — единый фильтр для «Карты» и «Записи»

Три оси: диапазон дат, диапазон месяцев (сезон, без года), виды грибов.

- **Виды грибов переиспользуют `Category.isActive`**, не заводят новый
  персистентный набор — подтверждено явным вопросом пользователю. Поле уже
  означает «этот гриб включён»; окно фильтра просто переехало сюда с экрана
  «Настройки», пишет тем же `categoryRepository.upsert(category.copy
  (isActive=...))`. `MapFilterRepository` (DataStore) хранит только 2
  оставшиеся оси — 4 ключа, все nullable.
- **Список видов в самом диалоге фильтра гейтится `Category.
  isFilterEligible`**, не полным каталогом — `MapFilterViewModel` читает
  `categoryRepository.observeFilterEligible()`, сортировка `isPicked DESC`
  (через `partition` поверх обычного `sortCategories()`) поверх обычного
  порядка. Модель из двух флагов (`isPicked`/`isFilterEligible`) и каскад
  `isActive` при пересчёте — см. `.claude/plans/mushroom-collections.md`
  (Phase 2). Денормализация окупается тем, что список фильтра — простой
  `WHERE isFilterEligible = 1` без JOIN на `category_collections`; сам
  каскад (гашение `isActive`, когда вид перестаёт быть `isFilterEligible`)
  живёт в `RecalculateFilterEligibilityUseCase`, а не здесь — `MapFilterViewModel`
  только читает уже пересчитанное состояние.
- **iOS-визуальный проход (Phase 4)**: диалог фильтра, пикер подборок
  (тогда ещё в Настройках, с `user-mushrooms.md` Phase 4 переехал в раздел
  «Грибы» — см. ниже) и каскад `isPicked`→`isActive` из Phase 2 воспроизведены на
  симуляторе идентично Android — общий `commonMain`-код, платформенных
  расхождений в этой фиче нет. Единственный найденный на iOS баг был не в
  этой части, а в `OnboardingScreen` (см. `ui/navigation/CLAUDE.md`).
- **Дата/сезон фильтруют только отображение** (карта, статистика, фоновый
  слой прошлых находок на живой карте «Записи») — **НЕ** список грибов,
  доступных к отметке. Та же ось (виды) гейтит и список плашек: датой/сезоном
  гейтить запись было бы бессмысленно, прогулка идёт «сейчас».
- **Фоновый слой прошлых находок на «Записи»** (`LiveTrackMap.
  historicalMarkers`) не исключает находки ТЕКУЩЕЙ незавершённой прогулки по
  `walkId` — они физически совпадают по координатам с уже показанными
  крупными маркерами и полностью перекрыты ими по z-order, дублирование
  безвредно и не требует `walkId` как реактивного входа в `combine`.
- **Два бага в счётчике «Filters: N», оба в `computeFilterCount`
  (`domain/util/MapFilterMatching.kt`):**
  1. Проверка «есть ли выключенный вид» включала служебную `category_misc`
     (всегда `isActive=false` по сидированию) — счётчик был перманентно
     залипшим на +1 независимо от реальных тумблеров. Фикс — исключать
     `MISC_CATEGORY_NAME_KEY` из проверки.
  2. Слайдер диапазона дат коммитит миллисекунды, округлённые до начала
     суток, а счётчик сравнивал их с точными `Walk.startTime` — «визуально
     полный» диапазон всё равно считался «уже полного». Фикс — сравнивать
     ВЕЗДЕ (коммит слайдера, `matchesDateAndSeason`, сам счётчик) по одной и
     той же гранулярности суток через общую `const val MILLIS_PER_DAY`.

## `SpeciesViewModel` — не `SettingsViewModel` — владеет пикером подборок

`.claude/plans/user-mushrooms.md`, Phase 4: `collectionPickerItems`/
`toggleCollection`/`setCategoryPicked` переехали из `SettingsViewModel` в
новый `presentation/species/SpeciesViewModel.kt` вместе с самим
`CollectionPicker` (теперь на экране «Грибы», `ui/screens/SpeciesScreen.kt`,
а не в `SettingsScreen`). Причина переезда — не техническая, а по месту: у
раздела «Грибы» подборки и «Мои грибы» — одна логическая группа (выбор,
какие виды показывать/добавлять), Настройки остались только про
язык/размер-маркера/сортировку/данные карты. `SettingsViewModel` эту логику
больше не знает вообще — искать её теперь здесь, а не в Settings.
