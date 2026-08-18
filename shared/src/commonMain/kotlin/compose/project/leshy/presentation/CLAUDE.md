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
- **iOS-визуальный проход (Phase 4)**: диалог фильтра, пикер подборок в
  Настройках и каскад `isPicked`→`isActive` из Phase 2 воспроизведены на
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
