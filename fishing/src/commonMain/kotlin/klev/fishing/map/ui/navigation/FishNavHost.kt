package klev.fishing.map.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import klev.fishing.map.ui.screens.CatchMapScreen
import klev.fishing.map.ui.screens.FishArchiveScreen
import klev.fishing.map.ui.screens.FishSettingsScreen
import klev.fishing.map.ui.screens.FishSpeciesScreen
import klev.fishing.map.ui.screens.RecordScreen
import klev.fishing.map.ui.screens.TripDetailScreen

/**
 * Переход между экранами — короткий fade, 200 мс. Число взято у грибного `LeshyNavHost` вместе с
 * причиной: библиотечный дефолт (700 мс) не только ощущается медленным, но и увеличивает окно, в
 * котором карта уходящего экрана видна поверх приходящего (`ui/map/CLAUDE.md` про `TextureView`).
 */
private const val NAV_TRANSITION_DURATION_MS = 200

@Composable
fun FishNavHost(navController: NavHostController, onMenuClick: () -> Unit) {
    NavHost(
        navController = navController,
        startDestination = FishDestination.Record,
        enterTransition = { fadeIn(animationSpec = tween(NAV_TRANSITION_DURATION_MS)) },
        exitTransition = { fadeOut(animationSpec = tween(NAV_TRANSITION_DURATION_MS)) },
    ) {
        composable<FishDestination.Record> { RecordScreen(onMenuClick = onMenuClick) }
        composable<FishDestination.Archive> {
            FishArchiveScreen(
                onMenuClick = onMenuClick,
                // Детализация — обычный `navigate()`, не `navigateToTopLevel`: это вложенный экран
                // раздела, а не раздел. Смешивать эти два способа — ровно та ошибка, от которой
                // предостерегает KDoc `navigateToTopLevel`.
                onTripClick = { id -> navController.navigate(FishDestination.TripDetail(id)) },
            )
        }
        composable<FishDestination.Map> { CatchMapScreen(onMenuClick = onMenuClick) }
        composable<FishDestination.Species> { FishSpeciesScreen(onMenuClick = onMenuClick) }
        composable<FishDestination.Settings> { FishSettingsScreen(onMenuClick = onMenuClick) }
        composable<FishDestination.TripDetail> { entry ->
            TripDetailScreen(
                tripId = entry.toRoute<FishDestination.TripDetail>().tripId,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
