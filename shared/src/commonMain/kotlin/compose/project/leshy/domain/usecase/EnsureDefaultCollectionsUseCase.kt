package compose.project.leshy.domain.usecase

import compose.project.leshy.domain.model.Collection
import compose.project.leshy.domain.repository.CategoryRepository
import compose.project.leshy.domain.repository.CollectionRepository

private data class DemoCollection(val nameKey: String, val order: Int, val memberNameKeys: List<String>)

// Demo-only grouping of the existing 30-species catalog into arbitrary buckets, just to exercise
// the collections plumbing end-to-end before real per-country data exists — see
// .claude/plans/mushroom-collections.md, Phase 5 replaces this with real data. Two members are
// deliberately shared between adjacent buckets (imleria_badia/lactarius_deliciosus,
// russula_species/agaricus_species) so the many-to-many join is exercised now rather than only
// once real overlapping country data shows up.
private val DEMO_COLLECTIONS = listOf(
    DemoCollection(
        nameKey = "collection_demo_north",
        order = 0,
        memberNameKeys = listOf(
            "category_boletus_edulis",
            "category_pleurotus_ostreatus",
            "category_macrolepiota_procera",
            "category_suillus_bovinus",
            "category_cantharellus_cibarius",
            "category_suillus_luteus",
            "category_xerocomus_subtomentosus_group",
            "category_coprinus_comatus",
            "category_leccinum_scabrum",
            "category_leccinum_aurantiacum",
            "category_imleria_badia",
            "category_lactarius_deliciosus",
        ),
    ),
    DemoCollection(
        nameKey = "collection_demo_south",
        order = 1,
        memberNameKeys = listOf(
            "category_imleria_badia",
            "category_lactarius_deliciosus",
            "category_craterellus_tubaeformis",
            "category_russula_foetens",
            "category_lactarius_torminosus",
            "category_lactarius_resimus",
            "category_lycoperdon_calvatia_species",
            "category_armillaria_mellea",
            "category_amanita_vaginata",
            "category_morchella_species",
            "category_russula_species",
            "category_agaricus_species",
        ),
    ),
    DemoCollection(
        nameKey = "collection_demo_east",
        order = 2,
        memberNameKeys = listOf(
            "category_russula_species",
            "category_agaricus_species",
            "category_amanita_virosa",
            "category_amanita_phalloides",
            "category_galerina_marginata",
            "category_hygrophoropsis_aurantiaca",
            "category_amanita_muscaria",
            "category_amanita_pantherina",
            "category_paxillus_involutus",
            "category_gyromitra_species",
        ),
    ),
)

class EnsureDefaultCollectionsUseCase(
    private val collectionRepository: CollectionRepository,
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke() {
        DEMO_COLLECTIONS.forEach { demo ->
            val existing = collectionRepository.getByNameKey(demo.nameKey)
            val collectionId = when {
                existing == null ->
                    collectionRepository.upsert(Collection(id = 0, nameKey = demo.nameKey, order = demo.order))
                existing.order != demo.order -> collectionRepository.upsert(existing.copy(order = demo.order))
                else -> existing.id
            }
            demo.memberNameKeys.forEach { categoryNameKey ->
                val category = categoryRepository.getByNameKey(categoryNameKey) ?: return@forEach
                collectionRepository.addMember(category.id, collectionId)
            }
        }
    }
}
