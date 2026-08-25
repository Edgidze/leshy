package compose.project.leshy.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.i18n.strings.frenchStrings
import compose.project.leshy.i18n.strings.germanStrings
import compose.project.leshy.i18n.strings.italianStrings
import compose.project.leshy.i18n.strings.spanishStrings

val LocalAppLanguage = compositionLocalOf { AppLanguage.EN }

@Composable
fun stringResource(key: StringKey): String = string(key, LocalAppLanguage.current)

/**
 * `ru`/`en` stay exhaustive `when` branches on [StringKey] — the compiler catches a forgotten
 * translation the moment a new key is added, which is the whole point of the enum
 * (`i18n/CLAUDE.md`). The other 24 languages go through [uiTranslations] instead: a generated
 * per-language `Map<StringKey, String>` (`i18n/strings/Strings<Xx>.kt`, Phases 6–11 of
 * `.claude/plans/countries-and-languages.md`) checked for completeness by a `commonTest`, not the
 * compiler — a missing key there degrades to English rather than failing the build, the right
 * tradeoff for 24 languages translated in batches over many sessions. [uiTranslations] is empty as
 * of Phase 4, so every non-ru/en language currently reads as English end to end.
 */
fun string(key: StringKey, language: AppLanguage): String = when (language) {
    AppLanguage.RU -> russianStrings(key)
    AppLanguage.EN -> englishStrings(key)
    else -> uiTranslations[language]?.get(key) ?: englishStrings(key)
}

/** Per-language translation tables for every [AppLanguage] beyond `ru`/`en` — see [string]'s doc.
 * Populated one language at a time in Phases 6–11 of `.claude/plans/countries-and-languages.md`;
 * `internal` rather than `private` so `StringsTest` (`commonTest`) can assert completeness once
 * entries land. Phase 6 fills `de`/`fr`/`es`/`it` (`i18n/strings/Strings<Xx>.kt`); the rest still
 * fall back to English until their own phase lands. */
internal val uiTranslations: Map<AppLanguage, Map<StringKey, String>> = mapOf(
    AppLanguage.DE to germanStrings,
    AppLanguage.FR to frenchStrings,
    AppLanguage.ES to spanishStrings,
    AppLanguage.IT to italianStrings,
)

/** The countable units this app formats, each with its six per-[PluralCategory] [StringKey]s.
 * A unit's forms are picked by [pluralCategory] for the active language — Phase 5 of
 * `.claude/plans/countries-and-languages.md`; before it, every non-Russian language read the
 * English one/other split. */
internal val mushroomsForms = PluralForms(
    zero = StringKey.WalkDetailMushroomsCountZero,
    one = StringKey.WalkDetailMushroomsCountOne,
    two = StringKey.WalkDetailMushroomsCountTwo,
    few = StringKey.WalkDetailMushroomsCountFew,
    many = StringKey.WalkDetailMushroomsCountMany,
    other = StringKey.WalkDetailMushroomsCountOther,
)

internal val walksForms = PluralForms(
    zero = StringKey.DataWalksCountZero,
    one = StringKey.DataWalksCountOne,
    two = StringKey.DataWalksCountTwo,
    few = StringKey.DataWalksCountFew,
    many = StringKey.DataWalksCountMany,
    other = StringKey.DataWalksCountOther,
)

internal val regionsForms = PluralForms(
    zero = StringKey.SettingsMapDataRegionsCountZero,
    one = StringKey.SettingsMapDataRegionsCountOne,
    two = StringKey.SettingsMapDataRegionsCountTwo,
    few = StringKey.SettingsMapDataRegionsCountFew,
    many = StringKey.SettingsMapDataRegionsCountMany,
    other = StringKey.SettingsMapDataRegionsCountOther,
)

/** The [forms] of a unit agreeing with [count] in the active language, per CLDR — see
 * [pluralCategory]. Not `@Composable`-free by accident: the active language comes from
 * [LocalAppLanguage], same as [stringResource]. */
