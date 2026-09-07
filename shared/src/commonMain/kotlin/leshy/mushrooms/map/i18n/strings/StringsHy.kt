package leshy.mushrooms.map.i18n.strings

import leshy.mushrooms.map.i18n.StringKey

/** Armenian — Phase 5 of `.claude/plans/post-soviet-countries.md`. Plural rule is the two-way
 * split with 0 joining 1 in the singular (`Plurals.kt`), but the counted noun itself stays in the
 * singular after any numeral in Armenian ("1 սունկ", "5 սունկ") — all six per-unit forms below
 * carry the identical word on purpose, not a placeholder. */
internal val armenianStrings: Map<StringKey, String> = mapOf(
    StringKey.AppName to "Սնկերի քարտեզ Լեշիից",
    StringKey.NavRecord to "Նոր գրառում",
    StringKey.NavArchive to "Զբոսանքների արխիվ",
    StringKey.NavMap to "Գտածոների քարտեզ",
    StringKey.NavData to "Արտահանում/Ներմուծում",
    StringKey.NavPreparation to "Նախնական ներբեռնում",
    StringKey.NavSpecies to "Իմ սնկերը",
    StringKey.SettingsTitle to "Կարգավորումներ",
    StringKey.SettingsContentDescription to "Կարգավորումներ",
    StringKey.SettingsLanguageTitle to "Ինտերֆեյսի լեզու",
    StringKey.SettingsThemeTitle to "Տեսք",
    StringKey.SettingsThemeLight to "Բաց",
    StringKey.SettingsThemeDark to "Մուգ",
    StringKey.SettingsThemeSystem to "Համակարգային",
    StringKey.SettingsCategoriesTitle to "Հետևվող սնկերը",
    StringKey.SettingsMushroomSizeTitle to "Կարգավորեք սնկի չափը քարտեզի վրա",
    StringKey.SettingsMushroomSortTitle to "Սնկերի դասավորությունը",
    StringKey.SettingsResetMushroomOrderOnWalkFinish to
        "Զբոսանքի վերջում վերականգնել սնկերի դասավորությունը",
    StringKey.SettingsFreezeMushroomOrder to "Ամրագրել սնկերի դասավորությունը",

    StringKey.MushroomImagesDisclaimer to
        "Հավելվածի բոլոր սնկերի պատկերները միայն տեղեկատվական են — մի՛ օգտագործեք դրանք " +
            "անծանոթ սունկ որոշելու համար։",

    StringKey.SpeciesCollectionsTitle to "Սնկերի հավաքածուներ ըստ երկրների",
    StringKey.SpeciesMyMushroomsTitle to "Ավելացված սնկեր",
    StringKey.SpeciesMyMushroomsEmpty to "Ձեր ավելացրած սնկերն այստեղ կհայտնվեն",
    StringKey.SpeciesAddButton to "Ավելացնել սունկ",
    StringKey.SpeciesFormTitleCreate to "Նոր սունկ",
    StringKey.SpeciesFormTitleEdit to "Խմբագրել սունկը",
    StringKey.SpeciesFormNameHint to "Անվանում",
    StringKey.SpeciesFormScientificNameHint to "Գիտական անվանում",
    StringKey.SpeciesFormColorLabel to "Գույն",
    StringKey.SpeciesFormTakePhotoButton to "Տեսախցիկ",
    StringKey.SpeciesFormPickPhotoButton to "Պատկերասրահ",
    StringKey.SpeciesFormPickCatalogButton to "Նկարներ",
    StringKey.SpeciesFormSaveButton to "Պահպանել",
    StringKey.SpeciesFormCancelContentDescription to "Չեղարկել",
    StringKey.SpeciesCollectionDialogTitle to "Ո՞ր հավաքածուի մեջ",
    StringKey.SpeciesCollectionDialogBackContentDescription to "Հետ",
    StringKey.SpeciesCollectionDialogSaveContentDescription to "Պահել հավաքածուում",
    StringKey.SpeciesCollectionNameIsCountry to "Սա երկրի անուն է — ընտրեք այլ անուն",
    StringKey.SpeciesListImportedLabel to "արխիվից",
    StringKey.SpeciesListEditContentDescription to "Խմբագրել",
    StringKey.SpeciesListDeleteContentDescription to "Ջնջել տեսակը",
    StringKey.SpeciesDeleteConfirmTitle to "Ջնջե՞լ այս սունկը",
    StringKey.SpeciesDeleteConfirmMessage to
        "Վստա՞հ եք, որ ուզում եք ջնջել այս տեսակը։ Դրանով գրանցված բոլոր գտածոները կտեղափոխվեն " +
            "«Անհայտ սունկ» կատեգորիա։ Այս գործողությունն անշրջելի է։",
    StringKey.SpeciesDeleteConfirmYes to "Այո",
    StringKey.SpeciesDeleteConfirmNo to "Ոչ",

    StringKey.CatalogPhotoPickerTitle to "Ընտրեք նկար",

    StringKey.IconEditorTitle to "Լուսանկարի խմբագրիչ",
    StringKey.IconEditorToolEraser to "Ջնջոց",
    StringKey.IconEditorToolCrop to "Կտրել",
    StringKey.IconEditorShapeRectangle to "Ուղղանկյուն",
    StringKey.IconEditorShapeOval to "Օվալ",
    StringKey.IconEditorBrushSizeLabel to "Վրձնի չափը",
    StringKey.IconEditorUndoContentDescription to "Հետարկել",
    StringKey.IconEditorRedoContentDescription to "Կրկնել",
    StringKey.IconEditorDoneContentDescription to "Պատրաստ է",

    StringKey.OnboardingTitle to "Բարի գալուստ։",
    StringKey.OnboardingDescription to
        "Ընտրեք ձեզ հետաքրքրող սնկերի հավաքածուները։ Սա հետո կարող եք փոխել Կարգավորումներում։",
    StringKey.OnboardingContinueButton to "Սկսենք",
    StringKey.OnboardingNothingPickedWarning to
        "Շարունակելու համար ընտրեք առնվազն մեկ հավաքածու կամ մեկ սունկ",

    StringKey.WelcomeIntro to
        "Հավելվածը հիշում է, թե որտեղով եք անցել և ինչ եք գտել, և իսկապես օգնում է սունկ հավաքելիս․ լավ " +
            "վայրեր հեշտ է վերադառնալ, իսկ բոլոր գտածոները երևում են մեկ քարտեզի վրա։",
    StringKey.WelcomeRecordTitle to "Գրանցեք ձեր զբոսանքը",
    StringKey.WelcomeRecordText to
        "Երթուղին, ժամանակը և կիլոմետրերը հավելվածն ինքն է վարում։ Սունկ գտա՞ք — նշեք այն՝ հպվելով նրա " +
            "սալիկին. աղբյուրը, ընկած ծառը կամ մեքենան կարող եք նշել հենց քարտեզի վրա։",
    StringKey.WelcomeArchiveTitle to "Վերադարձեք ձեր գտածոներին",
    StringKey.WelcomeArchiveText to
        "Արխիվում յուրաքանչյուր զբոսանք առանձին է՝ իր երթուղով և գտածոներով։ Իսկ ընդհանուր քարտեզը ցույց է " +
            "տալիս բոլորը միասին՝ որտեղ, ինչ և որքան է գտնվել բոլոր եղանակների ընթացքում։",
    StringKey.WelcomeHelpTitle to "Վստահ չե՞ք — սեղմեք «?»",
    StringKey.WelcomeHelpText to
        "«?» կոճակը վերևի աջ անկյունում կա բոլոր բաժիններում և բացատրում է, թե ինչպես է կառուցված տվյալ " +
            "բաժինը։",
    StringKey.WelcomeMenuTitle to "Մնացածը՝ ընտրացանկում",
    StringKey.WelcomeMenuText to
        "Վերևի ձախ անկյունի ընտրացանկի կոճակը բացում է հավելվածի բոլոր բաժինների և հնարավորությունների " +
            "ցանկը։",
    StringKey.WelcomeConsentTitle to "Նախքան օգտագործելը",
    StringKey.WelcomeConsentIntro to "Բուն հավելվածին անցնելուց առաջ անհրաժեշտ է համաձայնվել հետևյալ պնդումների հետ․",
    StringKey.WelcomeConsentImages to
        "Դուք չեք փորձի սունկերը որոշել հավելվածի նկարներով։ Նկարները պատկերազարդման դեր են կատարում և ստուգված " +
            "որոշիչ չեն։",
    StringKey.WelcomeConsentEating to
        "Դուք ոչ մի պարագայում չեք ուտի այն սունկերը, որոնք չգիտեք։ Սունկերը կարող են ուտելի չլինել, կարող են նաև " +
            "թունավոր լինել։ Ամենալավը՝ դիմեք մեկին, ով տիրապետում է ձեր տարածքի սունկերին, որպեսզի իմանաք, թե որ " +
            "սունկերը կարելի է հավաքել և ինչպես պետք է դրանք հետո պատրաստել։",
    StringKey.WelcomeConsentBattery to
        "Զբոսանքների ընթացքում դուք կպահպանեք անվտանգության անհրաժեշտ կանոնները և հաշվի կառնեք, որ աշխատող " +
            "հավելվածի դեպքում հեռախոսը ավելի արագ է լիցքաթափվում։ Ցածր լիցքի դեպքում ավելի լավ է դադարեցնել " +
            "զբոսանքի գրանցումը, իսկ հավելվածը՝ փակել։",
    StringKey.WelcomeConsentWarning to
        "Շարունակելու համար անհրաժեշտ է համաձայնվել վերևի պնդումների հետ՝ նշում դնելով այն պնդումների դիմաց, որոնց " +
            "հետ համաձայն եք",

    StringKey.WelcomeNextButton to "Առաջ",

    StringKey.LegalTitle to "Գաղտնիություն",
    StringKey.LegalPrivacyText to
        "Ձեր զբոսանքները, նշումները և լուսանկարները մնում են ձեր սարքում։ Հավելվածը հաշիվներ չի ստեղծում և " +
            "ձեր տվյալները ոչ մի տեղ չի ուղարկում — համացանց են գնում միայն քարտեզի հատվածների հարցումները " +
            "openfreemap.org հասցեին։",
    StringKey.LegalPrivacyLink to "Գաղտնիության քաղաքականություն",

    StringKey.AboutTitle to "Հավելվածի մասին",
    StringKey.AboutMapDataTitle to "Քարտեզի տվյալներ",
    StringKey.AboutMapDataText to
        "Քարտեզը կառուցված է OpenStreetMap-ի տվյալների վրա, որոնք տարածվում են ODbL արտոնագրով։ Վեկտորային " +
            "սալիկները և ոճը՝ OpenMapTiles-ից, մատուցումը՝ OpenFreeMap ծառայության։",
    StringKey.AboutOpenSourceTitle to "Բաց կոդ",
    StringKey.AboutOpenSourceText to
        "Հավելվածը հավաքված է բաց կոդով գրադարաններից։ Ցանկի տողին հպվելը բացում է դրա արտոնագրի ամբողջական " +
            "տեքստը։",

    StringKey.NavMenuContentDescription to "Ցանկ",
    StringKey.HelpContentDescription to "Օգնություն",
    StringKey.HelpDialogTitle to "Օգնություն",
    StringKey.HelpDialogDismiss to "Հասկացա",

    StringKey.CategoryMisc to "Այլ",
    StringKey.CategoryUnknownMushroom to "Անհայտ սունկ",

    StringKey.CollectionOtherName to "Այլ",
    StringKey.CollectionPickerSearchHint to "Փնտրել հավաքածու կամ սունկ",
    StringKey.CollectionPickerMoreMatches to "Ցուցադրված չեն բոլոր համընկնումները — ճշտեք հարցումը",
    StringKey.LanguagePickerSearchHint to "Փնտրել լեզու",
    StringKey.LanguagePickerBackContentDescription to "Հետ",
    StringKey.LanguagePickerConfirmContentDescription to "Հաստատել",

    StringKey.DefaultWalkName to "Զբոսանք",
    StringKey.RecordWalkNameHint to "Զբոսանքի անվանումը",
    StringKey.RecordStart to "Սկսել",
    StringKey.RecordPause to "Դադար",
    StringKey.RecordResume to "Շարունակել",
    StringKey.RecordFinish to "Ավարտել",
    StringKey.RecordSetWalkNameTitle to "Նշեք զբոսանքի անվանումը՝",
    StringKey.RecordDefaultWalkNamePrefix to "Զբոսանք",
    StringKey.RecordConfirmWalkNameContentDescription to "Հաստատել",
    StringKey.RecordMarkLocationContentDescription to "Նշել վայրը",
    StringKey.RecordLocationUnavailable to
        "Տեղորոշումն անհասանելի է — երթուղին չի գրանցվում։ Միացրեք տեղորոշման ծառայությունները " +
            "և սարքի կարգավորումներում թույլ տվեք հավելվածին օգտվել դրանցից։",
    StringKey.RecordLocationUnknownMessage to
        "Տեղորոշումը դեռ հայտնի չէ՝ նշումը կապելու տեղ չկա։ Ստուգեք՝ միացված է արդյոք տեղորոշումը, և սպասեք ազդանշանին։",
    StringKey.RecordSearchContentDescription to "Որոնում",
    StringKey.RecordSearchDialogTitle to "Ընտրեք ձեզ անհրաժեշտ սունկը",
    StringKey.RecordBulkAddQuestion to "Քանի՞ նոր սունկ է գտնվել",
    StringKey.RecordBulkAddCancelContentDescription to "Չեղարկել",
    StringKey.RecordBulkAddConfirmContentDescription to "Հաստատել",
    StringKey.RecordBulkAddLimitMessage to "Մեկ զբոսանքում նույն տեսակի առավելագույնը 999 գտածո։",
    StringKey.DialogAcknowledge to "Հասկացա",

    StringKey.NavigationDirectionToPrefix to "Ուղղություն՝",
    StringKey.NavigationDistanceToTargetPrefix to "մինչև նպատակակետ",
    StringKey.NavigationMetersSuffix to "մետր",
    StringKey.NavigationKeepRightPhrase to "վերցրեք աջ",
    StringKey.NavigationKeepLeftPhrase to "վերցրեք ձախ",
    StringKey.NavigationGoStraightPhrase to "գնացեք ուղիղ",
    StringKey.NavigationDeterminingDirection to "Ուղղությունը որոշվում է…",
    StringKey.NavigationArrivedPhrase to "Դուք հասել եք",
    StringKey.NavigationCloseContentDescription to "Փակել",

    StringKey.AddPlaceTitle to "Ավելացնել վայր",
    StringKey.AddPlaceEditTitle to "Խմբագրել վայրը",
    StringKey.AddPlaceDefaultName to "Վայր",
    StringKey.AddPlaceNameHint to "Վայրի անվանումը",
    StringKey.AddPlacePhotoContentDescription to "Լուսանկարել",
    StringKey.CameraPermissionDenied to
        "Տեսախցիկը հասանելի չէ։ Թույլատրեք այն հավելվածին սարքի կարգավորումներում։",
    StringKey.AddPlaceDescriptionTitle to "Նկարագրություն",
    StringKey.AddPlaceDescriptionHint to "Նկարագրեք վայրը",
    StringKey.AddPlaceCoordinatesTitle to "Կոորդինատներ",
    StringKey.AddPlaceCopyCoordinatesContentDescription to "Պատճենել կոորդինատները",
    StringKey.AddPlaceSaveContentDescription to "Պահպանել վայրը",
    StringKey.AddPlaceDiscardContentDescription to "Հրաժարվել վայրից",
    StringKey.PlaceViewEditContentDescription to "Խմբագրել վայրը",
    StringKey.PlaceViewDeleteContentDescription to "Ջնջել վայրը",
    StringKey.PlaceDeleteConfirmTitle to "Ջնջե՞լ վայրը",
    StringKey.PlaceDeleteConfirmMessage to "Վայրը կջնջվի ընդմիշտ։ Այս գործողությունն անշրջելի է։",
    StringKey.PlaceDeleteConfirmYes to "Այո",
    StringKey.PlaceDeleteConfirmNo to "Ոչ",

    StringKey.ArchiveEmpty to "Դեռ ոչ մի զբոսանք գրանցված չէ",
    StringKey.ArchiveEmptyHint to "Այստեղ կլինեն գրանցված զբոսանքները՝ երթուղին, գտածոները և նշված վայրերը։",
    StringKey.EmptyStartWalkButton to "Սկսել զբոսանք",
    StringKey.ArchiveDeleteWalksButton to "Ջնջել զբոսանքները",
    StringKey.ArchiveDeleteConfirmMessage to
        "Վստա՞հ եք, որ ուզում եք ընդմիշտ ջնջել ընտրված զբոսանքները",
    StringKey.ArchiveDeleteConfirmYes to "Այո",
    StringKey.ArchiveDeleteConfirmNo to "Ոչ",

    StringKey.WalkDetailStartTime to "Սկիզբ",
    StringKey.WalkDetailEndTime to "Ավարտ",
    StringKey.WalkDetailInProgress to "ընթացքի մեջ",
    StringKey.WalkDetailDistance to "Հեռավորություն",
    StringKey.WalkDetailDuration to "Տևողություն",
    StringKey.WalkDetailAvgSpeed to "Միջին արագություն",
    StringKey.WalkDetailDurationDays to "օր",
    StringKey.WalkDetailDurationHours to "ժ",
    StringKey.WalkDetailDurationMinutes to "ր",
    StringKey.WalkCardDurationHours to "ժ",
    StringKey.WalkCardDurationMinutes to "ր",
    StringKey.UnitKilometers to "կմ",
    StringKey.UnitKmh to "կմ/ժ",
    StringKey.UnitMegabytes to "ՄԲ",
    StringKey.WalkDetailFindsTitle to "Գտածոներն ըստ տեսակի",
    StringKey.WalkDetailFindsEmpty to "Գտածո գրանցված չէ",
    StringKey.WalkDetailPlacesTitle to "Նշված վայրեր",
    StringKey.WalkDetailViewMap to "Դիտել քարտեզը",
    StringKey.WalkDetailEditContentDescription to "Փոխել զբոսանքի անվանումը",
    StringKey.WalkDetailEditWalkNameTitle to "Փոխեք զբոսանքի անվանումը՝",
    StringKey.WalkDetailConfirmEditWalkNameContentDescription to "Հաստատել",
    StringKey.WalkDetailDeleteContentDescription to "Ջնջել զբոսանքը",
    StringKey.WalkDetailShareAction to "Կիսվել",
    StringKey.WalkDetailDeleteAction to "Ջնջել",
    StringKey.WalkDetailDeleteConfirmTitle to "Ջնջե՞լ զբոսանքը",
    StringKey.WalkDetailDeleteConfirmMessage to
        "Զբոսանքը և նրա բոլոր գտածոները կջնջվեն ընդմիշտ։ Այս գործողությունն անշրջելի է։",
    StringKey.WalkDetailDeleteConfirmYes to "Այո",
    StringKey.WalkDetailDeleteConfirmNo to "Ոչ",
    StringKey.WalkDetailMushroomsCountZero to "սունկ",
    StringKey.WalkDetailMushroomsCountOne to "սունկ",
    StringKey.WalkDetailMushroomsCountTwo to "սունկ",
    StringKey.WalkDetailMushroomsCountFew to "սունկ",
    StringKey.WalkDetailMushroomsCountMany to "սունկ",
    StringKey.WalkDetailMushroomsCountOther to "սունկ",
    StringKey.WalkDetailDescriptionTitle to "Նկարագրություն",
    StringKey.WalkDetailDescriptionEmpty to "Նկարագրություն ավելացված չէ",
    StringKey.WalkDetailDescriptionHint to "Նկարագրեք զբոսանքը",
    StringKey.WalkDetailEditDescriptionContentDescription to "Խմբագրել նկարագրությունը",
    StringKey.WalkDetailDescriptionCancelContentDescription to "Չեղարկել",
    StringKey.WalkDetailDescriptionSaveContentDescription to "Պահպանել",

    StringKey.WalkShareContentDescription to "Կիսվել զբոսանքով",
    StringKey.WalkShareDialogTitle to "Կիսվել",
    StringKey.WalkShareOptionName to "Զբոսանքի անվանումը",
    StringKey.WalkShareOptionStats to "Զբոսանքի վիճակագրությունը",
    StringKey.WalkShareOptionDescription to "Զբոսանքի նկարագրությունը",
    StringKey.WalkShareOptionDiagram to "Գտածոների գծապատկերը",
    StringKey.WalkShareOptionMap to "Քարտեզ նշումներով",
    StringKey.WalkShareMapWarning to "Ուրիշները կկարողանան տեսնել, թե որտեղ եք սունկ գտել",
    StringKey.WalkShareCancelButton to "Չեղարկել",
    StringKey.WalkShareConfirmButton to "Կիսվել",
    StringKey.WalkShareFooter to "Ստեղծված է «Սնկերի քարտեզ Լեշիից» հավելվածով",
    StringKey.WalkShareImageFooter to "Ստեղծված է «Սնկերի քարտեզ Լեշիից» հավելվածում",

    StringKey.MapStatsTitle to "Վիճակագրություն",
    StringKey.MapStatsWalksCount to "Զբոսանքներ",
    StringKey.MapStatsFindsCount to "Գտնված սնկեր",
    StringKey.MapStatsEmptyHint to "Վիճակագրությունը կհավաքվի ինքնաբերաբար, հենց որ գրանցվի առաջին զբոսանքը։",
    StringKey.MapFilterButtonLabel to "Զտիչներ",
    StringKey.MapFilterDialogTitle to "Կարգավորեք քարտեզի սնկերի վրա կիրառվող զտիչները՝",
    StringKey.MapFilterBackContentDescription to "Հետ",
    StringKey.MapFilterDateRangeTitle to "Ամսաթվերի միջակայք",
    StringKey.MapFilterMonthRangeTitle to "Սեզոն",
    StringKey.MapFilterPastRoutesTitle to "Անցյալ երթուղիների ցուցադրում",
    StringKey.MapFilterShowPastRoutes to "Ցույց տալ անցյալ երթուղիները",

    StringKey.MonthJanuary to "Հունվար",
    StringKey.MonthFebruary to "Փետրվար",
    StringKey.MonthMarch to "Մարտ",
    StringKey.MonthApril to "Ապրիլ",
    StringKey.MonthMay to "Մայիս",
    StringKey.MonthJune to "Հունիս",
    StringKey.MonthJuly to "Հուլիս",
    StringKey.MonthAugust to "Օգոստոս",
    StringKey.MonthSeptember to "Սեպտեմբեր",
    StringKey.MonthOctober to "Հոկտեմբեր",
    StringKey.MonthNovember to "Նոյեմբեր",
    StringKey.MonthDecember to "Դեկտեմբեր",

    StringKey.BackgroundRecordingChannelName to "Զբոսանքի գրանցում",
    StringKey.BackgroundRecordingNotificationTitle to "Ձեր զբոսանքը գրանցվում է",
    StringKey.BackgroundRecordingNotificationText to
        "Երթուղին գրանցվում է ֆոնային ռեժիմում։ Հպեք՝ հավելված վերադառնալու համար։",

    StringKey.DataExportOption to "Արտահանում",
    StringKey.DataImportOption to "Ներմուծում",
    StringKey.DataArchiveNameLabel to "Արխիվի անվանումը",
    StringKey.DataChooseFileButton to "Ընտրել ֆայլ",
    StringKey.DataFileStatusLabel to "Ներմուծման ֆայլ",
    StringKey.DataFileNotSelected to "ընտրված չէ",
    StringKey.DataImportLabelFieldLabel to "Ներմուծված զբոսանքների անուններին ավելացվող նշում",
    StringKey.DataDoneButton to "Պատրաստ է",
    StringKey.DataSavedButton to "Պահպանված է",
    StringKey.DataGoToArchiveButton to "Դեպի արխիվ",
    StringKey.DataCancelButton to "Չեղարկել",
    StringKey.DataProcessingLabel to "Մշակվում է…",
    StringKey.DataExportSuccessMessage to "Արխիվը հաջողությամբ պահպանվեց",
    StringKey.DataImportedWalksLabel to "Ներմուծված զբոսանքներ",
    StringKey.DataImportFailedWalksLabel to "Չհաջողվեց ներմուծել",
    StringKey.DataErrorLabel to "Սխալ",
    StringKey.DataImportRejectedTitle to "Այս ֆայլը հնարավոր չէ ներմուծել",
    StringKey.DataImportRejectedNotArchive to
        "Սա արխիվ չէ. ֆայլը չի ընթերցվում որպես ZIP։ Ընտրեք Լեշիից արտահանված արխիվ։",
    StringKey.DataImportRejectedNotLeshy to
        "Սա ZIP արխիվ է, բայց ոչ Լեշիինը. դրա մեջ manifest.json չկա։",
    StringKey.DataImportRejectedNewerFormat to
        "Արխիվը գրվել է հավելվածի ավելի նոր տարբերակով։ Թարմացրեք հավելվածը և կրկին փորձեք։",
    StringKey.DataImportRejectedDamaged to
        "Արխիվը վնասված է. բովանդակության մի մասը չի ընթերցվում։ Ոչինչ չի ներմուծվել։",
    StringKey.DataImportRejectedNoWalks to "Արխիվում զբոսանքներ չկան — ներմուծելու բան չկա։",
    StringKey.DataChooseWalksTitle to "Արտահանվող զբոսանքները",
    StringKey.DataWalksBackContentDescription to "Հետ՝ առանց ընտրությունը պահպանելու",
    StringKey.DataWalksConfirmContentDescription to "Հաստատել ընտրությունը",
    StringKey.DataWalksSelectedLabel to "Ընտրված է",
    StringKey.DataWalksCountZero to "զբոսանք",
    StringKey.DataWalksCountOne to "զբոսանք",
    StringKey.DataWalksCountTwo to "զբոսանք",
    StringKey.DataWalksCountFew to "զբոսանք",
    StringKey.DataWalksCountMany to "զբոսանք",
    StringKey.DataWalksCountOther to "զբոսանք",

    StringKey.PreparationSelectAreaButton to "Ներբեռնել տեսանելի տարածքը",
    StringKey.PreparationDownloadThisAreaButton to "Ներբեռնել այս տարածքը",
    StringKey.PreparationRegionNameDialogTitle to "Տարածքի անվանումը",
    StringKey.PreparationRegionNameLabel to "Օրինակ՝ գյուղի մոտի անտառը",
    StringKey.PreparationSaveButton to "Ներբեռնել",
    StringKey.PreparationCancelButton to "Չեղարկել",
    StringKey.PreparationDeleteConfirmTitle to "Ջնջե՞լ տարածքը",
    StringKey.PreparationDeleteConfirmMessage to "Ներբեռնված քարտեզի հատվածները կջնջվեն ընդմիշտ։",
    StringKey.PreparationDeleteConfirmYes to "Այո",
    StringKey.PreparationDeleteConfirmNo to "Ոչ",
    StringKey.PreparationDeleteContentDescription to "Ջնջել տարածքը",
    StringKey.PreparationPauseContentDescription to "Դադարեցնել ներբեռնումը",
    StringKey.PreparationResumeContentDescription to "Շարունակել ներբեռնումը",
    StringKey.PreparationStatusDownloading to "Ներբեռնվում է",
    StringKey.PreparationStatusPaused to "Դադարեցված է",
    StringKey.PreparationStatusComplete to "Ներբեռնված է",
    StringKey.PreparationStatusError to "Սխալ",
    StringKey.PreparationSubtitle to
        "Ներբեռնեք քարտեզի տեսանելի հատվածը՝ այն առանց ինտերնետի օգտագործելու համար",
    StringKey.PreparationRetryContentDescription to "Կրկնել ներբեռնումը",

    StringKey.MapTilesLoadFailed to "Քարտեզը լիովին չբեռնվեց՝",
    StringKey.MapTilesLoadFailedDismissContentDescription to "Փակել ծանուցումը",

    StringKey.SettingsMapDataTitle to "Քարտեզի տվյալներ",
    StringKey.SettingsRefreshMapDataButton to "Թարմացնել քարտեզի տվյալները",
    StringKey.SettingsMapDataUpdateConfirmTitle to "Թարմացնե՞լ քարտեզի տվյալները",
    StringKey.SettingsMapDataUpdateConfirmMessage to
        "Եթե քարտեզի բովանդակությունը փոխվել է, բոլոր ներբեռնված օֆլայն տարածքները կներբեռնվեն " +
            "կրկին։ Վստա՞հ եք, որ ուզում եք թարմացնել քարտեզի տվյալները։",
    StringKey.SettingsMapDataUpdateConfirmYes to "Այո",
    StringKey.SettingsMapDataUpdateConfirmNo to "Ոչ",
    StringKey.SettingsMapDataRefreshError to "Թարմացումը չհաջողվեց — ստուգեք ինտերնետ կապը",
    StringKey.SettingsMapDataRedownloadingPrefix to "Քարտեզի տվյալները թարմացվեցին։ Կրկին ներբեռնվում է՝",
    StringKey.SettingsMapDataRedownloadingSuffix to
        "— ընթացքը հետևեք Նախնական ներբեռնում բաժնում։",
    StringKey.SettingsMapDataRegionsCountZero to "տարածք",
    StringKey.SettingsMapDataRegionsCountOne to "տարածք",
    StringKey.SettingsMapDataRegionsCountTwo to "տարածք",
    StringKey.SettingsMapDataRegionsCountFew to "տարածք",
    StringKey.SettingsMapDataRegionsCountMany to "տարածք",
    StringKey.SettingsMapDataRegionsCountOther to "տարածք",
    StringKey.SettingsClearMapCacheButton to "Մաքրել քարտեզի քեշը",
    StringKey.SettingsClearMapCacheConfirmTitle to "Մաքրե՞լ քարտեզի քեշը",
    StringKey.SettingsClearMapCacheConfirmMessage to
        "Քարտեզի քեշի մաքրումը հեռացնում է դիտված քարտեզի այն տարածքները, որոնք չեն պահպանվել " +
            "Նախնական ներբեռնում բաժնում։ Վստա՞հ եք, որ ուզում եք մաքրել քեշը։",
    StringKey.SettingsClearMapCacheConfirmYes to "Այո",
    StringKey.SettingsClearMapCacheConfirmNo to "Ոչ",
    StringKey.SettingsMapCacheCleared to "Քեշը մաքրվեց",
)
