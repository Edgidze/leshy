package compose.project.leshy.i18n

import androidx.compose.runtime.Composable
import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.domain.model.Category

/**
 * Display name of [category] — the overload every UI call site should use. A catalog species
 * resolves through its `nameKey` as before; a user-created/imported one carries its own
 * [Category.customNames] instead, since its `nameKey` is a generated technical id
 * (`user_<millis>_<random>`) that no `StringKey` will ever match.
 */
@Composable
fun categoryDisplayName(category: Category): String =
    customDisplayName(category, LocalAppLanguage.current) ?: categoryDisplayName(category.nameKey)

/** Non-composable counterpart of [categoryDisplayName], for contexts like sorting in a ViewModel. */
fun categoryDisplayName(category: Category, language: AppLanguage): String =
    customDisplayName(category, language) ?: categoryDisplayName(category.nameKey, language)

/**
 * The user-entered name for [language], falling back across the other language and then the Latin
 * name: a species named in Russian only must still show *something* readable after switching the
 * app to English, and vice versa. Null means "nothing user-entered here", i.e. a catalog species.
 */
private fun customDisplayName(category: Category, language: AppLanguage): String? =
    category.customNames[language]?.takeIf { it.isNotBlank() }
        ?: category.customNames.values.firstOrNull { it.isNotBlank() }
        ?: category.scientificName?.takeIf { it.isNotBlank() }

/** Resolves a [compose.project.leshy.domain.model.Category.nameKey] to a localized display name. */
@Composable
fun categoryDisplayName(nameKey: String): String =
    categoryNameStringKey(nameKey)?.let { stringResource(it) } ?: nameKey

/** Non-composable counterpart of [categoryDisplayName], for contexts like sorting in a ViewModel. */
fun categoryDisplayName(nameKey: String, language: AppLanguage): String =
    categoryNameStringKey(nameKey)?.let { string(it, language) } ?: nameKey

private fun categoryNameStringKey(nameKey: String): StringKey? = when (nameKey) {
    "category_boletus_edulis" -> StringKey.CategoryBoletusEdulis
    "category_leccinum_aurantiacum" -> StringKey.CategoryLeccinumAurantiacum
    "category_leccinum_scabrum" -> StringKey.CategoryLeccinumScabrum
    "category_cantharellus_cibarius" -> StringKey.CategoryCantharellusCibarius
    "category_lactarius_deliciosus" -> StringKey.CategoryLactariusDeliciosus
    "category_suillus_luteus" -> StringKey.CategorySuillusLuteus
    "category_armillaria_mellea" -> StringKey.CategoryArmillariaMellea
    "category_macrolepiota_procera" -> StringKey.CategoryMacrolepiotaProcera
    "category_craterellus_tubaeformis" -> StringKey.CategoryCraterellusTubaeformis
    "category_imleria_badia" -> StringKey.CategoryImleriaBadia
    "category_lactarius_resimus" -> StringKey.CategoryLactariusResimus
    "category_lactarius_torminosus" -> StringKey.CategoryLactariusTorminosus
    "category_russula_species" -> StringKey.CategoryRussulaSpecies
    "category_pleurotus_ostreatus" -> StringKey.CategoryPleurotusOstreatus
    "category_agaricus_species" -> StringKey.CategoryAgaricusSpecies
    "category_morchella_species" -> StringKey.CategoryMorchellaSpecies
    "category_lycoperdon_calvatia_species" -> StringKey.CategoryLycoperdonCalvatiaSpecies
    "category_coprinus_comatus" -> StringKey.CategoryCoprinusComatus
    "category_hygrophoropsis_aurantiaca" -> StringKey.CategoryHygrophoropsisAurantiaca
    "category_paxillus_involutus" -> StringKey.CategoryPaxillusInvolutus
    "category_amanita_phalloides" -> StringKey.CategoryAmanitaPhalloides
    "category_amanita_virosa" -> StringKey.CategoryAmanitaVirosa
    "category_galerina_marginata" -> StringKey.CategoryGalerinaMarginata
    "category_amanita_muscaria" -> StringKey.CategoryAmanitaMuscaria
    "category_amanita_pantherina" -> StringKey.CategoryAmanitaPantherina
    "category_gyromitra_species" -> StringKey.CategoryGyromitraSpecies
    "category_xerocomus_subtomentosus_group" -> StringKey.CategoryXerocomusSubtomentosusGroup
    "category_suillus_bovinus" -> StringKey.CategorySuillusBovinus
    "category_amanita_vaginata" -> StringKey.CategoryAmanitaVaginata
    "category_russula_foetens" -> StringKey.CategoryRussulaFoetens
    "category_misc" -> StringKey.CategoryMisc
    else -> null
}
