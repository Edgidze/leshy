package compose.project.leshy

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.domain.model.MUSHROOM_MARKER_SIZE_SCALE_DEFAULT
import compose.project.leshy.domain.repository.OnboardingRepository
import compose.project.leshy.domain.repository.SettingsRepository
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
                    LeshyNavHost(navController)
                }
            }
        }
    }
}
