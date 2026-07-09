package compose.project.leshy.i18n

import androidx.compose.runtime.Composable

/** Resolves a [compose.project.leshy.domain.model.Category.nameKey] to a localized display name. */
@Composable
fun categoryDisplayName(nameKey: String): String = when (nameKey) {
    "category_boletus_edulis" -> stringResource(StringKey.CategoryBoletusEdulis)
    "category_leccinum_aurantiacum" -> stringResource(StringKey.CategoryLeccinumAurantiacum)
    "category_leccinum_scabrum" -> stringResource(StringKey.CategoryLeccinumScabrum)
    "category_cantharellus_cibarius" -> stringResource(StringKey.CategoryCantharellusCibarius)
    "category_lactarius_deliciosus" -> stringResource(StringKey.CategoryLactariusDeliciosus)
    "category_suillus_luteus" -> stringResource(StringKey.CategorySuillusLuteus)
    "category_armillaria_mellea" -> stringResource(StringKey.CategoryArmillariaMellea)
    "category_macrolepiota_procera" -> stringResource(StringKey.CategoryMacrolepiotaProcera)
    "category_craterellus_tubaeformis" -> stringResource(StringKey.CategoryCraterellusTubaeformis)
    "category_imleria_badia" -> stringResource(StringKey.CategoryImleriaBadia)
    "category_lactarius_resimus" -> stringResource(StringKey.CategoryLactariusResimus)
    "category_lactarius_torminosus" -> stringResource(StringKey.CategoryLactariusTorminosus)
    "category_russula_species" -> stringResource(StringKey.CategoryRussulaSpecies)
    "category_pleurotus_ostreatus" -> stringResource(StringKey.CategoryPleurotusOstreatus)
    "category_agaricus_species" -> stringResource(StringKey.CategoryAgaricusSpecies)
    "category_morchella_species" -> stringResource(StringKey.CategoryMorchellaSpecies)
    "category_lycoperdon_calvatia_species" -> stringResource(StringKey.CategoryLycoperdonCalvatiaSpecies)
    "category_coprinus_comatus" -> stringResource(StringKey.CategoryCoprinusComatus)
    "category_hygrophoropsis_aurantiaca" -> stringResource(StringKey.CategoryHygrophoropsisAurantiaca)
    "category_paxillus_involutus" -> stringResource(StringKey.CategoryPaxillusInvolutus)
    "category_amanita_phalloides" -> stringResource(StringKey.CategoryAmanitaPhalloides)
    "category_amanita_virosa" -> stringResource(StringKey.CategoryAmanitaVirosa)
    "category_galerina_marginata" -> stringResource(StringKey.CategoryGalerinaMarginata)
    "category_amanita_muscaria" -> stringResource(StringKey.CategoryAmanitaMuscaria)
    "category_amanita_pantherina" -> stringResource(StringKey.CategoryAmanitaPantherina)
    "category_gyromitra_species" -> stringResource(StringKey.CategoryGyromitraSpecies)
    "category_xerocomus_subtomentosus_group" -> stringResource(StringKey.CategoryXerocomusSubtomentosusGroup)
    "category_suillus_bovinus" -> stringResource(StringKey.CategorySuillusBovinus)
    "category_amanita_vaginata" -> stringResource(StringKey.CategoryAmanitaVaginata)
    "category_russula_foetens" -> stringResource(StringKey.CategoryRussulaFoetens)
    "category_misc" -> stringResource(StringKey.CategoryMisc)
    else -> nameKey
}
