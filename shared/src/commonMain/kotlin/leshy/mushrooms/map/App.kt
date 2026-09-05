package leshy.mushrooms.map

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import leshy.mushrooms.map.data.platform.currentDeviceLanguage
import leshy.mushrooms.map.data.repository.MapStyleCacheRepository
import leshy.mushrooms.map.domain.model.MUSHROOM_MARKER_SIZE_SCALE_DEFAULT
import leshy.mushrooms.map.domain.model.ThemeMode
import leshy.mushrooms.map.domain.repository.OnboardingRepository
import leshy.mushrooms.map.domain.repository.SettingsRepository
import leshy.mushrooms.map.domain.usecase.RepairPhotoPathsUseCase
import leshy.mushrooms.map.i18n.LocalAppLanguage
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.stringResource
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.ic_mushrooms_inset
import org.jetbrains.compose.resources.painterResource
import leshy.mushrooms.map.ui.map.LocalMushroomMarkerSizeScale
import leshy.mushrooms.map.ui.navigation.Destination
import leshy.mushrooms.map.ui.navigation.LeshyNavHost
import leshy.mushrooms.map.ui.navigation.navigateToTopLevel
import leshy.mushrooms.map.ui.screens.OnboardingScreen
import leshy.mushrooms.map.ui.theme.ApplySystemBarsAppearance
import leshy.mushrooms.map.ui.theme.LeshyTheme
import leshy.mushrooms.map.ui.theme.isDark
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * [icon] — не `ImageVector`, а поставщик `Painter`: шесть пунктов берут значок из Material
 * (вектор), седьмой — из `composeResources` (растр, см. `ic_mushrooms.webp`), и общий тип нужен,
 * чтобы список оставался одним списком. `rememberVectorPainter` приводит вектор к `Painter`
 * без потерь.
 */
private data class DrawerNavEntry(
    val destination: Destination,
    val labelKey: StringKey,
    val icon: @Composable () -> Painter,
)

/** Размер значка пункта меню — тот же, что Material подставляет своим иконкам по умолчанию. */
private val DRAWER_ICON_SIZE = 24.dp

