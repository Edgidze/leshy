package leshy.mushrooms.map.domain.repository

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

    /**
     * Ключи видов, по которым разовая уборка снятых дублей уже проходила
     * ([leshy.mushrooms.map.domain.usecase.RetireDroppedSpeciesUseCase]).
     *
     * Список ключей, а не «версия», и это условие: уборка снимает галочку, а человек вправе
     * поставить её обратно — найдя вид поиском. Гейт по версии файла снял бы её снова при
     * следующей же правке наборов, то есть приложение спорило бы с человеком. Отметка «по этому
     * ключу мы уже прошли» такого спора не допускает по построению.
     */
    suspend fun getRetiredSpeciesKeys(): Set<String>
    suspend fun addRetiredSpeciesKeys(keys: Set<String>)
}
