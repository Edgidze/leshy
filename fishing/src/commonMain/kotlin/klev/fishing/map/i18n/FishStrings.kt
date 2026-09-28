package klev.fishing.map.i18n

import androidx.compose.runtime.Composable
import klev.fishing.map.domain.model.Cloudiness
import klev.fishing.map.domain.model.CatchOutcome
import klev.fishing.map.domain.model.FishingMethod
import klev.fishing.map.domain.model.LostReason
import klev.fishing.map.domain.model.Precipitation
import klev.fishing.map.domain.model.PressureUnit
import klev.fishing.map.domain.model.PressureTrend
import klev.fishing.map.domain.model.WeatherProvenance
import klev.fishing.map.domain.model.WindDirection
import klev.fishing.map.domain.util.MoonPhase
import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.domain.model.ThemeMode
import leshy.mushrooms.map.i18n.LocalAppLanguage

/**
 * Строки интерфейса рыбацкого продукта на русском и английском.
 *
 * **Состояние: два языка из 42, и это решение владельца на первый проход, а не нарушение правила.**
 * Правило проекта «новая строка идёт сразу во все языки» существует для добавления строк в готовое
 * приложение; у нового продукта формулировки почти наверняка изменятся после первой приёмки, и
 * перевод 139 ключей на 42 языка пришлось бы делать дважды. Остальные сорок языков получают
 * английский текст через [fishString] и заводятся отдельным шагом — после того, как тексты
 * утверждены.
 *
 * Оба `when` — exhaustive, `else ->` в них не добавлять: именно это заставляет компилятор поймать
 * новый ключ без перевода. Причина та же, что у грибного `Strings.kt` (`i18n/CLAUDE.md`).
 */
fun fishString(key: FishStringKey, language: AppLanguage): String = when (language) {
    AppLanguage.RU -> russianFishStrings(key)
    // Сорок остальных языков пока приходят сюда. Когда появятся их таблицы, это место станет
    // обычным `when` по языку, как в грибном `Strings.kt`.
    else -> englishFishStrings(key)
}

@Composable
fun fishStringResource(key: FishStringKey): String = fishString(key, LocalAppLanguage.current)

