package leshy.mushrooms.map.i18n.strings

import leshy.mushrooms.map.i18n.StringKey

/** Azerbaijani — Phase 5 of `.claude/plans/post-soviet-countries.md`. Plural forms follow the
 * two-way split (`Plurals.kt`: `n == 1` → [leshy.mushrooms.map.i18n.PluralCategory.One], else →
 * [leshy.mushrooms.map.i18n.PluralCategory.Other]), but as in Turkish the noun itself never
 * inflects after a numeral ("1 göbələk", "5 göbələk") — all six per-unit forms below carry the
 * identical singular word on purpose, not a placeholder. */
internal val azerbaijaniStrings: Map<StringKey, String> = mapOf(
    StringKey.AppName to "Leşidən Göbələk Xəritəsi",
    StringKey.NavRecord to "Yeni qeyd",
    StringKey.NavArchive to "Gəzinti arxivi",
    StringKey.NavMap to "Tapıntı xəritəsi",
    StringKey.NavData to "İxrac/İdxal",
    StringKey.NavPreparation to "Öncədən yükləmə",
    StringKey.NavSpecies to "Göbələklərim",
    StringKey.SettingsTitle to "Parametrlər",
    StringKey.SettingsContentDescription to "Parametrlər",
    StringKey.SettingsLanguageTitle to "İnterfeys dili",
    StringKey.SettingsThemeTitle to "Görünüş",
    StringKey.SettingsThemeLight to "İşıqlı",
    StringKey.SettingsThemeDark to "Qaranlıq",
    StringKey.SettingsThemeSystem to "Sistem",
    StringKey.SettingsCategoriesTitle to "İzlənilən göbələklər",
    StringKey.SettingsMushroomSizeTitle to "Xəritədə göbələyin ölçüsünü tənzimləyin",
    StringKey.SettingsMushroomSortTitle to "Göbələklərin sırası",
    StringKey.SettingsResetMushroomOrderOnWalkFinish to "Gəzinti sonunda göbələk sırasını sıfırla",
    StringKey.SettingsFreezeMushroomOrder to "Göbələk sırasını sabitlə",

    StringKey.MushroomImagesDisclaimer to
        "Tətbiqdəki bütün göbələk şəkilləri yalnız məlumat xarakterlidir — tanımadığınız " +
            "göbələkləri təyin etmək üçün onlardan istifadə etməyin!",

    StringKey.SpeciesCollectionsTitle to "Göbələk kolleksiyaları",
    StringKey.SpeciesMyMushroomsTitle to "Əlavə edilmiş göbələklər",
    StringKey.SpeciesMyMushroomsEmpty to "Özünüzün əlavə etdiyiniz göbələklər burada görünəcək",
    StringKey.SpeciesAddButton to "Göbələk əlavə et",
    StringKey.SpeciesFormTitleCreate to "Yeni göbələk",
    StringKey.SpeciesFormTitleEdit to "Göbələyi redaktə et",
    StringKey.SpeciesFormNameHint to "Ad",
    StringKey.SpeciesFormScientificNameHint to "Elmi ad",
    StringKey.SpeciesFormColorLabel to "Rəng",
    StringKey.SpeciesFormTakePhotoButton to "Kamera",
    StringKey.SpeciesFormPickPhotoButton to "Qalereya",
    StringKey.SpeciesFormPickCatalogButton to "Şəkillər",
    StringKey.SpeciesFormSaveButton to "Yadda saxla",
    StringKey.SpeciesFormCancelContentDescription to "Ləğv et",
    StringKey.SpeciesListImportedLabel to "arxivdən",
    StringKey.SpeciesListEditContentDescription to "Redaktə et",
    StringKey.SpeciesListDeleteContentDescription to "Növü sil",
    StringKey.SpeciesDeleteConfirmTitle to "Bu göbələk silinsin?",
    StringKey.SpeciesDeleteConfirmMessage to
        "Bu növü silmək istədiyinizə əminsiniz? Bu növ altında qeyd edilmiş bütün tapıntılar " +
            "\"Naməlum göbələk\" kateqoriyasına köçürüləcək. Bu əməliyyat geri qaytarılmır.",
    StringKey.SpeciesDeleteConfirmYes to "Bəli",
    StringKey.SpeciesDeleteConfirmNo to "Xeyr",

    StringKey.CatalogPhotoPickerTitle to "Şəkil seçin",

    StringKey.IconEditorTitle to "Şəkil redaktoru",
    StringKey.IconEditorToolEraser to "Pozan",
    StringKey.IconEditorToolCrop to "Kəs",
    StringKey.IconEditorShapeRectangle to "Düzbucaqlı",
    StringKey.IconEditorShapeOval to "Oval",
    StringKey.IconEditorBrushSizeLabel to "Fırçanın ölçüsü",
    StringKey.IconEditorUndoContentDescription to "Geri al",
    StringKey.IconEditorRedoContentDescription to "İrəli al",
    StringKey.IconEditorDoneContentDescription to "Hazırdır",

    StringKey.OnboardingTitle to "Xoş gəlmisiniz!",
    StringKey.OnboardingDescription to
        "Maraqlandığınız göbələk kolleksiyalarını seçin. Bunu sonra Parametrlərdə dəyişə bilərsiniz.",
    StringKey.OnboardingContinueButton to "Başlayaq",
    StringKey.OnboardingNothingPickedWarning to
        "Davam etmək üçün ən azı bir kolleksiya və ya bir göbələk seçin",

    StringKey.WelcomeIntro to
        "Tətbiq haradan keçdiyinizi və nə tapdığınızı yadda saxlayır — və göbələk yığmağa həqiqətən kömək " +
            "edir: yaxşı yerlərə asanlıqla qayıdırsınız, bütün tapıntılar isə bir xəritədə görünür.",
    StringKey.WelcomeRecordTitle to "Gəzintinizi yazın",
    StringKey.WelcomeRecordText to
        "Marşrutu, vaxtı və məsafəni tətbiq özü aparır. Göbələk tapdınız — onun lövhəciyinə toxunub qeyd " +
            "edin; bulağı, aşmış ağacı və ya avtomobilinizi birbaşa xəritədə işarələyə bilərsiniz.",
    StringKey.WelcomeArchiveTitle to "Tapıntılarınıza qayıdın",
    StringKey.WelcomeArchiveText to
        "Arxivdə hər gəzinti ayrıca durur — öz marşrutu və tapıntıları ilə. Ümumi xəritə isə hamısını bir " +
            "yerdə göstərir: bütün mövsümlər boyu harada, nəyin, nə qədər tapıldığını.",
    StringKey.WelcomeHelpTitle to "Əmin deyilsinizsə «?» düyməsinə toxunun",
    StringKey.WelcomeHelpText to
        "Sağ yuxarıdakı «?» düyməsi hər bölmədə var və həmin bölmənin necə qurulduğunu izah edir.",
    StringKey.WelcomeMenuTitle to "Qalanı menyudadır",
    StringKey.WelcomeMenuText to
        "Sol yuxarıdakı menyu düyməsi tətbiqin bütün bölmələrinin və imkanlarının siyahısını açır.",
    StringKey.WelcomeConsentTitle to "İstifadədən əvvəl",
    StringKey.WelcomeConsentIntro to "Tətbiqin özünə keçməzdən əvvəl aşağıdakı müddəalarla razılaşmaq lazımdır:",
    StringKey.WelcomeConsentImages to
        "Göbələkləri tətbiqdəki şəkillərə görə təyin etməyə çalışmayacaqsınız. Şəkillər yalnız illüstrasiya rolunu " +
            "oynayır və yoxlanılmış təyinedici deyil.",
    StringKey.WelcomeConsentEating to
        "Heç bir halda tanımadığınız göbələkləri yeməyəcəksiniz. Göbələklər yeməli olmaya bilər, hətta zəhərli də " +
            "ola bilər. Ən yaxşısı — bölgənizin göbələklərini bilən birinə müraciət edin ki, hansı göbələkləri " +
            "yığmağın mümkün olduğunu və onları sonra necə hazırlamaq lazım gəldiyini öyrənəsiniz.",
    StringKey.WelcomeConsentWarning to
        "Davam etmək üçün yuxarıdakı müddəalarla razılaşmaq, razı olduğunuz müddəaların qarşısına işarə qoymaq " +
            "lazımdır",

    StringKey.WelcomeNextButton to "İrəli",

    StringKey.LegalTitle to "Məxfilik",
    StringKey.LegalPrivacyText to
        "Gəzintiləriniz, qeydləriniz və fotolarınız cihazınızda qalır. Tətbiq hesab açmır və " +
            "məlumatlarınızı heç yerə göndərmir — internetə yalnız openfreemap.org ünvanından xəritə " +
            "hissələri üçün sorğular gedir.",
    StringKey.LegalPrivacyLink to "Məxfilik siyasəti",

    StringKey.AboutTitle to "Tətbiq haqqında",
    StringKey.AboutMapDataTitle to "Xəritə məlumatları",
    StringKey.AboutMapDataText to
        "Xəritə ODbL lisenziyası ilə yayılan OpenStreetMap məlumatları üzərində qurulub. Vektor plitələri " +
            "və üslub — OpenMapTiles-dən, çatdırılma — OpenFreeMap xidmətindən.",
    StringKey.AboutOpenSourceTitle to "Açıq kod",
    StringKey.AboutOpenSourceText to
        "Tətbiq açıq kodlu kitabxanalardan yığılıb. Siyahıdakı sətrə toxunmaqla onun lisenziyasının tam " +
            "mətni açılır.",

    StringKey.NavMenuContentDescription to "Menyu",
    StringKey.HelpContentDescription to "Kömək",
    StringKey.HelpDialogTitle to "Kömək",
    StringKey.HelpDialogDismiss to "Aydındır",

    StringKey.CategoryMisc to "Digər",
    StringKey.CategoryUnknownMushroom to "Naməlum göbələk",

    StringKey.CollectionPickerSearchHint to "Ölkə və ya göbələk axtar",
    StringKey.CollectionPickerMoreMatches to "Bütün uyğunluqlar göstərilmir — sorğunu dəqiqləşdirin",
    StringKey.LanguagePickerSearchHint to "Dil axtar",
    StringKey.LanguagePickerBackContentDescription to "Geri",
    StringKey.LanguagePickerConfirmContentDescription to "Təsdiqlə",

    StringKey.DefaultWalkName to "Gəzinti",
    StringKey.RecordWalkNameHint to "Gəzintinin adı",
    StringKey.RecordStart to "Başla",
    StringKey.RecordPause to "Fasilə",
    StringKey.RecordResume to "Davam et",
    StringKey.RecordFinish to "Bitir",
    StringKey.RecordSetWalkNameTitle to "Gəzintinin adını yazın:",
    StringKey.RecordDefaultWalkNamePrefix to "Gəzinti",
    StringKey.RecordConfirmWalkNameContentDescription to "Təsdiqlə",
    StringKey.RecordMarkLocationContentDescription to "Yeri qeyd et",
    StringKey.RecordLocationUnavailable to
        "Məkan əlçatan deyil — marşrut yazılmır. Cihazın parametrlərində məkan xidmətlərini " +
            "yandırın və tətbiqə onlardan istifadəyə icazə verin.",
    StringKey.RecordLocationUnknownMessage to
        "Məkan hələ təyin edilməyib — nişanı bağlamağa yer yoxdur. Məkan xidmətlərinin açıq olduğunu yoxlayın və siqnalı gözləyin.",
    StringKey.RecordSearchContentDescription to "Axtarış",
    StringKey.RecordSearchDialogTitle to "Lazım olan göbələyi seçin",
    StringKey.RecordBulkAddQuestion to "Neçə yeni göbələk tapıldı?",
    StringKey.RecordBulkAddCancelContentDescription to "Ləğv et",
    StringKey.RecordBulkAddConfirmContentDescription to "Təsdiqlə",
    StringKey.RecordBulkAddLimitMessage to "Bir gəzintidə eyni növdən ən çoxu 999 tapıntı.",
    StringKey.DialogAcknowledge to "Aydındır",

    StringKey.NavigationDirectionToPrefix to "İstiqamət:",
    StringKey.NavigationDistanceToTargetPrefix to "hədəfə qədər",
    StringKey.NavigationMetersSuffix to "metr",
    StringKey.NavigationKeepRightPhrase to "sağa alın",
    StringKey.NavigationKeepLeftPhrase to "sola alın",
    StringKey.NavigationGoStraightPhrase to "düz gedin",
    StringKey.NavigationDeterminingDirection to "İstiqamət təyin edilir…",
    StringKey.NavigationArrivedPhrase to "Gəlib çatdınız",
    StringKey.NavigationCloseContentDescription to "Bağla",

    StringKey.AddPlaceTitle to "Yer əlavə et",
    StringKey.AddPlaceEditTitle to "Yeri redaktə et",
    StringKey.AddPlaceDefaultName to "Yer",
    StringKey.AddPlaceNameHint to "Yerin adı",
    StringKey.AddPlacePhotoContentDescription to "Şəkil çək",
    StringKey.CameraPermissionDenied to
        "Kameraya giriş yoxdur. Cihazın parametrlərində tətbiqə icazə verin.",
    StringKey.AddPlaceDescriptionTitle to "Təsvir",
    StringKey.AddPlaceDescriptionHint to "Yeri təsvir edin",
    StringKey.AddPlaceCoordinatesTitle to "Koordinatlar",
    StringKey.AddPlaceCopyCoordinatesContentDescription to "Koordinatları kopyala",
    StringKey.AddPlaceSaveContentDescription to "Yeri yadda saxla",
    StringKey.AddPlaceDiscardContentDescription to "Yerdən imtina et",
    StringKey.PlaceViewEditContentDescription to "Yeri redaktə et",
    StringKey.PlaceViewDeleteContentDescription to "Yeri sil",
    StringKey.PlaceDeleteConfirmTitle to "Yer silinsin?",
    StringKey.PlaceDeleteConfirmMessage to "Yer həmişəlik silinəcək. Bu əməliyyat geri qaytarılmır.",
    StringKey.PlaceDeleteConfirmYes to "Bəli",
    StringKey.PlaceDeleteConfirmNo to "Xeyr",

    StringKey.ArchiveEmpty to "Hələ heç bir gəzinti yazılmayıb",
    StringKey.ArchiveEmptyHint to
        "Yazılan gəzintilər burada olacaq: marşrut, tapıntılar və qeyd edilmiş yerlər.",
    StringKey.EmptyStartWalkButton to "Gəzintiyə başla",
    StringKey.ArchiveDeleteWalksButton to "Gəzintiləri sil",
    StringKey.ArchiveDeleteConfirmMessage to
        "Seçilmiş gəzintiləri həmişəlik silmək istədiyinizə əminsiniz?",
    StringKey.ArchiveDeleteConfirmYes to "Bəli",
    StringKey.ArchiveDeleteConfirmNo to "Xeyr",

    StringKey.WalkDetailStartTime to "Başlanğıc",
    StringKey.WalkDetailEndTime to "Bitmə",
    StringKey.WalkDetailInProgress to "davam edir",
    StringKey.WalkDetailDistance to "Məsafə",
    StringKey.WalkDetailDuration to "Müddət",
    StringKey.WalkDetailAvgSpeed to "Orta sürət",
    StringKey.WalkDetailDurationDays to "g",
    StringKey.WalkDetailDurationHours to "s",
    StringKey.WalkDetailDurationMinutes to "dəq",
    StringKey.WalkCardDurationHours to "s",
    StringKey.WalkCardDurationMinutes to "d",
    StringKey.UnitKilometers to "km",
    StringKey.UnitKmh to "km/s",
    StringKey.UnitMegabytes to "MB",
    StringKey.WalkDetailFindsTitle to "Növlərə görə tapıntılar",
    StringKey.WalkDetailFindsEmpty to "Tapıntı qeyd edilməyib",
    StringKey.WalkDetailPlacesTitle to "Qeyd edilmiş yerlər",
    StringKey.WalkDetailViewMap to "Xəritəyə bax",
    StringKey.WalkDetailEditContentDescription to "Gəzintinin adını dəyiş",
    StringKey.WalkDetailEditWalkNameTitle to "Gəzintinin adını dəyişin:",
    StringKey.WalkDetailConfirmEditWalkNameContentDescription to "Təsdiqlə",
    StringKey.WalkDetailDeleteContentDescription to "Gəzintini sil",
    StringKey.WalkDetailShareAction to "Paylaş",
    StringKey.WalkDetailDeleteAction to "Sil",
    StringKey.WalkDetailDeleteConfirmTitle to "Gəzinti silinsin?",
    StringKey.WalkDetailDeleteConfirmMessage to
        "Gəzinti və onun bütün tapıntıları həmişəlik silinəcək. Bu əməliyyat geri qaytarılmır.",
    StringKey.WalkDetailDeleteConfirmYes to "Bəli",
    StringKey.WalkDetailDeleteConfirmNo to "Xeyr",
    StringKey.WalkDetailMushroomsCountZero to "göbələk",
    StringKey.WalkDetailMushroomsCountOne to "göbələk",
    StringKey.WalkDetailMushroomsCountTwo to "göbələk",
    StringKey.WalkDetailMushroomsCountFew to "göbələk",
    StringKey.WalkDetailMushroomsCountMany to "göbələk",
    StringKey.WalkDetailMushroomsCountOther to "göbələk",
    StringKey.WalkDetailDescriptionTitle to "Təsvir",
    StringKey.WalkDetailDescriptionEmpty to "Təsvir əlavə edilməyib",
    StringKey.WalkDetailDescriptionHint to "Gəzintini təsvir edin",
    StringKey.WalkDetailEditDescriptionContentDescription to "Təsviri redaktə et",
    StringKey.WalkDetailDescriptionCancelContentDescription to "Ləğv et",
    StringKey.WalkDetailDescriptionSaveContentDescription to "Yadda saxla",

    StringKey.WalkShareContentDescription to "Gəzintini paylaş",
    StringKey.WalkShareDialogTitle to "Paylaş",
    StringKey.WalkShareOptionName to "Gəzintinin adı",
    StringKey.WalkShareOptionStats to "Gəzintinin statistikası",
    StringKey.WalkShareOptionDescription to "Gəzintinin təsviri",
    StringKey.WalkShareOptionDiagram to "Tapıntı diaqramı",
    StringKey.WalkShareOptionMap to "İşarələrlə xəritə",
    StringKey.WalkShareMapWarning to "Başqaları göbələkləri harada tapdığınızı görə biləcək",
    StringKey.WalkShareCancelButton to "Ləğv et",
    StringKey.WalkShareConfirmButton to "Paylaş",
    StringKey.WalkShareFooter to "\"Leşidən Göbələk Xəritəsi\" tətbiqi ilə hazırlanıb",
    StringKey.WalkShareImageFooter to "Leşidən Göbələk Xəritəsi tətbiqində yaradılıb",

    StringKey.MapStatsTitle to "Statistika",
    StringKey.MapStatsWalksCount to "Gəzintilər",
    StringKey.MapStatsFindsCount to "Tapılan göbələklər",
    StringKey.MapStatsEmptyHint to "İlk gəzinti yazılan kimi statistika özü yığılacaq.",
    StringKey.MapFilterButtonLabel to "Filtrlər",
    StringKey.MapFilterDialogTitle to "Xəritədəki göbələklərə tətbiq olunan filtrləri tənzimləyin:",
    StringKey.MapFilterBackContentDescription to "Geri",
    StringKey.MapFilterDateRangeTitle to "Tarix aralığı",
    StringKey.MapFilterMonthRangeTitle to "Mövsüm",
    StringKey.MapFilterPastRoutesTitle to "Keçmiş marşrutların göstərilməsi",
    StringKey.MapFilterShowPastRoutes to "Keçmiş marşrutları göstər",

    StringKey.MonthJanuary to "Yanvar",
    StringKey.MonthFebruary to "Fevral",
    StringKey.MonthMarch to "Mart",
    StringKey.MonthApril to "Aprel",
    StringKey.MonthMay to "May",
    StringKey.MonthJune to "İyun",
    StringKey.MonthJuly to "İyul",
    StringKey.MonthAugust to "Avqust",
    StringKey.MonthSeptember to "Sentyabr",
    StringKey.MonthOctober to "Oktyabr",
    StringKey.MonthNovember to "Noyabr",
    StringKey.MonthDecember to "Dekabr",

    StringKey.BackgroundRecordingChannelName to "Gəzintinin yazılması",
    StringKey.BackgroundRecordingNotificationTitle to "Gəzintiniz yazılır",
    StringKey.BackgroundRecordingNotificationText to
        "Marşrut arxa planda yazılır. Tətbiqə qayıtmaq üçün toxunun.",

    StringKey.DataExportOption to "İxrac",
    StringKey.DataImportOption to "İdxal",
    StringKey.DataArchiveNameLabel to "Arxivin adı",
    StringKey.DataChooseFileButton to "Fayl seç",
    StringKey.DataFileStatusLabel to "İdxal faylı",
    StringKey.DataFileNotSelected to "seçilməyib",
    StringKey.DataImportLabelFieldLabel to "İdxal edilmiş gəzintilərin adına əlavə olunan işarə",
    StringKey.DataDoneButton to "Hazırdır",
    StringKey.DataSavedButton to "Saxlanıldı",
    StringKey.DataGoToArchiveButton to "Arxivə",
    StringKey.DataCancelButton to "Ləğv et",
    StringKey.DataProcessingLabel to "Emal olunur…",
    StringKey.DataExportSuccessMessage to "Arxiv uğurla saxlanıldı",
    StringKey.DataImportedWalksLabel to "İdxal edilən gəzintilər",
    StringKey.DataImportFailedWalksLabel to "İdxal edilə bilmədi",
    StringKey.DataErrorLabel to "Xəta",
    StringKey.DataImportRejectedTitle to "Bu faylı idxal etmək mümkün deyil",
    StringKey.DataImportRejectedNotArchive to
        "Bu arxiv deyil: fayl ZIP kimi oxunmur. Leşidən ixrac edilmiş arxiv seçin.",
    StringKey.DataImportRejectedNotLeshy to
        "Bu ZIP arxividir, amma Leşinin arxivi deyil: içində manifest.json yoxdur.",
    StringKey.DataImportRejectedNewerFormat to
        "Arxiv tətbiqin daha yeni versiyası ilə yazılıb. Tətbiqi yeniləyin və yenidən cəhd edin.",
    StringKey.DataImportRejectedDamaged to
        "Arxiv zədələnib: məzmununun bir hissəsi oxunmur. Heç nə idxal edilmədi.",
    StringKey.DataImportRejectedNoWalks to "Arxivdə gəzinti yoxdur — idxal ediləcək bir şey yoxdur.",
    StringKey.DataChooseWalksTitle to "İxrac ediləcək gəzintilər",
    StringKey.DataWalksBackContentDescription to "Seçimi saxlamadan geri",
    StringKey.DataWalksConfirmContentDescription to "Seçimi təsdiqlə",
    StringKey.DataWalksSelectedLabel to "Seçilib",
    StringKey.DataWalksCountZero to "gəzinti",
    StringKey.DataWalksCountOne to "gəzinti",
    StringKey.DataWalksCountTwo to "gəzinti",
    StringKey.DataWalksCountFew to "gəzinti",
    StringKey.DataWalksCountMany to "gəzinti",
    StringKey.DataWalksCountOther to "gəzinti",

    StringKey.PreparationSelectAreaButton to "Görünən ərazini yüklə",
    StringKey.PreparationDownloadThisAreaButton to "Bu ərazini yüklə",
    StringKey.PreparationRegionNameDialogTitle to "Ərazinin adı",
    StringKey.PreparationRegionNameLabel to "Məsələn, kənd yaxınlığındakı meşə",
    StringKey.PreparationSaveButton to "Yüklə",
    StringKey.PreparationCancelButton to "Ləğv et",
    StringKey.PreparationDeleteConfirmTitle to "Ərazi silinsin?",
    StringKey.PreparationDeleteConfirmMessage to "Yüklənmiş xəritə fraqmentləri həmişəlik silinəcək.",
    StringKey.PreparationDeleteConfirmYes to "Bəli",
    StringKey.PreparationDeleteConfirmNo to "Xeyr",
    StringKey.PreparationDeleteContentDescription to "Ərazini sil",
    StringKey.PreparationPauseContentDescription to "Yükləməni dayandır",
    StringKey.PreparationResumeContentDescription to "Yükləməni davam etdir",
    StringKey.PreparationStatusDownloading to "Yüklənir",
    StringKey.PreparationStatusPaused to "Dayandırılıb",
    StringKey.PreparationStatusComplete to "Yükləndi",
    StringKey.PreparationStatusError to "Xəta",
    StringKey.PreparationSubtitle to
        "Oflayn istifadə etmək üçün xəritənin görünən hissəsini yükləyin",
    StringKey.PreparationRetryContentDescription to "Yükləməni təkrarla",

    StringKey.MapTilesLoadFailed to "Xəritə tam yüklənmədi:",
    StringKey.MapTilesLoadFailedDismissContentDescription to "Bildirişi bağla",

    StringKey.SettingsMapDataTitle to "Xəritə məlumatları",
    StringKey.SettingsRefreshMapDataButton to "Xəritə məlumatlarını yenilə",
    StringKey.SettingsMapDataUpdateConfirmTitle to "Xəritə məlumatları yenilənsin?",
    StringKey.SettingsMapDataUpdateConfirmMessage to
        "Xəritənin məzmunu dəyişibsə, yüklənmiş bütün oflayn ərazilər yenidən yüklənəcək. " +
            "Xəritə məlumatlarını yeniləmək istədiyinizə əminsiniz?",
    StringKey.SettingsMapDataUpdateConfirmYes to "Bəli",
    StringKey.SettingsMapDataUpdateConfirmNo to "Xeyr",
    StringKey.SettingsMapDataRefreshError to "Yeniləmə alınmadı — internet bağlantınızı yoxlayın",
    StringKey.SettingsMapDataRedownloadingPrefix to "Xəritə məlumatları yeniləndi. Yenidən yüklənir:",
    StringKey.SettingsMapDataRedownloadingSuffix to
        "— gedişatı Öncədən yükləmə bölməsindən izləyə bilərsiniz.",
    StringKey.SettingsMapDataRegionsCountZero to "ərazi",
    StringKey.SettingsMapDataRegionsCountOne to "ərazi",
    StringKey.SettingsMapDataRegionsCountTwo to "ərazi",
    StringKey.SettingsMapDataRegionsCountFew to "ərazi",
    StringKey.SettingsMapDataRegionsCountMany to "ərazi",
    StringKey.SettingsMapDataRegionsCountOther to "ərazi",
    StringKey.SettingsClearMapCacheButton to "Xəritə keşini təmizlə",
    StringKey.SettingsClearMapCacheConfirmTitle to "Xəritə keşi təmizlənsin?",
    StringKey.SettingsClearMapCacheConfirmMessage to
        "Xəritə keşinin təmizlənməsi Öncədən yükləmə bölməsində saxlanılmayan baxılmış xəritə " +
            "ərazilərini silir. Keşi təmizləmək istədiyinizə əminsiniz?",
    StringKey.SettingsClearMapCacheConfirmYes to "Bəli",
    StringKey.SettingsClearMapCacheConfirmNo to "Xeyr",
    StringKey.SettingsMapCacheCleared to "Keş təmizləndi",
)
