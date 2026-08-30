package leshy.mushrooms.map.i18n

import androidx.compose.runtime.Composable
import leshy.mushrooms.map.domain.model.AppLanguage

/**
 * Resolves one help paragraph, mirroring [string] exactly: `ru`/`en` are exhaustive `when`
 * branches so the compiler catches a forgotten paragraph the moment a [HelpKey] is added, every
 * other [AppLanguage] goes through [helpTranslations] and degrades to English when its table is
 * missing or incomplete.
 *
 * The degradation is the intended state for a language whose help texts haven't been translated
 * yet — a reader gets English prose instead of an empty dialog. Which languages are in that state
 * is tracked by `HelpTextsTest` and by `.claude/plans/help-screens.md`, which also documents the
 * per-language translation procedure.
 */
fun helpText(key: HelpKey, language: AppLanguage): String = when (language) {
    AppLanguage.RU -> russianHelpTexts(key)
    AppLanguage.EN -> englishHelpTexts(key)
    else -> helpTranslations[language]?.get(key) ?: englishHelpTexts(key)
}

/** Composable form of [helpText], reading the active language from [LocalAppLanguage] — same
 * relationship [stringResource] has to [string]. */
@Composable
fun helpResource(key: HelpKey): String = helpText(key, LocalAppLanguage.current)

/**
 * Per-language help tables for every [AppLanguage] beyond `ru`/`en`, one `i18n/help/HelpTexts<Xx>
 * .kt` file each — the help-text twin of `uiTranslations` (`Strings.kt`) and, like it, checked for
 * completeness by a `commonTest` rather than by the compiler.
 *
 * **Currently empty on purpose**: the Russian and English texts below are reviewed first, and only
 * then translated language by language (`.claude/plans/help-screens.md`). Until a language lands
 * here its help dialog shows [englishHelpTexts]. `internal` so `HelpTextsTest` can assert against
 * it.
 */
internal val helpTranslations: Map<AppLanguage, Map<HelpKey, String>> = emptyMap()

