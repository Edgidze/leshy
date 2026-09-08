package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Svenska — hjälpskärmar, `.claude/plans/help-screens.md`. */
internal val swedishHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "Appens huvudskärm: här spelas promenaden in. GPS ritar ditt spår och varje fynd sparas med " +
            "koordinater och tid — direkt, så promenaden kan avbrytas när som helst utan att det " +
            "inspelade går förlorat.",
    HelpKey.RecordStartFinish to
        "”Start” frågar efter ett namn och startar inspelningen; sedan blir knappen ”Paus”, och i pausen " +
            "dyker ”Fortsätt” och ”Avsluta” upp. ”Avsluta” stänger promenaden och flyttar den till " +
            "”Promenadarkiv”.",
    HelpKey.RecordTiles to
        "Svamprutorna längst ned är det man markerar fynd med: ”+” lägger ett fynd på din nuvarande " +
            "punkt, ”−” tar bort den senaste felaktiga markeringen av den arten. Långt tryck på ”+” " +
            "öppnar inmatning av flera stycken samtidigt; fler än 999 likadana svampar under en promenad " +
            "går inte att markera.",
    HelpKey.RecordPlace to
        "Den runda knappen till vänster markerar en plats — med namn, beskrivning och foto. Platsen " +
            "hamnar där du står just nu och blir kvar på kartan även efter promenaden.",
    HelpKey.RecordNavigation to
        "Långt tryck på en platsmarkering startar navigering dit: panelen uppe till höger visar riktning " +
            "och avstånd till målet. Krysset på panelen stänger av navigeringen.",
    HelpKey.RecordSearchAndOwn to
        "Förstoringsglaset till höger hittar svampen på namn och flyttar dess ruta först i raden — det " +
            "går snabbare när många arter är påslagna. Radens sista ruta, med ett plus, lägger till en " +
            "egen art som inte finns i katalogen.",
    HelpKey.RecordFilters to
        "Knappen ”Filter” uppe till vänster bestämmer vilka arters fynd och från vilken tid som visas på " +
            "kartan, och siffran på den säger hur många filter som är på. Filtret delas med ”Fyndkarta”: " +
            "det du slår på här gäller även där.",
    HelpKey.RecordBackground to
        "Inspelningen av spåret fortsätter när appen ligger i bakgrunden. På Android visas den " +
            "pågående promenaden även som en avisering med knapparna ”+”/”−” — ett fynd kan noteras " +
            "utan att låsa upp telefonen. Utöver den pågående promenaden visar kartan fynd och " +
            "markerade platser från tidigare promenader — av dem syns var du redan gått och vad som " +
            "fanns där.",
    HelpKey.ArchivePurpose to
        "Alla dina promenader, de nyaste överst. På kortet finns namn, datum, längd i tid, kilometer, " +
            "antal fynd och en miniatyr av det gångna spåret.",
    HelpKey.ArchiveDetail to
        "Ett tryck på kortet öppnar hela promenaden: statistik, fynd per art, markerade platser, " +
            "beskrivning och knappen ”Visa karta”. Namn och beskrivning kan ändras på samma ställe.",
    HelpKey.ArchiveShare to
        "Knappen ”Dela” sätter ihop en bild av de delar av promenaden som du kryssar i. Kom ihåg innan du " +
            "skickar promenadens karta: på den syns exakt var du hittade svamparna.",
    HelpKey.ArchiveSelection to
        "Långt tryck på ett kort slår på valläget: markera önskade promenader med ett tryck och tryck ”Ta " +
            "bort promenader”; knappen ”Tillbaka” lämnar läget. Borttagningen går inte att ångra — med " +
            "promenaden försvinner dess spår, fynd, markerade platser och foton.",
    HelpKey.ArchiveUnfinished to
        "En oavslutad promenad syns också i listan: i stället för sluttid står det ”pågår”. En sådan " +
            "promenad har ännu ingen längd och räknas därför inte in i den totala tiden på ”Fyndkarta”.",
    HelpKey.MapPurpose to
        "Samlingskartan: fynd, spår och markerade platser från alla dina promenader på en och samma yta. " +
            "Den finns för att du ska se helheten — var dina svampställen ligger och hur de ändras från " +
            "år till år.",
    HelpKey.MapFullScreen to
        "Överst ligger kartan med alla fynd samtidigt; ett tryck öppnar kartan i helskärm. När fynden är " +
            "många samlas närliggande markeringar i en cirkel med en siffra — zooma in så faller den isär " +
            "i enskilda svampar. Storleken på svampikonerna ställs in i ”Inställningar”.",
    HelpKey.MapSliders to
        "Under kartan finns två reglage — datumintervall och säsong, alltså ett månadsintervall — och " +
            "allt nedanför räknas efter valet. Reglagen dyker upp först när du har promenader från mer än " +
            "en dag.",
    HelpKey.MapStats to
        "Under reglagen: hur många promenader, kilometer, hur mycket tid och hur många fynd det blev, " +
            "rutorna per art och cirkeldiagrammet. Den totala tiden summerar avslutade promenader: en " +
            "pågående har ännu ingen längd.",
    HelpKey.MapFilters to
        "Knappen ”Filter” bor på helskärmskartan, uppe till vänster: där finns samma två axlar, artlistan " +
            "och reglaget för att visa tidigare spår. Siffran på knappen säger hur många filter som är " +
            "på; filtret delas med inspelningsskärmen.",
    HelpKey.MapPlaces to
        "Ett tryck på en platsmarkering öppnar dess kort med foto och beskrivning. Därifrån kan platsen " +
            "också ändras eller tas bort.",
    HelpKey.SpeciesPurpose to
        "Här bestämmer du vilka svampar som ska stå som rutor på inspelningsskärmen. Katalogen är indelad " +
            "i samlingar per land, och bredvid den bor de arter som katalogen saknar — dem lägger du till " +
            "själv.",
    HelpKey.SpeciesCollections to
        "Under ”Svampsamlingar” fäller ett tryck på landets rad ut dess arter: bocken vid landet " +
            "slår på hela samlingen, bockarna inuti enskilda arter. Sökfältet högst upp hittar både " +
            "ett land och en enskild svamp på namn.",
    HelpKey.SpeciesOwn to
        "Under ”Tillagda svampar” öppnar knappen ”Lägg till svamp” ett formulär: namn, " +
            "vetenskapligt namn, markeringsfärg och bild — från kameran, från galleriet eller från " +
            "katalogen. Sedan frågar appen ”Vilken samling?”: ett eget namn samlar sådana svampar, " +
            "ett tomt fält lägger dem i ”Övriga”. Pennan ändrar en redan tillagd art, krysset tar " +
            "bort den.",
    HelpKey.SpeciesCheckboxes to
        "En borttagen bock raderar ingenting — arten slutar bara visas som ruta, och tidigare fynd ligger " +
            "kvar. Att ta bort en egen art går däremot inte att ångra: alla dess markeringar i tidigare " +
            "promenader flyttas till ”Okänd svamp”. Arter märkta ”från arkivet” kom med importerade " +
            "promenader.",
    HelpKey.SpeciesImages to
        "Alla svampbilder i appen är bara vägledande: de hjälper dig att känna igen rutan, inte svampen i " +
            "skogen. Bestäm inte okända svampar efter dem.",
    HelpKey.PreparationPurpose to
        "Laddar ner kartbitar till telefonens minne i förväg, så att kartan finns kvar i skogen utan " +
            "internet: utan det får du utanför täckning en tom bakgrund i stället för karta.",
    HelpKey.PreparationDownload to
        "Hitta området du behöver — flytta och zooma kartan — och tryck sedan på den runda knappen med " +
            "nedåtpil nere till höger. Appen visar hur mycket plats det som syns nu tar: ”Ladda ner det " +
            "här området” frågar efter ett namn och startar nedladdningen, ”Avbryt” tar tillbaka kartan.",
    HelpKey.PreparationRegions to
        "De nedladdade områdena ligger i en rad längst ned. Ett tryck på en bricka flyger till det " +
            "området på kartan, och knapparna på brickan pausar nedladdningen och återupptar den, " +
            "försöker igen efter ett fel och tar bort området.",
    HelpKey.PreparationAreaSize to
        "Det som laddas ner är exakt det som syns på skärmen, därför ändras storleksuppskattningen medan " +
            "du flyttar kartan. Ju större område, desto mindre detaljerat måste det bli — det lönar sig " +
            "att ladda ner flera små områden i stället för ett enormt. Områdenas namn får inte upprepas.",
    HelpKey.PreparationBackground to
        "Nedladdningen går i bakgrunden och avbryts inte om du lämnar skärmen, och i paus bevaras " +
            "förloppet. ”Uppdatera kartdata” i ”Inställningar” laddar ner alla sparade områden på nytt.",
    HelpKey.DataPurpose to
        "Flytt av promenader mellan telefoner och säkerhetskopia: de valda promenaderna skrivs till en " +
            "enda arkivfil, och en sådan fil kan läsas in igen — på den här eller på en annan enhet.",
    HelpKey.DataExport to
        "Reglaget överst väljer ”Export” eller ”Import”. Under ”Export” ger du arkivet ett namn, trycker " +
            "på raden för val av promenader, kryssar i de önskade och trycker ”Klar” — telefonen frågar " +
            "var filen ska sparas.",
    HelpKey.DataImport to
        "Under ”Import” trycker du ”Välj fil”, skriver vid behov ett tillägg som läggs till namnen på de " +
            "inlästa promenaderna och trycker ”Klar”; när arkivet har lästs dyker knappen ”Till arkivet” " +
            "upp. Knappen ”Avbryt” rensar det du skrivit utan att spara något.",
    HelpKey.DataArchiveContents to
        "I arkivet hamnar spåret, fynden, de markerade platserna, fotona och de svamparter som saknas i " +
            "katalogen — på den andra enheten dyker de upp under ”Tillagda svampar” med texten ”från " +
            "arkivet”.",
    HelpKey.DataDuplicates to
        "Import lägger alltid promenaderna bredvid de befintliga och ersätter ingenting, så samma fil " +
            "inläst igen skapar dem en gång till: tillägget i namnen hjälper dig att skilja dem åt " +
            "efteråt. Till sist visas hur många promenader som lästes in och hur många som inte gick att " +
            "läsa.",
    HelpKey.SettingsPurpose to
        "Appens allmänna inställningar: gränssnittsspråk, utseende, svamprutornas utseende och ordning på " +
            "inspelningsskärmen samt underhåll av kartan.",
    HelpKey.SettingsLanguage to
        "Raden ”Gränssnittsspråk” öppnar listan över språk: ett tryck väljer språk, bocken överst " +
            "bekräftar valet, pilen går tillbaka utan att ändra något. Språket gäller genast i hela " +
            "appen, omstart behövs inte.",
    HelpKey.SettingsTheme to
        "”Utseende” växlar mellan appens ljusa och mörka tema. ”System” överlåter valet till telefonen: " +
            "appen blir mörk och ljus tillsammans med den.",
    HelpKey.SettingsMushroomSize to
        "Reglaget bestämmer svampikonernas storlek på kartan — både på inspelningsskärmen och på den " +
            "samlade ”Fyndkarta”. Bilden under ändras redan medan du drar, så storleken syns innan du " +
            "släpper.",
    HelpKey.SettingsMushroomOrder to
        "Vanligtvis flyttas nyss markerade svampar först i rutraden. ”Lås svampordningen” stänger av det " +
            "helt, och ”Återställ svampordningen när promenaden avslutas” återställer den ursprungliga " +
            "ordningen när en promenad är klar.",
    HelpKey.SettingsMapData to
        "”Uppdatera kartdata” kontrollerar om kartan har ändrats på servern och laddar i så fall ner alla " +
            "sparade offlineområden på nytt. ”Rensa kartcache” tar bara bort det som lästes in medan du " +
            "tittade — områdena från ”Förhandsnedladdning” ligger kvar.",
)
