package leshy.mushrooms.map.i18n

import androidx.compose.runtime.Composable
import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.i18n.help.albanianHelpTexts
import leshy.mushrooms.map.i18n.help.armenianHelpTexts
import leshy.mushrooms.map.i18n.help.azerbaijaniHelpTexts
import leshy.mushrooms.map.i18n.help.belarusianHelpTexts
import leshy.mushrooms.map.i18n.help.bosnianHelpTexts
import leshy.mushrooms.map.i18n.help.bulgarianHelpTexts
import leshy.mushrooms.map.i18n.help.croatianHelpTexts
import leshy.mushrooms.map.i18n.help.czechHelpTexts
import leshy.mushrooms.map.i18n.help.danishHelpTexts
import leshy.mushrooms.map.i18n.help.dutchHelpTexts
import leshy.mushrooms.map.i18n.help.estonianHelpTexts
import leshy.mushrooms.map.i18n.help.finnishHelpTexts
import leshy.mushrooms.map.i18n.help.frenchHelpTexts
import leshy.mushrooms.map.i18n.help.georgianHelpTexts
import leshy.mushrooms.map.i18n.help.germanHelpTexts
import leshy.mushrooms.map.i18n.help.greekHelpTexts
import leshy.mushrooms.map.i18n.help.hungarianHelpTexts
import leshy.mushrooms.map.i18n.help.icelandicHelpTexts
import leshy.mushrooms.map.i18n.help.italianHelpTexts
import leshy.mushrooms.map.i18n.help.japaneseHelpTexts
import leshy.mushrooms.map.i18n.help.kazakhHelpTexts
import leshy.mushrooms.map.i18n.help.koreanHelpTexts
import leshy.mushrooms.map.i18n.help.kyrgyzHelpTexts
import leshy.mushrooms.map.i18n.help.latvianHelpTexts
import leshy.mushrooms.map.i18n.help.lithuanianHelpTexts
import leshy.mushrooms.map.i18n.help.macedonianHelpTexts
import leshy.mushrooms.map.i18n.help.norwegianHelpTexts
import leshy.mushrooms.map.i18n.help.polishHelpTexts
import leshy.mushrooms.map.i18n.help.portugueseHelpTexts
import leshy.mushrooms.map.i18n.help.romanianHelpTexts
import leshy.mushrooms.map.i18n.help.serbianHelpTexts
import leshy.mushrooms.map.i18n.help.slovakHelpTexts
import leshy.mushrooms.map.i18n.help.slovenianHelpTexts
import leshy.mushrooms.map.i18n.help.spanishHelpTexts
import leshy.mushrooms.map.i18n.help.swedishHelpTexts
import leshy.mushrooms.map.i18n.help.tajikHelpTexts
import leshy.mushrooms.map.i18n.help.turkishHelpTexts
import leshy.mushrooms.map.i18n.help.turkmenHelpTexts
import leshy.mushrooms.map.i18n.help.ukrainianHelpTexts
import leshy.mushrooms.map.i18n.help.uzbekHelpTexts

/**
 * Resolves one help block, mirroring [string] exactly: `ru`/`en` are exhaustive `when` branches so
 * the compiler catches a forgotten block the moment a [HelpKey] is added, every other
 * [AppLanguage] goes through [helpTranslations] and degrades to English when its table is missing
 * or incomplete.
 *
 * The degradation is the intended state for a language whose help texts haven't been translated
 * yet — a reader gets English prose instead of an empty screen, next to the same pictures everyone
 * else sees. Which languages are in that state is tracked by `HelpTextsTest` and by
 * `.claude/plans/help-screens.md`, which also documents the per-language translation procedure.
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
 * **This map is complete**: all 40 non-ru/en [AppLanguage] values have a table, so the `else`
 * branch of [helpText] reaches real prose for every language and the English fallback is now only
 * the safety net for a 43rd language added before its own file is written. Order mirrors
 * `uiTranslations` — same languages, same batches, easier to diff the two side by side.
 *
 * `internal` so `HelpTextsTest` can assert against it.
 */
