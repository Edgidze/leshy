package compose.project.leshy.domain.usecase

import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.repository.CategoryRepository

const val MISC_CATEGORY_NAME_KEY = "category_misc"

/** Catch-all species finds land in when their real species is deleted via
 * `DeleteUserSpeciesUseCase` — unlike [MISC_CATEGORY_NAME_KEY], this one is a normal, visible
 * catalog entry (tile feed, search, Filter), not a hidden FK-only bucket. */
const val UNKNOWN_MUSHROOM_NAME_KEY = "category_unknown_mushroom"

// Order is purely visual (list position on Record/Filter) — kept as originally seeded.
private val DEFAULT_CATEGORIES = listOf(
    Category(id = 0, nameKey = "category_boletus_edulis", colorHex = "#A95620", iconRef = "boletus_edulis", order = 0, isActive = true),
    Category(id = 0, nameKey = "category_pleurotus_ostreatus", colorHex = "#BBAA93", iconRef = "pleurotus_ostreatus", order = 1, isActive = true),
    Category(id = 0, nameKey = "category_macrolepiota_procera", colorHex = "#B08A5C", iconRef = "macrolepiota_procera", order = 2, isActive = true),
    Category(id = 0, nameKey = "category_suillus_bovinus", colorHex = "#C98923", iconRef = "suillus_bovinus", order = 3, isActive = true),
    Category(id = 0, nameKey = "category_cantharellus_cibarius", colorHex = "#E69A1F", iconRef = "cantharellus_cibarius", order = 4, isActive = true),
    Category(id = 0, nameKey = "category_suillus_luteus", colorHex = "#7F2F14", iconRef = "suillus_luteus", order = 5, isActive = true),
    Category(id = 0, nameKey = "category_xerocomus_subtomentosus_group", colorHex = "#8C8A2A", iconRef = "xerocomus_subtomentosus_group", order = 6, isActive = true),
    Category(id = 0, nameKey = "category_coprinus_comatus", colorHex = "#E6E0D4", iconRef = "coprinus_comatus", order = 7, isActive = true),
    Category(id = 0, nameKey = "category_leccinum_scabrum", colorHex = "#6D5A44", iconRef = "leccinum_scabrum", order = 8, isActive = true),
    Category(id = 0, nameKey = "category_leccinum_aurantiacum", colorHex = "#E85A1A", iconRef = "leccinum_aurantiacum", order = 9, isActive = true),
    Category(id = 0, nameKey = "category_imleria_badia", colorHex = "#8B3F1E", iconRef = "imleria_badia", order = 10, isActive = true),
    Category(id = 0, nameKey = "category_lactarius_deliciosus", colorHex = "#C96517", iconRef = "lactarius_deliciosus", order = 11, isActive = true),
    Category(id = 0, nameKey = "category_craterellus_tubaeformis", colorHex = "#7A6A3A", iconRef = "craterellus_tubaeformis", order = 12, isActive = true),
    Category(id = 0, nameKey = "category_russula_foetens", colorHex = "#B77A27", iconRef = "russula_foetens", order = 13, isActive = true),
    Category(id = 0, nameKey = "category_lactarius_torminosus", colorHex = "#D69CA0", iconRef = "lactarius_torminosus", order = 14, isActive = true),
    Category(id = 0, nameKey = "category_lactarius_resimus", colorHex = "#D8C9AD", iconRef = "lactarius_resimus", order = 15, isActive = true),
    Category(id = 0, nameKey = "category_lycoperdon_calvatia_species", colorHex = "#E7E1D5", iconRef = "lycoperdon_calvatia_species", order = 16, isActive = true),
    Category(id = 0, nameKey = "category_armillaria_mellea", colorHex = "#C98932", iconRef = "armillaria_mellea", order = 17, isActive = true),
    Category(id = 0, nameKey = "category_amanita_vaginata", colorHex = "#8A8177", iconRef = "amanita_vaginata", order = 18, isActive = true),
    Category(id = 0, nameKey = "category_morchella_species", colorHex = "#936321", iconRef = "morchella_species", order = 19, isActive = true),
    Category(id = 0, nameKey = "category_russula_species", colorHex = "#A83246", iconRef = "russula_species", order = 20, isActive = true),
    Category(id = 0, nameKey = "category_agaricus_species", colorHex = "#E8DED0", iconRef = "agaricus_species", order = 21, isActive = true),
    Category(id = 0, nameKey = "category_amanita_virosa", colorHex = "#F2F0EA", iconRef = "amanita_virosa", order = 22, isActive = true),
    Category(id = 0, nameKey = "category_amanita_phalloides", colorHex = "#B7A94C", iconRef = "amanita_phalloides", order = 23, isActive = true),
    Category(id = 0, nameKey = "category_galerina_marginata", colorHex = "#A66B2A", iconRef = "galerina_marginata", order = 24, isActive = true),
    Category(id = 0, nameKey = "category_hygrophoropsis_aurantiaca", colorHex = "#E87517", iconRef = "hygrophoropsis_aurantiaca", order = 25, isActive = true),
    Category(id = 0, nameKey = "category_amanita_muscaria", colorHex = "#D73B21", iconRef = "amanita_muscaria", order = 26, isActive = true),
    Category(id = 0, nameKey = "category_amanita_pantherina", colorHex = "#6E5138", iconRef = "amanita_pantherina", order = 27, isActive = true),
    Category(id = 0, nameKey = "category_paxillus_involutus", colorHex = "#7A4B31", iconRef = "paxillus_involutus", order = 28, isActive = true),
    Category(id = 0, nameKey = "category_gyromitra_species", colorHex = "#8F3B2E", iconRef = "gyromitra_species", order = 29, isActive = true),
    Category(id = 0, nameKey = UNKNOWN_MUSHROOM_NAME_KEY, colorHex = "#9E9E8C", iconRef = "unknown_mushroom", order = 30, isActive = true),
    Category(id = 0, nameKey = MISC_CATEGORY_NAME_KEY, colorHex = "#808080", iconRef = null, order = 999, isActive = false),
)

class EnsureDefaultCategoriesUseCase(
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke() {
        DEFAULT_CATEGORIES.forEach { category ->
            val existing = categoryRepository.getByNameKey(category.nameKey)
            when {
                existing == null -> categoryRepository.upsert(category)
                // Keep already-seeded installs in sync with catalog data (order,
                // color, icon) on every launch, without a schema
                // migration — but never touch isActive/isPicked/isFilterEligible,
                // those are user-owned (see .claude/plans/mushroom-collections.md).
                // User-created and imported species are unreachable from here by
                // construction: this only ever looks up the catalog's own nameKeys,
                // and theirs are generated (`user_...`, see user-mushrooms.md).
                existing.copy(
                    isActive = category.isActive,
                    isPicked = category.isPicked,
                    isFilterEligible = category.isFilterEligible,
                ) != category ->
                    categoryRepository.upsert(
                        category.copy(
                            id = existing.id,
                            isActive = existing.isActive,
                            isPicked = existing.isPicked,
                            isFilterEligible = existing.isFilterEligible,
                        ),
                    )
            }
        }
    }
}
