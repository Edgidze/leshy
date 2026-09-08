package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Dansk — hjælpeskærme, `.claude/plans/help-screens.md`. */
internal val danishHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "Appens hovedskærm: her optages turen. GPS tegner dit spor, og hvert fund gemmes med koordinater " +
            "og tidspunkt — med det samme, så turen kan afbrydes når som helst, uden at det optagne går " +
            "tabt.",
    HelpKey.RecordStartFinish to
        "«Start» beder om et navn og begynder optagelsen; derefter bliver knappen til «Pause», og under " +
            "pausen kommer «Fortsæt» og «Afslut» frem. «Afslut» lukker turen og flytter den til " +
            "«Turarkiv».",
    HelpKey.RecordTiles to
        "Svampefelterne nederst er dem, fund markeres med: «+» sætter et fund i dit nuværende punkt, «−» " +
            "fjerner den seneste forkerte markering af den art. Langt tryk på «+» åbner indtastning af " +
            "flere stykker på én gang; mere end 999 ens svampe på én tur kan ikke markeres.",
    HelpKey.RecordPlace to
        "Den runde knap til venstre markerer et sted — med navn, beskrivelse og foto. Stedet placeres " +
            "dér, hvor du står nu, og bliver på kortet også efter turen.",
    HelpKey.RecordNavigation to
        "Langt tryk på en stedmarkering starter navigation dertil: panelet øverst til højre viser retning " +
            "og afstand til målet. Krydset på panelet slår navigationen fra.",
    HelpKey.RecordSearchAndOwn to
        "Forstørrelsesglasset til højre finder svampen på navn og flytter dens felt forrest i rækken — " +
            "det er hurtigere, når mange arter er slået til. Rækkens sidste felt, med plus, tilføjer din " +
            "egen art, som ikke findes i kataloget.",
    HelpKey.RecordFilters to
        "Knappen «Filtre» øverst til venstre bestemmer, hvilke arters fund og fra hvilken periode der " +
            "vises på kortet, og tallet på den siger, hvor mange filtre der er slået til. Filteret deles " +
            "med «Fundkort»: det, du slår til her, gælder også der.",
    HelpKey.RecordBackground to
        "Optagelsen af sporet fortsætter, når appen er i baggrunden. På Android vises den " +
            "igangværende tur også som en notifikation med «+»/«−»-knapper — et fund kan noteres uden " +
            "at låse telefonen op. Ud over den aktuelle tur viser kortet fund og markerede steder fra " +
            "tidligere ture — af dem kan du se, hvor du allerede har gået, og hvad der var dér.",
    HelpKey.ArchivePurpose to
        "Alle dine ture, de nyeste øverst. På kortet står navn, dato, varighed, kilometer, antal fund og " +
            "et miniaturebillede af det gåede spor.",
    HelpKey.ArchiveDetail to
        "Et tryk på kortet åbner hele turen: statistik, fund efter art, markerede steder, beskrivelse og " +
            "knappen «Se kort». Navn og beskrivelse kan ændres samme sted.",
    HelpKey.ArchiveShare to
        "Knappen «Del» samler et billede af de dele af turen, du sætter flueben ved. Husk, før du sender " +
            "turens kort: man kan se på det, præcis hvor du fandt svampene.",
    HelpKey.ArchiveSelection to
        "Langt tryk på et kort slår markeringstilstand til: markér de ønskede ture med et tryk, og tryk " +
            "på «Slet ture»; knappen «Tilbage» forlader tilstanden. Sletning kan ikke fortrydes — sammen " +
            "med turen forsvinder dens spor, fund, markerede steder og fotos.",
    HelpKey.ArchiveUnfinished to
        "En uafsluttet tur står også på listen: i stedet for sluttidspunkt står der «i gang». Sådan en " +
            "tur har endnu ingen varighed og tæller derfor ikke med i den samlede tid på «Fundkort».",
    HelpKey.MapPurpose to
        "Samlekortet: fund, spor og markerede steder fra alle dine ture på én gang på ét lærred. Det er " +
            "der, så du kan se det store billede — hvor dine svampesteder ligger, og hvordan de ændrer " +
            "sig fra år til år.",
    HelpKey.MapFullScreen to
        "Øverst er kortet med alle fund på én gang; et tryk åbner kortet i fuld skærm. Når der er mange " +
            "fund, samles nære markeringer i en cirkel med et tal — zoom ind, og den falder fra hinanden " +
            "i enkelte svampe. Størrelsen på svampeikonerne indstilles i «Indstillinger».",
    HelpKey.MapSliders to
        "Under kortet er der to skydere — datointerval og sæson, altså et månedsinterval — og alt " +
            "nedenunder beregnes efter det valgte. Skyderne kommer først frem, når du har ture fra mere " +
            "end én dag.",
    HelpKey.MapStats to
        "Under skyderne: hvor mange ture, kilometer, tid og fund der var, felterne pr. art og " +
            "cirkeldiagrammet. Den samlede tid lægger afsluttede ture sammen: en igangværende har endnu " +
            "ingen varighed.",
    HelpKey.MapFilters to
        "Knappen «Filtre» bor på fuldskærmskortet, øverst til venstre: dér er de samme to akser, " +
            "artslisten og kontakten for visning af tidligere spor. Tallet på knappen siger, hvor mange " +
            "filtre der er slået til; filteret deles med optageskærmen.",
    HelpKey.MapPlaces to
        "Et tryk på en stedmarkering åbner dens kort med foto og beskrivelse. Derfra kan stedet også " +
            "ændres eller slettes.",
    HelpKey.SpeciesPurpose to
        "Her bestemmer du, hvilke svampe der står som felter på optageskærmen. Kataloget er delt i " +
            "samlinger efter land, og ved siden af bor de arter, kataloget ikke har — dem tilføjer du " +
            "selv.",
    HelpKey.SpeciesCollections to
        "Under «Svampesamlinger» folder et tryk på et lands række dets arter ud: fluebenet ved " +
            "landet slår hele samlingen til, fluebenene indeni de enkelte arter. Søgefeltet øverst " +
            "finder både et land og en enkelt svamp på navn.",
    HelpKey.SpeciesOwn to
        "Under «Tilføjede svampe» åbner knappen «Tilføj svamp» en formular: navn, videnskabeligt " +
            "navn, markeringsfarve og billede — fra kameraet, fra galleriet eller fra kataloget. " +
            "Derefter spørger appen «Hvilken samling?»: et eget navn samler den slags svampe, et tomt " +
            "felt lægger dem i «Andre». Blyanten ændrer en allerede tilføjet art, krydset sletter " +
            "den.",
    HelpKey.SpeciesCheckboxes to
        "Et fjernet flueben sletter ingenting — arten vises bare ikke længere som felt, og tidligere fund " +
            "bliver, hvor de er. At slette din egen art kan derimod ikke fortrydes: alle dens markeringer " +
            "i tidligere ture flyttes til «Ukendt svamp». Arter med teksten «fra arkiv» kom med " +
            "importerede ture.",
    HelpKey.SpeciesImages to
        "Alle svampebilleder i appen er kun vejledende: de hjælper med at genkende feltet, ikke svampen i " +
            "skoven. Bestem ikke ukendte svampe efter dem.",
    HelpKey.PreparationPurpose to
        "Henter stykker af kortet på forhånd til telefonens hukommelse, så kortet bliver i skoven uden " +
            "internet: uden det får du uden for dækning en tom baggrund i stedet for et kort.",
    HelpKey.PreparationDownload to
        "Find det ønskede område — flyt og zoom kortet — og tryk så på den runde knap med pilen nedad " +
            "nederst til højre. Appen viser, hvor meget plads det aktuelle skærmbillede fylder: «Hent " +
            "dette område» beder om et navn og starter hentningen, «Annullér» bringer kortet tilbage.",
    HelpKey.PreparationRegions to
        "De hentede områder ligger i en stribe nederst. Et tryk på et felt flyver hen til området på " +
            "kortet, og knapperne på feltet sætter hentningen på pause og genoptager den, prøver igen " +
            "efter en fejl og sletter området.",
    HelpKey.PreparationAreaSize to
        "Der hentes præcis det, der er synligt på skærmen, derfor ændrer størrelsesskønnet sig, mens du " +
            "flytter kortet. Jo større område, jo mindre detaljeret må det være — det betaler sig at " +
            "hente flere små områder frem for ét kæmpestort. Områdernes navne må ikke gentages.",
    HelpKey.PreparationBackground to
        "Hentningen kører i baggrunden og afbrydes ikke, hvis du forlader skærmen, og på pause bevares " +
            "fremdriften. «Opdatér kortdata» i «Indstillinger» henter alle gemte områder forfra.",
    HelpKey.DataPurpose to
        "Flytning af ture mellem telefoner og sikkerhedskopi: de valgte ture skrives til én arkivfil, og " +
            "sådan en fil kan indlæses igen — på denne eller en anden enhed.",
    HelpKey.DataExport to
        "Kontakten øverst vælger «Eksport» eller «Import». Under «Eksport» giver du arkivet et navn, " +
            "trykker på rækken til valg af ture, sætter flueben ved de ønskede og trykker «Færdig» — " +
            "telefonen spørger, hvor filen skal gemmes.",
    HelpKey.DataImport to
        "Under «Import» trykker du på «Vælg fil», skriver om ønsket en tilføjelse, der sættes på navnene " +
            "på de indlæste ture, og trykker «Færdig»; når arkivet er læst, kommer knappen «Til arkivet» " +
            "frem. Knappen «Annullér» rydder det indtastede uden at gemme noget.",
    HelpKey.DataArchiveContents to
        "I arkivet kommer sporet, fundene, de markerede steder, fotoene og de svampearter, der ikke er i " +
            "kataloget — på den anden enhed dukker de op under «Tilføjede svampe» med teksten «fra " +
            "arkiv».",
    HelpKey.DataDuplicates to
        "Import lægger altid ture ved siden af de eksisterende og erstatter intet, så den samme fil " +
            "indlæst igen opretter dem endnu en gang: tilføjelsen til navnene hjælper med at skelne dem " +
            "bagefter. Til sidst vises, hvor mange ture der blev indlæst, og hvor mange der ikke kunne " +
            "læses.",
    HelpKey.SettingsPurpose to
        "Appens generelle indstillinger: sprog, udseende, svampefelternes udseende og rækkefølge på " +
            "optageskærmen samt vedligeholdelse af kortet.",
    HelpKey.SettingsLanguage to
        "Rækken «Sprog i appen» åbner listen over sprog: et tryk vælger sprog, fluebenet øverst bekræfter " +
            "valget, pilen går tilbage uden at ændre noget. Sproget gælder straks i hele appen, genstart " +
            "er ikke nødvendig.",
    HelpKey.SettingsTheme to
        "«Udseende» skifter mellem appens lyse og mørke tema. «System» overlader valget til telefonen: " +
            "appen bliver mørk og lys sammen med den.",
    HelpKey.SettingsMushroomSize to
        "Skyderen bestemmer størrelsen på svampeikonerne på kortet — både på optageskærmen og på det " +
            "samlede «Fundkort». Billedet nedenunder ændrer sig allerede, mens du trækker, så størrelsen " +
            "ses, før du slipper.",
    HelpKey.SettingsMushroomOrder to
        "Normalt rykker netop markerede svampe forrest i feltrækken. «Lås rækkefølgen» slår det helt fra, " +
            "og «Nulstil rækkefølgen når turen afsluttes» gendanner den oprindelige rækkefølge, når en " +
            "tur er slut.",
    HelpKey.SettingsMapData to
        "«Opdatér kortdata» tjekker, om kortet er ændret på serveren, og henter i så fald alle gemte " +
            "offlineområder forfra. «Ryd kortets cache» fjerner kun det, der blev hentet undervejs — " +
            "områderne fra «Forhåndsdownload» bliver liggende.",
)
