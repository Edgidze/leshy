package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Eesti — abiekraanid, `.claude/plans/help-screens.md`. */
internal val estonianHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "Rakenduse põhiekraan: siin salvestatakse matk. GPS-i järgi kirjutatakse teie rada ja iga leid " +
            "salvestatakse koordinaatide ja kellaajaga — mällu jõuab see kohe, nii et matka võib igal " +
            "hetkel katkestada ja salvestatu ei kao.",
    HelpKey.RecordStartFinish to
        "„Alusta“ küsib nime ja alustab salvestamist; seejärel muutub nupp „Paus“-iks ning pausi ajal " +
            "ilmuvad „Jätka“ ja „Lõpeta“. „Lõpeta“ sulgeb matka ja viib selle „Matkade arhiivi“.",
    HelpKey.RecordTiles to
        "Seenepaanid all on see, millega leide märgitakse: „+“ märgib leiu teie praeguses punktis, „−“ " +
            "eemaldab selle liigi viimase eksliku märke. Pikk vajutus „+“ peal avab mitme tüki korraga " +
            "sisestamise; rohkem kui 999 sama liigi seent ühe matka kohta märkida ei saa.",
    HelpKey.RecordPlace to
        "Ümmargune nupp vasakul märgib koha — nime, kirjelduse ja fotoga. Koht pannakse sinna, kus te " +
            "parasjagu seisate, ja jääb kaardile ka pärast matka.",
    HelpKey.RecordNavigation to
        "Pikk vajutus koha tähisel lülitab sisse navigatsiooni selleni: paneel üleval paremal näitab " +
            "suunda ja kaugust sihtkohani. Rist paneelil lülitab navigatsiooni välja.",
    HelpKey.RecordSearchAndOwn to
        "Luup paremal leiab seene nime järgi ja viib selle paani riba algusesse — nii on kiirem, kui " +
            "liike on sisse lülitatud palju. Riba viimane paan, plussiga, lisab oma liigi, mida " +
            "kataloogis pole.",
    HelpKey.RecordFilters to
        "Nupp „Filtrid“ üleval vasakul määrab, milliste liikide ja millise aja leide kaardil näidata, " +
            "ning number sellel ütleb, mitu filtrit on praegu sees. Filter on ühine „Leidude kaardiga“: " +
            "siin sisse lülitatu kehtib ka seal.",
    HelpKey.RecordBackground to
        "Raja salvestamine jätkub ka siis, kui rakendus on taustal. Peale praeguse matka näitab kaart " +
            "varasemate matkade leide ja märgitud kohti — nende järgi on näha, kus te juba käisite ja mis " +
            "seal oli.",
    HelpKey.ArchivePurpose to
        "Kõik teie matkad, uuemad üleval. Kaardil on nimi, kuupäev, kestus, kilomeetrid, leidude arv ja " +
            "läbitud raja pisipilt.",
    HelpKey.ArchiveDetail to
        "Vajutus kaardile avab kogu matka: statistika, leiud liikide kaupa, märgitud kohad, kirjeldus ja " +
            "nupp „Vaata kaarti“. Nime ja kirjeldust saab muuta samas.",
    HelpKey.ArchiveShare to
        "Nupp „Jaga“ paneb pildi kokku neist matka osadest, mille linnukesega märgite. Enne kui matka " +
            "kaardi ära saadate, pidage meeles: sellelt on näha, kust täpselt te seened leidsite.",
    HelpKey.ArchiveSelection to
        "Pikk vajutus kaardil lülitab sisse valikurežiimi: märkige soovitud matkad vajutusega ja vajutage " +
            "„Kustuta matkad“, nupp „Tagasi“ väljub sellest režiimist. Kustutamine on pöördumatu — koos " +
            "matkaga kaovad selle rada, leiud, märgitud kohad ja fotod.",
    HelpKey.ArchiveUnfinished to
        "Lõpetamata matk on samuti loendis näha: lõpuaja asemel on tal kirjas „pooleli“. Sellisel matkal " +
            "pole veel kestust, seetõttu ei lähe ta „Leidude kaardi“ koguaja sisse.",
    HelpKey.MapPurpose to
        "Koondkaart: kõigi teie matkade leiud, rajad ja märgitud kohad korraga ühel lõuendil. Vajalik " +
            "selleks, et näha üldpilti — kus on teie seenekohad ja kuidas need aastate lõikes muutuvad.",
    HelpKey.MapFullScreen to
        "Ülal on kaart kõigi leidudega korraga; vajutus sellele avab kaardi üle terve ekraani. Kui leide " +
            "on palju, koonduvad lähestikku tähised numbriga ringiks — suumige kaarti lähemale ja see " +
            "laguneb üksikuteks seenteks. Seeneikoonide suurust seatakse „Seadetes“.",
    HelpKey.MapSliders to
        "Kaardi all on kaks liugurit — kuupäevavahemik ja hooaeg ehk kuude vahemik — ja kõike nende all " +
            "arvutatakse valitu järgi. Liugurid ilmuvad alles siis, kui matku on rohkem kui ühest " +
            "päevast.",
    HelpKey.MapStats to
        "Liugurite all: mitu matka, kilomeetrit, aega ja leidu oli, paanid liikide kaupa ja " +
            "sektordiagramm. Koguaeg liidab kokku lõpetatud matkad: lõpetamata matkal pole veel kestust.",
    HelpKey.MapFilters to
        "Nupp „Filtrid“ elab täisekraankaardil, selle ülal vasakul: seal on samad kaks telge, liikide " +
            "loend ja varasemate radade näitamise lüliti. Number nupul ütleb, mitu filtrit on sees; " +
            "filter on ühine salvestusekraaniga.",
    HelpKey.MapPlaces to
        "Vajutus koha tähisel avab selle kaardi foto ja kirjeldusega. Sealtsamast saab kohta ka muuta või " +
            "kustutada.",
    HelpKey.SpeciesPurpose to
        "Siin otsustate, millised seened on salvestusekraanil paanidena. Kataloog on jaotatud kogudeks " +
            "riikide kaupa ja selle kõrval elavad liigid, mida kataloogis pole — need lisate ise.",
    HelpKey.SpeciesCollections to
        "„Seenekogudes“ avab vajutus riigi real selle liigid: linnuke riigi juures lülitab sisse kogu " +
            "kogumi, linnukesed sees üksikud liigid. Otsinguväli ülal leiab riigi nime järgi.",
    HelpKey.SpeciesOwn to
        "Jaotises „Lisatud seened“ avab nupp „Lisa seen“ vormi: nimi, teaduslik nimi, tähise värv ja pilt " +
            "— kaamerast, galeriist või kataloogist. Pliiats muudab juba lisatud liiki, rist kustutab " +
            "selle.",
    HelpKey.SpeciesCheckboxes to
        "Eemaldatud linnuke ei kustuta midagi — liik lihtsalt ei ilmu enam paanina ja varasemad leiud " +
            "jäävad oma kohale. Oma liigi kustutamine on seevastu pöördumatu: kõik selle märked " +
            "varasematel matkadel lähevad üle „Tundmatule seenele“. Liigid sildiga „arhiivist“ tulid koos " +
            "imporditud matkadega.",
    HelpKey.SpeciesImages to
        "Kõik seenepildid rakenduses on tinglikud: nad aitavad ära tunda paani, mitte seent metsas. Ärge " +
            "määrake nende järgi tundmatuid seeni.",
    HelpKey.PreparationPurpose to
        "Laadib kaarditükid ette telefoni mällu, et kaart jääks metsas ilma internetita alles: ilma " +
            "selleta on levist väljas kaardi asemel tühi taust.",
    HelpKey.PreparationDownload to
        "Leidke vajalik ala — liigutage ja suumige kaarti — seejärel vajutage ümmargust allanoolega nuppu " +
            "all paremal. Rakendus näitab, kui palju ruumi võtab see, mis on praegu ekraanil: „Laadi alla " +
            "see piirkond“ küsib nime ja alustab allalaadimist, „Loobu“ toob kaardi tagasi.",
    HelpKey.PreparationRegions to
        "Allalaaditud alad on ribana all. Vajutus sildile lendab kaardil selle alani ja sildi nupud " +
            "panevad allalaadimise pausile ja jätkavad seda, kordavad katset pärast viga ning kustutavad " +
            "ala.",
    HelpKey.PreparationAreaSize to
        "Alla laaditakse täpselt see, mis on ekraanil näha, seetõttu muutub mahu hinnang kaardi " +
            "liigutamise ajal. Mida suurem ala, seda vähem üksikasjalikuks tuleb see teha — tasuvam on " +
            "laadida mitu väiksemat ala kui üks hiiglaslik. Alade nimed ei tohi korduda.",
    HelpKey.PreparationBackground to
        "Allalaadimine käib taustal ega katke, kui ekraanilt lahkute, ja pausi ajal säilib edenemine. " +
            "„Uuenda kaardiandmeid“ „Seadetes“ laadib kõik salvestatud alad uuesti alla.",
    HelpKey.DataPurpose to
        "Matkade ülekandmine telefonide vahel ja varukoopia: valitud matkad eksporditakse ühte " +
            "arhiivifaili ja sellise faili saab laadida tagasi — sellesse või teise seadmesse.",
    HelpKey.DataExport to
        "Lüliti ülal valib „Eksport“ või „Import“. Jaotises „Eksport“ määrake arhiivi nimi, vajutage " +
            "matkade valiku reale ja märkige vajalikud, seejärel „Valmis“ — telefon küsib, kuhu fail " +
            "salvestada.",
    HelpKey.DataImport to
        "Jaotises „Import“ vajutage „Vali fail“, soovi korral kirjutage lisand, mis lisatakse laaditavate " +
            "matkade nimedele, ja vajutage „Valmis“; kui arhiiv on loetud, ilmub nupp „Arhiivi“. Nupp " +
            "„Loobu“ tühjendab sisestatu midagi salvestamata.",
    HelpKey.DataArchiveContents to
        "Arhiivi lähevad rada, leiud, märgitud kohad, fotod ja need seeneliigid, mida kataloogis pole — " +
            "teises seadmes ilmuvad need jaotisse „Lisatud seened“ sildiga „arhiivist“.",
    HelpKey.DataDuplicates to
        "Import lisab matkad alati olemasolevate kõrvale ega asenda midagi, seega sama faili uuesti " +
            "laadimine loob need veel kord: nimede lisand aitab neid hiljem eristada. Lõpus näidatakse, " +
            "mitu matka laaditi ja mitut ei õnnestunud lugeda.",
    HelpKey.SettingsPurpose to
        "Rakenduse üldised seaded: liidese keel, välimus, seenepaanide välimus ja järjekord " +
            "salvestusekraanil ning kaardi hooldus.",
    HelpKey.SettingsLanguage to
        "Rida „Liidese keel“ avab keelte loendi: vajutus valib keele, linnuke ülal kinnitab valiku, nool " +
            "väljub midagi muutmata. Keel rakendub kohe kogu rakenduses, taaskäivitust pole vaja.",
    HelpKey.SettingsTheme to
        "„Välimus“ lülitab rakenduse heleda ja tumeda teema vahel. „Süsteemne“ annab valiku telefonile: " +
            "rakendus tumeneb ja heleneb koos sellega.",
    HelpKey.SettingsMushroomSize to
        "Liugur määrab seeneikoonide suuruse kaardil — nii salvestusekraanil kui ka koond-„Leidude " +
            "kaardil“. Liuguri all olev pilt muutub juba lohistamise ajal, nii et suurust näete enne " +
            "lahtilaskmist.",
    HelpKey.SettingsMushroomOrder to
        "Tavaliselt tõusevad äsja märgitud seened paaniriba algusesse. „Muutumatu seente järjekord“ " +
            "lülitab selle täiesti välja ja „Lähtesta seente järjekord matka lõpus“ taastab algse " +
            "järjekorra, kui matk on lõpetatud.",
    HelpKey.SettingsMapData to
        "„Uuenda kaardiandmeid“ kontrollib, kas kaart on serveris muutunud, ja kui on, laadib kõik " +
            "salvestatud võrguühenduseta alad uuesti alla. „Tühjenda kaardi vahemälu“ eemaldab ainult " +
            "selle, mis laaditi sirvimise ajal — „Eellaadimise“ alad jäävad alles.",
)
