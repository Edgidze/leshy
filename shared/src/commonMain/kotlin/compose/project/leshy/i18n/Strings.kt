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

/** "гриб"/"гриба"/"грибов" (Russian 3-way plural, agreeing with [count]) or "mushroom"/"mushrooms" (English). */
@Composable
fun mushroomsUnitLabel(count: Int): String {
    val key = when (LocalAppLanguage.current) {
        AppLanguage.RU -> russianMushroomsPluralKey(count)
        AppLanguage.EN -> if (count == 1) StringKey.WalkDetailMushroomsCountOne else StringKey.WalkDetailMushroomsCountMany
    }
    return stringResource(key)
}

private fun russianMushroomsPluralKey(count: Int): StringKey {
    val mod100 = count % 100
    val mod10 = count % 10
    return when {
        mod100 in 11..14 -> StringKey.WalkDetailMushroomsCountMany
        mod10 == 1 -> StringKey.WalkDetailMushroomsCountOne
        mod10 in 2..4 -> StringKey.WalkDetailMushroomsCountFew
        else -> StringKey.WalkDetailMushroomsCountMany
    }
}

/** "прогулка"/"прогулки"/"прогулок" (Russian 3-way plural, agreeing with [count]) or "walk"/"walks" (English). */
@Composable
fun walksUnitLabel(count: Int): String {
    val key = when (LocalAppLanguage.current) {
        AppLanguage.RU -> russianWalksPluralKey(count)
        AppLanguage.EN -> if (count == 1) StringKey.DataWalksCountOne else StringKey.DataWalksCountMany
    }
    return stringResource(key)
}

private fun russianWalksPluralKey(count: Int): StringKey {
    val mod100 = count % 100
    val mod10 = count % 10
    return when {
        mod100 in 11..14 -> StringKey.DataWalksCountMany
        mod10 == 1 -> StringKey.DataWalksCountOne
        mod10 in 2..4 -> StringKey.DataWalksCountFew
        else -> StringKey.DataWalksCountMany
    }
}

/** "область"/"области"/"областей" (Russian 3-way plural, agreeing with [count]) or "area"/"areas" (English). */
@Composable
fun regionsUnitLabel(count: Int): String {
    val key = when (LocalAppLanguage.current) {
        AppLanguage.RU -> russianRegionsPluralKey(count)
        AppLanguage.EN -> if (count == 1) StringKey.SettingsMapDataRegionsCountOne else StringKey.SettingsMapDataRegionsCountMany
    }
    return stringResource(key)
}

private fun russianRegionsPluralKey(count: Int): StringKey {
    val mod100 = count % 100
    val mod10 = count % 10
    return when {
        mod100 in 11..14 -> StringKey.SettingsMapDataRegionsCountMany
        mod10 == 1 -> StringKey.SettingsMapDataRegionsCountOne
        mod10 in 2..4 -> StringKey.SettingsMapDataRegionsCountFew
        else -> StringKey.SettingsMapDataRegionsCountMany
    }
}

