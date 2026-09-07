package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Deutsch — Hilfebildschirme, `.claude/plans/help-screens.md`. */
internal val germanHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "Der Hauptbildschirm der App: Hier wird der Spaziergang aufgezeichnet. Per GPS entsteht Ihre " +
            "Strecke, und jeder Fund wird mit Koordinaten und Uhrzeit gespeichert — sofort, sodass der " +
            "Spaziergang jederzeit abbrechen kann, ohne dass Aufgezeichnetes verloren geht.",
    HelpKey.RecordStartFinish to
        "„Start“ fragt nach einem Namen und beginnt die Aufzeichnung; danach wird die Taste zu „Pause“, " +
            "und in der Pause erscheinen „Fortsetzen“ und „Beenden“. „Beenden“ schließt den Spaziergang " +
            "ab und legt ihn ins „Archiv“.",
    HelpKey.RecordTiles to
        "Mit den Pilzkacheln unten werden Funde markiert: „+“ setzt einen Fund an Ihrer aktuellen " +
            "Position, „−“ entfernt die letzte falsche Markierung dieser Art. Langes Drücken auf „+“ " +
            "öffnet die Eingabe mehrerer Stück auf einmal; mehr als 999 gleiche Pilze pro Spaziergang " +
            "lassen sich nicht markieren.",
    HelpKey.RecordPlace to
        "Die runde Taste links markiert einen Ort — mit Namen, Beschreibung und Foto. Der Ort entsteht " +
            "dort, wo Sie gerade stehen, und bleibt auch nach dem Spaziergang auf der Karte.",
    HelpKey.RecordNavigation to
        "Langes Drücken auf eine Ortsmarkierung startet die Navigation dorthin: Die Leiste oben rechts " +
            "zeigt Richtung und Entfernung zum Ziel. Das Kreuz auf der Leiste beendet die Navigation.",
    HelpKey.RecordSearchAndOwn to
        "Die Lupe rechts findet einen Pilz über den Namen und schiebt seine Kachel an den Anfang der " +
            "Reihe — das ist schneller, wenn viele Arten aktiviert sind. Die letzte Kachel der Reihe, mit " +
            "dem Plus, fügt eine eigene Art hinzu, die der Katalog nicht kennt.",
    HelpKey.RecordFilters to
        "Die Taste „Filter“ oben links legt fest, Funde welcher Arten und aus welchem Zeitraum auf der " +
            "Karte erscheinen; die Zahl darauf sagt, wie viele Filter gerade aktiv sind. Der Filter gilt " +
            "gemeinsam mit der „Fundkarte“: Was Sie hier einschalten, wirkt auch dort.",
    HelpKey.RecordBackground to
        "Die Streckenaufzeichnung läuft weiter, wenn die App im Hintergrund ist. Neben dem laufenden " +
            "Spaziergang zeigt die Karte Funde und markierte Orte früherer Spaziergänge — daran sehen " +
            "Sie, wo Sie schon waren und was es dort gab.",
    HelpKey.ArchivePurpose to
        "Alle Ihre Spaziergänge, die neuesten oben. Auf der Karte stehen Name, Datum, Dauer, Kilometer, " +
            "Anzahl der Funde und eine Miniatur der gelaufenen Strecke.",
    HelpKey.ArchiveDetail to
        "Ein Tippen auf die Karte öffnet den ganzen Spaziergang: Statistik, Funde nach Arten, markierte " +
            "Orte, Beschreibung und die Taste „Karte ansehen“. Name und Beschreibung lassen sich an " +
            "derselben Stelle ändern.",
    HelpKey.ArchiveShare to
        "Die Taste „Teilen“ baut ein Bild aus den Teilen des Spaziergangs, die Sie ankreuzen. Bevor Sie " +
            "die Karte eines Spaziergangs verschicken, denken Sie daran: Man sieht darauf genau, wo Sie " +
            "die Pilze gefunden haben.",
    HelpKey.ArchiveSelection to
        "Langes Drücken auf eine Karte schaltet den Auswahlmodus ein: Markieren Sie die gewünschten " +
            "Spaziergänge durch Antippen und drücken Sie „Spaziergänge löschen“; die Taste „Zurück“ " +
            "verlässt diesen Modus. Das Löschen ist endgültig — mit dem Spaziergang verschwinden Strecke, " +
            "Funde, markierte Orte und Fotos.",
    HelpKey.ArchiveUnfinished to
        "Ein nicht beendeter Spaziergang steht ebenfalls in der Liste: statt der Endzeit steht bei ihm " +
            "„läuft noch“. Er hat noch keine Dauer und zählt deshalb nicht zur Gesamtzeit auf der " +
            "„Fundkarte“.",
    HelpKey.MapPurpose to
        "Die Gesamtkarte: Funde, Strecken und markierte Orte aller Ihrer Spaziergänge auf einmal auf " +
            "einer Fläche. Sie zeigt das große Bild — wo Ihre Pilzstellen liegen und wie sie sich von " +
            "Jahr zu Jahr verändern.",
    HelpKey.MapFullScreen to
        "Oben liegt die Karte mit allen Funden auf einmal; ein Tippen öffnet sie über den ganzen " +
            "Bildschirm. Bei vielen Funden fassen sich nahe Markierungen zu einem Kreis mit Zahl zusammen " +
            "— zoomen Sie hinein, und er zerfällt in einzelne Pilze. Die Größe der Pilzsymbole stellen " +
            "Sie in den „Einstellungen“ ein.",
    HelpKey.MapSliders to
        "Unter der Karte liegen zwei Schieberegler — Zeitraum und Saison, also ein Monatsbereich — und " +
            "alles darunter wird nach Ihrer Auswahl berechnet. Die Regler erscheinen erst, wenn " +
            "Spaziergänge von mehr als einem Tag vorliegen.",
    HelpKey.MapStats to
        "Unter den Reglern: wie viele Spaziergänge, Kilometer, Stunden und Funde es gab, die Kacheln nach " +
            "Arten und das Kreisdiagramm. Die Gesamtzeit summiert abgeschlossene Spaziergänge: Ein " +
            "laufender hat noch keine Dauer.",
    HelpKey.MapFilters to
        "Die Taste „Filter“ sitzt auf der Vollbildkarte, dort oben links: dieselben zwei Achsen, dazu die " +
            "Artenliste und der Schalter für frühere Strecken. Die Zahl auf der Taste nennt die Anzahl " +
            "aktiver Filter; der Filter gilt gemeinsam mit dem Aufzeichnungsbildschirm.",
    HelpKey.MapPlaces to
        "Ein Tippen auf eine Ortsmarkierung öffnet deren Karte mit Foto und Beschreibung. Von dort lässt " +
            "sich der Ort auch ändern oder löschen.",
    HelpKey.SpeciesPurpose to
        "Hier entscheiden Sie, welche Pilze auf dem Aufzeichnungsbildschirm als Kacheln erscheinen. Der " +
            "Katalog ist in Sammlungen nach Ländern geteilt, und daneben leben die Arten, die der Katalog " +
            "nicht kennt — die fügen Sie selbst hinzu.",
    HelpKey.SpeciesCollections to
        "Unter „Pilzsammlungen“ klappt ein Tippen auf die Länderzeile deren Arten auf: Das Häkchen beim " +
            "Land schaltet die ganze Sammlung ein, die Häkchen darin einzelne Arten. Das Suchfeld oben " +
            "findet ein Land über den Namen.",
    HelpKey.SpeciesOwn to
        "Unter „Hinzugefügte Pilze“ öffnet die Taste „Pilz hinzufügen“ ein Formular: Name, " +
            "wissenschaftlicher Name, Markierungsfarbe und Bild — aus der Kamera, aus der Galerie oder " +
            "aus dem Katalog. Der Stift ändert eine bereits hinzugefügte Art, das Kreuz löscht sie.",
    HelpKey.SpeciesCheckboxes to
        "Ein entferntes Häkchen löscht nichts — die Art erscheint einfach nicht mehr als Kachel, frühere " +
            "Funde bleiben, wo sie sind. Das Löschen einer eigenen Art ist dagegen endgültig: Alle ihre " +
            "Markierungen in früheren Spaziergängen wandern zu „Unbekannter Pilz“. Arten mit dem Vermerk " +
            "„aus dem Archiv“ kamen mit importierten Spaziergängen.",
    HelpKey.SpeciesImages to
        "Alle Pilzbilder in der App sind nur schematisch: Sie helfen, die Kachel zu erkennen, nicht den " +
            "Pilz im Wald. Bestimmen Sie damit keine unbekannten Pilze.",
    HelpKey.PreparationPurpose to
        "Lädt Kartenstücke vorab in den Speicher des Telefons, damit die Karte im Wald ohne Internet " +
            "erhalten bleibt: Ohne das erscheint außerhalb der Netzabdeckung statt der Karte ein leerer " +
            "Hintergrund.",
    HelpKey.PreparationDownload to
        "Suchen Sie den gewünschten Bereich — verschieben und zoomen Sie die Karte — und drücken Sie dann " +
            "die runde Taste mit dem Pfeil nach unten rechts unten. Die App zeigt, wie viel Platz das " +
            "aktuell Sichtbare braucht: „Diesen Bereich herunterladen“ fragt nach einem Namen und " +
            "startet, „Abbrechen“ bringt die Karte zurück.",
    HelpKey.PreparationRegions to
        "Die geladenen Bereiche liegen als Leiste unten. Ein Tippen auf eine Kachel fliegt zu diesem " +
            "Bereich auf der Karte, und die Tasten auf der Kachel pausieren den Download und setzen ihn " +
            "fort, wiederholen den Versuch nach einem Fehler und löschen den Bereich.",
    HelpKey.PreparationAreaSize to
        "Geladen wird genau das, was auf dem Bildschirm zu sehen ist; deshalb ändert sich die " +
            "Größenschätzung, während Sie die Karte bewegen. Je größer der Bereich, desto gröber muss er " +
            "ausfallen — mehrere kleine Bereiche lohnen sich mehr als ein riesiger. Die Namen der " +
            "Bereiche dürfen sich nicht wiederholen.",
    HelpKey.PreparationBackground to
        "Der Download läuft im Hintergrund und bricht nicht ab, wenn Sie den Bildschirm verlassen; in der " +
            "Pause bleibt der Fortschritt erhalten. „Kartendaten aktualisieren“ in den „Einstellungen“ " +
            "lädt alle gespeicherten Bereiche neu.",
    HelpKey.DataPurpose to
        "Spaziergänge zwischen Telefonen übertragen und sichern: Die gewählten Spaziergänge werden in " +
            "eine Archivdatei geschrieben, und eine solche Datei lässt sich wieder einlesen — auf diesem " +
            "oder einem anderen Gerät.",
    HelpKey.DataExport to
        "Der Schalter oben wählt „Export“ oder „Import“. Unter „Export“ vergeben Sie einen Archivnamen, " +
            "tippen die Zeile zur Auswahl der Spaziergänge an, kreuzen die gewünschten an und drücken " +
            "„Fertig“ — das Telefon fragt, wohin die Datei gespeichert wird.",
    HelpKey.DataImport to
        "Unter „Import“ drücken Sie „Datei auswählen“, tragen bei Bedarf einen Zusatz ein, der an die " +
            "Namen der geladenen Spaziergänge angehängt wird, und drücken „Fertig“; sobald das Archiv " +
            "gelesen ist, erscheint die Taste „Zum Archiv“. Die Taste „Abbrechen“ verwirft die Eingaben, " +
            "ohne etwas zu speichern.",
    HelpKey.DataArchiveContents to
        "Ins Archiv kommen Strecke, Funde, markierte Orte, Fotos und jene Pilzarten, die der Katalog " +
            "nicht kennt — auf dem anderen Gerät erscheinen sie unter „Hinzugefügte Pilze“ mit dem " +
            "Vermerk „aus dem Archiv“.",
    HelpKey.DataDuplicates to
        "Der Import legt Spaziergänge immer neben die vorhandenen und ersetzt nichts; dieselbe Datei ein " +
            "zweites Mal geladen erzeugt sie also erneut: Der Zusatz im Namen hilft, die Stapel später " +
            "auseinanderzuhalten. Am Ende steht, wie viele Spaziergänge geladen wurden und wie viele " +
            "nicht lesbar waren.",
    HelpKey.SettingsPurpose to
        "Die allgemeinen Optionen der App: Oberflächensprache, Erscheinungsbild, Aussehen und Reihenfolge " +
            "der Pilzkacheln auf dem Aufzeichnungsbildschirm sowie Kartenpflege.",
    HelpKey.SettingsLanguage to
        "Die Zeile „Oberflächensprache“ öffnet die Sprachliste: Antippen wählt eine Sprache, das Häkchen " +
            "oben bestätigt, der Pfeil verlässt sie ohne Änderung. Die Sprache gilt sofort in der ganzen " +
            "App, ein Neustart ist nicht nötig.",
    HelpKey.SettingsTheme to
        "„Erscheinungsbild“ schaltet zwischen hellem und dunklem Design der App um. „System“ überlässt " +
            "die Wahl dem Telefon: Die App wird mit ihm zusammen dunkel und wieder hell.",
    HelpKey.SettingsMushroomSize to
        "Der Schieberegler bestimmt die Größe der Pilzsymbole auf der Karte — auf dem " +
            "Aufzeichnungsbildschirm ebenso wie auf der gesammelten „Fundkarte“. Das Bild darunter ändert " +
            "sich schon beim Ziehen, die Größe ist also vor dem Loslassen zu sehen.",
    HelpKey.SettingsMushroomOrder to
        "Normalerweise rücken gerade markierte Pilze an den Anfang der Kachelreihe. „Pilzreihenfolge " +
            "einfrieren“ schaltet das ganz ab, und „Pilzreihenfolge am Ende des Spaziergangs " +
            "zurücksetzen“ stellt die ursprüngliche Reihenfolge wieder her, sobald ein Spaziergang " +
            "beendet ist.",
    HelpKey.SettingsMapData to
        "„Kartendaten aktualisieren“ prüft, ob sich die Karte auf dem Server geändert hat, und lädt " +
            "gegebenenfalls alle gespeicherten Offline-Bereiche neu. „Kartencache leeren“ entfernt nur " +
            "das beim Blättern Nachgeladene — die Bereiche aus der „Vorbereitung“ bleiben erhalten.",
)
