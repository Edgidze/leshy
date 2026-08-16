package compose.project.leshy

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.domain.repository.SettingsRepository
import compose.project.leshy.i18n.LocalAppLanguage
import compose.project.leshy.ui.navigation.LeshyNavHost
import compose.project.leshy.ui.theme.LeshyTheme
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    val settingsRepository = koinInject<SettingsRepository>()
    val language by settingsRepository.observeLanguage().collectAsState(initial = AppLanguage.EN)

    CompositionLocalProvider(LocalAppLanguage provides language) {
        LeshyTheme {
            val navController = rememberNavController()
            LeshyNavHost(navController)
        }
    }
}