private fun russianHelpTexts(key: HelpKey): String = when (key) {
    HelpKey.RecordPurpose ->
        "Главный экран приложения. Здесь записывается прогулка: по GPS пишется ваш трек, а каждая " +
            "находка сохраняется с координатами и временем. Всё, что вы отметили, попадает в " +
            "память сразу же, поэтому прогулку можно прервать в любой момент — записанное не " +
            "пропадёт."

    HelpKey.RecordActions ->
        "Кнопка «Старт» спрашивает название и начинает запись; дальше она превращается в «Пауза», " +
            "а на паузе появляются «Продолжить» и «Завершить». Плитки грибов внизу: «+» отмечает " +
            "находку в вашей текущей точке, «−» убирает последнюю ошибочную отметку этого вида, " +
            "долгое нажатие на «+» открывает ввод сразу нескольких штук. Круглая кнопка слева " +
            "отмечает место — с названием, описанием и фотографией; лупа справа находит нужный " +
            "гриб по названию и ставит его плитку в начало ленты; последняя плитка с плюсом " +
            "добавляет свой вид, которого нет в каталоге. Кнопка «Фильтры» слева сверху задаёт, " +
            "находки каких видов показывать на карте."

    HelpKey.RecordDetails ->
        "Запись трека продолжается, когда приложение свёрнуто. Кроме текущей прогулки карта " +
            "показывает находки и места прошлых прогулок — по ним видно, где вы уже ходили и что " +
            "там было. Долгое нажатие на метку места включает навигацию к ней: панель справа " +
            "сверху показывает направление и расстояние до цели. Одинаковых грибов за одну " +
            "прогулку можно отметить не больше 999. Кнопка «Завершить» закрывает прогулку и " +
            "переносит её в «Архив прогулок»."

    HelpKey.ArchivePurpose ->
        "Все ваши прогулки, новые сверху. На карточке — название, дата, продолжительность, " +
            "километраж, число находок и миниатюра пройденного трека."

    HelpKey.ArchiveActions ->
        "Нажатие на карточку открывает прогулку целиком: статистика, находки по видам, отмеченные " +
            "места, описание, кнопка «Смотреть карту» и кнопка «Поделиться» — она собирает картинку " +
            "из тех частей прогулки, которые вы отметите галочками. Название и описание можно " +
            "изменить там же. Долгое нажатие на карточку включает режим выбора: отмечайте нужные " +
            "прогулки нажатием и жмите «Удалить прогулки»; кнопка «Назад» выходит из этого режима."

    HelpKey.ArchiveDetails ->
        "Удаление необратимо — вместе с прогулкой исчезают её трек, находки, отмеченные места и " +
            "фотографии. Незавершённая прогулка тоже видна в списке: вместо времени финиша у неё " +
            "написано «не завершена». Перед тем как поделиться картой прогулки, помните, что по " +
            "ней видно, где именно вы нашли грибы."

    HelpKey.MapPurpose ->
        "Сводная карта: находки, треки и отмеченные места всех ваших прогулок сразу на одном " +
            "полотне. Нужна, чтобы видеть общую картину — где у вас грибные места и как они " +
            "меняются от года к году."

    HelpKey.MapActions ->
        "Переключатель сверху выбирает вид: «Карта» — сами отметки, «Статистика» — числа: сколько " +
            "было прогулок, километров, находок и сколько чего найдено по видам. Кнопка «Фильтры» " +
            "слева сверху ограничивает показ диапазоном дат, сезоном (диапазоном месяцев) и списком " +
            "видов; число на кнопке показывает, сколько фильтров сейчас включено. Нажатие на метку " +
            "места открывает его карточку с фотографией и описанием, оттуда же его можно изменить " +
            "или удалить."

    HelpKey.MapDetails ->
        "Фильтр общий с экраном записи: то, что вы включите здесь, будет действовать и там. " +
            "Диапазон дат появляется, только когда прогулки есть больше чем за один день. Когда " +
            "находок много, близкие отметки собираются в кружок с числом — приблизьте карту, чтобы " +
            "он рассыпался на отдельные грибы. Размер значков грибов настраивается в «Настройках»."

    HelpKey.SpeciesPurpose ->
        "Здесь вы решаете, какие грибы будут плитками на экране записи. Каталог разбит на подборки " +
            "по странам, и рядом с ним живут виды, которых в каталоге нет, — вы добавляете их сами."

    HelpKey.SpeciesActions ->
        "В «Подборках грибов» нажатие на строку страны раскрывает её виды: галочка у страны " +
            "включает всю подборку целиком, галочки внутри — отдельные виды. Поле поиска сверху " +
            "ищет страну по названию. Ниже, в «Добавленных грибах», кнопка «Добавить гриб» " +
            "открывает форму: название, научное название, цвет метки и картинка — с камеры, из " +
            "галереи или из каталога. Карандаш меняет уже добавленный вид, крестик удаляет его, а " +
            "галочка слева, как и у каталожных видов, отвечает только за показ на экране записи."

    HelpKey.SpeciesDetails ->
        "Снятая галочка ничего не удаляет — вид просто не показывается плиткой, а прошлые находки " +
            "остаются на месте. Удаление своего вида, наоборот, необратимо: все его отметки в " +
            "прошлых прогулках перейдут в «Неизвестный гриб». Виды с подписью «из архива» пришли " +
            "вместе с импортированными прогулками. Все изображения грибов в приложении условны — " +
            "не определяйте по ним незнакомые грибы."

    HelpKey.PreparationPurpose ->
        "Заранее скачивает куски карты в память телефона, чтобы в лесу без интернета карта " +
            "осталась на месте: без этого вдали от связи вместо карты будет пустой фон."

    HelpKey.PreparationActions ->
        "Найдите нужный участок — двигайте и масштабируйте карту, — затем нажмите круглую кнопку " +
            "со стрелкой вниз справа внизу. Приложение покажет, сколько места займёт то, что " +
            "сейчас на экране; кнопка «Скачать эту область» спросит название и начнёт " +
            "скачивание, «Отмена» вернёт карту. Скачанные области лежат полосой внизу: нажатие на " +
            "плашку перелетает к этой области на карте, а кнопки на плашке ставят скачивание на " +
            "паузу и возобновляют его, повторяют попытку после ошибки и удаляют область."

    HelpKey.PreparationDetails ->
        "Скачивается ровно то, что видно на экране, поэтому оценка размера меняется, пока вы " +
            "двигаете карту. Чем крупнее область, тем менее подробной её приходится делать — " +
            "выгоднее скачать несколько небольших участков, чем один огромный. Названия областей " +
            "не должны повторяться. Скачивание идёт в фоне и не прерывается, если уйти с экрана; " +
            "на паузе прогресс сохраняется. Обновление данных карты в «Настройках» перекачивает " +
            "все скачанные области заново."

    HelpKey.DataPurpose ->
        "Перенос прогулок между телефонами и резервная копия: выбранные прогулки выгружаются в " +
            "один файл-архив, и такой же файл можно загрузить обратно — на этом или на другом " +
            "устройстве."

    HelpKey.DataActions ->
        "Переключатель сверху выбирает «Экспорт» или «Импорт». В «Экспорте» задайте название " +
            "архива, нажмите строку выбора прогулок и отметьте нужные, затем «Готово» — телефон " +
            "спросит, куда сохранить файл. В «Импорте» нажмите «Выбрать файл», при желании впишите " +
            "приписку, которая добавится к названиям загружаемых прогулок, и нажмите «Готово»; " +
            "когда архив разберётся, появится кнопка «В архив». Кнопка «Отмена» сбрасывает " +
            "введённое, ничего не сохраняя."

    HelpKey.DataDetails ->
        "В архив попадают трек, находки, отмеченные места, фотографии и те виды грибов, которых " +
            "нет в каталоге, — на другом устройстве они появятся в «Моих грибах» с подписью «из " +
            "архива». Импорт всегда добавляет прогулки к уже имеющимся и ничего не заменяет, " +
            "поэтому повторная загрузка того же файла создаст их ещё раз; приписка к названиям " +
            "помогает потом отличить одно от другого. По окончании показывается, сколько прогулок " +
            "загрузилось и сколько не удалось прочитать."

    HelpKey.SettingsPurpose ->
        "Общие параметры приложения: язык интерфейса, вид и порядок плиток грибов на экране записи " +
            "и обслуживание карты."

    HelpKey.SettingsActions ->
        "Строка «Язык интерфейса» открывает список языков: нажатие выбирает язык, галочка сверху " +
            "подтверждает выбор, стрелка — выходит, ничего не меняя. Ползунок задаёт размер " +
            "значков грибов на карте, картинка под ним меняется прямо во время перетаскивания. Две " +
            "галочки в «Порядке грибов» управляют лентой плиток на экране записи. Внизу — кнопки " +
            "«Обновить данные карты» и «Очистить кэш карты»."

    HelpKey.SettingsDetails ->
        "Язык применяется сразу во всём приложении, перезапуск не нужен. Обычно только что " +
            "отмеченные грибы поднимаются в начало ленты плиток; «Неподвижный порядок грибов» " +
            "отключает это совсем, а «Сбрасывать порядок грибов в конце прогулки» возвращает " +
            "исходный порядок, когда прогулка завершена. «Обновить данные карты» проверяет, " +
            "изменилась ли карта на сервере, и, если да, перекачивает все скачанные офлайн-области " +
            "заново. «Очистить кэш карты» удаляет только то, что подгрузилось при просмотре, — " +
            "области из «Предзагрузки» остаются на месте."
}