private fun russianFishStrings(key: FishStringKey): String = when (key) {
    FishStringKey.AppName -> "Клёв"
    FishStringKey.NavRecord -> "Рыбалка"
    FishStringKey.NavArchive -> "Архив"
    FishStringKey.NavMap -> "Карта"
    FishStringKey.NavSettings -> "Настройки"
    FishStringKey.NavSpecies -> "Виды рыб"
    FishStringKey.NavSummary -> "Итоги"
    FishStringKey.SummaryTitle -> "Итоги"
    FishStringKey.SummaryTrips -> "Выездов"
    FishStringKey.SummaryFish -> "Рыб"
    FishStringKey.SummaryWeight -> "Общий вес"
    FishStringKey.SummaryBest -> "Самая крупная"
    FishStringKey.SummaryBaits -> "Что работало"
    FishStringKey.SummaryBaitNone -> "Без приманки"
    FishStringKey.SummarySpecies -> "Кого ловили"
    FishStringKey.SummaryHours -> "Часы клёва"
    FishStringKey.SummaryHoursHint -> "Поклёвки по часам суток — и взятые, и сошедшие."
    FishStringKey.SummaryPressure -> "Давление"
    FishStringKey.SummaryEmpty -> "Итогов пока нет"
    FishStringKey.SummaryEmptyHint -> "Запишите рыбалку — и здесь появится, что работало: приманки, виды, часы и давление."
    FishStringKey.SummaryLost -> "Сошло"
    FishStringKey.NavMenu -> "Меню"
    FishStringKey.SpeciesTitle -> "Виды рыб"
    FishStringKey.SpeciesHint -> "Касание убирает вид из ленты «Рыбалки» или возвращает его обратно. Ловится у вас не всё — и лента не должна быть длиннее нужного."
    FishStringKey.SettingsThemeLight -> "Светлое"
    FishStringKey.SettingsThemeSystem -> "Как в системе"
    FishStringKey.SettingsThemeDark -> "Тёмное"
    FishStringKey.SettingsMethodsTitle -> "Мои способы ловли"
    FishStringKey.SettingsMethodsHint -> "Оставьте те, которыми ловите: при старте рыбалки приложение предложит только их."
    FishStringKey.RecordTitle -> "Рыбалка"
    FishStringKey.RecordStart -> "Начать рыбалку"
    FishStringKey.RecordFinish -> "Закончить"
    FishStringKey.RecordIdleHint -> "Начните рыбалку — приложение запишет трек, а улов вы будете отмечать по ходу дела."
    FishStringKey.RecordDuration -> "Время"
    FishStringKey.RecordDistance -> "Путь"
    FishStringKey.RecordCatchCount -> "Поймано"
    FishStringKey.RecordAddCatch -> "Улов"
    FishStringKey.RecordMethodShore -> "С берега"
    FishStringKey.RecordMethodBoat -> "С лодки"
    FishStringKey.RecordMethodIce -> "Со льда"
    FishStringKey.RecordWaterBodyLabel -> "Водоём"
    FishStringKey.RecordWaterBodyHint -> "Название водоёма"
    FishStringKey.RecordNoLocation -> "Нет доступа к геопозиции — трек не записывается"
    FishStringKey.RecordSpeciesHidden -> "Все виды скрыты — вернуть их можно в разделе «Виды рыб»"
    FishStringKey.RecordQuickHint -> "Касание плитки — рыба записана сразу. Удержание — записать и тут же уточнить вес с приманкой."
    FishStringKey.RecordCatchSaved -> "Записано"
    FishStringKey.RecordRefine -> "Уточнить"
    FishStringKey.RecordTripDetails -> "Выезд"
    FishStringKey.RecordTripDetailsTitle -> "Детали выезда"
    FishStringKey.RecordFinishConfirm -> "Закончить рыбалку?"
    FishStringKey.RecordFinishConfirmText -> "Запись трека остановится. Улов и время останутся в архиве."
    FishStringKey.CatchDone -> "Готово"
    FishStringKey.CatchMore -> "Ещё"
    FishStringKey.CatchLess -> "Свернуть"
    FishStringKey.CatchBaitOther -> "Другая"
    FishStringKey.RecordTotalWeight -> "Всего"
    FishStringKey.CatchTitle -> "Улов"
    FishStringKey.CatchSpecies -> "Вид"
    FishStringKey.CatchSpeciesPick -> "Выберите вид"
    FishStringKey.CatchSpeciesChange -> "Сменить вид"
    FishStringKey.CatchWeight -> "Вес"
    FishStringKey.CatchWeightHint -> "Не взвешивали — оставьте пустым"
    FishStringKey.CatchLength -> "Длина"
    FishStringKey.CatchDepth -> "Глубина"
    FishStringKey.CatchDepthTooBig -> "Слишком глубоко — проверьте цифру"
    FishStringKey.CatchLengthHint -> "Не мерили — оставьте пустым"
    FishStringKey.CatchBait -> "Приманка"
    FishStringKey.CatchBaitHint -> "На что поймана"
    FishStringKey.CatchOutcome -> "Исход"
    FishStringKey.CatchOutcomeKept -> "Взята"
    FishStringKey.CatchOutcomeReleased -> "Отпущена"
    FishStringKey.CatchOutcomeLost -> "Упущена"
    FishStringKey.CatchLostReason -> "Что случилось"
    FishStringKey.CatchLostCameOff -> "Сошла при вываживании"
    FishStringKey.CatchLostLineBroke -> "Обрыв лески"
    FishStringKey.CatchLostHookFailed -> "Крючок разогнулся или сломался"
    FishStringKey.CatchLostTackleBroke -> "Сломалась снасть"
    FishStringKey.CatchLostSnagged -> "Ушла в коряги или траву"
    FishStringKey.CatchLostAtLanding -> "Упустили у берега"
    FishStringKey.CatchLostOther -> "Другое"
    FishStringKey.CatchPhoto -> "Фото улова"
    FishStringKey.CatchPhotoFromGallery -> "Из галереи"
    FishStringKey.CatchPhotoRemove -> "Убрать фото"
    FishStringKey.CatchPhotoDenied -> "Нет доступа к камере — снимок можно выбрать из галереи"
    FishStringKey.CatchNote -> "Заметка"
    FishStringKey.CatchNoteHint -> "Что стоит запомнить"
    FishStringKey.CatchDelete -> "Удалить улов"
    FishStringKey.CatchDeleteConfirm -> "Удалить эту запись?"
    FishStringKey.CatchWeightTooBig -> "Слишком большой вес"
    FishStringKey.CatchLengthTooBig -> "Слишком большая длина"
    FishStringKey.WeatherTitle -> "Погода на рыбалке"
    FishStringKey.WeatherIntro -> "Запишите погоду так, как её видели вы. Подсказка покажет, что о ней думает интернет, — её можно поправить."
    FishStringKey.WeatherAirTemp -> "Воздух"
    FishStringKey.WeatherWaterTemp -> "Вода"
    FishStringKey.WeatherPressure -> "Давление"
    FishStringKey.WeatherPressureTrend -> "За сутки"
    FishStringKey.WeatherTrendRising -> "Росло"
    FishStringKey.WeatherTrendSteady -> "Ровное"
    FishStringKey.WeatherTrendFalling -> "Падало"
    FishStringKey.WeatherWind -> "Ветер"
    FishStringKey.WeatherWindDirection -> "Направление"
    FishStringKey.WeatherCloudiness -> "Облачность"
    FishStringKey.WeatherClear -> "Ясно"
    FishStringKey.WeatherPartly -> "Переменная"
    FishStringKey.WeatherOvercast -> "Пасмурно"
    FishStringKey.WeatherPrecipitation -> "Осадки"
    FishStringKey.WeatherPrecipNone -> "Без осадков"
    FishStringKey.WeatherPrecipDrizzle -> "Морось"
    FishStringKey.WeatherPrecipRain -> "Дождь"
    FishStringKey.WeatherPrecipShower -> "Ливень"
    FishStringKey.WeatherPrecipSnow -> "Снег"
    FishStringKey.WeatherSuggest -> "Подсказать"
    FishStringKey.WeatherSuggesting -> "Смотрим…"
    FishStringKey.WeatherSuggestFailed -> "Не удалось узнать погоду — впишите сами"
    FishStringKey.WeatherSuggestHint -> "Подставлено по данным сети — проверьте"
    FishStringKey.WeatherSave -> "Сохранить погоду"
    FishStringKey.WeatherSkip -> "Не сейчас"
    FishStringKey.WeatherEdit -> "Изменить погоду"
    FishStringKey.WeatherEmpty -> "Погода не записана"
    FishStringKey.WeatherFromUser -> "По памяти"
    FishStringKey.WeatherFromService -> "По данным сети"
    FishStringKey.WeatherFromServiceEdited -> "По данным сети, с правками"
    FishStringKey.WeatherMoonPhase -> "Луна"
    FishStringKey.MoonNew -> "Новолуние"
    FishStringKey.MoonWaxingCrescent -> "Молодая"
    FishStringKey.MoonFirstQuarter -> "Первая четверть"
    FishStringKey.MoonWaxingGibbous -> "Растущая"
    FishStringKey.MoonFull -> "Полнолуние"
    FishStringKey.MoonWaningGibbous -> "Убывающая"
    FishStringKey.MoonLastQuarter -> "Последняя четверть"
    FishStringKey.MoonWaningCrescent -> "Старая"
    FishStringKey.WindN -> "С"
    FishStringKey.WindNE -> "СВ"
    FishStringKey.WindE -> "В"
    FishStringKey.WindSE -> "ЮВ"
    FishStringKey.WindS -> "Ю"
    FishStringKey.WindSW -> "ЮЗ"
    FishStringKey.WindW -> "З"
    FishStringKey.WindNW -> "СЗ"
    FishStringKey.ArchiveTitle -> "Архив"
    FishStringKey.ArchiveEmpty -> "Рыбалок пока нет"
    FishStringKey.ArchiveEmptyHint -> "Законченные рыбалки будут собираться здесь."
    FishStringKey.ArchiveRename -> "Переименовать"
    FishStringKey.ArchiveRenameTitle -> "Название рыбалки"
    FishStringKey.ArchiveDelete -> "Удалить рыбалку"
    FishStringKey.ArchiveDeleteConfirm -> "Удалить рыбалку вместе со всем уловом?"
    FishStringKey.ArchiveCatchesNone -> "Без улова"
    FishStringKey.MapTitle -> "Карта улова"
    FishStringKey.MapEmpty -> "На карте пока ничего нет"
    FishStringKey.MapEmptyHint -> "Отмеченный улов появится здесь после первой рыбалки."
    FishStringKey.MapAllSpecies -> "Все виды"
    FishStringKey.SettingsTitle -> "Настройки"
    FishStringKey.SettingsLanguage -> "Язык"
    FishStringKey.SettingsTheme -> "Оформление"
    FishStringKey.SettingsUnits -> "Единицы"
    FishStringKey.SettingsPressureUnit -> "Давление"
    FishStringKey.SettingsPressureHpa -> "гПа"
    FishStringKey.SettingsPressureMmHg -> "мм рт. ст."
    FishStringKey.SettingsWeightUnit -> "Вес и длина"
    FishStringKey.SettingsWeightMetric -> "Граммы и сантиметры"
    FishStringKey.SettingsWeightImperial -> "Фунты и дюймы"
    FishStringKey.SettingsAbout -> "О приложении"
    FishStringKey.UnitGram -> "г"
    FishStringKey.UnitKilogram -> "кг"
    FishStringKey.UnitCentimeter -> "см"
    FishStringKey.UnitMeter -> "м"
    FishStringKey.UnitMeterPerSecond -> "м/с"
    FishStringKey.UnitCelsius -> "°C"
    FishStringKey.UnitKilometer -> "км"
    FishStringKey.Cancel -> "Отмена"
    FishStringKey.Save -> "Сохранить"
    FishStringKey.Delete -> "Удалить"
    FishStringKey.Skip -> "Пропустить"
    FishStringKey.Close -> "Закрыть"
    FishStringKey.Yes -> "Да"
    FishStringKey.No -> "Нет"
}

