package compose.project.leshy.domain.usecase

import compose.project.leshy.data.platform.PhotoStorage
import compose.project.leshy.data.platform.currentTimeMillis
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.repository.CategoryRepository
import okio.FileSystem
import okio.Path.Companion.toPath

/**
 * Longest side of a stored species icon, matching the bundled catalog illustrations
 * (the webp files under `composeResources/drawable`, 400×267) so a user's own species doesn't render visibly
 * softer or sharper than its neighbours on the Record tiles and map markers.
 */
const val CATEGORY_ICON_MAX_DIMENSION = 400

/**
 * Writes an encoded species icon into this app's photo storage and points [category] at it,
 * deleting whatever icon file that species had before.
 *
 * The file name carries a timestamp instead of being just `catimg_<nameKey>.png`, and the previous
 * file is removed rather than overwritten, because Coil caches by model string: rewriting the same
 * path would keep showing the *old* picture from the memory/disk cache until the app restarts.
 * (The archive format still names icon entries deterministically by `nameKey` — that's a separate
 * concern, see Phase 6 in `.claude/plans/user-mushrooms.md`.)
 *
 * Only the file name is stored in [Category.iconFile], never an absolute path — see the field's own
 * doc for why that's deliberately unlike `FieldMark.photoPath`.
 */
class SaveCategoryIconUseCase(
    private val categoryRepository: CategoryRepository,
    private val photoStorage: PhotoStorage,
    private val fileSystem: FileSystem = FileSystem.SYSTEM,
) {
    suspend operator fun invoke(category: Category, pngBytes: ByteArray): Category {
        val fileName = "catimg_${category.nameKey}_${currentTimeMillis()}.png"
        fileSystem.write(photoStorage.resolvePath(fileName).toPath()) { write(pngBytes) }

        val previousFileName = category.iconFile
        val updated = category.copy(iconFile = fileName)
        categoryRepository.upsert(updated)

        if (previousFileName != null && previousFileName != fileName) {
            // Best-effort: a leftover file is harmless, a failed save is not.
            runCatching { fileSystem.delete(photoStorage.resolvePath(previousFileName).toPath()) }
        }
        return updated
    }
}
