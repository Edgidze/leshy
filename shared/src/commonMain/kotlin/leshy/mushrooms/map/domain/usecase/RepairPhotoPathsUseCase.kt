package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.data.platform.repairStalePhotoPath
import leshy.mushrooms.map.domain.repository.FieldMarkRepository
import leshy.mushrooms.map.domain.repository.WalkRepository
import kotlinx.coroutines.flow.first
import okio.FileSystem
import okio.Path.Companion.toPath

/**
 * One-shot repair pass for `photoPath`/`thumbnailPath` values that no longer point to an existing
 * file — on iOS this happens when the app's sandbox container UUID changes (some update/restore
 * paths), invalidating every previously stored absolute path even though the files themselves
 * survive the move. Re-resolves each dangling path against the platform's *current* storage root
 * ([repairStalePhotoPath]) and persists the corrected path if the file is actually found there.
 *
 * Called once per [leshy.mushrooms.map.presentation.archive.ArchiveViewModel]/
 * [leshy.mushrooms.map.presentation.map.MapViewModel] lifecycle — cheap no-op once every path
 * resolves, same idempotent-repair idiom as [BackfillWalkThumbnailsUseCase].
 */
class RepairPhotoPathsUseCase(
    private val walkRepository: WalkRepository,
    private val fieldMarkRepository: FieldMarkRepository,
    private val fileSystem: FileSystem = FileSystem.SYSTEM,
) {
    suspend operator fun invoke() {
        walkRepository.observeAll().first().forEach { walk ->
            val path = walk.thumbnailPath ?: return@forEach
            if (fileSystem.exists(path.toPath())) return@forEach
            val repaired = repairStalePhotoPath(path) ?: return@forEach
            walkRepository.update(walk.copy(thumbnailPath = repaired))
        }
        fieldMarkRepository.observeAll().first().forEach { mark ->
            val path = mark.photoPath ?: return@forEach
            if (fileSystem.exists(path.toPath())) return@forEach
            val repaired = repairStalePhotoPath(path) ?: return@forEach
            fieldMarkRepository.updateMark(mark.copy(photoPath = repaired))
        }
    }
}