private fun englishFishStrings(key: FishStringKey): String = when (key) {
    FishStringKey.AppName -> "Bite"
    FishStringKey.NavRecord -> "Fishing"
    FishStringKey.NavArchive -> "Archive"
    FishStringKey.NavMap -> "Map"
    FishStringKey.NavSettings -> "Settings"
    FishStringKey.NavSpecies -> "Fish species"
    FishStringKey.NavSummary -> "Summary"
    FishStringKey.SummaryTitle -> "Summary"
    FishStringKey.SummaryTrips -> "Trips"
    FishStringKey.SummaryFish -> "Fish"
    FishStringKey.SummaryWeight -> "Total weight"
    FishStringKey.SummaryBest -> "Biggest fish"
    FishStringKey.SummaryBaits -> "What worked"
    FishStringKey.SummaryBaitNone -> "No bait"
    FishStringKey.SummarySpecies -> "What you caught"
    FishStringKey.SummaryHours -> "Bite hours"
    FishStringKey.SummaryHoursHint -> "Bites by hour of day — landed and lost alike."
    FishStringKey.SummaryPressure -> "Pressure"
    FishStringKey.SummaryEmpty -> "Nothing to sum up yet"
    FishStringKey.SummaryEmptyHint -> "Record a trip and this is where what worked shows up: baits, species, hours and pressure."
    FishStringKey.SummaryLost -> "Lost"
    FishStringKey.NavMenu -> "Menu"
    FishStringKey.SpeciesTitle -> "Fish species"
    FishStringKey.SpeciesHint -> "Tap a species to drop it from the Record strip or bring it back. You do not catch everything — the strip should not be longer than it needs to be."
    FishStringKey.SettingsThemeLight -> "Light"
    FishStringKey.SettingsThemeSystem -> "Follow system"
    FishStringKey.SettingsThemeDark -> "Dark"
    FishStringKey.SettingsMethodsTitle -> "How I fish"
    FishStringKey.SettingsMethodsHint -> "Keep the ways you actually fish — starting a trip then offers only those."
    FishStringKey.RecordTitle -> "Fishing"
    FishStringKey.RecordStart -> "Start a trip"
    FishStringKey.RecordFinish -> "Finish"
    FishStringKey.RecordIdleHint -> "Start a trip and the app records your track; log each fish as you go."
    FishStringKey.RecordDuration -> "Duration"
    FishStringKey.RecordDistance -> "Distance"
    FishStringKey.RecordCatchCount -> "Caught"
    FishStringKey.RecordAddCatch -> "Add catch"
    FishStringKey.RecordMethodShore -> "From shore"
    FishStringKey.RecordMethodBoat -> "From a boat"
    FishStringKey.RecordMethodIce -> "On ice"
    FishStringKey.RecordWaterBodyLabel -> "Water"
    FishStringKey.RecordWaterBodyHint -> "Name of the water"
    FishStringKey.RecordNoLocation -> "No location access — the track is not being recorded"
    FishStringKey.RecordSpeciesHidden -> "Every species is hidden — bring them back in Fish species"
    FishStringKey.RecordQuickHint -> "Tap a tile and the fish is logged. Hold it to log and fill in weight and bait right away."
    FishStringKey.RecordCatchSaved -> "Logged"
    FishStringKey.RecordRefine -> "Refine"
    FishStringKey.RecordTripDetails -> "Trip"
    FishStringKey.RecordTripDetailsTitle -> "Trip details"
    FishStringKey.RecordFinishConfirm -> "Finish the trip?"
    FishStringKey.RecordFinishConfirmText -> "Track recording stops. Catches and time stay in the archive."
    FishStringKey.CatchDone -> "Done"
    FishStringKey.CatchMore -> "More"
    FishStringKey.CatchLess -> "Less"
    FishStringKey.CatchBaitOther -> "Other"
    FishStringKey.RecordTotalWeight -> "Total"
    FishStringKey.CatchTitle -> "Catch"
    FishStringKey.CatchSpecies -> "Species"
    FishStringKey.CatchSpeciesPick -> "Pick a species"
    FishStringKey.CatchSpeciesChange -> "Change species"
    FishStringKey.CatchWeight -> "Weight"
    FishStringKey.CatchWeightHint -> "Leave empty if you did not weigh it"
    FishStringKey.CatchLength -> "Length"
    FishStringKey.CatchDepth -> "Depth"
    FishStringKey.CatchDepthTooBig -> "That is very deep — check the number"
    FishStringKey.CatchLengthHint -> "Leave empty if you did not measure it"
    FishStringKey.CatchBait -> "Bait"
    FishStringKey.CatchBaitHint -> "What it took"
    FishStringKey.CatchOutcome -> "Outcome"
    FishStringKey.CatchOutcomeKept -> "Kept"
    FishStringKey.CatchOutcomeReleased -> "Released"
    FishStringKey.CatchOutcomeLost -> "Lost"
    FishStringKey.CatchLostReason -> "What happened"
    FishStringKey.CatchLostCameOff -> "Came off while playing it"
    FishStringKey.CatchLostLineBroke -> "Line broke"
    FishStringKey.CatchLostHookFailed -> "Hook bent or broke"
    FishStringKey.CatchLostTackleBroke -> "Tackle broke"
    FishStringKey.CatchLostSnagged -> "Went into snags or weed"
    FishStringKey.CatchLostAtLanding -> "Lost at the bank"
    FishStringKey.CatchLostOther -> "Other"
    FishStringKey.CatchPhoto -> "Catch photo"
    FishStringKey.CatchPhotoFromGallery -> "From gallery"
    FishStringKey.CatchPhotoRemove -> "Remove photo"
    FishStringKey.CatchPhotoDenied -> "No camera access — pick a shot from the gallery instead"
    FishStringKey.CatchNote -> "Note"
    FishStringKey.CatchNoteHint -> "Anything worth remembering"
    FishStringKey.CatchDelete -> "Delete catch"
    FishStringKey.CatchDeleteConfirm -> "Delete this entry?"
    FishStringKey.CatchWeightTooBig -> "Weight is too large"
    FishStringKey.CatchLengthTooBig -> "Length is too large"
    FishStringKey.WeatherTitle -> "Weather on the trip"
    FishStringKey.WeatherIntro -> "Record the weather as you saw it. The suggestion shows what the internet thinks it was; correct it as needed."
    FishStringKey.WeatherAirTemp -> "Air"
    FishStringKey.WeatherWaterTemp -> "Water"
    FishStringKey.WeatherPressure -> "Pressure"
    FishStringKey.WeatherPressureTrend -> "Over the day"
    FishStringKey.WeatherTrendRising -> "Rising"
    FishStringKey.WeatherTrendSteady -> "Steady"
    FishStringKey.WeatherTrendFalling -> "Falling"
    FishStringKey.WeatherWind -> "Wind"
    FishStringKey.WeatherWindDirection -> "Direction"
    FishStringKey.WeatherCloudiness -> "Cloud"
    FishStringKey.WeatherClear -> "Clear"
    FishStringKey.WeatherPartly -> "Partly cloudy"
    FishStringKey.WeatherOvercast -> "Overcast"
    FishStringKey.WeatherPrecipitation -> "Precipitation"
    FishStringKey.WeatherPrecipNone -> "None"
    FishStringKey.WeatherPrecipDrizzle -> "Drizzle"
    FishStringKey.WeatherPrecipRain -> "Rain"
    FishStringKey.WeatherPrecipShower -> "Downpour"
    FishStringKey.WeatherPrecipSnow -> "Snow"
    FishStringKey.WeatherSuggest -> "Suggest"
    FishStringKey.WeatherSuggesting -> "Looking…"
    FishStringKey.WeatherSuggestFailed -> "Could not fetch the weather — fill it in yourself"
    FishStringKey.WeatherSuggestHint -> "Filled in from the network — check it"
    FishStringKey.WeatherSave -> "Save weather"
    FishStringKey.WeatherSkip -> "Not now"
    FishStringKey.WeatherEdit -> "Edit weather"
    FishStringKey.WeatherEmpty -> "Weather not recorded"
    FishStringKey.WeatherFromUser -> "From memory"
    FishStringKey.WeatherFromService -> "From the network"
    FishStringKey.WeatherFromServiceEdited -> "From the network, corrected"
    FishStringKey.WeatherMoonPhase -> "Moon"
    FishStringKey.MoonNew -> "New moon"
    FishStringKey.MoonWaxingCrescent -> "Waxing crescent"
    FishStringKey.MoonFirstQuarter -> "First quarter"
    FishStringKey.MoonWaxingGibbous -> "Waxing gibbous"
    FishStringKey.MoonFull -> "Full moon"
    FishStringKey.MoonWaningGibbous -> "Waning gibbous"
    FishStringKey.MoonLastQuarter -> "Last quarter"
    FishStringKey.MoonWaningCrescent -> "Waning crescent"
    FishStringKey.WindN -> "N"
    FishStringKey.WindNE -> "NE"
    FishStringKey.WindE -> "E"
    FishStringKey.WindSE -> "SE"
    FishStringKey.WindS -> "S"
    FishStringKey.WindSW -> "SW"
    FishStringKey.WindW -> "W"
    FishStringKey.WindNW -> "NW"
    FishStringKey.ArchiveTitle -> "Archive"
    FishStringKey.ArchiveEmpty -> "No trips yet"
    FishStringKey.ArchiveEmptyHint -> "Finished trips collect here."
    FishStringKey.ArchiveRename -> "Rename"
    FishStringKey.ArchiveRenameTitle -> "Trip name"
    FishStringKey.ArchiveDelete -> "Delete trip"
    FishStringKey.ArchiveDeleteConfirm -> "Delete the trip and everything logged on it?"
    FishStringKey.ArchiveCatchesNone -> "No catch"
    FishStringKey.MapTitle -> "Catch map"
    FishStringKey.MapEmpty -> "Nothing on the map yet"
    FishStringKey.MapEmptyHint -> "Logged catches appear here after your first trip."
    FishStringKey.MapAllSpecies -> "All species"
    FishStringKey.SettingsTitle -> "Settings"
    FishStringKey.SettingsLanguage -> "Language"
    FishStringKey.SettingsTheme -> "Appearance"
    FishStringKey.SettingsUnits -> "Units"
    FishStringKey.SettingsPressureUnit -> "Pressure"
    FishStringKey.SettingsPressureHpa -> "hPa"
    FishStringKey.SettingsPressureMmHg -> "mmHg"
    FishStringKey.SettingsWeightUnit -> "Weight and length"
    FishStringKey.SettingsWeightMetric -> "Grams and centimetres"
    FishStringKey.SettingsWeightImperial -> "Pounds and inches"
    FishStringKey.SettingsAbout -> "About"
    FishStringKey.UnitGram -> "g"
    FishStringKey.UnitKilogram -> "kg"
    FishStringKey.UnitCentimeter -> "cm"
    FishStringKey.UnitMeter -> "m"
    FishStringKey.UnitMeterPerSecond -> "m/s"
    FishStringKey.UnitCelsius -> "°C"
    FishStringKey.UnitKilometer -> "km"
    FishStringKey.Cancel -> "Cancel"
    FishStringKey.Save -> "Save"
    FishStringKey.Delete -> "Delete"
    FishStringKey.Skip -> "Skip"
    FishStringKey.Close -> "Close"
    FishStringKey.Yes -> "Yes"
    FishStringKey.No -> "No"
}

