package leshy.mushrooms.map.i18n.strings

import leshy.mushrooms.map.i18n.StringKey

/** Georgian — Phase 11 of `.claude/plans/countries-and-languages.md`. `pluralCategory` gives `ka`
 * the two-way split (`One` at n = 1, else `Other`), but **a Georgian noun counted by a numeral
 * stays in the singular** — "1 სოკო", "5 სოკო", never a plural — so all six forms of each unit
 * carry the same word. Deliberate, and the same situation Phase 8 documented for `tr` and
 * `ja`/`ko`: the counted noun genuinely never changes shape.
 *
 * Georgian has no letter case, so titles, buttons and labels are all lowercase by nature — that is
 * correct typography for the script, not missing capitalisation. */
internal val georgianStrings: Map<StringKey, String> = mapOf(
    StringKey.AppName to "ლეშის სოკოს რუკა",
    StringKey.NavRecord to "ახალი ჩანაწერი",
    StringKey.NavArchive to "გასეირნებების არქივი",
    StringKey.NavMap to "აღმოჩენების რუკა",
    StringKey.NavData to "ექსპორტი/იმპორტი",
    StringKey.NavPreparation to "წინასწარი ჩამოტვირთვა",
    StringKey.NavSpecies to "ჩემი სოკოები",
    StringKey.SettingsTitle to "პარამეტრები",
    StringKey.SettingsContentDescription to "პარამეტრები",
    StringKey.SettingsLanguageTitle to "ინტერფეისის ენა",
    StringKey.SettingsThemeTitle to "იერსახე",
    StringKey.SettingsThemeLight to "ღია",
    StringKey.SettingsThemeDark to "მუქი",
    StringKey.SettingsThemeSystem to "სისტემური",
    StringKey.SettingsCategoriesTitle to "მოსანიშნი სოკოები",
    StringKey.SettingsMushroomSizeTitle to "დაარეგულირეთ სოკოების ზომა რუკაზე",
    StringKey.SettingsMushroomSortTitle to "სოკოების თანმიმდევრობა",
    StringKey.SettingsResetMushroomOrderOnWalkFinish to
        "სოკოების თანმიმდევრობის აღდგენა გასეირნების ბოლოს",
    StringKey.SettingsFreezeMushroomOrder to "სოკოების უცვლელი თანმიმდევრობა",

    StringKey.MushroomImagesDisclaimer to
        "აპლიკაციაში სოკოების ყველა გამოსახულება მხოლოდ საილუსტრაციოა — არ გამოიყენოთ ისინი " +
            "უცნობი სოკოების დასადგენად!",

    StringKey.SpeciesCollectionsTitle to "სოკოების კრებულები",
    StringKey.SpeciesMyMushroomsTitle to "დამატებული სოკოები",
    StringKey.SpeciesMyMushroomsEmpty to "აქ გამოჩნდება სოკოები, რომლებსაც თავად დაამატებთ",
    StringKey.SpeciesAddButton to "სოკოს დამატება",
    StringKey.SpeciesFormTitleCreate to "ახალი სოკო",
    StringKey.SpeciesFormTitleEdit to "სოკოს რედაქტირება",
    StringKey.SpeciesFormNameHint to "სახელი",
    StringKey.SpeciesFormScientificNameHint to "სამეცნიერო სახელი",
    StringKey.SpeciesFormColorLabel to "ფერი",
    StringKey.SpeciesFormTakePhotoButton to "კამერა",
    StringKey.SpeciesFormPickPhotoButton to "გალერეა",
    StringKey.SpeciesFormPickCatalogButton to "სურათები",
    StringKey.SpeciesFormSaveButton to "შენახვა",
    StringKey.SpeciesFormCancelContentDescription to "გაუქმება",
    StringKey.SpeciesListImportedLabel to "არქივიდან",
    StringKey.SpeciesListEditContentDescription to "რედაქტირება",
    StringKey.SpeciesListDeleteContentDescription to "სახეობის წაშლა",
    StringKey.SpeciesDeleteConfirmTitle to "წავშალოთ ეს სოკო?",
    StringKey.SpeciesDeleteConfirmMessage to
        "ნამდვილად გსურთ ამ სახეობის წაშლა? ამ სახეობის ყველა ნიშანი გასეირნებებში გადავა " +
            "კატეგორიაში „უცნობი სოკო“. ამ მოქმედების გაუქმება შეუძლებელია.",
    StringKey.SpeciesDeleteConfirmYes to "დიახ",
    StringKey.SpeciesDeleteConfirmNo to "არა",

    StringKey.CatalogPhotoPickerTitle to "აირჩიეთ სურათი",

    StringKey.IconEditorTitle to "ფოტორედაქტორი",
    StringKey.IconEditorToolEraser to "საშლელი",
    StringKey.IconEditorToolCrop to "ჩამოჭრა",
    StringKey.IconEditorShapeRectangle to "მართკუთხედი",
    StringKey.IconEditorShapeOval to "ოვალი",
    StringKey.IconEditorBrushSizeLabel to "ფუნჯის ზომა",
    StringKey.IconEditorUndoContentDescription to "დაბრუნება",
    StringKey.IconEditorRedoContentDescription to "გამეორება",
    StringKey.IconEditorDoneContentDescription to "მზადაა",

    StringKey.OnboardingTitle to "მოგესალმებით!",
    StringKey.OnboardingDescription to
        "აირჩიეთ სოკოების კრებულები, რომლებიც გაინტერესებთ. ამის შეცვლა მოგვიანებით " +
            "პარამეტრებში შეგიძლიათ.",
    StringKey.OnboardingContinueButton to "დაწყება",
    StringKey.OnboardingNothingPickedWarning to
        "გასაგრძელებლად აირჩიეთ სულ მცირე ერთი კრებული ან ერთი სოკო",

    StringKey.WelcomeIntro to
        "აპლიკაცია იმახსოვრებს, სად გაიარეთ და რა იპოვეთ — და სოკოს კრეფაში ნამდვილად გეხმარებათ: კარგ " +
            "ადგილებზე ადვილად დაბრუნდებით, ყველა ნაპოვნი კი ერთ რუკაზე ჩანს.",
    StringKey.WelcomeRecordTitle to "ჩაიწერეთ თქვენი გასეირნება",
    StringKey.WelcomeRecordText to
        "მარშრუტს, დროსა და კილომეტრებს აპლიკაცია თავად აწარმოებს. იპოვეთ სოკო — მონიშნეთ მისი ფილაკის " +
            "შეხებით; წყარო, წაქცეული ხე ან მანქანა პირდაპირ რუკაზე მოინიშნება.",
    StringKey.WelcomeArchiveTitle to "დაუბრუნდით თქვენს ნაპოვნებს",
    StringKey.WelcomeArchiveText to
        "არქივში ყოველი გასეირნება ცალკე დევს — თავისი მარშრუტითა და ნაპოვნებით. საერთო რუკა კი ყველას " +
            "ერთად აჩვენებს: სად, რა და რამდენი მოიძებნა ყველა სეზონის განმავლობაში.",
    StringKey.WelcomeHelpTitle to "დარწმუნებული არ ხართ — დააჭირეთ „?“-ს",
    StringKey.WelcomeHelpText to
        "ღილაკი „?“ ზემოთ მარჯვნივ ყველა განყოფილებაშია და განმარტავს, როგორ არის ეს განყოფილება მოწყობილი.",
    StringKey.WelcomeMenuTitle to "დანარჩენი — მენიუშია",
    StringKey.WelcomeMenuText to
        "ზემოთ მარცხნივ მენიუს ღილაკი ხსნის აპლიკაციის ყველა განყოფილებისა და შესაძლებლობის სიას.",
    StringKey.WelcomeConsentTitle to "გამოყენებამდე",
    StringKey.WelcomeConsentIntro to "თავად აპლიკაციაზე გადასვლამდე საჭიროა დაეთანხმოთ შემდეგ დებულებებს:",
    StringKey.WelcomeConsentImages to
        "თქვენ არ შეეცდებით სოკოს ამოცნობას აპლიკაციის სურათებით. სურათები ილუსტრაციის როლს ასრულებს და არ არის " +
            "შემოწმებული განმსაზღვრელი.",
    StringKey.WelcomeConsentEating to
        "თქვენ არავითარ შემთხვევაში არ შეჭამთ იმ სოკოს, რომელსაც არ იცნობთ. სოკო შეიძლება იყოს უჭმელი, შეიძლება " +
            "იყოს შხამიანიც. საუკეთესოა — მოიწვიოთ ვინმე, ვინც კარგად იცნობს თქვენი მხარის სოკოს, რომ გაიგოთ, " +
            "რომელი სოკოს კრეფა შეიძლება და როგორ უნდა მოამზადოთ ისინი ამის შემდეგ.",
    StringKey.WelcomeConsentWarning to
        "გასაგრძელებლად საჭიროა დაეთანხმოთ ზემოთ მოცემულ დებულებებს და მონიშნოთ ის დებულებები, რომლებსაც ეთანხმებით",

    StringKey.WelcomeNextButton to "შემდეგ",

    StringKey.LegalTitle to "კონფიდენციალურობა",
    StringKey.LegalPrivacyText to
        "თქვენი გასეირნებები, აღნიშვნები და ფოტოები რჩება თქვენს მოწყობილობაზე. აპლიკაცია ანგარიშებს არ " +
            "ქმნის და თქვენს მონაცემებს არსად აგზავნის — ინტერნეტში მიდის მხოლოდ რუკის ფრაგმენტების " +
            "მოთხოვნები openfreemap.org-ისკენ.",
    StringKey.LegalPrivacyLink to "კონფიდენციალურობის პოლიტიკა",

    StringKey.AboutTitle to "აპლიკაციის შესახებ",
    StringKey.AboutMapDataTitle to "რუკის მონაცემები",
    StringKey.AboutMapDataText to
        "რუკა აგებულია OpenStreetMap-ის მონაცემებზე, რომლებიც ვრცელდება ODbL ლიცენზიით. ვექტორული " +
            "ფრაგმენტები და სტილი — OpenMapTiles-ისგან, მიწოდება — სერვისი OpenFreeMap.",
    StringKey.AboutOpenSourceTitle to "ღია კოდი",
    StringKey.AboutOpenSourceText to
        "აპლიკაცია აწყობილია ღია კოდის ბიბლიოთეკებისგან. სიის სტრიქონზე შეხება ხსნის მისი ლიცენზიის სრულ " +
            "ტექსტს.",

    StringKey.NavMenuContentDescription to "მენიუ",
    StringKey.HelpContentDescription to "დახმარება",
    StringKey.HelpDialogTitle to "დახმარება",
    StringKey.HelpDialogDismiss to "გასაგებია",

    StringKey.CategoryMisc to "სხვადასხვა",
    StringKey.CategoryUnknownMushroom to "უცნობი სოკო",

    StringKey.CollectionPickerSearchHint to "ქვეყნის ან სოკოს ძიება",
    StringKey.CollectionPickerMoreMatches to "ყველა შედეგი არ არის ნაჩვენები — დააზუსტეთ ძიება",

    StringKey.LanguagePickerSearchHint to "ენის ძიება",
    StringKey.LanguagePickerBackContentDescription to "უკან",
    StringKey.LanguagePickerConfirmContentDescription to "დადასტურება",

    StringKey.DefaultWalkName to "გასეირნება",
    StringKey.RecordWalkNameHint to "გასეირნების სახელი",
    StringKey.RecordStart to "დაწყება",
    StringKey.RecordPause to "პაუზა",
    StringKey.RecordResume to "გაგრძელება",
    StringKey.RecordFinish to "დასრულება",
    StringKey.RecordSetWalkNameTitle to "მიუთითეთ გასეირნების სახელი:",
    StringKey.RecordDefaultWalkNamePrefix to "გასეირნება",
    StringKey.RecordConfirmWalkNameContentDescription to "დადასტურება",
    StringKey.RecordMarkLocationContentDescription to "ადგილის მონიშვნა",
    StringKey.RecordLocationUnavailable to "მდებარეობა მიუწვდომელია — მარშრუტი არ იწერება. ჩართეთ გეოლოკაცია და მიეცით აპლიკაციას წვდომა მოწყობილობის პარამეტრებში.",
    StringKey.RecordLocationUnknownMessage to
        "მდებარეობა ჯერ არ არის ცნობილი — ნიშნულს მიბმის ადგილი არ აქვს. შეამოწმეთ, ჩართულია თუ არა გეოლოკაცია, და დაელოდეთ სიგნალს.",
    StringKey.RecordSearchContentDescription to "ძიება",
    StringKey.RecordSearchDialogTitle to "აირჩიეთ საჭირო სოკო",
    StringKey.RecordBulkAddQuestion to "რამდენი ახალი სოკო იპოვეთ?",
    StringKey.RecordBulkAddCancelContentDescription to "გაუქმება",

    StringKey.RecordBulkAddConfirmContentDescription to "დადასტურება",
    StringKey.RecordBulkAddLimitMessage to
        "ერთ გასეირნებაზე მაქსიმუმ 999 ერთი და იმავე სახეობის აღმოჩენა.",
    StringKey.DialogAcknowledge to "გასაგებია",

    StringKey.NavigationDirectionToPrefix to "მიმართულება:",
    StringKey.NavigationDistanceToTargetPrefix to "მიზნამდე",
    StringKey.NavigationMetersSuffix to "მეტრი",
    StringKey.NavigationKeepRightPhrase to "აიღეთ მარჯვნივ",
    StringKey.NavigationKeepLeftPhrase to "აიღეთ მარცხნივ",
    StringKey.NavigationGoStraightPhrase to "იარეთ პირდაპირ",
    StringKey.NavigationDeterminingDirection to "მიმართულებას ვადგენთ…",
    StringKey.NavigationArrivedPhrase to "მიაღწიეთ ადგილს",
    StringKey.NavigationCloseContentDescription to "დახურვა",

    StringKey.AddPlaceTitle to "დაამატეთ ადგილი",
    StringKey.AddPlaceEditTitle to "დაარედაქტირეთ ადგილი",
    StringKey.AddPlaceDefaultName to "ადგილი",
    StringKey.AddPlaceNameHint to "ადგილის სახელი",
    StringKey.AddPlacePhotoContentDescription to "გადაღება",
    StringKey.CameraPermissionDenied to "კამერაზე წვდომა არ არის. მიეცით აპლიკაციას ნებართვა მოწყობილობის პარამეტრებში.",
    StringKey.AddPlaceDescriptionTitle to "აღწერა",
    StringKey.AddPlaceDescriptionHint to "აღწერეთ ადგილი",
    StringKey.AddPlaceCoordinatesTitle to "კოორდინატები",
    StringKey.AddPlaceCopyCoordinatesContentDescription to "კოორდინატების კოპირება",
    StringKey.AddPlaceSaveContentDescription to "ადგილის შენახვა",
    StringKey.AddPlaceDiscardContentDescription to "ადგილის წაშლა",

    StringKey.PlaceViewEditContentDescription to "ადგილის რედაქტირება",
    StringKey.PlaceViewDeleteContentDescription to "ადგილის წაშლა",
    StringKey.PlaceDeleteConfirmTitle to "წავშალოთ ადგილი?",
    StringKey.PlaceDeleteConfirmMessage to
        "ადგილი სამუდამოდ წაიშლება. მისი აღდგენა შეუძლებელი იქნება.",
    StringKey.PlaceDeleteConfirmYes to "დიახ",
    StringKey.PlaceDeleteConfirmNo to "არა",

    StringKey.ArchiveEmpty to "გასეირნებები ჯერ არ არის",
    StringKey.ArchiveEmptyHint to
        "აქ იქნება ჩაწერილი გასეირნებები: მარშრუტი, ნაპოვნი სოკოები და მონიშნული ადგილები.",
    StringKey.EmptyStartWalkButton to "გასეირნების დაწყება",
    StringKey.ArchiveDeleteWalksButton to "გასეირნებების წაშლა",
    StringKey.ArchiveDeleteConfirmMessage to
        "ნამდვილად გსურთ არჩეული გასეირნებების სამუდამოდ წაშლა?",
    StringKey.ArchiveDeleteConfirmYes to "დიახ",
    StringKey.ArchiveDeleteConfirmNo to "არა",
    StringKey.WalkDetailStartTime to "დაწყება",
    StringKey.WalkDetailEndTime to "დასრულება",
    StringKey.WalkDetailInProgress to "არ დასრულებულა",
    StringKey.WalkDetailDistance to "გავლილი გზა",
    StringKey.WalkDetailDuration to "ხანგრძლივობა",
    StringKey.WalkDetailAvgSpeed to "საშუალო სიჩქარე",
    StringKey.WalkDetailDurationDays to "დღ",
    StringKey.WalkDetailDurationHours to "სთ",
    StringKey.WalkDetailDurationMinutes to "წთ",
    StringKey.WalkCardDurationHours to "სთ",
    StringKey.WalkCardDurationMinutes to "წთ",
    StringKey.UnitKilometers to "კმ",
    StringKey.UnitKmh to "კმ/სთ",
    StringKey.UnitMegabytes to "მბ",
    StringKey.WalkDetailFindsTitle to "აღმოჩენები სახეობების მიხედვით",
    StringKey.WalkDetailFindsEmpty to "აღმოჩენები არ დაფიქსირებულა",
    StringKey.WalkDetailPlacesTitle to "მონიშნული ადგილები",
    StringKey.WalkDetailViewMap to "რუკის ნახვა",
    StringKey.WalkDetailEditContentDescription to "გასეირნების სახელის რედაქტირება",
    StringKey.WalkDetailEditWalkNameTitle to "შეცვალეთ გასეირნების სახელი:",
    StringKey.WalkDetailConfirmEditWalkNameContentDescription to "დადასტურება",
    StringKey.WalkDetailDeleteContentDescription to "გასეირნების წაშლა",
    StringKey.WalkDetailShareAction to "გაზიარება",
    StringKey.WalkDetailDeleteAction to "წაშლა",
    StringKey.WalkDetailDeleteConfirmTitle to "წავშალოთ გასეირნება?",
    StringKey.WalkDetailDeleteConfirmMessage to
        "გასეირნება და ყველა აღმოჩენა სამუდამოდ წაიშლება. მათი აღდგენა შეუძლებელი იქნება.",
    StringKey.WalkDetailDeleteConfirmYes to "დიახ",
    StringKey.WalkDetailDeleteConfirmNo to "არა",
    StringKey.WalkDetailMushroomsCountZero to "სოკო",
    StringKey.WalkDetailMushroomsCountOne to "სოკო",
    StringKey.WalkDetailMushroomsCountTwo to "სოკო",
    StringKey.WalkDetailMushroomsCountFew to "სოკო",
    StringKey.WalkDetailMushroomsCountMany to "სოკო",
    StringKey.WalkDetailMushroomsCountOther to "სოკო",
    StringKey.WalkDetailDescriptionTitle to "აღწერა",
    StringKey.WalkDetailDescriptionEmpty to "აღწერა არ არის დამატებული",
    StringKey.WalkDetailDescriptionHint to "აღწერეთ გასეირნება",
    StringKey.WalkDetailEditDescriptionContentDescription to "აღწერის რედაქტირება",
    StringKey.WalkDetailDescriptionCancelContentDescription to "გაუქმება",
    StringKey.WalkDetailDescriptionSaveContentDescription to "შენახვა",

    StringKey.WalkShareContentDescription to "გასეირნების გაზიარება",
    StringKey.WalkShareDialogTitle to "გაზიარება",
    StringKey.WalkShareOptionName to "გასეირნების სახელი",
    StringKey.WalkShareOptionStats to "გასეირნების სტატისტიკა",
    StringKey.WalkShareOptionDescription to "გასეირნების აღწერა",
    StringKey.WalkShareOptionDiagram to "აღმოჩენების დიაგრამა",
    StringKey.WalkShareOptionMap to "რუკა ნიშნებით",
    StringKey.WalkShareMapWarning to "სხვები დაინახავენ, სად იპოვეთ სოკოები",
    StringKey.WalkShareCancelButton to "გაუქმება",
    StringKey.WalkShareConfirmButton to "გაზიარება",
    StringKey.WalkShareFooter to "შექმნილია აპლიკაციით „ლეშის სოკოს რუკა“",
    StringKey.WalkShareImageFooter to "შექმნილია აპლიკაციაში ლეშის სოკოს რუკა",

    StringKey.MapStatsTitle to "სტატისტიკა",
    StringKey.MapStatsWalksCount to "გასეირნებები",
    StringKey.MapStatsFindsCount to "ნაპოვნი სოკოები",
    StringKey.MapStatsEmptyHint to "სტატისტიკა თავად შეგროვდება, როგორც კი პირველი გასეირნება ჩაიწერება.",

    StringKey.MapFilterButtonLabel to "ფილტრები",
    StringKey.MapFilterDialogTitle to
        "დააყენეთ ფილტრები, რომლებიც რუკაზე სოკოებს მიესადაგება:",
    StringKey.MapFilterBackContentDescription to "უკან",
    StringKey.MapFilterDateRangeTitle to "თარიღების დიაპაზონი",
    StringKey.MapFilterMonthRangeTitle to "სეზონი",
    StringKey.MapFilterPastRoutesTitle to "წარსული მარშრუტების ჩვენება",
    StringKey.MapFilterShowPastRoutes to "ნაჩვენები იყოს წარსული მარშრუტები",

    StringKey.MonthJanuary to "იანვარი",
    StringKey.MonthFebruary to "თებერვალი",
    StringKey.MonthMarch to "მარტი",
    StringKey.MonthApril to "აპრილი",
    StringKey.MonthMay to "მაისი",
    StringKey.MonthJune to "ივნისი",
    StringKey.MonthJuly to "ივლისი",
    StringKey.MonthAugust to "აგვისტო",
    StringKey.MonthSeptember to "სექტემბერი",
    StringKey.MonthOctober to "ოქტომბერი",
    StringKey.MonthNovember to "ნოემბერი",
    StringKey.MonthDecember to "დეკემბერი",

    StringKey.BackgroundRecordingChannelName to "გასეირნების ჩაწერა",
    StringKey.BackgroundRecordingNotificationTitle to "მიმდინარეობს გასეირნების ჩაწერა",
    StringKey.BackgroundRecordingNotificationText to
        "მარშრუტი იწერება ფონურ რეჟიმში. შეეხეთ აპლიკაციაში დასაბრუნებლად.",

    StringKey.DataExportOption to "ექსპორტი",
    StringKey.DataImportOption to "იმპორტი",
    StringKey.DataArchiveNameLabel to "არქივის სახელი",
    StringKey.DataChooseFileButton to "ფაილის არჩევა",
    StringKey.DataFileStatusLabel to "იმპორტის ფაილი",
    StringKey.DataFileNotSelected to "არ არის არჩეული",
    StringKey.DataImportLabelFieldLabel to "მინაწერი გასეირნებების სახელებთან",
    StringKey.DataDoneButton to "მზადაა",
    StringKey.DataSavedButton to "შენახულია",
    StringKey.DataGoToArchiveButton to "არქივში",
    StringKey.DataCancelButton to "გაუქმება",
    StringKey.DataProcessingLabel to "მიმდინარეობს დამუშავება…",
    StringKey.DataExportSuccessMessage to "არქივი წარმატებით შეინახა",
    StringKey.DataImportedWalksLabel to "იმპორტირებული გასეირნებები",
    StringKey.DataImportFailedWalksLabel to "იმპორტი ვერ მოხერხდა",
    StringKey.DataErrorLabel to "შეცდომა",
    StringKey.DataImportRejectedTitle to "ამ ფაილის იმპორტი შეუძლებელია",
    StringKey.DataImportRejectedNotArchive to "ეს არ არის არქივი: ფაილი ვერ იკითხება როგორც ZIP. აირჩიეთ Leshy-დან ექსპორტირებული არქივი.",
    StringKey.DataImportRejectedNotLeshy to "ეს ZIP არქივია, მაგრამ არა Leshy-ის: მასში არ არის manifest.json.",
    StringKey.DataImportRejectedNewerFormat to "არქივი შექმნილია აპლიკაციის უფრო ახალი ვერსიით. განაახლეთ აპლიკაცია და სცადეთ ხელახლა.",
    StringKey.DataImportRejectedDamaged to "არქივი დაზიანებულია: შიგთავსის ნაწილი ვერ იკითხება. არაფერი იმპორტირებულა.",
    StringKey.DataImportRejectedNoWalks to "არქივში არც ერთი გასეირნება არ არის — იმპორტისთვის არაფერია.",
    StringKey.DataChooseWalksTitle to "გასეირნებები არქივისთვის",
    StringKey.DataWalksBackContentDescription to "უკან არჩევანის შენახვის გარეშე",
    StringKey.DataWalksConfirmContentDescription to "არჩევანის დადასტურება",
    StringKey.DataWalksSelectedLabel to "არჩეულია",
    StringKey.DataWalksCountZero to "გასეირნება",
    StringKey.DataWalksCountOne to "გასეირნება",
    StringKey.DataWalksCountTwo to "გასეირნება",
    StringKey.DataWalksCountFew to "გასეირნება",
    StringKey.DataWalksCountMany to "გასეირნება",
    StringKey.DataWalksCountOther to "გასეირნება",
    StringKey.PreparationSelectAreaButton to "ხილული ტერიტორიის ჩამოტვირთვა",
    StringKey.PreparationDownloadThisAreaButton to "ამ ტერიტორიის ჩამოტვირთვა",
    StringKey.PreparationRegionNameDialogTitle to "ტერიტორიის სახელი",
    StringKey.PreparationRegionNameLabel to "მაგალითად: ტყე სოფელთან",
    StringKey.PreparationSaveButton to "ჩამოტვირთვა",
    StringKey.PreparationCancelButton to "გაუქმება",
    StringKey.PreparationDeleteConfirmTitle to "წავშალოთ ტერიტორია?",
    StringKey.PreparationDeleteConfirmMessage to
        "ჩამოტვირთული რუკის ფრაგმენტები სამუდამოდ წაიშლება.",
    StringKey.PreparationDeleteConfirmYes to "დიახ",
    StringKey.PreparationDeleteConfirmNo to "არა",
    StringKey.PreparationDeleteContentDescription to "ტერიტორიის წაშლა",
    StringKey.PreparationPauseContentDescription to "ჩამოტვირთვის შეჩერება",
    StringKey.PreparationResumeContentDescription to "ჩამოტვირთვის გაგრძელება",
    StringKey.PreparationStatusDownloading to "ჩამოიტვირთება",
    StringKey.PreparationStatusPaused to "შეჩერებულია",
    StringKey.PreparationStatusComplete to "ჩამოტვირთულია",
    StringKey.PreparationStatusError to "შეცდომა",
    StringKey.PreparationSubtitle to
        "ჩამოტვირთეთ რუკის ხილული ტერიტორია, რომ ინტერნეტის გარეშე გამოიყენოთ",
    StringKey.PreparationRetryContentDescription to "ჩამოტვირთვის გამეორება",

    StringKey.MapTilesLoadFailed to "რუკა სრულად არ ჩაიტვირთა აქედან:",
    StringKey.MapTilesLoadFailedDismissContentDescription to "შეტყობინების დახურვა",

    StringKey.SettingsMapDataTitle to "რუკის მონაცემები",
    StringKey.SettingsRefreshMapDataButton to "რუკის მონაცემების განახლება",
    StringKey.SettingsMapDataUpdateConfirmTitle to "განვაახლოთ რუკის მონაცემები?",
    StringKey.SettingsMapDataUpdateConfirmMessage to
        "თუ რუკის შიგთავსი შეიცვალა, ყველა ჩამოტვირთული ოფლაინ ტერიტორია თავიდან " +
            "ჩამოიტვირთება. ნამდვილად გსურთ რუკის მონაცემების განახლება?",
    StringKey.SettingsMapDataUpdateConfirmYes to "დიახ",
    StringKey.SettingsMapDataUpdateConfirmNo to "არა",
    StringKey.SettingsMapDataRefreshError to
        "განახლება ვერ მოხერხდა — შეამოწმეთ ინტერნეტკავშირი",
    StringKey.SettingsMapDataRedownloadingPrefix to
        "რუკის მონაცემები განახლდა. თავიდან ჩამოიტვირთება",
    StringKey.SettingsMapDataRedownloadingSuffix to
        "— მიმდინარეობა შეგიძლიათ ნახოთ განყოფილებაში „წინასწარი ჩამოტვირთვა“.",
    StringKey.SettingsMapDataRegionsCountZero to "ტერიტორია",
    StringKey.SettingsMapDataRegionsCountOne to "ტერიტორია",
    StringKey.SettingsMapDataRegionsCountTwo to "ტერიტორია",
    StringKey.SettingsMapDataRegionsCountFew to "ტერიტორია",
    StringKey.SettingsMapDataRegionsCountMany to "ტერიტორია",
    StringKey.SettingsMapDataRegionsCountOther to "ტერიტორია",
    StringKey.SettingsClearMapCacheButton to "რუკის ქეშის გასუფთავება",
    StringKey.SettingsClearMapCacheConfirmTitle to "გავასუფთაოთ რუკის ქეში?",
    StringKey.SettingsClearMapCacheConfirmMessage to
        "ქეშის გასუფთავებისას წაიშლება რუკის ნანახი ნაწილები, რომლებიც არ შენახულა " +
            "განყოფილებაში „წინასწარი ჩამოტვირთვა“. ნამდვილად გსურთ ქეშის გასუფთავება?",
    StringKey.SettingsClearMapCacheConfirmYes to "დიახ",
    StringKey.SettingsClearMapCacheConfirmNo to "არა",
    StringKey.SettingsMapCacheCleared to "ქეში გასუფთავდა",
)
