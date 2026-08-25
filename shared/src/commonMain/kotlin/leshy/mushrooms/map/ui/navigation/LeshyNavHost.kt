package leshy.mushrooms.map.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import leshy.mushrooms.map.domain.repository.SettingsRepository
import leshy.mushrooms.map.i18n.LocalAppLanguage
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.presentation.archive.WalkDetailViewModel
import leshy.mushrooms.map.presentation.record.RecordViewModel
import leshy.mushrooms.map.ui.components.SectionScaffold
import leshy.mushrooms.map.ui.screens.ArchiveScreen
import leshy.mushrooms.map.ui.screens.DataScreen
import leshy.mushrooms.map.ui.screens.LanguagePickerScreen
import leshy.mushrooms.map.ui.screens.MapScreen
import leshy.mushrooms.map.ui.screens.PreparationScreen
import leshy.mushrooms.map.ui.screens.RecordScreen
import leshy.mushrooms.map.ui.screens.SettingsScreen
import leshy.mushrooms.map.ui.screens.SpeciesScreen
import leshy.mushrooms.map.ui.screens.WalkDescriptionEditScreen
import leshy.mushrooms.map.ui.screens.WalkDetailScreen
import leshy.mushrooms.map.ui.screens.WalkMapScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

// The library default is a 700ms fade, which reads as sluggish for top-level tab switches and
// also gives native map views (rendered outside normal Compose alpha compositing, see
// mapRenderOptions) a long window in which to visibly bleed through the transition.
private const val NAV_TRANSITION_DURATION_MS = 200

@Composable
fun LeshyNavHost(
    navController: NavHostController,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Record,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(NAV_TRANSITION_DURATION_MS)) },
        exitTransition = { fadeOut(animationSpec = tween(NAV_TRANSITION_DURATION_MS)) },
    ) {
        composable<Destination.Record> { backStackEntry ->
            val viewModel = koinViewModel<RecordViewModel>(viewModelStoreOwner = backStackEntry)
            // Title is the app name (this is now the home screen), not "New Entry" — the drawer's
            // own row for this destination still reads NavRecord, see App.kt's drawerNavEntries.
            SectionScaffold(
                title = StringKey.AppName,
                onMenuClick = onMenuClick,
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
                onMenuClick = onMenuClick,
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
                onEditDescription = { navController.navigate(Destination.WalkDescriptionEdit(route.walkId)) },
            )
        }
        composable<Destination.WalkMap> { backStackEntry ->
            val route = backStackEntry.toRoute<Destination.WalkMap>()
            // The parent WalkDetail entry can already be gone from the back stack while this
            // composable is still recomposing during the exit transition (e.g. the user opened
            // the drawer and picked another section, which pops WalkDetail via popUpTo) — guard
            // instead of crashing.
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
        composable<Destination.WalkDescriptionEdit> { backStackEntry ->
            val route = backStackEntry.toRoute<Destination.WalkDescriptionEdit>()
            // Same guard as Destination.WalkMap above — the parent WalkDetail entry can already be
            // gone from the back stack during an exit transition.
            val detailEntry = runCatching {
                navController.getBackStackEntry(Destination.WalkDetail(route.walkId))
            }.getOrNull()
            if (detailEntry != null) {
                val viewModel = koinViewModel<WalkDetailViewModel>(
                    viewModelStoreOwner = detailEntry,
                    parameters = { parametersOf(route.walkId) },
                )
                WalkDescriptionEditScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
        }
        composable<Destination.Map> {
            SectionScaffold(
                title = StringKey.NavMap,
                onMenuClick = onMenuClick,
            ) { padding -> MapScreen(modifier = Modifier.padding(padding)) }
        }
        composable<Destination.Preparation> {
            SectionScaffold(
                title = StringKey.NavPreparation,
                onMenuClick = onMenuClick,
            ) { padding -> PreparationScreen(modifier = Modifier.padding(padding)) }
        }
        composable<Destination.Settings> {
            SectionScaffold(
                title = StringKey.SettingsTitle,
                onMenuClick = onMenuClick,
            ) { padding ->
                SettingsScreen(
                    onLanguageClick = { navController.navigate(Destination.LanguagePicker) },
                    modifier = Modifier.padding(padding),
                )
            }
        }
        composable<Destination.LanguagePicker> {
            // No dedicated ViewModel — the current language is already reactive via
            // LocalAppLanguage (provided once at the App() root from the same SettingsRepository),
            // and confirming only ever needs to write, not observe, so injecting the repository
            // straight into this route (same pattern App.kt itself uses for it) avoids spinning up
            // a whole SettingsViewModel — with its category/collection seeding side effects in
            // init — just to flip one DataStore value.
            val settingsRepository: SettingsRepository = koinInject()
            val scope = rememberCoroutineScope()
            LanguagePickerScreen(
                currentLanguage = LocalAppLanguage.current,
                onConfirm = { language ->
                    scope.launch { settingsRepository.setLanguage(language) }
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() },
            )
        }
        composable<Destination.Data> {
            SectionScaffold(
                title = StringKey.NavData,
                onMenuClick = onMenuClick,
            ) { padding ->
                DataScreen(
                    onNavigateToArchive = { navController.navigateToTopLevel(Destination.Archive) },
                    modifier = Modifier.padding(padding),
                )
            }
        }
        composable<Destination.Species> {
            SectionScaffold(
                title = StringKey.NavSpecies,
                onMenuClick = onMenuClick,
            ) { padding -> SpeciesScreen(modifier = Modifier.padding(padding)) }
        }
    }
}
