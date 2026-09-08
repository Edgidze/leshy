package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Türkmençe — kömek ekranlary, `.claude/plans/help-screens.md`. */
internal val turkmenHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "Programmanyň esasy ekrany: gezelenç şu ýerde ýazylýar. GPS arkaly siziň yzyňyz ýazylýar, her " +
            "tapyndy bolsa koordinatalary we wagty bilen saklanýar — hem-de derrew ýada düşýär, şonuň " +
            "üçin gezelenji islendik pursatda kesip bolýar, ýazylany ýitmeýär.",
    HelpKey.RecordStartFinish to
        "«Başlamak» ady soraýar we ýazgyny başlaýar; soňra düwme «Arakesme» bolýar, arakesmede bolsa " +
            "«Dowam etmek» we «Tamamlamak» peýda bolýar. «Tamamlamak» gezelenji ýapýar we ony " +
            "«Gezelençler arhiwine» geçirýär.",
    HelpKey.RecordTiles to
        "Aşakdaky kömelek plitkalary tapyndylary bellemek üçin: «+» häzirki nokadyňyzda tapyndy goýýar, " +
            "«−» şol görnüşiň soňky ýalňyş bellikini aýyrýar. «+» düwmesine uzak basmak birnäçesini " +
            "birbada girizmegi açýar; bir gezelençde birmeňzeş kömelekden 999-dan köpüsini bellemek " +
            "bolmaýar.",
    HelpKey.RecordPlace to
        "Çepdäki tegelek düwme ýeri belleýär — ady, beýany we suraty bilen. Ýer häzir duran nokadyňyza " +
            "goýulýar we gezelençden soň hem kartada galýar.",
    HelpKey.RecordNavigation to
        "Ýer bellikine uzak basmak şoňa tarap ugrukdyrmany açýar: sag ýokardaky panel ugry we maksada " +
            "çenli aralygy görkezýär. Paneldäki haç ugrukdyrmany öçürýär.",
    HelpKey.RecordSearchAndOwn to
        "Sagdaky lupa kömelegi ady boýunça tapýar we onuň plitkasyny lentanyň başyna geçirýär — köp " +
            "görnüş açyk bolanda bu çaltrak. Lentanyň plýusly soňky plitkasy katalogda ýok öz görnüşiňizi " +
            "goşýar.",
    HelpKey.RecordFilters to
        "Çep ýokardaky «Filtrler» düwmesi haýsy görnüşleriň we haýsy döwrüň tapyndylarynyň kartada " +
            "görünjegini kesgitleýär, üstündäki san bolsa häzir näçe filtriň açykdygyny görkezýär. Filtr " +
            "«Tapyndylar kartasy» bilen umumy: bu ýerde açanyňyz ol ýerde-de işleýär.",
    HelpKey.RecordBackground to
        "Yzyň ýazylmagy programma arka meýdanda bolanda-da dowam edýär. Android-de dowam edýän " +
            "gezelenç «+»/«−» düwmeli habarnama görnüşinde-de durýar — tapyndyny telefonyň gulpuny " +
            "açman bellemek bolýar. Häzirki gezelençden başga karta öňki gezelençleriň tapyndylaryny " +
            "we bellenen ýerlerini görkezýär — olardan nirede ýörändigiňiz we ol ýerde nämäniň " +
            "bolandygy görünýär.",
    HelpKey.ArchivePurpose to
        "Ähli gezelençleriňiz, täzeleri ýokarda. Kartoçkada ady, senesi, dowamlylygy, kilometrleri, " +
            "tapyndy sany we geçilen yzyň kiçi şekili bar.",
    HelpKey.ArchiveDetail to
        "Kartoçka basmak gezelenji doly açýar: statistika, görnüşler boýunça tapyndylar, bellenen ýerler, " +
            "beýan we «Kartany görmek» düwmesi. Adyny we beýanyny şol ýerde üýtgetse bolýar.",
    HelpKey.ArchiveShare to
        "«Paýlaşmak» düwmesi gezelenjiň belläniňiz böleklerinden surat ýygnaýar. Gezelenjiň kartasyny " +
            "ibermezden öň ýatda saklaň: ondan kömelekleri anyk nireden tapanyňyz görünýär.",
    HelpKey.ArchiveSelection to
        "Kartoçka uzak basmak saýlaw tertibini açýar: gerekli gezelençleri basyp belläň we «Gezelençleri " +
            "pozmak» düwmesine basyň; «Yza» düwmesi bu tertipden çykýar. Pozmak yzyna gaýtarylmaýar — " +
            "gezelenç bilen bilelikde onuň yzy, tapyndylary, bellenen ýerleri we suratlary ýitýär.",
    HelpKey.ArchiveUnfinished to
        "Tamamlanmadyk gezelenç hem sanawda görünýär: gutaran wagtynyň ýerine onda «dowam edýär» diýlip " +
            "ýazylýar. Beýle gezelenjiň heniz dowamlylygy ýok, şonuň üçin «Tapyndylar kartasyndaky» umumy " +
            "wagta girmeýär.",
    HelpKey.MapPurpose to
        "Jemleýji karta: ähli gezelençleriňiziň tapyndylary, yzlary we bellenen ýerleri bir kendirde " +
            "birbada. Umumy suraty görmek üçin gerek — kömelek ýerleriňiz nirede we olar ýyldan-ýyla " +
            "nähili üýtgeýär.",
    HelpKey.MapFullScreen to
        "Ýokarda ähli tapyndylary birbada görkezýän karta; oňa basmak kartany doly ekranda açýar. Tapyndy " +
            "köp bolanda ýakyn bellikler sanly tegelege ýygnanýar — kartany golaýlaşdyryň, ol aýry-aýry " +
            "kömeleklere dargaýar. Kömelek nyşanlarynyň ölçegi «Sazlamalarda» kadalaşdyrylýar.",
    HelpKey.MapSliders to
        "Kartanyň aşagynda iki süýşüriji bar — seneler aralygy we möwsüm, ýagny aýlar aralygy — olardan " +
            "aşakdakylaryň ählisi saýlanana görä hasaplanýar. Süýşürijiler diňe bir günden köp günde " +
            "gezelenjiňiz bolanda peýda bolýar.",
    HelpKey.MapStats to
        "Süýşürijileriň aşagynda: näçe gezelenç, kilometr, wagt we tapyndy bolandygy, görnüşler boýunça " +
            "plitkalar we tegelek diagramma. Umumy wagt tamamlanan gezelençlerden ýygnalýar: " +
            "tamamlanmadygyň heniz dowamlylygy ýok.",
    HelpKey.MapFilters to
        "«Filtrler» düwmesi doly ekranly kartada, çep ýokarda dur: ol ýerde şol iki ok, görnüşleriň " +
            "sanawy we öňki yzlary görkezmek açary bar. Düwmedäki san näçe filtriň açykdygyny aýdýar; " +
            "filtr ýazgy ekrany bilen umumy.",
    HelpKey.MapPlaces to
        "Ýer bellikine basmak onuň suratly we beýanly kartoçkasyny açýar. Şol ýerden ýeri üýtgetmek ýa-da " +
            "pozmak hem bolýar.",
    HelpKey.SpeciesPurpose to
        "Bu ýerde ýazgy ekranynda haýsy kömelekleriň plitka boljakdygyny çözýärsiňiz. Katalog ýurtlar " +
            "boýunça ýygyndylara bölünen, ýanynda bolsa katalogda ýok görnüşler ýaşaýar — olary özüňiz " +
            "goşýarsyňyz.",
    HelpKey.SpeciesCollections to
        "«Kömelek ýygyndylarynda» ýurduň setirine basmak onuň görnüşlerini açýar: ýurduň " +
            "ýanyndaky bellik tutuş ýygyndyny açýar, içindäki bellikler — aýry görnüşleri. Ýokardaky " +
            "gözleg meýdany ada görä ýurdy-da, aýratyn kömelegi-de tapýar.",
    HelpKey.SpeciesOwn to
        "«Goşulan kömeleklerde» «Kömelek goşmak» düwmesi görnüşi açýar: ady, ylmy ady, bellikiň " +
            "reňki we surat — kameradan, galereýadan ýa-da katalogdan. Soňra programma «Haýsy " +
            "ýygynda?» diýip soraýar: öz adyňyz şeýle kömelekleri bir ýere jemleýär, boş meýdan olary " +
            "«Beýlekiler» ýygyndysyna salýar. Galam goşulan görnüşi üýtgedýär, haç ony pozýar.",
    HelpKey.SpeciesCheckboxes to
        "Aýrylan bellik hiç zady pozmaýar — görnüş diňe plitka bolup görünmeýär, öňki tapyndylar bolsa " +
            "ýerinde galýar. Öz görnüşiňizi pozmak bolsa yzyna gaýtarylmaýar: onuň öňki gezelençlerdäki " +
            "ähli bellikleri «Näbelli kömelege» geçýär. «arhiwden» ýazgyly görnüşler import edilen " +
            "gezelençler bilen gelendir.",
    HelpKey.SpeciesImages to
        "Programmadaky ähli kömelek suratlary şertlidir: olar plitkany tanamaga kömek edýär, tokaýdaky " +
            "kömelegi däl. Olara görä nätanyş kömelekleri kesgitlemäň.",
    HelpKey.PreparationPurpose to
        "Karta böleklerini öňünden telefonyň ýadyna ýükleýär, şonda tokaýda internetsiz karta ýerinde " +
            "galýar: bolmasa aragatnaşykdan uzakda kartanyň ýerine boş fon bolar.",
    HelpKey.PreparationDownload to
        "Gerekli sebiti tapyň — kartany süýşüriň we ulaldyň — soňra aşaky sag burçdaky aşak oklyja " +
            "tegelek düwmä basyň. Programma häzir ekranda duranyň näçe ýer tutjakdygyny görkezýär: «Şu " +
            "sebiti ýüklemek» ady soraýar we ýüklemäni başlaýar, «Ýatyrmak» kartany yzyna getirýär.",
    HelpKey.PreparationRegions to
        "Ýüklenen sebitler aşakda zolak bolup durýar. Plitka basmak kartada şol sebite uçýar, plitkadaky " +
            "düwmeler bolsa ýüklemäni arakesmä goýýar we dowam etdirýär, ýalňyşdan soň synanyşygy " +
            "gaýtalaýar we sebiti pozýar.",
    HelpKey.PreparationAreaSize to
        "Ekranda görünýäniň hut özi ýüklenýär, şonuň üçin kartany süýşürdigiňizçe ölçeg bahasy üýtgeýär. " +
            "Sebit näçe uly bolsa, ony şonça-da az jikme-jik etmeli bolýar — bir uly sebitiň ýerine " +
            "birnäçe kiçi bölegi ýüklemek amatly. Sebitleriň atlary gaýtalanmaly däl.",
    HelpKey.PreparationBackground to
        "Ýükleme arka meýdanda gidýär we ekrandan çyksaňyz-da kesilmeýär, arakesmede bolsa ösüşi " +
            "saklanýar. «Sazlamalardaky» «Karta maglumatlaryny täzelemek» saklanan ähli sebiti täzeden " +
            "ýükleýär.",
    HelpKey.DataPurpose to
        "Gezelençleri telefonlaryň arasynda geçirmek we ätiýaçlyk nusga: saýlanan gezelençler bir arhiw " +
            "faýlyna ýazylýar, beýle faýly bolsa yzyna ýükläp bolýar — şu ýa-da başga enjamda.",
    HelpKey.DataExport to
        "Ýokardaky açar «Eksport» ýa-da «Import» saýlaýar. «Eksportda» arhiwe at beriň, gezelençleri " +
            "saýlamak setirine basyp gereklilerini belläň, soňra «Taýýar» — telefon faýly nirede " +
            "saklamalydygyny soraýar.",
    HelpKey.DataImport to
        "«Importda» «Faýl saýlamak» düwmesine basyň, isleseňiz ýüklenýän gezelençleriň atlaryna goşuljak " +
            "bellik ýazyň we «Taýýar» düwmesine basyň; arhiw okalanda «Arhiwe» düwmesi peýda bolar. " +
            "«Ýatyrmak» düwmesi girizileni hiç zat saklaman arassalaýar.",
    HelpKey.DataArchiveContents to
        "Arhiwe yz, tapyndylar, bellenen ýerler, suratlar we katalogda ýok kömelek görnüşleri düşýär — " +
            "beýleki enjamda olar «Goşulan kömeleklerde» «arhiwden» ýazgysy bilen peýda bolýar.",
    HelpKey.DataDuplicates to
        "Import gezelençleri hemişe barlarynyň ýanyna goşýar we hiç zady çalyşmaýar, şonuň üçin şol faýly " +
            "gaýtadan ýüklemek olary ýene bir gezek döreder: atlara goşmaça soňra birini beýlekisinden " +
            "tapawutlandyrmaga kömek edýär. Ahyrynda näçe gezelenjiň ýüklenendigi we näçesiniň " +
            "okalmandygy görkezilýär.",
    HelpKey.SettingsPurpose to
        "Programmanyň umumy sazlamalary: interfeýs dili, görnüşi, ýazgy ekranyndaky kömelek plitkalarynyň " +
            "görnüşi we tertibi hem-de karta hyzmaty.",
    HelpKey.SettingsLanguage to
        "«Interfeýs dili» setiri dilleriň sanawyny açýar: basmak dili saýlaýar, ýokardaky bellik saýlawy " +
            "tassyklaýar, ok hiç zady üýtgetmän çykýar. Dil bütin programmada derrew ulanylýar, gaýtadan " +
            "işletmek gerek däl.",
    HelpKey.SettingsTheme to
        "«Görnüş» programmanyň açyk we goýy mowzugyny çalyşýar. «Ulgam» saýlawy telefona berýär: " +
            "programma onuň bilen bilelikde garalýar we ýagtylýar.",
    HelpKey.SettingsMushroomSize to
        "Süýşüriji kartadaky kömelek nyşanlarynyň ölçegini kesgitleýär — ýazgy ekranynda-da, jemleýji " +
            "«Tapyndylar kartasynda-da». Aşagyndaky surat siz süýreniňizde üýtgeýär, şeýlelikde ölçegi " +
            "goýbermezden öň görýärsiňiz.",
    HelpKey.SettingsMushroomOrder to
        "Adatça täze bellenen kömelekler plitka lentasynyň başyna galýar. «Kömelekleriň tertibini " +
            "berkitmek» muny düýbünden öçürýär, «Gezelenjiň ahyrynda kömelekleriň tertibini dikeltmek» " +
            "bolsa gezelenç gutaranda başdaky tertibi gaýtarýar.",
    HelpKey.SettingsMapData to
        "«Karta maglumatlaryny täzelemek» kartanyň serwerde üýtgändigini barlaýar we üýtgän bolsa, " +
            "saklanan ähli oflaýn sebiti täzeden ýükleýär. «Karta keşini arassalamak» diňe görlende " +
            "ýüklenenleri pozýar — «Öňünden ýüklemedäki» sebitler ýerinde galýar.",
)
