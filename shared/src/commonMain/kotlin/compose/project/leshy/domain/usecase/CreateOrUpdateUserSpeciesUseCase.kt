package compose.project.leshy.domain.usecase

import compose.project.leshy.data.platform.currentTimeMillis
import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.CategorySource
import compose.project.leshy.domain.repository.CategoryRepository
import compose.project.leshy.domain.util.scientificNameFallback
import kotlin.random.Random

/** Between the bundled catalog (`order` 0–29) and the service `category_misc` (`order` 999) — same
 * band `DebugUserCategoryUseCase` used to occupy. Also used by [ImportDataUseCase] for species
 * created from an archive — not `private` for that reason. */
internal const val USER_SPECIES_ORDER = 500

/** `nameKey` is the only identifier the export/import merge (Phase 6 of
 * `.claude/plans/user-mushrooms.md`) is allowed to key on — never the display name or scientific
 * name, both of which two different user species can easily share (see that plan's note on this).
 * Random suffix keeps it globally unique without a round trip to the database. */
private fun generateUserNameKey(): String = "user_${currentTimeMillis()}_${Random.nextInt(100_000, 999_999)}"

/**
 * Creates a new user species, or edits an existing [CategorySource.USER]/[CategorySource.IMPORTED]
 * one — same form, same use case, `existing == null` is the only branch. Editing never touches
 * [Category.source]: that field is provenance, not a permission, see the plan's Phase 4 note.
 *
 * [name] is the current-language display name and is required by the caller (the form disables Save
 * until it's non-blank) — this use case doesn't re-validate that. [scientificNameInput] is whatever
 * the user typed in that optional field; a blank value is replaced with [scientificNameFallback] so
 * a user species practically never ends up with a null scientific name, which matters because
 * `categoryDisplayName` falls back to it whenever the *other* language has no name of its own.
 */
class CreateOrUpdateUserSpeciesUseCase(
    private val categoryRepository: CategoryRepository,
    private val saveCategoryIcon: SaveCategoryIconUseCase,
) {
    suspend operator fun invoke(
        existing: Category?,
        name: String,
        scientificNameInput: String?,
        language: AppLanguage,
        colorHex: String,
        iconPngBytes: ByteArray?,
    ): Category {
        val scientificName = scientificNameInput?.trim()?.takeIf { it.isNotEmpty() }
            ?: scientificNameFallback(name, language)
        val base = existing ?: Category(
            id = 0,
            nameKey = generateUserNameKey(),
            colorHex = colorHex,
            iconRef = null,
            order = USER_SPECIES_ORDER,
            isActive = true,
            isPicked = true,
            isFilterEligible = true,
            source = CategorySource.USER,
        )
        val updated = base.copy(
            colorHex = colorHex,
            scientificName = scientificName,
            customNames = base.customNames + (language to name),
        )
        val savedId = categoryRepository.upsert(updated)
        val saved = if (base.id == 0L) updated.copy(id = savedId) else updated
        return if (iconPngBytes != null) saveCategoryIcon(saved, iconPngBytes) else saved
    }
}