private fun russianStrings(key: StringKey): String = when (key) {
    StringKey.AppName -> "Грибная карта от Лешего"
    StringKey.NavRecord -> "Новая запись"
    StringKey.NavArchive -> "Архив прогулок"
    StringKey.NavData -> "Экспорт/Импорт"
    StringKey.NavMap -> "Карта находок"
    StringKey.NavPreparation -> "Предзагрузка"
    StringKey.NavSpecies -> "Мои грибы"
    StringKey.SettingsTitle -> "Настройки"
    StringKey.SettingsContentDescription -> "Настройки"
    StringKey.SettingsLanguageTitle -> "Язык"
    StringKey.SettingsCategoriesTitle -> "Грибы для отметки"
    StringKey.SettingsMushroomSizeTitle -> "Настройте размер грибов на карте"
    StringKey.SettingsMushroomSortTitle -> "Порядок грибов"
    StringKey.SettingsMushroomSortByAlphabetical -> "По алфавиту"
    StringKey.SettingsMushroomSortByPoisonousLast -> "Ядовитые в конец"
    StringKey.SettingsResetMushroomOrderOnWalkFinish -> "Сбрасывать порядок грибов в конце прогулки"
    StringKey.SettingsFreezeMushroomOrder -> "Неподвижный порядок грибов"

    StringKey.SpeciesCollectionsTitle -> "Подборки грибов"
    StringKey.SpeciesMyMushroomsTitle -> "Добавленные грибы"
    StringKey.SpeciesMyMushroomsEmpty -> "Здесь появятся грибы, которые вы добавите сами"
    StringKey.SpeciesAddButton -> "Добавить гриб"
    StringKey.SpeciesFormTitleCreate -> "Новый гриб"
    StringKey.SpeciesFormTitleEdit -> "Изменить гриб"
    StringKey.SpeciesFormNameHint -> "Название"
    StringKey.SpeciesFormScientificNameHint -> "Научное название"
    StringKey.SpeciesFormEdibilityLabel -> "Ядовитость"
    StringKey.SpeciesFormEdibilityPoisonous -> "Ядовитый"
    StringKey.SpeciesFormEdibilityNotSpecified -> "Не указано"
    StringKey.SpeciesFormColorLabel -> "Цвет"
    StringKey.SpeciesFormTakePhotoButton -> "Камера"
    StringKey.SpeciesFormPickPhotoButton -> "Галерея"
    StringKey.SpeciesFormSaveButton -> "Сохранить"
    StringKey.SpeciesFormCancelContentDescription -> "Отмена"
    StringKey.SpeciesListImportedLabel -> "из архива"
    StringKey.SpeciesListEditContentDescription -> "Редактировать"
    StringKey.SpeciesListDeleteContentDescription -> "Удалить вид"
    StringKey.SpeciesDeleteConfirmTitle -> "Удалить этот гриб?"
    StringKey.SpeciesDeleteConfirmMessage ->
        "Вы уверены, что хотите удалить этот вид? Все отметки этого вида в прогулках будут " +
            "перенесены в категорию «Неизвестный гриб». Это действие необратимо."
    StringKey.SpeciesDeleteConfirmYes -> "Да"
    StringKey.SpeciesDeleteConfirmNo -> "Нет"

    StringKey.IconEditorTitle -> "Редактор фото"
    StringKey.IconEditorToolEraser -> "Ластик"
    StringKey.IconEditorToolCrop -> "Обрезка"
    StringKey.IconEditorShapeRectangle -> "Прямоугольник"
    StringKey.IconEditorShapeOval -> "Овал"
    StringKey.IconEditorBrushSizeLabel -> "Размер кисти"
    StringKey.IconEditorUndoContentDescription -> "Отменить"
    StringKey.IconEditorRedoContentDescription -> "Вернуть"
    StringKey.IconEditorDoneContentDescription -> "Готово"

    StringKey.OnboardingTitle -> "Добро пожаловать!"
    StringKey.OnboardingDescription ->
        "Выберите подборки грибов, которые вам интересны. Это можно изменить позже в Настройках."
    StringKey.OnboardingContinueButton -> "Начать"

    StringKey.NavMenuContentDescription -> "Меню"
    StringKey.HelpContentDescription -> "Помощь"
    StringKey.HelpDialogTitle -> "Помощь"
    StringKey.HelpDialogMessage -> "Инструкции для этого экрана появятся здесь в будущем обновлении."
    StringKey.HelpDialogDismiss -> "Понятно"

    StringKey.CategoryBoletusEdulis -> "Белый гриб"
    StringKey.CategoryLeccinumAurantiacum -> "По​до​си​но​вик"
    StringKey.CategoryLeccinumScabrum -> "Под​бе​рё​зо​вик"
    StringKey.CategoryCantharellusCibarius -> "Ли​сич​ка"
    StringKey.CategoryLactariusDeliciosus -> "Рыжик"
    StringKey.CategorySuillusLuteus -> "Мас​лё​нок"
    StringKey.CategoryArmillariaMellea -> "Опёнок"
    StringKey.CategoryMacrolepiotaProcera -> "Гриб-зонтик"
    StringKey.CategoryCraterellusTubaeformis -> "Труб​ча​тая ли​сич​ка"
    StringKey.CategoryImleriaBadia -> "Поль​ский гриб"
    StringKey.CategoryLactariusResimus -> "Груздь на​сто​ящий"
    StringKey.CategoryLactariusTorminosus -> "Вол​нуш​ка"
    StringKey.CategoryRussulaSpecies -> "Сы​ро​еж​ка"
    StringKey.CategoryPleurotusOstreatus -> "Ве​шен​ка"
    StringKey.CategoryAgaricusSpecies -> "Шам​пинь​он"
    StringKey.CategoryMorchellaSpecies -> "Смор​чок"
    StringKey.CategoryLycoperdonCalvatiaSpecies -> "Дож​де​вик"
    StringKey.CategoryCoprinusComatus -> "На​воз​ник белый"
    StringKey.CategoryHygrophoropsisAurantiaca -> "Ложная ли​сич​ка"
    StringKey.CategoryPaxillusInvolutus -> "Сви​нуш​ка тонкая"
    StringKey.CategoryAmanitaPhalloides -> "Блед​ная по​ган​ка"
    StringKey.CategoryAmanitaVirosa -> "Белая по​ган​ка"
    StringKey.CategoryGalerinaMarginata -> "Га​ле​ри​на окайм​лён​ная"
    StringKey.CategoryAmanitaMuscaria -> "Му​хо​мор крас​ный"
    StringKey.CategoryAmanitaPantherina -> "Му​хо​мор пан​тер​ный"
    StringKey.CategoryGyromitraSpecies -> "Стро​чок"
    StringKey.CategoryXerocomusSubtomentosusGroup -> "Мо​хо​вик зе​лё​ный"
    StringKey.CategorySuillusBovinus -> "Козляк"
    StringKey.CategoryAmanitaVaginata -> "По​пла​вок серый"
    StringKey.CategoryRussulaFoetens -> "Валуй"
    StringKey.CategoryMisc -> "Разное"
    StringKey.CategoryUnknownMushroom -> "Не​из​вест​ный гриб"

    StringKey.CollectionDemoNorth -> "Демо: Север"
    StringKey.CollectionDemoSouth -> "Демо: Юг"
    StringKey.CollectionDemoEast -> "Демо: Восток"

    StringKey.DefaultWalkName -> "Прогулка"
    StringKey.RecordWalkNameHint -> "Название прогулки"
    StringKey.RecordStart -> "Старт"
    StringKey.RecordPause -> "Пауза"
    StringKey.RecordResume -> "Продолжить"
    StringKey.RecordFinish -> "Завершить"
    StringKey.RecordSetWalkNameTitle -> "Задайте название прогулки:"
    StringKey.RecordDefaultWalkNamePrefix -> "Прогулка от"
    StringKey.RecordConfirmWalkNameContentDescription -> "Принять"
    StringKey.RecordMarkLocationContentDescription -> "Отметить место"
    StringKey.RecordSearchContentDescription -> "Поиск"
    StringKey.RecordSearchDialogTitle -> "Выберите необходимый гриб"
    StringKey.RecordBulkAddQuestion -> "Сколько новых грибов найдено?"
    StringKey.RecordBulkAddCancelContentDescription -> "Отмена"
    StringKey.RecordBulkAddLimitMessage -> "Максимум одинаковых грибов за одну прогулку — 999."
    StringKey.RecordBulkAddLimitConfirm -> "Понятно"

    StringKey.NavigationDirectionToPrefix -> "Направление к"
    StringKey.NavigationDistanceToTargetPrefix -> "до цели"
    StringKey.NavigationMetersSuffix -> "метров"
    StringKey.NavigationKeepRightPhrase -> "держитесь правее на"
    StringKey.NavigationKeepLeftPhrase -> "держитесь левее на"
    StringKey.NavigationGoStraightPhrase -> "идите прямо"
    StringKey.NavigationDeterminingDirection -> "Определяем направление…"
    StringKey.NavigationArrivedPhrase -> "Вы на месте"
    StringKey.NavigationCloseContentDescription -> "Закрыть"

    StringKey.AddPlaceTitle -> "Добавьте место"
    StringKey.AddPlaceEditTitle -> "Отредактируйте место"
    StringKey.AddPlaceDefaultName -> "Место"
    StringKey.AddPlaceNameHint -> "Название места"
    StringKey.AddPlacePhotoContentDescription -> "Сфотографировать"
    StringKey.AddPlaceDescriptionTitle -> "Описание"
    StringKey.AddPlaceDescriptionHint -> "Опишите место"
    StringKey.AddPlaceCoordinatesTitle -> "Координаты"
    StringKey.AddPlaceCopyCoordinatesContentDescription -> "Скопировать координаты"
    StringKey.AddPlaceSaveContentDescription -> "Сохранить место"
    StringKey.AddPlaceDiscardContentDescription -> "Удалить место"

    StringKey.PlaceViewEditContentDescription -> "Редактировать место"
    StringKey.PlaceViewDeleteContentDescription -> "Удалить место"
    StringKey.PlaceDeleteConfirmTitle -> "Удалить место?"
    StringKey.PlaceDeleteConfirmMessage -> "Место будет удалено безвозвратно. Восстановить его будет невозможно."
    StringKey.PlaceDeleteConfirmYes -> "Да"
    StringKey.PlaceDeleteConfirmNo -> "Нет"

    StringKey.ArchiveEmpty -> "Прогулок пока нет"
    StringKey.ArchiveDeleteWalksButton -> "Удалить прогулки"
    StringKey.ArchiveDeleteConfirmMessage ->
        "Вы уверены, что хотели бы безвозвратно удалить выделенные прогулки?"
    StringKey.ArchiveDeleteConfirmYes -> "Да"
    StringKey.ArchiveDeleteConfirmNo -> "Нет"
    StringKey.WalkDetailStartTime -> "Старт"
    StringKey.WalkDetailEndTime -> "Финиш"
    StringKey.WalkDetailInProgress -> "не завершена"
    StringKey.WalkDetailDistance -> "Километраж"
    StringKey.WalkDetailDuration -> "Продолжительность"
    StringKey.WalkDetailAvgSpeed -> "Средняя скорость"
    StringKey.WalkDetailDurationDays -> "д"
    StringKey.WalkDetailDurationHours -> "ч"
    StringKey.WalkDetailDurationMinutes -> "мин"
    StringKey.WalkCardDurationHours -> "ч"
    StringKey.WalkCardDurationMinutes -> "мин"
    StringKey.UnitKilometers -> "км"
    StringKey.UnitMegabytes -> "МБ"
    StringKey.WalkDetailFindsTitle -> "Находки по типам"
    StringKey.WalkDetailFindsEmpty -> "Находок не зафиксировано"
    StringKey.WalkDetailPlacesTitle -> "Отмеченные места"
    StringKey.WalkDetailViewMap -> "Смотреть карту"
    StringKey.WalkDetailEditContentDescription -> "Редактировать название прогулки"
    StringKey.WalkDetailEditWalkNameTitle -> "Измените название прогулки:"
    StringKey.WalkDetailConfirmEditWalkNameContentDescription -> "Принять"
    StringKey.WalkDetailDeleteContentDescription -> "Удалить прогулку"
    StringKey.WalkDetailDeleteConfirmTitle -> "Удалить прогулку?"
    StringKey.WalkDetailDeleteConfirmMessage ->
        "Прогулка и все находки будут удалены безвозвратно. Восстановить их будет невозможно."
    StringKey.WalkDetailDeleteConfirmYes -> "Да"
    StringKey.WalkDetailDeleteConfirmNo -> "Нет"
    StringKey.WalkDetailMushroomsCountOne -> "гриб"
    StringKey.WalkDetailMushroomsCountFew -> "гриба"
    StringKey.WalkDetailMushroomsCountMany -> "грибов"

    StringKey.MapToggleMap -> "Карта"
    StringKey.MapToggleStats -> "Статистика"
    StringKey.MapStatsWalksCount -> "Прогулок"
    StringKey.MapStatsFindsCount -> "Найдено грибов"

    StringKey.MapFilterButtonLabel -> "Фильтры"
    StringKey.MapFilterDialogTitle -> "Настройте фильтры, применяемые к грибам на карте:"
    StringKey.MapFilterBackContentDescription -> "Назад"
    StringKey.MapFilterDateRangeTitle -> "Диапазон дат"
    StringKey.MapFilterMonthRangeTitle -> "Сезон"

    StringKey.MonthJanuary -> "Январь"
    StringKey.MonthFebruary -> "Февраль"
    StringKey.MonthMarch -> "Март"
    StringKey.MonthApril -> "Апрель"
    StringKey.MonthMay -> "Май"
    StringKey.MonthJune -> "Июнь"
    StringKey.MonthJuly -> "Июль"
    StringKey.MonthAugust -> "Август"
    StringKey.MonthSeptember -> "Сентябрь"
    StringKey.MonthOctober -> "Октябрь"
    StringKey.MonthNovember -> "Ноябрь"
    StringKey.MonthDecember -> "Декабрь"

    StringKey.BackgroundRecordingChannelName -> "Запись прогулки"
    StringKey.BackgroundRecordingNotificationTitle -> "Идёт запись прогулки"
    StringKey.BackgroundRecordingNotificationText -> "Трек записывается в фоне. Нажмите, чтобы вернуться в приложение."

    StringKey.DataExportOption -> "Экспорт"
    StringKey.DataImportOption -> "Импорт"
    StringKey.DataArchiveNameLabel -> "Название архива"
    StringKey.DataChooseFileButton -> "Выбрать файл"
    StringKey.DataFileStatusLabel -> "Файл для импорта"
    StringKey.DataFileNotSelected -> "не выбран"
    StringKey.DataImportLabelFieldLabel -> "Приписка к названиям прогулок"
    StringKey.DataDoneButton -> "Готово"
    StringKey.DataSavedButton -> "Сохранено"
    StringKey.DataGoToArchiveButton -> "В архив"
    StringKey.DataCancelButton -> "Отмена"
    StringKey.DataProcessingLabel -> "Идёт обработка…"
    StringKey.DataExportSuccessMessage -> "Архив успешно сохранён"
    StringKey.DataImportedWalksLabel -> "Импортировано прогулок"
    StringKey.DataImportFailedWalksLabel -> "Не удалось импортировать"
    StringKey.DataErrorLabel -> "Ошибка"
    StringKey.DataChooseWalksTitle -> "Прогулки для архива"
    StringKey.DataWalksBackContentDescription -> "Назад без сохранения выбора"
    StringKey.DataWalksConfirmContentDescription -> "Подтвердить выбор"
    StringKey.DataWalksSelectedLabel -> "Выбрано"
    StringKey.DataWalksCountOne -> "прогулка"
    StringKey.DataWalksCountFew -> "прогулки"
    StringKey.DataWalksCountMany -> "прогулок"
    StringKey.PreparationSelectAreaButton -> "Выбрать область для скачивания"
    StringKey.PreparationDownloadThisAreaButton -> "Скачать эту область"
    StringKey.PreparationRegionNameDialogTitle -> "Название области"
    StringKey.PreparationRegionNameLabel -> "Например: Лес у деревни"
    StringKey.PreparationSaveButton -> "Скачать"
    StringKey.PreparationCancelButton -> "Отмена"
    StringKey.PreparationDeleteConfirmTitle -> "Удалить область?"
    StringKey.PreparationDeleteConfirmMessage -> "Скачанные тайлы карты будут удалены безвозвратно."
    StringKey.PreparationDeleteConfirmYes -> "Да"
    StringKey.PreparationDeleteConfirmNo -> "Нет"
    StringKey.PreparationDeleteContentDescription -> "Удалить область"
    StringKey.PreparationPauseContentDescription -> "Приостановить скачивание"
    StringKey.PreparationResumeContentDescription -> "Продолжить скачивание"
    StringKey.PreparationStatusDownloading -> "Скачивается"
    StringKey.PreparationStatusPaused -> "На паузе"
    StringKey.PreparationStatusComplete -> "Скачано"
    StringKey.PreparationStatusError -> "Ошибка"
    StringKey.PreparationSubtitle -> "Скачайте карту, чтобы пользоваться ей без интернета в лесу"
    StringKey.PreparationEstimatedSizeLabel -> "Место на диске"
    StringKey.PreparationRetryContentDescription -> "Повторить скачивание"

    StringKey.MapTilesLoadFailed -> "Карта не полностью загрузилась с сайта"
    StringKey.MapTilesLoadFailedDismissContentDescription -> "Закрыть уведомление"

    StringKey.SettingsMapDataTitle -> "Данные карты"
    StringKey.SettingsRefreshMapDataButton -> "Обновить данные карты"
    StringKey.SettingsMapDataUpdateConfirmTitle -> "Обновить данные карты?"
    StringKey.SettingsMapDataUpdateConfirmMessage ->
        "Если содержимое карты изменится, все скачанные офлайн-области будут загружены заново. " +
            "Вы уверены, что хотите обновить данные карты?"
    StringKey.SettingsMapDataUpdateConfirmYes -> "Да"
    StringKey.SettingsMapDataUpdateConfirmNo -> "Нет"
    StringKey.SettingsMapDataRefreshError -> "Не удалось обновить — проверьте подключение к интернету"
    StringKey.SettingsMapDataRedownloadingPrefix -> "Данные карты обновлены. Перекачивается"
    StringKey.SettingsMapDataRedownloadingSuffix -> "— прогресс можно посмотреть в разделе «Подготовка»."
    StringKey.SettingsMapDataRegionsCountOne -> "область"
    StringKey.SettingsMapDataRegionsCountFew -> "области"
    StringKey.SettingsMapDataRegionsCountMany -> "областей"
    StringKey.SettingsClearMapCacheButton -> "Очистить кэш карты"
    StringKey.SettingsClearMapCacheConfirmTitle -> "Очистить кэш карты?"
    StringKey.SettingsClearMapCacheConfirmMessage ->
        "При очистке кэша карты будут удалены просмотренные участки карты, которые не были " +
            "сохранены в разделе «Подготовка». Вы уверены, что хотите очистить кэш?"
    StringKey.SettingsClearMapCacheConfirmYes -> "Да"
    StringKey.SettingsClearMapCacheConfirmNo -> "Нет"
    StringKey.SettingsMapCacheCleared -> "Кэш очищен"
}

