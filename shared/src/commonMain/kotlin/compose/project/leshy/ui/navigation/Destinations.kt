package compose.project.leshy.ui.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable

sealed interface Destination {
    @Serializable
    data object Home : Destination

    @Serializable
    data object Record : Destination

    @Serializable
    data object Archive : Destination

    @Serializable
    data class WalkDetail(val walkId: Long) : Destination

    @Serializable
    data class WalkMap(val walkId: Long) : Destination

    @Serializable
    data object Map : Destination

    @Serializable
    data object Preparation : Destination

    @Serializable
    data object Settings : Destination
}

/**
 * All top-level section destinations (home-screen entries) must navigate through this
 * same pop/save/restore scheme. Mixing a plain `navigate()` for one of them corrupts the
 * saved-state cache the others rely on to survive tab switches.
 *
 * `inclusive = false` keeps `Home` (the graph's start destination) anchored at the bottom
 * of the back stack rather than removing it — that's what makes back-from-a-section land
 * on Home, and back-from-Home fall through to the platform default (app exit) instead of
 * a custom exit handler.
 */
fun NavHostController.navigateToTopLevel(destination: Destination) {
    navigate(destination) {
        popUpTo(graph.findStartDestination().id) {
            inclusive = false
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