@Composable
internal fun pluralLabel(forms: PluralForms, count: Int): String =
    stringResource(forms.keyFor(pluralCategory(LocalAppLanguage.current, count)))

/** "гриб"/"гриба"/"грибов" and the equivalent in the other 25 languages, agreeing with [count]. */
@Composable
fun mushroomsUnitLabel(count: Int): String = pluralLabel(mushroomsForms, count)

/** "прогулка"/"прогулки"/"прогулок" and the equivalent in the other 25 languages, agreeing with
 * [count]. */
@Composable
fun walksUnitLabel(count: Int): String = pluralLabel(walksForms, count)

/** "область"/"области"/"областей" and the equivalent in the other 25 languages, agreeing with
 * [count]. */
@Composable
fun regionsUnitLabel(count: Int): String = pluralLabel(regionsForms, count)

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
    StringKey.SettingsLanguageTitle -> "Язык интерфейса"
    StringKey.SettingsCategoriesTitle -> "Грибы для отметки"
    StringKey.SettingsMushroomSizeTitle -> "Настройте размер грибов на карте"
    StringKey.SettingsMushroomSortTitle -> "Порядок грибов"
    StringKey.SettingsResetMushroomOrderOnWalkFinish -> "Сбрасывать порядок грибов в конце прогулки"
    StringKey.SettingsFreezeMushroomOrder -> "Неподвижный порядок грибов"

    StringKey.MushroomImagesDisclaimer ->
        "Все изображения грибов в приложении условны — не используйте их для определения " +
            "незнакомых грибов!"

    StringKey.SpeciesCollectionsTitle -> "Подборки грибов"
    StringKey.SpeciesMyMushroomsTitle -> "Добавленные грибы"
    StringKey.SpeciesMyMushroomsEmpty -> "Здесь появятся грибы, которые вы добавите сами"
    StringKey.SpeciesAddButton -> "Добавить гриб"
    StringKey.SpeciesFormTitleCreate -> "Новый гриб"
    StringKey.SpeciesFormTitleEdit -> "Изменить гриб"
    StringKey.SpeciesFormNameHint -> "Название"
    StringKey.SpeciesFormScientificNameHint -> "Научное название"
    StringKey.SpeciesFormColorLabel -> "Цвет"
    StringKey.SpeciesFormTakePhotoButton -> "Камера"
    StringKey.SpeciesFormPickPhotoButton -> "Галерея"
    StringKey.SpeciesFormPickCatalogButton -> "Картинки"
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

    StringKey.CatalogPhotoPickerTitle -> "Выбрать изображение"

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

    StringKey.CategoryMisc -> "Разное"
    StringKey.CategoryUnknownMushroom -> "Неизвестный гриб"

    StringKey.CollectionPickerSearchHint -> "Поиск страны"

    StringKey.LanguagePickerSearchHint -> "Поиск языка"
    StringKey.LanguagePickerBackContentDescription -> "Назад"
    StringKey.LanguagePickerConfirmContentDescription -> "Подтвердить"

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
    StringKey.UnitKmh -> "км/ч"
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
    // Russian reaches only One/Few/Many for integer counts (`Plurals.kt`); the other three repeat
    // the genitive plural so a rule that ever mis-fires still reads as Russian.
    StringKey.WalkDetailMushroomsCountZero -> "грибов"
    StringKey.WalkDetailMushroomsCountOne -> "гриб"
    StringKey.WalkDetailMushroomsCountTwo -> "гриба"
    StringKey.WalkDetailMushroomsCountFew -> "гриба"
    StringKey.WalkDetailMushroomsCountMany -> "грибов"
    StringKey.WalkDetailMushroomsCountOther -> "грибов"
    StringKey.WalkDetailDescriptionTitle -> "Описание"
    StringKey.WalkDetailDescriptionEmpty -> "Описание не добавлено"
    StringKey.WalkDetailDescriptionHint -> "Опишите прогулку"
    StringKey.WalkDetailEditDescriptionContentDescription -> "Редактировать описание"
    StringKey.WalkDetailDescriptionCancelContentDescription -> "Отмена"
    StringKey.WalkDetailDescriptionSaveContentDescription -> "Сохранить"

    StringKey.WalkShareContentDescription -> "Поделиться прогулкой"
    StringKey.WalkShareDialogTitle -> "Поделиться"
    StringKey.WalkShareOptionName -> "Название прогулки"
    StringKey.WalkShareOptionStats -> "Статистика прогулки"
    StringKey.WalkShareOptionDescription -> "Описание прогулки"
    StringKey.WalkShareOptionDiagram -> "Диаграмма находок"
    StringKey.WalkShareOptionMap -> "Карта с отметками"
    StringKey.WalkShareMapWarning -> "Другим людям будет видно, где вы нашли грибы"
    StringKey.WalkShareCancelButton -> "Отменить"
    StringKey.WalkShareConfirmButton -> "Поделиться"
    StringKey.WalkShareFooter -> "Создано с помощью приложения «Грибная карта от Лешего»"
    StringKey.WalkShareImageFooter -> "Создано в приложении Грибная карта от Лешего"

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
    StringKey.DataWalksCountZero -> "прогулок"
    StringKey.DataWalksCountOne -> "прогулка"
    StringKey.DataWalksCountTwo -> "прогулки"
    StringKey.DataWalksCountFew -> "прогулки"
    StringKey.DataWalksCountMany -> "прогулок"
    StringKey.DataWalksCountOther -> "прогулок"
    StringKey.PreparationSelectAreaButton -> "Скачать видимую область"
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
    StringKey.PreparationSubtitle -> "Скачайте видимую область карты, чтобы пользоваться ей без интернета"
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
    StringKey.SettingsMapDataRegionsCountZero -> "областей"
    StringKey.SettingsMapDataRegionsCountOne -> "область"
    StringKey.SettingsMapDataRegionsCountTwo -> "области"
    StringKey.SettingsMapDataRegionsCountFew -> "области"
    StringKey.SettingsMapDataRegionsCountMany -> "областей"
    StringKey.SettingsMapDataRegionsCountOther -> "областей"
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
    StringKey.SettingsLanguageTitle -> "Interface language"
    StringKey.SettingsCategoriesTitle -> "Mushrooms to track"
    StringKey.SettingsMushroomSizeTitle -> "Adjust mushroom size on the map"
    StringKey.SettingsMushroomSortTitle -> "Mushroom order"
    StringKey.SettingsResetMushroomOrderOnWalkFinish -> "Reset mushroom order at the end of a walk"
    StringKey.SettingsFreezeMushroomOrder -> "Freeze mushroom order"

    StringKey.MushroomImagesDisclaimer ->
        "All mushroom images in the app are illustrative only — do not use them to identify " +
            "unfamiliar mushrooms!"

    StringKey.SpeciesCollectionsTitle -> "Mushroom collections"
    StringKey.SpeciesMyMushroomsTitle -> "Added mushrooms"
    StringKey.SpeciesMyMushroomsEmpty -> "Mushrooms you add yourself will show up here"
    StringKey.SpeciesAddButton -> "Add mushroom"
    StringKey.SpeciesFormTitleCreate -> "New mushroom"
    StringKey.SpeciesFormTitleEdit -> "Edit mushroom"
    StringKey.SpeciesFormNameHint -> "Name"
    StringKey.SpeciesFormScientificNameHint -> "Scientific name"
    StringKey.SpeciesFormColorLabel -> "Color"
    StringKey.SpeciesFormTakePhotoButton -> "Camera"
    StringKey.SpeciesFormPickPhotoButton -> "Gallery"
    StringKey.SpeciesFormPickCatalogButton -> "Pictures"
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

    StringKey.CatalogPhotoPickerTitle -> "Choose a picture"

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

    StringKey.CategoryMisc -> "Misc"
    StringKey.CategoryUnknownMushroom -> "Unknown mushroom"

    StringKey.CollectionPickerSearchHint -> "Search country"

    StringKey.LanguagePickerSearchHint -> "Search language"
    StringKey.LanguagePickerBackContentDescription -> "Back"
    StringKey.LanguagePickerConfirmContentDescription -> "Confirm"

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
    StringKey.UnitKmh -> "km/h"
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
    // English reaches only One/Other (`Plurals.kt`), but every form has to carry a value: these
    // are also what an untranslated language falls back to, form by form, in `string()`.
    StringKey.WalkDetailMushroomsCountZero -> "mushrooms"
    StringKey.WalkDetailMushroomsCountOne -> "mushroom"
    StringKey.WalkDetailMushroomsCountTwo -> "mushrooms"
    StringKey.WalkDetailMushroomsCountFew -> "mushrooms"
    StringKey.WalkDetailMushroomsCountMany -> "mushrooms"
    StringKey.WalkDetailMushroomsCountOther -> "mushrooms"
    StringKey.WalkDetailDescriptionTitle -> "Description"
    StringKey.WalkDetailDescriptionEmpty -> "No description added"
    StringKey.WalkDetailDescriptionHint -> "Describe the walk"
    StringKey.WalkDetailEditDescriptionContentDescription -> "Edit description"
    StringKey.WalkDetailDescriptionCancelContentDescription -> "Cancel"
    StringKey.WalkDetailDescriptionSaveContentDescription -> "Save"

    StringKey.WalkShareContentDescription -> "Share walk"
    StringKey.WalkShareDialogTitle -> "Share"
    StringKey.WalkShareOptionName -> "Walk name"
    StringKey.WalkShareOptionStats -> "Walk statistics"
    StringKey.WalkShareOptionDescription -> "Walk description"
    StringKey.WalkShareOptionDiagram -> "Finds diagram"
    StringKey.WalkShareOptionMap -> "Map with markers"
    StringKey.WalkShareMapWarning -> "Other people will be able to see where you found mushrooms"
    StringKey.WalkShareCancelButton -> "Cancel"
    StringKey.WalkShareConfirmButton -> "Share"
    StringKey.WalkShareFooter -> "Made with the \"Mushroom Map from Leshy\" app"
    StringKey.WalkShareImageFooter -> "Created in the Mushroom Map from Leshy app"

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
    StringKey.DataWalksCountZero -> "walks"
    StringKey.DataWalksCountOne -> "walk"
    StringKey.DataWalksCountTwo -> "walks"
    StringKey.DataWalksCountFew -> "walks"
    StringKey.DataWalksCountMany -> "walks"
    StringKey.DataWalksCountOther -> "walks"
    StringKey.PreparationSelectAreaButton -> "Download visible area"
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
    StringKey.PreparationSubtitle -> "Download the visible map area to use it offline"
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
    StringKey.SettingsMapDataRegionsCountZero -> "areas"
    StringKey.SettingsMapDataRegionsCountOne -> "area"
    StringKey.SettingsMapDataRegionsCountTwo -> "areas"
    StringKey.SettingsMapDataRegionsCountFew -> "areas"
    StringKey.SettingsMapDataRegionsCountMany -> "areas"
    StringKey.SettingsMapDataRegionsCountOther -> "areas"
    StringKey.SettingsClearMapCacheButton -> "Clear map cache"
    StringKey.SettingsClearMapCacheConfirmTitle -> "Clear map cache?"
    StringKey.SettingsClearMapCacheConfirmMessage ->
        "Clearing the map cache removes browsed map areas that weren't saved in the Preparation " +
            "section. Are you sure you want to clear the cache?"
    StringKey.SettingsClearMapCacheConfirmYes -> "Yes"
    StringKey.SettingsClearMapCacheConfirmNo -> "No"
    StringKey.SettingsMapCacheCleared -> "Cache cleared"
}
