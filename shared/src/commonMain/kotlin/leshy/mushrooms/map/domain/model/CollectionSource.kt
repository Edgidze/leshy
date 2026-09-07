package leshy.mushrooms.map.domain.model

/**
 * Where a mushroom collection came from — the exact counterpart of [CategorySource], and for the
 * same reasons (see `.claude/plans/user-collections.md`).
 *
 * [COUNTRY] rows are reconciled with `countries.json` on every launch by
 * `EnsureDefaultCollectionsUseCase` and are not editable by anyone; the other two are the user's
 * own and carry their name as literal text in [Collection.name]. [IMPORTED] is kept apart from
 * [USER] for the same reason [CategorySource.IMPORTED] is: provenance, not permission.
 */
enum class CollectionSource {
    /** One of the bundled per-country presets. */
    COUNTRY,

    /** Created by the user on this device, in the dialog that follows saving a species. */
    USER,

    /** Arrived with an imported archive and didn't merge into a local collection. */
    IMPORTED,
}
