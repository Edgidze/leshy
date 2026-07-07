package compose.project.leshy.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import compose.project.leshy.ui.screens.ArchiveScreen
import compose.project.leshy.ui.screens.MapScreen
import compose.project.leshy.ui.screens.RecordScreen
import compose.project.leshy.ui.screens.SettingsScreen

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
        composable<Destination.Record> { RecordScreen() }
        composable<Destination.Archive> { ArchiveScreen() }
        composable<Destination.Map> { MapScreen() }
        composable<Destination.Settings> { SettingsScreen() }
    }
}