// --- Подписи значений перечислений: один вызов вместо `when` по месту требования ---

fun FishingMethod.labelKey(): FishStringKey = when (this) {
    FishingMethod.SHORE -> FishStringKey.RecordMethodShore
    FishingMethod.BOAT -> FishStringKey.RecordMethodBoat
    FishingMethod.ICE -> FishStringKey.RecordMethodIce
}

fun PressureUnit.labelKey(): FishStringKey = when (this) {
    PressureUnit.HPA -> FishStringKey.SettingsPressureHpa
    PressureUnit.MM_HG -> FishStringKey.SettingsPressureMmHg
}

/**
 * Подписи режима оформления. Ключи свои, хотя `ThemeMode` — грибной тип из `:shared`: грибные
 * подписи лежат в грибном `StringKey`, у которого свой набор языков, и брать их значило бы показывать
 * рыбацкий экран настроек на двух языках вперемешку. Сам тип при этом переиспользуется целиком —
 * дублировать перечисление ради подписей незачем.
 */
fun ThemeMode.labelKey(): FishStringKey = when (this) {
    ThemeMode.LIGHT -> FishStringKey.SettingsThemeLight
    ThemeMode.SYSTEM -> FishStringKey.SettingsThemeSystem
    ThemeMode.DARK -> FishStringKey.SettingsThemeDark
}

