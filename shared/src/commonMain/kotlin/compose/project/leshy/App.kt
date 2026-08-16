package compose.project.leshy

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.domain.model.MUSHROOM_MARKER_SIZE_SCALE_DEFAULT
import compose.project.leshy.domain.repository.SettingsRepository
import compose.project.leshy.i18n.LocalAppLanguage
import compose.project.leshy.ui.map.LocalMushroomMarkerSizeScale
import compose.project.leshy.ui.navigation.LeshyNavHost
import compose.project.leshy.ui.theme.LeshyTheme
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    val settingsRepository = koinInject<SettingsRepository>()
    val language by settingsRepository.observeLanguage().collectAsState(initial = AppLanguage.EN)
    val mushroomMarkerSizeScale by settingsRepository.observeMushroomMarkerSizeScale()
        .collectAsState(initial = MUSHROOM_MARKER_SIZE_SCALE_DEFAULT)

    CompositionLocalProvider(
        LocalAppLanguage provides language,
        LocalMushroomMarkerSizeScale provides mushroomMarkerSizeScale,
    ) {
        LeshyTheme {
            val navController = rememberNavController()
            LeshyNavHost(navController)
        }
    }
}
