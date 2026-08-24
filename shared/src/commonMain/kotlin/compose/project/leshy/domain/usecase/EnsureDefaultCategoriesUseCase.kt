package compose.project.leshy.domain.usecase

import compose.project.leshy.data.catalog.CatalogEntry
import compose.project.leshy.data.catalog.CatalogSource
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.CategorySource
import compose.project.leshy.domain.repository.CatalogStateRepository
import compose.project.leshy.domain.repository.CategoryRepository

const val MISC_CATEGORY_NAME_KEY = "category_misc"

/** Catch-all species finds land in when their real species is deleted via
 * `DeleteUserSpeciesUseCase` — unlike [MISC_CATEGORY_NAME_KEY], this one is a normal, visible
 * catalog entry (tile feed, search, Filter), not a hidden FK-only bucket. */
const val UNKNOWN_MUSHROOM_NAME_KEY = "category_unknown_mushroom"

/**
 * Reconciles the `categories` table with the bundled catalog (`catalog.json`, 408 entries —
 * `.claude/plans/countries-and-languages.md`, Phase 2). Idempotent, safe to call from several
 * ViewModel `init`s, same as before.
 *
 * **New catalog species arrive un-picked and inactive** (`isPicked`/`isFilterEligible`/`isActive`
 * all false), not visible anywhere until the user picks a collection that contains them. Seeding
 * them "on" would mean an existing install's carefully curated 30-species tile feed silently
 * becoming 408 the moment the app updates; the collection picker is exactly the mechanism that's
 * supposed to decide this (plan §4.3). The two service rows below are the exception — they're not
 * catalog entries and are never offered in a collection.
 *
 * A row that already exists keeps `isActive`/`isPicked`/`isFilterEligible` untouched — all three
 * are user-owned — while its catalog-owned fields (colour, illustration, latin name, order) are
 * brought back in line, so regenerating the catalog reaches installed copies without a migration.
 */
class EnsureDefaultCategoriesUseCase(
    private val categoryRepository: CategoryRepository,
    private val catalogSource: CatalogSource,
    private val catalogStateRepository: CatalogStateRepository,
) {
    suspend operator fun invoke() {
        val entries = catalogSource.entries
        val desired = desiredCategories(entries)

        // Fast path: nothing to do unless the bundled catalog changed since the last full pass.
        // The row-count check is what makes the gate safe rather than merely fast — the seeded
        // version lives in DataStore and the rows in Room, and those two can get out of step
        // (a restored backup, a wiped database), in which case the full pass must still run.
        if (catalogStateRepository.getSeededCatalogVersion() == catalogSource.version &&
            categoryRepository.count() >= desired.size
        ) {
            return
        }

        val existingByNameKey = categoryRepository.getAll().associateBy { it.nameKey }
        val pending = desired.mapNotNull { canonical ->
            val existing = existingByNameKey[canonical.nameKey]
            when {
                existing == null -> canonical
                existing.matchesCatalog(canonical) -> null
                else -> canonical.copy(
                    id = existing.id,
                    isActive = existing.isActive,
                    isPicked = existing.isPicked,
                    isFilterEligible = existing.isFilterEligible,
                )
            }
        }
        if (pending.isNotEmpty()) categoryRepository.upsertAll(pending)
        catalogStateRepository.setSeededCatalogVersion(catalogSource.version)
    }
}

/**
 * Whether this row already carries [canonical]'s catalog-owned data. Compared field by field
 * rather than as whole objects on purpose: the user-owned flags and the row id must stay out of
 * it, and `customNames`/`iconFile` belong to non-catalog species only and are never written here.
 */
private fun Category.matchesCatalog(canonical: Category): Boolean =
    colorHex == canonical.colorHex &&
        iconRef == canonical.iconRef &&
        order == canonical.order &&
        scientificName == canonical.scientificName &&
        source == canonical.source

/**
 * The catalog as rows: its 408 entries in file order, then the two service categories. `order` is
 * purely visual (and barely used — both species lists sort alphabetically by display name), so
 * taking it from the file's order is enough; the service rows sit after the catalog
 * ("unknown mushroom" defaults to the end of the tile feed) and `category_misc` stays parked at
 * 999, hidden from every list by name key.
 */
private fun desiredCategories(entries: List<CatalogEntry>): List<Category> =
    entries.mapIndexed { index, entry ->
        Category(
            id = 0,
            nameKey = entry.key,
            colorHex = entry.color,
            iconRef = entry.image,
            order = index,
            isActive = false,
            isPicked = false,
            isFilterEligible = false,
            source = CategorySource.APP,
            scientificName = entry.sci,
        )
    } + listOf(
        Category(
            id = 0,
            nameKey = UNKNOWN_MUSHROOM_NAME_KEY,
            colorHex = "#9E9E8C",
            iconRef = "unknown_mushroom",
            order = entries.size,
            isActive = true,
            isPicked = true,
            isFilterEligible = true,
        ),
        Category(
            id = 0,
            nameKey = MISC_CATEGORY_NAME_KEY,
            colorHex = "#808080",
            iconRef = null,
            order = 999,
            isActive = false,
        ),
    )
