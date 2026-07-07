package compose.project.leshy

import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.getValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavDestination.Companion.hasRoute
import compose.project.leshy.ui.navigation.Destination
import compose.project.leshy.ui.navigation.LeshyNavHost
import compose.project.leshy.ui.theme.LeshyTheme
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.app_name
import leshy.shared.generated.resources.nav_archive
import leshy.shared.generated.resources.nav_map
import leshy.shared.generated.resources.nav_record
import leshy.shared.generated.resources.settings_content_description
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private data class BottomNavEntry(
    val destination: Destination,
    val labelRes: StringResource,
    val icon: ImageVector,
)

private val bottomNavEntries = listOf(
    BottomNavEntry(Destination.Record, Res.string.nav_record, Icons.Filled.Home),
    BottomNavEntry(Destination.Archive, Res.string.nav_archive, Icons.AutoMirrored.Filled.List),
    BottomNavEntry(Destination.Map, Res.string.nav_map, Icons.Filled.Place),
)

@Composable
@Preview
fun App() {
    LeshyTheme {
        val navController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = backStackEntry?.destination

        Scaffold(
            modifier = Modifier.safeContentPadding(),
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Res.string.app_name)) },
                    actions = {
                        IconButton(onClick = {
                            navController.navigate(Destination.Settings)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = stringResource(Res.string.settings_content_description),
                            )
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
                                navController.navigate(entry.destination) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(entry.icon, contentDescription = stringResource(entry.labelRes)) },
                            label = { Text(stringResource(entry.labelRes)) },
                        )
                    }
                }
            },
        ) { innerPadding ->
            LeshyNavHost(navController, contentPadding = innerPadding)
        }
    }
}
