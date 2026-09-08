package leshy.mushrooms.map.i18n.strings

import leshy.mushrooms.map.i18n.StringKey

/** Portuguese (European) — Phase 5 of `.claude/plans/europe-15-countries.md`. Plural rule is the
 * French one exactly (`Plurals.kt`): 0 joins 1 in the singular, and
 * [leshy.mushrooms.map.i18n.PluralCategory.Many] is the "million" form, unreachable at realistic
 * counts and repeating the plural here. `Zero`/`Two`/`Few` are unreachable for `pt` as well. */
internal val portugueseStrings: Map<StringKey, String> = mapOf(
    StringKey.AppName to "Mapa de Cogumelos do Leshy",
    StringKey.NavRecord to "Novo registo",
    StringKey.NavArchive to "Arquivo de caminhadas",
    StringKey.NavData to "Exportar/Importar",
    StringKey.NavMap to "Mapa de achados",
    StringKey.NavPreparation to "Descarregar antecipadamente",
    StringKey.NavSpecies to "Os meus cogumelos",
    StringKey.SettingsTitle to "Definições",
    StringKey.SettingsContentDescription to "Definições",
    StringKey.SettingsLanguageTitle to "Idioma da aplicação",
    StringKey.SettingsThemeTitle to "Aspeto",
    StringKey.SettingsThemeLight to "Claro",
    StringKey.SettingsThemeDark to "Escuro",
    StringKey.SettingsThemeSystem to "Do sistema",
    StringKey.SettingsCategoriesTitle to "Cogumelos a registar",
    StringKey.SettingsMushroomSizeTitle to "Ajustar o tamanho dos cogumelos no mapa",
    StringKey.SettingsMushroomSortTitle to "Ordem dos cogumelos",
    StringKey.SettingsResetMushroomOrderOnWalkFinish to "Repor a ordem no fim da caminhada",
    StringKey.SettingsFreezeMushroomOrder to "Fixar a ordem",
    StringKey.MushroomImagesDisclaimer to
        "Todas as imagens de cogumelos na aplicação são ilustrações — não as use para identificar " +
            "cogumelos desconhecidos!",
    StringKey.SpeciesCollectionsTitle to "Coleções de cogumelos por país",
    StringKey.SpeciesMyMushroomsTitle to "Cogumelos adicionados",
    StringKey.SpeciesMyMushroomsEmpty to "Os cogumelos que adicionar aparecem aqui",
    StringKey.SpeciesAddButton to "Adicionar cogumelo",
    StringKey.SpeciesFormTitleCreate to "Novo cogumelo",
    StringKey.SpeciesFormTitleEdit to "Editar cogumelo",
    StringKey.SpeciesFormNameHint to "Nome",
    StringKey.SpeciesFormScientificNameHint to "Nome científico",
    StringKey.SpeciesFormColorLabel to "Cor",
    StringKey.SpeciesFormTakePhotoButton to "Câmara",
    StringKey.SpeciesFormPickPhotoButton to "Galeria",
    StringKey.SpeciesFormPickCatalogButton to "Imagens",
    StringKey.SpeciesFormSaveButton to "Guardar",
    StringKey.SpeciesFormCancelContentDescription to "Cancelar",
    StringKey.SpeciesCollectionDialogTitle to "Em que coleção?",
    StringKey.SpeciesCollectionDialogBackContentDescription to "Voltar",
    StringKey.SpeciesCollectionDialogSaveContentDescription to "Guardar na coleção",
    StringKey.SpeciesCollectionNameIsCountry to "Esse é o nome de um país — escolha outro",
    StringKey.SpeciesListImportedLabel to "do arquivo",
    StringKey.SpeciesListEditContentDescription to "Editar",
    StringKey.SpeciesListDeleteContentDescription to "Eliminar espécie",
    StringKey.SpeciesDeleteConfirmTitle to "Eliminar este cogumelo?",
    StringKey.SpeciesDeleteConfirmMessage to
        "Tem a certeza de que quer eliminar esta espécie? Todos os achados registados nela passam " +
            "para «Cogumelo desconhecido». Não é possível desfazer.",
    StringKey.SpeciesDeleteConfirmYes to "Sim",
    StringKey.SpeciesDeleteConfirmNo to "Não",
    StringKey.CatalogPhotoPickerTitle to "Escolha uma imagem",
    StringKey.IconEditorTitle to "Editor de fotografia",
    StringKey.IconEditorToolEraser to "Borracha",
    StringKey.IconEditorToolCrop to "Recortar",
    StringKey.IconEditorShapeRectangle to "Retângulo",
    StringKey.IconEditorShapeOval to "Oval",
    StringKey.IconEditorBrushSizeLabel to "Tamanho do pincel",
    StringKey.IconEditorUndoContentDescription to "Anular",
    StringKey.IconEditorRedoContentDescription to "Refazer",
    StringKey.IconEditorDoneContentDescription to "Concluído",
    StringKey.OnboardingTitle to "Bem-vindo!",
    StringKey.OnboardingDescription to
        "Escolha as coleções de cogumelos que lhe interessam. Pode mudar isto mais tarde nas definições.",
    StringKey.OnboardingContinueButton to "Começar",
    StringKey.OnboardingNothingPickedWarning to
        "Escolha pelo menos uma coleção ou um cogumelo para continuar",
    StringKey.WelcomeIntro to
        "A aplicação lembra-se de por onde andou e do que encontrou — e isso ajuda mesmo a " +
            "apanhar mais cogumelos: é fácil voltar aos bons sítios e cada achado fica num único " +
            "mapa.",
    StringKey.WelcomeRecordTitle to "Registe a sua caminhada",
    StringKey.WelcomeRecordText to
        "A aplicação guarda sozinha o percurso, o tempo e a distância. Encontrou um cogumelo — " +
            "toque no seu quadrado para o registar; uma nascente, uma árvore caída ou o seu carro " +
            "podem ser marcados no próprio mapa.",
    StringKey.WelcomeArchiveTitle to "Volte aos seus achados",
    StringKey.WelcomeArchiveText to
        "O arquivo guarda cada caminhada em separado, com o seu percurso e os seus achados. O " +
            "mapa comum mostra-as todas juntas: o que foi encontrado onde e em que quantidade, ao " +
            "longo de todas as suas épocas.",
    StringKey.WelcomeHelpTitle to "Em dúvida? Toque em «?»",
    StringKey.WelcomeHelpText to
        "Cada secção tem um botão «?» no canto superior direito que explica como essa secção funciona.",
    StringKey.WelcomeMenuTitle to "O resto está no menu",
    StringKey.WelcomeMenuText to
        "O botão de menu no canto superior esquerdo abre a lista de todas as secções e " +
            "funcionalidades da aplicação.",
    StringKey.WelcomeConsentTitle to "Antes de começar",
    StringKey.WelcomeConsentIntro to
        "Antes de passar à aplicação propriamente dita, tem de concordar com as seguintes afirmações:",
    StringKey.WelcomeConsentImages to
        "Não vai identificar cogumelos pelas imagens da aplicação. As imagens são ilustrações, " +
            "não um guia de campo verificado.",
    StringKey.WelcomeConsentEating to
        "Não vai, em circunstância alguma, comer cogumelos que não conhece. Os cogumelos podem " +
            "ser não comestíveis e podem ser venenosos. O melhor de tudo — pergunte a alguém que " +
            "conheça os cogumelos da sua região quais podem ser apanhados e como têm de ser " +
            "cozinhados depois.",
    StringKey.WelcomeConsentBattery to
        "Durante as caminhadas vai cumprir as regras de segurança necessárias e ter em conta que, com a aplicação " +
            "a funcionar, o telemóvel descarrega mais depressa. Com pouca bateria é melhor parar a gravação da " +
            "caminhada e fechar a aplicação.",
    StringKey.WelcomeConsentWarning to
        "Para continuar tem de concordar com as afirmações acima, assinalando as caixas à frente " +
            "das afirmações com que concorda",
    StringKey.WelcomeNextButton to "Continuar",
    StringKey.LegalTitle to "Privacidade",
    StringKey.LegalPrivacyText to
        "As suas caminhadas, marcas e fotografias ficam no seu dispositivo. A aplicação não tem " +
            "contas e não envia nenhum dos seus dados para lado nenhum — a única coisa que vai à " +
            "Internet é um pedido de mosaicos de mapa a openfreemap.org.",
    StringKey.LegalPrivacyLink to "Política de privacidade",
    StringKey.AboutTitle to "Acerca da aplicação",
    StringKey.AboutMapDataTitle to "Dados do mapa",
    StringKey.AboutMapDataText to
        "O mapa assenta em dados do OpenStreetMap, distribuídos sob a licença ODbL. Os mosaicos " +
            "vetoriais e o estilo vêm do OpenMapTiles e são fornecidos pelo serviço OpenFreeMap.",
    StringKey.AboutOpenSourceTitle to "Código aberto",
    StringKey.AboutOpenSourceText to
        "A aplicação é feita de bibliotecas de código aberto. Toque numa linha da lista para " +
            "abrir o texto completo da respetiva licença.",
    StringKey.NavMenuContentDescription to "Menu",
    StringKey.HelpContentDescription to "Ajuda",
    StringKey.HelpDialogTitle to "Ajuda",
    StringKey.HelpDialogDismiss to "Entendido",
    StringKey.CategoryMisc to "Diversos",
    StringKey.CategoryUnknownMushroom to "Cogumelo desconhecido",
    StringKey.CollectionOtherName to "Outros",
    StringKey.CollectionPickerSearchHint to "Procurar coleção ou cogumelo",
    StringKey.CollectionPickerMoreMatches to "Nem todos os resultados são mostrados — refine a pesquisa",
    StringKey.LanguagePickerSearchHint to "Procurar idioma",
    StringKey.LanguagePickerBackContentDescription to "Voltar",
    StringKey.LanguagePickerConfirmContentDescription to "Confirmar",
    StringKey.DefaultWalkName to "Caminhada",
    StringKey.RecordWalkNameHint to "Nome da caminhada",
    StringKey.RecordStart to "Iniciar",
    StringKey.RecordPause to "Pausa",
    StringKey.RecordResume to "Retomar",
    StringKey.RecordFinish to "Terminar",
    StringKey.RecordSetWalkNameTitle to "Dê um nome à caminhada:",
    StringKey.RecordDefaultWalkNamePrefix to "Caminhada de",
    StringKey.RecordConfirmWalkNameContentDescription to "Confirmar",
    StringKey.RecordMarkLocationContentDescription to "Marcar local",
    StringKey.RecordLocationUnavailable to
        "A localização não está disponível — o percurso não está a ser registado. Ligue a " +
            "localização e permita que a aplicação a use nas definições do dispositivo.",
    StringKey.RecordLocationUnknownMessage to
        "A sua localização ainda não é conhecida — não há a que fixar a marca. Verifique se a " +
            "localização está ligada e aguarde sinal.",
    StringKey.RecordSearchContentDescription to "Procurar",
    StringKey.RecordSearchDialogTitle to "Escolha o cogumelo de que precisa",
    StringKey.RecordBulkAddQuestion to "Quantos cogumelos novos encontrou?",
    StringKey.RecordBulkAddCancelContentDescription to "Cancelar",
    StringKey.RecordBulkAddConfirmContentDescription to "Confirmar",
    StringKey.RecordBulkAddLimitMessage to "No máximo 999 achados da mesma espécie por caminhada.",
    StringKey.DialogAcknowledge to "Entendido",
    StringKey.NavigationDirectionToPrefix to "Direção para",
    StringKey.NavigationDistanceToTargetPrefix to "até ao destino",
    StringKey.NavigationMetersSuffix to "metros",
    StringKey.NavigationKeepRightPhrase to "mantenha-se à direita",
    StringKey.NavigationKeepLeftPhrase to "mantenha-se à esquerda",
    StringKey.NavigationGoStraightPhrase to "siga em frente",
    StringKey.NavigationDeterminingDirection to "A determinar a direção…",
    StringKey.NavigationArrivedPhrase to "Chegou ao destino",
    StringKey.NavigationCloseContentDescription to "Fechar",
    StringKey.AddPlaceTitle to "Adicionar um local",
    StringKey.AddPlaceEditTitle to "Editar local",
    StringKey.AddPlaceDefaultName to "Local",
    StringKey.AddPlaceNameHint to "Nome do local",
    StringKey.AddPlacePhotoContentDescription to "Tirar fotografia",
    StringKey.CameraPermissionDenied to
        "Sem acesso à câmara. Permita-o para a aplicação nas definições do dispositivo.",
    StringKey.AddPlaceDescriptionTitle to "Descrição",
    StringKey.AddPlaceDescriptionHint to "Descreva o local",
    StringKey.AddPlaceCoordinatesTitle to "Coordenadas",
    StringKey.AddPlaceCopyCoordinatesContentDescription to "Copiar coordenadas",
    StringKey.AddPlaceSaveContentDescription to "Guardar local",
    StringKey.AddPlaceDiscardContentDescription to "Descartar local",
    StringKey.PlaceViewEditContentDescription to "Editar local",
    StringKey.PlaceViewDeleteContentDescription to "Eliminar local",
    StringKey.PlaceDeleteConfirmTitle to "Eliminar local?",
    StringKey.PlaceDeleteConfirmMessage to
        "O local será eliminado definitivamente. Não é possível desfazer.",
    StringKey.PlaceDeleteConfirmYes to "Sim",
    StringKey.PlaceDeleteConfirmNo to "Não",
    StringKey.ArchiveEmpty to "Ainda não há caminhadas registadas",
    StringKey.ArchiveEmptyHint to
        "As caminhadas que registar aparecem aqui: o percurso, os achados e os locais que marcou.",
    StringKey.EmptyStartWalkButton to "Iniciar caminhada",
    StringKey.ArchiveDeleteWalksButton to "Eliminar caminhadas",
    StringKey.ArchiveDeleteConfirmMessage to
        "Tem a certeza de que quer eliminar definitivamente as caminhadas selecionadas?",
    StringKey.ArchiveDeleteConfirmYes to "Sim",
    StringKey.ArchiveDeleteConfirmNo to "Não",
    StringKey.WalkDetailStartTime to "Início",
    StringKey.WalkDetailEndTime to "Fim",
    StringKey.WalkDetailInProgress to "a decorrer",
    StringKey.WalkDetailDistance to "Distância",
    StringKey.WalkDetailDuration to "Duração",
    StringKey.WalkDetailAvgSpeed to "Velocidade média",
    StringKey.WalkDetailDurationDays to "d",
    StringKey.WalkDetailDurationHours to "h",
    StringKey.WalkDetailDurationMinutes to "min",
    StringKey.WalkCardDurationHours to "h",
    StringKey.WalkCardDurationMinutes to "m",
    StringKey.UnitKilometers to "km",
    StringKey.UnitKmh to "km/h",
    StringKey.UnitMegabytes to "MB",
    StringKey.WalkDetailFindsTitle to "Achados por espécie",
    StringKey.WalkDetailFindsEmpty to "Nenhum achado registado",
    StringKey.WalkDetailPlacesTitle to "Locais marcados",
    StringKey.WalkDetailViewMap to "Ver o mapa",
    StringKey.WalkDetailEditContentDescription to "Editar o nome da caminhada",
    StringKey.WalkDetailEditWalkNameTitle to "Edite o nome da caminhada:",
    StringKey.WalkDetailConfirmEditWalkNameContentDescription to "Confirmar",
    StringKey.WalkDetailDeleteContentDescription to "Eliminar caminhada",
    StringKey.WalkDetailShareAction to "Partilhar",
    StringKey.WalkDetailDeleteAction to "Eliminar",
    StringKey.WalkDetailDeleteConfirmTitle to "Eliminar caminhada?",
    StringKey.WalkDetailDeleteConfirmMessage to
        "A caminhada e todos os seus achados serão eliminados definitivamente. Não é possível desfazer.",
    StringKey.WalkDetailDeleteConfirmYes to "Sim",
    StringKey.WalkDetailDeleteConfirmNo to "Não",
    StringKey.WalkDetailMushroomsCountZero to "cogumelos",
    StringKey.WalkDetailMushroomsCountOne to "cogumelo",
    StringKey.WalkDetailMushroomsCountTwo to "cogumelos",
    StringKey.WalkDetailMushroomsCountFew to "cogumelos",
    StringKey.WalkDetailMushroomsCountMany to "cogumelos",
    StringKey.WalkDetailMushroomsCountOther to "cogumelos",
    StringKey.WalkDetailDescriptionTitle to "Descrição",
    StringKey.WalkDetailDescriptionEmpty to "Sem descrição",
    StringKey.WalkDetailDescriptionHint to "Descreva a caminhada",
    StringKey.WalkDetailEditDescriptionContentDescription to "Editar a descrição",
    StringKey.WalkDetailDescriptionCancelContentDescription to "Cancelar",
    StringKey.WalkDetailDescriptionSaveContentDescription to "Guardar",
    StringKey.WalkShareContentDescription to "Partilhar caminhada",
    StringKey.WalkShareDialogTitle to "Partilhar",
    StringKey.WalkShareOptionName to "Nome da caminhada",
    StringKey.WalkShareOptionStats to "Estatísticas da caminhada",
    StringKey.WalkShareOptionDescription to "Descrição da caminhada",
    StringKey.WalkShareOptionDiagram to "Gráfico dos achados",
    StringKey.WalkShareOptionMap to "Mapa com as marcas",
    StringKey.WalkShareMapWarning to "Outras pessoas vão poder ver onde encontrou cogumelos",
    StringKey.WalkShareCancelButton to "Cancelar",
    StringKey.WalkShareConfirmButton to "Partilhar",
    StringKey.WalkShareFooter to "Feito com a aplicação «Mapa de Cogumelos do Leshy»",
    StringKey.WalkShareImageFooter to "Criado na aplicação Mapa de Cogumelos do Leshy",
    StringKey.MapStatsTitle to "Estatísticas",
    StringKey.MapStatsWalksCount to "Caminhadas",
    StringKey.MapStatsFindsCount to "Cogumelos encontrados",
    StringKey.MapStatsEmptyHint to
        "As estatísticas formam-se sozinhas assim que a primeira caminhada for registada.",
    StringKey.MapFilterButtonLabel to "Filtros",
    StringKey.MapFilterDialogTitle to "Defina os filtros aplicados aos cogumelos no mapa:",
    StringKey.MapFilterBackContentDescription to "Voltar",
    StringKey.MapFilterDateRangeTitle to "Período",
    StringKey.MapFilterMonthRangeTitle to "Época",
    StringKey.MapFilterPastRoutesTitle to "Mostrar percursos anteriores",
    StringKey.MapFilterShowPastRoutes to "Mostrar percursos anteriores",
    StringKey.MonthJanuary to "janeiro",
    StringKey.MonthFebruary to "fevereiro",
    StringKey.MonthMarch to "março",
    StringKey.MonthApril to "abril",
    StringKey.MonthMay to "maio",
    StringKey.MonthJune to "junho",
    StringKey.MonthJuly to "julho",
    StringKey.MonthAugust to "agosto",
    StringKey.MonthSeptember to "setembro",
    StringKey.MonthOctober to "outubro",
    StringKey.MonthNovember to "novembro",
    StringKey.MonthDecember to "dezembro",
    StringKey.BackgroundRecordingChannelName to "Registo da caminhada",
    StringKey.BackgroundRecordingNotificationTitle to "A registar a sua caminhada",
    StringKey.BackgroundRecordingNotificationText to
        "O percurso está a ser registado em segundo plano. Toque para voltar à aplicação.",
    StringKey.DataExportOption to "Exportar",
    StringKey.DataImportOption to "Importar",
    StringKey.DataArchiveNameLabel to "Nome do arquivo",
    StringKey.DataChooseFileButton to "Escolher ficheiro",
    StringKey.DataFileStatusLabel to "Ficheiro a importar",
    StringKey.DataFileNotSelected to "não escolhido",
    StringKey.DataImportLabelFieldLabel to "Etiqueta nos nomes das caminhadas importadas",
    StringKey.DataDoneButton to "Concluído",
    StringKey.DataSavedButton to "Guardado",
    StringKey.DataGoToArchiveButton to "Ir ao arquivo",
    StringKey.DataCancelButton to "Cancelar",
    StringKey.DataProcessingLabel to "A processar…",
    StringKey.DataExportSuccessMessage to "O arquivo foi guardado",
    StringKey.DataImportedWalksLabel to "Caminhadas importadas",
    StringKey.DataImportFailedWalksLabel to "Não importadas",
    StringKey.DataErrorLabel to "Erro",
    StringKey.DataImportRejectedTitle to "Não é possível importar este ficheiro",
    StringKey.DataImportRejectedNotArchive to
        "Isto não é um arquivo: o ficheiro não pode ser lido como ZIP. Escolha um arquivo " +
            "exportado do Leshy.",
    StringKey.DataImportRejectedNotLeshy to
        "Isto é um arquivo ZIP, mas não do Leshy: não tem manifest.json.",
    StringKey.DataImportRejectedNewerFormat to
        "O arquivo foi criado por uma versão mais recente da aplicação. Atualize a aplicação e " +
            "tente de novo.",
    StringKey.DataImportRejectedDamaged to
        "O arquivo está danificado: parte do conteúdo não pode ser lida. Nada foi importado.",
    StringKey.DataImportRejectedNoWalks to "O arquivo não contém caminhadas — não há nada para importar.",
    StringKey.DataChooseWalksTitle to "Caminhadas a exportar",
    StringKey.DataWalksBackContentDescription to "Voltar sem guardar a seleção",
    StringKey.DataWalksConfirmContentDescription to "Confirmar a seleção",
    StringKey.DataWalksSelectedLabel to "Selecionadas",
    StringKey.DataWalksCountZero to "caminhadas",
    StringKey.DataWalksCountOne to "caminhada",
    StringKey.DataWalksCountTwo to "caminhadas",
    StringKey.DataWalksCountFew to "caminhadas",
    StringKey.DataWalksCountMany to "caminhadas",
    StringKey.DataWalksCountOther to "caminhadas",
    StringKey.PreparationSelectAreaButton to "Descarregar a área visível",
    StringKey.PreparationDownloadThisAreaButton to "Descarregar esta área",
    StringKey.PreparationRegionNameDialogTitle to "Nome da área",
    StringKey.PreparationRegionNameLabel to "Por exemplo, Pinhal junto à aldeia",
    StringKey.PreparationSaveButton to "Descarregar",
    StringKey.PreparationCancelButton to "Cancelar",
    StringKey.PreparationDeleteConfirmTitle to "Eliminar a área?",
    StringKey.PreparationDeleteConfirmMessage to
        "Os mosaicos de mapa descarregados serão eliminados definitivamente.",
    StringKey.PreparationDeleteConfirmYes to "Sim",
    StringKey.PreparationDeleteConfirmNo to "Não",
    StringKey.PreparationDeleteContentDescription to "Eliminar área",
    StringKey.PreparationPauseContentDescription to "Pausar a transferência",
    StringKey.PreparationResumeContentDescription to "Retomar a transferência",
    StringKey.PreparationStatusDownloading to "A descarregar",
    StringKey.PreparationStatusPaused to "Em pausa",
    StringKey.PreparationStatusComplete to "Descarregada",
    StringKey.PreparationStatusError to "Erro",
    StringKey.PreparationSubtitle to "Descarregue a área do mapa visível para a usar sem Internet",
    StringKey.PreparationRetryContentDescription to "Tentar descarregar de novo",
    StringKey.MapTilesLoadFailed to
        "O fundo do mapa não carregou, mas a caminhada e os achados continuam a ser registados e guardados. " +
            "Sem ligação ao servidor:",
    StringKey.MapTilesLoadFailedPreparation to
        "O fundo do mapa não carregou e a área não pode ser descarregada agora. Sem ligação ao servidor:",
    StringKey.MapTilesLoadSlow to
        "O fundo do mapa está a carregar lentamente; a caminhada e os achados continuam a ser registados. O " +
            "servidor responde devagar:",
    StringKey.MapTilesLoadSlowPreparation to
        "O fundo do mapa está a carregar lentamente e descarregar a área também vai demorar. O servidor " +
            "responde devagar:",
    StringKey.MapTilesLoadFailedDismissContentDescription to "Fechar o aviso",
    StringKey.SettingsMapDataTitle to "Dados do mapa",
    StringKey.SettingsRefreshMapDataButton to "Atualizar os dados do mapa",
    StringKey.SettingsMapDataUpdateConfirmTitle to "Atualizar os dados do mapa?",
    StringKey.SettingsMapDataUpdateConfirmMessage to
        "Se o conteúdo do mapa tiver mudado, todas as áreas offline descarregadas serão " +
            "descarregadas de novo. Tem a certeza de que quer atualizar os dados do mapa?",
    StringKey.SettingsMapDataUpdateConfirmYes to "Sim",
    StringKey.SettingsMapDataUpdateConfirmNo to "Não",
    StringKey.SettingsMapDataRefreshError to "A atualização falhou — verifique a ligação à Internet",
    StringKey.SettingsMapDataRedownloadingPrefix to "Dados do mapa atualizados. A descarregar de novo",
    StringKey.SettingsMapDataRedownloadingSuffix to
        "— acompanhe o progresso em «Descarregar antecipadamente».",
    StringKey.SettingsMapDataRegionsCountZero to "áreas",
    StringKey.SettingsMapDataRegionsCountOne to "área",
    StringKey.SettingsMapDataRegionsCountTwo to "áreas",
    StringKey.SettingsMapDataRegionsCountFew to "áreas",
    StringKey.SettingsMapDataRegionsCountMany to "áreas",
    StringKey.SettingsMapDataRegionsCountOther to "áreas",
    StringKey.SettingsClearMapCacheButton to "Limpar a cache do mapa",
    StringKey.SettingsClearMapCacheConfirmTitle to "Limpar a cache do mapa?",
    StringKey.SettingsClearMapCacheConfirmMessage to
        "Limpar a cache do mapa remove as áreas do mapa que percorreu e que não foram guardadas " +
            "em «Descarregar antecipadamente». Tem a certeza de que quer limpar a cache?",
    StringKey.SettingsClearMapCacheConfirmYes to "Sim",
    StringKey.SettingsClearMapCacheConfirmNo to "Não",
    StringKey.SettingsMapCacheCleared to "Cache limpa",
)
