package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Bosanski — ekrani pomoći, `.claude/plans/help-screens.md`. */
internal val bosnianHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "Glavni ekran aplikacije: ovdje se snima šetnja. Preko GPS-a se bilježi vaš trag, a svaki nalaz " +
            "se čuva s koordinatama i vremenom — i odmah ide u memoriju, pa se šetnja može prekinuti u " +
            "bilo kojem trenutku, a zabilježeno neće nestati.",
    HelpKey.RecordStartFinish to
        "„Počni“ pita za naziv i započinje snimanje; zatim se dugme pretvara u „Pauza“, a na pauzi se " +
            "pojavljuju „Nastavi“ i „Završi“. „Završi“ zatvara šetnju i premješta je u „Arhivu šetnji“.",
    HelpKey.RecordTiles to
        "Pločice gljiva pri dnu služe za bilježenje nalaza: „+“ bilježi nalaz na vašoj trenutnoj tački, " +
            "„−“ uklanja posljednju pogrešnu oznaku te vrste. Dugi pritisak na „+“ otvara unos više " +
            "komada odjednom; više od 999 istih gljiva u jednoj šetnji nije moguće označiti.",
    HelpKey.RecordPlace to
        "Okruglo dugme lijevo označava mjesto — s nazivom, opisom i fotografijom. Mjesto se postavlja " +
            "tamo gdje trenutno stojite i ostaje na karti i nakon šetnje.",
    HelpKey.RecordNavigation to
        "Dugi pritisak na oznaku mjesta uključuje navigaciju do njega: panel gore desno pokazuje smjer i " +
            "udaljenost do cilja. Krstić na panelu isključuje navigaciju.",
    HelpKey.RecordSearchAndOwn to
        "Lupa desno pronalazi gljivu po nazivu i premješta njenu pločicu na početak trake — tako je brže " +
            "kada je uključeno mnogo vrsta. Posljednja pločica trake, s plusom, dodaje vašu vrstu koje " +
            "nema u katalogu.",
    HelpKey.RecordFilters to
        "Dugme „Filteri“ gore lijevo određuje nalaze kojih vrsta i iz kojeg perioda prikazivati na karti, " +
            "a broj na njemu govori koliko je filtera trenutno uključeno. Filter je zajednički s „Kartom " +
            "nalaza“: ono što uključite ovdje važi i tamo.",
    HelpKey.RecordBackground to
        "Snimanje traga nastavlja se i kad je aplikacija u pozadini. Osim trenutne šetnje, karta " +
            "prikazuje nalaze i označena mjesta ranijih šetnji — po njima se vidi kuda ste već išli i šta " +
            "je tamo bilo.",
    HelpKey.ArchivePurpose to
        "Sve vaše šetnje, najnovije na vrhu. Na kartici su naziv, datum, trajanje, kilometraža, broj " +
            "nalaza i minijatura pređenog traga.",
    HelpKey.ArchiveDetail to
        "Pritisak na karticu otvara cijelu šetnju: statistika, nalazi po vrstama, označena mjesta, opis i " +
            "dugme „Pogledaj kartu“. Naziv i opis mogu se promijeniti na istom mjestu.",
    HelpKey.ArchiveShare to
        "Dugme „Podijeli“ sastavlja sliku od onih dijelova šetnje koje označite kvačicama. Prije nego " +
            "pošaljete kartu šetnje, imajte na umu: po njoj se vidi tačno gdje ste našli gljive.",
    HelpKey.ArchiveSelection to
        "Dugi pritisak na karticu uključuje način odabira: pritiskom označite željene šetnje i pritisnite " +
            "„Obriši šetnje“, a dugme „Nazad“ izlazi iz tog načina. Brisanje je nepovratno — zajedno sa " +
            "šetnjom nestaju njen trag, nalazi, označena mjesta i fotografije.",
    HelpKey.ArchiveUnfinished to
        "Nezavršena šetnja također se vidi na spisku: umjesto vremena završetka kod nje piše „u toku“. " +
            "Takva šetnja još nema trajanje, pa ne ulazi u ukupno vrijeme na „Karti nalaza“.",
    HelpKey.MapPurpose to
        "Objedinjena karta: nalazi, tragovi i označena mjesta svih vaših šetnji odjednom na jednom " +
            "platnu. Služi da vidite širu sliku — gdje su vam gljivarska mjesta i kako se mijenjaju iz " +
            "godine u godinu.",
    HelpKey.MapFullScreen to
        "Gore je karta sa svim nalazima odjednom; pritisak na nju otvara kartu preko cijelog ekrana. Kad " +
            "je nalaza mnogo, bliske oznake se skupljaju u kružić s brojem — približite kartu i raspast " +
            "će se na pojedinačne gljive. Veličina ikona gljiva podešava se u „Postavkama“.",
    HelpKey.MapSliders to
        "Ispod karte su dva klizača — raspon datuma i sezona, to jest raspon mjeseci — a sve ispod njih " +
            "računa se prema odabranom. Klizači se pojavljuju tek kad imate šetnje iz više od jednog " +
            "dana.",
    HelpKey.MapStats to
        "Ispod klizača: koliko je bilo šetnji, kilometara, vremena i nalaza, pločice po vrstama i kružni " +
            "dijagram. Ukupno vrijeme sabira završene šetnje: nezavršena još nema trajanje.",
    HelpKey.MapFilters to
        "Dugme „Filteri“ živi na karti preko cijelog ekrana, gore lijevo: tamo su iste dvije ose, spisak " +
            "vrsta i prekidač za prikaz ranijih tragova. Broj na dugmetu govori koliko je filtera " +
            "uključeno; filter je zajednički s ekranom snimanja.",
    HelpKey.MapPlaces to
        "Pritisak na oznaku mjesta otvara njegovu karticu s fotografijom i opisom. Odatle se mjesto može " +
            "i promijeniti ili obrisati.",
    HelpKey.SpeciesPurpose to
        "Ovdje odlučujete koje će gljive biti pločice na ekranu snimanja. Katalog je podijeljen na zbirke " +
            "po državama, a uz njega žive vrste kojih u katalogu nema — njih dodajete sami.",
    HelpKey.SpeciesCollections to
        "U „Zbirkama gljiva“ pritisak na red države otvara njene vrste: kvačica uz državu uključuje " +
            "cijelu zbirku, kvačice unutra pojedine vrste. Polje pretrage na vrhu pronalazi državu po " +
            "nazivu.",
    HelpKey.SpeciesOwn to
        "U „Dodanim gljivama“ dugme „Dodaj gljivu“ otvara obrazac: naziv, naučni naziv, boja oznake i " +
            "slika — s kamere, iz galerije ili iz kataloga. Olovka mijenja već dodanu vrstu, krstić je " +
            "briše.",
    HelpKey.SpeciesCheckboxes to
        "Skinuta kvačica ništa ne briše — vrsta se jednostavno prestaje prikazivati kao pločica, a raniji " +
            "nalazi ostaju na svom mjestu. Brisanje vlastite vrste, naprotiv, nepovratno je: sve njene " +
            "oznake u ranijim šetnjama prelaze u „Nepoznatu gljivu“. Vrste s natpisom „iz arhive“ stigle " +
            "su zajedno s uvezenim šetnjama.",
    HelpKey.SpeciesImages to
        "Sve slike gljiva u aplikaciji su uvjetne: pomažu prepoznati pločicu, a ne gljivu u šumi. Ne " +
            "određujte po njima nepoznate gljive.",
    HelpKey.PreparationPurpose to
        "Unaprijed preuzima dijelove karte u memoriju telefona kako bi karta ostala na mjestu u šumi bez " +
            "interneta: bez toga će daleko od signala umjesto karte biti prazna podloga.",
    HelpKey.PreparationDownload to
        "Pronađite željeno područje — pomjerajte i približavajte kartu — pa pritisnite okruglo dugme sa " +
            "strelicom nadolje dolje desno. Aplikacija pokazuje koliko će prostora zauzeti ono što je " +
            "trenutno na ekranu: „Preuzmi ovo područje“ pita za naziv i započinje preuzimanje, „Otkaži“ " +
            "vraća kartu.",
    HelpKey.PreparationRegions to
        "Preuzeta područja stoje u traci pri dnu. Pritisak na pločicu prelijeće do tog područja na karti, " +
            "a dugmad na pločici pauziraju preuzimanje i nastavljaju ga, ponavljaju pokušaj nakon greške " +
            "i brišu područje.",
    HelpKey.PreparationAreaSize to
        "Preuzima se tačno ono što se vidi na ekranu, zato se procjena veličine mijenja dok pomjerate " +
            "kartu. Što je područje veće, to manje detaljno mora biti — isplativije je preuzeti nekoliko " +
            "manjih dijelova nego jedan ogroman. Nazivi područja ne smiju se ponavljati.",
    HelpKey.PreparationBackground to
        "Preuzimanje teče u pozadini i ne prekida se ako napustite ekran, a na pauzi se napredak čuva. " +
            "„Ažuriraj podatke karte“ u „Postavkama“ ponovo preuzima sva sačuvana područja.",
    HelpKey.DataPurpose to
        "Prenos šetnji između telefona i sigurnosna kopija: odabrane šetnje izvoze se u jednu arhivsku " +
            "datoteku, a takva se datoteka može učitati nazad — na ovom ili na drugom uređaju.",
    HelpKey.DataExport to
        "Prekidač na vrhu bira „Izvoz“ ili „Uvoz“. U „Izvozu“ zadajte naziv arhive, pritisnite red za " +
            "odabir šetnji i označite željene, pa „Gotovo“ — telefon će pitati gdje da sačuva datoteku.",
    HelpKey.DataImport to
        "U „Uvozu“ pritisnite „Odaberi datoteku“, po želji upišite dodatak koji će se pridodati nazivima " +
            "učitanih šetnji i pritisnite „Gotovo“; kad se arhiva pročita, pojavit će se dugme „U " +
            "arhivu“. Dugme „Otkaži“ briše uneseno, bez čuvanja.",
    HelpKey.DataArchiveContents to
        "U arhivu ulaze trag, nalazi, označena mjesta, fotografije i one vrste gljiva kojih nema u " +
            "katalogu — na drugom uređaju pojavljuju se u „Dodanim gljivama“ s natpisom „iz arhive“.",
    HelpKey.DataDuplicates to
        "Uvoz uvijek dodaje šetnje uz postojeće i ništa ne zamjenjuje, pa će ponovno učitavanje iste " +
            "datoteke napraviti još jedan primjerak: dodatak nazivima pomaže da ih kasnije razlikujete. " +
            "Na kraju se prikazuje koliko je šetnji učitano i koliko ih nije bilo moguće pročitati.",
    HelpKey.SettingsPurpose to
        "Opće postavke aplikacije: jezik sučelja, izgled, oblik i redoslijed pločica gljiva na ekranu " +
            "snimanja te održavanje karte.",
    HelpKey.SettingsLanguage to
        "Red „Jezik sučelja“ otvara spisak jezika: pritisak bira jezik, kvačica na vrhu potvrđuje odabir, " +
            "strelica izlazi bez promjene. Jezik se primjenjuje odmah u cijeloj aplikaciji, ponovno " +
            "pokretanje nije potrebno.",
    HelpKey.SettingsTheme to
        "„Izgled“ prebacuje svijetlu i tamnu temu aplikacije. „Sistemsko“ prepušta izbor telefonu: " +
            "aplikacija tamni i svijetli zajedno s njim.",
    HelpKey.SettingsMushroomSize to
        "Klizač određuje veličinu ikona gljiva na karti — i na ekranu snimanja i na objedinjenoj „Karti " +
            "nalaza“. Slika ispod klizača mijenja se već dok povlačite, pa se veličina vidi prije nego " +
            "pustite.",
    HelpKey.SettingsMushroomOrder to
        "Obično se upravo označene gljive pomjeraju na početak trake pločica. „Zaključaj redoslijed“ to " +
            "potpuno isključuje, a „Vrati redoslijed na kraju šetnje“ vraća prvobitni redoslijed kad " +
            "šetnja završi.",
    HelpKey.SettingsMapData to
        "„Ažuriraj podatke karte“ provjerava je li se karta promijenila na serveru i, ako jest, ponovo " +
            "preuzima sva sačuvana offline područja. „Očisti keš karte“ uklanja samo ono što se učitalo " +
            "tokom razgledanja — područja iz „Preuzimanja unaprijed“ ostaju.",
)
