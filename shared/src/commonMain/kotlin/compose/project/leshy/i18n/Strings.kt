package compose.project.leshy.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import compose.project.leshy.domain.model.AppLanguage

val LocalAppLanguage = compositionLocalOf { AppLanguage.EN }

@Composable
fun stringResource(key: StringKey): String = string(key, LocalAppLanguage.current)

fun string(key: StringKey, language: AppLanguage): String = when (language) {
    AppLanguage.RU -> russianStrings(key)
    AppLanguage.EN -> englishStrings(key)
}

private fun russianStrings(key: StringKey): String = when (key) {
    StringKey.AppName -> "Леший"
    StringKey.NavRecord -> "Запись"
    StringKey.NavArchive -> "Архив"
    StringKey.NavMap -> "Карта"
    StringKey.SettingsTitle -> "Настройки"
    StringKey.SettingsContentDescription -> "Настройки"
    StringKey.SettingsLanguageTitle -> "Язык"
    StringKey.SettingsCategoriesTitle -> "Грибы для отметки"

    StringKey.CategoryPorcini -> "Белый гриб"
    StringKey.CategoryChanterelle -> "Лисичка"
    StringKey.CategoryRyzhik -> "Рыжик"
    StringKey.CategoryBoletus -> "Подберёзовик"
    StringKey.CategoryMisc -> "Разное"

    StringKey.DefaultWalkName -> "Прогулка"
    StringKey.RecordWalkNameHint -> "Название прогулки"
    StringKey.RecordStart -> "Старт"
    StringKey.RecordPause -> "Пауза"
    StringKey.RecordResume -> "Продолжить"
    StringKey.RecordFinish -> "Завершить"
    StringKey.RecordViewMap -> "Смотреть карту"
    StringKey.RecordCameraContentDescription -> "Сфотографировать"
    StringKey.RecordMapTitle -> "Текущая прогулка"

    StringKey.ArchiveEmpty -> "Прогулок пока нет"
    StringKey.WalkDetailStartTime -> "Старт"
    StringKey.WalkDetailEndTime -> "Финиш"
    StringKey.WalkDetailInProgress -> "не завершена"
    StringKey.WalkDetailDistance -> "Километраж"
    StringKey.WalkDetailFindsTitle -> "Находки по типам"
    StringKey.WalkDetailViewMap -> "Смотреть карту"

    StringKey.MapToggleMap -> "Карта"
    StringKey.MapToggleStats -> "Статистика"
    StringKey.MapPeriodAll -> "За всё время"
    StringKey.MapStatsWalksCount -> "Прогулок"
    StringKey.MapStatsFindsCount -> "Найдено грибов"
}

private fun englishStrings(key: StringKey): String = when (key) {
    StringKey.AppName -> "Leshy"
    StringKey.NavRecord -> "Record"
    StringKey.NavArchive -> "Archive"
    StringKey.NavMap -> "Map"
    StringKey.SettingsTitle -> "Settings"
    StringKey.SettingsContentDescription -> "Settings"
    StringKey.SettingsLanguageTitle -> "Language"
    StringKey.SettingsCategoriesTitle -> "Mushrooms to track"

    StringKey.CategoryPorcini -> "Porcini"
    StringKey.CategoryChanterelle -> "Chanterelle"
    StringKey.CategoryRyzhik -> "Saffron milk cap"
    StringKey.CategoryBoletus -> "Boletus"
    StringKey.CategoryMisc -> "Misc"

    StringKey.DefaultWalkName -> "Walk"
    StringKey.RecordWalkNameHint -> "Walk name"
    StringKey.RecordStart -> "Start"
    StringKey.RecordPause -> "Pause"
    StringKey.RecordResume -> "Resume"
    StringKey.RecordFinish -> "Finish"
    StringKey.RecordViewMap -> "View map"
    StringKey.RecordCameraContentDescription -> "Take photo"
    StringKey.RecordMapTitle -> "Current walk"

    StringKey.ArchiveEmpty -> "No walks recorded yet"
    StringKey.WalkDetailStartTime -> "Started"
    StringKey.WalkDetailEndTime -> "Finished"
    StringKey.WalkDetailInProgress -> "in progress"
    StringKey.WalkDetailDistance -> "Distance"
    StringKey.WalkDetailFindsTitle -> "Finds by type"
    StringKey.WalkDetailViewMap -> "View map"

    StringKey.MapToggleMap -> "Map"
    StringKey.MapToggleStats -> "Statistics"
    StringKey.MapPeriodAll -> "All time"
    StringKey.MapStatsWalksCount -> "Walks"
    StringKey.MapStatsFindsCount -> "Mushrooms found"
}
