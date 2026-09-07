package leshy.mushrooms.map.i18n.strings

import leshy.mushrooms.map.i18n.StringKey

/** French — Phase 6 of `.claude/plans/countries-and-languages.md`. Plural forms follow
 * `Plurals.kt`: `n == 0 || n == 1` → [leshy.mushrooms.map.i18n.PluralCategory.One] (French
 * treats zero as singular — "0 champignon"), multiples of a million → `Many` (kept for
 * exactness, effectively unreachable at real counts), else → `Other`. `Zero` is unreachable
 * (0 resolves to `One`) and repeats the `One` form; `Two`/`Few` are unreachable and repeat
 * `Other`, same convention as `englishStrings`. */
internal val frenchStrings: Map<StringKey, String> = mapOf(
    StringKey.AppName to "Carte des champignons de Leshy",
    StringKey.NavRecord to "Sortie",
    StringKey.NavArchive to "Archives",
    StringKey.NavMap to "Carte des trouvailles",
    StringKey.NavData to "Export/Import",
    StringKey.NavPreparation to "Préchargement",
    StringKey.NavSpecies to "Mes champignons",
    StringKey.SettingsTitle to "Paramètres",
    StringKey.SettingsContentDescription to "Paramètres",
    StringKey.SettingsLanguageTitle to "Langue de l'interface",
    StringKey.SettingsThemeTitle to "Apparence",
    StringKey.SettingsThemeLight to "Clair",
    StringKey.SettingsThemeDark to "Sombre",
    StringKey.SettingsThemeSystem to "Système",
    StringKey.SettingsCategoriesTitle to "Champignons à suivre",
    StringKey.SettingsMushroomSizeTitle to "Ajustez la taille des champignons sur la carte",
    StringKey.SettingsMushroomSortTitle to "Ordre des champignons",
    StringKey.SettingsResetMushroomOrderOnWalkFinish to
        "Réinitialiser l'ordre des champignons à la fin de la promenade",
    StringKey.SettingsFreezeMushroomOrder to "Figer l'ordre des champignons",

    StringKey.MushroomImagesDisclaimer to
        "Toutes les images de champignons de l'application sont purement illustratives — ne les " +
            "utilisez pas pour identifier des champignons inconnus !",

    StringKey.SpeciesCollectionsTitle to "Collections de champignons",
    StringKey.SpeciesMyMushroomsTitle to "Champignons ajoutés",
    StringKey.SpeciesMyMushroomsEmpty to "Les champignons que vous ajoutez apparaîtront ici",
    StringKey.SpeciesAddButton to "Ajouter un champignon",
    StringKey.SpeciesFormTitleCreate to "Nouveau champignon",
    StringKey.SpeciesFormTitleEdit to "Modifier le champignon",
    StringKey.SpeciesFormNameHint to "Nom",
    StringKey.SpeciesFormScientificNameHint to "Nom scientifique",
    StringKey.SpeciesFormColorLabel to "Couleur",
    StringKey.SpeciesFormTakePhotoButton to "Appareil photo",
    StringKey.SpeciesFormPickPhotoButton to "Galerie",
    StringKey.SpeciesFormPickCatalogButton to "Images",
    StringKey.SpeciesFormSaveButton to "Enregistrer",
    StringKey.SpeciesFormCancelContentDescription to "Annuler",
    StringKey.SpeciesListImportedLabel to "depuis les archives",
    StringKey.SpeciesListEditContentDescription to "Modifier",
    StringKey.SpeciesListDeleteContentDescription to "Supprimer l'espèce",
    StringKey.SpeciesDeleteConfirmTitle to "Supprimer ce champignon ?",
    StringKey.SpeciesDeleteConfirmMessage to
        "Voulez-vous vraiment supprimer cette espèce ? Toutes les trouvailles qui lui sont " +
            "associées seront déplacées vers la catégorie « Champignon inconnu ». Cette action " +
            "est irréversible.",
    StringKey.SpeciesDeleteConfirmYes to "Oui",
    StringKey.SpeciesDeleteConfirmNo to "Non",

    StringKey.CatalogPhotoPickerTitle to "Choisir une image",

    StringKey.IconEditorTitle to "Éditeur de photo",
    StringKey.IconEditorToolEraser to "Gomme",
    StringKey.IconEditorToolCrop to "Recadrer",
    StringKey.IconEditorShapeRectangle to "Rectangle",
    StringKey.IconEditorShapeOval to "Ovale",
    StringKey.IconEditorBrushSizeLabel to "Taille du pinceau",
    StringKey.IconEditorUndoContentDescription to "Annuler",
    StringKey.IconEditorRedoContentDescription to "Rétablir",
    StringKey.IconEditorDoneContentDescription to "Terminé",

    StringKey.OnboardingTitle to "Bienvenue !",
    StringKey.OnboardingDescription to
        "Choisissez les collections de champignons qui vous intéressent. Vous pourrez modifier " +
            "cela plus tard dans les paramètres.",
    StringKey.OnboardingContinueButton to "Commencer",
    StringKey.OnboardingNothingPickedWarning to
        "Choisissez au moins une collection ou un champignon pour continuer",

    StringKey.WelcomeIntro to
        "L’application retient vos parcours et vos trouvailles — et aide vraiment à la cueillette : les " +
            "bons coins sont faciles à retrouver, et tout ce que vous avez trouvé tient sur une seule carte.",
    StringKey.WelcomeRecordTitle to "Enregistrez votre sortie",
    StringKey.WelcomeRecordText to
        "L’application suit le tracé, la durée et la distance toute seule. Un champignon trouvé — touchez " +
            "sa vignette ; une source, un arbre tombé ou votre voiture se marquent directement sur la carte.",
    StringKey.WelcomeArchiveTitle to "Revenez à vos trouvailles",
    StringKey.WelcomeArchiveText to
        "L’archive garde chaque sortie à part, avec son tracé et ses trouvailles. La carte commune les " +
            "réunit toutes : ce qui a été trouvé, où et en quelle quantité, sur toutes vos saisons.",
    StringKey.WelcomeHelpTitle to "Un doute ? Touchez « ? »",
    StringKey.WelcomeHelpText to
        "Chaque section possède en haut à droite un bouton « ? » qui explique comment elle fonctionne.",
    StringKey.WelcomeMenuTitle to "Le reste est dans le menu",
    StringKey.WelcomeMenuText to
        "Le bouton de menu en haut à gauche ouvre la liste de toutes les sections et possibilités de " +
            "l’application.",
    StringKey.WelcomeConsentTitle to "Avant de commencer",
    StringKey.WelcomeConsentIntro to
        "Avant de passer à l’application elle-même, vous devez accepter les affirmations suivantes :",
    StringKey.WelcomeConsentImages to
        "Vous n’essaierez pas d’identifier des champignons d’après les images de l’application. Les images servent " +
            "d’illustrations et ne constituent pas un guide vérifié.",
    StringKey.WelcomeConsentEating to
        "Vous ne mangerez en aucun cas des champignons que vous ne connaissez pas. Les champignons peuvent être non " +
            "comestibles, et ils peuvent aussi être vénéneux. Le mieux — faites appel à quelqu’un qui connaît les " +
            "champignons de votre région pour savoir lesquels peuvent être ramassés et comment il faut les préparer " +
            "ensuite.",
    StringKey.WelcomeConsentWarning to
        "Pour continuer, vous devez accepter les affirmations ci-dessus en cochant les cases devant celles avec " +
            "lesquelles vous êtes d’accord",

    StringKey.WelcomeNextButton to "Suivant",

    StringKey.LegalTitle to "Confidentialité",
    StringKey.LegalPrivacyText to
        "Vos sorties, vos marques et vos photos restent sur votre appareil. L’application ne crée aucun " +
            "compte et n’envoie aucune de vos données — seules des requêtes de tuiles de carte partent vers " +
            "openfreemap.org.",
    StringKey.LegalPrivacyLink to "Politique de confidentialité",

    StringKey.AboutTitle to "À propos",
    StringKey.AboutMapDataTitle to "Données cartographiques",
    StringKey.AboutMapDataText to
        "La carte repose sur les données d’OpenStreetMap, diffusées sous licence ODbL. Les tuiles " +
            "vectorielles et le style viennent d’OpenMapTiles, la diffusion est assurée par le service " +
            "OpenFreeMap.",
    StringKey.AboutOpenSourceTitle to "Code ouvert",
    StringKey.AboutOpenSourceText to
        "L’application est assemblée à partir de bibliothèques open source. Appuyer sur une ligne de la " +
            "liste ouvre le texte complet de sa licence.",

    StringKey.NavMenuContentDescription to "Menu",
    StringKey.HelpContentDescription to "Aide",
    StringKey.HelpDialogTitle to "Aide",
    StringKey.HelpDialogDismiss to "Compris",

    StringKey.CategoryMisc to "Divers",
    StringKey.CategoryUnknownMushroom to "Champignon inconnu",

    StringKey.CollectionPickerSearchHint to "Rechercher un pays",

    StringKey.LanguagePickerSearchHint to "Rechercher une langue",
    StringKey.LanguagePickerBackContentDescription to "Retour",
    StringKey.LanguagePickerConfirmContentDescription to "Confirmer",

    StringKey.DefaultWalkName to "Promenade",
    StringKey.RecordWalkNameHint to "Nom de la promenade",
    StringKey.RecordStart to "Démarrer",
    StringKey.RecordPause to "Pause",
    StringKey.RecordResume to "Reprendre",
    StringKey.RecordFinish to "Terminer",
    StringKey.RecordSetWalkNameTitle to "Définissez le nom de la promenade :",
    StringKey.RecordDefaultWalkNamePrefix to "Promenade du",
    StringKey.RecordConfirmWalkNameContentDescription to "Valider",
    StringKey.RecordMarkLocationContentDescription to "Marquer l'emplacement",
    StringKey.RecordLocationUnavailable to "La position n'est pas disponible — l'itinéraire n'est pas enregistré. Activez la localisation et autorisez-y l'accès dans les réglages de l'appareil.",
    StringKey.RecordLocationUnknownMessage to
        "Votre position n'est pas encore connue : il n'y a rien à quoi rattacher le repère. Vérifiez que la localisation est activée et attendez le signal.",
    StringKey.RecordSearchContentDescription to "Rechercher",
    StringKey.RecordSearchDialogTitle to "Choisissez le champignon recherché",
    StringKey.RecordBulkAddQuestion to "Combien de nouveaux champignons trouvés ?",
    StringKey.RecordBulkAddCancelContentDescription to "Annuler",

    StringKey.RecordBulkAddConfirmContentDescription to "Valider",
    StringKey.RecordBulkAddLimitMessage to
        "Maximum de 999 trouvailles de la même espèce par promenade.",
    StringKey.DialogAcknowledge to "Compris",

    StringKey.NavigationDirectionToPrefix to "Direction vers",
    StringKey.NavigationDistanceToTargetPrefix to "jusqu'à la cible",
    StringKey.NavigationMetersSuffix to "mètres",
    StringKey.NavigationKeepRightPhrase to "restez à droite sur",
    StringKey.NavigationKeepLeftPhrase to "restez à gauche sur",
    StringKey.NavigationGoStraightPhrase to "continuez tout droit",
    StringKey.NavigationDeterminingDirection to "Détermination de la direction…",
    StringKey.NavigationArrivedPhrase to "Vous êtes arrivé",
    StringKey.NavigationCloseContentDescription to "Fermer",

    StringKey.AddPlaceTitle to "Ajouter un lieu",
    StringKey.AddPlaceEditTitle to "Modifier le lieu",
    StringKey.AddPlaceDefaultName to "Lieu",
    StringKey.AddPlaceNameHint to "Nom du lieu",
    StringKey.AddPlacePhotoContentDescription to "Prendre une photo",
    StringKey.CameraPermissionDenied to "Pas d'accès à l'appareil photo. Autorisez-le dans les réglages de l'appareil.",
    StringKey.AddPlaceDescriptionTitle to "Description",
    StringKey.AddPlaceDescriptionHint to "Décrivez le lieu",
    StringKey.AddPlaceCoordinatesTitle to "Coordonnées",
    StringKey.AddPlaceCopyCoordinatesContentDescription to "Copier les coordonnées",
    StringKey.AddPlaceSaveContentDescription to "Enregistrer le lieu",
    StringKey.AddPlaceDiscardContentDescription to "Abandonner le lieu",

    StringKey.PlaceViewEditContentDescription to "Modifier le lieu",
    StringKey.PlaceViewDeleteContentDescription to "Supprimer le lieu",
    StringKey.PlaceDeleteConfirmTitle to "Supprimer ce lieu ?",
    StringKey.PlaceDeleteConfirmMessage to
        "Le lieu sera supprimé définitivement. Cette action est irréversible.",
    StringKey.PlaceDeleteConfirmYes to "Oui",
    StringKey.PlaceDeleteConfirmNo to "Non",

    StringKey.ArchiveEmpty to "Aucune promenade enregistrée pour le moment",
    StringKey.ArchiveEmptyHint to
        "Les promenades enregistrées apparaîtront ici : l'itinéraire, les trouvailles et les lieux marqués.",
    StringKey.EmptyStartWalkButton to "Démarrer une promenade",
    StringKey.ArchiveDeleteWalksButton to "Supprimer les promenades",
    StringKey.ArchiveDeleteConfirmMessage to
        "Voulez-vous vraiment supprimer définitivement les promenades sélectionnées ?",
    StringKey.ArchiveDeleteConfirmYes to "Oui",
    StringKey.ArchiveDeleteConfirmNo to "Non",
    StringKey.WalkDetailStartTime to "Départ",
    StringKey.WalkDetailEndTime to "Fin",
    StringKey.WalkDetailInProgress to "en cours",
    StringKey.WalkDetailDistance to "Distance",
    StringKey.WalkDetailDuration to "Durée",
    StringKey.WalkDetailAvgSpeed to "Vitesse moyenne",
    StringKey.WalkDetailDurationDays to "j",
    StringKey.WalkDetailDurationHours to "h",
    StringKey.WalkDetailDurationMinutes to "min",
    StringKey.WalkCardDurationHours to "h",
    StringKey.WalkCardDurationMinutes to "min",
    StringKey.UnitKilometers to "km",
    StringKey.UnitKmh to "km/h",
    StringKey.UnitMegabytes to "Mo",
    StringKey.WalkDetailFindsTitle to "Trouvailles par type",
    StringKey.WalkDetailFindsEmpty to "Aucune trouvaille enregistrée",
    StringKey.WalkDetailPlacesTitle to "Lieux marqués",
    StringKey.WalkDetailViewMap to "Voir la carte",
    StringKey.WalkDetailEditContentDescription to "Modifier le nom de la promenade",
    StringKey.WalkDetailEditWalkNameTitle to "Modifiez le nom de la promenade :",
    StringKey.WalkDetailConfirmEditWalkNameContentDescription to "Valider",
    StringKey.WalkDetailDeleteContentDescription to "Supprimer la promenade",
    StringKey.WalkDetailShareAction to "Partager",
    StringKey.WalkDetailDeleteAction to "Supprimer",
    StringKey.WalkDetailDeleteConfirmTitle to "Supprimer la promenade ?",
    StringKey.WalkDetailDeleteConfirmMessage to
        "La promenade et toutes ses trouvailles seront supprimées définitivement. Cette action " +
            "est irréversible.",
    StringKey.WalkDetailDeleteConfirmYes to "Oui",
    StringKey.WalkDetailDeleteConfirmNo to "Non",
    StringKey.WalkDetailMushroomsCountZero to "champignon",
    StringKey.WalkDetailMushroomsCountOne to "champignon",
    StringKey.WalkDetailMushroomsCountTwo to "champignons",
    StringKey.WalkDetailMushroomsCountFew to "champignons",
    StringKey.WalkDetailMushroomsCountMany to "millions de champignons",
    StringKey.WalkDetailMushroomsCountOther to "champignons",
    StringKey.WalkDetailDescriptionTitle to "Description",
    StringKey.WalkDetailDescriptionEmpty to "Aucune description ajoutée",
    StringKey.WalkDetailDescriptionHint to "Décrivez la promenade",
    StringKey.WalkDetailEditDescriptionContentDescription to "Modifier la description",
    StringKey.WalkDetailDescriptionCancelContentDescription to "Annuler",
    StringKey.WalkDetailDescriptionSaveContentDescription to "Enregistrer",

    StringKey.WalkShareContentDescription to "Partager la promenade",
    StringKey.WalkShareDialogTitle to "Partager",
    StringKey.WalkShareOptionName to "Nom de la promenade",
    StringKey.WalkShareOptionStats to "Statistiques de la promenade",
    StringKey.WalkShareOptionDescription to "Description de la promenade",
    StringKey.WalkShareOptionDiagram to "Diagramme des trouvailles",
    StringKey.WalkShareOptionMap to "Carte avec les repères",
    StringKey.WalkShareMapWarning to
        "D'autres personnes pourront voir où vous avez trouvé des champignons",
    StringKey.WalkShareCancelButton to "Annuler",
    StringKey.WalkShareConfirmButton to "Partager",
    StringKey.WalkShareFooter to "Créé avec l'application « Carte des champignons de Leshy »",
    StringKey.WalkShareImageFooter to "Créé avec l'application Carte des champignons de Leshy",

    StringKey.MapStatsTitle to "Statistiques",
    StringKey.MapStatsWalksCount to "Promenades",
    StringKey.MapStatsFindsCount to "Champignons trouvés",
    StringKey.MapStatsEmptyHint to
        "Les statistiques se constitueront d'elles-mêmes dès la première promenade enregistrée.",

    StringKey.MapFilterButtonLabel to "Filtres",
    StringKey.MapFilterDialogTitle to
        "Configurez les filtres appliqués aux champignons sur la carte :",
    StringKey.MapFilterBackContentDescription to "Retour",
    StringKey.MapFilterDateRangeTitle to "Période",
    StringKey.MapFilterMonthRangeTitle to "Saison",
    StringKey.MapFilterPastRoutesTitle to "Affichage des itinéraires passés",
    StringKey.MapFilterShowPastRoutes to "Afficher les itinéraires passés",

    StringKey.MonthJanuary to "Janvier",
    StringKey.MonthFebruary to "Février",
    StringKey.MonthMarch to "Mars",
    StringKey.MonthApril to "Avril",
    StringKey.MonthMay to "Mai",
    StringKey.MonthJune to "Juin",
    StringKey.MonthJuly to "Juillet",
    StringKey.MonthAugust to "Août",
    StringKey.MonthSeptember to "Septembre",
    StringKey.MonthOctober to "Octobre",
    StringKey.MonthNovember to "Novembre",
    StringKey.MonthDecember to "Décembre",

    StringKey.BackgroundRecordingChannelName to "Enregistrement de la promenade",
    StringKey.BackgroundRecordingNotificationTitle to "Enregistrement de votre promenade",
    StringKey.BackgroundRecordingNotificationText to
        "Le tracé est enregistré en arrière-plan. Appuyez pour revenir à l'application.",

    StringKey.DataExportOption to "Export",
    StringKey.DataImportOption to "Import",
    StringKey.DataArchiveNameLabel to "Nom de l'archive",
    StringKey.DataChooseFileButton to "Choisir un fichier",
    StringKey.DataFileStatusLabel to "Fichier à importer",
    StringKey.DataFileNotSelected to "non sélectionné",
    StringKey.DataImportLabelFieldLabel to "Mention ajoutée aux noms des promenades importées",
    StringKey.DataDoneButton to "Terminé",
    StringKey.DataSavedButton to "Enregistré",
    StringKey.DataGoToArchiveButton to "Vers les archives",
    StringKey.DataCancelButton to "Annuler",
    StringKey.DataProcessingLabel to "Traitement en cours…",
    StringKey.DataExportSuccessMessage to "Archive enregistrée avec succès",
    StringKey.DataImportedWalksLabel to "Promenades importées",
    StringKey.DataImportFailedWalksLabel to "Échec de l'import",
    StringKey.DataErrorLabel to "Erreur",
    StringKey.DataImportRejectedTitle to "Ce fichier ne peut pas être importé",
    StringKey.DataImportRejectedNotArchive to "Ce n'est pas une archive : le fichier ne peut pas être lu en tant que ZIP. Choisissez une archive exportée depuis Leshy.",
    StringKey.DataImportRejectedNotLeshy to "C'est une archive ZIP, mais pas une archive Leshy : elle ne contient pas manifest.json.",
    StringKey.DataImportRejectedNewerFormat to "L'archive a été créée par une version plus récente de l'application. Mettez l'application à jour et réessayez.",
    StringKey.DataImportRejectedDamaged to "L'archive est endommagée : une partie de son contenu est illisible. Rien n'a été importé.",
    StringKey.DataImportRejectedNoWalks to "L'archive ne contient aucune sortie — il n'y a rien à importer.",
    StringKey.DataChooseWalksTitle to "Promenades pour l'archive",
    StringKey.DataWalksBackContentDescription to "Retour sans enregistrer la sélection",
    StringKey.DataWalksConfirmContentDescription to "Confirmer la sélection",
    StringKey.DataWalksSelectedLabel to "Sélectionné",
    StringKey.DataWalksCountZero to "promenade",
    StringKey.DataWalksCountOne to "promenade",
    StringKey.DataWalksCountTwo to "promenades",
    StringKey.DataWalksCountFew to "promenades",
    StringKey.DataWalksCountMany to "millions de promenades",
    StringKey.DataWalksCountOther to "promenades",
    StringKey.PreparationSelectAreaButton to "Télécharger la zone visible",
    StringKey.PreparationDownloadThisAreaButton to "Télécharger cette zone",
    StringKey.PreparationRegionNameDialogTitle to "Nom de la zone",
    StringKey.PreparationRegionNameLabel to "Ex. : Forêt près du village",
    StringKey.PreparationSaveButton to "Télécharger",
    StringKey.PreparationCancelButton to "Annuler",
    StringKey.PreparationDeleteConfirmTitle to "Supprimer la zone ?",
    StringKey.PreparationDeleteConfirmMessage to
        "Les tuiles de carte téléchargées seront supprimées définitivement.",
    StringKey.PreparationDeleteConfirmYes to "Oui",
    StringKey.PreparationDeleteConfirmNo to "Non",
    StringKey.PreparationDeleteContentDescription to "Supprimer la zone",
    StringKey.PreparationPauseContentDescription to "Mettre en pause le téléchargement",
    StringKey.PreparationResumeContentDescription to "Reprendre le téléchargement",
    StringKey.PreparationStatusDownloading to "Téléchargement en cours",
    StringKey.PreparationStatusPaused to "En pause",
    StringKey.PreparationStatusComplete to "Téléchargée",
    StringKey.PreparationStatusError to "Erreur",
    StringKey.PreparationSubtitle to
        "Téléchargez la zone de carte visible pour l'utiliser hors ligne",
    StringKey.PreparationRetryContentDescription to "Relancer le téléchargement",

    StringKey.MapTilesLoadFailed to "La carte ne s'est pas chargée complètement depuis",
    StringKey.MapTilesLoadFailedDismissContentDescription to "Fermer l'avis",

    StringKey.SettingsMapDataTitle to "Données cartographiques",
    StringKey.SettingsRefreshMapDataButton to "Mettre à jour les données cartographiques",
    StringKey.SettingsMapDataUpdateConfirmTitle to "Mettre à jour les données cartographiques ?",
    StringKey.SettingsMapDataUpdateConfirmMessage to
        "Si le contenu de la carte a changé, toutes les zones hors ligne téléchargées seront " +
            "retéléchargées. Voulez-vous vraiment mettre à jour les données cartographiques ?",
    StringKey.SettingsMapDataUpdateConfirmYes to "Oui",
    StringKey.SettingsMapDataUpdateConfirmNo to "Non",
    StringKey.SettingsMapDataRefreshError to
        "Échec de la mise à jour — vérifiez votre connexion internet",
    StringKey.SettingsMapDataRedownloadingPrefix to
        "Données cartographiques mises à jour. Retéléchargement de",
    StringKey.SettingsMapDataRedownloadingSuffix to
        "— suivez la progression dans la section « Préchargement ».",
    StringKey.SettingsMapDataRegionsCountZero to "zone",
    StringKey.SettingsMapDataRegionsCountOne to "zone",
    StringKey.SettingsMapDataRegionsCountTwo to "zones",
    StringKey.SettingsMapDataRegionsCountFew to "zones",
    StringKey.SettingsMapDataRegionsCountMany to "millions de zones",
    StringKey.SettingsMapDataRegionsCountOther to "zones",
    StringKey.SettingsClearMapCacheButton to "Vider le cache de la carte",
    StringKey.SettingsClearMapCacheConfirmTitle to "Vider le cache de la carte ?",
    StringKey.SettingsClearMapCacheConfirmMessage to
        "Vider le cache de la carte supprime les zones parcourues qui n'ont pas été enregistrées " +
            "dans la section « Préchargement ». Voulez-vous vraiment vider le cache ?",
    StringKey.SettingsClearMapCacheConfirmYes to "Oui",
    StringKey.SettingsClearMapCacheConfirmNo to "Non",
    StringKey.SettingsMapCacheCleared to "Cache vidé",
)
