package leshy.mushrooms.map.domain.usecase

import kotlinx.coroutines.flow.first
import leshy.mushrooms.map.domain.repository.CategoryRepository
import leshy.mushrooms.map.domain.repository.CollectionRepository

/** Bulk-writes [Category.isPicked][leshy.mushrooms.map.domain.model.Category.isPicked] AND
 * [Category.isActive][leshy.mushrooms.map.domain.model.Category.isActive] for every member of a
 * collection — the "select whole collection" shortcut in the collection picker (Settings and the
 * first-run onboarding screen share this use case, see `.claude/plans/mushroom-collections.md`).
 * Writes `isActive` directly rather than leaving it to the recalculation cascade alone — see
 * [SetCategoryPickedUseCase] for why (a member with an existing find stays `isFilterEligible`
 * forever, so the cascade alone can never turn its `isActive` off). Recalculates
 * `isFilterEligible` afterward so every picker write site gets that part for free, rather than
 * each caller remembering to chain it (see [RecalculateFilterEligibilityUseCase]).
 *
 * ## Снятие галочки не трогает виды, которые держит другая отмеченная подборка
 *
 * Подборки **пересекаются**, и сильно: у соседних стран общего 80% и больше, а наборы российской
 * редакции (`SpeciesSetsSource`) намеренно кладут вид во все леса, где он растёт. Без оговорки
 * ниже снятие одной галочки уносило из ленты виды, которые человек оставил отмеченными другой:
 * снял «Беларусь» — пропали белый гриб и лисичка, хотя «Россия» осталась отмеченной. Дефект
 * общий для обеих редакций, найден при разборе наборов 2026-09-26 и чинится в обеих (решение
 * владельца).
 *
 * **Что считается «другой отмеченной подборкой»: та, у которой отмечены ВСЕ виды** — то же
 * определение, по которому рисует себя галочка пикера
 * ([leshy.mushrooms.map.presentation.CollectionPickState.ALL]), и другого здесь быть не должно:
 * человек рассуждает о том, что видит. Частично отмеченная (`SOME`) подборка вид не держит — иначе
 * снятие галочки не делало бы вообще ничего, ведь у соседней страны и так отмечено 80% её видов
 * просто потому, что они общие.
 */
class SetCollectionPickedUseCase(
    private val collectionRepository: CollectionRepository,
    private val categoryRepository: CategoryRepository,
    private val recalculateFilterEligibility: RecalculateFilterEligibilityUseCase,
) {
    suspend operator fun invoke(collectionId: Long, picked: Boolean) {
        val heldByOthers = if (picked) emptySet() else categoriesHeldByFullyPickedCollections(collectionId)
        collectionRepository.getMemberCategoryIds(collectionId).forEach { categoryId ->
            if (categoryId in heldByOthers) return@forEach
            val category = categoryRepository.getById(categoryId) ?: return@forEach
            if (category.isPicked != picked || category.isActive != picked) {
                categoryRepository.upsert(category.copy(isPicked = picked, isActive = picked))
            }
        }
        recalculateFilterEligibility()
    }

    /** Виды всех ПОЛНОСТЬЮ отмеченных подборок, кроме [excludedCollectionId]. */
    private suspend fun categoriesHeldByFullyPickedCollections(excludedCollectionId: Long): Set<Long> {
        val memberships = collectionRepository.observeAllMemberships().first()
        val pickedCategoryIds = categoryRepository.getAll().asSequence()
            .filter { it.isPicked }
            .map { it.id }
            .toSet()
        return memberships.asSequence()
            .filter { it.collectionId != excludedCollectionId }
            .groupBy { it.collectionId }
            .asSequence()
            .filter { (_, members) -> members.all { it.categoryId in pickedCategoryIds } }
            .flatMap { (_, members) -> members.asSequence().map { it.categoryId } }
            .toSet()
    }
}
