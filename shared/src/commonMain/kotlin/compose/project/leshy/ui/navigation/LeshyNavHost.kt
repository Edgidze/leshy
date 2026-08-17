package compose.project.leshy.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.presentation.archive.WalkDetailViewModel
import compose.project.leshy.presentation.record.RecordViewModel
import compose.project.leshy.ui.components.SectionScaffold
import compose.project.leshy.ui.screens.ArchiveScreen
import compose.project.leshy.ui.screens.DataScreen
import compose.project.leshy.ui.screens.HomeScreen
import compose.project.leshy.ui.screens.MapScreen
import compose.project.leshy.ui.screens.RecordScreen
import compose.project.leshy.ui.screens.SettingsScreen
import compose.project.leshy.ui.screens.WalkDetailScreen
import compose.project.leshy.ui.screens.WalkMapScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

// The library default is a 700ms fade, which reads as sluggish for top-level tab switches and
// also gives native map views (rendered outside normal Compose alpha compositing, see
// mapRenderOptions) a long window in which to visibly bleed through the transition.
private const val NAV_TRANSITION_DURATION_MS = 200

@Composable
fun LeshyNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Home,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(NAV_TRANSITION_DURATION_MS)) },
        exitTransition = { fadeOut(animationSpec = tween(NAV_TRANSITION_DURATION_MS)) },
    ) {
        composable<Destination.Home> {
            HomeScreen(onNavigate = { destination -> navController.navigateToTopLevel(destination) })
        }
        composable<Destination.Record> { backStackEntry ->
            val viewModel = koinViewModel<RecordViewModel>(viewModelStoreOwner = backStackEntry)
            SectionScaffold(
                title = StringKey.NavRecord,
                onHomeClick = { navController.popBackStack(Destination.Home, false) },
            ) { padding ->
                RecordScreen(
                    viewModel = viewModel,
                    onFinished = { navController.navigateToTopLevel(Destination.Archive) },
                    modifier = Modifier.padding(padding),
                )
            }
        }
        composable<Destination.Archive> {
            SectionScaffold(
                title = StringKey.NavArchive,
                onHomeClick = { navController.popBackStack(Destination.Home, false) },
            ) { padding ->
                ArchiveScreen(
                    onWalkClick = { walkId -> navController.navigate(Destination.WalkDetail(walkId)) },
                    modifier = Modifier.padding(padding),
                )
            }
        }
        composable<Destination.WalkDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Destination.WalkDetail>()
            val viewModel = koinViewModel<WalkDetailViewModel>(
                viewModelStoreOwner = backStackEntry,
                parameters = { parametersOf(route.walkId) },
            )
            WalkDetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onViewMap = { navController.navigate(Destination.WalkMap(route.walkId)) },
            )
        }
        composable<Destination.WalkMap> { backStackEntry ->
            val route = backStackEntry.toRoute<Destination.WalkMap>()
            // The parent WalkDetail entry can already be gone from the back stack while this
            // composable is still recomposing during the exit transition (e.g. the user tapped
            // the home icon, which pops WalkDetail via popUpTo) — guard instead of crashing.
            val detailEntry = runCatching {
                navController.getBackStackEntry(Destination.WalkDetail(route.walkId))
            }.getOrNull()
            if (detailEntry != null) {
                val viewModel = koinViewModel<WalkDetailViewModel>(
                    viewModelStoreOwner = detailEntry,
                    parameters = { parametersOf(route.walkId) },
                )
                WalkMapScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
        }
        composable<Destination.Map> {
            SectionScaffold(
                title = StringKey.NavMap,
                onHomeClick = { navController.popBackStack(Destination.Home, false) },
            ) { padding -> MapScreen(modifier = Modifier.padding(padding)) }
        }
        composable<Destination.Settings> {
            SectionScaffold(
                title = StringKey.SettingsTitle,
                onHomeClick = { navController.popBackStack(Destination.Home, false) },
            ) { padding -> SettingsScreen(modifier = Modifier.padding(padding)) }
        }
        composable<Destination.Data> {
            SectionScaffold(
                title = StringKey.NavData,
                onHomeClick = { navController.popBackStack(Destination.Home, false) },
            ) { padding -> DataScreen(modifier = Modifier.padding(padding)) }
        }
    }
}