private fun englishStrings(key: StringKey): String = when (key) {
    StringKey.AppName -> "Mushroom Map from Leshy"
    StringKey.NavRecord -> "New Entry"
    StringKey.NavArchive -> "Walk Archive"
    StringKey.NavData -> "Export/Import"
    StringKey.NavMap -> "Finds Map"
    StringKey.NavPreparation -> "Preload"
    StringKey.NavSpecies -> "My Mushrooms"
    StringKey.SettingsTitle -> "Settings"
    StringKey.SettingsContentDescription -> "Settings"
    StringKey.SettingsLanguageTitle -> "Language"
    StringKey.SettingsCategoriesTitle -> "Mushrooms to track"
    StringKey.SettingsMushroomSizeTitle -> "Adjust mushroom size on the map"
    StringKey.SettingsMushroomSortTitle -> "Mushroom order"
    StringKey.SettingsMushroomSortByAlphabetical -> "Alphabetically"
    StringKey.SettingsMushroomSortByPoisonousLast -> "Poisonous last"
    StringKey.SettingsResetMushroomOrderOnWalkFinish -> "Reset mushroom order at the end of a walk"
    StringKey.SettingsFreezeMushroomOrder -> "Freeze mushroom order"

    StringKey.SpeciesCollectionsTitle -> "Mushroom collections"
    StringKey.SpeciesMyMushroomsTitle -> "Added mushrooms"
    StringKey.SpeciesMyMushroomsEmpty -> "Mushrooms you add yourself will show up here"
    StringKey.SpeciesAddButton -> "Add mushroom"
    StringKey.SpeciesFormTitleCreate -> "New mushroom"
    StringKey.SpeciesFormTitleEdit -> "Edit mushroom"
    StringKey.SpeciesFormNameHint -> "Name"
    StringKey.SpeciesFormScientificNameHint -> "Scientific name"
    StringKey.SpeciesFormEdibilityLabel -> "Toxicity"
    StringKey.SpeciesFormEdibilityPoisonous -> "Poisonous"
    StringKey.SpeciesFormEdibilityNotSpecified -> "Not specified"
    StringKey.SpeciesFormColorLabel -> "Color"
    StringKey.SpeciesFormTakePhotoButton -> "Camera"
    StringKey.SpeciesFormPickPhotoButton -> "Gallery"
    StringKey.SpeciesFormSaveButton -> "Save"
    StringKey.SpeciesFormCancelContentDescription -> "Cancel"
    StringKey.SpeciesListImportedLabel -> "from archive"
    StringKey.SpeciesListEditContentDescription -> "Edit"
    StringKey.SpeciesListDeleteContentDescription -> "Delete species"
    StringKey.SpeciesDeleteConfirmTitle -> "Delete this mushroom?"
    StringKey.SpeciesDeleteConfirmMessage ->
        "Are you sure you want to delete this species? Every find logged under it will be moved " +
            "to \"Unknown mushroom\". This cannot be undone."
    StringKey.SpeciesDeleteConfirmYes -> "Yes"
    StringKey.SpeciesDeleteConfirmNo -> "No"

    StringKey.IconEditorTitle -> "Photo editor"
    StringKey.IconEditorToolEraser -> "Eraser"
    StringKey.IconEditorToolCrop -> "Crop"
    StringKey.IconEditorShapeRectangle -> "Rectangle"
    StringKey.IconEditorShapeOval -> "Oval"
    StringKey.IconEditorBrushSizeLabel -> "Brush size"
    StringKey.IconEditorUndoContentDescription -> "Undo"
    StringKey.IconEditorRedoContentDescription -> "Redo"
    StringKey.IconEditorDoneContentDescription -> "Done"

    StringKey.OnboardingTitle -> "Welcome!"
    StringKey.OnboardingDescription ->
        "Pick the mushroom collections you're interested in. You can change this later in Settings."
    StringKey.OnboardingContinueButton -> "Get started"

    StringKey.NavMenuContentDescription -> "Menu"
    StringKey.HelpContentDescription -> "Help"
    StringKey.HelpDialogTitle -> "Help"
    StringKey.HelpDialogMessage -> "Instructions for this screen will be available here in a future update."
    StringKey.HelpDialogDismiss -> "Got it"

    StringKey.CategoryBoletusEdulis -> "Porcini"
    StringKey.CategoryLeccinumAurantiacum -> "Orange-cap bolete"
    StringKey.CategoryLeccinumScabrum -> "Birch bolete"
    StringKey.CategoryCantharellusCibarius -> "Golden chan​te​relle"
    StringKey.CategoryLactariusDeliciosus -> "Saffron milk cap"
    StringKey.CategorySuillusLuteus -> "Slip​per​y jack"
    StringKey.CategoryArmillariaMellea -> "Honey fungus"
    StringKey.CategoryMacrolepiotaProcera -> "Parasol mush​room"
    StringKey.CategoryCraterellusTubaeformis -> "Funnel chan​te​relle"
    StringKey.CategoryImleriaBadia -> "Bay bolete"
    StringKey.CategoryLactariusResimus -> "True milk mush​room"
    StringKey.CategoryLactariusTorminosus -> "Woolly milk​cap"
    StringKey.CategoryRussulaSpecies -> "Russula"
    StringKey.CategoryPleurotusOstreatus -> "Oyster mush​room"
    StringKey.CategoryAgaricusSpecies -> "Field mush​room"
    StringKey.CategoryMorchellaSpecies -> "Morel"
    StringKey.CategoryLycoperdonCalvatiaSpecies -> "Puff​ball"
    StringKey.CategoryCoprinusComatus -> "Shaggy ink cap"
    StringKey.CategoryHygrophoropsisAurantiaca -> "False chan​te​relle"
    StringKey.CategoryPaxillusInvolutus -> "Brown roll-rim"
    StringKey.CategoryAmanitaPhalloides -> "Death cap"
    StringKey.CategoryAmanitaVirosa -> "De​stroy​ing angel"
    StringKey.CategoryGalerinaMarginata -> "Funeral bell"
    StringKey.CategoryAmanitaMuscaria -> "Fly agaric"
    StringKey.CategoryAmanitaPantherina -> "Panther cap"
    StringKey.CategoryGyromitraSpecies -> "False morel"
    StringKey.CategoryXerocomusSubtomentosusGroup -> "Green moss bolete"
    StringKey.CategorySuillusBovinus -> "Bovine bolete"
    StringKey.CategoryAmanitaVaginata -> "Gri​sette"
    StringKey.CategoryRussulaFoetens -> "Valui"
    StringKey.CategoryMisc -> "Misc"
    StringKey.CategoryUnknownMushroom -> "Unknown mush​room"

    StringKey.CollectionDemoNorth -> "Demo: North"
    StringKey.CollectionDemoSouth -> "Demo: South"
    StringKey.CollectionDemoEast -> "Demo: East"

    StringKey.DefaultWalkName -> "Walk"
    StringKey.RecordWalkNameHint -> "Walk name"
    StringKey.RecordStart -> "Start"
    StringKey.RecordPause -> "Pause"
    StringKey.RecordResume -> "Resume"
    StringKey.RecordFinish -> "Finish"
    StringKey.RecordSetWalkNameTitle -> "Set the walk name:"
    StringKey.RecordDefaultWalkNamePrefix -> "Walk on"
    StringKey.RecordConfirmWalkNameContentDescription -> "Confirm"
    StringKey.RecordMarkLocationContentDescription -> "Mark location"
    StringKey.RecordSearchContentDescription -> "Search"
    StringKey.RecordSearchDialogTitle -> "Choose the mushroom you need"
    StringKey.RecordBulkAddQuestion -> "How many new mushrooms found?"
    StringKey.RecordBulkAddCancelContentDescription -> "Cancel"
    StringKey.RecordBulkAddLimitMessage -> "Maximum of 999 finds of the same species per walk."
    StringKey.RecordBulkAddLimitConfirm -> "Got it"

    StringKey.NavigationDirectionToPrefix -> "Direction to"
    StringKey.NavigationDistanceToTargetPrefix -> "to target"
    StringKey.NavigationMetersSuffix -> "meters"
    StringKey.NavigationKeepRightPhrase -> "keep right by"
    StringKey.NavigationKeepLeftPhrase -> "keep left by"
    StringKey.NavigationGoStraightPhrase -> "go straight"
    StringKey.NavigationDeterminingDirection -> "Determining direction…"
    StringKey.NavigationArrivedPhrase -> "You have arrived"
    StringKey.NavigationCloseContentDescription -> "Close"

    StringKey.AddPlaceTitle -> "Add a place"
    StringKey.AddPlaceEditTitle -> "Edit place"
    StringKey.AddPlaceDefaultName -> "Place"
    StringKey.AddPlaceNameHint -> "Place name"
    StringKey.AddPlacePhotoContentDescription -> "Take photo"
    StringKey.AddPlaceDescriptionTitle -> "Description"
    StringKey.AddPlaceDescriptionHint -> "Describe the place"
    StringKey.AddPlaceCoordinatesTitle -> "Coordinates"
    StringKey.AddPlaceCopyCoordinatesContentDescription -> "Copy coordinates"
    StringKey.AddPlaceSaveContentDescription -> "Save place"
    StringKey.AddPlaceDiscardContentDescription -> "Discard place"

    StringKey.PlaceViewEditContentDescription -> "Edit place"
    StringKey.PlaceViewDeleteContentDescription -> "Delete place"
    StringKey.PlaceDeleteConfirmTitle -> "Delete place?"
    StringKey.PlaceDeleteConfirmMessage -> "The place will be permanently deleted. This cannot be undone."
    StringKey.PlaceDeleteConfirmYes -> "Yes"
    StringKey.PlaceDeleteConfirmNo -> "No"

    StringKey.ArchiveEmpty -> "No walks recorded yet"
    StringKey.ArchiveDeleteWalksButton -> "Delete walks"
    StringKey.ArchiveDeleteConfirmMessage -> "Are you sure you want to permanently delete the selected walks?"
    StringKey.ArchiveDeleteConfirmYes -> "Yes"
    StringKey.ArchiveDeleteConfirmNo -> "No"
    StringKey.WalkDetailStartTime -> "Started"
    StringKey.WalkDetailEndTime -> "Finished"
    StringKey.WalkDetailInProgress -> "in progress"
    StringKey.WalkDetailDistance -> "Distance"
    StringKey.WalkDetailDuration -> "Duration"
    StringKey.WalkDetailAvgSpeed -> "Average speed"
    StringKey.WalkDetailDurationDays -> "d"
    StringKey.WalkDetailDurationHours -> "h"
    StringKey.WalkDetailDurationMinutes -> "min"
    StringKey.WalkCardDurationHours -> "h"
    StringKey.WalkCardDurationMinutes -> "m"
    StringKey.UnitKilometers -> "km"
    StringKey.UnitMegabytes -> "MB"
    StringKey.WalkDetailFindsTitle -> "Finds by type"
    StringKey.WalkDetailFindsEmpty -> "No finds recorded"
    StringKey.WalkDetailPlacesTitle -> "Marked places"
    StringKey.WalkDetailViewMap -> "View map"
    StringKey.WalkDetailEditContentDescription -> "Edit walk name"
    StringKey.WalkDetailEditWalkNameTitle -> "Edit the walk name:"
    StringKey.WalkDetailConfirmEditWalkNameContentDescription -> "Confirm"
    StringKey.WalkDetailDeleteContentDescription -> "Delete walk"
    StringKey.WalkDetailDeleteConfirmTitle -> "Delete walk?"
    StringKey.WalkDetailDeleteConfirmMessage ->
        "The walk and all its finds will be permanently deleted. This cannot be undone."
    StringKey.WalkDetailDeleteConfirmYes -> "Yes"
    StringKey.WalkDetailDeleteConfirmNo -> "No"
    StringKey.WalkDetailMushroomsCountOne -> "mushroom"
    StringKey.WalkDetailMushroomsCountFew -> "mushrooms"
    StringKey.WalkDetailMushroomsCountMany -> "mushrooms"

    StringKey.MapToggleMap -> "Map"
    StringKey.MapToggleStats -> "Statistics"
    StringKey.MapStatsWalksCount -> "Walks"
    StringKey.MapStatsFindsCount -> "Mushrooms found"

    StringKey.MapFilterButtonLabel -> "Filters"
    StringKey.MapFilterDialogTitle -> "Configure the filters applied to mushrooms on the map:"
    StringKey.MapFilterBackContentDescription -> "Back"
    StringKey.MapFilterDateRangeTitle -> "Date range"
    StringKey.MapFilterMonthRangeTitle -> "Season"

    StringKey.MonthJanuary -> "January"
    StringKey.MonthFebruary -> "February"
    StringKey.MonthMarch -> "March"
    StringKey.MonthApril -> "April"
    StringKey.MonthMay -> "May"
    StringKey.MonthJune -> "June"
    StringKey.MonthJuly -> "July"
    StringKey.MonthAugust -> "August"
    StringKey.MonthSeptember -> "September"
    StringKey.MonthOctober -> "October"
    StringKey.MonthNovember -> "November"
    StringKey.MonthDecember -> "December"

    StringKey.BackgroundRecordingChannelName -> "Walk recording"
    StringKey.BackgroundRecordingNotificationTitle -> "Recording your walk"
    StringKey.BackgroundRecordingNotificationText -> "Track is being recorded in the background. Tap to return to the app."

    StringKey.DataExportOption -> "Export"
    StringKey.DataImportOption -> "Import"
    StringKey.DataArchiveNameLabel -> "Archive name"
    StringKey.DataChooseFileButton -> "Choose file"
    StringKey.DataFileStatusLabel -> "Import file"
    StringKey.DataFileNotSelected -> "not selected"
    StringKey.DataImportLabelFieldLabel -> "Tag added to imported walk names"
    StringKey.DataDoneButton -> "Done"
    StringKey.DataSavedButton -> "Saved"
    StringKey.DataGoToArchiveButton -> "To Archive"
    StringKey.DataCancelButton -> "Cancel"
    StringKey.DataProcessingLabel -> "Processing…"
    StringKey.DataExportSuccessMessage -> "Archive saved successfully"
    StringKey.DataImportedWalksLabel -> "Walks imported"
    StringKey.DataImportFailedWalksLabel -> "Failed to import"
    StringKey.DataErrorLabel -> "Error"
    StringKey.DataChooseWalksTitle -> "Walks to export"
    StringKey.DataWalksBackContentDescription -> "Back without saving the selection"
    StringKey.DataWalksConfirmContentDescription -> "Confirm selection"
    StringKey.DataWalksSelectedLabel -> "Selected"
    StringKey.DataWalksCountOne -> "walk"
    StringKey.DataWalksCountFew -> "walks"
    StringKey.DataWalksCountMany -> "walks"
    StringKey.PreparationSelectAreaButton -> "Select an area to download"
    StringKey.PreparationDownloadThisAreaButton -> "Download this area"
    StringKey.PreparationRegionNameDialogTitle -> "Area name"
    StringKey.PreparationRegionNameLabel -> "E.g. Forest near the village"
    StringKey.PreparationSaveButton -> "Download"
    StringKey.PreparationCancelButton -> "Cancel"
    StringKey.PreparationDeleteConfirmTitle -> "Delete area?"
    StringKey.PreparationDeleteConfirmMessage -> "The downloaded map tiles will be permanently deleted."
    StringKey.PreparationDeleteConfirmYes -> "Yes"
    StringKey.PreparationDeleteConfirmNo -> "No"
    StringKey.PreparationDeleteContentDescription -> "Delete area"
    StringKey.PreparationPauseContentDescription -> "Pause download"
    StringKey.PreparationResumeContentDescription -> "Resume download"
    StringKey.PreparationStatusDownloading -> "Downloading"
    StringKey.PreparationStatusPaused -> "Paused"
    StringKey.PreparationStatusComplete -> "Downloaded"
    StringKey.PreparationStatusError -> "Error"
    StringKey.PreparationSubtitle -> "Download the map to use it offline in the forest"
    StringKey.PreparationEstimatedSizeLabel -> "Storage"
    StringKey.PreparationRetryContentDescription -> "Retry download"

    StringKey.MapTilesLoadFailed -> "The map didn't fully load from"
    StringKey.MapTilesLoadFailedDismissContentDescription -> "Dismiss notice"

    StringKey.SettingsMapDataTitle -> "Map data"
    StringKey.SettingsRefreshMapDataButton -> "Update map data"
    StringKey.SettingsMapDataUpdateConfirmTitle -> "Update map data?"
    StringKey.SettingsMapDataUpdateConfirmMessage ->
        "If the map content has changed, all downloaded offline areas will be re-downloaded. " +
            "Are you sure you want to update the map data?"
    StringKey.SettingsMapDataUpdateConfirmYes -> "Yes"
    StringKey.SettingsMapDataUpdateConfirmNo -> "No"
    StringKey.SettingsMapDataRefreshError -> "Update failed — check your internet connection"
    StringKey.SettingsMapDataRedownloadingPrefix -> "Map data updated. Re-downloading"
    StringKey.SettingsMapDataRedownloadingSuffix -> "— check progress in the Preparation section."
    StringKey.SettingsMapDataRegionsCountOne -> "area"
    StringKey.SettingsMapDataRegionsCountFew -> "areas"
    StringKey.SettingsMapDataRegionsCountMany -> "areas"
    StringKey.SettingsClearMapCacheButton -> "Clear map cache"
    StringKey.SettingsClearMapCacheConfirmTitle -> "Clear map cache?"
    StringKey.SettingsClearMapCacheConfirmMessage ->
        "Clearing the map cache removes browsed map areas that weren't saved in the Preparation " +
            "section. Are you sure you want to clear the cache?"
    StringKey.SettingsClearMapCacheConfirmYes -> "Yes"
    StringKey.SettingsClearMapCacheConfirmNo -> "No"
    StringKey.SettingsMapCacheCleared -> "Cache cleared"
}
