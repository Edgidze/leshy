package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Norsk bokmål — hjelpeskjermer, `.claude/plans/help-screens.md`. */
internal val norwegianHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "Appens hovedskjerm: her tas turen opp. GPS tegner sporet ditt, og hvert funn lagres med " +
            "koordinater og tidspunkt — med én gang, så turen kan avbrytes når som helst uten at det " +
            "opptatte går tapt.",
    HelpKey.RecordStartFinish to
        "«Start» spør om et navn og begynner opptaket; deretter blir knappen til «Pause», og i pause " +
            "dukker «Fortsett» og «Avslutt» opp. «Avslutt» lukker turen og flytter den til «Turarkiv».",
    HelpKey.RecordTiles to
        "Soppflisene nederst er det funn merkes med: «+» setter et funn i punktet du står i nå, «−» " +
            "fjerner den siste feilaktige merkingen av den arten. Langt trykk på «+» åpner inntasting av " +
            "flere stykker samtidig; mer enn 999 like sopper på én tur kan ikke merkes.",
    HelpKey.RecordPlace to
        "Den runde knappen til venstre merker et sted — med navn, beskrivelse og bilde. Stedet plasseres " +
            "der du står nå, og blir liggende på kartet også etter turen.",
    HelpKey.RecordNavigation to
        "Langt trykk på en stedsmarkør slår på navigasjon dit: panelet øverst til høyre viser retning og " +
            "avstand til målet. Krysset på panelet slår av navigasjonen.",
    HelpKey.RecordSearchAndOwn to
        "Forstørrelsesglasset til høyre finner soppen på navn og flytter flisen fremst i raden — det går " +
            "raskere når mange arter er slått på. Radens siste flis, med pluss, legger til din egen art " +
            "som ikke finnes i katalogen.",
    HelpKey.RecordFilters to
        "Knappen «Filtre» øverst til venstre bestemmer hvilke arters funn og fra hvilken periode som " +
            "vises på kartet, og tallet på den sier hvor mange filtre som er på nå. Filteret deles med " +
            "«Funnkart»: det du slår på her, gjelder også der.",
    HelpKey.RecordBackground to
        "Opptaket av sporet fortsetter når appen er i bakgrunnen. På Android ligger turen som " +
            "pågår også som et varsel med «+»/«−»-knapper — et funn kan noteres uten å låse opp " +
            "telefonen. I tillegg til dagens tur viser kartet funn og merkede steder fra tidligere " +
            "turer — av dem ser du hvor du allerede har gått og hva som var der.",
    HelpKey.ArchivePurpose to
        "Alle turene dine, de nyeste øverst. På kortet står navn, dato, varighet, kilometer, antall funn " +
            "og et miniatyrbilde av sporet du gikk.",
    HelpKey.ArchiveDetail to
        "Et trykk på kortet åpner hele turen: statistikk, funn etter art, merkede steder, beskrivelse og " +
            "knappen «Se kartet». Navn og beskrivelse kan endres samme sted.",
    HelpKey.ArchiveShare to
        "Knappen «Del» setter sammen et bilde av de delene av turen du krysser av. Husk før du sender " +
            "turens kart: det viser nøyaktig hvor du fant soppen.",
    HelpKey.ArchiveSelection to
        "Langt trykk på et kort slår på valgmodus: merk turene du vil ha med et trykk, og trykk «Slett " +
            "turer»; knappen «Tilbake» går ut av modusen. Sletting kan ikke angres — sammen med turen " +
            "forsvinner sporet, funnene, de merkede stedene og bildene.",
    HelpKey.ArchiveUnfinished to
        "En uavsluttet tur står også i listen: i stedet for sluttidspunkt står det «pågår». En slik tur " +
            "har ennå ingen varighet og teller derfor ikke i den samlede tiden på «Funnkart».",
    HelpKey.MapPurpose to
        "Samlekartet: funn, spor og merkede steder fra alle turene dine samtidig på ett lerret. Det er " +
            "der for at du skal se helheten — hvor soppstedene dine ligger og hvordan de endrer seg fra " +
            "år til år.",
    HelpKey.MapFullScreen to
        "Øverst er kartet med alle funn samtidig; et trykk åpner kartet i fullskjerm. Når det er mange " +
            "funn, samler nære markører seg i en sirkel med et tall — zoom inn, så faller den fra " +
            "hverandre i enkeltsopper. Størrelsen på soppikonene stilles i «Innstillinger».",
    HelpKey.MapSliders to
        "Under kartet er det to glidere — datointervall og sesong, altså et månedsintervall — og alt " +
            "nedenfor regnes ut etter valget. Gliderne dukker først opp når du har turer fra mer enn én " +
            "dag.",
    HelpKey.MapStats to
        "Under gliderne: hvor mange turer, kilometer, tid og funn det ble, flisene per art og " +
            "sektordiagrammet. Samlet tid summerer avsluttede turer: en pågående har ennå ingen varighet.",
    HelpKey.MapFilters to
        "Knappen «Filtre» bor på fullskjermkartet, øverst til venstre: der er de samme to aksene, " +
            "artslisten og bryteren for visning av tidligere spor. Tallet på knappen sier hvor mange " +
            "filtre som er på; filteret deles med opptaksskjermen.",
    HelpKey.MapPlaces to
        "Et trykk på en stedsmarkør åpner kortet dets med bilde og beskrivelse. Derfra kan stedet også " +
            "endres eller slettes.",
    HelpKey.SpeciesPurpose to
        "Her bestemmer du hvilke sopper som står som fliser på opptaksskjermen. Katalogen er delt i " +
            "samlinger etter land, og ved siden av bor artene katalogen ikke har — dem legger du til " +
            "selv.",
    HelpKey.SpeciesCollections to
        "Under «Soppsamlinger» folder et trykk på landets rad ut artene: haken ved landet slår på " +
            "hele samlingen, hakene inni de enkelte artene. Søkefeltet øverst finner både et land og " +
            "en enkelt sopp på navn.",
    HelpKey.SpeciesOwn to
        "Under «Lagte til sopper» åpner knappen «Legg til sopp» et skjema: navn, vitenskapelig " +
            "navn, markørfarge og bilde — fra kameraet, fra galleriet eller fra katalogen. Deretter " +
            "spør appen «Hvilken samling?»: et eget navn samler slike sopper, et tomt felt legger dem " +
            "i «Andre». Blyanten endrer en art du har lagt til, krysset sletter den.",
    HelpKey.SpeciesCheckboxes to
        "En fjernet hake sletter ingenting — arten vises bare ikke lenger som flis, og tidligere funn " +
            "blir der de er. Å slette din egen art kan derimot ikke angres: alle merkingene dens i " +
            "tidligere turer går over til «Ukjent sopp». Arter merket «fra arkiv» kom sammen med " +
            "importerte turer.",
    HelpKey.SpeciesImages to
        "Alle soppbildene i appen er bare veiledende: de hjelper deg å kjenne igjen flisen, ikke soppen i " +
            "skogen. Ikke bestem ukjente sopper etter dem.",
    HelpKey.PreparationPurpose to
        "Laster ned kartbiter til telefonens minne på forhånd, slik at kartet blir værende i skogen uten " +
            "internett: uten dette får du utenfor dekning en tom bakgrunn i stedet for kart.",
    HelpKey.PreparationDownload to
        "Finn området du trenger — flytt og zoom kartet — og trykk så den runde knappen med pil ned " +
            "nederst til høyre. Appen viser hvor mye plass det som er på skjermen nå vil ta: «Last ned " +
            "dette området» spør om navn og starter nedlastingen, «Avbryt» henter kartet tilbake.",
    HelpKey.PreparationRegions to
        "De nedlastede områdene ligger i en stripe nederst. Et trykk på en flis flyr til det området på " +
            "kartet, og knappene på flisen setter nedlastingen på pause og gjenopptar den, prøver på nytt " +
            "etter en feil og sletter området.",
    HelpKey.PreparationAreaSize to
        "Det lastes ned nøyaktig det som er synlig på skjermen, derfor endrer størrelsesanslaget seg mens " +
            "du flytter kartet. Jo større område, jo mindre detaljert må det være — det lønner seg å " +
            "laste ned flere små områder enn ett kjempestort. Navnene på områdene må ikke gjentas.",
    HelpKey.PreparationBackground to
        "Nedlastingen går i bakgrunnen og avbrytes ikke om du forlater skjermen, og i pause tas " +
            "fremdriften vare på. «Oppdater kartdata» i «Innstillinger» laster ned alle lagrede områder " +
            "på nytt.",
    HelpKey.DataPurpose to
        "Flytting av turer mellom telefoner og sikkerhetskopi: de valgte turene skrives til én arkivfil, " +
            "og en slik fil kan lastes inn igjen — på denne eller en annen enhet.",
    HelpKey.DataExport to
        "Bryteren øverst velger «Eksport» eller «Import». Under «Eksport» gir du arkivet et navn, trykker " +
            "på raden for valg av turer, huker av de ønskede og trykker «Ferdig» — telefonen spør hvor " +
            "filen skal lagres.",
    HelpKey.DataImport to
        "Under «Import» trykker du «Velg fil», skriver om ønskelig et tillegg som settes på navnene til " +
            "turene som lastes inn, og trykker «Ferdig»; når arkivet er lest, dukker knappen «Til " +
            "arkivet» opp. Knappen «Avbryt» tømmer det du skrev, uten å lagre noe.",
    HelpKey.DataArchiveContents to
        "I arkivet havner sporet, funnene, de merkede stedene, bildene og de soppartene som ikke finnes i " +
            "katalogen — på den andre enheten dukker de opp under «Lagte til sopper» med teksten «fra " +
            "arkiv».",
    HelpKey.DataDuplicates to
        "Import legger alltid turene ved siden av de eksisterende og erstatter ingenting, så samme fil " +
            "lastet inn på nytt lager dem en gang til: tillegget i navnene hjelper deg å skille dem " +
            "etterpå. Til slutt vises hvor mange turer som ble lastet inn og hvor mange som ikke kunne " +
            "leses.",
    HelpKey.SettingsPurpose to
        "Appens generelle innstillinger: språk, utseende, soppflisenes utseende og rekkefølge på " +
            "opptaksskjermen, og vedlikehold av kartet.",
    HelpKey.SettingsLanguage to
        "Raden «Språk i appen» åpner listen over språk: et trykk velger språk, haken øverst bekrefter " +
            "valget, pilen går ut uten å endre noe. Språket gjelder umiddelbart i hele appen, omstart " +
            "trengs ikke.",
    HelpKey.SettingsTheme to
        "«Utseende» bytter mellom appens lyse og mørke tema. «System» overlater valget til telefonen: " +
            "appen blir mørk og lys sammen med den.",
    HelpKey.SettingsMushroomSize to
        "Glideren bestemmer størrelsen på soppikonene på kartet — både på opptaksskjermen og på det " +
            "samlede «Funnkart». Bildet under endrer seg allerede mens du drar, så størrelsen ses før du " +
            "slipper.",
    HelpKey.SettingsMushroomOrder to
        "Vanligvis flyttes nettopp merkede sopper fremst i flisraden. «Lås rekkefølgen» slår dette helt " +
            "av, og «Nullstill rekkefølgen når turen avsluttes» gjenoppretter den opprinnelige " +
            "rekkefølgen når en tur er ferdig.",
    HelpKey.SettingsMapData to
        "«Oppdater kartdata» sjekker om kartet er endret på serveren, og laster i så fall ned alle " +
            "lagrede frakoblede områder på nytt. «Tøm kartbufferen» fjerner bare det som ble lastet " +
            "underveis — områdene fra «Forhåndsnedlasting» blir liggende.",
)
