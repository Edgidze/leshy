package compose.project.leshy

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import compose.project.leshy.data.repository.MapStyleCacheRepository
import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.domain.model.MUSHROOM_MARKER_SIZE_SCALE_DEFAULT
import compose.project.leshy.domain.repository.OnboardingRepository
import compose.project.leshy.domain.repository.SettingsRepository
import compose.project.leshy.domain.usecase.RepairPhotoPathsUseCase
import compose.project.leshy.i18n.LocalAppLanguage
import compose.project.leshy.ui.map.LocalMushroomMarkerSizeScale
import compose.project.leshy.ui.navigation.LeshyNavHost
import compose.project.leshy.ui.screens.OnboardingScreen
import compose.project.leshy.ui.theme.LeshyTheme
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    val settingsRepository = koinInject<SettingsRepository>()
    val onboardingRepository = koinInject<OnboardingRepository>()
    val language by settingsRepository.observeLanguage().collectAsState(initial = AppLanguage.EN)
    val mushroomMarkerSizeScale by settingsRepository.observeMushroomMarkerSizeScale()
        .collectAsState(initial = MUSHROOM_MARKER_SIZE_SCALE_DEFAULT)
    // null while the persisted flag is still loading. Deliberately kept outside the nav graph
    // entirely (see OnboardingScreen's doc, .claude/plans/mushroom-collections.md Phase 3) rather
    // than made a NavHost destination — Home must stay the graph's only startDestination, or
    // navigateToTopLevel's popUpTo(graph.findStartDestination()) breaks for every top-level screen
    // afterward (see ui/navigation/CLAUDE.md).
    val onboardingCompleted by produceState<Boolean?>(initialValue = null) {
        onboardingRepository.observeCollectionPickerCompleted().collect { value = it }
    }

    CompositionLocalProvider(
        LocalAppLanguage provides language,
        LocalMushroomMarkerSizeScale provides mushroomMarkerSizeScale,
    ) {
        LeshyTheme {
            when (onboardingCompleted) {
                null -> Unit
                false -> OnboardingScreen()
                true -> {
                    val navController = rememberNavController()
                    // Runs once per cold launch, before the user can reach ANY screen that shows a
                    // marker photo (Home/Запись/Карта/Архив alike) — see RepairPhotoPathsUseCase.
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
                    LaunchedEffect(Unit) { mapStyleCacheRepository.ensureLoaded() }
                    LeshyNavHost(navController)
                }
            }
        }
    }
}