internal val helpTranslations: Map<AppLanguage, Map<HelpKey, String>> = mapOf(
    AppLanguage.AZ to azerbaijaniHelpTexts,
    AppLanguage.HY to armenianHelpTexts,
    AppLanguage.KK to kazakhHelpTexts,
    AppLanguage.KY to kyrgyzHelpTexts,
    AppLanguage.TG to tajikHelpTexts,
    AppLanguage.TK to turkmenHelpTexts,
    AppLanguage.UZ to uzbekHelpTexts,
    AppLanguage.DE to germanHelpTexts,
    AppLanguage.FR to frenchHelpTexts,
    AppLanguage.ES to spanishHelpTexts,
    AppLanguage.IT to italianHelpTexts,
    AppLanguage.PL to polishHelpTexts,
    AppLanguage.CS to czechHelpTexts,
    AppLanguage.UK to ukrainianHelpTexts,
    AppLanguage.SV to swedishHelpTexts,
    AppLanguage.JA to japaneseHelpTexts,
    AppLanguage.KO to koreanHelpTexts,
    AppLanguage.TR to turkishHelpTexts,
    AppLanguage.RO to romanianHelpTexts,
    AppLanguage.BE to belarusianHelpTexts,
    AppLanguage.BG to bulgarianHelpTexts,
    AppLanguage.SR to serbianHelpTexts,
    AppLanguage.HR to croatianHelpTexts,
    AppLanguage.SK to slovakHelpTexts,
    AppLanguage.SL to slovenianHelpTexts,
    AppLanguage.HU to hungarianHelpTexts,
    AppLanguage.FI to finnishHelpTexts,
    AppLanguage.LT to lithuanianHelpTexts,
    AppLanguage.LV to latvianHelpTexts,
    AppLanguage.ET to estonianHelpTexts,
    AppLanguage.KA to georgianHelpTexts,
    AppLanguage.BS to bosnianHelpTexts,
    AppLanguage.DA to danishHelpTexts,
    AppLanguage.EL to greekHelpTexts,
    AppLanguage.IS to icelandicHelpTexts,
    AppLanguage.MK to macedonianHelpTexts,
    AppLanguage.NB to norwegianHelpTexts,
    AppLanguage.NL to dutchHelpTexts,
    AppLanguage.PT to portugueseHelpTexts,
    AppLanguage.SQ to albanianHelpTexts,
)

/**
 * Русский оригинал справки. Один ключ — один блок: картинка элемента интерфейса
 * (`ui/components/HelpIllustrations.kt`) и одно-три предложения про этот элемент, не больше.
 *
 * Название кнопки внутри текста должно совпадать со `StringKey`, которым эта кнопка подписана в
 * интерфейсе, — иначе читатель ищет на экране слово, которого там нет.
 */