fun CatchOutcome.labelKey(): FishStringKey = when (this) {
    CatchOutcome.KEPT -> FishStringKey.CatchOutcomeKept
    CatchOutcome.RELEASED -> FishStringKey.CatchOutcomeReleased
    CatchOutcome.LOST -> FishStringKey.CatchOutcomeLost
}

fun LostReason.labelKey(): FishStringKey = when (this) {
    LostReason.CAME_OFF -> FishStringKey.CatchLostCameOff
    LostReason.LINE_BROKE -> FishStringKey.CatchLostLineBroke
    LostReason.HOOK_FAILED -> FishStringKey.CatchLostHookFailed
    LostReason.TACKLE_BROKE -> FishStringKey.CatchLostTackleBroke
    LostReason.SNAGGED -> FishStringKey.CatchLostSnagged
    LostReason.LOST_AT_LANDING -> FishStringKey.CatchLostAtLanding
    LostReason.OTHER -> FishStringKey.CatchLostOther
}

fun PressureTrend.labelKey(): FishStringKey = when (this) {
    PressureTrend.RISING -> FishStringKey.WeatherTrendRising
    PressureTrend.STEADY -> FishStringKey.WeatherTrendSteady
    PressureTrend.FALLING -> FishStringKey.WeatherTrendFalling
}

