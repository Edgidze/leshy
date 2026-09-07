package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Shqip — ekranet e ndihmës, `.claude/plans/help-screens.md`. */
internal val albanianHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "Ekrani kryesor i aplikacionit: këtu regjistrohet shëtitja. Me GPS shkruhet gjurma juaj, ndërsa " +
            "çdo gjetje ruhet me koordinata dhe orë — dhe shkon menjëherë në memorie, prandaj shëtitja " +
            "mund të ndërpritet në çdo çast pa humbur atë që u regjistrua.",
    HelpKey.RecordStartFinish to
        "«Nis» pyet për emrin dhe fillon regjistrimin; më pas butoni kthehet në «Pauzë», ndërsa në pauzë " +
            "shfaqen «Vazhdo» dhe «Përfundo». «Përfundo» e mbyll shëtitjen dhe e kalon te «Arkivi i " +
            "shëtitjeve».",
    HelpKey.RecordTiles to
        "Pllakëzat e kërpudhave poshtë janë ato me të cilat shënohen gjetjet: «+» shënon një gjetje në " +
            "pikën tuaj aktuale, «−» heq shënimin e fundit të gabuar të asaj lloji. Shtypja e gjatë mbi " +
            "«+» hap futjen e disa copëve njëherësh; më shumë se 999 kërpudha të njëjta në një shëtitje " +
            "nuk mund të shënohen.",
    HelpKey.RecordPlace to
        "Butoni i rrumbullakët majtas shënon një vend — me emër, përshkrim dhe fotografi. Vendi vendoset " +
            "aty ku ndodheni tani dhe mbetet në hartë edhe pas shëtitjes.",
    HelpKey.RecordNavigation to
        "Shtypja e gjatë mbi shenjën e një vendi ndez navigimin drejt tij: paneli lart djathtas tregon " +
            "drejtimin dhe distancën deri te objektivi. Kryqi në panel e fik navigimin.",
    HelpKey.RecordSearchAndOwn to
        "Lupa djathtas gjen kërpudhën sipas emrit dhe e vendos pllakëzën e saj në fillim të rreshtit — " +
            "kështu është më shpejt kur janë ndezur shumë lloje. Pllakëza e fundit, me plus, shton llojin " +
            "tuaj që nuk gjendet në katalog.",
    HelpKey.RecordFilters to
        "Butoni «Filtrat» lart majtas cakton gjetjet e cilave lloje dhe të cilës periudhë të shfaqen në " +
            "hartë, ndërsa numri mbi të tregon sa filtra janë ndezur tani. Filtri është i përbashkët me " +
            "«Hartën e gjetjeve»: ajo që ndizni këtu vlen edhe atje.",
    HelpKey.RecordBackground to
        "Regjistrimi i gjurmës vazhdon edhe kur aplikacioni është në sfond. Përveç shëtitjes aktuale, " +
            "harta tregon gjetjet dhe vendet e shënuara të shëtitjeve të kaluara — prej tyre duket ku " +
            "keni ecur tashmë dhe çfarë kishte atje.",
    HelpKey.ArchivePurpose to
        "Të gjitha shëtitjet tuaja, të rejat lart. Në kartë janë emri, data, kohëzgjatja, kilometrat, " +
            "numri i gjetjeve dhe një miniaturë e gjurmës së përshkuar.",
    HelpKey.ArchiveDetail to
        "Shtypja mbi kartë hap tërë shëtitjen: statistika, gjetjet sipas llojeve, vendet e shënuara, " +
            "përshkrimi dhe butoni «Shiko hartën». Emri dhe përshkrimi mund të ndryshohen po aty.",
    HelpKey.ArchiveShare to
        "Butoni «Ndaj» ndërton një figurë nga ato pjesë të shëtitjes që i shënoni me shenjë. Para se ta " +
            "dërgoni hartën e shëtitjes, mbani mend: prej saj duket saktësisht ku i gjetët kërpudhat.",
    HelpKey.ArchiveSelection to
        "Shtypja e gjatë mbi kartë ndez regjimin e përzgjedhjes: shënoni shëtitjet e dëshiruara me " +
            "shtypje dhe shtypni «Fshi shëtitjet», ndërsa butoni «Prapa» del nga ky regjim. Fshirja është " +
            "e pakthyeshme — bashkë me shëtitjen zhduken gjurma, gjetjet, vendet e shënuara dhe " +
            "fotografitë e saj.",
    HelpKey.ArchiveUnfinished to
        "Shëtitja e papërfunduar duket gjithashtu në listë: në vend të orës së mbarimit tek ajo shkruhet " +
            "«në vazhdim». Një shëtitje e tillë ende nuk ka kohëzgjatje, prandaj nuk hyn në kohën e " +
            "përgjithshme te «Harta e gjetjeve».",
    HelpKey.MapPurpose to
        "Harta përmbledhëse: gjetjet, gjurmët dhe vendet e shënuara të të gjitha shëtitjeve tuaja " +
            "njëherësh në një pëlhurë. Shërben që të shihni pamjen e përgjithshme — ku i keni vendet me " +
            "kërpudha dhe si ndryshojnë nga viti në vit.",
    HelpKey.MapFullScreen to
        "Lart është harta me të gjitha gjetjet njëherësh; shtypja mbi të e hap hartën në tërë ekranin. " +
            "Kur gjetjet janë të shumta, shenjat e afërta mblidhen në një rreth me numër — afroni hartën " +
            "dhe ai shpërbëhet në kërpudha të veçanta. Madhësia e ikonave caktohet te «Cilësimet».",
    HelpKey.MapSliders to
        "Nën hartë janë dy rrëshqitës — intervali i datave dhe sezoni, pra intervali i muajve — dhe " +
            "gjithçka nën ta llogaritet sipas asaj që zgjidhni. Rrëshqitësit shfaqen vetëm kur keni " +
            "shëtitje nga më shumë se një ditë.",
    HelpKey.MapStats to
        "Nën rrëshqitës: sa shëtitje, kilometra, kohë dhe gjetje pati, pllakëzat sipas llojeve dhe " +
            "diagrami rrethor. Koha e përgjithshme mblidhet nga shëtitjet e përfunduara: e papërfunduara " +
            "ende nuk ka kohëzgjatje.",
    HelpKey.MapFilters to
        "Butoni «Filtrat» rri në hartën me ekran të plotë, lart majtas: aty janë të njëjtat dy boshte, " +
            "lista e llojeve dhe çelësi për shfaqjen e gjurmëve të kaluara. Numri mbi buton tregon sa " +
            "filtra janë ndezur; filtri është i përbashkët me ekranin e regjistrimit.",
    HelpKey.MapPlaces to
        "Shtypja mbi shenjën e një vendi hap kartën e tij me fotografi dhe përshkrim. Që aty vendi mund " +
            "edhe të ndryshohet ose të fshihet.",
    HelpKey.SpeciesPurpose to
        "Këtu vendosni cilat kërpudha do të jenë pllakëza në ekranin e regjistrimit. Katalogu është ndarë " +
            "në përmbledhje sipas vendeve, ndërsa pranë tij jetojnë llojet që nuk gjenden në katalog — " +
            "ato i shtoni vetë.",
    HelpKey.SpeciesCollections to
        "Te «Përmbledhjet e kërpudhave» shtypja mbi rreshtin e një vendi hap llojet e tij: shenja pranë " +
            "vendit ndez tërë përmbledhjen, shenjat brenda llojet e veçanta. Fusha e kërkimit lart gjen " +
            "vendin sipas emrit.",
    HelpKey.SpeciesOwn to
        "Te «Kërpudhat e shtuara» butoni «Shto kërpudhë» hap një formular: emri, emri shkencor, ngjyra e " +
            "shenjës dhe figura — nga kamera, nga galeria ose nga katalogu. Lapsi ndryshon një lloj " +
            "tashmë të shtuar, kryqi e fshin.",
    HelpKey.SpeciesCheckboxes to
        "Heqja e shenjës nuk fshin asgjë — lloji thjesht nuk shfaqet më si pllakëz, ndërsa gjetjet e " +
            "mëparshme mbeten në vend. Fshirja e llojit tuaj, përkundrazi, është e pakthyeshme: të gjitha " +
            "shënimet e tij në shëtitjet e kaluara kalojnë te «Kërpudhë e panjohur». Llojet me " +
            "mbishkrimin «nga arkivi» kanë ardhur bashkë me shëtitje të importuara.",
    HelpKey.SpeciesImages to
        "Të gjitha figurat e kërpudhave në aplikacion janë vetëm orientuese: ndihmojnë të njihni " +
            "pllakëzën, jo kërpudhën në pyll. Mos përcaktoni sipas tyre kërpudha të panjohura.",
    HelpKey.PreparationPurpose to
        "Shkarkon paraprakisht copa të hartës në memorien e telefonit, që në pyll pa internet harta të " +
            "mbetet në vend: pa këtë, larg sinjalit, në vend të hartës do të ketë sfond bosh.",
    HelpKey.PreparationDownload to
        "Gjeni zonën e nevojshme — lëvizni dhe zmadhoni hartën — pastaj shtypni butonin e rrumbullakët me " +
            "shigjetë poshtë, poshtë djathtas. Aplikacioni tregon sa vend do të zërë ajo që duket tani në " +
            "ekran: «Shkarko këtë zonë» pyet për emrin dhe nis shkarkimin, «Anulo» e kthen hartën.",
    HelpKey.PreparationRegions to
        "Zonat e shkarkuara qëndrojnë në një rrip poshtë. Shtypja mbi pllakëz fluturon te ajo zonë në " +
            "hartë, ndërsa butonat mbi pllakëz e ndalin shkarkimin dhe e vazhdojnë, e përsërisin " +
            "përpjekjen pas gabimi dhe e fshijnë zonën.",
    HelpKey.PreparationAreaSize to
        "Shkarkohet pikërisht ajo që duket në ekran, prandaj vlerësimi i madhësisë ndryshon ndërsa " +
            "lëvizni hartën. Sa më e madhe zona, aq më pak e detajuar duhet të jetë — është më e " +
            "leverdishme të shkarkoni disa zona të vogla se një të stërmadhe. Emrat e zonave nuk duhet të " +
            "përsëriten.",
    HelpKey.PreparationBackground to
        "Shkarkimi ecën në sfond dhe nuk ndërpritet nëse largoheni nga ekrani, ndërsa në pauzë ecuria " +
            "ruhet. «Përditëso të dhënat e hartës» te «Cilësimet» i shkarkon nga e para të gjitha zonat e " +
            "ruajtura.",
    HelpKey.DataPurpose to
        "Kalimi i shëtitjeve mes telefonave dhe kopja rezervë: shëtitjet e zgjedhura eksportohen në një " +
            "skedar të vetëm arkivi, dhe një skedar i tillë mund të ngarkohet prapa — në këtë ose në një " +
            "pajisje tjetër.",
    HelpKey.DataExport to
        "Çelësi lart zgjedh «Eksport» ose «Import». Te «Eksport» jepni emrin e arkivit, shtypni rreshtin " +
            "e zgjedhjes së shëtitjeve dhe shënoni ato që doni, pastaj «Gati» — telefoni do të pyesë ku " +
            "ta ruajë skedarin.",
    HelpKey.DataImport to
        "Te «Import» shtypni «Zgjidh skedarin», nëse doni shkruani një shtesë që do t’u ngjitet emrave të " +
            "shëtitjeve që ngarkohen, dhe shtypni «Gati»; kur arkivi të lexohet, shfaqet butoni «Te " +
            "arkivi». Butoni «Anulo» e pastron të shkruarën pa ruajtur asgjë.",
    HelpKey.DataArchiveContents to
        "Në arkiv hyjnë gjurma, gjetjet, vendet e shënuara, fotografitë dhe ato lloje kërpudhash që nuk " +
            "gjenden në katalog — në pajisjen tjetër ato shfaqen te «Kërpudhat e shtuara» me mbishkrimin " +
            "«nga arkivi».",
    HelpKey.DataDuplicates to
        "Importi gjithmonë i shton shëtitjet pranë atyre ekzistuese dhe nuk zëvendëson asgjë, prandaj " +
            "ngarkimi i të njëjtit skedar përsëri do t’i krijojë edhe një herë: shtesa te emrat ndihmon " +
            "t’i dalloni më vonë. Në fund tregohet sa shëtitje u ngarkuan dhe sa nuk u lexuan dot.",
    HelpKey.SettingsPurpose to
        "Cilësimet e përgjithshme të aplikacionit: gjuha e ndërfaqes, pamja, forma dhe renditja e " +
            "pllakëzave të kërpudhave në ekranin e regjistrimit, si dhe mirëmbajtja e hartës.",
    HelpKey.SettingsLanguage to
        "Rreshti «Gjuha e ndërfaqes» hap listën e gjuhëve: shtypja zgjedh gjuhën, shenja lart e vërteton " +
            "zgjedhjen, shigjeta del pa ndryshuar asgjë. Gjuha zbatohet menjëherë në tërë aplikacionin, " +
            "rinisja nuk nevojitet.",
    HelpKey.SettingsTheme to
        "«Pamja» kalon mes temës së çelët dhe asaj të errët të aplikacionit. «E sistemit» ia lë zgjedhjen " +
            "telefonit: aplikacioni erret dhe çelet bashkë me të.",
    HelpKey.SettingsMushroomSize to
        "Rrëshqitësi cakton madhësinë e ikonave të kërpudhave në hartë — si në ekranin e regjistrimit, " +
            "ashtu edhe në «Hartën e gjetjeve». Figura poshtë tij ndryshon që gjatë tërheqjes, kështu që " +
            "madhësinë e shihni para se ta lëshoni.",
    HelpKey.SettingsMushroomOrder to
        "Zakonisht kërpudhat e saposhënuara ngjiten në fillim të rreshtit të pllakëzave. «Ngri renditjen» " +
            "e fik këtë krejtësisht, ndërsa «Rivendos renditjen në fund të shëtitjes» e kthen renditjen " +
            "fillestare sapo shëtitja të mbarojë.",
    HelpKey.SettingsMapData to
        "«Përditëso të dhënat e hartës» kontrollon nëse harta ka ndryshuar në server dhe, nëse po, i " +
            "shkarkon sërish të gjitha zonat e ruajtura jashtë linje. «Pastro memorien e hartës» heq " +
            "vetëm atë që u ngarkua gjatë shfletimit — zonat nga «Shkarkimi paraprak» mbeten.",
)