private fun russianHelpTexts(key: HelpKey): String = when (key) {
    HelpKey.RecordPurpose ->
        "Главный экран приложения: здесь записывается прогулка. По GPS пишется ваш трек, а каждая " +
            "находка сохраняется с координатами и временем — и попадает в память сразу же, " +
            "поэтому прогулку можно прервать в любой момент, записанное не пропадёт."

    HelpKey.RecordStartFinish ->
        "«Старт» спрашивает название и начинает запись; дальше кнопка превращается в «Пауза», а на " +
            "паузе появляются «Продолжить» и «Завершить». «Завершить» закрывает прогулку и " +
            "переносит её в «Архив прогулок»."

    HelpKey.RecordTiles ->
        "Плитки грибов внизу — то, чем отмечаются находки: «+» ставит находку в вашей текущей " +
            "точке, «−» убирает последнюю ошибочную отметку этого вида. Долгое нажатие на «+» " +
            "открывает ввод сразу нескольких штук; больше 999 одинаковых грибов за одну прогулку " +
            "отметить нельзя."

    HelpKey.RecordPlace ->
        "Круглая кнопка слева отмечает место — с названием, описанием и фотографией. Место " +
            "ставится там, где вы сейчас стоите, и остаётся на карте после прогулки."

    HelpKey.RecordNavigation ->
        "Долгое нажатие на метку места включает навигацию к нему: панель справа сверху показывает " +
            "направление и расстояние до цели. Крестик на панели выключает навигацию."

    HelpKey.RecordSearchAndOwn ->
        "Лупа справа находит нужный гриб по названию и ставит его плитку в начало ленты — так " +
            "быстрее, когда видов включено много. Последняя плитка ленты, с плюсом, добавляет свой " +
            "вид, которого нет в каталоге."

    HelpKey.RecordFilters ->
        "Кнопка «Фильтры» слева сверху задаёт, находки каких видов и за какое время показывать на " +
            "карте, а число на ней — сколько фильтров сейчас включено. Фильтр общий с «Картой " +
            "находок»: включённое здесь действует и там."

    HelpKey.RecordBackground ->
        "Запись трека продолжается, когда приложение свёрнуто. Кроме текущей прогулки карта " +
            "показывает находки и отмеченные места прошлых прогулок — по ним видно, где вы уже " +
            "ходили и что там было."

    HelpKey.ArchivePurpose ->
        "Все ваши прогулки, новые сверху. На карточке — название, дата, продолжительность, " +
            "километраж, число находок и миниатюра пройденного трека."

    HelpKey.ArchiveDetail ->
        "Нажатие на карточку открывает прогулку целиком: статистика, находки по видам, отмеченные " +
            "места, описание и кнопка «Смотреть карту». Название и описание можно изменить там же."

    HelpKey.ArchiveShare ->
        "Кнопка «Поделиться» собирает картинку из тех частей прогулки, которые вы отметите " +
            "галочками. Перед тем как отправить карту прогулки, помните: по ней видно, где именно " +
            "вы нашли грибы."

    HelpKey.ArchiveSelection ->
        "Долгое нажатие на карточку включает режим выбора: отмечайте нужные прогулки нажатием и " +
            "жмите «Удалить прогулки», а кнопка «Назад» выходит из этого режима. Удаление " +
            "необратимо — вместе с прогулкой исчезают её трек, находки, отмеченные места и " +
            "фотографии."

    HelpKey.ArchiveUnfinished ->
        "Незавершённая прогулка тоже видна в списке: вместо времени финиша у неё написано «не " +
            "завершена». Продолжительности у такой прогулки ещё нет, поэтому в общее время на " +
            "«Карте находок» она не идёт."

    HelpKey.MapPurpose ->
        "Сводная карта: находки, треки и отмеченные места всех ваших прогулок сразу на одном " +
            "полотне. Нужна, чтобы видеть общую картину — где у вас грибные места и как они " +
            "меняются от года к году."

    HelpKey.MapFullScreen ->
        "Сверху — карта со всеми находками сразу; нажатие на неё открывает карту во весь экран. " +
            "Когда находок много, близкие отметки собираются в кружок с числом — приблизьте карту, " +
            "и он рассыплется на отдельные грибы. Размер значков грибов настраивается в " +
            "«Настройках»."

    HelpKey.MapSliders ->
        "Под картой два ползунка — диапазон дат и сезон, то есть диапазон месяцев, — и всё, что " +
            "ниже них, считается по выбранному. Ползунки появляются, только когда прогулки есть " +
            "больше чем за один день."

    HelpKey.MapStats ->
        "Ниже ползунков — сколько было прогулок, километров, времени и находок, плитки по видам и " +
            "круговая диаграмма. Общее время складывается из завершённых прогулок: у незавершённой " +
            "продолжительности ещё нет."

    HelpKey.MapFilters ->
        "Кнопка «Фильтры» живёт на полноэкранной карте, слева сверху: там те же две оси, список " +
            "видов и переключатель показа прошлых треков. Число на кнопке — сколько фильтров " +
            "включено; фильтр общий с экраном записи."

    HelpKey.MapPlaces ->
        "Нажатие на метку места открывает его карточку с фотографией и описанием. Оттуда же место " +
            "можно изменить или удалить."

    HelpKey.SpeciesPurpose ->
        "Здесь вы решаете, какие грибы будут плитками на экране записи. Каталог разбит на подборки " +
            "по странам, а рядом с ним живут виды, которых в каталоге нет, — вы добавляете их сами."

    HelpKey.SpeciesCollections ->
        "В «Подборках грибов» нажатие на строку страны раскрывает её виды: галочка у страны " +
            "включает всю подборку целиком, галочки внутри — отдельные виды. Поле поиска сверху " +
            "находит страну по названию."

    HelpKey.SpeciesOwn ->
        "В «Добавленных грибах» кнопка «Добавить гриб» открывает форму: название, научное " +
            "название, цвет метки и картинка — с камеры, из галереи или из каталога. Карандаш " +
            "меняет уже добавленный вид, крестик удаляет его."

    HelpKey.SpeciesCheckboxes ->
        "Снятая галочка ничего не удаляет — вид просто не показывается плиткой, а прошлые находки " +
            "остаются на месте. Удаление своего вида, наоборот, необратимо: все его отметки в " +
            "прошлых прогулках перейдут в «Неизвестный гриб». Виды с подписью «из архива» пришли " +
            "вместе с импортированными прогулками."

    HelpKey.SpeciesImages ->
        "Все изображения грибов в приложении условны: они помогают узнать плитку, а не гриб в " +
            "лесу. Не определяйте по ним незнакомые грибы."

    HelpKey.PreparationPurpose ->
        "Заранее скачивает куски карты в память телефона, чтобы в лесу без интернета карта " +
            "осталась на месте: без этого вдали от связи вместо карты будет пустой фон."

    HelpKey.PreparationDownload ->
        "Найдите нужный участок — двигайте и масштабируйте карту, — затем нажмите круглую кнопку " +
            "со стрелкой вниз справа внизу. Приложение покажет, сколько места займёт то, что " +
            "сейчас на экране: «Скачать эту область» спросит название и начнёт скачивание, " +
            "«Отмена» вернёт карту."

    HelpKey.PreparationRegions ->
        "Скачанные области лежат полосой внизу. Нажатие на плашку перелетает к этой области на " +
            "карте, а кнопки на плашке ставят скачивание на паузу и возобновляют его, повторяют " +
            "попытку после ошибки и удаляют область."

    HelpKey.PreparationAreaSize ->
        "Скачивается ровно то, что видно на экране, поэтому оценка размера меняется, пока вы " +
            "двигаете карту. Чем крупнее область, тем менее подробной её приходится делать — " +
            "выгоднее скачать несколько небольших участков, чем один огромный. Названия областей " +
            "не должны повторяться."

    HelpKey.PreparationBackground ->
        "Скачивание идёт в фоне и не прерывается, если уйти с экрана, а на паузе прогресс " +
            "сохраняется. «Обновить данные карты» в «Настройках» перекачивает все скачанные " +
            "области заново."

    HelpKey.DataPurpose ->
        "Перенос прогулок между телефонами и резервная копия: выбранные прогулки выгружаются в " +
            "один файл-архив, и такой же файл можно загрузить обратно — на этом или на другом " +
            "устройстве."

    HelpKey.DataExport ->
        "Переключатель сверху выбирает «Экспорт» или «Импорт». В «Экспорте» задайте название " +
            "архива, нажмите строку выбора прогулок и отметьте нужные, затем «Готово» — телефон " +
            "спросит, куда сохранить файл."

    HelpKey.DataImport ->
        "В «Импорте» нажмите «Выбрать файл», при желании впишите приписку, которая добавится к " +
            "названиям загружаемых прогулок, и нажмите «Готово»; когда архив разберётся, появится " +
            "кнопка «В архив». Кнопка «Отмена» сбрасывает введённое, ничего не сохраняя."

    HelpKey.DataArchiveContents ->
        "В архив попадают трек, находки, отмеченные места, фотографии и те виды грибов, которых " +
            "нет в каталоге, — на другом устройстве они появятся в «Добавленных грибах» с " +
            "подписью «из архива»."

    HelpKey.DataDuplicates ->
        "Импорт всегда добавляет прогулки к уже имеющимся и ничего не заменяет, поэтому повторная " +
            "загрузка того же файла создаст их ещё раз: приписка к названиям помогает потом " +
            "отличить одно от другого. По окончании показывается, сколько прогулок загрузилось и " +
            "сколько не удалось прочитать."

    HelpKey.SettingsPurpose ->
        "Общие параметры приложения: язык интерфейса, оформление, вид и порядок плиток грибов на " +
            "экране записи и обслуживание карты."

    HelpKey.SettingsLanguage ->
        "Строка «Язык интерфейса» открывает список языков: нажатие выбирает язык, галочка сверху " +
            "подтверждает выбор, стрелка выходит, ничего не меняя. Язык применяется сразу во всём " +
            "приложении, перезапуск не нужен."

    HelpKey.SettingsTheme ->
        "«Оформление» переключает светлую и тёмную тему приложения. «Системное» отдаёт выбор " +
            "телефону: приложение темнеет и светлеет вместе с ним."

    HelpKey.SettingsMushroomSize ->
        "Ползунок задаёт размер значков грибов на карте — и на «Записи», и на сводной «Карте " +
            "находок». Картинка под ползунком меняется прямо во время перетаскивания, так что " +
            "размер видно до того, как отпустите."

    HelpKey.SettingsMushroomOrder ->
        "Обычно только что отмеченные грибы поднимаются в начало ленты плиток. «Неподвижный " +
            "порядок грибов» отключает это совсем, а «Сбрасывать порядок грибов в конце прогулки» " +
            "возвращает исходный порядок, когда прогулка завершена."

    HelpKey.SettingsMapData ->
        "«Обновить данные карты» проверяет, изменилась ли карта на сервере, и, если да, " +
            "перекачивает все скачанные офлайн-области заново. «Очистить кэш карты» удаляет только " +
            "то, что подгрузилось при просмотре, — области из «Предзагрузки» остаются на месте."
}

