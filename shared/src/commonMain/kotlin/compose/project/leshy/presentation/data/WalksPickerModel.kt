package compose.project.leshy.presentation.data

import compose.project.leshy.domain.model.Walk
import compose.project.leshy.domain.util.yearMonthOf

enum class WalksPickState { ALL, SOME, NONE }

/** One year+month group of walks for the export picker, newest-first like the Archive screen. */
data class WalksPickerGroup(val year: Int, val month: Int, val walks: List<Walk>, val selectedIds: Set<Long>) {
    val pickState: WalksPickState = when {
        walks.all { it.id in selectedIds } -> WalksPickState.ALL
        walks.none { it.id in selectedIds } -> WalksPickState.NONE
        else -> WalksPickState.SOME
    }
}

/** Groups [walks] by (year, month) of [Walk.startTime], newest year+month first, walks within a
 * group newest first — same ordering as the Archive screen's list. */
fun buildWalksPickerGroups(walks: List<Walk>, selectedIds: Set<Long>): List<WalksPickerGroup> =
    walks.groupBy { yearMonthOf(it.startTime) }
        .entries
        .sortedWith(compareByDescending<Map.Entry<Pair<Int, Int>, List<Walk>>> { it.key.first }.thenByDescending { it.key.second })
        .map { (yearMonth, groupWalks) ->
            WalksPickerGroup(
                year = yearMonth.first,
                month = yearMonth.second,
                walks = groupWalks.sortedByDescending { it.startTime },
                selectedIds = selectedIds,
            )
        }
