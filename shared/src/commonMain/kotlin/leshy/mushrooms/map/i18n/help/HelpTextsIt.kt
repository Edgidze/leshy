package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Italiano — schermate di aiuto, `.claude/plans/help-screens.md`. */
internal val italianHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "La schermata principale dell'app: qui viene registrata l'uscita. Il GPS traccia il tuo percorso " +
            "e ogni ritrovamento viene salvato con coordinate e ora — subito, così l'uscita può essere " +
            "interrotta in qualsiasi momento senza perdere ciò che è già registrato.",
    HelpKey.RecordStartFinish to
        "«Avvia» chiede un nome e fa partire la registrazione; poi il pulsante diventa «Pausa» e in pausa " +
            "compaiono «Riprendi» e «Termina». «Termina» chiude l'uscita e la sposta nell'«Archivio " +
            "uscite».",
    HelpKey.RecordTiles to
        "Le caselle dei funghi in basso servono a segnare i ritrovamenti: «+» registra un ritrovamento " +
            "nel punto in cui ti trovi, «−» toglie l'ultima segnalazione errata di quella specie. La " +
            "pressione prolungata su «+» apre l'inserimento di più pezzi insieme; oltre 999 funghi uguali " +
            "per uscita non si possono segnare.",
    HelpKey.RecordPlace to
        "Il pulsante rotondo a sinistra segna un luogo — con nome, descrizione e foto. Il luogo viene " +
            "messo dove ti trovi ora e resta sulla mappa anche dopo l'uscita.",
    HelpKey.RecordNavigation to
        "La pressione prolungata sul segnaposto di un luogo avvia la navigazione verso di esso: il " +
            "pannello in alto a destra mostra direzione e distanza dalla meta. La crocetta sul pannello " +
            "spegne la navigazione.",
    HelpKey.RecordSearchAndOwn to
        "La lente a destra trova un fungo per nome e porta la sua casella all'inizio della fila — è più " +
            "rapido quando sono attive molte specie. L'ultima casella della fila, con il più, aggiunge " +
            "una tua specie che il catalogo non ha.",
    HelpKey.RecordFilters to
        "Il pulsante «Filtri» in alto a sinistra stabilisce quali specie e quale periodo mostrare sulla " +
            "mappa, mentre il numero sopra indica quanti filtri sono attivi. Il filtro è comune con la " +
            "«Mappa dei ritrovamenti»: ciò che attivi qui vale anche là.",
    HelpKey.RecordBackground to
        "La registrazione del percorso prosegue quando l'app è in secondo piano. Su Android " +
            "l'uscita in corso resta anche come notifica con i pulsanti «+»/«−» — un ritrovamento si " +
            "annota senza sbloccare il telefono. Oltre all'uscita in corso, la mappa mostra " +
            "ritrovamenti e luoghi segnati delle uscite passate — da essi si vede dove sei già stato " +
            "e cosa c'era.",
    HelpKey.ArchivePurpose to
        "Tutte le tue uscite, le più recenti in alto. Sulla scheda ci sono nome, data, durata, " +
            "chilometri, numero di ritrovamenti e una miniatura del percorso.",
    HelpKey.ArchiveDetail to
        "Toccando la scheda si apre l'uscita per intero: statistiche, ritrovamenti per specie, luoghi " +
            "segnati, descrizione e il pulsante «Vedi mappa». Nome e descrizione si modificano nello " +
            "stesso posto.",
    HelpKey.ArchiveShare to
        "Il pulsante «Condividi» compone un'immagine con le parti dell'uscita che spunti. Prima di " +
            "inviare la mappa di un'uscita, ricorda: da essa si vede esattamente dove hai trovato i " +
            "funghi.",
    HelpKey.ArchiveSelection to
        "La pressione prolungata su una scheda attiva la modalità di selezione: segna le uscite volute " +
            "con un tocco e premi «Elimina uscite»; il pulsante «Indietro» esce da questa modalità. " +
            "L'eliminazione è definitiva — con l'uscita spariscono percorso, ritrovamenti, luoghi segnati " +
            "e foto.",
    HelpKey.ArchiveUnfinished to
        "Anche un'uscita non conclusa compare nell'elenco: al posto dell'ora di fine c'è scritto «in " +
            "corso». Un'uscita così non ha ancora una durata, perciò non entra nel tempo totale della " +
            "«Mappa dei ritrovamenti».",
    HelpKey.MapPurpose to
        "La mappa d'insieme: ritrovamenti, percorsi e luoghi segnati di tutte le tue uscite insieme su " +
            "un'unica tela. Serve a vedere il quadro generale — dove sono i tuoi posti da funghi e come " +
            "cambiano di anno in anno.",
    HelpKey.MapFullScreen to
        "In alto c'è la mappa con tutti i ritrovamenti insieme; toccandola si apre a schermo intero. " +
            "Quando i ritrovamenti sono tanti, i segni vicini si raccolgono in un cerchietto con un " +
            "numero — avvicina la mappa e si sgrana in singoli funghi. La dimensione delle icone si " +
            "regola nelle «Impostazioni».",
    HelpKey.MapSliders to
        "Sotto la mappa ci sono due cursori — intervallo di date e stagione, cioè un intervallo di mesi — " +
            "e tutto ciò che sta sotto viene calcolato secondo la scelta. I cursori compaiono solo quando " +
            "ci sono uscite di più di un giorno.",
    HelpKey.MapStats to
        "Sotto i cursori: quante uscite, chilometri, tempo e ritrovamenti ci sono stati, le caselle per " +
            "specie e il diagramma a torta. Il tempo totale somma le uscite concluse: quella in corso non " +
            "ha ancora una durata.",
    HelpKey.MapFilters to
        "Il pulsante «Filtri» vive sulla mappa a schermo intero, in alto a sinistra: lì ci sono gli " +
            "stessi due assi, l'elenco delle specie e l'interruttore per mostrare i percorsi passati. Il " +
            "numero sul pulsante dice quanti filtri sono attivi; il filtro è comune con la schermata di " +
            "registrazione.",
    HelpKey.MapPlaces to
        "Toccando il segnaposto di un luogo si apre la sua scheda con foto e descrizione. Da lì il luogo " +
            "si può anche modificare o eliminare.",
    HelpKey.SpeciesPurpose to
        "Qui decidi quali funghi saranno caselle nella schermata di registrazione. Il catalogo è diviso " +
            "in collezioni per paese, e accanto vivono le specie che il catalogo non ha — quelle le " +
            "aggiungi tu.",
    HelpKey.SpeciesCollections to
        "In «Collezioni di funghi», toccando la riga di un paese si aprono le sue specie: la " +
            "spunta accanto al paese attiva l'intera collezione, le spunte interne le singole specie. " +
            "Il campo di ricerca in alto trova per nome sia un paese sia un singolo fungo.",
    HelpKey.SpeciesOwn to
        "In «Funghi aggiunti» il pulsante «Aggiungi fungo» apre un modulo: nome, nome " +
            "scientifico, colore del segno e immagine — dalla fotocamera, dalla galleria o dal " +
            "catalogo. Poi l'app chiede «In quale collezione?»: un nome tuo raccoglie insieme questi " +
            "funghi, il campo vuoto li mette in «Altri». La matita modifica una specie già aggiunta, " +
            "la crocetta la elimina.",
    HelpKey.SpeciesCheckboxes to
        "Togliere la spunta non cancella nulla — la specie semplicemente smette di comparire come " +
            "casella, e i ritrovamenti passati restano al loro posto. Eliminare una tua specie, invece, è " +
            "definitivo: tutte le sue segnalazioni nelle uscite passate passano a «Fungo sconosciuto». Le " +
            "specie con la dicitura «dall'archivio» sono arrivate con uscite importate.",
    HelpKey.SpeciesImages to
        "Tutte le immagini dei funghi nell'app sono indicative: aiutano a riconoscere la casella, non il " +
            "fungo nel bosco. Non determinare con esse funghi sconosciuti.",
    HelpKey.PreparationPurpose to
        "Scarica in anticipo pezzi di mappa nella memoria del telefono, così nel bosco senza internet la " +
            "mappa resta al suo posto: senza questo, fuori copertura, al posto della mappa c'è uno sfondo " +
            "vuoto.",
    HelpKey.PreparationDownload to
        "Trova l'area che ti serve — sposta e ingrandisci la mappa — poi premi il pulsante rotondo con la " +
            "freccia in giù in basso a destra. L'app mostra quanto spazio occuperà ciò che è ora sullo " +
            "schermo: «Scarica quest'area» chiede un nome e avvia il download, «Annulla» riporta la " +
            "mappa.",
    HelpKey.PreparationRegions to
        "Le aree scaricate stanno in una striscia in basso. Toccando una targhetta si vola a quell'area " +
            "sulla mappa, e i pulsanti sulla targhetta mettono il download in pausa e lo riprendono, " +
            "ritentano dopo un errore ed eliminano l'area.",
    HelpKey.PreparationAreaSize to
        "Viene scaricato esattamente ciò che si vede sullo schermo, perciò la stima delle dimensioni " +
            "cambia mentre sposti la mappa. Più l'area è grande, meno dettagliata deve essere — conviene " +
            "scaricare qualche area piccola invece di una enorme. I nomi delle aree non devono ripetersi.",
    HelpKey.PreparationBackground to
        "Il download procede in secondo piano e non si interrompe se lasci la schermata; in pausa " +
            "l'avanzamento viene conservato. «Aggiorna dati della mappa» nelle «Impostazioni» riscarica " +
            "da capo tutte le aree salvate.",
    HelpKey.DataPurpose to
        "Trasferimento delle uscite tra telefoni e copia di sicurezza: le uscite scelte vengono scritte " +
            "in un unico file di archivio, e un file simile può essere ricaricato — su questo o su un " +
            "altro dispositivo.",
    HelpKey.DataExport to
        "L'interruttore in alto sceglie «Esporta» o «Importa». In «Esporta» dai un nome all'archivio, " +
            "tocca la riga di selezione delle uscite, spunta quelle volute e premi «Fatto» — il telefono " +
            "chiederà dove salvare il file.",
    HelpKey.DataImport to
        "In «Importa» premi «Scegli file», scrivi se vuoi un'aggiunta che verrà accodata ai nomi delle " +
            "uscite caricate e premi «Fatto»; quando l'archivio è stato letto compare il pulsante «Vai " +
            "all'archivio». Il pulsante «Annulla» cancella quanto inserito senza salvare nulla.",
    HelpKey.DataArchiveContents to
        "Nell'archivio finiscono il percorso, i ritrovamenti, i luoghi segnati, le foto e quelle specie " +
            "di funghi che non sono nel catalogo — sull'altro dispositivo compaiono in «Funghi aggiunti» " +
            "con la dicitura «dall'archivio».",
    HelpKey.DataDuplicates to
        "L'importazione aggiunge sempre le uscite accanto a quelle esistenti e non sostituisce nulla, " +
            "perciò ricaricare lo stesso file le crea una seconda volta: l'aggiunta ai nomi aiuta poi a " +
            "distinguere un gruppo dall'altro. Alla fine viene mostrato quante uscite sono state caricate " +
            "e quante non si sono potute leggere.",
    HelpKey.SettingsPurpose to
        "Le opzioni generali dell'app: lingua dell'interfaccia, aspetto, forma e ordine delle caselle dei " +
            "funghi nella schermata di registrazione e manutenzione della mappa.",
    HelpKey.SettingsLanguage to
        "La riga «Lingua dell'interfaccia» apre l'elenco delle lingue: il tocco ne sceglie una, la spunta " +
            "in alto conferma la scelta, la freccia esce senza cambiare nulla. La lingua vale subito in " +
            "tutta l'app, non serve riavviare.",
    HelpKey.SettingsTheme to
        "«Aspetto» commuta tra il tema chiaro e quello scuro dell'app. «Sistema» lascia la scelta al " +
            "telefono: l'app si scurisce e si schiarisce insieme a lui.",
    HelpKey.SettingsMushroomSize to
        "Il cursore imposta la dimensione delle icone dei funghi sulla mappa — sia nella schermata di " +
            "registrazione sia nella «Mappa dei ritrovamenti». L'immagine sotto cambia già mentre " +
            "trascini, così la dimensione si vede prima di lasciare.",
    HelpKey.SettingsMushroomOrder to
        "Di solito i funghi appena segnati salgono all'inizio della fila di caselle. «Blocca l'ordine dei " +
            "funghi» lo disattiva del tutto, mentre «Ripristina l'ordine dei funghi al termine " +
            "dell'uscita» riporta l'ordine iniziale quando l'uscita è conclusa.",
    HelpKey.SettingsMapData to
        "«Aggiorna dati della mappa» controlla se la mappa è cambiata sul server e, in tal caso, " +
            "riscarica tutte le aree offline salvate. «Svuota cache della mappa» rimuove solo ciò che si " +
            "è caricato durante la consultazione — le aree del «Precaricamento» restano al loro posto.",
)
