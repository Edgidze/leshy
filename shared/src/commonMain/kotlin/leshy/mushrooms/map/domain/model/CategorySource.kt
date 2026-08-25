package leshy.mushrooms.map.domain.model

/**
 * Where a mushroom species came from — see `.claude/plans/user-mushrooms.md`.
 *
 * Almost every caller only cares whether a species is [APP] or not (that's what decides whether it
 * shows up in the "my mushrooms" section, is editable, and gets written into an export archive).
 * [IMPORTED] is kept separate from [USER] for two reasons: the merge rule on import needs to tell
 * "a species this device created" from "a species that arrived in someone else's archive", and the
 * list screen labels the latter as such instead of pretending the user made it.
 */
enum class CategorySource {
    /** Part of the bundled catalog, reseeded on every launch by `EnsureDefaultCategoriesUseCase`. */
    APP,

    /** Created by the user on this device. */
    USER,

    /** Arrived with an imported archive. Stays [IMPORTED] even after the user edits it — this is
     * provenance, not a permission. */
    IMPORTED,
}
