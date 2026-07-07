package compose.project.leshy.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import compose.project.leshy.presentation.record.RecordViewModel
import compose.project.leshy.ui.screens.ArchiveScreen
import compose.project.leshy.ui.screens.MapScreen
import compose.project.leshy.ui.screens.RecordMapScreen
import compose.project.leshy.ui.screens.RecordScreen
import compose.project.leshy.ui.screens.SettingsScreen
import org.koin.compose.viewmodel.koinViewModel

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
        composable<Destination.Archive> { ArchiveScreen() }
        composable<Destination.Map> { MapScreen() }
        composable<Destination.Settings> { SettingsScreen() }
    }
}
