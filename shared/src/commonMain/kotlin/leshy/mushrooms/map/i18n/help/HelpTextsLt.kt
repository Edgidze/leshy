package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Lietuvių — pagalbos ekranai, `.claude/plans/help-screens.md`. */
internal val lithuanianHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "Pagrindinis programėlės ekranas: čia įrašomas žygis. Per GPS rašomas jūsų pėdsakas, o kiekvienas " +
            "radinys išsaugomas su koordinatėmis ir laiku — ir patenka į atmintį iš karto, tad žygį " +
            "galima nutraukti bet kurią akimirką, o užrašyta nedings.",
    HelpKey.RecordStartFinish to
        "„Pradėti“ paklausia pavadinimo ir pradeda įrašymą; toliau mygtukas virsta „Pauzė“, o per pauzę " +
            "atsiranda „Tęsti“ ir „Baigti“. „Baigti“ uždaro žygį ir perkelia jį į „Žygių archyvą“.",
    HelpKey.RecordTiles to
        "Grybų kortelės apačioje — tai, kuo žymimi radiniai: „+“ pažymi radinį jūsų dabartiniame taške, " +
            "„−“ pašalina paskutinę klaidingą šios rūšies žymę. Ilgas „+“ paspaudimas atveria kelių " +
            "vienetų įvedimą iš karto; daugiau nei 999 tos pačios rūšies grybų per vieną žygį pažymėti " +
            "negalima.",
    HelpKey.RecordPlace to
        "Apvalus mygtukas kairėje pažymi vietą — su pavadinimu, aprašymu ir nuotrauka. Vieta padedama " +
            "ten, kur dabar stovite, ir lieka žemėlapyje po žygio.",
    HelpKey.RecordNavigation to
        "Ilgas paspaudimas ant vietos žymės įjungia navigaciją iki jos: skydelis viršuje dešinėje rodo " +
            "kryptį ir atstumą iki tikslo. Kryželis skydelyje navigaciją išjungia.",
    HelpKey.RecordSearchAndOwn to
        "Didinamasis stiklas dešinėje suranda grybą pagal pavadinimą ir perkelia jo kortelę į juostos " +
            "pradžią — taip greičiau, kai įjungta daug rūšių. Paskutinė juostos kortelė su pliusu prideda " +
            "savo rūšį, kurios kataloge nėra.",
    HelpKey.RecordFilters to
        "Mygtukas „Filtrai“ viršuje kairėje nustato, kurių rūšių ir kurio laikotarpio radinius rodyti " +
            "žemėlapyje, o skaičius ant jo — kiek filtrų dabar įjungta. Filtras bendras su „Radinių " +
            "žemėlapiu“: kas įjungta čia, veikia ir ten.",
    HelpKey.RecordBackground to
        "Pėdsako įrašymas tęsiasi ir tada, kai programėlė sutraukta. Be dabartinio žygio, žemėlapis rodo " +
            "ankstesnių žygių radinius ir pažymėtas vietas — iš jų matyti, kur jau vaikščiojote ir kas " +
            "ten buvo.",
    HelpKey.ArchivePurpose to
        "Visi jūsų žygiai, naujausi viršuje. Kortelėje — pavadinimas, data, trukmė, kilometrai, radinių " +
            "skaičius ir nueito pėdsako miniatiūra.",
    HelpKey.ArchiveDetail to
        "Paspaudus kortelę atsiveria visas žygis: statistika, radiniai pagal rūšis, pažymėtos vietos, " +
            "aprašymas ir mygtukas „Žiūrėti žemėlapį“. Pavadinimą ir aprašymą galima pakeisti čia pat.",
    HelpKey.ArchiveShare to
        "Mygtukas „Bendrinti“ sudeda paveikslėlį iš tų žygio dalių, kurias pažymėsite varnelėmis. Prieš " +
            "siųsdami žygio žemėlapį prisiminkite: iš jo matyti, kur būtent radote grybus.",
    HelpKey.ArchiveSelection to
        "Ilgas kortelės paspaudimas įjungia pasirinkimo režimą: pažymėkite reikiamus žygius paspaudimu ir " +
            "spauskite „Ištrinti žygius“, o mygtukas „Atgal“ iš šio režimo išeina. Ištrynimas negrįžtamas " +
            "— kartu su žygiu dingsta jo pėdsakas, radiniai, pažymėtos vietos ir nuotraukos.",
    HelpKey.ArchiveUnfinished to
        "Nebaigtas žygis sąraše taip pat matomas: vietoj pabaigos laiko jam parašyta „nebaigtas“. Toks " +
            "žygis dar neturi trukmės, todėl į bendrą laiką „Radinių žemėlapyje“ neįskaitomas.",
    HelpKey.MapPurpose to
        "Suvestinis žemėlapis: visų jūsų žygių radiniai, pėdsakai ir pažymėtos vietos iš karto viename " +
            "lauke. Reikalingas, kad matytumėte bendrą vaizdą — kur jūsų grybų vietos ir kaip jos " +
            "keičiasi metai iš metų.",
    HelpKey.MapFullScreen to
        "Viršuje — žemėlapis su visais radiniais iš karto; paspaudus jį žemėlapis atsiveria per visą " +
            "ekraną. Kai radinių daug, artimos žymės susirenka į apskritimą su skaičiumi — priartinkite " +
            "žemėlapį ir jis subyrės į atskirus grybus. Grybų ženkliukų dydis nustatomas „Nustatymuose“.",
    HelpKey.MapSliders to
        "Po žemėlapiu yra dvi slankiklių juostos — datų rėžis ir sezonas, tai yra mėnesių rėžis, — ir " +
            "viskas, kas žemiau jų, skaičiuojama pagal pasirinkimą. Slankikliai atsiranda tik tada, kai " +
            "turite žygių iš daugiau nei vienos dienos.",
    HelpKey.MapStats to
        "Žemiau slankiklių — kiek buvo žygių, kilometrų, laiko ir radinių, kortelės pagal rūšis ir " +
            "skritulinė diagrama. Bendras laikas sudedamas iš baigtų žygių: nebaigtas trukmės dar neturi.",
    HelpKey.MapFilters to
        "Mygtukas „Filtrai“ gyvena viso ekrano žemėlapyje, viršuje kairėje: ten tos pačios dvi ašys, " +
            "rūšių sąrašas ir ankstesnių pėdsakų rodymo jungiklis. Skaičius ant mygtuko — kiek filtrų " +
            "įjungta; filtras bendras su įrašymo ekranu.",
    HelpKey.MapPlaces to
        "Paspaudus vietos žymę atsiveria jos kortelė su nuotrauka ir aprašymu. Iš ten vietą galima " +
            "pakeisti arba ištrinti.",
    HelpKey.SpeciesPurpose to
        "Čia nusprendžiate, kurie grybai bus kortelės įrašymo ekrane. Katalogas suskirstytas į rinkinius " +
            "pagal šalis, o šalia gyvena rūšys, kurių kataloge nėra — jas pridedate patys.",
    HelpKey.SpeciesCollections to
        "„Grybų rinkiniuose“ paspaudus šalies eilutę išsiskleidžia jos rūšys: varnelė prie šalies įjungia " +
            "visą rinkinį, varnelės viduje — atskiras rūšis. Paieškos laukas viršuje randa šalį pagal " +
            "pavadinimą.",
    HelpKey.SpeciesOwn to
        "Skiltyje „Pridėti grybai“ mygtukas „Pridėti grybą“ atveria formą: pavadinimas, mokslinis " +
            "pavadinimas, žymės spalva ir paveikslėlis — iš kameros, iš galerijos arba iš katalogo. " +
            "Pieštukas keičia jau pridėtą rūšį, kryželis ją ištrina.",
    HelpKey.SpeciesCheckboxes to
        "Nuimta varnelė nieko neištrina — rūšis tiesiog nerodoma kortele, o ankstesni radiniai lieka savo " +
            "vietose. Savo rūšies ištrynimas, priešingai, negrįžtamas: visos jos žymės ankstesniuose " +
            "žygiuose pereis į „Nežinomą grybą“. Rūšys su užrašu „iš archyvo“ atkeliavo kartu su " +
            "importuotais žygiais.",
    HelpKey.SpeciesImages to
        "Visi grybų paveikslėliai programėlėje yra sąlyginiai: jie padeda atpažinti kortelę, o ne grybą " +
            "miške. Nenustatinėkite pagal juos nepažįstamų grybų.",
    HelpKey.PreparationPurpose to
        "Iš anksto atsisiunčia žemėlapio gabalus į telefono atmintį, kad miške be interneto žemėlapis " +
            "liktų vietoje: be to toli nuo ryšio vietoj žemėlapio bus tuščias fonas.",
    HelpKey.PreparationDownload to
        "Raskite reikiamą plotą — stumdykite ir mastelį keiskite žemėlapyje — tada paspauskite apvalų " +
            "mygtuką su rodykle žemyn apačioje dešinėje. Programėlė parodys, kiek vietos užims tai, kas " +
            "dabar ekrane: „Atsisiųsti šią sritį“ paklaus pavadinimo ir pradės atsisiuntimą, „Atšaukti“ " +
            "grąžins žemėlapį.",
    HelpKey.PreparationRegions to
        "Atsisiųstos sritys guli juosta apačioje. Paspaudus plokštelę nuskrendama į tą sritį žemėlapyje, " +
            "o mygtukai plokštelėje sustabdo atsisiuntimą ir jį tęsia, kartoja bandymą po klaidos ir " +
            "ištrina sritį.",
    HelpKey.PreparationAreaSize to
        "Atsiunčiama būtent tai, kas matoma ekrane, todėl dydžio įvertis kinta, kol judinate žemėlapį. " +
            "Kuo didesnė sritis, tuo mažiau detali ji turi būti — verčiau atsisiųsti kelis nedidelius " +
            "plotus nei vieną milžinišką. Sričių pavadinimai neturi kartotis.",
    HelpKey.PreparationBackground to
        "Atsisiuntimas vyksta fone ir nenutrūksta, jei paliksite ekraną, o per pauzę pažanga išsaugoma. " +
            "„Atnaujinti žemėlapio duomenis“ „Nustatymuose“ iš naujo atsiunčia visas išsaugotas sritis.",
    HelpKey.DataPurpose to
        "Žygių perkėlimas tarp telefonų ir atsarginė kopija: pasirinkti žygiai išsaugomi į vieną archyvo " +
            "failą, o tokį failą galima įkelti atgal — šiame ar kitame įrenginyje.",
    HelpKey.DataExport to
        "Jungiklis viršuje pasirenka „Eksportas“ arba „Importas“. Skiltyje „Eksportas“ nurodykite archyvo " +
            "pavadinimą, paspauskite žygių pasirinkimo eilutę ir pažymėkite reikiamus, tada „Atlikta“ — " +
            "telefonas paklaus, kur išsaugoti failą.",
    HelpKey.DataImport to
        "Skiltyje „Importas“ paspauskite „Pasirinkti failą“, jei norite, įrašykite priesagą, kuri bus " +
            "pridėta prie įkeliamų žygių pavadinimų, ir paspauskite „Atlikta“; kai archyvas bus " +
            "perskaitytas, atsiras mygtukas „Į archyvą“. Mygtukas „Atšaukti“ išvalo įvestį nieko " +
            "neišsaugojęs.",
    HelpKey.DataArchiveContents to
        "Į archyvą patenka pėdsakas, radiniai, pažymėtos vietos, nuotraukos ir tos grybų rūšys, kurių " +
            "kataloge nėra — kitame įrenginyje jos atsiras skiltyje „Pridėti grybai“ su užrašu „iš " +
            "archyvo“.",
    HelpKey.DataDuplicates to
        "Importas visada prideda žygius prie jau esamų ir nieko nekeičia, todėl pakartotinai įkėlus tą " +
            "patį failą jie bus sukurti dar kartą: priesaga prie pavadinimų padeda paskui juos atskirti. " +
            "Pabaigoje parodoma, kiek žygių įkelta ir kiek nepavyko perskaityti.",
    HelpKey.SettingsPurpose to
        "Bendri programėlės parametrai: sąsajos kalba, išvaizda, grybų kortelių vaizdas ir tvarka įrašymo " +
            "ekrane bei žemėlapio priežiūra.",
    HelpKey.SettingsLanguage to
        "Eilutė „Sąsajos kalba“ atveria kalbų sąrašą: paspaudimas pasirenka kalbą, varnelė viršuje " +
            "patvirtina pasirinkimą, rodyklė išeina nieko nekeisdama. Kalba pritaikoma iš karto visoje " +
            "programėlėje, paleisti iš naujo nereikia.",
    HelpKey.SettingsTheme to
        "„Išvaizda“ perjungia šviesią ir tamsią programėlės temą. „Sistemos“ atiduoda pasirinkimą " +
            "telefonui: programėlė tamsėja ir šviesėja kartu su juo.",
    HelpKey.SettingsMushroomSize to
        "Slankiklis nustato grybų ženkliukų dydį žemėlapyje — ir įrašymo ekrane, ir suvestiniame „Radinių " +
            "žemėlapyje“. Paveikslėlis po slankikliu keičiasi dar velkant, tad dydį matote prieš " +
            "paleisdami.",
    HelpKey.SettingsMushroomOrder to
        "Paprastai ką tik pažymėti grybai pakyla į kortelių juostos pradžią. „Nekintama grybų tvarka“ tai " +
            "visiškai išjungia, o „Atkurti grybų tvarką žygio pabaigoje“ grąžina pradinę tvarką, kai " +
            "žygis baigtas.",
    HelpKey.SettingsMapData to
        "„Atnaujinti žemėlapio duomenis“ patikrina, ar žemėlapis pasikeitė serveryje, ir, jei taip, iš " +
            "naujo atsiunčia visas išsaugotas neprisijungus sritis. „Išvalyti žemėlapio podėlį“ pašalina " +
            "tik tai, kas įsikėlė naršant — sritys iš „Išankstinio atsisiuntimo“ lieka vietoje.",
)