/** English original of the help — the text every untranslated language falls back to. Same rule as
 * in [russianHelpTexts]: a button named in the prose must be named exactly as its [StringKey]. */
private fun englishHelpTexts(key: HelpKey): String = when (key) {
    HelpKey.RecordPurpose ->
        "The app's home screen: this is where a walk is recorded. Your track is logged from GPS " +
            "and every find is saved with its coordinates and time — written to storage " +
            "immediately, so the walk can be interrupted at any moment without losing what was " +
            "already recorded."

    HelpKey.RecordStartFinish ->
        "Start asks for a name and begins recording; the button then turns into Pause, and while " +
            "paused you get Resume and Finish. Finish closes the walk and moves it to the Walk " +
            "Archive."

    HelpKey.RecordTiles ->
        "The mushroom tiles along the bottom are what finds are logged with: “+” logs a find at " +
            "your current position, “−” removes the last mistaken find of that species. Holding " +
            "“+” opens an input for several at once; one walk can hold at most 999 finds of the " +
            "same species."

    HelpKey.RecordPlace ->
        "The round button on the left marks a place — with a name, a description and a photo. The " +
            "place is put where you are standing and stays on the map after the walk."

    HelpKey.RecordNavigation ->
        "Long-pressing a place marker starts navigation to it: the panel in the top right shows " +
            "the direction and the distance to the target. The cross on the panel turns " +
            "navigation off."

    HelpKey.RecordSearchAndOwn ->
        "The magnifier on the right finds a mushroom by name and moves its tile to the front of " +
            "the row — quicker than scrolling when many species are switched on. The last tile in " +
            "the row, with a plus on it, adds a species of your own that the catalog doesn't have."

    HelpKey.RecordFilters ->
        "The Filters button in the top left controls which species' finds, and from which dates, " +
            "appear on the map; the number on it is how many filters are currently on. The filter " +
            "is shared with the Finds Map: whatever you switch on here applies there as well."

    HelpKey.RecordBackground ->
        "Track recording continues while the app is in the background. Besides the current walk, " +
            "the map also shows the finds and marked places of past walks, so you can see where " +
            "you have already been and what was there."

    HelpKey.ArchivePurpose ->
        "All of your walks, newest first. Each card shows the name, the date, the duration, the " +
            "distance, the number of finds and a thumbnail of the track you walked."

    HelpKey.ArchiveDetail ->
        "Tapping a card opens the whole walk: statistics, finds by species, marked places, the " +
            "description and a View map button. The name and the description can be edited there " +
            "too."

    HelpKey.ArchiveShare ->
        "The Share button builds an image out of whichever parts of the walk you tick. Before " +
            "sending a walk's map on, keep in mind that it shows exactly where you found the " +
            "mushrooms."

    HelpKey.ArchiveSelection ->
        "Long-pressing a card turns on selection mode: tap the walks you want and press Delete " +
            "walks; the Back button leaves selection mode. Deletion is permanent — the walk's " +
            "track, finds, marked places and photos go with it."

    HelpKey.ArchiveUnfinished ->
        "A walk still in progress is listed too, with “in progress” instead of a finish time. It " +
            "has no duration yet, which is why it adds nothing to the total time on the Finds Map."

    HelpKey.MapPurpose ->
        "A combined map: the finds, tracks and marked places of all your walks at once on a " +
            "single canvas. It's here to show the big picture — where your mushroom spots are and " +
            "how they change from year to year."

    HelpKey.MapFullScreen ->
        "At the top is a map of every find at once; tapping it opens the map full screen. When " +
            "there are many finds, nearby markers collapse into a circle with a count — zoom in " +
            "and it breaks apart into individual mushrooms. The size of the mushroom markers is " +
            "adjusted in Settings."

    HelpKey.MapSliders ->
        "Below the map are two sliders — a date range and a season, that is a from–to range of " +
            "months — and everything under them is counted for what you pick. The sliders only " +
            "appear once you have walks from more than a single day."

    HelpKey.MapStats ->
        "Under the sliders: how many walks, kilometres, hours and finds there were, the " +
            "per-species tiles and the pie chart. The total time adds up finished walks only — a " +
            "walk still in progress has no duration yet."

    HelpKey.MapFilters ->
        "The Filters button lives on the full-screen map, in its top left: the same two axes " +
            "there, plus the list of species and the switch for showing past tracks. The number " +
            "on the button says how many filters are on; the filter is shared with the recording " +
            "screen."

    HelpKey.MapPlaces ->
        "Tapping a place marker opens its card with the photo and the description. From there the " +
            "place can also be edited or deleted."

    HelpKey.SpeciesPurpose ->
        "This is where you decide which mushrooms appear as tiles on the recording screen. The " +
            "catalog is split into collections by country, and next to it live the species the " +
            "catalog doesn't have — the ones you add yourself."

    HelpKey.SpeciesCollections ->
        "Under Mushroom collections, tapping a country's row expands its species: the checkbox " +
            "next to the country switches the whole collection on or off, the checkboxes inside " +
            "switch individual species. The search field at the top finds a country by name."

    HelpKey.SpeciesOwn ->
        "Under Added mushrooms, the Add mushroom button opens a form: name, scientific name, " +
            "marker colour and a picture — from the camera, from the gallery or from the catalog. " +
            "The pencil edits a species you added, the cross deletes it."

    HelpKey.SpeciesCheckboxes ->
        "Clearing a checkbox deletes nothing — the species simply stops appearing as a tile, and " +
            "past finds stay where they are. Deleting a species you added, on the other hand, is " +
            "permanent: all of its finds in past walks move to “Unknown mushroom”. Species marked " +
            "“from archive” arrived together with imported walks."

    HelpKey.SpeciesImages ->
        "Every mushroom image in the app is illustrative only: it is there to help you recognise " +
            "a tile, not a mushroom in the forest. Never identify an unfamiliar mushroom by it."

    HelpKey.PreparationPurpose ->
        "Downloads pieces of the map into the phone's storage ahead of time, so the map is still " +
            "there in a forest with no connection — without this you get an empty background once " +
            "you're out of coverage."

    HelpKey.PreparationDownload ->
        "Find the area you need — pan and zoom the map — then press the round button with the " +
            "down arrow in the bottom right. The app shows how much space what's currently on " +
            "screen will take: Download this area asks for a name and starts the download, Cancel " +
            "returns to the map."

    HelpKey.PreparationRegions ->
        "Downloaded areas sit in the strip along the bottom. Tapping a chip flies to that area on " +
            "the map, and the buttons on the chip pause and resume the download, retry it after " +
            "an error, and delete the area."

    HelpKey.PreparationAreaSize ->
        "What gets downloaded is exactly what is visible on screen, which is why the size " +
            "estimate changes as you move the map. The larger the area, the less detailed it has " +
            "to be — several smaller areas are worth more than one huge one. Area names must be " +
            "unique."

    HelpKey.PreparationBackground ->
        "The download runs in the background and is not interrupted by leaving this screen, and " +
            "pausing keeps the progress. Update map data in Settings re-downloads every saved " +
            "area from scratch."

    HelpKey.DataPurpose ->
        "Moving walks between phones, and backups: the walks you select are written to a single " +
            "archive file, and such a file can be loaded back in — on this device or another one."

    HelpKey.DataExport ->
        "The switch at the top chooses Export or Import. Under Export, set the archive name, tap " +
            "the walk-selection row and tick the walks you want, then press Done — the phone will " +
            "ask where to save the file."

    HelpKey.DataImport ->
        "Under Import, press Choose file, optionally type a tag to be appended to the names of " +
            "the incoming walks, and press Done; once the archive has been read a To Archive " +
            "button appears. The Cancel button clears what you entered without saving anything."

    HelpKey.DataArchiveContents ->
        "An archive carries the track, the finds, the marked places, the photos and any species " +
            "that aren't in the catalog — on the other device those appear under Added mushrooms " +
            "labelled “from archive”."

    HelpKey.DataDuplicates ->
        "Import always adds walks alongside the existing ones and never replaces them, so loading " +
            "the same file twice creates them twice: the name tag is what lets you tell one batch " +
            "from the other afterwards. When it finishes, the screen reports how many walks were " +
            "imported and how many could not be read."

    HelpKey.SettingsPurpose ->
        "The app's general options: the interface language, the appearance, the look and the " +
            "ordering of the mushroom tiles on the recording screen, and map maintenance."

    HelpKey.SettingsLanguage ->
        "The Interface language row opens the list of languages: tapping picks one, the tick at " +
            "the top confirms it, the arrow leaves without changing anything. The language " +
            "applies across the whole app at once; no restart is needed."

    HelpKey.SettingsTheme ->
        "Appearance switches the app between the light and the dark theme. System hands the " +
            "choice to the phone: the app then darkens and lightens together with it."

    HelpKey.SettingsMushroomSize ->
        "The slider sets the size of the mushroom markers on the map — both on the recording " +
            "screen and on the combined Finds Map. The picture below it resizes as you drag, so " +
            "you see the size before letting go."

    HelpKey.SettingsMushroomOrder ->
        "Normally the species you have just marked move to the front of the tile row. “Freeze " +
            "mushroom order” switches that off entirely, and “Reset mushroom order when a walk " +
            "ends” restores the original order once a walk is finished."

    HelpKey.SettingsMapData ->
        "Update map data checks whether the map has changed on the server and, if it has, " +
            "re-downloads every saved offline area. Clear map cache removes only what was loaded " +
            "while browsing — the areas saved under Preload stay untouched."
}
