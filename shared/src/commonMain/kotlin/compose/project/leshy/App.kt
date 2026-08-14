package compose.project.leshy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
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
import compose.project.leshy.ui.navigation.navigateToTopLevel
import compose.project.leshy.ui.theme.LeshyTheme
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private data class DrawerNavEntry(
    val destination: Destination,
    val labelKey: StringKey,
    val icon: ImageVector,
)

private val drawerNavEntries = listOf(
    DrawerNavEntry(Destination.Record, StringKey.NavRecord, Icons.Filled.Hiking),
    DrawerNavEntry(Destination.Archive, StringKey.NavArchive, Icons.AutoMirrored.Filled.List),
    DrawerNavEntry(Destination.Map, StringKey.NavMap, Icons.Filled.Place),
    DrawerNavEntry(Destination.Settings, StringKey.SettingsTitle, Icons.Filled.Settings),
)

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

            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            var showHelpDialog by remember { mutableStateOf(false) }

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        ) {
                            IconButton(onClick = { scope.launch { drawerState.close() } }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                            }
                            Text(
                                text = stringResource(StringKey.NavDrawerHeader),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        drawerNavEntries.forEach { entry ->
                            val selected = currentDestination?.hierarchy?.any {
                                it.hasRoute(entry.destination::class)
                            } == true
                            NavigationDrawerItem(
                                selected = selected,
                                label = { Text(stringResource(entry.labelKey)) },
                                icon = { Icon(entry.icon, contentDescription = null) },
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    navController.navigateToTopLevel(entry.destination)
                                },
                            )
                        }
                    }
                },
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(StringKey.AppName)) },
                            navigationIcon = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(
                                        imageVector = Icons.Filled.Menu,
                                        contentDescription = stringResource(StringKey.NavMenuContentDescription),
                                        modifier = Modifier.size(36.dp),
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = { showHelpDialog = true }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                        contentDescription = stringResource(StringKey.HelpContentDescription),
                                    )
                                }
                            },
                        )
                    },
                ) { innerPadding ->
                    LeshyNavHost(navController, contentPadding = innerPadding)
                }
            }

            if (showHelpDialog) {
                AlertDialog(
                    onDismissRequest = { showHelpDialog = false },
                    title = { Text(stringResource(StringKey.HelpDialogTitle)) },
                    text = { Text(stringResource(StringKey.HelpDialogMessage)) },
                    confirmButton = {
                        TextButton(onClick = { showHelpDialog = false }) {
                            Text(stringResource(StringKey.HelpDialogDismiss))
                        }
                    },
                )
            }
        }
    }
}
