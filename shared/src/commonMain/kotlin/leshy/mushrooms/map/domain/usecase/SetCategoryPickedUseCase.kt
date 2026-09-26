package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.data.catalog.SpeciesSetsSource
import leshy.mushrooms.map.data.catalog.speciesSetCollectionNameKey
import leshy.mushrooms.map.domain.repository.CategoryRepository
import leshy.mushrooms.map.domain.repository.CollectionRepository

/** Single-species toggle in the collection picker (Settings and the first-run onboarding screen —
 * see `.claude/plans/mushroom-collections.md`). Writes
 * [Category.isPicked][leshy.mushrooms.map.domain.model.Category.isPicked] AND
 * [Category.isActive][leshy.mushrooms.map.domain.model.Category.isActive] together, then
 * recalculates `isFilterEligible` (same cascade as [SetCollectionPickedUseCase]'s bulk path).
 *
 * `isActive` can't be left to the recalculation cascade alone: that cascade only ever turns
 * `isActive` off when a species stops being `isFilterEligible`, and a species with an existing
 * find stays `isFilterEligible` forever (`isPicked || has finds`) — so unchecking an
 * already-recorded species in the picker was a silent no-op for Map/Record, which still read a
 * stale `isActive = true`. Writing `isActive` directly here makes the picker checkbox the source
 * of truth regardless of find history, same fix already applied to user species via
 * [ToggleUserSpeciesVisibilityUseCase]. */
class SetCategoryPickedUseCase(
    private val categoryRepository: CategoryRepository,
    private val collectionRepository: CollectionRepository,
    private val speciesSetsSource: SpeciesSetsSource,
    private val recalculateFilterEligibility: RecalculateFilterEligibilityUseCase,
) {
    suspend operator fun invoke(category: Category, picked: Boolean) {
        categoryRepository.upsert(category.copy(isPicked = picked, isActive = picked))
        if (picked) fileUnderFallbackSet(category)
        recalculateFilterEligibility()
    }

    /**
     * Вид, отмеченный поиском и не входящий НИ В ОДНУ подборку, приписывается к набору-приёмнику
     * редакции ([SpeciesSetsSource.pickedFallbackSetId], у российской это «Другие виды»).
     *
     * Без этого получалось неинтуитивно, и владелец сказал об этом сразу: отмеченный вид появлялся
     * в ленте «Записи», а на экране «Мои грибы» его не было ни в одной подборке — «как будто их и
     * нет». Причина в том, что экран рисует только членов подборок, а наборы редакции покрывают
     * каталог не целиком (168 ключей из 409), и до остальных ведёт только поиск.
     *
     * **Только на отметке и только для бесподборочных.** Вид, уже лежащий хоть в одной подборке
     * (любая страна мировой редакции, любой набор, своя подборка пользователя), не трогается —
     * иначе «Другие виды» собрали бы под себя половину каталога. В мировой редакции код инертен
     * дважды: набора-приёмника там нет, и бесподборочных каталожных видов там не бывает.
     *
     * **Снятие галочки членство НЕ убирает** — и это осознанно: вид остаётся видимым в «Других
     * видах» уже снятым, то есть его можно отметить снова тем же движением, а не искать поиском
     * заново. Это же делает набор осмысленным списком «что я сюда добавил».
     */
    private suspend fun fileUnderFallbackSet(category: Category) {
        val setId = speciesSetsSource.pickedFallbackSetId ?: return
        if (collectionRepository.getMemberCollectionIds(category.id).isNotEmpty()) return
        val collection = collectionRepository.getByNameKey(speciesSetCollectionNameKey(setId)) ?: return
        collectionRepository.addMember(categoryId = category.id, collectionId = collection.id)
    }
}
