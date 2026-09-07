package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Português — ecrãs de ajuda, `.claude/plans/help-screens.md`. */
internal val portugueseHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "O ecrã principal da aplicação: é aqui que a caminhada é registada. O GPS traça o seu percurso e " +
            "cada achado é guardado com coordenadas e hora — de imediato, pelo que a caminhada pode ser " +
            "interrompida a qualquer momento sem perder o que já foi registado.",
    HelpKey.RecordStartFinish to
        "«Iniciar» pede um nome e começa o registo; depois o botão passa a «Pausa» e, em pausa, aparecem " +
            "«Retomar» e «Terminar». «Terminar» fecha a caminhada e passa-a para o «Arquivo de " +
            "caminhadas».",
    HelpKey.RecordTiles to
        "Os mosaicos de cogumelos em baixo são com o que se marcam os achados: «+» regista um achado no " +
            "seu ponto atual, «−» retira a última marcação errada dessa espécie. Manter «+» premido abre " +
            "a introdução de vários de uma vez; mais de 999 cogumelos iguais por caminhada não é possível " +
            "marcar.",
    HelpKey.RecordPlace to
        "O botão redondo à esquerda marca um local — com nome, descrição e fotografia. O local fica onde " +
            "está neste momento e permanece no mapa depois da caminhada.",
    HelpKey.RecordNavigation to
        "Manter premido o marcador de um local liga a navegação até ele: o painel no canto superior " +
            "direito mostra a direção e a distância ao destino. A cruz no painel desliga a navegação.",
    HelpKey.RecordSearchAndOwn to
        "A lupa à direita encontra o cogumelo pelo nome e leva o seu mosaico para o início da fila — é " +
            "mais rápido quando há muitas espécies ativas. O último mosaico da fila, com um mais, " +
            "acrescenta uma espécie sua que não existe no catálogo.",
    HelpKey.RecordFilters to
        "O botão «Filtros» no canto superior esquerdo define que espécies e de que período aparecem no " +
            "mapa, e o número nele indica quantos filtros estão ativos. O filtro é comum com o «Mapa de " +
            "achados»: o que ligar aqui vale também lá.",
    HelpKey.RecordBackground to
        "O registo do percurso continua quando a aplicação está em segundo plano. Além da caminhada " +
            "atual, o mapa mostra achados e locais marcados de caminhadas anteriores — por eles vê-se por " +
            "onde já andou e o que havia ali.",
    HelpKey.ArchivePurpose to
        "Todas as suas caminhadas, as mais recentes em cima. No cartão estão o nome, a data, a duração, " +
            "os quilómetros, o número de achados e uma miniatura do percurso feito.",
    HelpKey.ArchiveDetail to
        "Tocar no cartão abre a caminhada inteira: estatísticas, achados por espécie, locais marcados, " +
            "descrição e o botão «Ver o mapa». O nome e a descrição podem ser alterados no mesmo sítio.",
    HelpKey.ArchiveShare to
        "O botão «Partilhar» compõe uma imagem com as partes da caminhada que assinalar. Antes de enviar " +
            "o mapa de uma caminhada, lembre-se: nele vê-se exatamente onde encontrou os cogumelos.",
    HelpKey.ArchiveSelection to
        "Manter premido um cartão liga o modo de seleção: marque as caminhadas pretendidas com um toque e " +
            "prima «Eliminar caminhadas»; o botão «Voltar» sai deste modo. A eliminação é irreversível — " +
            "com a caminhada desaparecem o percurso, os achados, os locais marcados e as fotografias.",
    HelpKey.ArchiveUnfinished to
        "Uma caminhada por terminar também aparece na lista: em vez da hora de fim tem escrito «a " +
            "decorrer». Essa caminhada ainda não tem duração, por isso não entra no tempo total do «Mapa " +
            "de achados».",
    HelpKey.MapPurpose to
        "O mapa de conjunto: os achados, os percursos e os locais marcados de todas as suas caminhadas ao " +
            "mesmo tempo numa só tela. Serve para ver o panorama — onde estão os seus sítios de cogumelos " +
            "e como mudam de ano para ano.",
    HelpKey.MapFullScreen to
        "Em cima está o mapa com todos os achados ao mesmo tempo; tocar nele abre o mapa em ecrã inteiro. " +
            "Quando há muitos achados, as marcas próximas juntam-se num círculo com um número — aproxime " +
            "o mapa e ele desfaz-se em cogumelos separados. O tamanho dos ícones ajusta-se nas " +
            "«Definições».",
    HelpKey.MapSliders to
        "Por baixo do mapa há dois cursores — intervalo de datas e época, ou seja, intervalo de meses — e " +
            "tudo o que está abaixo é calculado conforme a escolha. Os cursores só aparecem quando há " +
            "caminhadas de mais do que um dia.",
    HelpKey.MapStats to
        "Abaixo dos cursores: quantas caminhadas, quilómetros, tempo e achados houve, os mosaicos por " +
            "espécie e o gráfico circular. O tempo total soma as caminhadas terminadas: a que está a " +
            "decorrer ainda não tem duração.",
    HelpKey.MapFilters to
        "O botão «Filtros» vive no mapa em ecrã inteiro, no canto superior esquerdo: ali estão os mesmos " +
            "dois eixos, a lista de espécies e o interruptor para mostrar percursos anteriores. O número " +
            "no botão diz quantos filtros estão ativos; o filtro é comum com o ecrã de registo.",
    HelpKey.MapPlaces to
        "Tocar no marcador de um local abre o seu cartão com fotografia e descrição. Dali o local também " +
            "pode ser alterado ou eliminado.",
    HelpKey.SpeciesPurpose to
        "Aqui decide que cogumelos serão mosaicos no ecrã de registo. O catálogo está dividido em " +
            "coleções por país e, ao lado, vivem as espécies que o catálogo não tem — essas é o " +
            "utilizador que as acrescenta.",
    HelpKey.SpeciesCollections to
        "Em «Coleções de cogumelos», tocar na linha de um país abre as suas espécies: a marca junto ao " +
            "país liga a coleção inteira, as marcas interiores as espécies individuais. O campo de " +
            "pesquisa em cima encontra um país pelo nome.",
    HelpKey.SpeciesOwn to
        "Em «Cogumelos adicionados», o botão «Adicionar cogumelo» abre um formulário: nome, nome " +
            "científico, cor da marca e imagem — da câmara, da galeria ou do catálogo. O lápis altera uma " +
            "espécie já adicionada, a cruz elimina-a.",
    HelpKey.SpeciesCheckboxes to
        "Tirar a marca não apaga nada — a espécie deixa apenas de aparecer como mosaico, e os achados " +
            "anteriores ficam onde estão. Eliminar uma espécie sua, pelo contrário, é irreversível: todas " +
            "as suas marcações em caminhadas anteriores passam para «Cogumelo desconhecido». As espécies " +
            "com a nota «do arquivo» vieram com caminhadas importadas.",
    HelpKey.SpeciesImages to
        "Todas as imagens de cogumelos na aplicação são indicativas: ajudam a reconhecer o mosaico, não o " +
            "cogumelo na floresta. Não identifique por elas cogumelos desconhecidos.",
    HelpKey.PreparationPurpose to
        "Descarrega antecipadamente pedaços do mapa para a memória do telemóvel, para que na floresta sem " +
            "internet o mapa continue lá: sem isso, fora de cobertura, em vez do mapa fica um fundo " +
            "vazio.",
    HelpKey.PreparationDownload to
        "Encontre a área de que precisa — mova e amplie o mapa — e depois prima o botão redondo com a " +
            "seta para baixo no canto inferior direito. A aplicação mostra quanto espaço ocupará o que " +
            "está agora no ecrã: «Descarregar esta área» pede um nome e inicia a transferência, " +
            "«Cancelar» devolve o mapa.",
    HelpKey.PreparationRegions to
        "As áreas descarregadas ficam numa faixa em baixo. Tocar numa placa voa até essa área no mapa, e " +
            "os botões da placa põem a transferência em pausa e retomam-na, repetem a tentativa após um " +
            "erro e eliminam a área.",
    HelpKey.PreparationAreaSize to
        "É descarregado exatamente o que se vê no ecrã, por isso a estimativa de tamanho muda enquanto " +
            "move o mapa. Quanto maior a área, menos detalhada tem de ser — compensa mais descarregar " +
            "várias áreas pequenas do que uma enorme. Os nomes das áreas não se podem repetir.",
    HelpKey.PreparationBackground to
        "A transferência decorre em segundo plano e não é interrompida se sair do ecrã; em pausa o " +
            "progresso é conservado. «Atualizar os dados do mapa» nas «Definições» volta a descarregar " +
            "todas as áreas guardadas.",
    HelpKey.DataPurpose to
        "Passagem de caminhadas entre telemóveis e cópia de segurança: as caminhadas escolhidas são " +
            "exportadas para um único ficheiro de arquivo, e um ficheiro desses pode ser carregado de " +
            "volta — neste ou noutro dispositivo.",
    HelpKey.DataExport to
        "O interruptor em cima escolhe «Exportar» ou «Importar». Em «Exportar» dê um nome ao arquivo, " +
            "toque na linha de seleção de caminhadas, assinale as pretendidas e prima «Concluído» — o " +
            "telemóvel perguntará onde guardar o ficheiro.",
    HelpKey.DataImport to
        "Em «Importar» prima «Escolher ficheiro», escreva se quiser um acrescento que será junto aos " +
            "nomes das caminhadas carregadas e prima «Concluído»; quando o arquivo for lido, aparece o " +
            "botão «Ir ao arquivo». O botão «Cancelar» limpa o introduzido sem guardar nada.",
    HelpKey.DataArchiveContents to
        "Para o arquivo vão o percurso, os achados, os locais marcados, as fotografias e aquelas espécies " +
            "de cogumelos que não estão no catálogo — no outro dispositivo aparecem em «Cogumelos " +
            "adicionados» com a nota «do arquivo».",
    HelpKey.DataDuplicates to
        "A importação acrescenta sempre as caminhadas às já existentes e não substitui nada, por isso " +
            "carregar o mesmo ficheiro outra vez cria-as de novo: o acrescento aos nomes ajuda depois a " +
            "distinguir um lote do outro. No fim é mostrado quantas caminhadas foram carregadas e quantas " +
            "não puderam ser lidas.",
    HelpKey.SettingsPurpose to
        "As opções gerais da aplicação: idioma, aspeto, aparência e ordem dos mosaicos de cogumelos no " +
            "ecrã de registo e manutenção do mapa.",
    HelpKey.SettingsLanguage to
        "A linha «Idioma da aplicação» abre a lista de idiomas: o toque escolhe um idioma, a marca em " +
            "cima confirma a escolha, a seta sai sem alterar nada. O idioma aplica-se de imediato em toda " +
            "a aplicação, não é preciso reiniciar.",
    HelpKey.SettingsTheme to
        "«Aspeto» alterna entre o tema claro e o escuro da aplicação. «Do sistema» deixa a escolha ao " +
            "telemóvel: a aplicação escurece e clareia com ele.",
    HelpKey.SettingsMushroomSize to
        "O cursor define o tamanho dos ícones de cogumelos no mapa — tanto no ecrã de registo como no " +
            "«Mapa de achados». A imagem por baixo muda enquanto arrasta, pelo que vê o tamanho antes de " +
            "largar.",
    HelpKey.SettingsMushroomOrder to
        "Normalmente os cogumelos acabados de marcar sobem para o início da fila de mosaicos. «Fixar a " +
            "ordem» desliga isso por completo, e «Repor a ordem no fim da caminhada» devolve a ordem " +
            "inicial quando a caminhada termina.",
    HelpKey.SettingsMapData to
        "«Atualizar os dados do mapa» verifica se o mapa mudou no servidor e, se sim, volta a descarregar " +
            "todas as áreas offline guardadas. «Limpar a cache do mapa» remove apenas o que foi carregado " +
            "ao navegar — as áreas de «Descarregar antecipadamente» ficam.",
)
