package compose.project.leshy.domain.usecase

import compose.project.leshy.domain.repository.TrackPointRepository
import compose.project.leshy.domain.repository.WalkRepository
import kotlinx.coroutines.flow.first

/**
 * Closes out walks left with `endTime == null` by a process death mid-recording — RecordViewModel's
 * `walkId` lives only in memory (see `androidMain/CLAUDE.md`, "Фоновая запись трека"), so a killed
 * process loses track of which walk was active while its Room row stays open forever. Safe to run
 * unconditionally at RecordViewModel startup: the walk actually being recorded right now hasn't been
 * assigned a walkId yet at that point, so anything already open in the DB can't be it.
 */
class HealOrphanedWalksUseCase(
    private val walkRepository: WalkRepository,
    private val trackPointRepository: TrackPointRepository,
    private val finishWalk: FinishWalkUseCase,
) {
    suspend operator fun invoke() {
        val orphaned = walkRepository.observeAll().first().filter { it.endTime == null }
        for (walk in orphaned) {
            val lastPoint = trackPointRepository.observeByWalkId(walk.id).first().lastOrNull()
            finishWalk(
                walkId = walk.id,
                endTime = lastPoint?.timestamp ?: walk.startTime,
                endLat = lastPoint?.lat ?: walk.startLat,
                endLon = lastPoint?.lon ?: walk.startLon,
            )
        }
    }
}
