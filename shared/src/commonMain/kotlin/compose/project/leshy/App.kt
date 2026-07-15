package compose.project.leshy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavDestination.Companion.hasRoute
import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.domain.repository.SettingsRepository
import compose.project.leshy.i18n.LocalAppLanguage
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.ui.navigation.Destination
import compose.project.leshy.ui.navigation.LeshyNavHost
import compose.project.leshy.ui.theme.LeshyTheme
import org.koin.compose.koinInject

private data class BottomNavEntry(
    val destination: Destination,
    val labelKey: StringKey,
    val icon: ImageVector,
)

private val bottomNavEntries = listOf(
    BottomNavEntry(Destination.Record, StringKey.NavRecord, Icons.Filled.Home),
    BottomNavEntry(Destination.Archive, StringKey.NavArchive, Icons.AutoMirrored.Filled.List),
    BottomNavEntry(Destination.Map, StringKey.NavMap, Icons.Filled.Place),
)

/**
 * All top-level destinations (bottom nav + Settings) must navigate through this same
 * pop/save/restore scheme. Mixing a plain `navigate()` for one of them (e.g. Settings pushed
 * without popUpTo) corrupts the saved-state cache the others rely on to survive tab switches.
 */
private fun NavHostController.navigateToTopLevel(destination: Destination) {
    navigate(destination) {
        popUpTo(graph.findStartDestination().id) {
            inclusive = true
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
@Preview
fun App() {
    val settingsRepository = koinInject<SettingsRepository>()
    val language by settingsRepository.observeLanguage().collectAsState(initial = AppLanguage.EN)

    CompositionLocalProvider(LocalAppLanguage provides language) {
        LeshyTheme {
            val navController = rememberNavController()
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = backStackEntry?.destination
            val isSettings = currentDestination?.hierarchy?.any {
                it.hasRoute(Destination.Settings::class)
            } == true

            // Tracks which bottom-nav tab was active before jumping to Settings, so the back
            // arrow shown there returns to that same tab instead of always landing on Record.
            var lastMainDestination by remember { mutableStateOf<Destination>(Destination.Record) }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(stringResource(StringKey.AppName)) },
                        actions = {
                            if (isSettings) {
                                TextButton(onClick = {
                                    navController.navigateToTopLevel(lastMainDestination)
                                }) {
                                    Text(stringResource(StringKey.SettingsBackButtonLabel))
                                    Spacer(Modifier.width(4.dp))
                                    Icon(imageVector = Icons.AutoMirrored.Filled.Undo, contentDescription = null)
                                }
                            } else {
                                IconButton(onClick = {
                                    navController.navigateToTopLevel(Destination.Settings)
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Settings,
                                        contentDescription = stringResource(StringKey.SettingsContentDescription),
                                    )
                                }
                            }
                        },
                    )
                },
                bottomBar = {
                    NavigationBar {
                        bottomNavEntries.forEach { entry ->
                            val selected = currentDestination?.hierarchy?.any {
                                it.hasRoute(entry.destination::class)
                            } == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    lastMainDestination = entry.destination
                                    navController.navigateToTopLevel(entry.destination)
                                },
                                icon = { Icon(entry.icon, contentDescription = stringResource(entry.labelKey)) },
                                label = { Text(stringResource(entry.labelKey)) },
                            )
                        }
                    }
                },
            ) { innerPadding ->
                LeshyNavHost(navController, contentPadding = innerPadding)
            }
        }
    }
}
