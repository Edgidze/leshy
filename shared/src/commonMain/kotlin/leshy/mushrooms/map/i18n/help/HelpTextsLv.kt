package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Latviešu — palīdzības ekrāni, `.claude/plans/help-screens.md`. */
internal val latvianHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "Lietotnes galvenais ekrāns: te tiek ierakstīta pastaiga. Pēc GPS tiek rakstīts jūsu maršruts, un " +
            "katrs atradums tiek saglabāts ar koordinātām un laiku — un nonāk atmiņā uzreiz, tāpēc " +
            "pastaigu var pārtraukt jebkurā brīdī, un ierakstītais nepazudīs.",
    HelpKey.RecordStartFinish to
        "„Sākt“ pajautā nosaukumu un sāk ierakstīšanu; tālāk poga pārvēršas par „Pauze“, bet pauzē " +
            "parādās „Turpināt“ un „Pabeigt“. „Pabeigt“ noslēdz pastaigu un pārvieto to uz „Pastaigu " +
            "arhīvu“.",
    HelpKey.RecordTiles to
        "Sēņu elementi apakšā ir tas, ar ko atzīmē atradumus: „+“ atzīmē atradumu jūsu pašreizējā punktā, " +
            "„−“ noņem pēdējo kļūdaino šīs sugas atzīmi. Ilgs „+“ nospiediens atver vairāku gabalu ievadi " +
            "uzreiz; vairāk nekā 999 vienas sugas sēnes vienā pastaigā atzīmēt nevar.",
    HelpKey.RecordPlace to
        "Apaļā poga pa kreisi atzīmē vietu — ar nosaukumu, aprakstu un fotogrāfiju. Vieta tiek novietota " +
            "tur, kur pašlaik stāvat, un paliek kartē arī pēc pastaigas.",
    HelpKey.RecordNavigation to
        "Ilgs nospiediens uz vietas atzīmes ieslēdz navigāciju līdz tai: panelis augšā pa labi rāda " +
            "virzienu un attālumu līdz mērķim. Krustiņš panelī navigāciju izslēdz.",
    HelpKey.RecordSearchAndOwn to
        "Lupa pa labi atrod sēni pēc nosaukuma un pārvieto tās elementu joslas sākumā — tā ir ātrāk, kad " +
            "ieslēgtas daudzas sugas. Pēdējais joslas elements ar plusu pievieno savu sugu, kuras " +
            "katalogā nav.",
    HelpKey.RecordFilters to
        "Poga „Filtri“ augšā pa kreisi nosaka, kuru sugu un kura laika atradumus rādīt kartē, bet " +
            "skaitlis uz tās — cik filtru pašlaik ieslēgts. Filtrs ir kopīgs ar „Atradumu karti“: " +
            "ieslēgtais te darbojas arī tur.",
    HelpKey.RecordBackground to
        "Maršruta ierakstīšana turpinās arī tad, kad lietotne ir fonā. Papildus pašreizējai pastaigai " +
            "karte rāda iepriekšējo pastaigu atradumus un atzīmētās vietas — pēc tām redzams, kur jau " +
            "esat gājis un kas tur bija.",
    HelpKey.ArchivePurpose to
        "Visas jūsu pastaigas, jaunākās augšā. Kartītē ir nosaukums, datums, ilgums, kilometri, atradumu " +
            "skaits un noietā maršruta sīktēls.",
    HelpKey.ArchiveDetail to
        "Pieskāriens kartītei atver visu pastaigu: statistika, atradumi pa sugām, atzīmētās vietas, " +
            "apraksts un poga „Skatīt karti“. Nosaukumu un aprakstu var mainīt turpat.",
    HelpKey.ArchiveShare to
        "Poga „Kopīgot“ saliek attēlu no tām pastaigas daļām, kuras atzīmēsiet ar ķeksīšiem. Pirms sūtāt " +
            "pastaigas karti, atcerieties: pēc tās redzams, kur tieši atradāt sēnes.",
    HelpKey.ArchiveSelection to
        "Ilgs pieskāriens kartītei ieslēdz atlases režīmu: atzīmējiet vajadzīgās pastaigas ar pieskārienu " +
            "un spiediet „Dzēst pastaigas“, bet poga „Atpakaļ“ no šī režīma iziet. Dzēšana ir " +
            "neatgriezeniska — kopā ar pastaigu pazūd tās maršruts, atradumi, atzīmētās vietas un " +
            "fotogrāfijas.",
    HelpKey.ArchiveUnfinished to
        "Nepabeigta pastaiga arī ir redzama sarakstā: finiša laika vietā tai rakstīts „nav pabeigta“. " +
            "Tādai pastaigai vēl nav ilguma, tāpēc kopējā laikā „Atradumu kartē“ tā neieskaitās.",
    HelpKey.MapPurpose to
        "Kopsavilkuma karte: visu jūsu pastaigu atradumi, maršruti un atzīmētās vietas uzreiz vienā " +
            "audeklā. Vajadzīga, lai redzētu kopainu — kur ir jūsu sēņu vietas un kā tās mainās gadu no " +
            "gada.",
    HelpKey.MapFullScreen to
        "Augšā ir karte ar visiem atradumiem uzreiz; pieskāriens tai atver karti visā ekrānā. Kad " +
            "atradumu ir daudz, tuvās atzīmes sakopojas aplītī ar skaitli — pietuviniet karti, un tas " +
            "izjuks atsevišķās sēnēs. Sēņu ikonu izmēru iestata „Iestatījumos“.",
    HelpKey.MapSliders to
        "Zem kartes ir divi slīdņi — datumu diapazons un sezona, tas ir, mēnešu diapazons — un viss, kas " +
            "zem tiem, tiek rēķināts pēc izvēlētā. Slīdņi parādās tikai tad, kad ir pastaigas no vairāk " +
            "nekā vienas dienas.",
    HelpKey.MapStats to
        "Zem slīdņiem: cik bija pastaigu, kilometru, laika un atradumu, elementi pa sugām un sektoru " +
            "diagramma. Kopējais laiks summējas no pabeigtajām pastaigām: nepabeigtajai ilguma vēl nav.",
    HelpKey.MapFilters to
        "Poga „Filtri“ dzīvo pilnekrāna kartē, tās augšā pa kreisi: tur ir tās pašas divas asis, sugu " +
            "saraksts un iepriekšējo maršrutu rādīšanas slēdzis. Skaitlis uz pogas — cik filtru ieslēgts; " +
            "filtrs ir kopīgs ar ierakstīšanas ekrānu.",
    HelpKey.MapPlaces to
        "Pieskāriens vietas atzīmei atver tās kartīti ar fotogrāfiju un aprakstu. No turienes vietu var " +
            "arī mainīt vai dzēst.",
    HelpKey.SpeciesPurpose to
        "Te jūs izlemjat, kuras sēnes būs elementi ierakstīšanas ekrānā. Katalogs ir sadalīts kolekcijās " +
            "pa valstīm, un tam blakus dzīvo sugas, kuru katalogā nav — tās pievienojat pats.",
    HelpKey.SpeciesCollections to
        "Sadaļā „Sēņu kolekcijas“ pieskāriens valsts rindai atver tās sugas: ķeksītis pie valsts ieslēdz " +
            "visu kolekciju, ķeksīši iekšpusē — atsevišķas sugas. Meklēšanas lauks augšā atrod valsti pēc " +
            "nosaukuma.",
    HelpKey.SpeciesOwn to
        "Sadaļā „Pievienotās sēnes“ poga „Pievienot sēni“ atver formu: nosaukums, zinātniskais nosaukums, " +
            "atzīmes krāsa un attēls — no kameras, no galerijas vai no kataloga. Zīmulis maina jau " +
            "pievienoto sugu, krustiņš to dzēš.",
    HelpKey.SpeciesCheckboxes to
        "Noņemts ķeksītis neko nedzēš — suga vienkārši netiek rādīta kā elements, bet iepriekšējie " +
            "atradumi paliek savās vietās. Savas sugas dzēšana turpretī ir neatgriezeniska: visas tās " +
            "atzīmes iepriekšējās pastaigās pāries uz „Nezināmu sēni“. Sugas ar uzrakstu „no arhīva“ " +
            "atnākušas kopā ar importētajām pastaigām.",
    HelpKey.SpeciesImages to
        "Visi sēņu attēli lietotnē ir nosacīti: tie palīdz atpazīt elementu, nevis sēni mežā. Nenosakiet " +
            "pēc tiem nepazīstamas sēnes.",
    HelpKey.PreparationPurpose to
        "Iepriekš lejupielādē kartes gabalus telefona atmiņā, lai mežā bez interneta karte paliktu savā " +
            "vietā: bez tā tālu no sakariem kartes vietā būs tukšs fons.",
    HelpKey.PreparationDownload to
        "Atrodiet vajadzīgo apgabalu — pārvietojiet un tuviniet karti — pēc tam nospiediet apaļo pogu ar " +
            "bultiņu uz leju apakšā pa labi. Lietotne parādīs, cik vietas aizņems tas, kas pašlaik ir " +
            "ekrānā: „Lejupielādēt šo apgabalu“ pajautās nosaukumu un sāks lejupielādi, „Atcelt“ " +
            "atgriezīs karti.",
    HelpKey.PreparationRegions to
        "Lejupielādētie apgabali atrodas joslā apakšā. Pieskāriens plāksnītei aizlido līdz šim apgabalam " +
            "kartē, bet pogas uz plāksnītes aptur lejupielādi un atsāk to, atkārto mēģinājumu pēc kļūdas " +
            "un dzēš apgabalu.",
    HelpKey.PreparationAreaSize to
        "Tiek lejupielādēts tieši tas, kas redzams ekrānā, tāpēc izmēra novērtējums mainās, kamēr " +
            "pārvietojat karti. Jo lielāks apgabals, jo mazāk detalizētu to nākas veidot — izdevīgāk " +
            "lejupielādēt vairākus nelielus apgabalus nekā vienu milzīgu. Apgabalu nosaukumi nedrīkst " +
            "atkārtoties.",
    HelpKey.PreparationBackground to
        "Lejupielāde notiek fonā un netiek pārtraukta, ja aizejat no ekrāna, bet pauzē progress tiek " +
            "saglabāts. „Atjaunināt kartes datus“ sadaļā „Iestatījumi“ no jauna lejupielādē visus " +
            "saglabātos apgabalus.",
    HelpKey.DataPurpose to
        "Pastaigu pārnešana starp telefoniem un rezerves kopija: izvēlētās pastaigas tiek izgūtas vienā " +
            "arhīva failā, un tādu failu var ielādēt atpakaļ — šajā vai citā ierīcē.",
    HelpKey.DataExport to
        "Slēdzis augšā izvēlas „Eksports“ vai „Imports“. Sadaļā „Eksports“ norādiet arhīva nosaukumu, " +
            "pieskarieties pastaigu izvēles rindai un atzīmējiet vajadzīgās, tad „Gatavs“ — telefons " +
            "pajautās, kur saglabāt failu.",
    HelpKey.DataImport to
        "Sadaļā „Imports“ nospiediet „Izvēlēties failu“, ja vēlaties, ierakstiet piebildi, kas tiks " +
            "pievienota ielādējamo pastaigu nosaukumiem, un nospiediet „Gatavs“; kad arhīvs būs nolasīts, " +
            "parādīsies poga „Uz arhīvu“. Poga „Atcelt“ notīra ievadīto, neko nesaglabājot.",
    HelpKey.DataArchiveContents to
        "Arhīvā nonāk maršruts, atradumi, atzīmētās vietas, fotogrāfijas un tās sēņu sugas, kuru katalogā " +
            "nav — citā ierīcē tās parādīsies sadaļā „Pievienotās sēnes“ ar uzrakstu „no arhīva“.",
    HelpKey.DataDuplicates to
        "Imports vienmēr pievieno pastaigas jau esošajām un neko neaizstāj, tāpēc atkārtota tā paša faila " +
            "ielāde izveidos tās vēlreiz: piebilde pie nosaukumiem palīdz pēc tam atšķirt vienu no otras. " +
            "Beigās tiek parādīts, cik pastaigu ielādējās un cik neizdevās nolasīt.",
    HelpKey.SettingsPurpose to
        "Lietotnes vispārīgie iestatījumi: saskarnes valoda, izskats, sēņu elementu izskats un secība " +
            "ierakstīšanas ekrānā un kartes uzturēšana.",
    HelpKey.SettingsLanguage to
        "Rinda „Saskarnes valoda“ atver valodu sarakstu: pieskāriens izvēlas valodu, ķeksītis augšā " +
            "apstiprina izvēli, bultiņa iziet, neko nemainot. Valoda tiek piemērota uzreiz visā lietotnē, " +
            "restarts nav vajadzīgs.",
    HelpKey.SettingsTheme to
        "„Izskats“ pārslēdz lietotnes gaišo un tumšo tēmu. „Sistēmas“ atdod izvēli telefonam: lietotne " +
            "tumšojas un gaišojas kopā ar to.",
    HelpKey.SettingsMushroomSize to
        "Slīdnis nosaka sēņu ikonu izmēru kartē — gan ierakstīšanas ekrānā, gan kopsavilkuma „Atradumu " +
            "kartē“. Attēls zem slīdņa mainās jau vilkšanas laikā, tāpēc izmēru redzat, pirms atlaižat.",
    HelpKey.SettingsMushroomOrder to
        "Parasti tikko atzīmētās sēnes paceļas elementu joslas sākumā. „Nemainīga sēņu secība“ to izslēdz " +
            "pavisam, bet „Atiestatīt sēņu secību pastaigas beigās“ atgriež sākotnējo secību, kad " +
            "pastaiga pabeigta.",
    HelpKey.SettingsMapData to
        "„Atjaunināt kartes datus“ pārbauda, vai karte serverī ir mainījusies, un, ja ir, no jauna " +
            "lejupielādē visus saglabātos bezsaistes apgabalus. „Notīrīt kartes kešatmiņu“ dzēš tikai to, " +
            "kas ielādējās pārlūkojot — apgabali no „Iepriekšējas lejupielādes“ paliek savās vietās.",
)
