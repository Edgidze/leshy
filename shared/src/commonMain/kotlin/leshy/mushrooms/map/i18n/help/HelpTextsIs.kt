package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Íslenska — hjálparskjáir, `.claude/plans/help-screens.md`. */
internal val icelandicHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "Aðalskjár forritsins: hér er ferðin skráð. GPS teiknar slóðina þína og hver fundur er vistaður " +
            "með hnitum og tíma — strax, svo hætta má ferðinni hvenær sem er án þess að það sem skráð var " +
            "glatist.",
    HelpKey.RecordStartFinish to
        "„Byrja“ spyr um heiti og hefur upptöku; síðan breytist hnappurinn í „Gera hlé“ og í hléi birtast " +
            "„Halda áfram“ og „Ljúka“. „Ljúka“ lokar ferðinni og færir hana í „Safn gönguferða“.",
    HelpKey.RecordTiles to
        "Sveppareitirnir neðst eru það sem fundir eru merktir með: „+“ skráir fund á núverandi stað " +
            "þínum, „−“ fjarlægir síðustu röngu merkingu þeirrar tegundar. Langur smellur á „+“ opnar " +
            "innslátt margra í einu; ekki er hægt að merkja fleiri en 999 eins sveppi í einni ferð.",
    HelpKey.RecordPlace to
        "Kringlótti hnappurinn til vinstri merkir stað — með heiti, lýsingu og ljósmynd. Staðurinn fer " +
            "þangað sem þú stendur núna og verður áfram á kortinu eftir ferðina.",
    HelpKey.RecordNavigation to
        "Langur smellur á staðarmerki kveikir á leiðsögn þangað: spjaldið efst til hægri sýnir stefnu og " +
            "fjarlægð að markinu. Krossinn á spjaldinu slekkur á leiðsögninni.",
    HelpKey.RecordSearchAndOwn to
        "Stækkunarglerið til hægri finnur sveppinn eftir heiti og færir reitinn hans fremst í röðina — " +
            "það er fljótlegra þegar margar tegundir eru virkar. Síðasti reiturinn, með plús, bætir við " +
            "þinni eigin tegund sem er ekki í skránni.",
    HelpKey.RecordFilters to
        "Hnappurinn „Síur“ efst til vinstri ræður hvaða tegundir og hvaða tímabil birtast á kortinu, og " +
            "talan á honum segir hve margar síur eru virkar núna. Sían er sameiginleg með „Korti yfir " +
            "fundi“: það sem þú kveikir á hér gildir líka þar.",
    HelpKey.RecordBackground to
        "Skráning slóðarinnar heldur áfram þegar forritið er í bakgrunni. Auk yfirstandandi ferðar sýnir " +
            "kortið fundi og merkta staði fyrri ferða — af þeim sést hvar þú hefur þegar gengið og hvað " +
            "var þar.",
    HelpKey.ArchivePurpose to
        "Allar ferðirnar þínar, þær nýjustu efst. Á spjaldinu eru heiti, dagsetning, lengd, kílómetrar, " +
            "fjöldi funda og smámynd af genginni slóð.",
    HelpKey.ArchiveDetail to
        "Smellur á spjald opnar alla ferðina: tölfræði, fundir eftir tegundum, merktir staðir, lýsing og " +
            "hnappurinn „Skoða kortið“. Heiti og lýsingu má breyta á sama stað.",
    HelpKey.ArchiveShare to
        "Hnappurinn „Deila“ setur saman mynd úr þeim hlutum ferðarinnar sem þú hakar við. Mundu áður en " +
            "þú sendir kort ferðarinnar: á því sést nákvæmlega hvar þú fannst sveppina.",
    HelpKey.ArchiveSelection to
        "Langur smellur á spjald kveikir á valham: merktu ferðirnar sem þú vilt með smelli og ýttu á " +
            "„Eyða ferðum“; hnappurinn „Til baka“ fer út úr hamnum. Eyðing er óafturkræf — með ferðinni " +
            "hverfa slóðin, fundirnir, merktu staðirnir og myndirnar.",
    HelpKey.ArchiveUnfinished to
        "Ólokin ferð sést líka í listanum: í stað lokatíma stendur „í gangi“. Slík ferð hefur ekki lengd " +
            "enn og telst því ekki með í heildartímanum á „Korti yfir fundi“.",
    HelpKey.MapPurpose to
        "Heildarkortið: fundir, slóðir og merktir staðir allra ferða þinna í einu á einum fleti. Það er " +
            "til þess að sjá heildarmyndina — hvar sveppastaðirnir þínir eru og hvernig þeir breytast ár " +
            "frá ári.",
    HelpKey.MapFullScreen to
        "Efst er kortið með öllum fundum í einu; smellur opnar kortið á öllum skjánum. Þegar fundir eru " +
            "margir safnast nálæg merki í hring með tölu — renndu nær og hann leysist upp í staka sveppi. " +
            "Stærð sveppatáknanna er stillt í „Stillingum“.",
    HelpKey.MapSliders to
        "Undir kortinu eru tveir sleðar — dagsetningabil og árstíð, það er mánaðabil — og allt fyrir " +
            "neðan þá reiknast eftir valinu. Sleðarnir birtast fyrst þegar ferðir eru frá fleiri en einum " +
            "degi.",
    HelpKey.MapStats to
        "Undir sleðunum: hve margar ferðir, kílómetrar, tími og fundir urðu, reitirnir eftir tegundum og " +
            "kökuritið. Heildartíminn leggur saman lokaðar ferðir: ólokin ferð hefur ekki lengd enn.",
    HelpKey.MapFilters to
        "Hnappurinn „Síur“ býr á heilskjáskortinu, efst til vinstri: þar eru sömu tveir ásar, " +
            "tegundalistinn og rofinn fyrir birtingu fyrri slóða. Talan á hnappinum segir hve margar síur " +
            "eru virkar; sían er sameiginleg með upptökuskjánum.",
    HelpKey.MapPlaces to
        "Smellur á staðarmerki opnar spjald þess með ljósmynd og lýsingu. Þaðan má einnig breyta staðnum " +
            "eða eyða honum.",
    HelpKey.SpeciesPurpose to
        "Hér ákveður þú hvaða sveppir verða reitir á upptökuskjánum. Skráin skiptist í söfn eftir löndum, " +
            "og við hliðina búa tegundirnar sem eru ekki í skránni — þeim bætir þú við sjálf.",
    HelpKey.SpeciesCollections to
        "Í „Sveppasöfnum“ opnar smellur á línu lands tegundir þess: hakið við landið kveikir á öllu " +
            "safninu, hökin innan í stökum tegundum. Leitarreiturinn efst finnur land eftir heiti.",
    HelpKey.SpeciesOwn to
        "Í „Sveppir sem bætt var við“ opnar hnappurinn „Bæta við sveppi“ eyðublað: heiti, fræðiheiti, " +
            "litur merkis og mynd — úr myndavél, úr myndasafni eða úr skránni. Blýanturinn breytir tegund " +
            "sem þegar var bætt við, krossinn eyðir henni.",
    HelpKey.SpeciesCheckboxes to
        "Hak sem tekið er af eyðir engu — tegundin hættir einfaldlega að birtast sem reitur og fyrri " +
            "fundir sitja kyrrir. Að eyða eigin tegund er hins vegar óafturkræft: allar merkingar hennar " +
            "í fyrri ferðum færast í „Óþekktur sveppur“. Tegundir merktar „úr safnskrá“ komu með " +
            "innfluttum ferðum.",
    HelpKey.SpeciesImages to
        "Allar sveppamyndir í forritinu eru aðeins til viðmiðunar: þær hjálpa þér að þekkja reitinn, ekki " +
            "sveppinn í skóginum. Greindu ekki óþekkta sveppi eftir þeim.",
    HelpKey.PreparationPurpose to
        "Sækir kortbúta fyrir fram í minni símans svo kortið haldist í skóginum án nettengingar: án þess " +
            "kemur auður bakgrunnur í stað korts þegar samband er ekkert.",
    HelpKey.PreparationDownload to
        "Finndu svæðið sem þú þarft — færðu og aðdregðu kortið — og ýttu svo á kringlótta hnappinn með ör " +
            "niður neðst til hægri. Forritið sýnir hve mikið pláss það sem sést núna tekur: „Sækja þetta " +
            "svæði“ spyr um heiti og hefur niðurhalið, „Hætta við“ skilar kortinu.",
    HelpKey.PreparationRegions to
        "Sótt svæði liggja í rönd neðst. Smellur á spjald flýgur að því svæði á kortinu, og hnapparnir á " +
            "spjaldinu setja niðurhalið í bið og halda því áfram, reyna aftur eftir villu og eyða " +
            "svæðinu.",
    HelpKey.PreparationAreaSize to
        "Sótt er nákvæmlega það sem sést á skjánum, þess vegna breytist stærðarmatið meðan þú færir " +
            "kortið. Því stærra sem svæðið er, því grófara þarf það að vera — betra er að sækja nokkur " +
            "lítil svæði en eitt risastórt. Heiti svæða mega ekki endurtaka sig.",
    HelpKey.PreparationBackground to
        "Niðurhalið gengur í bakgrunni og rofnar ekki þótt þú farir af skjánum, og í bið varðveitist " +
            "framvindan. „Uppfæra kortagögn“ í „Stillingum“ sækir öll vistuð svæði upp á nýtt.",
    HelpKey.DataPurpose to
        "Flutningur ferða milli síma og öryggisafrit: valdar ferðir eru fluttar út í eina safnskrá, og " +
            "slíka skrá má hlaða inn aftur — í þetta tæki eða annað.",
    HelpKey.DataExport to
        "Rofinn efst velur „Útflutningur“ eða „Innflutningur“. Í „Útflutningi“ gefurðu safnskránni heiti, " +
            "ýtir á línuna fyrir val ferða, hakar við þær sem þú vilt og ýtir á „Lokið“ — síminn spyr " +
            "hvar eigi að vista skrána.",
    HelpKey.DataImport to
        "Í „Innflutningi“ ýtirðu á „Velja skrá“, skrifar ef vill viðbót sem bætist við heiti ferðanna sem " +
            "hlaðið er inn, og ýtir á „Lokið“; þegar safnskráin hefur verið lesin birtist hnappurinn „Í " +
            "safnið“. Hnappurinn „Hætta við“ hreinsar innsláttinn án þess að vista neitt.",
    HelpKey.DataArchiveContents to
        "Í safnskrána fara slóðin, fundirnir, merktu staðirnir, myndirnar og þær sveppategundir sem eru " +
            "ekki í skránni — í hinu tækinu birtast þær undir „Sveppir sem bætt var við“ með merkingunni " +
            "„úr safnskrá“.",
    HelpKey.DataDuplicates to
        "Innflutningur bætir ferðum alltaf við þær sem fyrir eru og skiptir engu út, svo sama skrá hlaðin " +
            "inn aftur býr þær til á ný: viðbótin við heitin hjálpar þér að greina þær síðar. Í lokin " +
            "birtist hve margar ferðir hlóðust inn og hve margar tókst ekki að lesa.",
    HelpKey.SettingsPurpose to
        "Almennar stillingar forritsins: tungumál viðmótsins, útlit, útlit og röð sveppareitanna á " +
            "upptökuskjánum og viðhald kortsins.",
    HelpKey.SettingsLanguage to
        "Línan „Tungumál viðmótsins“ opnar lista yfir tungumál: smellur velur tungumál, hakið efst " +
            "staðfestir valið, örin fer út án þess að breyta neinu. Tungumálið tekur strax gildi í öllu " +
            "forritinu, ekki þarf að endurræsa.",
    HelpKey.SettingsTheme to
        "„Útlit“ skiptir milli ljóss og dökks þema forritsins. „Kerfisstilling“ lætur símann ráða: " +
            "forritið dökknar og lýsist með honum.",
    HelpKey.SettingsMushroomSize to
        "Sleðinn ræður stærð sveppatáknanna á kortinu — bæði á upptökuskjánum og á heildar-„Korti yfir " +
            "fundi“. Myndin fyrir neðan breytist meðan þú dregur, svo stærðin sést áður en þú sleppir.",
    HelpKey.SettingsMushroomOrder to
        "Venjulega færast nýmerktir sveppir fremst í reitaröðina. „Festa röðina“ slekkur alveg á því, og " +
            "„Endurstilla röðina þegar ferð lýkur“ skilar upphaflegu röðinni þegar ferð er lokið.",
    HelpKey.SettingsMapData to
        "„Uppfæra kortagögn“ athugar hvort kortið hafi breyst á þjóninum og sækir þá öll vistuð ónettengd " +
            "svæði upp á nýtt. „Hreinsa skyndiminni kortsins“ fjarlægir aðeins það sem hlóðst inn við " +
            "skoðun — svæðin úr „Sækja fyrir fram“ verða eftir.",
)
