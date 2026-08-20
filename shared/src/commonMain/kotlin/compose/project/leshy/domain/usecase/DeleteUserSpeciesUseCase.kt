package compose.project.leshy.domain.usecase

import compose.project.leshy.data.platform.PhotoStorage
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.repository.CategoryRepository
import compose.project.leshy.domain.repository.FieldMarkRepository
import kotlinx.coroutines.flow.first
import okio.FileSystem
import okio.Path.Companion.toPath

/**
 * Permanently deletes a user-created/imported species together with every find logged against it
 * — the explicit "delete" action next to the hide toggle in "Мои грибы". Supersedes the "hide
 * only, no delete" decision in `.claude/plans/user-mushrooms.md` ("Только скрытие..." section,
 * Phase 9): the user asked for a real delete and accepts the data loss as their own choice.
 *
 * `ObjectEntity.categoryId`'s FK is `ON DELETE CASCADE` as of migration 7→8, so removing the
 * category row cascades the matching `objects` rows in SQLite by itself — this use case only
 * cleans up what CASCADE can't reach: files on disk (find photos + the species' own icon). Those
 * have to be read out *before* the delete, since a cascading DELETE doesn't return the rows it
 * removed.
 */
class DeleteUserSpeciesUseCase(
    private val categoryRepository: CategoryRepository,
    private val fieldMarkRepository: FieldMarkRepository,
    private val photoStorage: PhotoStorage,
    private val fileSystem: FileSystem = FileSystem.SYSTEM,
) {
    suspend operator fun invoke(category: Category) {
        // FieldMark.photoPath is stored as an absolute path already (unlike Category.iconFile).
        val orphanedPhotoPaths = fieldMarkRepository.observeAll().first()
            .filter { it.categoryId == category.id }
            .mapNotNull { it.photoPath }

        categoryRepository.delete(category)

        for (photoPath in orphanedPhotoPaths) {
            // Best-effort: a leftover file is harmless, a failed cleanup shouldn't surface as an error.
            runCatching { fileSystem.delete(photoPath.toPath()) }
        }
        category.iconFile?.let { iconFile ->
            runCatching { fileSystem.delete(photoStorage.resolvePath(iconFile).toPath()) }
        }
    }
}
