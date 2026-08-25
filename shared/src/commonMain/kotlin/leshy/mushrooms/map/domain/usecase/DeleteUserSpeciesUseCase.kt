package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.data.platform.PhotoStorage
import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.domain.repository.CategoryRepository
import leshy.mushrooms.map.domain.repository.FieldMarkRepository
import okio.FileSystem
import okio.Path.Companion.toPath

/**
 * Permanently deletes a user-created/imported species — the explicit "delete" action next to the
 * hide toggle in "Мои грибы". Supersedes the cascade-delete decision in
 * `.claude/plans/user-mushrooms.md` (Phase 9): finds logged under the deleted species are no
 * longer destroyed along with it, they're moved onto the [UNKNOWN_MUSHROOM_NAME_KEY] catalog
 * species instead (Phase 10) — reassigned *before* the category row is deleted, so
 * `ObjectEntity.categoryId`'s `ON DELETE CASCADE` (migration 7→8) never actually fires on the
 * normal path: no `objects` row still points at the deleted id by the time the `DELETE` runs.
 */
class DeleteUserSpeciesUseCase(
    private val categoryRepository: CategoryRepository,
    private val fieldMarkRepository: FieldMarkRepository,
    private val photoStorage: PhotoStorage,
    private val fileSystem: FileSystem = FileSystem.SYSTEM,
) {
    suspend operator fun invoke(category: Category) {
        val unknownMushroom = requireNotNull(categoryRepository.getByNameKey(UNKNOWN_MUSHROOM_NAME_KEY)) {
            "Unknown mushroom category must exist before deleting a species"
        }
        fieldMarkRepository.reassignCategory(category.id, unknownMushroom.id)
        categoryRepository.delete(category)

        // Only the species' own icon is garbage now — its finds survive under Unknown mushroom
        // with their photos untouched, so there's nothing else to clean up on disk.
        category.iconFile?.let { iconFile ->
            runCatching { fileSystem.delete(photoStorage.resolvePath(iconFile).toPath()) }
        }
    }
}
