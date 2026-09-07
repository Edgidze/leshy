package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Nederlands — hulpschermen, `.claude/plans/help-screens.md`. */
internal val dutchHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "Het hoofdscherm van de app: hier wordt de wandeling opgenomen. Via gps wordt je route vastgelegd " +
            "en elke vondst wordt met coördinaten en tijd bewaard — meteen, dus de wandeling mag op elk " +
            "moment worden onderbroken zonder dat het opgenomene verdwijnt.",
    HelpKey.RecordStartFinish to
        "“Start” vraagt om een naam en begint de opname; daarna verandert de knop in “Pauze”, en tijdens " +
            "de pauze verschijnen “Hervatten” en “Beëindigen”. “Beëindigen” sluit de wandeling af en " +
            "verplaatst hem naar het “Wandelarchief”.",
    HelpKey.RecordTiles to
        "Met de paddenstoeltegels onderaan markeer je vondsten: “+” zet een vondst op je huidige punt, " +
            "“−” haalt de laatste foutieve markering van die soort weg. Lang drukken op “+” opent de " +
            "invoer van meerdere stuks tegelijk; meer dan 999 dezelfde paddenstoelen per wandeling kunnen " +
            "niet worden gemarkeerd.",
    HelpKey.RecordPlace to
        "De ronde knop links markeert een plek — met naam, beschrijving en foto. De plek komt waar je nu " +
            "staat en blijft na de wandeling op de kaart staan.",
    HelpKey.RecordNavigation to
        "Lang drukken op een plekmarkering start de navigatie ernaartoe: het paneel rechtsboven toont de " +
            "richting en de afstand tot het doel. Het kruisje op het paneel zet de navigatie uit.",
    HelpKey.RecordSearchAndOwn to
        "Het vergrootglas rechts zoekt een paddenstoel op naam en zet de tegel vooraan in de rij — dat " +
            "gaat sneller als er veel soorten aanstaan. De laatste tegel van de rij, met een plus, voegt " +
            "een eigen soort toe die niet in de catalogus staat.",
    HelpKey.RecordFilters to
        "De knop “Filters” linksboven bepaalt welke soorten en welke periode op de kaart verschijnen; het " +
            "getal erop zegt hoeveel filters nu aanstaan. Het filter is gedeeld met de “Vondstenkaart”: " +
            "wat je hier aanzet, geldt daar ook.",
    HelpKey.RecordBackground to
        "Het opnemen van de route gaat door wanneer de app op de achtergrond staat. Naast de huidige " +
            "wandeling toont de kaart de vondsten en gemarkeerde plekken van eerdere wandelingen — " +
            "daaraan zie je waar je al liep en wat daar was.",
    HelpKey.ArchivePurpose to
        "Al je wandelingen, de nieuwste bovenaan. Op de kaart staan naam, datum, duur, kilometers, aantal " +
            "vondsten en een miniatuur van de gelopen route.",
    HelpKey.ArchiveDetail to
        "Op een kaart tikken opent de hele wandeling: statistieken, vondsten per soort, gemarkeerde " +
            "plekken, beschrijving en de knop “Kaart bekijken”. Naam en beschrijving zijn daar ook te " +
            "wijzigen.",
    HelpKey.ArchiveShare to
        "De knop “Delen” bouwt een afbeelding uit de delen van de wandeling die je aanvinkt. Bedenk " +
            "voordat je de kaart van een wandeling verstuurt: daarop is precies te zien waar je de " +
            "paddenstoelen vond.",
    HelpKey.ArchiveSelection to
        "Lang drukken op een kaart zet de selectiestand aan: tik de gewenste wandelingen aan en druk op " +
            "“Wandelingen verwijderen”; de knop “Terug” verlaat deze stand. Verwijderen is onomkeerbaar — " +
            "met de wandeling verdwijnen de route, de vondsten, de gemarkeerde plekken en de foto’s.",
    HelpKey.ArchiveUnfinished to
        "Een niet afgeronde wandeling staat ook in de lijst: in plaats van een eindtijd staat er “bezig”. " +
            "Zo’n wandeling heeft nog geen duur en telt daarom niet mee in de totale tijd op de " +
            "“Vondstenkaart”.",
    HelpKey.MapPurpose to
        "De verzamelkaart: de vondsten, routes en gemarkeerde plekken van al je wandelingen tegelijk op " +
            "één vlak. Bedoeld om het geheel te zien — waar je paddenstoelenplekken liggen en hoe ze van " +
            "jaar tot jaar veranderen.",
    HelpKey.MapFullScreen to
        "Bovenaan staat de kaart met alle vondsten tegelijk; tikken opent de kaart schermvullend. Bij " +
            "veel vondsten trekken dicht bij elkaar liggende markeringen samen tot een rondje met een " +
            "getal — zoom in en het valt uiteen in losse paddenstoelen. De grootte van de pictogrammen " +
            "stel je in bij “Instellingen”.",
    HelpKey.MapSliders to
        "Onder de kaart staan twee schuifregelaars — een datumbereik en een seizoen, dus een maandbereik " +
            "— en alles eronder wordt volgens die keuze berekend. De regelaars verschijnen pas als er " +
            "wandelingen van meer dan één dag zijn.",
    HelpKey.MapStats to
        "Onder de regelaars: hoeveel wandelingen, kilometers, tijd en vondsten er waren, de tegels per " +
            "soort en het cirkeldiagram. De totale tijd telt afgeronde wandelingen op: een lopende heeft " +
            "nog geen duur.",
    HelpKey.MapFilters to
        "De knop “Filters” woont op de schermvullende kaart, linksboven: daar staan dezelfde twee assen, " +
            "de soortenlijst en de schakelaar voor het tonen van eerdere routes. Het getal op de knop " +
            "zegt hoeveel filters aanstaan; het filter is gedeeld met het opnamescherm.",
    HelpKey.MapPlaces to
        "Op een plekmarkering tikken opent de kaart ervan met foto en beschrijving. Vandaar kan de plek " +
            "ook worden gewijzigd of verwijderd.",
    HelpKey.SpeciesPurpose to
        "Hier bepaal je welke paddenstoelen als tegels op het opnamescherm staan. De catalogus is " +
            "verdeeld in collecties per land, en daarnaast leven de soorten die niet in de catalogus " +
            "staan — die voeg je zelf toe.",
    HelpKey.SpeciesCollections to
        "Bij “Collecties paddenstoelen” klapt tikken op de regel van een land de soorten uit: het vinkje " +
            "bij het land zet de hele collectie aan, de vinkjes erbinnen losse soorten. Het zoekveld " +
            "bovenaan vindt een land op naam.",
    HelpKey.SpeciesOwn to
        "Bij “Toegevoegde paddenstoelen” opent de knop “Paddenstoel toevoegen” een formulier: naam, " +
            "wetenschappelijke naam, kleur van de markering en een afbeelding — uit de camera, uit de " +
            "galerij of uit de catalogus. Het potlood wijzigt een al toegevoegde soort, het kruisje " +
            "verwijdert hem.",
    HelpKey.SpeciesCheckboxes to
        "Een vinkje weghalen verwijdert niets — de soort verschijnt gewoon niet meer als tegel, en " +
            "eerdere vondsten blijven staan. Een eigen soort verwijderen is daarentegen onomkeerbaar: al " +
            "zijn markeringen in eerdere wandelingen gaan naar “Onbekende paddenstoel”. Soorten met het " +
            "label “uit archief” kwamen mee met geïmporteerde wandelingen.",
    HelpKey.SpeciesImages to
        "Alle paddenstoelafbeeldingen in de app zijn slechts indicatief: ze helpen de tegel te herkennen, " +
            "niet de paddenstoel in het bos. Determineer er geen onbekende paddenstoelen mee.",
    HelpKey.PreparationPurpose to
        "Downloadt stukken kaart vooraf naar het geheugen van de telefoon, zodat de kaart in het bos " +
            "zonder internet blijft staan: zonder dat krijg je buiten bereik een lege achtergrond in " +
            "plaats van een kaart.",
    HelpKey.PreparationDownload to
        "Zoek het gewenste gebied — verschuif en zoom de kaart — en druk dan rechtsonder op de ronde knop " +
            "met de pijl omlaag. De app toont hoeveel ruimte het huidige beeld inneemt: “Dit gebied " +
            "downloaden” vraagt om een naam en start de download, “Annuleren” brengt de kaart terug.",
    HelpKey.PreparationRegions to
        "De gedownloade gebieden staan in een strook onderaan. Op een kaartje tikken vliegt naar dat " +
            "gebied op de kaart, en de knoppen op het kaartje pauzeren de download en hervatten hem, " +
            "proberen het na een fout opnieuw en verwijderen het gebied.",
    HelpKey.PreparationAreaSize to
        "Gedownload wordt precies wat op het scherm staat, daarom verandert de schatting terwijl je de " +
            "kaart verschuift. Hoe groter het gebied, hoe grover het moet zijn — een paar kleine gebieden " +
            "leveren meer op dan één enorm gebied. Namen van gebieden mogen niet dubbel zijn.",
    HelpKey.PreparationBackground to
        "Het downloaden loopt op de achtergrond en stopt niet als je het scherm verlaat; bij pauze blijft " +
            "de voortgang bewaard. “Kaartgegevens bijwerken” bij “Instellingen” haalt alle opgeslagen " +
            "gebieden opnieuw op.",
    HelpKey.DataPurpose to
        "Wandelingen tussen telefoons overzetten en back-uppen: de gekozen wandelingen gaan naar één " +
            "archiefbestand, en zo’n bestand kan weer worden ingeladen — op dit of op een ander apparaat.",
    HelpKey.DataExport to
        "De schakelaar bovenaan kiest “Export” of “Import”. Geef bij “Export” het archief een naam, tik " +
            "de regel voor het kiezen van wandelingen aan, vink de gewenste aan en druk op “Klaar” — de " +
            "telefoon vraagt waar het bestand moet komen.",
    HelpKey.DataImport to
        "Druk bij “Import” op “Bestand kiezen”, typ desgewenst een toevoeging die aan de namen van de " +
            "geladen wandelingen wordt geplakt, en druk op “Klaar”; zodra het archief is gelezen " +
            "verschijnt de knop “Naar archief”. De knop “Annuleren” wist het ingevoerde zonder iets te " +
            "bewaren.",
    HelpKey.DataArchiveContents to
        "In het archief komen de route, de vondsten, de gemarkeerde plekken, de foto’s en die " +
            "paddenstoelsoorten die niet in de catalogus staan — op het andere apparaat verschijnen ze " +
            "bij “Toegevoegde paddenstoelen” met het label “uit archief”.",
    HelpKey.DataDuplicates to
        "Importeren zet wandelingen altijd naast de bestaande en vervangt niets, dus hetzelfde bestand " +
            "een tweede keer laden maakt ze opnieuw aan: de toevoeging in de naam helpt ze later uit " +
            "elkaar te houden. Aan het eind verschijnt hoeveel wandelingen zijn geladen en hoeveel niet " +
            "leesbaar waren.",
    HelpKey.SettingsPurpose to
        "De algemene opties van de app: taal, weergave, uiterlijk en volgorde van de paddenstoeltegels op " +
            "het opnamescherm, en onderhoud van de kaart.",
    HelpKey.SettingsLanguage to
        "De regel “Taal van de app” opent de lijst met talen: tikken kiest een taal, het vinkje bovenaan " +
            "bevestigt de keuze, de pijl gaat terug zonder iets te wijzigen. De taal geldt meteen in de " +
            "hele app, opnieuw starten is niet nodig.",
    HelpKey.SettingsTheme to
        "“Weergave” schakelt tussen het lichte en het donkere thema van de app. “Systeem” laat de keuze " +
            "aan de telefoon: de app wordt samen met de telefoon donker en weer licht.",
    HelpKey.SettingsMushroomSize to
        "De schuifregelaar bepaalt de grootte van de paddenstoelpictogrammen op de kaart — zowel op het " +
            "opnamescherm als op de verzamelde “Vondstenkaart”. De afbeelding eronder verandert al " +
            "tijdens het slepen, dus je ziet de maat voordat je loslaat.",
    HelpKey.SettingsMushroomOrder to
        "Normaal schuiven zojuist gemarkeerde paddenstoelen naar het begin van de tegelrij. “Volgorde " +
            "vastzetten” schakelt dat helemaal uit, en “Volgorde herstellen aan het einde van een " +
            "wandeling” zet de oorspronkelijke volgorde terug zodra een wandeling klaar is.",
    HelpKey.SettingsMapData to
        "“Kaartgegevens bijwerken” controleert of de kaart op de server is veranderd en haalt zo ja alle " +
            "opgeslagen offlinegebieden opnieuw op. “Kaartcache wissen” verwijdert alleen wat tijdens het " +
            "bladeren is bijgeladen — de gebieden uit “Vooraf downloaden” blijven staan.",
)
