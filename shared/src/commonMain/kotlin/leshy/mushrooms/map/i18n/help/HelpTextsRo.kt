package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Română — ecrane de ajutor, `.claude/plans/help-screens.md`. */
internal val romanianHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "Ecranul principal al aplicației: aici se înregistrează plimbarea. Prin GPS se scrie traseul " +
            "dumneavoastră, iar fiecare descoperire se salvează cu coordonate și oră — și ajunge imediat " +
            "în memorie, așa că plimbarea poate fi întreruptă oricând, iar ce s-a înregistrat nu se " +
            "pierde.",
    HelpKey.RecordStartFinish to
        "„Start” cere un nume și pornește înregistrarea; apoi butonul devine „Pauză”, iar în pauză apar " +
            "„Continuă” și „Încheie”. „Încheie” închide plimbarea și o mută în „Arhivă plimbări”.",
    HelpKey.RecordTiles to
        "Plăcile cu ciuperci de jos sunt cele cu care se marchează descoperirile: „+” marchează o " +
            "descoperire în punctul dumneavoastră curent, „−” șterge ultima marcare greșită a acelei " +
            "specii. Apăsarea lungă pe „+” deschide introducerea mai multor bucăți deodată; mai mult de " +
            "999 de ciuperci identice într-o plimbare nu se pot marca.",
    HelpKey.RecordPlace to
        "Butonul rotund din stânga marchează un loc — cu nume, descriere și fotografie. Locul se pune " +
            "acolo unde vă aflați acum și rămâne pe hartă și după plimbare.",
    HelpKey.RecordNavigation to
        "Apăsarea lungă pe marcajul unui loc pornește navigarea până la el: panoul din dreapta sus arată " +
            "direcția și distanța până la țintă. Crucea de pe panou oprește navigarea.",
    HelpKey.RecordSearchAndOwn to
        "Lupa din dreapta găsește ciuperca după nume și îi mută placa la începutul benzii — așa e mai " +
            "rapid când sunt activate multe specii. Ultima placă a benzii, cea cu plus, adaugă o specie " +
            "proprie, care nu există în catalog.",
    HelpKey.RecordFilters to
        "Butonul „Filtre” din stânga sus stabilește ce specii și din ce perioadă să apară pe hartă, iar " +
            "numărul de pe el arată câte filtre sunt active acum. Filtrul este comun cu „Harta " +
            "descoperirilor”: ce activați aici se aplică și acolo.",
    HelpKey.RecordBackground to
        "Înregistrarea traseului continuă și când aplicația este în fundal. Pe Android plimbarea " +
            "în curs stă și ca notificare cu butoane „+”/„−” — o descoperire poate fi notată fără a " +
            "debloca telefonul. Pe lângă plimbarea curentă, harta arată descoperirile și locurile " +
            "marcate din plimbările trecute — din ele se vede pe unde ați umblat deja și ce a fost " +
            "acolo.",
    HelpKey.ArchivePurpose to
        "Toate plimbările dumneavoastră, cele noi sus. Pe fișă sunt numele, data, durata, kilometrii, " +
            "numărul de descoperiri și o miniatură a traseului parcurs.",
    HelpKey.ArchiveDetail to
        "Apăsarea pe fișă deschide plimbarea în întregime: statistici, descoperiri pe specii, locuri " +
            "marcate, descriere și butonul „Vezi harta”. Numele și descrierea se pot modifica tot acolo.",
    HelpKey.ArchiveShare to
        "Butonul „Distribuie” compune o imagine din acele părți ale plimbării pe care le bifați. Înainte " +
            "să trimiteți harta plimbării, țineți minte: din ea se vede exact unde ați găsit ciupercile.",
    HelpKey.ArchiveSelection to
        "Apăsarea lungă pe fișă pornește modul de selecție: marcați plimbările dorite prin apăsare și " +
            "apăsați „Șterge plimbările”, iar butonul „Înapoi” iese din acest mod. Ștergerea este " +
            "ireversibilă — odată cu plimbarea dispar traseul, descoperirile, locurile marcate și " +
            "fotografiile ei.",
    HelpKey.ArchiveUnfinished to
        "Plimbarea neîncheiată se vede și ea în listă: în locul orei de final scrie „în desfășurare”. O " +
            "astfel de plimbare încă nu are durată, de aceea nu intră în timpul total din „Harta " +
            "descoperirilor”.",
    HelpKey.MapPurpose to
        "Harta de ansamblu: descoperirile, traseele și locurile marcate ale tuturor plimbărilor " +
            "dumneavoastră deodată, pe o singură pânză. Este acolo ca să vedeți imaginea de ansamblu — " +
            "unde vă sunt locurile cu ciuperci și cum se schimbă de la un an la altul.",
    HelpKey.MapFullScreen to
        "Sus este harta cu toate descoperirile deodată; apăsarea pe ea deschide harta pe tot ecranul. " +
            "Când sunt multe descoperiri, marcajele apropiate se adună într-un cerculeț cu număr — " +
            "apropiați harta și se va desface în ciuperci separate. Mărimea pictogramelor se reglează în " +
            "„Setări”.",
    HelpKey.MapSliders to
        "Sub hartă sunt două cursoare — intervalul de date și sezonul, adică intervalul de luni — iar tot " +
            "ce se află sub ele se calculează după ce ați ales. Cursoarele apar doar când aveți plimbări " +
            "din mai mult de o zi.",
    HelpKey.MapStats to
        "Sub cursoare: câte plimbări, câți kilometri, cât timp și câte descoperiri au fost, plăcile pe " +
            "specii și diagrama circulară. Timpul total se adună din plimbările încheiate: cea " +
            "neîncheiată încă nu are durată.",
    HelpKey.MapFilters to
        "Butonul „Filtre” stă pe harta pe tot ecranul, în stânga sus: acolo sunt aceleași două axe, lista " +
            "speciilor și comutatorul pentru afișarea traseelor trecute. Numărul de pe buton arată câte " +
            "filtre sunt active; filtrul este comun cu ecranul de înregistrare.",
    HelpKey.MapPlaces to
        "Apăsarea pe marcajul unui loc deschide fișa acestuia cu fotografie și descriere. Tot de acolo " +
            "locul poate fi modificat sau șters.",
    HelpKey.SpeciesPurpose to
        "Aici hotărâți care ciuperci vor fi plăci pe ecranul de înregistrare. Catalogul este împărțit în " +
            "colecții pe țări, iar alături trăiesc speciile care nu există în catalog — pe acelea le " +
            "adăugați dumneavoastră.",
    HelpKey.SpeciesCollections to
        "În „Colecții de ciuperci”, apăsarea pe rândul unei țări îi desface speciile: bifa de " +
            "lângă țară activează întreaga colecție, bifele dinăuntru speciile individuale. Câmpul de " +
            "căutare de sus găsește după nume atât o țară, cât și o ciupercă anume.",
    HelpKey.SpeciesOwn to
        "În „Ciuperci adăugate”, butonul „Adaugă ciupercă” deschide un formular: nume, nume " +
            "științific, culoarea marcajului și o imagine — de la cameră, din galerie sau din " +
            "catalog. Apoi aplicația întreabă „În ce colecție?”: un nume propriu adună astfel de " +
            "ciuperci laolaltă, câmpul gol le pune în „Altele”. Creionul modifică o specie deja " +
            "adăugată, crucea o șterge.",
    HelpKey.SpeciesCheckboxes to
        "Bifa scoasă nu șterge nimic — specia pur și simplu nu mai apare ca placă, iar descoperirile " +
            "trecute rămân la locul lor. Ștergerea unei specii proprii este, dimpotrivă, ireversibilă: " +
            "toate marcările ei din plimbările trecute trec la „Ciupercă necunoscută”. Speciile cu " +
            "eticheta „din arhivă” au venit odată cu plimbările importate.",
    HelpKey.SpeciesImages to
        "Toate imaginile de ciuperci din aplicație sunt orientative: ele ajută la recunoașterea plăcii, " +
            "nu a ciupercii din pădure. Nu determinați după ele ciuperci necunoscute.",
    HelpKey.PreparationPurpose to
        "Descarcă din timp bucăți de hartă în memoria telefonului, ca harta să rămână la locul ei în " +
            "pădure fără internet: fără asta, departe de semnal, în locul hărții va fi un fundal gol.",
    HelpKey.PreparationDownload to
        "Găsiți zona dorită — mutați și măriți harta — apoi apăsați butonul rotund cu săgeată în jos din " +
            "dreapta jos. Aplicația arată cât spațiu va ocupa ce se vede acum pe ecran: „Descarcă această " +
            "zonă” cere un nume și pornește descărcarea, „Anulează” readuce harta.",
    HelpKey.PreparationRegions to
        "Zonele descărcate stau într-o bandă jos. Apăsarea pe plăcuță zboară la acea zonă pe hartă, iar " +
            "butoanele de pe plăcuță pun descărcarea pe pauză și o reiau, repetă încercarea după o eroare " +
            "și șterg zona.",
    HelpKey.PreparationAreaSize to
        "Se descarcă exact ce se vede pe ecran, de aceea estimarea dimensiunii se schimbă în timp ce " +
            "mutați harta. Cu cât zona e mai mare, cu atât mai puțin detaliată trebuie făcută — e mai " +
            "avantajos să descărcați câteva zone mici decât una uriașă. Numele zonelor nu trebuie să se " +
            "repete.",
    HelpKey.PreparationBackground to
        "Descărcarea merge în fundal și nu se întrerupe dacă părăsiți ecranul, iar în pauză progresul se " +
            "păstrează. „Actualizează datele hărții” din „Setări” descarcă din nou toate zonele salvate.",
    HelpKey.DataPurpose to
        "Mutarea plimbărilor între telefoane și copie de rezervă: plimbările alese se exportă într-un " +
            "singur fișier arhivă, iar un astfel de fișier poate fi încărcat înapoi — pe acest dispozitiv " +
            "sau pe altul.",
    HelpKey.DataExport to
        "Comutatorul de sus alege „Export” sau „Import”. În „Export” dați un nume arhivei, apăsați rândul " +
            "de alegere a plimbărilor și bifați-le pe cele dorite, apoi „Gata” — telefonul vă va întreba " +
            "unde să salveze fișierul.",
    HelpKey.DataImport to
        "În „Import” apăsați „Alege fișierul”, scrieți dacă doriți un adaos care se va lipi la numele " +
            "plimbărilor încărcate, apoi apăsați „Gata”; când arhiva este citită, apare butonul „Spre " +
            "arhivă”. Butonul „Anulează” golește ce ați introdus, fără să salveze nimic.",
    HelpKey.DataArchiveContents to
        "În arhivă intră traseul, descoperirile, locurile marcate, fotografiile și acele specii de " +
            "ciuperci care nu sunt în catalog — pe celălalt dispozitiv ele apar în „Ciuperci adăugate” cu " +
            "eticheta „din arhivă”.",
    HelpKey.DataDuplicates to
        "Importul adaugă întotdeauna plimbările lângă cele existente și nu înlocuiește nimic, așa că " +
            "încărcarea aceluiași fișier a doua oară le va crea din nou: adaosul la nume ajută să le " +
            "deosebiți după aceea. La final se arată câte plimbări s-au încărcat și câte nu au putut fi " +
            "citite.",
    HelpKey.SettingsPurpose to
        "Setările generale ale aplicației: limba interfeței, aspectul, înfățișarea și ordinea plăcilor cu " +
            "ciuperci pe ecranul de înregistrare și întreținerea hărții.",
    HelpKey.SettingsLanguage to
        "Rândul „Limba interfeței” deschide lista limbilor: apăsarea alege limba, bifa de sus confirmă " +
            "alegerea, săgeata iese fără să schimbe nimic. Limba se aplică imediat în toată aplicația, " +
            "repornirea nu este necesară.",
    HelpKey.SettingsTheme to
        "„Aspect” comută între tema luminoasă și cea întunecată a aplicației. „Sistem” lasă alegerea pe " +
            "seama telefonului: aplicația se întunecă și se luminează odată cu el.",
    HelpKey.SettingsMushroomSize to
        "Cursorul stabilește mărimea pictogramelor cu ciuperci pe hartă — atât pe ecranul de " +
            "înregistrare, cât și pe „Harta descoperirilor”. Imaginea de sub cursor se schimbă chiar în " +
            "timp ce trageți, așa că vedeți mărimea înainte de a da drumul.",
    HelpKey.SettingsMushroomOrder to
        "De obicei, ciupercile tocmai marcate urcă la începutul benzii de plăci. „Blocați ordinea " +
            "ciupercilor” dezactivează complet acest lucru, iar „Resetați ordinea ciupercilor la " +
            "sfârșitul plimbării” readuce ordinea inițială când plimbarea s-a încheiat.",
    HelpKey.SettingsMapData to
        "„Actualizează datele hărții” verifică dacă harta s-a schimbat pe server și, dacă da, descarcă " +
            "din nou toate zonele offline salvate. „Golește memoria cache a hărții” șterge doar ce s-a " +
            "încărcat în timpul navigării — zonele din „Preîncărcare” rămân la locul lor.",
)
