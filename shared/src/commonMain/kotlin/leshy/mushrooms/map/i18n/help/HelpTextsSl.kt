package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Slovenščina — zasloni s pomočjo, `.claude/plans/help-screens.md`. */
internal val slovenianHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "Glavni zaslon aplikacije: tu se snema sprehod. Prek GPS se beleži vaša sled, vsaka najdba pa se " +
            "shrani s koordinatami in časom — in gre v pomnilnik takoj, zato lahko sprehod kadar koli " +
            "prekinete, zabeleženo pa ne bo izgubljeno.",
    HelpKey.RecordStartFinish to
        "„Začni“ vpraša za ime in začne snemanje; nato se gumb spremeni v „Premor“, med premorom pa se " +
            "pojavita „Nadaljuj“ in „Končaj“. „Končaj“ sprehod zapre in ga premakne v „Arhiv sprehodov“.",
    HelpKey.RecordTiles to
        "Ploščice gob spodaj so tisto, s čimer se beležijo najdbe: „+“ zabeleži najdbo na vaši trenutni " +
            "točki, „−“ odstrani zadnjo napačno oznako te vrste. Dolg pritisk na „+“ odpre vnos več kosov " +
            "hkrati; več kot 999 enakih gob v enem sprehodu ni mogoče označiti.",
    HelpKey.RecordPlace to
        "Okrogli gumb levo označi kraj — z imenom, opisom in fotografijo. Kraj se postavi tam, kjer " +
            "trenutno stojite, in ostane na zemljevidu tudi po sprehodu.",
    HelpKey.RecordNavigation to
        "Dolg pritisk na oznako kraja vklopi navigacijo do njega: plošča zgoraj desno kaže smer in " +
            "razdaljo do cilja. Križec na plošči navigacijo izklopi.",
    HelpKey.RecordSearchAndOwn to
        "Lupa desno poišče gobo po imenu in postavi njeno ploščico na začetek traku — tako je hitreje, " +
            "kadar je vklopljenih veliko vrst. Zadnja ploščica traku, s plusom, doda vašo vrsto, ki je v " +
            "katalogu ni.",
    HelpKey.RecordFilters to
        "Gumb „Filtri“ zgoraj levo določa, najdbe katerih vrst in iz katerega obdobja naj se kažejo na " +
            "zemljevidu, številka na njem pa pove, koliko filtrov je zdaj vklopljenih. Filter je skupen z " +
            "„Zemljevidom najdb“: vklopljeno tukaj velja tudi tam.",
    HelpKey.RecordBackground to
        "Snemanje sledi se nadaljuje tudi, ko je aplikacija v ozadju. Poleg trenutnega sprehoda zemljevid " +
            "kaže najdbe in označene kraje prejšnjih sprehodov — iz njih je videti, kod ste že hodili in " +
            "kaj je tam bilo.",
    HelpKey.ArchivePurpose to
        "Vsi vaši sprehodi, najnovejši zgoraj. Na kartici so ime, datum, trajanje, kilometri, število " +
            "najdb in sličica prehojene sledi.",
    HelpKey.ArchiveDetail to
        "Pritisk na kartico odpre cel sprehod: statistika, najdbe po vrstah, označeni kraji, opis in gumb " +
            "„Poglej zemljevid“. Ime in opis je mogoče spremeniti prav tam.",
    HelpKey.ArchiveShare to
        "Gumb „Deli“ sestavi sliko iz tistih delov sprehoda, ki jih odkljukate. Preden pošljete zemljevid " +
            "sprehoda, pomnite: z njega je razvidno, kje točno ste našli gobe.",
    HelpKey.ArchiveSelection to
        "Dolg pritisk na kartico vklopi način izbire: s pritiskom označite želene sprehode in pritisnite " +
            "„Izbriši sprehode“, gumb „Nazaj“ pa iz tega načina izstopi. Brisanje je nepovratno — skupaj " +
            "s sprehodom izginejo njegova sled, najdbe, označeni kraji in fotografije.",
    HelpKey.ArchiveUnfinished to
        "Nedokončan sprehod je prav tako viden na seznamu: namesto časa konca piše „ni končan“. Tak " +
            "sprehod še nima trajanja, zato se ne šteje v skupni čas na „Zemljevidu najdb“.",
    HelpKey.MapPurpose to
        "Skupni zemljevid: najdbe, sledi in označeni kraji vseh vaših sprehodov naenkrat na enem platnu. " +
            "Je tu zato, da vidite celotno sliko — kje so vaša gobarska mesta in kako se spreminjajo iz " +
            "leta v leto.",
    HelpKey.MapFullScreen to
        "Zgoraj je zemljevid z vsemi najdbami naenkrat; pritisk nanj odpre zemljevid čez cel zaslon. Ko " +
            "je najdb veliko, se bližnje oznake združijo v krogec s številko — približajte zemljevid in " +
            "razpadel bo na posamezne gobe. Velikost ikon gob se nastavi v „Nastavitvah“.",
    HelpKey.MapSliders to
        "Pod zemljevidom sta dva drsnika — razpon datumov in sezona, torej razpon mesecev — in vse pod " +
            "njima se šteje po izbranem. Drsnika se pojavita šele, ko imate sprehode iz več kot enega " +
            "dneva.",
    HelpKey.MapStats to
        "Pod drsnikoma: koliko je bilo sprehodov, kilometrov, časa in najdb, ploščice po vrstah in tortni " +
            "diagram. Skupni čas sešteva končane sprehode: nedokončani še nima trajanja.",
    HelpKey.MapFilters to
        "Gumb „Filtri“ živi na celozaslonskem zemljevidu, zgoraj levo: tam sta isti dve osi, seznam vrst " +
            "in stikalo za prikaz prejšnjih sledi. Številka na gumbu pove, koliko filtrov je vklopljenih; " +
            "filter je skupen z zaslonom snemanja.",
    HelpKey.MapPlaces to
        "Pritisk na oznako kraja odpre njegovo kartico s fotografijo in opisom. Od tam je kraj mogoče " +
            "tudi spremeniti ali izbrisati.",
    HelpKey.SpeciesPurpose to
        "Tu odločate, katere gobe bodo ploščice na zaslonu snemanja. Katalog je razdeljen na zbirke po " +
            "državah, poleg njega pa živijo vrste, ki jih v katalogu ni — te dodate sami.",
    HelpKey.SpeciesCollections to
        "V „Zbirkah gob“ pritisk na vrstico države razgrne njene vrste: kljukica pri državi vklopi " +
            "celotno zbirko, kljukice znotraj pa posamezne vrste. Iskalno polje zgoraj poišče državo po " +
            "imenu.",
    HelpKey.SpeciesOwn to
        "V „Dodanih gobah“ gumb „Dodaj gobo“ odpre obrazec: ime, znanstveno ime, barva oznake in slika — " +
            "s kamere, iz galerije ali iz kataloga. Svinčnik spremeni že dodano vrsto, križec jo izbriše.",
    HelpKey.SpeciesCheckboxes to
        "Odstranjena kljukica ne izbriše ničesar — vrsta se preprosto ne kaže več kot ploščica, prejšnje " +
            "najdbe pa ostanejo na svojem mestu. Brisanje lastne vrste je nasprotno nepovratno: vse njene " +
            "oznake v prejšnjih sprehodih preidejo v „Neznano gobo“. Vrste z napisom „iz arhiva“ so " +
            "prišle skupaj z uvoženimi sprehodi.",
    HelpKey.SpeciesImages to
        "Vse slike gob v aplikaciji so zgolj okvirne: pomagajo prepoznati ploščico, ne gobe v gozdu. " +
            "Neznanih gob ne določajte po njih.",
    HelpKey.PreparationPurpose to
        "Vnaprej prenese kose zemljevida v pomnilnik telefona, da zemljevid v gozdu brez interneta ostane " +
            "na mestu: brez tega bo zunaj dosega namesto zemljevida prazno ozadje.",
    HelpKey.PreparationDownload to
        "Poiščite želeno območje — premikajte in približujte zemljevid — nato pritisnite okrogli gumb s " +
            "puščico navzdol spodaj desno. Aplikacija pokaže, koliko prostora bo zavzelo to, kar je zdaj " +
            "na zaslonu: „Prenesi to območje“ vpraša za ime in začne prenos, „Prekliči“ vrne zemljevid.",
    HelpKey.PreparationRegions to
        "Prenesena območja stojijo v traku spodaj. Pritisk na ploščico odleti do tega območja na " +
            "zemljevidu, gumbi na ploščici pa prenos zaustavijo in nadaljujejo, ponovijo poskus po napaki " +
            "in območje izbrišejo.",
    HelpKey.PreparationAreaSize to
        "Prenese se natanko to, kar je vidno na zaslonu, zato se ocena velikosti spreminja, medtem ko " +
            "premikate zemljevid. Večje ko je območje, manj podrobno mora biti — bolje je prenesti nekaj " +
            "manjših odsekov kot enega ogromnega. Imena območij se ne smejo ponavljati.",
    HelpKey.PreparationBackground to
        "Prenos teče v ozadju in se ne prekine, če zapustite zaslon, med premorom pa se napredek ohrani. " +
            "„Posodobi podatke zemljevida“ v „Nastavitvah“ znova prenese vsa shranjena območja.",
    HelpKey.DataPurpose to
        "Prenos sprehodov med telefoni in varnostna kopija: izbrani sprehodi se izvozijo v eno arhivsko " +
            "datoteko, tako datoteko pa je mogoče naložiti nazaj — na tej ali na drugi napravi.",
    HelpKey.DataExport to
        "Stikalo zgoraj izbere „Izvoz“ ali „Uvoz“. V „Izvozu“ določite ime arhiva, pritisnite vrstico za " +
            "izbiro sprehodov in označite želene, nato „Končano“ — telefon vpraša, kam shraniti datoteko.",
    HelpKey.DataImport to
        "V „Uvozu“ pritisnite „Izberi datoteko“, po želji vpišite pripis, ki se doda k imenom naloženih " +
            "sprehodov, in pritisnite „Končano“; ko je arhiv prebran, se pojavi gumb „V arhiv“. Gumb " +
            "„Prekliči“ počisti vneseno, ne da bi kaj shranil.",
    HelpKey.DataArchiveContents to
        "V arhiv gredo sled, najdbe, označeni kraji, fotografije in tiste vrste gob, ki jih v katalogu ni " +
            "— na drugi napravi se pojavijo v „Dodanih gobah“ z napisom „iz arhiva“.",
    HelpKey.DataDuplicates to
        "Uvoz sprehode vedno doda k obstoječim in ničesar ne zamenja, zato bo ponovno nalaganje iste " +
            "datoteke ustvarilo še eno kopijo: pripis k imenom pomaga, da jih pozneje ločite. Na koncu se " +
            "pokaže, koliko sprehodov je bilo naloženih in koliko jih ni bilo mogoče prebrati.",
    HelpKey.SettingsPurpose to
        "Splošne nastavitve aplikacije: jezik vmesnika, videz, oblika in vrstni red ploščic gob na " +
            "zaslonu snemanja ter vzdrževanje zemljevida.",
    HelpKey.SettingsLanguage to
        "Vrstica „Jezik vmesnika“ odpre seznam jezikov: pritisk izbere jezik, kljukica zgoraj izbiro " +
            "potrdi, puščica izstopi brez sprememb. Jezik začne veljati takoj v vsej aplikaciji, ponovni " +
            "zagon ni potreben.",
    HelpKey.SettingsTheme to
        "„Videz“ preklaplja svetlo in temno temo aplikacije. „Sistemsko“ prepusti izbiro telefonu: " +
            "aplikacija potemni in posvetli skupaj z njim.",
    HelpKey.SettingsMushroomSize to
        "Drsnik določa velikost ikon gob na zemljevidu — tako na zaslonu snemanja kot na skupnem " +
            "„Zemljevidu najdb“. Slika pod drsnikom se spreminja že med vlečenjem, tako da velikost " +
            "vidite, preden spustite.",
    HelpKey.SettingsMushroomOrder to
        "Običajno se pravkar označene gobe premaknejo na začetek traku ploščic. „Nespremenljiv vrstni red " +
            "gob“ to povsem izklopi, „Ponastavi vrstni red gob ob koncu sprehoda“ pa vrne prvotni vrstni " +
            "red, ko se sprehod konča.",
    HelpKey.SettingsMapData to
        "„Posodobi podatke zemljevida“ preveri, ali se je zemljevid na strežniku spremenil, in če se je, " +
            "znova prenese vsa shranjena offline območja. „Počisti predpomnilnik zemljevida“ odstrani " +
            "samo to, kar se je naložilo med brskanjem — območja iz „Predhodnega prenosa“ ostanejo.",
)
