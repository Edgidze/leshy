package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Hrvatski — zasloni pomoći, `.claude/plans/help-screens.md`. */
internal val croatianHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "Glavni zaslon aplikacije: ovdje se snima šetnja. Preko GPS-a se bilježi vaš trag, a svaki nalaz " +
            "se sprema s koordinatama i vremenom — i odmah odlazi u memoriju, pa se šetnja može prekinuti " +
            "u bilo kojem trenutku, a zabilježeno neće nestati.",
    HelpKey.RecordStartFinish to
        "„Kreni“ pita za naziv i započinje snimanje; zatim se gumb pretvara u „Pauza“, a na pauzi se " +
            "pojavljuju „Nastavi“ i „Završi“. „Završi“ zatvara šetnju i premješta je u „Arhivu šetnji“.",
    HelpKey.RecordTiles to
        "Pločice gljiva pri dnu služe za bilježenje nalaza: „+“ bilježi nalaz na vašoj trenutnoj točki, " +
            "„−“ uklanja posljednju pogrešnu oznaku te vrste. Dugi pritisak na „+“ otvara unos više " +
            "komada odjednom; više od 999 istih gljiva u jednoj šetnji nije moguće označiti.",
    HelpKey.RecordPlace to
        "Okrugli gumb lijevo označava mjesto — s nazivom, opisom i fotografijom. Mjesto se postavlja " +
            "ondje gdje trenutno stojite i ostaje na karti i nakon šetnje.",
    HelpKey.RecordNavigation to
        "Dugi pritisak na oznaku mjesta uključuje navigaciju do njega: ploča gore desno pokazuje smjer i " +
            "udaljenost do cilja. Križić na ploči isključuje navigaciju.",
    HelpKey.RecordSearchAndOwn to
        "Povećalo desno pronalazi gljivu po nazivu i premješta njezinu pločicu na početak trake — tako je " +
            "brže kada je uključeno mnogo vrsta. Posljednja pločica trake, s plusom, dodaje vašu vrstu " +
            "koje nema u katalogu.",
    HelpKey.RecordFilters to
        "Gumb „Filtri“ gore lijevo određuje nalaze kojih vrsta i iz kojeg razdoblja prikazivati na karti, " +
            "a broj na njemu govori koliko je filtara trenutno uključeno. Filtar je zajednički s „Kartom " +
            "nalaza“: ono što uključite ovdje vrijedi i tamo.",
    HelpKey.RecordBackground to
        "Snimanje traga nastavlja se i kad je aplikacija u pozadini. Na Androidu šetnja u tijeku " +
            "stoji i kao obavijest s tipkama „+“/„−“ — nalaz se može zabilježiti bez otključavanja " +
            "telefona. Osim trenutne šetnje, karta prikazuje nalaze i označena mjesta prijašnjih " +
            "šetnji — po njima se vidi kuda ste već hodali i što je ondje bilo.",
    HelpKey.ArchivePurpose to
        "Sve vaše šetnje, najnovije na vrhu. Na kartici su naziv, datum, trajanje, kilometraža, broj " +
            "nalaza i minijatura prijeđenog traga.",
    HelpKey.ArchiveDetail to
        "Pritisak na karticu otvara cijelu šetnju: statistika, nalazi po vrstama, označena mjesta, opis i " +
            "gumb „Pogledaj kartu“. Naziv i opis mogu se promijeniti na istom mjestu.",
    HelpKey.ArchiveShare to
        "Gumb „Podijeli“ sastavlja sliku od onih dijelova šetnje koje označite kvačicama. Prije nego " +
            "pošaljete kartu šetnje, imajte na umu: po njoj se vidi točno gdje ste našli gljive.",
    HelpKey.ArchiveSelection to
        "Dugi pritisak na karticu uključuje način odabira: pritiskom označite željene šetnje i pritisnite " +
            "„Izbriši šetnje“, a gumb „Natrag“ izlazi iz tog načina. Brisanje je nepovratno — zajedno sa " +
            "šetnjom nestaju njezin trag, nalazi, označena mjesta i fotografije.",
    HelpKey.ArchiveUnfinished to
        "Nezavršena šetnja također se vidi na popisu: umjesto vremena završetka kod nje piše „nije " +
            "završena“. Takva šetnja još nema trajanje, pa ne ulazi u ukupno vrijeme na „Karti nalaza“.",
    HelpKey.MapPurpose to
        "Objedinjena karta: nalazi, tragovi i označena mjesta svih vaših šetnji odjednom na jednom " +
            "platnu. Služi da vidite širu sliku — gdje su vam gljivarska mjesta i kako se mijenjaju iz " +
            "godine u godinu.",
    HelpKey.MapFullScreen to
        "Gore je karta sa svim nalazima odjednom; pritisak na nju otvara kartu preko cijelog zaslona. Kad " +
            "je nalaza mnogo, bliske se oznake skupljaju u kružić s brojem — približite kartu i raspast " +
            "će se na pojedinačne gljive. Veličina ikona gljiva namješta se u „Postavkama“.",
    HelpKey.MapSliders to
        "Ispod karte su dva klizača — raspon datuma i sezona, to jest raspon mjeseci — a sve ispod njih " +
            "računa se prema odabranom. Klizači se pojavljuju tek kad imate šetnje iz više od jednog " +
            "dana.",
    HelpKey.MapStats to
        "Ispod klizača: koliko je bilo šetnji, kilometara, vremena i nalaza, pločice po vrstama i kružni " +
            "dijagram. Ukupno vrijeme zbraja završene šetnje: nezavršena još nema trajanje.",
    HelpKey.MapFilters to
        "Gumb „Filtri“ živi na karti preko cijelog zaslona, gore lijevo: ondje su iste dvije osi, popis " +
            "vrsta i preklopnik za prikaz prijašnjih tragova. Broj na gumbu govori koliko je filtara " +
            "uključeno; filtar je zajednički sa zaslonom snimanja.",
    HelpKey.MapPlaces to
        "Pritisak na oznaku mjesta otvara njegovu karticu s fotografijom i opisom. Odatle se mjesto može " +
            "i promijeniti ili izbrisati.",
    HelpKey.SpeciesPurpose to
        "Ovdje odlučujete koje će gljive biti pločice na zaslonu snimanja. Katalog je podijeljen na " +
            "zbirke po državama, a uz njega žive vrste kojih u katalogu nema — njih dodajete sami.",
    HelpKey.SpeciesCollections to
        "U „Zbirkama gljiva“ pritisak na redak države otvara njezine vrste: kvačica uz državu " +
            "uključuje cijelu zbirku, kvačice unutra pojedine vrste. Polje pretraživanja na vrhu po " +
            "nazivu pronalazi i državu i pojedinu gljivu.",
    HelpKey.SpeciesOwn to
        "U „Dodanim gljivama“ gumb „Dodaj gljivu“ otvara obrazac: naziv, znanstveni naziv, boja " +
            "oznake i slika — s kamere, iz galerije ili iz kataloga. Zatim aplikacija pita „U koju " +
            "zbirku?“: vlastiti naziv skuplja takve gljive zajedno, prazno polje ih stavlja u " +
            "„Ostale“. Olovka mijenja već dodanu vrstu, križić je briše.",
    HelpKey.SpeciesCheckboxes to
        "Skinuta kvačica ništa ne briše — vrsta se jednostavno prestaje prikazivati kao pločica, a " +
            "prijašnji nalazi ostaju na svome mjestu. Brisanje vlastite vrste, naprotiv, nepovratno je: " +
            "sve njezine oznake u prijašnjim šetnjama prelaze u „Nepoznatu gljivu“. Vrste s natpisom „iz " +
            "arhive“ stigle su zajedno s uvezenim šetnjama.",
    HelpKey.SpeciesImages to
        "Sve slike gljiva u aplikaciji su uvjetne: pomažu prepoznati pločicu, a ne gljivu u šumi. Ne " +
            "određujte po njima nepoznate gljive.",
    HelpKey.PreparationPurpose to
        "Unaprijed preuzima dijelove karte u memoriju telefona kako bi karta ostala na mjestu u šumi bez " +
            "interneta: bez toga će daleko od signala umjesto karte biti prazna podloga.",
    HelpKey.PreparationDownload to
        "Pronađite željeno područje — pomičite i približavajte kartu — pa pritisnite okrugli gumb sa " +
            "strelicom prema dolje dolje desno. Aplikacija pokazuje koliko će prostora zauzeti ono što je " +
            "trenutno na zaslonu: „Preuzmi ovo područje“ pita za naziv i započinje preuzimanje, " +
            "„Odustani“ vraća kartu.",
    HelpKey.PreparationRegions to
        "Preuzeta područja stoje u traci pri dnu. Pritisak na pločicu prelijeće do tog područja na karti, " +
            "a gumbi na pločici pauziraju preuzimanje i nastavljaju ga, ponavljaju pokušaj nakon pogreške " +
            "i brišu područje.",
    HelpKey.PreparationAreaSize to
        "Preuzima se točno ono što se vidi na zaslonu, zato se procjena veličine mijenja dok pomičete " +
            "kartu. Što je područje veće, to manje detaljno mora biti — isplativije je preuzeti nekoliko " +
            "manjih dijelova nego jedan golem. Nazivi područja ne smiju se ponavljati.",
    HelpKey.PreparationBackground to
        "Preuzimanje teče u pozadini i ne prekida se ako napustite zaslon, a na pauzi se napredak čuva. " +
            "„Ažuriraj podatke karte“ u „Postavkama“ ponovno preuzima sva spremljena područja.",
    HelpKey.DataPurpose to
        "Prijenos šetnji između telefona i sigurnosna kopija: odabrane šetnje izvoze se u jednu arhivsku " +
            "datoteku, a takva se datoteka može učitati natrag — na ovom ili na drugom uređaju.",
    HelpKey.DataExport to
        "Preklopnik na vrhu bira „Izvoz“ ili „Uvoz“. U „Izvozu“ zadajte naziv arhive, pritisnite redak za " +
            "odabir šetnji i označite željene, pa „Gotovo“ — telefon će pitati kamo spremiti datoteku.",
    HelpKey.DataImport to
        "U „Uvozu“ pritisnite „Odaberi datoteku“, po želji upišite dodatak koji će se pridodati nazivima " +
            "učitanih šetnji i pritisnite „Gotovo“; kad se arhiva pročita, pojavit će se gumb „U arhivu“. " +
            "Gumb „Odustani“ briše uneseno, bez spremanja.",
    HelpKey.DataArchiveContents to
        "U arhivu ulaze trag, nalazi, označena mjesta, fotografije i one vrste gljiva kojih nema u " +
            "katalogu — na drugom uređaju pojavljuju se u „Dodanim gljivama“ s natpisom „iz arhive“.",
    HelpKey.DataDuplicates to
        "Uvoz uvijek dodaje šetnje uz postojeće i ništa ne zamjenjuje, pa će ponovno učitavanje iste " +
            "datoteke stvoriti još jedan primjerak: dodatak nazivima pomaže da ih kasnije razlikujete. Na " +
            "kraju se prikazuje koliko je šetnji učitano i koliko ih nije bilo moguće pročitati.",
    HelpKey.SettingsPurpose to
        "Opće postavke aplikacije: jezik sučelja, izgled, oblik i redoslijed pločica gljiva na zaslonu " +
            "snimanja te održavanje karte.",
    HelpKey.SettingsLanguage to
        "Redak „Jezik sučelja“ otvara popis jezika: pritisak bira jezik, kvačica na vrhu potvrđuje " +
            "odabir, strelica izlazi bez promjene. Jezik se primjenjuje odmah u cijeloj aplikaciji, " +
            "ponovno pokretanje nije potrebno.",
    HelpKey.SettingsTheme to
        "„Izgled“ prebacuje svijetlu i tamnu temu aplikacije. „Sustav“ prepušta izbor telefonu: " +
            "aplikacija tamni i svijetli zajedno s njim.",
    HelpKey.SettingsMushroomSize to
        "Klizač određuje veličinu ikona gljiva na karti — i na zaslonu snimanja i na objedinjenoj „Karti " +
            "nalaza“. Slika ispod klizača mijenja se već dok povlačite, pa se veličina vidi prije nego " +
            "pustite.",
    HelpKey.SettingsMushroomOrder to
        "Obično se upravo označene gljive pomiču na početak trake pločica. „Nepromjenjiv redoslijed " +
            "gljiva“ to potpuno isključuje, a „Poništi redoslijed gljiva na kraju šetnje“ vraća prvotni " +
            "redoslijed kad šetnja završi.",
    HelpKey.SettingsMapData to
        "„Ažuriraj podatke karte“ provjerava je li se karta promijenila na poslužitelju i, ako jest, " +
            "ponovno preuzima sva spremljena izvanmrežna područja. „Očisti predmemoriju karte“ uklanja " +
            "samo ono što se učitalo tijekom razgledavanja — područja iz „Prethodnog preuzimanja“ ostaju.",
)
