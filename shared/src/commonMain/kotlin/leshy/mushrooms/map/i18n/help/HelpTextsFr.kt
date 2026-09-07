package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Français — écrans d'aide, `.claude/plans/help-screens.md`. */
internal val frenchHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "L'écran principal de l'application : c'est ici que la promenade est enregistrée. Le GPS trace " +
            "votre parcours et chaque trouvaille est enregistrée avec ses coordonnées et l'heure — " +
            "aussitôt, si bien que la promenade peut être interrompue à tout moment sans rien perdre.",
    HelpKey.RecordStartFinish to
        "« Démarrer » demande un nom et lance l'enregistrement ; le bouton devient ensuite « Pause », et " +
            "en pause apparaissent « Reprendre » et « Terminer ». « Terminer » clôt la promenade et la " +
            "range dans les « Archives ».",
    HelpKey.RecordTiles to
        "Les tuiles de champignons en bas servent à noter les trouvailles : « + » place une trouvaille à " +
            "votre position actuelle, « − » retire la dernière marque erronée de cette espèce. Un appui " +
            "long sur « + » ouvre la saisie de plusieurs pièces à la fois ; plus de 999 champignons " +
            "identiques par promenade, c'est impossible.",
    HelpKey.RecordPlace to
        "Le bouton rond à gauche marque un lieu — avec un nom, une description et une photo. Le lieu se " +
            "pose là où vous vous tenez et reste sur la carte après la promenade.",
    HelpKey.RecordNavigation to
        "Un appui long sur le repère d'un lieu lance la navigation vers lui : le panneau en haut à droite " +
            "indique la direction et la distance jusqu'à la cible. La croix du panneau coupe la " +
            "navigation.",
    HelpKey.RecordSearchAndOwn to
        "La loupe à droite trouve un champignon par son nom et place sa tuile en tête de la bande — c'est " +
            "plus rapide quand beaucoup d'espèces sont activées. La dernière tuile de la bande, celle " +
            "avec un plus, ajoute votre propre espèce absente du catalogue.",
    HelpKey.RecordFilters to
        "Le bouton « Filtres » en haut à gauche définit les espèces et la période dont les trouvailles " +
            "s'affichent sur la carte ; le nombre dessus indique combien de filtres sont actifs. Le " +
            "filtre est commun avec la « Carte des trouvailles » : ce que vous activez ici s'applique là " +
            "aussi.",
    HelpKey.RecordBackground to
        "L'enregistrement du parcours continue quand l'application passe en arrière-plan. Outre la " +
            "promenade en cours, la carte montre les trouvailles et les lieux marqués des promenades " +
            "passées — on y voit où vous êtes déjà allé et ce qu'il y avait.",
    HelpKey.ArchivePurpose to
        "Toutes vos promenades, les plus récentes en haut. La fiche porte le nom, la date, la durée, les " +
            "kilomètres, le nombre de trouvailles et une miniature du parcours effectué.",
    HelpKey.ArchiveDetail to
        "Un appui sur la fiche ouvre la promenade entière : statistiques, trouvailles par espèce, lieux " +
            "marqués, description et le bouton « Voir la carte ». Le nom et la description se modifient " +
            "au même endroit.",
    HelpKey.ArchiveShare to
        "Le bouton « Partager » compose une image à partir des parties de la promenade que vous cochez. " +
            "Avant d'envoyer la carte d'une promenade, souvenez-vous : on y voit exactement où vous avez " +
            "trouvé les champignons.",
    HelpKey.ArchiveSelection to
        "Un appui long sur une fiche active le mode sélection : marquez les promenades voulues d'un appui " +
            "et appuyez sur « Supprimer les promenades » ; le bouton « Retour » quitte ce mode. La " +
            "suppression est définitive — avec la promenade disparaissent son parcours, ses trouvailles, " +
            "ses lieux marqués et ses photos.",
    HelpKey.ArchiveUnfinished to
        "Une promenade non terminée figure aussi dans la liste : à la place de l'heure de fin, il est " +
            "écrit « en cours ». Une telle promenade n'a pas encore de durée et n'entre donc pas dans le " +
            "temps total de la « Carte des trouvailles ».",
    HelpKey.MapPurpose to
        "La carte d'ensemble : les trouvailles, les parcours et les lieux marqués de toutes vos " +
            "promenades à la fois sur une même toile. Elle sert à voir la vue générale — où sont vos " +
            "coins à champignons et comment ils changent d'année en année.",
    HelpKey.MapFullScreen to
        "En haut, la carte de toutes les trouvailles à la fois ; un appui l'ouvre en plein écran. Quand " +
            "les trouvailles sont nombreuses, les repères proches se regroupent en un cercle chiffré — " +
            "zoomez et il se disperse en champignons distincts. La taille des icônes se règle dans les « " +
            "Paramètres ».",
    HelpKey.MapSliders to
        "Sous la carte, deux curseurs — la plage de dates et la saison, c'est-à-dire une plage de mois — " +
            "et tout ce qui suit est calculé selon votre choix. Les curseurs n'apparaissent que si vous " +
            "avez des promenades de plus d'un jour.",
    HelpKey.MapStats to
        "Sous les curseurs : combien de promenades, de kilomètres, de temps et de trouvailles, les tuiles " +
            "par espèce et le diagramme circulaire. Le temps total additionne les promenades terminées : " +
            "celle en cours n'a pas encore de durée.",
    HelpKey.MapFilters to
        "Le bouton « Filtres » vit sur la carte en plein écran, en haut à gauche : on y retrouve les deux " +
            "mêmes axes, la liste des espèces et l'interrupteur d'affichage des anciens parcours. Le " +
            "nombre sur le bouton dit combien de filtres sont actifs ; le filtre est commun avec l'écran " +
            "d'enregistrement.",
    HelpKey.MapPlaces to
        "Un appui sur le repère d'un lieu ouvre sa fiche avec la photo et la description. De là, le lieu " +
            "peut aussi être modifié ou supprimé.",
    HelpKey.SpeciesPurpose to
        "Ici vous décidez quels champignons seront des tuiles sur l'écran d'enregistrement. Le catalogue " +
            "est découpé en collections par pays, et à côté vivent les espèces absentes du catalogue — " +
            "celles que vous ajoutez vous-même.",
    HelpKey.SpeciesCollections to
        "Dans « Collections de champignons », un appui sur la ligne d'un pays déplie ses espèces : la " +
            "case à côté du pays active toute la collection, les cases à l'intérieur les espèces une à " +
            "une. Le champ de recherche en haut trouve un pays par son nom.",
    HelpKey.SpeciesOwn to
        "Dans « Champignons ajoutés », le bouton « Ajouter un champignon » ouvre un formulaire : nom, nom " +
            "scientifique, couleur du repère et image — depuis l'appareil photo, la galerie ou le " +
            "catalogue. Le crayon modifie une espèce déjà ajoutée, la croix la supprime.",
    HelpKey.SpeciesCheckboxes to
        "Décocher ne supprime rien — l'espèce cesse simplement d'apparaître en tuile, et les trouvailles " +
            "passées restent en place. Supprimer votre propre espèce est en revanche définitif : toutes " +
            "ses marques dans les promenades passées basculent vers « Champignon inconnu ». Les espèces " +
            "portant « depuis les archives » sont arrivées avec des promenades importées.",
    HelpKey.SpeciesImages to
        "Toutes les images de champignons de l'application sont indicatives : elles aident à reconnaître " +
            "la tuile, pas le champignon en forêt. N'identifiez pas un champignon inconnu avec elles.",
    HelpKey.PreparationPurpose to
        "Télécharge à l'avance des morceaux de carte dans la mémoire du téléphone, pour que la carte " +
            "reste disponible en forêt sans internet : sans cela, hors couverture, à la place de la carte " +
            "il n'y a qu'un fond vide.",
    HelpKey.PreparationDownload to
        "Trouvez la zone voulue — déplacez et zoomez la carte — puis appuyez sur le bouton rond à flèche " +
            "vers le bas, en bas à droite. L'application indique la place qu'occupera ce qui est à " +
            "l'écran : « Télécharger cette zone » demande un nom et lance le téléchargement, « Annuler » " +
            "rend la carte.",
    HelpKey.PreparationRegions to
        "Les zones téléchargées forment une bande en bas. Un appui sur une vignette vole jusqu'à cette " +
            "zone sur la carte, et les boutons de la vignette mettent le téléchargement en pause et le " +
            "reprennent, réessaient après une erreur et suppriment la zone.",
    HelpKey.PreparationAreaSize to
        "Ce qui est téléchargé est exactement ce qui est visible à l'écran, d'où l'estimation de taille " +
            "qui change pendant que vous déplacez la carte. Plus la zone est grande, moins elle peut être " +
            "détaillée — mieux vaut plusieurs petites zones qu'une énorme. Les noms des zones ne doivent " +
            "pas se répéter.",
    HelpKey.PreparationBackground to
        "Le téléchargement se poursuit en arrière-plan et ne s'interrompt pas si vous quittez l'écran ; " +
            "en pause, la progression est conservée. « Mettre à jour les données cartographiques » dans " +
            "les « Paramètres » retélécharge toutes les zones enregistrées.",
    HelpKey.DataPurpose to
        "Transfert de promenades d'un téléphone à l'autre et sauvegarde : les promenades choisies sont " +
            "écrites dans un seul fichier d'archive, et un tel fichier peut être rechargé — sur cet " +
            "appareil ou sur un autre.",
    HelpKey.DataExport to
        "Le sélecteur en haut choisit « Export » ou « Import ». Sous « Export », donnez un nom à " +
            "l'archive, appuyez sur la ligne de sélection des promenades, cochez celles voulues, puis « " +
            "Terminé » — le téléphone demandera où enregistrer le fichier.",
    HelpKey.DataImport to
        "Sous « Import », appuyez sur « Choisir un fichier », saisissez si vous le souhaitez une mention " +
            "qui s'ajoutera aux noms des promenades chargées, et appuyez sur « Terminé » ; une fois " +
            "l'archive lue, le bouton « Vers les archives » apparaît. Le bouton « Annuler » efface la " +
            "saisie sans rien enregistrer.",
    HelpKey.DataArchiveContents to
        "L'archive emporte le parcours, les trouvailles, les lieux marqués, les photos et les espèces " +
            "absentes du catalogue — sur l'autre appareil, elles apparaissent dans « Champignons ajoutés " +
            "» avec la mention « depuis les archives ».",
    HelpKey.DataDuplicates to
        "L'import ajoute toujours les promenades à côté de celles déjà présentes et ne remplace rien : " +
            "recharger le même fichier les crée une seconde fois, et la mention ajoutée aux noms aide " +
            "ensuite à distinguer les lots. À la fin, l'écran indique combien de promenades ont été " +
            "chargées et combien n'ont pas pu être lues.",
    HelpKey.SettingsPurpose to
        "Les options générales de l'application : langue de l'interface, apparence, aspect et ordre des " +
            "tuiles de champignons sur l'écran d'enregistrement, et entretien de la carte.",
    HelpKey.SettingsLanguage to
        "La ligne « Langue de l'interface » ouvre la liste des langues : un appui en choisit une, la " +
            "coche en haut valide, la flèche ressort sans rien changer. La langue s'applique aussitôt à " +
            "toute l'application, aucun redémarrage n'est nécessaire.",
    HelpKey.SettingsTheme to
        "« Apparence » bascule entre le thème clair et le thème sombre de l'application. « Système » " +
            "laisse le choix au téléphone : l'application s'assombrit et s'éclaircit avec lui.",
    HelpKey.SettingsMushroomSize to
        "Le curseur règle la taille des icônes de champignons sur la carte — aussi bien sur l'écran " +
            "d'enregistrement que sur la « Carte des trouvailles ». L'image en dessous change pendant que " +
            "vous faites glisser, la taille se voit donc avant de relâcher.",
    HelpKey.SettingsMushroomOrder to
        "D'ordinaire, les champignons que vous venez de marquer remontent en tête de la bande de tuiles. " +
            "« Figer l'ordre des champignons » désactive complètement ce comportement, et « Réinitialiser " +
            "l'ordre des champignons à la fin de la promenade » rétablit l'ordre initial une fois la " +
            "promenade terminée.",
    HelpKey.SettingsMapData to
        "« Mettre à jour les données cartographiques » vérifie si la carte a changé sur le serveur et, le " +
            "cas échéant, retélécharge toutes les zones hors ligne enregistrées. « Vider le cache de la " +
            "carte » n'enlève que ce qui a été chargé en naviguant — les zones du « Préchargement » " +
            "restent en place.",
)
