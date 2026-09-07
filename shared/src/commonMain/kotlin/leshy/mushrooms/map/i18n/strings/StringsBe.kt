package leshy.mushrooms.map.i18n.strings

import leshy.mushrooms.map.i18n.StringKey

/** Belarusian — Phase 9 of `.claude/plans/countries-and-languages.md`. Plurals follow the East
 * Slavic three-way split (`Plurals.kt`, the same branch Russian and Ukrainian share): `One`
 * (n % 10 = 1, n % 100 != 11), `Few` (n % 10 in 2..4, n % 100 not in 12..14), else `Many`. `Zero`
 * and `Other` never fire as distinct categories — 0 resolves to `Many`, and no integer reaches
 * `Other` at all — so both repeat the genitive plural; `Two` repeats `Few`, which is what 2
 * actually resolves to. Same convention as `russianStrings`/`ukrainianStrings`. */
internal val belarusianStrings: Map<StringKey, String> = mapOf(
    StringKey.AppName to "Грыбная карта ад Лешага",
    StringKey.NavRecord to "Новы запіс",
    StringKey.NavArchive to "Архіў прагулак",
    StringKey.NavMap to "Карта знаходак",
    StringKey.NavData to "Экспарт/Імпарт",
    StringKey.NavPreparation to "Папярэдняя загрузка",
    StringKey.NavSpecies to "Мае грыбы",
    StringKey.SettingsTitle to "Налады",
    StringKey.SettingsContentDescription to "Налады",
    StringKey.SettingsLanguageTitle to "Мова інтэрфейсу",
    StringKey.SettingsThemeTitle to "Афармленне",
    StringKey.SettingsThemeLight to "Светлае",
    StringKey.SettingsThemeDark to "Цёмнае",
    StringKey.SettingsThemeSystem to "Сістэмнае",
    StringKey.SettingsCategoriesTitle to "Грыбы для пазначэння",
    StringKey.SettingsMushroomSizeTitle to "Наладзьце памер грыбоў на карце",
    StringKey.SettingsMushroomSortTitle to "Парадак грыбоў",
    StringKey.SettingsResetMushroomOrderOnWalkFinish to
        "Скідваць парадак грыбоў у канцы прагулкі",
    StringKey.SettingsFreezeMushroomOrder to "Нязменны парадак грыбоў",

    StringKey.MushroomImagesDisclaimer to
        "Усе выявы грыбоў у праграме ўмоўныя — не выкарыстоўвайце іх для вызначэння " +
            "незнаёмых грыбоў!",

    StringKey.SpeciesCollectionsTitle to "Падборкі грыбоў па краінах",
    StringKey.SpeciesMyMushroomsTitle to "Дададзеныя грыбы",
    StringKey.SpeciesMyMushroomsEmpty to "Тут з'явяцца грыбы, якія вы дадасце самі",
    StringKey.SpeciesAddButton to "Дадаць грыб",
    StringKey.SpeciesFormTitleCreate to "Новы грыб",
    StringKey.SpeciesFormTitleEdit to "Змяніць грыб",
    StringKey.SpeciesFormNameHint to "Назва",
    StringKey.SpeciesFormScientificNameHint to "Навуковая назва",
    StringKey.SpeciesFormColorLabel to "Колер",
    StringKey.SpeciesFormTakePhotoButton to "Камера",
    StringKey.SpeciesFormPickPhotoButton to "Галерэя",
    StringKey.SpeciesFormPickCatalogButton to "Малюнкі",
    StringKey.SpeciesFormSaveButton to "Захаваць",
    StringKey.SpeciesFormCancelContentDescription to "Скасаваць",
    StringKey.SpeciesCollectionDialogTitle to "У якую падборку?",
    StringKey.SpeciesCollectionDialogBackContentDescription to "Назад",
    StringKey.SpeciesCollectionDialogSaveContentDescription to "Захаваць у падборку",
    StringKey.SpeciesCollectionNameIsCountry to "Так называецца краіна — выберыце іншую назву",
    StringKey.SpeciesListImportedLabel to "з архіва",
    StringKey.SpeciesListEditContentDescription to "Рэдагаваць",
    StringKey.SpeciesListDeleteContentDescription to "Выдаліць від",
    StringKey.SpeciesDeleteConfirmTitle to "Выдаліць гэты грыб?",
    StringKey.SpeciesDeleteConfirmMessage to
        "Вы ўпэўнены, што хочаце выдаліць гэты від? Усе пазнакі гэтага віду ў прагулках будуць " +
            "перанесены ў катэгорыю «Невядомы грыб». Гэта дзеянне незваротнае.",
    StringKey.SpeciesDeleteConfirmYes to "Так",
    StringKey.SpeciesDeleteConfirmNo to "Не",

    StringKey.CatalogPhotoPickerTitle to "Выбраць выяву",

    StringKey.IconEditorTitle to "Рэдактар фота",
    StringKey.IconEditorToolEraser to "Гумка",
    StringKey.IconEditorToolCrop to "Абрэзка",
    StringKey.IconEditorShapeRectangle to "Прамавугольнік",
    StringKey.IconEditorShapeOval to "Авал",
    StringKey.IconEditorBrushSizeLabel to "Памер пэндзля",
    StringKey.IconEditorUndoContentDescription to "Адмяніць",
    StringKey.IconEditorRedoContentDescription to "Паўтарыць",
    StringKey.IconEditorDoneContentDescription to "Гатова",

    StringKey.OnboardingTitle to "Сардэчна запрашаем!",
    StringKey.OnboardingDescription to
        "Выберыце падборкі грыбоў, якія вам цікавыя. Гэта можна змяніць пазней у Наладах.",
    StringKey.OnboardingContinueButton to "Пачаць",
    StringKey.OnboardingNothingPickedWarning to
        "Выберыце хаця б адну падборку ці адзін грыб, каб працягнуць",

    StringKey.WelcomeIntro to
        "Праграма запамінае, дзе вы прайшлі і што знайшлі, — і прыкметна дапамагае ў зборы грыбоў: да " +
            "добрых месцаў лёгка вярнуцца, а ўсе знаходкі відаць на адной карце.",
    StringKey.WelcomeRecordTitle to "Запісвайце прагулку",
    StringKey.WelcomeRecordText to
        "Трэк, час і кіламетраж праграма вядзе сама. Знайшлі грыб — адзначце яго дотыкам да плашкі; " +
            "крыніцу, паваленае дрэва ці машыну можна адзначыць проста на карце.",
    StringKey.WelcomeArchiveTitle to "Вяртайцеся да знаходак",
    StringKey.WelcomeArchiveText to
        "У архіве кожная прагулка ляжыць асобна — са сваім трэкам і знаходкамі. А агульная карта паказвае " +
            "ўсе прагулкі разам: дзе, чаго і колькі знайшлося за ўсе сезоны.",
    StringKey.WelcomeHelpTitle to "Не ўпэўнены — націсніце «?»",
    StringKey.WelcomeHelpText to
        "Кнопка «?» справа ўверсе ёсць у кожным раздзеле і тлумачыць, як гэты раздзел уладкаваны.",
    StringKey.WelcomeMenuTitle to "Астатняе — у меню",
    StringKey.WelcomeMenuText to
        "Кнопка меню злева ўверсе адкрывае спіс усіх раздзелаў і магчымасцей праграмы.",
    StringKey.WelcomeConsentTitle to "Перад выкарыстаннем",
    StringKey.WelcomeConsentIntro to
        "Перад пераходам да самой праграмы неабходна пагадзіцца з наступнымі сцвярджэннямі:",
    StringKey.WelcomeConsentImages to
        "Вы не будзеце спрабаваць вызначаць грыбы па малюнках з праграмы. Малюнкі выконваюць ролю ілюстрацый і не " +
            "з’яўляюцца выверанным вызначальнікам.",
    StringKey.WelcomeConsentEating to
        "Вы ні пры якіх умовах не будзеце есці тыя грыбы, якіх не ведаеце. Грыбы могуць быць неядомымі, а могуць " +
            "быць і атрутнымі. Найлепш — паклічце кагосьці, хто разбіраецца ў грыбах вашай мясцовасці, каб " +
            "даведацца, якія грыбы можна збіраць і як іх пасля трэба гатаваць.",
    StringKey.WelcomeConsentWarning to
        "Каб працягнуць, неабходна пагадзіцца са сцвярджэннямі вышэй, паставіўшы галачкі перад тымі сцвярджэннямі, " +
            "з якімі вы згодныя",

    StringKey.WelcomeNextButton to "Далей",

    StringKey.LegalTitle to "Прыватнасць",
    StringKey.LegalPrivacyText to
        "Прагулкі, адзнакі і фатаграфіі застаюцца на вашай прыладзе. Праграма не заводзіць уліковых запісаў " +
            "і нікуды не перадае вашы даныя — у інтэрнэт ідуць толькі запыты ўчасткаў карты з " +
            "openfreemap.org.",
    StringKey.LegalPrivacyLink to "Палітыка прыватнасці",

    StringKey.AboutTitle to "Пра праграму",
    StringKey.AboutMapDataTitle to "Даныя карты",
    StringKey.AboutMapDataText to
        "Карта пабудавана на даных OpenStreetMap, якія распаўсюджваюцца паводле ліцэнзіі ODbL. Вектарныя " +
            "плiткi і стыль — OpenMapTiles, дастаўка — сэрвіс OpenFreeMap.",
    StringKey.AboutOpenSourceTitle to "Адкрыты код",
    StringKey.AboutOpenSourceText to
        "Праграма сабрана з бібліятэк з адкрытым зыходным кодам. Націск на радок спіса адкрывае поўны тэкст " +
            "яе ліцэнзіі.",

    StringKey.NavMenuContentDescription to "Меню",
    StringKey.HelpContentDescription to "Даведка",
    StringKey.HelpDialogTitle to "Даведка",
    StringKey.HelpDialogDismiss to "Зразумела",

    StringKey.CategoryMisc to "Рознае",
    StringKey.CategoryUnknownMushroom to "Невядомы грыб",

    StringKey.CollectionOtherName to "Іншыя",
    StringKey.CollectionPickerSearchHint to "Пошук падборкі ці грыба",
    StringKey.CollectionPickerMoreMatches to "Паказаны не ўсе супадзенні — удакладніце запыт",

    StringKey.LanguagePickerSearchHint to "Пошук мовы",
    StringKey.LanguagePickerBackContentDescription to "Назад",
    StringKey.LanguagePickerConfirmContentDescription to "Пацвердзіць",

    StringKey.DefaultWalkName to "Прагулка",
    StringKey.RecordWalkNameHint to "Назва прагулкі",
    StringKey.RecordStart to "Старт",
    StringKey.RecordPause to "Паўза",
    StringKey.RecordResume to "Працягнуць",
    StringKey.RecordFinish to "Завяршыць",
    StringKey.RecordSetWalkNameTitle to "Задайце назву прагулкі:",
    StringKey.RecordDefaultWalkNamePrefix to "Прагулка ад",
    StringKey.RecordConfirmWalkNameContentDescription to "Прыняць",
    StringKey.RecordMarkLocationContentDescription to "Пазначыць месца",
    StringKey.RecordLocationUnavailable to "Месцазнаходжанне недаступнае — маршрут не запісваецца. Уключыце геалакацыю і дазвольце да яе доступ у наладах прылады.",
    StringKey.RecordLocationUnknownMessage to
        "Месцазнаходжанне пакуль не вызначана — пазнаку няма да чаго прывязаць. Праверце, ці ўключана геалакацыя, і пачакайце сігналу.",
    StringKey.RecordSearchContentDescription to "Пошук",
    StringKey.RecordSearchDialogTitle to "Выберыце патрэбны грыб",
    StringKey.RecordBulkAddQuestion to "Колькі новых грыбоў знойдзена?",
    StringKey.RecordBulkAddCancelContentDescription to "Скасаваць",

    StringKey.RecordBulkAddConfirmContentDescription to "Прыняць",
    StringKey.RecordBulkAddLimitMessage to
        "Максімум аднолькавых грыбоў за адну прагулку — 999.",
    StringKey.DialogAcknowledge to "Зразумела",

    StringKey.NavigationDirectionToPrefix to "Напрамак да",
    StringKey.NavigationDistanceToTargetPrefix to "да мэты",
    StringKey.NavigationMetersSuffix to "метраў",
    StringKey.NavigationKeepRightPhrase to "трымайцеся правей на",
    StringKey.NavigationKeepLeftPhrase to "трымайцеся лявей на",
    StringKey.NavigationGoStraightPhrase to "ідзіце проста",
    StringKey.NavigationDeterminingDirection to "Вызначаем напрамак…",
    StringKey.NavigationArrivedPhrase to "Вы на месцы",
    StringKey.NavigationCloseContentDescription to "Закрыць",

    StringKey.AddPlaceTitle to "Дадайце месца",
    StringKey.AddPlaceEditTitle to "Адрэдагуйце месца",
    StringKey.AddPlaceDefaultName to "Месца",
    StringKey.AddPlaceNameHint to "Назва месца",
    StringKey.AddPlacePhotoContentDescription to "Сфатаграфаваць",
    StringKey.CameraPermissionDenied to "Няма доступу да камеры. Дазвольце яго праграме ў наладах прылады.",
    StringKey.AddPlaceDescriptionTitle to "Апісанне",
    StringKey.AddPlaceDescriptionHint to "Апішыце месца",
    StringKey.AddPlaceCoordinatesTitle to "Каардынаты",
    StringKey.AddPlaceCopyCoordinatesContentDescription to "Скапіраваць каардынаты",
    StringKey.AddPlaceSaveContentDescription to "Захаваць месца",
    StringKey.AddPlaceDiscardContentDescription to "Выдаліць месца",

    StringKey.PlaceViewEditContentDescription to "Рэдагаваць месца",
    StringKey.PlaceViewDeleteContentDescription to "Выдаліць месца",
    StringKey.PlaceDeleteConfirmTitle to "Выдаліць месца?",
    StringKey.PlaceDeleteConfirmMessage to
        "Месца будзе выдалена беззваротна. Аднавіць яго будзе немагчыма.",
    StringKey.PlaceDeleteConfirmYes to "Так",
    StringKey.PlaceDeleteConfirmNo to "Не",

    StringKey.ArchiveEmpty to "Прагулак пакуль няма",
    StringKey.ArchiveEmptyHint to "Тут будуць запісаныя прагулкі: маршрут, знаходкі і пазначаныя месцы.",
    StringKey.EmptyStartWalkButton to "Пачаць прагулку",
    StringKey.ArchiveDeleteWalksButton to "Выдаліць прагулкі",
    StringKey.ArchiveDeleteConfirmMessage to
        "Вы ўпэўнены, што хочаце беззваротна выдаліць вылучаныя прагулкі?",
    StringKey.ArchiveDeleteConfirmYes to "Так",
    StringKey.ArchiveDeleteConfirmNo to "Не",
    StringKey.WalkDetailStartTime to "Старт",
    StringKey.WalkDetailEndTime to "Фініш",
    StringKey.WalkDetailInProgress to "не завершана",
    StringKey.WalkDetailDistance to "Кіламетраж",
    StringKey.WalkDetailDuration to "Працягласць",
    StringKey.WalkDetailAvgSpeed to "Сярэдняя хуткасць",
    StringKey.WalkDetailDurationDays to "д",
    StringKey.WalkDetailDurationHours to "г",
    StringKey.WalkDetailDurationMinutes to "хв",
    StringKey.WalkCardDurationHours to "г",
    StringKey.WalkCardDurationMinutes to "хв",
    StringKey.UnitKilometers to "км",
    StringKey.UnitKmh to "км/г",
    StringKey.UnitMegabytes to "МБ",
    StringKey.WalkDetailFindsTitle to "Знаходкі па тыпах",
    StringKey.WalkDetailFindsEmpty to "Знаходак не зафіксавана",
    StringKey.WalkDetailPlacesTitle to "Пазначаныя месцы",
    StringKey.WalkDetailViewMap to "Глядзець карту",
    StringKey.WalkDetailEditContentDescription to "Рэдагаваць назву прагулкі",
    StringKey.WalkDetailEditWalkNameTitle to "Змяніце назву прагулкі:",
    StringKey.WalkDetailConfirmEditWalkNameContentDescription to "Прыняць",
    StringKey.WalkDetailDeleteContentDescription to "Выдаліць прагулку",
    StringKey.WalkDetailShareAction to "Падзяліцца",
    StringKey.WalkDetailDeleteAction to "Выдаліць",
    StringKey.WalkDetailDeleteConfirmTitle to "Выдаліць прагулку?",
    StringKey.WalkDetailDeleteConfirmMessage to
        "Прагулка і ўсе знаходкі будуць выдалены беззваротна. Аднавіць іх будзе немагчыма.",
    StringKey.WalkDetailDeleteConfirmYes to "Так",
    StringKey.WalkDetailDeleteConfirmNo to "Не",
    StringKey.WalkDetailMushroomsCountZero to "грыбоў",
    StringKey.WalkDetailMushroomsCountOne to "грыб",
    StringKey.WalkDetailMushroomsCountTwo to "грыбы",
    StringKey.WalkDetailMushroomsCountFew to "грыбы",
    StringKey.WalkDetailMushroomsCountMany to "грыбоў",
    StringKey.WalkDetailMushroomsCountOther to "грыбоў",
    StringKey.WalkDetailDescriptionTitle to "Апісанне",
    StringKey.WalkDetailDescriptionEmpty to "Апісанне не дададзена",
    StringKey.WalkDetailDescriptionHint to "Апішыце прагулку",
    StringKey.WalkDetailEditDescriptionContentDescription to "Рэдагаваць апісанне",
    StringKey.WalkDetailDescriptionCancelContentDescription to "Скасаваць",
    StringKey.WalkDetailDescriptionSaveContentDescription to "Захаваць",

    StringKey.WalkShareContentDescription to "Падзяліцца прагулкай",
    StringKey.WalkShareDialogTitle to "Падзяліцца",
    StringKey.WalkShareOptionName to "Назва прагулкі",
    StringKey.WalkShareOptionStats to "Статыстыка прагулкі",
    StringKey.WalkShareOptionDescription to "Апісанне прагулкі",
    StringKey.WalkShareOptionDiagram to "Дыяграма знаходак",
    StringKey.WalkShareOptionMap to "Карта з пазнакамі",
    StringKey.WalkShareMapWarning to "Іншым людзям будзе відаць, дзе вы знайшлі грыбы",
    StringKey.WalkShareCancelButton to "Скасаваць",
    StringKey.WalkShareConfirmButton to "Падзяліцца",
    StringKey.WalkShareFooter to "Створана з дапамогай праграмы «Грыбная карта ад Лешага»",
    StringKey.WalkShareImageFooter to "Створана ў праграме Грыбная карта ад Лешага",

    StringKey.MapStatsTitle to "Статыстыка",
    StringKey.MapStatsWalksCount to "Прагулак",
    StringKey.MapStatsFindsCount to "Знойдзена грыбоў",
    StringKey.MapStatsEmptyHint to "Статыстыка збярэцца сама, як толькі будзе запісана першая прагулка.",

    StringKey.MapFilterButtonLabel to "Фільтры",
    StringKey.MapFilterDialogTitle to "Наладзьце фільтры, якія прымяняюцца да грыбоў на карце:",
    StringKey.MapFilterBackContentDescription to "Назад",
    StringKey.MapFilterDateRangeTitle to "Дыяпазон дат",
    StringKey.MapFilterMonthRangeTitle to "Сезон",
    StringKey.MapFilterPastRoutesTitle to "Адлюстраванне мінулых маршрутаў",
    StringKey.MapFilterShowPastRoutes to "Паказваць мінулыя маршруты",

    StringKey.MonthJanuary to "Студзень",
    StringKey.MonthFebruary to "Люты",
    StringKey.MonthMarch to "Сакавік",
    StringKey.MonthApril to "Красавік",
    StringKey.MonthMay to "Май",
    StringKey.MonthJune to "Чэрвень",
    StringKey.MonthJuly to "Ліпень",
    StringKey.MonthAugust to "Жнівень",
    StringKey.MonthSeptember to "Верасень",
    StringKey.MonthOctober to "Кастрычнік",
    StringKey.MonthNovember to "Лістапад",
    StringKey.MonthDecember to "Снежань",

    StringKey.BackgroundRecordingChannelName to "Запіс прагулкі",
    StringKey.BackgroundRecordingNotificationTitle to "Ідзе запіс прагулкі",
    StringKey.BackgroundRecordingNotificationText to
        "Трэк запісваецца ў фоне. Націсніце, каб вярнуцца ў праграму.",

    StringKey.DataExportOption to "Экспарт",
    StringKey.DataImportOption to "Імпарт",
    StringKey.DataArchiveNameLabel to "Назва архіва",
    StringKey.DataChooseFileButton to "Выбраць файл",
    StringKey.DataFileStatusLabel to "Файл для імпарту",
    StringKey.DataFileNotSelected to "не выбраны",
    StringKey.DataImportLabelFieldLabel to "Прыпіска да назваў прагулак",
    StringKey.DataDoneButton to "Гатова",
    StringKey.DataSavedButton to "Захавана",
    StringKey.DataGoToArchiveButton to "У архіў",
    StringKey.DataCancelButton to "Скасаваць",
    StringKey.DataProcessingLabel to "Ідзе апрацоўка…",
    StringKey.DataExportSuccessMessage to "Архіў паспяхова захаваны",
    StringKey.DataImportedWalksLabel to "Імпартавана прагулак",
    StringKey.DataImportFailedWalksLabel to "Не ўдалося імпартаваць",
    StringKey.DataErrorLabel to "Памылка",
    StringKey.DataImportRejectedTitle to "Гэты файл не падыходзіць для імпарту",
    StringKey.DataImportRejectedNotArchive to "Гэта не архіў: файл не чытаецца як ZIP. Выберыце архіў, выгружаны з «Лешага».",
    StringKey.DataImportRejectedNotLeshy to "Гэта ZIP-архіў, але не архіў «Лешага»: у ім няма manifest.json.",
    StringKey.DataImportRejectedNewerFormat to "Архіў створаны навейшай версіяй праграмы. Абнавіце праграму і паўтарыце імпарт.",
    StringKey.DataImportRejectedDamaged to "Архіў пашкоджаны: частку яго змесціва не ўдаецца прачытаць. Нічога не імпартавана.",
    StringKey.DataImportRejectedNoWalks to "У архіве няма ніводнай прагулкі — імпартаваць няма чаго.",
    StringKey.DataChooseWalksTitle to "Прагулкі для архіва",
    StringKey.DataWalksBackContentDescription to "Назад без захавання выбару",
    StringKey.DataWalksConfirmContentDescription to "Пацвердзіць выбар",
    StringKey.DataWalksSelectedLabel to "Выбрана",
    StringKey.DataWalksCountZero to "прагулак",
    StringKey.DataWalksCountOne to "прагулка",
    StringKey.DataWalksCountTwo to "прагулкі",
    StringKey.DataWalksCountFew to "прагулкі",
    StringKey.DataWalksCountMany to "прагулак",
    StringKey.DataWalksCountOther to "прагулак",
    StringKey.PreparationSelectAreaButton to "Спампаваць бачную вобласць",
    StringKey.PreparationDownloadThisAreaButton to "Спампаваць гэтую вобласць",
    StringKey.PreparationRegionNameDialogTitle to "Назва вобласці",
    StringKey.PreparationRegionNameLabel to "Напрыклад: Лес каля вёскі",
    StringKey.PreparationSaveButton to "Спампаваць",
    StringKey.PreparationCancelButton to "Скасаваць",
    StringKey.PreparationDeleteConfirmTitle to "Выдаліць вобласць?",
    StringKey.PreparationDeleteConfirmMessage to
        "Спампаваныя тайлы карты будуць выдалены беззваротна.",
    StringKey.PreparationDeleteConfirmYes to "Так",
    StringKey.PreparationDeleteConfirmNo to "Не",
    StringKey.PreparationDeleteContentDescription to "Выдаліць вобласць",
    StringKey.PreparationPauseContentDescription to "Прыпыніць спампоўку",
    StringKey.PreparationResumeContentDescription to "Працягнуць спампоўку",
    StringKey.PreparationStatusDownloading to "Спампоўваецца",
    StringKey.PreparationStatusPaused to "На паўзе",
    StringKey.PreparationStatusComplete to "Спампавана",
    StringKey.PreparationStatusError to "Памылка",
    StringKey.PreparationSubtitle to
        "Спампуйце бачную вобласць карты, каб карыстацца ёй без інтэрнэту",
    StringKey.PreparationRetryContentDescription to "Паўтарыць спампоўку",

    StringKey.MapTilesLoadFailed to "Карта не цалкам загрузілася з сайта",
    StringKey.MapTilesLoadFailedDismissContentDescription to "Закрыць паведамленне",

    StringKey.SettingsMapDataTitle to "Даныя карты",
    StringKey.SettingsRefreshMapDataButton to "Абнавіць даныя карты",
    StringKey.SettingsMapDataUpdateConfirmTitle to "Абнавіць даныя карты?",
    StringKey.SettingsMapDataUpdateConfirmMessage to
        "Калі змест карты зменіцца, усе спампаваныя офлайн-вобласці будуць загружаны нанова. " +
            "Вы ўпэўнены, што хочаце абнавіць даныя карты?",
    StringKey.SettingsMapDataUpdateConfirmYes to "Так",
    StringKey.SettingsMapDataUpdateConfirmNo to "Не",
    StringKey.SettingsMapDataRefreshError to
        "Не ўдалося абнавіць — праверце падключэнне да інтэрнэту",
    StringKey.SettingsMapDataRedownloadingPrefix to "Даныя карты абноўлены. Спампоўваецца нанова",
    StringKey.SettingsMapDataRedownloadingSuffix to
        "— прагрэс можна паглядзець у раздзеле «Папярэдняя загрузка».",
    StringKey.SettingsMapDataRegionsCountZero to "абласцей",
    StringKey.SettingsMapDataRegionsCountOne to "вобласць",
    StringKey.SettingsMapDataRegionsCountTwo to "вобласці",
    StringKey.SettingsMapDataRegionsCountFew to "вобласці",
    StringKey.SettingsMapDataRegionsCountMany to "абласцей",
    StringKey.SettingsMapDataRegionsCountOther to "абласцей",
    StringKey.SettingsClearMapCacheButton to "Ачысціць кэш карты",
    StringKey.SettingsClearMapCacheConfirmTitle to "Ачысціць кэш карты?",
    StringKey.SettingsClearMapCacheConfirmMessage to
        "Пры ачыстцы кэша карты будуць выдалены прагледжаныя ўчасткі карты, якія не былі " +
            "захаваны ў раздзеле «Папярэдняя загрузка». Вы ўпэўнены, што хочаце ачысціць кэш?",
    StringKey.SettingsClearMapCacheConfirmYes to "Так",
    StringKey.SettingsClearMapCacheConfirmNo to "Не",
    StringKey.SettingsMapCacheCleared to "Кэш ачышчаны",
)
