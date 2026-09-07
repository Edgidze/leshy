package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Slovenčina — obrazovky pomocníka, `.claude/plans/help-screens.md`. */
internal val slovakHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "Hlavná obrazovka aplikácie: tu sa zaznamenáva prechádzka. Z GPS sa zapisuje vaša trasa a každý " +
            "nález sa ukladá so súradnicami a časom — do pamäte ide hneď, takže prechádzku možno " +
            "kedykoľvek prerušiť a zaznamenané sa nestratí.",
    HelpKey.RecordStartFinish to
        "„Štart“ sa opýta na názov a spustí záznam; potom sa tlačidlo zmení na „Pauza“ a počas pauzy sa " +
            "objavia „Pokračovať“ a „Ukončiť“. „Ukončiť“ prechádzku uzavrie a presunie ju do „Archívu " +
            "prechádzok“.",
    HelpKey.RecordTiles to
        "Dlaždice húb dole slúžia na označovanie nálezov: „+“ zapíše nález vo vašom aktuálnom bode, „−“ " +
            "odoberie posledné chybné označenie tohto druhu. Dlhé podržanie „+“ otvorí zadanie viacerých " +
            "kusov naraz; viac než 999 rovnakých húb za jednu prechádzku označiť nemožno.",
    HelpKey.RecordPlace to
        "Okrúhle tlačidlo vľavo označí miesto — s názvom, popisom a fotografiou. Miesto sa umiestni tam, " +
            "kde práve stojíte, a na mape zostane aj po prechádzke.",
    HelpKey.RecordNavigation to
        "Dlhé podržanie značky miesta zapne navigáciu k nemu: panel vpravo hore ukazuje smer a " +
            "vzdialenosť k cieľu. Krížik na paneli navigáciu vypne.",
    HelpKey.RecordSearchAndOwn to
        "Lupa vpravo nájde hubu podľa názvu a presunie jej dlaždicu na začiatok pásu — to je rýchlejšie, " +
            "keď máte zapnutých veľa druhov. Posledná dlaždica pásu, s plusom, pridá vlastný druh, ktorý " +
            "v katalógu nie je.",
    HelpKey.RecordFilters to
        "Tlačidlo „Filtre“ vľavo hore určuje, nálezy ktorých druhov a za aké obdobie sa na mape ukážu, a " +
            "číslo na ňom hovorí, koľko filtrov je práve zapnutých. Filter je spoločný s „Mapou nálezov“: " +
            "čo zapnete tu, platí aj tam.",
    HelpKey.RecordBackground to
        "Záznam trasy pokračuje aj vtedy, keď je aplikácia na pozadí. Okrem aktuálnej prechádzky mapa " +
            "ukazuje nálezy a označené miesta z minulých prechádzok — vidno z nich, kadiaľ ste už chodili " +
            "a čo tam bolo.",
    HelpKey.ArchivePurpose to
        "Všetky vaše prechádzky, najnovšie hore. Na karte je názov, dátum, trvanie, kilometre, počet " +
            "nálezov a náhľad prejdenej trasy.",
    HelpKey.ArchiveDetail to
        "Ťuknutie na kartu otvorí celú prechádzku: štatistiky, nálezy podľa druhov, označené miesta, " +
            "popis a tlačidlo „Zobraziť mapu“. Názov a popis možno zmeniť na tom istom mieste.",
    HelpKey.ArchiveShare to
        "Tlačidlo „Zdieľať“ zloží obrázok z tých častí prechádzky, ktoré zaškrtnete. Skôr než mapu " +
            "prechádzky odošlete, pamätajte: vidno z nej presne, kde ste huby našli.",
    HelpKey.ArchiveSelection to
        "Dlhé podržanie karty zapne režim výberu: ťuknutím označte prechádzky a stlačte „Odstrániť " +
            "prechádzky“, tlačidlo „Späť“ z tohto režimu vyjde. Odstránenie je nezvratné — spolu s " +
            "prechádzkou zmizne jej trasa, nálezy, označené miesta aj fotografie.",
    HelpKey.ArchiveUnfinished to
        "Neukončená prechádzka je v zozname tiež vidieť: namiesto času konca má napísané „neukončená“. " +
            "Taká prechádzka ešte nemá trvanie, a preto sa nezapočítava do celkového času na „Mape " +
            "nálezov“.",
    HelpKey.MapPurpose to
        "Súhrnná mapa: nálezy, trasy a označené miesta všetkých vašich prechádzok naraz na jednom plátne. " +
            "Je tu na to, aby ste videli celkový obraz — kde máte hubárske miesta a ako sa menia rok po " +
            "roku.",
    HelpKey.MapFullScreen to
        "Hore je mapa so všetkými nálezmi naraz; ťuknutie na ňu otvorí mapu na celú obrazovku. Keď je " +
            "nálezov veľa, blízke značky sa zlúčia do kolieska s číslom — priblížte mapu a rozpadne sa na " +
            "jednotlivé huby. Veľkosť ikon húb sa nastavuje v „Nastaveniach“.",
    HelpKey.MapSliders to
        "Pod mapou sú dva posuvníky — rozsah dátumov a sezóna, teda rozsah mesiacov — a všetko pod nimi " +
            "sa počíta podľa vybraného. Posuvníky sa objavia až vtedy, keď máte prechádzky z viac než " +
            "jedného dňa.",
    HelpKey.MapStats to
        "Pod posuvníkmi: koľko bolo prechádzok, kilometrov, času a nálezov, dlaždice podľa druhov a " +
            "koláčový graf. Celkový čas sčítava dokončené prechádzky: tá neukončená ešte trvanie nemá.",
    HelpKey.MapFilters to
        "Tlačidlo „Filtre“ žije na celoobrazovkovej mape, vľavo hore: sú tam tie isté dve osi, zoznam " +
            "druhov a prepínač zobrazenia predošlých trás. Číslo na tlačidle hovorí, koľko filtrov je " +
            "zapnutých; filter je spoločný s obrazovkou záznamu.",
    HelpKey.MapPlaces to
        "Ťuknutie na značku miesta otvorí jeho kartu s fotografiou a popisom. Odtiaľ sa miesto dá aj " +
            "zmeniť alebo odstrániť.",
    HelpKey.SpeciesPurpose to
        "Tu rozhodujete, ktoré huby budú dlaždicami na obrazovke záznamu. Katalóg je rozdelený na zbierky " +
            "podľa krajín a vedľa neho žijú druhy, ktoré v katalógu nie sú — tie pridávate sami.",
    HelpKey.SpeciesCollections to
        "V „Zbierkach húb“ ťuknutie na riadok krajiny rozbalí jej druhy: zaškrtnutie pri krajine zapne " +
            "celú zbierku, zaškrtnutia vnútri jednotlivé druhy. Vyhľadávacie pole hore nájde krajinu " +
            "podľa názvu.",
    HelpKey.SpeciesOwn to
        "V „Pridaných hubách“ tlačidlo „Pridať hubu“ otvorí formulár: názov, vedecký názov, farba značky " +
            "a obrázok — z fotoaparátu, z galérie alebo z katalógu. Ceruzka zmení už pridaný druh, krížik " +
            "ho odstráni.",
    HelpKey.SpeciesCheckboxes to
        "Zrušené zaškrtnutie nič nemaže — druh sa jednoducho prestane ukazovať ako dlaždica a predošlé " +
            "nálezy zostanú na mieste. Odstránenie vlastného druhu je naopak nezvratné: všetky jeho " +
            "označenia v predošlých prechádzkach prejdú na „Neznámu hubu“. Druhy s popisom „z archívu“ " +
            "prišli spolu s importovanými prechádzkami.",
    HelpKey.SpeciesImages to
        "Všetky obrázky húb v aplikácii sú iba orientačné: pomáhajú spoznať dlaždicu, nie hubu v lese. " +
            "Neurčujte podľa nich neznáme huby.",
    HelpKey.PreparationPurpose to
        "Vopred stiahne kusy mapy do pamäte telefónu, aby mapa zostala v lese bez internetu na mieste: " +
            "bez toho bude mimo signálu namiesto mapy prázdne pozadie.",
    HelpKey.PreparationDownload to
        "Nájdite potrebnú oblasť — posúvajte a približujte mapu — a potom stlačte okrúhle tlačidlo so " +
            "šípkou nadol vpravo dole. Aplikácia ukáže, koľko miesta zaberie to, čo je práve na " +
            "obrazovke: „Stiahnuť túto oblasť“ sa opýta na názov a spustí sťahovanie, „Zrušiť“ vráti " +
            "mapu.",
    HelpKey.PreparationRegions to
        "Stiahnuté oblasti ležia v páse dole. Ťuknutie na štítok preletí k tejto oblasti na mape a " +
            "tlačidlá na štítku sťahovanie pozastavia a obnovia, zopakujú pokus po chybe a oblasť " +
            "odstránia.",
    HelpKey.PreparationAreaSize to
        "Sťahuje sa presne to, čo vidno na obrazovke, preto sa odhad veľkosti mení, kým mapou pohybujete. " +
            "Čím väčšia oblasť, tým menej podrobná musí byť — oplatí sa stiahnuť niekoľko menších úsekov " +
            "než jeden obrovský. Názvy oblastí sa nesmú opakovať.",
    HelpKey.PreparationBackground to
        "Sťahovanie beží na pozadí a nepreruší sa, keď obrazovku opustíte, a počas pauzy sa postup " +
            "uchová. „Aktualizovať údaje mapy“ v „Nastaveniach“ stiahne všetky uložené oblasti nanovo.",
    HelpKey.DataPurpose to
        "Prenos prechádzok medzi telefónmi a záloha: vybrané prechádzky sa vyexportujú do jedného " +
            "archívneho súboru a taký súbor možno načítať späť — na tomto alebo na inom zariadení.",
    HelpKey.DataExport to
        "Prepínač hore volí „Export“ alebo „Import“. V „Exporte“ zadajte názov archívu, ťuknite na riadok " +
            "výberu prechádzok a zaškrtnite potrebné, potom „Hotovo“ — telefón sa opýta, kam súbor " +
            "uložiť.",
    HelpKey.DataImport to
        "V „Importe“ stlačte „Vybrať súbor“, prípadne napíšte dodatok, ktorý sa pridá k názvom načítaných " +
            "prechádzok, a stlačte „Hotovo“; keď sa archív prečíta, objaví sa tlačidlo „Do archívu“. " +
            "Tlačidlo „Zrušiť“ zadané vymaže bez uloženia.",
    HelpKey.DataArchiveContents to
        "Do archívu sa dostane trasa, nálezy, označené miesta, fotografie a tie druhy húb, ktoré v " +
            "katalógu nie sú — na druhom zariadení sa objavia v „Pridaných hubách“ s popisom „z archívu“.",
    HelpKey.DataDuplicates to
        "Import vždy pridáva prechádzky k tým existujúcim a nič nenahrádza, takže opätovné načítanie toho " +
            "istého súboru ich vytvorí znova: dodatok pri názvoch pomôže ich neskôr rozlíšiť. Na konci sa " +
            "ukáže, koľko prechádzok sa načítalo a koľko sa ich nedalo prečítať.",
    HelpKey.SettingsPurpose to
        "Všeobecné nastavenia aplikácie: jazyk rozhrania, vzhľad, podoba a poradie dlaždíc húb na " +
            "obrazovke záznamu a údržba mapy.",
    HelpKey.SettingsLanguage to
        "Riadok „Jazyk rozhrania“ otvorí zoznam jazykov: ťuknutie jazyk vyberie, zaškrtnutie hore voľbu " +
            "potvrdí, šípka odíde bez zmeny. Jazyk sa prejaví hneď v celej aplikácii, reštart netreba.",
    HelpKey.SettingsTheme to
        "„Vzhľad“ prepína svetlú a tmavú tému aplikácie. „Systémový“ prenecháva voľbu telefónu: aplikácia " +
            "tmavne a svetlie spolu s ním.",
    HelpKey.SettingsMushroomSize to
        "Posuvník nastavuje veľkosť ikon húb na mape — aj na obrazovke záznamu, aj na súhrnnej „Mape " +
            "nálezov“. Obrázok pod posuvníkom sa mení už počas ťahania, takže veľkosť vidíte skôr, než " +
            "pustíte.",
    HelpKey.SettingsMushroomOrder to
        "Zvyčajne sa práve označené huby posunú na začiatok pásu dlaždíc. „Nemenné poradie húb“ to úplne " +
            "vypne a „Obnoviť poradie húb na konci prechádzky“ vráti pôvodné poradie, len čo sa " +
            "prechádzka skončí.",
    HelpKey.SettingsMapData to
        "„Aktualizovať údaje mapy“ overí, či sa mapa na serveri zmenila, a ak áno, stiahne znova všetky " +
            "uložené offline oblasti. „Vymazať vyrovnávaciu pamäť mapy“ odstráni len to, čo sa donačítalo " +
            "pri prezeraní — oblasti zo „Stiahnutia vopred“ zostanú.",
)
