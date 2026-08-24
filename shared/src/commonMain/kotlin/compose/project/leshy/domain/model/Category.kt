package compose.project.leshy.domain.model

data class Category(
    val id: Long,
    val nameKey: String,
    val colorHex: String,
    val iconRef: String?,
    val order: Int,
    val isActive: Boolean,
    /** User picked this species via the collection picker (Settings / first-run) — see
     * `.claude/plans/mushroom-collections.md`. Independent of [isActive]: this only feeds into
     * which species are *offered* on the Filter screen, [isActive] still governs what's actually
     * shown on Map/Record. */
    val isPicked: Boolean = true,
    /** Denormalized: `isPicked || has an existing FieldMark`, recomputed whenever the collection
     * picker saves (not on every read — see the plan doc for why that's safe). Drives the Filter
     * screen's species list directly, no join needed at read time. */
    val isFilterEligible: Boolean = true,
    /** See `.claude/plans/user-mushrooms.md`. Everything below this line only ever applies to
     * non-[CategorySource.APP] species: catalog ones take their name from [nameKey] via
     * `StringKey` and their illustration from [iconRef]. */
    val source: CategorySource = CategorySource.APP,
    /** User-entered name per language, empty for catalog species. Not every language is
     * necessarily filled in — `categoryDisplayName` falls back across them. */
    val customNames: Map<AppLanguage, String> = emptyMap(),
    /** Latin name, shown as-is in every language. */
    val scientificName: String? = null,
    /** *File name* (not an absolute path) of this species' illustration inside the app's own photo
     * storage — resolved through `PhotoStorage.resolvePath` at read time. Deliberately unlike
     * `FieldMark.photoPath`/`Walk.thumbnailPath`, which store absolute paths and therefore need
     * `RepairPhotoPathsUseCase` to survive an iOS container UUID change. */
    val iconFile: String? = null,
)