fun Cloudiness.labelKey(): FishStringKey = when (this) {
    Cloudiness.CLEAR -> FishStringKey.WeatherClear
    Cloudiness.PARTLY -> FishStringKey.WeatherPartly
    Cloudiness.OVERCAST -> FishStringKey.WeatherOvercast
}

fun Precipitation.labelKey(): FishStringKey = when (this) {
    Precipitation.NONE -> FishStringKey.WeatherPrecipNone
    Precipitation.DRIZZLE -> FishStringKey.WeatherPrecipDrizzle
    Precipitation.RAIN -> FishStringKey.WeatherPrecipRain
    Precipitation.SHOWER -> FishStringKey.WeatherPrecipShower
    Precipitation.SNOW -> FishStringKey.WeatherPrecipSnow
}

fun WindDirection.labelKey(): FishStringKey = when (this) {
    WindDirection.N -> FishStringKey.WindN
    WindDirection.NE -> FishStringKey.WindNE
    WindDirection.E -> FishStringKey.WindE
    WindDirection.SE -> FishStringKey.WindSE
    WindDirection.S -> FishStringKey.WindS
    WindDirection.SW -> FishStringKey.WindSW
    WindDirection.W -> FishStringKey.WindW
    WindDirection.NW -> FishStringKey.WindNW
}

fun MoonPhase.labelKey(): FishStringKey = when (this) {
    MoonPhase.NEW -> FishStringKey.MoonNew
    MoonPhase.WAXING_CRESCENT -> FishStringKey.MoonWaxingCrescent
    MoonPhase.FIRST_QUARTER -> FishStringKey.MoonFirstQuarter
    MoonPhase.WAXING_GIBBOUS -> FishStringKey.MoonWaxingGibbous
    MoonPhase.FULL -> FishStringKey.MoonFull
    MoonPhase.WANING_GIBBOUS -> FishStringKey.MoonWaningGibbous
    MoonPhase.LAST_QUARTER -> FishStringKey.MoonLastQuarter
    MoonPhase.WANING_CRESCENT -> FishStringKey.MoonWaningCrescent
}

fun WeatherProvenance.labelKey(): FishStringKey = when (this) {
    WeatherProvenance.USER -> FishStringKey.WeatherFromUser
    WeatherProvenance.SUGGESTED -> FishStringKey.WeatherFromService
    WeatherProvenance.SUGGESTED_EDITED -> FishStringKey.WeatherFromServiceEdited
}
