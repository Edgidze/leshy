package compose.project.leshy.domain.model

data class Category(
    val id: Long,
    val nameKey: String,
    val colorHex: String,
    val iconRef: String?,
    val order: Int,
    val isActive: Boolean,
    val edibilityStatus: EdibilityStatus,
    /** User picked this species via the collection picker (Settings / first-run) — see
     * `.claude/plans/mushroom-collections.md`. Independent of [isActive]: this only feeds into
     * which species are *offered* on the Filter screen, [isActive] still governs what's actually
     * shown on Map/Record. */
    val isPicked: Boolean = true,
    /** Denormalized: `isPicked || has an existing FieldMark`, recomputed whenever the collection
     * picker saves (not on every read — see the plan doc for why that's safe). Drives the Filter
     * screen's species list directly, no join needed at read time. */
    val isFilterEligible: Boolean = true,
)