private val drawerNavEntries = listOf(
    DrawerNavEntry(Destination.Record, StringKey.NavRecord) { rememberVectorPainter(Icons.Filled.Hiking) },
    DrawerNavEntry(Destination.Archive, StringKey.NavArchive) {
        rememberVectorPainter(Icons.AutoMirrored.Filled.List)
    },
    DrawerNavEntry(Destination.Map, StringKey.NavMap) { rememberVectorPainter(Icons.Filled.Place) },
    DrawerNavEntry(Destination.Preparation, StringKey.NavPreparation) {
        rememberVectorPainter(Icons.Filled.Download)
    },
    DrawerNavEntry(Destination.Settings, StringKey.SettingsTitle) { rememberVectorPainter(Icons.Filled.Settings) },
    // Единственный не-стоковый пункт: гриба в Material нет. Ресурс здесь отдельный, с внутренним
    // отступом: у иконок Material содержимое занимает около 20 единиц из 24, и значок, заполняющий
    // своё поле целиком, при одинаковом размере выглядел бы в этом столбце крупнее всех соседей.
    DrawerNavEntry(Destination.Species, StringKey.NavSpecies) {
        painterResource(Res.drawable.ic_mushrooms_inset)
    },
    DrawerNavEntry(Destination.Data, StringKey.NavData) { rememberVectorPainter(Icons.Filled.ImportExport) },
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun App() {
    val settingsRepository = koinInject<SettingsRepository>()
    val onboardingRepository = koinInject<OnboardingRepository>()
    // Начальное значение — язык системы, а не EN: `initial` показывается ровно до первой эмиссии
    // DataStore, и на холодном старте это те кадры, в которых уже нарисован приветственный экран.
    // Тот же дефолт, что отдаёт сам репозиторий, когда язык ещё не выбран (SettingsRepositoryImpl).
    val language by settingsRepository.observeLanguage().collectAsState(initial = currentDeviceLanguage())
    val mushroomMarkerSizeScale by settingsRepository.observeMushroomMarkerSizeScale()
        .collectAsState(initial = MUSHROOM_MARKER_SIZE_SCALE_DEFAULT)
    val themeMode by settingsRepository.observeThemeMode().collectAsState(initial = ThemeMode.SYSTEM)
    // null while the persisted flag is still loading. Deliberately kept outside the nav graph
    // entirely (see OnboardingScreen's doc, .claude/plans/mushroom-collections.md Phase 3) rather
    // than made a NavHost destination — Record must stay the graph's only startDestination, or
    // navigateToTopLevel's popUpTo(graph.findStartDestination()) breaks for every top-level screen
    // afterward (see ui/navigation/CLAUDE.md).
    val onboardingCompleted by produceState<Boolean?>(initialValue = null) {
        onboardingRepository.observeCollectionPickerCompleted().collect { value = it }
    }

    CompositionLocalProvider(
        LocalAppLanguage provides language,
        LocalMushroomMarkerSizeScale provides mushroomMarkerSizeScale,
    ) {
        val useDarkTheme = themeMode.isDark()
        LeshyTheme(useDarkTheme = useDarkTheme) {
            // Выше `when (onboardingCompleted)`, а не в его ветках: системные панели должны
            // подхватывать тему и на онбординге, а не только после того, как он пройден.
            ApplySystemBarsAppearance(themeMode)
            when (onboardingCompleted) {
                null -> Unit
                false -> OnboardingScreen()
                true -> {
                    val navController = rememberNavController()
                    // Runs once per cold launch, before the user can reach ANY screen that shows a
                    // marker photo (Запись/Карта/Архив alike) — see RepairPhotoPathsUseCase.
                    // Centralized here instead of per-ViewModel so it doesn't depend on which
                    // screen the user happens to open first (Record used to have no repair path at
                    // all, since it never injected this use case).
                    val repairPhotoPaths: RepairPhotoPathsUseCase = koinInject()
                    // Also load the pinned map style here, not only lazily from each map screen's
                    // own composable (which still call it too — safe, ensureLoaded() is a no-op once
                    // done). Reason: PinnedStyleInterceptor only has bytes to serve once this has run
                    // at least once, and on "Подготовка" specifically, the map screen's own
                    // ensureLoaded() call races against PreparationViewModel.init — which is what
                    // first creates the native OfflineManager (and thus initializes MapLibre's native
                    // SDK, which can auto-resume a pack left ACTIVE from a killed app) — with no
                    // ordering guarantee between the two. Running it here first closes that race:
                    // by the time any screen can be reached, the interceptor already has bytes.
                    val mapStyleCacheRepository: MapStyleCacheRepository = koinInject()
                    // Two independent effects, not one sequential block — neither should wait on the
                    // other to start.
                    LaunchedEffect(Unit) { repairPhotoPaths() }
                    // Keyed on `language`, before ensureLoaded()'s effect: on a cold start this
                    // just records the language (nothing is pinned yet, so ensureLoaded() then
                    // publishes already-localized bytes), and on a later switch in Settings it
                    // re-localizes the pinned style in place. Never touches the network, the pinned
                    // file or offline packs — see MapStyleCacheRepository.setLabelLanguage.
                    LaunchedEffect(language) { mapStyleCacheRepository.setLabelLanguage(language) }
                    // Same shape as the language effect above and equally free of network/disk:
                    // the dark variant is a repaint of the pinned style that changes no URL, so it
                    // never touches offline packs. See MapStyleCacheRepository.setDarkTheme.
                    LaunchedEffect(useDarkTheme) { mapStyleCacheRepository.setDarkTheme(useDarkTheme) }
                    LaunchedEffect(Unit) { mapStyleCacheRepository.ensureLoaded() }

                    val backStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = backStackEntry?.destination
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        // Swipe-from-left-edge to open is ambiguous with map panning on the Record
                        // screen (that gesture conflict is why it was turned off historically) —
                        // opening is only ever through the hamburger button.
                        gesturesEnabled = false,
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
                                        text = stringResource(StringKey.AppName),
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
                                        icon = {
                                            // Размер обязателен именно здесь. Material подставляет
                                            // свои 24.dp только тем значкам, у которых нет
                                            // собственного размера; у растрового Painter он есть —
                                            // 192px, то есть около 73dp, — и гриб растекался на всю
                                            // высоту строки, вчетверо крупнее соседей.
                                            Icon(
                                                painter = entry.icon(),
                                                contentDescription = null,
                                                modifier = Modifier.size(DRAWER_ICON_SIZE),
                                            )
                                        },
                                        onClick = {
                                            scope.launch { drawerState.close() }
                                            navController.navigateToTopLevel(entry.destination)
                                        },
                                    )
                                }
                            }
                        },
                    ) {
                        LeshyNavHost(
                            navController = navController,
                            onMenuClick = { scope.launch { drawerState.open() } },
                        )
                    }

                    // Composed AFTER ModalNavigationDrawer/LeshyNavHost above, not before: the
                    // back dispatcher gives priority to whichever BackHandler/PredictiveBackHandler
                    // registered LAST, and NavHost registers its own internal one as part of
                    // composing LeshyNavHost. Registering ours first (i.e. above the drawer) let
                    // NavHost's own back handling win whenever both were enabled — system back with
                    // the drawer open on e.g. "Настройки" popped the screen underneath straight to
                    // Record while the drawer stayed open, instead of just closing the drawer. The
                    // KMP ModalNavigationDrawer (unlike the Android-only one) doesn't close itself
                    // on system back on its own — only on scrim click, Escape, or swipe (disabled
                    // above) — hence this explicit handler at all.
                    BackHandler(enabled = drawerState.isOpen) { scope.launch { drawerState.close() } }
                }
            }
        }
    }
}
