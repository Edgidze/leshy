package compose.project.leshy.domain.usecase

import compose.project.leshy.data.catalog.CountriesSource
import compose.project.leshy.data.catalog.countryCodeForCollectionNameKey
import compose.project.leshy.data.catalog.countryCollectionNameKey
import compose.project.leshy.domain.model.CategoryCollectionMembership
import compose.project.leshy.domain.model.Collection
import compose.project.leshy.domain.repository.CatalogStateRepository
import compose.project.leshy.domain.repository.CategoryRepository
import compose.project.leshy.domain.repository.CollectionRepository

/**
 * Reconciles the `collections` table with the bundled per-country presets (`countries.json`, 33
 * countries — `.claude/plans/countries-and-languages.md`, Phase 3). Replaces the old hardcoded
 * 3-bucket demo seeding; same batch/gate shape as `EnsureDefaultCategoriesUseCase`.
 *
 * Membership is always re-inserted in full rather than diffed against what's already there —
 * `CollectionDao.insertMembers` uses `OnConflictStrategy.IGNORE`, so handing it the complete desired
 * list every time this runs is idempotent by construction and cheap (~1650 rows, one transaction),
 * without needing to compute a per-row diff. A country losing a species between catalog
 * regenerations does *not* remove the stale membership row — same additive-only philosophy as
 * `EnsureDefaultCategoriesUseCase`, which never deletes rows either.
 */
class EnsureDefaultCollectionsUseCase(
    private val collectionRepository: CollectionRepository,
    private val categoryRepository: CategoryRepository,
    private val countriesSource: CountriesSource,
    private val catalogStateRepository: CatalogStateRepository,
) {
    suspend operator fun invoke() {
        val countries = countriesSource.entries

        // Fast path — see EnsureDefaultCategoriesUseCase for why the row-count check matters too.
        if (catalogStateRepository.getSeededCountriesVersion() == countriesSource.version &&
            collectionRepository.count() >= countries.size
        ) {
            return
        }

        val desired = countries.mapIndexed { index, country ->
            Collection(id = 0, nameKey = countryCollectionNameKey(country.code), order = index)
        }
        val existingByNameKey = collectionRepository.getAll().associateBy { it.nameKey }
        val pending = desired.mapNotNull { canonical ->
            val existing = existingByNameKey[canonical.nameKey]
            when {
                existing == null -> canonical
                existing.order == canonical.order -> null
                else -> canonical.copy(id = existing.id)
            }
        }
        if (pending.isNotEmpty()) collectionRepository.upsertAll(pending)

        val collectionIdByCode = collectionRepository.getAll()
            .mapNotNull { collection -> countryCodeForCollectionNameKey(collection.nameKey)?.let { it to collection.id } }
            .toMap()
        val categoryIdByKey = categoryRepository.getAll().associateBy { it.nameKey }.mapValues { it.value.id }
        val memberships = countries.flatMap { country ->
            val collectionId = collectionIdByCode[country.code] ?: return@flatMap emptyList()
            country.keys.mapNotNull { key ->
                categoryIdByKey[key]?.let { categoryId ->
                    CategoryCollectionMembership(categoryId = categoryId, collectionId = collectionId)
                }
            }
        }
        if (memberships.isNotEmpty()) collectionRepository.addMembers(memberships)

        catalogStateRepository.setSeededCountriesVersion(countriesSource.version)
    }
}
