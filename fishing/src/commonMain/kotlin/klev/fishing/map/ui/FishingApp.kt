package klev.fishing.map.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import klev.fishing.map.i18n.FishStringKey
import klev.fishing.map.i18n.fishStringResource
import klev.fishing.map.ui.screens.CatchMapScreen
import klev.fishing.map.ui.screens.FishArchiveScreen
import klev.fishing.map.ui.screens.FishSettingsScreen
import klev.fishing.map.ui.screens.RecordScreen
import klev.fishing.map.ui.screens.TripDetailScreen
import kotlinx.serialization.Serializable
import leshy.mushrooms.map.data.platform.currentDeviceLanguage
import leshy.mushrooms.map.domain.model.Edition
import leshy.mushrooms.map.domain.model.EditionLanguages
import leshy.mushrooms.map.domain.model.ThemeMode
import leshy.mushrooms.map.domain.repository.SettingsRepository
import leshy.mushrooms.map.i18n.LocalAppLanguage
import leshy.mushrooms.map.ui.theme.LeshyTheme
import leshy.mushrooms.map.ui.theme.isDark
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import org.koin.compose.koinInject

@Serializable object RecordRoute
@Serializable object ArchiveRoute
@Serializable object MapRoute
@Serializable object SettingsRoute
@Serializable data class TripDetailRoute(val tripId: Long)

private data class Tab(val route: Any, val labelKey: FishStringKey, val icon: ImageVector)

/**
 * Разделы — нижняя панель, а не боковое выдвижное меню, как у грибного «Лешего». Причина
 * практическая: рыбак держит телефон одной рукой, часто в перчатке и часто над водой, и тянуться к
 * гамбургеру в левом верхнем углу неудобно; у грибов панель появилась при другом наборе экранов.
 *
 * Правило грибной навигации при этом соблюдается и здесь, потому что оно про механику, а не про
 * оформление: **переход между разделами идёт одним способом** — [navigateToTab] с
 * `popUpTo(startDestination) + saveState + restoreState`. Подмена его голым `navigate()` для одного
 * раздела ломает сохранение состояния у остальных (у грибов это стоило нескольких настоящих
 * крашей — `ui/navigation/CLAUDE.md`).
 */
private val tabs = listOf(
    Tab(RecordRoute, FishStringKey.NavRecord, Icons.Filled.Timeline),
    Tab(ArchiveRoute, FishStringKey.NavArchive, Icons.Outlined.Inventory2),
    Tab(MapRoute, FishStringKey.NavMap, Icons.Filled.Map),
    Tab(SettingsRoute, FishStringKey.NavSettings, Icons.Filled.Settings),
)

@Composable
fun FishingApp() {
    val settings = koinInject<SettingsRepository>()
    val editionLanguages = koinInject<EditionLanguages>()
    // Начальное значение — язык системы, а не EN: `initial` видно ровно до первой эмиссии
    // DataStore, и это кадры холодного старта. Тот же приём, что в грибном `App()`.
    val language by settings.observeLanguage().collectAsState(initial = currentDeviceLanguage(editionLanguages))
    val themeMode by settings.observeThemeMode().collectAsState(initial = ThemeMode.SYSTEM)

    CompositionLocalProvider(LocalAppLanguage provides language) {
        // Тема берётся у `:shared` целиком и с мировой палитрой: своё оформление рыбацкого продукта
        // — работа владельца, а до неё честнее выглядеть как есть, чем выдумывать палитру.
        LeshyTheme(edition = Edition.WORLD, useDarkTheme = themeMode.isDark()) {
            val navController = rememberNavController()
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route

            Scaffold(
                bottomBar = {
                    NavigationBar {
                        tabs.forEach { tab ->
                            val selected = currentRoute?.contains(tab.route::class.simpleName ?: "") == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navController.navigateToTab(tab.route) },
                                icon = { Icon(tab.icon, contentDescription = null) },
                                label = { Text(fishStringResource(tab.labelKey)) },
                            )
                        }
                    }
                },
            ) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = RecordRoute,
                    modifier = Modifier.padding(padding),
                ) {
                    composable<RecordRoute> { RecordScreen() }
                    composable<ArchiveRoute> {
                        FishArchiveScreen(onTripClick = { id -> navController.navigate(TripDetailRoute(id)) })
                    }
                    composable<MapRoute> { CatchMapScreen() }
                    composable<SettingsRoute> { FishSettingsScreen() }
                    composable<TripDetailRoute> { entry ->
                        TripDetailScreen(
                            tripId = entry.toRoute<TripDetailRoute>().tripId,
                            onBack = { navController.popBackStack() },
                        )
                    }
                }
            }
        }
    }
}

/** Единственный разрешённый способ перехода между разделами — см. KDoc у [tabs]. */
private fun NavHostController.navigateToTab(route: Any) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
