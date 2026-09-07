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
import leshy.mushrooms.map.i18n.HelpTopic
import leshy.mushrooms.map.i18n.LocalAppLanguage
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.presentation.archive.WalkDetailViewModel
import leshy.mushrooms.map.presentation.record.RecordViewModel
import leshy.mushrooms.map.ui.components.SectionScaffold
import leshy.mushrooms.map.ui.screens.ArchiveScreen
import leshy.mushrooms.map.ui.screens.DataScreen
import leshy.mushrooms.map.ui.screens.FindsMapScreen
import leshy.mushrooms.map.ui.screens.HelpScreen
import leshy.mushrooms.map.ui.screens.LanguagePickerScreen
import leshy.mushrooms.map.ui.screens.MapScreen
import leshy.mushrooms.map.ui.screens.PreparationScreen
import leshy.mushrooms.map.ui.screens.RecordScreen
import leshy.mushrooms.map.ui.screens.AboutScreen
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
    // Один и тот же переход для всех семи разделов: «?» открывает лист со справкой поверх
    // текущего раздела, не трогая сохранённое состояние остальных.
    val onHelpClick: (HelpTopic) -> Unit = { topic -> navController.navigate(Destination.Help(topic)) }

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
                help = HelpTopic.RECORD,
                onMenuClick = onMenuClick,
                onHelpClick = onHelpClick,
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
                help = HelpTopic.ARCHIVE,
                onMenuClick = onMenuClick,
                onHelpClick = onHelpClick,
            ) { padding ->
                ArchiveScreen(
                    onWalkClick = { walkId -> navController.navigate(Destination.WalkDetail(walkId)) },
                    // Кнопка пустого состояния ведёт на домашний экран — а он top-level раздел, то
                    // есть только через navigateToTopLevel (см. CLAUDE.md и Destinations.kt).
                    onStartWalkClick = { navController.navigateToTopLevel(Destination.Record) },
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
        composable<Destination.Map> { backStackEntry ->
            SectionScaffold(
                title = StringKey.NavMap,
                help = HelpTopic.MAP,
                onMenuClick = onMenuClick,
                onHelpClick = onHelpClick,
            ) { padding ->
                MapScreen(
                    onStartWalkClick = { navController.navigateToTopLevel(Destination.Record) },
                    onOpenFullMap = { navController.navigate(Destination.FindsMap) },
                    viewModel = koinViewModel(viewModelStoreOwner = backStackEntry),
                    modifier = Modifier.padding(padding),
                )
            }
        }
        composable<Destination.FindsMap> {
            // ViewModel берётся у записи раздела «Карта» в бэкстеке, а не заводится своя, — тем же
            // приёмом и по той же причине, что WalkMap берёт её у WalkDetail: это тот же экран,
            // открытый во весь рост, и второй инстанс означал бы вторую подписку на всю базу и
            // повторное чтение всех треков ради тех же данных. Гвард — оттуда же: запись раздела
            // может уже уйти из бэкстека, пока этот композабл дорисовывает переход выхода.
            val mapEntry = runCatching {
                navController.getBackStackEntry(Destination.Map)
            }.getOrNull()
            if (mapEntry != null) {
                FindsMapScreen(
                    viewModel = koinViewModel(viewModelStoreOwner = mapEntry),
                    onBack = { navController.popBackStack() },
                )
            }
        }
        composable<Destination.Preparation> {
            SectionScaffold(
                title = StringKey.NavPreparation,
                help = HelpTopic.PREPARATION,
                onMenuClick = onMenuClick,
                onHelpClick = onHelpClick,
            ) { padding -> PreparationScreen(modifier = Modifier.padding(padding)) }
        }
        composable<Destination.Settings> {
            SectionScaffold(
                title = StringKey.SettingsTitle,
                help = HelpTopic.SETTINGS,
                onMenuClick = onMenuClick,
                onHelpClick = onHelpClick,
            ) { padding ->
                SettingsScreen(
                    onLanguageClick = { navController.navigate(Destination.LanguagePicker) },
                    onAboutClick = { navController.navigate(Destination.About) },
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
        composable<Destination.Help> { backStackEntry ->
            HelpScreen(
                topic = backStackEntry.toRoute<Destination.Help>().topic,
                onBack = { navController.popBackStack() },
            )
        }
        composable<Destination.About> {
            AboutScreen(onBack = { navController.popBackStack() })
        }
        composable<Destination.Data> {
            SectionScaffold(
                title = StringKey.NavData,
                help = HelpTopic.DATA,
                onMenuClick = onMenuClick,
                onHelpClick = onHelpClick,
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
                help = HelpTopic.SPECIES,
                onMenuClick = onMenuClick,
                onHelpClick = onHelpClick,
            ) { padding -> SpeciesScreen(modifier = Modifier.padding(padding)) }
        }
    }
}
