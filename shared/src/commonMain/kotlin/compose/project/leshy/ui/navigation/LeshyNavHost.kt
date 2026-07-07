package compose.project.leshy.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import compose.project.leshy.presentation.archive.WalkDetailViewModel
import compose.project.leshy.presentation.record.RecordViewModel
import compose.project.leshy.ui.screens.ArchiveScreen
import compose.project.leshy.ui.screens.MapScreen
import compose.project.leshy.ui.screens.RecordMapScreen
import compose.project.leshy.ui.screens.RecordScreen
import compose.project.leshy.ui.screens.SettingsScreen
import compose.project.leshy.ui.screens.WalkDetailScreen
import compose.project.leshy.ui.screens.WalkMapScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun LeshyNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Record,
        modifier = modifier.padding(contentPadding),
    ) {
        composable<Destination.Record> { backStackEntry ->
            val viewModel = koinViewModel<RecordViewModel>(viewModelStoreOwner = backStackEntry)
            RecordScreen(
                viewModel = viewModel,
                onViewMap = { navController.navigate(Destination.RecordMap) },
            )
        }
        composable<Destination.RecordMap> {
            val recordEntry = navController.getBackStackEntry(Destination.Record)
            val viewModel = koinViewModel<RecordViewModel>(viewModelStoreOwner = recordEntry)
            RecordMapScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable<Destination.Archive> {
            ArchiveScreen(onWalkClick = { walkId -> navController.navigate(Destination.WalkDetail(walkId)) })
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
            val detailEntry = navController.getBackStackEntry(Destination.WalkDetail(route.walkId))
            val viewModel = koinViewModel<WalkDetailViewModel>(
                viewModelStoreOwner = detailEntry,
                parameters = { parametersOf(route.walkId) },
            )
            WalkMapScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable<Destination.Map> { MapScreen() }
        composable<Destination.Settings> { SettingsScreen() }
    }
}
