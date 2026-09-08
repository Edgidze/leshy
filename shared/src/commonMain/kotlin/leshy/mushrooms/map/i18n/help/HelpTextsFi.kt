package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Suomi — ohjenäytöt, `.claude/plans/help-screens.md`. */
internal val finnishHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "Sovelluksen päänäyttö: täällä retki tallennetaan. GPS piirtää reittisi, ja jokainen löytö " +
            "tallentuu koordinaatteineen ja kellonaikoineen — muistiin se menee heti, joten retken voi " +
            "keskeyttää milloin tahansa eikä tallennettu katoa.",
    HelpKey.RecordStartFinish to
        "”Aloita” kysyy nimen ja käynnistää tallennuksen; sen jälkeen painike muuttuu muotoon ”Tauko”, ja " +
            "tauolla ilmestyvät ”Jatka” ja ”Lopeta”. ”Lopeta” päättää retken ja siirtää sen " +
            "”Retkiarkistoon”.",
    HelpKey.RecordTiles to
        "Alalaidan sienilaatoilla merkitään löydöt: ”+” merkitsee löydön nykyiseen sijaintiisi, ”−” " +
            "poistaa lajin viimeisen virheellisen merkinnän. Pitkä painallus ”+”-painikkeella avaa " +
            "useamman kappaleen syötön kerralla; samaa sientä voi merkitä yhdellä retkellä enintään 999 " +
            "kappaletta.",
    HelpKey.RecordPlace to
        "Vasemmalla oleva pyöreä painike merkitsee paikan — nimen, kuvauksen ja valokuvan kanssa. Paikka " +
            "asettuu siihen, missä nyt seisot, ja jää kartalle retken jälkeenkin.",
    HelpKey.RecordNavigation to
        "Pitkä painallus paikkamerkinnän päällä käynnistää navigoinnin sinne: oikean yläkulman paneeli " +
            "näyttää suunnan ja etäisyyden kohteeseen. Paneelin rasti sammuttaa navigoinnin.",
    HelpKey.RecordSearchAndOwn to
        "Oikean reunan suurennuslasi etsii sienen nimellä ja siirtää sen laatan rivin alkuun — se on " +
            "nopeampaa, kun lajeja on valittuna paljon. Rivin viimeinen laatta, jossa on plus, lisää oman " +
            "lajin, jota luettelossa ei ole.",
    HelpKey.RecordFilters to
        "Vasemman ylälaidan painike ”Suodattimet” määrää, minkä lajien ja miltä ajalta löydöt näkyvät " +
            "kartalla, ja painikkeen luku kertoo, montako suodatinta on nyt päällä. Suodatin on yhteinen " +
            "”Löytöjen kartan” kanssa: täällä valittu pätee siellä.",
    HelpKey.RecordBackground to
        "Reitin tallennus jatkuu, vaikka sovellus olisi taustalla. Androidissa käynnissä oleva " +
            "retki näkyy myös ilmoituksena, jossa on ”+”/”−”-painikkeet — löydön voi merkitä " +
            "puhelinta avaamatta. Nykyisen retken lisäksi kartta näyttää aiempien retkien löydöt ja " +
            "merkityt paikat — niistä näkee, missä olet jo kulkenut ja mitä siellä oli.",
    HelpKey.ArchivePurpose to
        "Kaikki retkesi, uusimmat ylimpänä. Kortissa ovat nimi, päivämäärä, kesto, kilometrit, löytöjen " +
            "määrä ja pienoiskuva kuljetusta reitistä.",
    HelpKey.ArchiveDetail to
        "Kortin painallus avaa koko retken: tilastot, löydöt lajeittain, merkityt paikat, kuvaus ja " +
            "painike ”Katso kartta”. Nimen ja kuvauksen voi muuttaa samassa paikassa.",
    HelpKey.ArchiveShare to
        "Painike ”Jaa” kokoaa kuvan niistä retken osista, jotka rastitat. Ennen kuin lähetät retken " +
            "kartan, muista: siitä näkee tarkalleen, mistä löysit sienet.",
    HelpKey.ArchiveSelection to
        "Pitkä painallus kortin päällä käynnistää valintatilan: merkitse haluamasi retket painamalla ja " +
            "paina ”Poista retket”; painike ”Takaisin” poistuu tästä tilasta. Poisto on peruuttamaton — " +
            "retken mukana katoavat sen reitti, löydöt, merkityt paikat ja valokuvat.",
    HelpKey.ArchiveUnfinished to
        "Kesken jäänyt retki näkyy myös listassa: lopetusajan tilalla lukee ”kesken”. Tällaisella " +
            "retkellä ei ole vielä kestoa, joten se ei kerry ”Löytöjen kartan” kokonaisaikaan.",
    HelpKey.MapPurpose to
        "Koontikartta: kaikkien retkiesi löydöt, reitit ja merkityt paikat yhdellä pohjalla kerralla. Sen " +
            "avulla näet kokonaiskuvan — missä sienipaikkasi ovat ja miten ne muuttuvat vuodesta toiseen.",
    HelpKey.MapFullScreen to
        "Ylhäällä on kartta kaikista löydöistä kerralla; painallus avaa kartan koko näytölle. Kun löytöjä " +
            "on paljon, lähekkäiset merkinnät kerääntyvät ympyräksi, jossa on luku — lähennä karttaa, " +
            "niin se hajoaa yksittäisiksi sieniksi. Sienikuvakkeiden koko säädetään ”Asetuksissa”.",
    HelpKey.MapSliders to
        "Kartan alla on kaksi liukusäädintä — päivämääräväli ja kausi eli kuukausiväli — ja kaikki niiden " +
            "alapuolella lasketaan valinnan mukaan. Säätimet ilmestyvät vasta, kun retkiä on useammalta " +
            "kuin yhdeltä päivältä.",
    HelpKey.MapStats to
        "Säätimien alla: montako retkeä, kilometriä, tuntia ja löytöä kertyi, lajikohtaiset laatat ja " +
            "ympyrädiagrammi. Kokonaisaika lasketaan päättyneistä retkistä: keskeneräisellä ei ole vielä " +
            "kestoa.",
    HelpKey.MapFilters to
        "Painike ”Suodattimet” asuu koko näytön kartalla, sen vasemmassa ylälaidassa: siellä ovat samat " +
            "kaksi akselia, lajilista ja aiempien reittien näyttökytkin. Painikkeen luku kertoo, montako " +
            "suodatinta on päällä; suodatin on yhteinen tallennusnäytön kanssa.",
    HelpKey.MapPlaces to
        "Paikkamerkinnän painallus avaa sen kortin valokuvineen ja kuvauksineen. Samasta paikasta paikkaa " +
            "voi myös muuttaa tai poistaa sen.",
    HelpKey.SpeciesPurpose to
        "Täällä päätät, mitkä sienet näkyvät laattoina tallennusnäytöllä. Luettelo on jaettu " +
            "maakohtaisiin kokoelmiin, ja sen vieressä elävät lajit, joita luettelossa ei ole — ne lisäät " +
            "itse.",
    HelpKey.SpeciesCollections to
        "Kohdassa ”Sienikokoelmat” maan rivin painallus avaa sen lajit: maan vieressä oleva rasti " +
            "kytkee koko kokoelman, sisällä olevat rastit yksittäiset lajit. Ylälaidan hakukenttä " +
            "löytää nimellä sekä maan että yksittäisen sienen.",
    HelpKey.SpeciesOwn to
        "Kohdassa ”Lisätyt sienet” painike ”Lisää sieni” avaa lomakkeen: nimi, tieteellinen nimi, " +
            "merkin väri ja kuva — kamerasta, galleriasta tai luettelosta. Sitten sovellus kysyy " +
            "”Mihin kokoelmaan?”: oma nimi kokoaa tällaiset sienet yhteen, tyhjä kenttä vie ne " +
            "kokoelmaan ”Muut”. Kynä muokkaa jo lisättyä lajia, rasti poistaa sen.",
    HelpKey.SpeciesCheckboxes to
        "Rastin poistaminen ei poista mitään — laji vain lakkaa näkymästä laattana, ja aiemmat löydöt " +
            "jäävät paikoilleen. Oman lajin poistaminen sen sijaan on peruuttamaton: kaikki sen merkinnät " +
            "aiemmilla retkillä siirtyvät kohtaan ”Tuntematon sieni”. Lajit, joissa lukee ”arkistosta”, " +
            "tulivat tuotujen retkien mukana.",
    HelpKey.SpeciesImages to
        "Kaikki sovelluksen sienikuvat ovat viitteellisiä: ne auttavat tunnistamaan laatan, eivät sientä " +
            "metsässä. Älä määritä niiden perusteella tuntemattomia sieniä.",
    HelpKey.PreparationPurpose to
        "Lataa kartan paloja etukäteen puhelimen muistiin, jotta kartta pysyy metsässä ilman nettiä " +
            "paikallaan: ilman tätä kuuluvuuden ulkopuolella kartan tilalla on tyhjä tausta.",
    HelpKey.PreparationDownload to
        "Etsi haluamasi alue — siirrä ja zoomaa karttaa — ja paina sitten oikean alalaidan pyöreää " +
            "painiketta, jossa on alaspäin osoittava nuoli. Sovellus näyttää, paljonko tilaa näytöllä " +
            "oleva alue vie: ”Lataa tämä alue” kysyy nimen ja aloittaa latauksen, ”Peruuta” palauttaa " +
            "kartan.",
    HelpKey.PreparationRegions to
        "Ladatut alueet ovat nauhana alalaidassa. Kortin painallus lentää kartalla kyseiselle alueelle, " +
            "ja kortin painikkeet keskeyttävät latauksen ja jatkavat sitä, yrittävät uudelleen virheen " +
            "jälkeen ja poistavat alueen.",
    HelpKey.PreparationAreaSize to
        "Ladattavaksi menee tarkalleen se, mikä näkyy näytöllä, siksi kokoarvio muuttuu karttaa " +
            "liikuteltaessa. Mitä suurempi alue, sitä karkeammaksi se on tehtävä — kannattaa ladata " +
            "useampi pieni alue kuin yksi valtava. Alueiden nimet eivät saa toistua.",
    HelpKey.PreparationBackground to
        "Lataus etenee taustalla eikä keskeydy, vaikka poistuisit näytöltä, ja tauolla edistyminen " +
            "säilyy. ”Päivitä karttatiedot” ”Asetuksissa” lataa kaikki tallennetut alueet uudelleen.",
    HelpKey.DataPurpose to
        "Retkien siirto puhelimesta toiseen ja varmuuskopio: valitut retket viedään yhteen " +
            "arkistotiedostoon, ja sellaisen tiedoston voi ladata takaisin — tähän tai toiseen " +
            "laitteeseen.",
    HelpKey.DataExport to
        "Ylälaidan kytkin valitsee ”Vienti” tai ”Tuonti”. Kohdassa ”Vienti” anna arkistolle nimi, paina " +
            "retkien valintariviä ja rastita haluamasi, sitten ”Valmis” — puhelin kysyy, mihin tiedosto " +
            "tallennetaan.",
    HelpKey.DataImport to
        "Kohdassa ”Tuonti” paina ”Valitse tiedosto”, kirjoita halutessasi lisäys, joka liitetään " +
            "tuotavien retkien nimiin, ja paina ”Valmis”; kun arkisto on luettu, ilmestyy painike " +
            "”Arkistoon”. Painike ”Peruuta” tyhjentää syötetyn tallentamatta mitään.",
    HelpKey.DataArchiveContents to
        "Arkistoon menevät reitti, löydöt, merkityt paikat, valokuvat ja ne sienilajit, joita luettelossa " +
            "ei ole — toisessa laitteessa ne ilmestyvät kohtaan ”Lisätyt sienet” merkinnällä " +
            "”arkistosta”.",
    HelpKey.DataDuplicates to
        "Tuonti lisää retket aina olemassa olevien rinnalle eikä korvaa mitään, joten saman tiedoston " +
            "lataaminen uudelleen luo ne toistamiseen: nimien lisäys auttaa erottamaan erät jälkikäteen. " +
            "Lopuksi näytetään, montako retkeä latautui ja montaako ei voitu lukea.",
    HelpKey.SettingsPurpose to
        "Sovelluksen yleiset asetukset: käyttöliittymän kieli, ulkoasu, sienilaattojen ulkoasu ja " +
            "järjestys tallennusnäytöllä sekä kartan ylläpito.",
    HelpKey.SettingsLanguage to
        "Rivi ”Käyttöliittymän kieli” avaa kielilistan: painallus valitsee kielen, ylälaidan rasti " +
            "vahvistaa valinnan, nuoli poistuu muuttamatta mitään. Kieli vaihtuu heti koko sovelluksessa, " +
            "uudelleenkäynnistystä ei tarvita.",
    HelpKey.SettingsTheme to
        "”Ulkoasu” vaihtaa sovelluksen vaalean ja tumman teeman välillä. ”Järjestelmä” jättää valinnan " +
            "puhelimelle: sovellus tummuu ja vaalenee sen mukana.",
    HelpKey.SettingsMushroomSize to
        "Liukusäädin asettaa sienikuvakkeiden koon kartalla — sekä tallennusnäytöllä että " +
            "koonti-”Löytöjen kartalla”. Säätimen alla oleva kuva muuttuu jo vetäessä, joten koon näkee " +
            "ennen kuin irrottaa otteen.",
    HelpKey.SettingsMushroomOrder to
        "Yleensä juuri merkityt sienet nousevat laattarivin alkuun. ”Kiinteä sienten järjestys” poistaa " +
            "tämän kokonaan käytöstä, ja ”Palauta sienten järjestys retken päätteeksi” palauttaa " +
            "alkuperäisen järjestyksen, kun retki on päättynyt.",
    HelpKey.SettingsMapData to
        "”Päivitä karttatiedot” tarkistaa, onko kartta muuttunut palvelimella, ja jos on, lataa kaikki " +
            "tallennetut offline-alueet uudelleen. ”Tyhjennä kartan välimuisti” poistaa vain sen, mikä " +
            "latautui selatessa — ”Esilatauksen” alueet säilyvät.",
)
