package compose.project.leshy.domain.repository

/**
 * Which build of the bundled catalog this install has already been seeded from — the gate that
 * keeps `EnsureDefaultCategoriesUseCase` from diffing 408 rows on every single launch. Not a user
 * setting (hence its own repository rather than `SettingsRepository`), just persisted app state,
 * stored in the same DataStore as the onboarding flag.
 */
interface CatalogStateRepository {
    /** `null` when nothing has been seeded yet (fresh install, or cleared app data). */
    suspend fun getSeededCatalogVersion(): Int?
    suspend fun setSeededCatalogVersion(version: Int)

    /** Same gate as [getSeededCatalogVersion]/[setSeededCatalogVersion], for `countries.json` —
     * keeps `EnsureDefaultCollectionsUseCase` from diffing 33 collections/~1650 memberships on every
     * launch (`.claude/plans/countries-and-languages.md`, Phase 3). */
    suspend fun getSeededCountriesVersion(): Int?
    suspend fun setSeededCountriesVersion(version: Int)
}
