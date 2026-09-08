package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.data.platform.WALK_THUMBNAIL_VARIANT
import leshy.mushrooms.map.data.platform.WalkThumbnailRenderer
import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.domain.model.MarkType
import leshy.mushrooms.map.domain.model.Walk
import leshy.mushrooms.map.domain.repository.FieldMarkRepository
import leshy.mushrooms.map.domain.repository.TrackPointRepository
import leshy.mushrooms.map.domain.repository.WalkRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import okio.FileSystem
import okio.Path.Companion.toPath

/**
 * Потолок на ОДНУ прогулку, а не на весь проход.
 *
 * Снапшоттеры обеих платформ — колбэчные, и `suspendCancellableCoroutine` вокруг них резюмится
 * только из колбэка: не пришёл колбэк — корутина висит вечно. Проход идёт последовательно, поэтому
 * одна такая прогулка раньше забирала с собой все оставшиеся, и у владельца архива на сотню
 * прогулок не появлялось НИ ОДНОЙ миниатюры (репорт 2026-09-07, импорт после переустановки).
 * Потолок не «на всякий случай»: он и есть то, что превращает проход в постепенную загрузку,
 * которую видно на экране, вместо одного молчаливого зависания.
 *
 * Щедрый намеренно — снимок это стиль плюс десяток-другой тайлов по сети, и на медленной связи
 * секунды складываются. Не уложившаяся прогулка ничего не теряет: `thumbnailPath` у неё остаётся
 * пустым, в архиве рисуется силуэт маршрута (`WalkRouteThumbnail`), а попытка повторится при
 * следующем заходе в архив.
 */
private const val RENDER_TIMEOUT_MILLIS = 30_000L

/**
 * One-shot repair pass for walks whose `thumbnailPath` is still null — either recorded before the
 * thumbnail feature existed (pre-v3 Room schema), or hit the now-fixed [WalkThumbnailRenderer] gap
 * where too few live track points at Finish time (short walks) permanently skipped rendering
 * instead of falling back to *some* location. Re-renders from each walk's already-persisted track
 * points/finds/start-or-end coordinates, so no walk is stuck without a map background forever.
 *
 * **Плюс снимки прежних поколений отрисовки** — те, чьё имя файла названо не
 * [WALK_THUMBNAIL_VARIANT]: у прогулок, записанных до перехода на 16:9, на диске лежит квадрат
 * (в поле 16:9 его можно показать только срезав маршруту верх и низ), у чуть более поздних —
 * полоса, но с точками находок без обводки, слипающимися в пятно там, где находок много.
 * Поколение узнаётся по имени файла, без чтения самого файла, — ради этого имя его и называет.
 *
 * Запускается на КАЖДОМ входе на экран «Архив»
 * ([leshy.mushrooms.map.presentation.archive.ArchiveViewModel.onScreenShown]) — дешёвый no-op,
 * когда у всех прогулок уже есть снимок текущей геометрии: оба набора схлопываются в пустой и
 * остаются такими через обычную запись [WalkRepository.update].
 *
 * **Из импорта этот проход НЕ вызывается, и это условие, а не деталь.** Импорт заканчивается
 * своей последней записью в базу, а не отрисовкой картинок по сети: раньше `DataViewModel` ждал
 * здесь окончания прохода, и владелец архива на сотню прогулок видел «Идёт обработка…» минутами
 * после того, как все треки и находки уже лежали в базе, — а без сети не дожидался вовсе.
 *
 * Первый заход после обновления, сменившего поколение, — не no-op: он перерисовывает снимок
 * КАЖДОЙ прогулки, а каждый
 * снимок это обращение к тайлам, то есть сеть. Идёт последовательно и в фоне, экран архива не
 * ждёт (см. `ArchiveViewModel`), неудача любой отдельной прогулки оставляет прежний файл на месте
 * и повторяется при следующем открытии архива — но у владельца большого архива первый заход
 * заметно потратит трафик, и без сети он просто не сделает ничего: в карточках останется силуэт
 * маршрута (`WalkRouteThumbnail`), это нормальный результат, а не недогруженный экран.
 */
class BackfillWalkThumbnailsUseCase(
    private val walkRepository: WalkRepository,
    private val trackPointRepository: TrackPointRepository,
    private val fieldMarkRepository: FieldMarkRepository,
    private val walkThumbnailRenderer: WalkThumbnailRenderer,
    private val updateWalkThumbnail: UpdateWalkThumbnailUseCase,
    private val fileSystem: FileSystem = FileSystem.SYSTEM,
) {
    suspend operator fun invoke() {
        val walksNeedingThumbnail = walkRepository.observeAll().first().filter { it.thumbnailPath.isStale() }
        for (walk in walksNeedingThumbnail) {
            try {
                withTimeoutOrNull(RENDER_TIMEOUT_MILLIS) { backfill(walk) }
            } catch (e: CancellationException) {
                // Отмена всего прохода (экран закрыт) — не «неудача прогулки», её нельзя гасить:
                // проглоти её здесь, и цикл продолжил бы крутиться в уже мёртвой области.
                throw e
            } catch (_: Exception) {
                // Сбой одной прогулки не имеет права остановить очередь — ровно тот же контракт,
                // что у [RENDER_TIMEOUT_MILLIS] выше, только для исключения вместо зависания.
                // Записи в базу тут нет, значит прогулка просто останется в списке на следующий раз.
            }
        }
    }

    /** Нет снимка вовсе — или есть, но снятый в другой пропорции (см. [WALK_THUMBNAIL_VARIANT]). */
    private fun String?.isStale(): Boolean = this == null || !endsWith("$WALK_THUMBNAIL_VARIANT.png")

    private suspend fun backfill(walk: Walk) {
        val track = trackPointRepository.observeByWalkId(walk.id).first()
            .sortedBy { it.sequence }
            .map { GeoPoint(it.lat, it.lon, it.elevation, it.timestamp) }
        val findLocations = fieldMarkRepository.observeByWalkId(walk.id).first()
            .filter { it.type == MarkType.MUSHROOM }
            .map { GeoPoint(it.lat, it.lon, null, it.timestamp) }
        val anchor = anchorOf(walk)

        val thumbnailPath = walkThumbnailRenderer.render(walk.id, track, findLocations, anchor) ?: return
        val replacedPath = walk.thumbnailPath
        updateWalkThumbnail(walk.id, thumbnailPath)
        // Снимок прежней геометрии лежит под другим именем и после замены на него уже никто не
        // смотрит — иначе он остался бы на диске навсегда. Порядок важен: сначала в базу новый
        // путь, потом удаление старого файла, чтобы обрыв между двумя шагами оставлял лишний файл,
        // а не прогулку с путём на удалённый. Не «best-effort из лени»: не удалившийся файл
        // безвреден, а исключение отсюда прервало бы перерисовку остальных прогулок.
        if (replacedPath != null && replacedPath != thumbnailPath) {
            runCatching { fileSystem.delete(replacedPath.toPath()) }
        }
    }

    // walk.startLat/startLon default to (0.0, 0.0) when Start was pressed before GPS produced a
    // fix (see RecordViewModel.start()) — not a real location, so it's only usable as a last
    // resort, and never when it's the (0,0) sentinel with nothing else to go on either.
    private fun anchorOf(walk: Walk): GeoPoint? = when {
        walk.endLat != null && walk.endLon != null ->
            GeoPoint(walk.endLat, walk.endLon, null, walk.endTime ?: walk.startTime)
        walk.startLat != 0.0 || walk.startLon != 0.0 ->
            GeoPoint(walk.startLat, walk.startLon, null, walk.startTime)
        else -> null
    }
}