private fun englishHelpTexts(key: HelpKey): String = when (key) {
    HelpKey.RecordPurpose ->
        "The app's home screen. This is where a walk is recorded: your track is logged from GPS " +
            "and every find is saved with its coordinates and time. Anything you mark is written " +
            "to storage immediately, so the walk can be interrupted at any moment without losing " +
            "what was already recorded."

    HelpKey.RecordActions ->
        "The Start button asks for a name and begins recording; it then turns into Pause, and " +
            "while paused you get Resume and Finish. The mushroom tiles along the bottom: “+” " +
            "logs a find at your current position, “−” removes the last mistaken find of that " +
            "species, and holding “+” opens an input for several at once. The round button on the " +
            "left marks a place — with a name, a description and a photo; the magnifier on the " +
            "right finds a mushroom by name and moves its tile to the front of the row; the last " +
            "tile, with a plus on it, adds a species of your own that the catalog doesn't have. " +
            "The Filters button in the top left controls which species' finds appear on the map."

    HelpKey.RecordDetails ->
        "Track recording continues while the app is in the background. Besides the current walk, " +
            "the map also shows finds and places from past walks, so you can see where you have " +
            "already been and what was there. Long-pressing a place marker starts navigation to " +
            "it: the panel in the top right shows the direction and the distance to the target. " +
            "One walk can hold at most 999 finds of the same species. Finish closes the walk and " +
            "moves it to the Archive."

    HelpKey.ArchivePurpose ->
        "All of your walks, newest first. Each card shows the name, the date, the duration, the " +
            "distance, the number of finds and a thumbnail of the track you walked."

    HelpKey.ArchiveActions ->
        "Tapping a card opens the whole walk: statistics, finds by species, marked places, the " +
            "description, a View map button and a Share button that builds an image out of " +
            "whichever parts of the walk you tick. The name and the description can be edited " +
            "there too. Long-pressing a card turns on selection mode: tap the walks you want and " +
            "press Delete walks; the Back button leaves selection mode."

    HelpKey.ArchiveDetails ->
        "Deletion is permanent — the walk's track, finds, marked places and photos go with it. An " +
            "unfinished walk is listed too, with “in progress” instead of a finish time. Before " +
            "sharing a walk's map, keep in mind that it shows exactly where you found the " +
            "mushrooms."

    HelpKey.MapPurpose ->
        "A combined map: the finds, tracks and marked places of all your walks at once on a " +
            "single canvas. It's here to show the big picture — where your mushroom spots are and " +
            "how they change from year to year."

    HelpKey.MapActions ->
        "The switch at the top picks the view: Map for the markers themselves, Stats for the " +
            "numbers — how many walks, kilometres and finds there were, and how many of each " +
            "species. The Filters button in the top left narrows what is shown by date range, by " +
            "season (a from–to range of months) and by species; the number on the button says how " +
            "many filters are currently on. Tapping a place marker opens its card with the photo " +
            "and description, from where it can also be edited or deleted."

    HelpKey.MapDetails ->
        "The filter is shared with the recording screen: whatever you switch on here applies " +
            "there as well. The date range only appears once you have walks from more than a " +
            "single day. When there are many finds, nearby markers collapse into a circle with a " +
            "count — zoom in and it breaks apart into individual mushrooms. The size of the " +
            "mushroom markers is adjusted in Settings."

    HelpKey.SpeciesPurpose ->
        "This is where you decide which mushrooms appear as tiles on the recording screen. The " +
            "catalog is split into collections by country, and next to it live the species the " +
            "catalog doesn't have — the ones you add yourself."

    HelpKey.SpeciesActions ->
        "Under Mushroom collections, tapping a country's row expands its species: the checkbox " +
            "next to the country switches the whole collection on or off, the checkboxes inside " +
            "switch individual species. The search field at the top finds a country by name. " +
            "Below, under Added mushrooms, the Add mushroom button opens a form: name, scientific " +
            "name, marker colour and a picture — from the camera, from the gallery or from the " +
            "catalog. The pencil edits a species you added, the cross deletes it, and the " +
            "checkbox on the left, exactly as for catalog species, only controls whether it shows " +
            "up on the recording screen."

    HelpKey.SpeciesDetails ->
        "Clearing a checkbox deletes nothing — the species simply stops appearing as a tile, and " +
            "past finds stay where they are. Deleting a species you added, on the other hand, is " +
            "permanent: all of its finds in past walks move to “Unknown mushroom”. Species marked " +
            "“from archive” arrived together with imported walks. Every mushroom image in the app " +
            "is illustrative only — never identify an unfamiliar mushroom by it."

    HelpKey.PreparationPurpose ->
        "Downloads pieces of the map into the phone's storage ahead of time, so the map is still " +
            "there in a forest with no connection — without this you get an empty background " +
            "once you're out of coverage."

    HelpKey.PreparationActions ->
        "Find the area you need — pan and zoom the map — then press the round button with the " +
            "down arrow in the bottom right. The app shows how much space what's currently on " +
            "screen will take; Download this area asks for a name and starts the download, Cancel " +
            "returns to the map. Downloaded areas sit in the strip along the bottom: tapping a " +
            "chip flies to that area on the map, and the buttons on the chip pause and resume the " +
            "download, retry it after an error, and delete the area."

    HelpKey.PreparationDetails ->
        "What gets downloaded is exactly what is visible on screen, which is why the size " +
            "estimate changes as you move the map. The larger the area, the less detailed it has " +
            "to be — several smaller areas are worth more than one huge one. Area names must be " +
            "unique. The download runs in the background and is not interrupted by leaving this " +
            "screen; pausing keeps the progress. Refreshing the map data in Settings re-downloads " +
            "every saved area from scratch."

    HelpKey.DataPurpose ->
        "Moving walks between phones, and backups: the walks you select are written to a single " +
            "archive file, and such a file can be loaded back in — on this device or another one."

    HelpKey.DataActions ->
        "The switch at the top chooses Export or Import. Under Export, set the archive name, tap " +
            "the walk-selection row and tick the walks you want, then press Done — the phone will " +
            "ask where to save the file. Under Import, press Choose file, optionally type a tag " +
            "to be appended to the names of the incoming walks, and press Done; once the archive " +
            "has been read a To archive button appears. The Cancel button clears what you entered " +
            "without saving anything."

    HelpKey.DataDetails ->
        "An archive carries the track, the finds, the marked places, the photos and any species " +
            "that aren't in the catalog — on the other device those appear under My mushrooms " +
            "labelled “from archive”. Import always adds walks alongside the existing ones and " +
            "never replaces them, so loading the same file twice creates them twice; the name tag " +
            "is what lets you tell one batch from the other afterwards. When it finishes, the " +
            "screen reports how many walks were imported and how many could not be read."

    HelpKey.SettingsPurpose ->
        "The app's general options: the interface language, the look and the ordering of the " +
            "mushroom tiles on the recording screen, and map maintenance."

    HelpKey.SettingsActions ->
        "The Interface language row opens the list of languages: tapping picks one, the tick at " +
            "the top confirms it, the arrow leaves without changing anything. The slider sets the " +
            "size of the mushroom markers on the map, and the picture below it resizes as you " +
            "drag. The two checkboxes under Mushroom order control the tile row on the recording " +
            "screen. At the bottom are the Refresh map data and Clear map cache buttons."

    HelpKey.SettingsDetails ->
        "The language applies across the whole app at once; no restart is needed. Normally the " +
            "species you have just marked move to the front of the tile row; “Freeze mushroom " +
            "order” switches that off entirely, and “Reset mushroom order when a walk ends” " +
            "restores the original order once a walk is finished. Refresh map data checks whether " +
            "the map has changed on the server and, if it has, re-downloads every saved offline " +
            "area. Clear map cache removes only what was loaded while browsing — the areas saved " +
            "under Preparation stay untouched."
}
