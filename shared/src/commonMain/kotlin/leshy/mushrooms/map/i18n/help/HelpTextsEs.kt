package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Español — pantallas de ayuda, `.claude/plans/help-screens.md`. */
internal val spanishHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "La pantalla principal de la aplicación: aquí se registra el paseo. El GPS traza tu recorrido y " +
            "cada hallazgo se guarda con coordenadas y hora — al instante, así que el paseo puede " +
            "interrumpirse en cualquier momento sin perder lo registrado.",
    HelpKey.RecordStartFinish to
        "«Iniciar» pide un nombre y comienza el registro; después el botón se convierte en «Pausar», y en " +
            "pausa aparecen «Reanudar» y «Finalizar». «Finalizar» cierra el paseo y lo traslada al " +
            "«Archivo de paseos».",
    HelpKey.RecordTiles to
        "Las fichas de setas de abajo son con lo que se marcan los hallazgos: «+» anota un hallazgo en tu " +
            "punto actual, «−» quita la última marca errónea de esa especie. Mantener pulsado «+» abre la " +
            "entrada de varias a la vez; más de 999 setas iguales por paseo no se pueden marcar.",
    HelpKey.RecordPlace to
        "El botón redondo de la izquierda marca un lugar — con nombre, descripción y foto. El lugar se " +
            "coloca donde estás ahora y permanece en el mapa después del paseo.",
    HelpKey.RecordNavigation to
        "Mantener pulsada la marca de un lugar activa la navegación hacia él: el panel de arriba a la " +
            "derecha muestra la dirección y la distancia al destino. La cruz del panel apaga la " +
            "navegación.",
    HelpKey.RecordSearchAndOwn to
        "La lupa de la derecha encuentra una seta por su nombre y lleva su ficha al principio de la fila " +
            "— así es más rápido cuando hay muchas especies activadas. La última ficha de la fila, con un " +
            "más, añade una especie propia que no está en el catálogo.",
    HelpKey.RecordFilters to
        "El botón «Filtros» de arriba a la izquierda decide de qué especies y de qué periodo se muestran " +
            "los hallazgos en el mapa, y el número que lleva indica cuántos filtros están activos. El " +
            "filtro es común con el «Mapa de hallazgos»: lo que actives aquí vale allí también.",
    HelpKey.RecordBackground to
        "El registro del recorrido continúa cuando la aplicación queda en segundo plano. Además del paseo " +
            "actual, el mapa muestra los hallazgos y los lugares marcados de paseos anteriores — por " +
            "ellos se ve por dónde ya anduviste y qué había allí.",
    HelpKey.ArchivePurpose to
        "Todos tus paseos, los más nuevos arriba. En la tarjeta están el nombre, la fecha, la duración, " +
            "los kilómetros, el número de hallazgos y una miniatura del recorrido.",
    HelpKey.ArchiveDetail to
        "Al pulsar una tarjeta se abre el paseo entero: estadísticas, hallazgos por especie, lugares " +
            "marcados, descripción y el botón «Ver mapa». El nombre y la descripción se cambian ahí " +
            "mismo.",
    HelpKey.ArchiveShare to
        "El botón «Compartir» compone una imagen con las partes del paseo que marques con la casilla. " +
            "Antes de enviar el mapa de un paseo, recuerda: en él se ve exactamente dónde encontraste las " +
            "setas.",
    HelpKey.ArchiveSelection to
        "Mantener pulsada una tarjeta activa el modo de selección: marca los paseos que quieras " +
            "pulsándolos y pulsa «Eliminar paseos»; el botón «Atrás» sale de este modo. El borrado es " +
            "irreversible — con el paseo desaparecen su recorrido, sus hallazgos, sus lugares marcados y " +
            "sus fotos.",
    HelpKey.ArchiveUnfinished to
        "Un paseo sin terminar también aparece en la lista: en lugar de la hora de fin pone «en curso». " +
            "Ese paseo aún no tiene duración, por eso no entra en el tiempo total del «Mapa de " +
            "hallazgos».",
    HelpKey.MapPurpose to
        "El mapa de conjunto: los hallazgos, los recorridos y los lugares marcados de todos tus paseos a " +
            "la vez sobre un mismo lienzo. Sirve para ver el panorama — dónde están tus sitios de setas y " +
            "cómo cambian de un año a otro.",
    HelpKey.MapFullScreen to
        "Arriba está el mapa con todos los hallazgos a la vez; al pulsarlo se abre a pantalla completa. " +
            "Cuando hay muchos hallazgos, las marcas cercanas se juntan en un círculo con un número — " +
            "acerca el mapa y se deshace en setas sueltas. El tamaño de los iconos se ajusta en " +
            "«Ajustes».",
    HelpKey.MapSliders to
        "Bajo el mapa hay dos deslizadores — el intervalo de fechas y la temporada, es decir, un " +
            "intervalo de meses — y todo lo que va debajo se calcula según lo elegido. Los deslizadores " +
            "solo aparecen cuando hay paseos de más de un día.",
    HelpKey.MapStats to
        "Debajo de los deslizadores: cuántos paseos, kilómetros, tiempo y hallazgos hubo, las fichas por " +
            "especie y el diagrama circular. El tiempo total suma los paseos terminados: el que sigue en " +
            "curso aún no tiene duración.",
    HelpKey.MapFilters to
        "El botón «Filtros» vive en el mapa a pantalla completa, arriba a la izquierda: allí están los " +
            "mismos dos ejes, la lista de especies y el interruptor para mostrar recorridos anteriores. " +
            "El número del botón dice cuántos filtros están activos; el filtro es común con la pantalla " +
            "de registro.",
    HelpKey.MapPlaces to
        "Al pulsar la marca de un lugar se abre su tarjeta con la foto y la descripción. Desde ahí el " +
            "lugar también puede modificarse o eliminarse.",
    HelpKey.SpeciesPurpose to
        "Aquí decides qué setas serán fichas en la pantalla de registro. El catálogo está dividido en " +
            "colecciones por países, y junto a él viven las especies que el catálogo no tiene — esas las " +
            "añades tú.",
    HelpKey.SpeciesCollections to
        "En «Colecciones de setas», al pulsar la fila de un país se despliegan sus especies: la casilla " +
            "junto al país activa la colección entera, las casillas de dentro las especies sueltas. El " +
            "campo de búsqueda de arriba encuentra un país por su nombre.",
    HelpKey.SpeciesOwn to
        "En «Setas añadidas», el botón «Añadir seta» abre un formulario: nombre, nombre científico, color " +
            "de la marca e imagen — de la cámara, de la galería o del catálogo. El lápiz modifica una " +
            "especie ya añadida, la cruz la elimina.",
    HelpKey.SpeciesCheckboxes to
        "Quitar la casilla no borra nada — la especie simplemente deja de aparecer como ficha, y los " +
            "hallazgos anteriores se quedan donde están. Eliminar una especie propia, en cambio, es " +
            "irreversible: todas sus marcas en paseos anteriores pasan a «Seta desconocida». Las especies " +
            "con la etiqueta «del archivo» llegaron con paseos importados.",
    HelpKey.SpeciesImages to
        "Todas las imágenes de setas de la aplicación son orientativas: ayudan a reconocer la ficha, no " +
            "la seta en el bosque. No identifiques con ellas setas desconocidas.",
    HelpKey.PreparationPurpose to
        "Descarga por adelantado trozos del mapa a la memoria del teléfono para que en el bosque sin " +
            "internet el mapa siga ahí: sin eso, fuera de cobertura, en lugar del mapa habrá un fondo " +
            "vacío.",
    HelpKey.PreparationDownload to
        "Encuentra la zona que necesitas — mueve y amplía el mapa — y pulsa después el botón redondo con " +
            "la flecha hacia abajo de abajo a la derecha. La aplicación muestra cuánto espacio ocupará lo " +
            "que hay en pantalla: «Descargar esta zona» pide un nombre y empieza la descarga, «Cancelar» " +
            "devuelve el mapa.",
    HelpKey.PreparationRegions to
        "Las zonas descargadas están en una banda abajo. Al pulsar una tarjeta se vuela hasta esa zona en " +
            "el mapa, y los botones de la tarjeta pausan la descarga y la reanudan, reintentan tras un " +
            "error y eliminan la zona.",
    HelpKey.PreparationAreaSize to
        "Se descarga exactamente lo que se ve en pantalla, por eso la estimación de tamaño cambia " +
            "mientras mueves el mapa. Cuanto mayor es la zona, menos detallada tiene que ser — sale más a " +
            "cuenta descargar varias zonas pequeñas que una enorme. Los nombres de las zonas no deben " +
            "repetirse.",
    HelpKey.PreparationBackground to
        "La descarga sigue en segundo plano y no se interrumpe si sales de la pantalla; en pausa se " +
            "conserva el progreso. «Actualizar datos del mapa» en «Ajustes» vuelve a descargar todas las " +
            "zonas guardadas.",
    HelpKey.DataPurpose to
        "Traslado de paseos entre teléfonos y copia de seguridad: los paseos elegidos se guardan en un " +
            "único archivo, y ese archivo puede volver a cargarse — en este dispositivo o en otro.",
    HelpKey.DataExport to
        "El interruptor de arriba elige «Exportar» o «Importar». En «Exportar» pon nombre al archivo, " +
            "pulsa la fila de selección de paseos, marca los que quieras y luego «Listo» — el teléfono " +
            "preguntará dónde guardar el archivo.",
    HelpKey.DataImport to
        "En «Importar» pulsa «Elegir archivo», escribe si quieres una coletilla que se añadirá a los " +
            "nombres de los paseos cargados y pulsa «Listo»; cuando el archivo se haya leído aparecerá el " +
            "botón «Al archivo». El botón «Cancelar» limpia lo introducido sin guardar nada.",
    HelpKey.DataArchiveContents to
        "Al archivo van el recorrido, los hallazgos, los lugares marcados, las fotos y aquellas especies " +
            "de setas que no están en el catálogo — en el otro dispositivo aparecen en «Setas añadidas» " +
            "con la etiqueta «del archivo».",
    HelpKey.DataDuplicates to
        "La importación siempre añade los paseos junto a los existentes y no sustituye nada, así que " +
            "cargar el mismo archivo otra vez los creará de nuevo: la coletilla en los nombres ayuda " +
            "luego a distinguir un lote del otro. Al terminar se muestra cuántos paseos se cargaron y " +
            "cuántos no se pudieron leer.",
    HelpKey.SettingsPurpose to
        "Las opciones generales de la aplicación: idioma de la interfaz, apariencia, aspecto y orden de " +
            "las fichas de setas en la pantalla de registro, y mantenimiento del mapa.",
    HelpKey.SettingsLanguage to
        "La fila «Idioma de la interfaz» abre la lista de idiomas: al pulsar se elige uno, la marca de " +
            "arriba confirma la elección, la flecha sale sin cambiar nada. El idioma se aplica al " +
            "instante en toda la aplicación, no hace falta reiniciar.",
    HelpKey.SettingsTheme to
        "«Apariencia» alterna el tema claro y el oscuro de la aplicación. «Sistema» deja la elección al " +
            "teléfono: la aplicación se oscurece y se aclara junto con él.",
    HelpKey.SettingsMushroomSize to
        "El deslizador fija el tamaño de los iconos de setas en el mapa — tanto en la pantalla de " +
            "registro como en el «Mapa de hallazgos». La imagen de debajo cambia mientras arrastras, así " +
            "que ves el tamaño antes de soltar.",
    HelpKey.SettingsMushroomOrder to
        "Normalmente las setas recién marcadas suben al principio de la fila de fichas. «Fijar el orden " +
            "de las setas» lo desactiva por completo, y «Restablecer el orden de las setas al finalizar " +
            "el paseo» devuelve el orden inicial cuando el paseo termina.",
    HelpKey.SettingsMapData to
        "«Actualizar datos del mapa» comprueba si el mapa ha cambiado en el servidor y, si es así, vuelve " +
            "a descargar todas las zonas sin conexión guardadas. «Vaciar caché del mapa» borra solo lo " +
            "que se cargó al navegar — las zonas de «Precarga» siguen ahí.",
)
