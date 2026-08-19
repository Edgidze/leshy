package compose.project.leshy.domain.usecase

import compose.project.leshy.data.platform.PhotoStorage
import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.CategorySource
import compose.project.leshy.domain.model.EdibilityStatus
import compose.project.leshy.domain.repository.CategoryRepository
import okio.FileSystem
import okio.Path.Companion.toPath

/** Fixed `nameKey` of the throwaway species this use case owns — deliberately NOT the real
 * `user_<millis>_<random>` scheme (Phase 4), so pressing the debug button twice can't leave two
 * indistinguishable test species behind. */
const val DEBUG_USER_CATEGORY_NAME_KEY = "user_debug_sample"

/**
 * TEMPORARY scaffolding for Phase 1 of `.claude/plans/user-mushrooms.md` — **delete together with
 * its Settings button once Phase 4 lands the real "My mushrooms" screen.**
 *
 * Phase 1 rewires every place that renders a species' name/illustration onto `CategoryIcon` /
 * `categoryDisplayName(category)`, but the screens that *create* a user species only arrive in
 * Phases 2–4, so there would otherwise be nothing on a device to point that new code at. This
 * seeds exactly one `USER` species from [iconBytes] (the caller hands over a bundled catalog
 * illustration — Phase 2 is what replaces that with camera/gallery), and afterwards toggles it
 * between shown and hidden so the test species can be cleared out of Record/Map/Filter again
 * without a delete path (there isn't one, by design — see the plan's "only hiding" section).
 */
class DebugUserCategoryUseCase(
    private val categoryRepository: CategoryRepository,
    private val setCategoryPicked: SetCategoryPickedUseCase,
    private val photoStorage: PhotoStorage,
    private val fileSystem: FileSystem = FileSystem.SYSTEM,
) {
    suspend operator fun invoke(iconBytes: ByteArray) {
        val existing = categoryRepository.getByNameKey(DEBUG_USER_CATEGORY_NAME_KEY)
        when {
            existing == null -> create(iconBytes)
            // Both directions write isActive explicitly, because isPicked alone moves nothing the
            // user can see once the species has finds: Record's tile strip and the map read
            // isActive, and the cascade in RecalculateFilterEligibilityUseCase only clears it when
            // the species stops being filter-eligible — which never happens while finds exist
            // (isFilterEligible = isPicked || has finds). Confirmed on-device: hiding a test
            // species that had already been marked left it visible everywhere. The reverse
            // asymmetry is documented in mushroom-collections.md, Phase 4: the cascade never
            // switches isActive back on either.
            existing.isPicked -> setCategoryPicked(existing.copy(isActive = false), false)
            else -> setCategoryPicked(existing.copy(isActive = true), true)
        }
    }

    private suspend fun create(iconBytes: ByteArray) {
        // The extension is honest about what's inside (a catalog .webp); Coil sniffs content, and
        // the real 400px PNG pipeline is Phase 2/3's job.
        val fileName = "catimg_$DEBUG_USER_CATEGORY_NAME_KEY.webp"
        fileSystem.write(photoStorage.resolvePath(fileName).toPath()) { write(iconBytes) }
        categoryRepository.upsert(
            Category(
                id = 0,
                nameKey = DEBUG_USER_CATEGORY_NAME_KEY,
                colorHex = "#7B4DBC",
                iconRef = null,
                // Past the whole catalog (which ends at 29) but below the service category_misc.
                order = 500,
                isActive = true,
                edibilityStatus = EdibilityStatus.CONDITIONALLY_EDIBLE,
                isPicked = true,
                isFilterEligible = true,
                source = CategorySource.USER,
                customNames = mapOf(AppLanguage.RU to "Тестовый вид", AppLanguage.EN to "Test species"),
                scientificName = "Fungus probandus",
                iconFile = fileName,
            ),
        )
    }
}
