package leshy.mushrooms.map.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.presentation.onboarding.OnboardingViewModel
import leshy.mushrooms.map.ui.components.CollectionPicker
import leshy.mushrooms.map.ui.components.MushroomImageDisclaimerBanner
import org.koin.compose.viewmodel.koinViewModel

/**
 * First-run screen shown once before Home — see `.claude/plans/mushroom-collections.md`, Phase 3.
 * Not a NavHost destination: [leshy.mushrooms.map.App] renders this in place of the whole nav
 * graph until the onboarding flag is set, so Home stays the graph's only real startDestination
 * (see `ui/navigation/CLAUDE.md` on why that matters for `navigateToTopLevel`).
 */
@Composable
fun OnboardingScreen(modifier: Modifier = Modifier, viewModel: OnboardingViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).padding(16.dp)) {
        Text(text = stringResource(StringKey.OnboardingTitle), style = MaterialTheme.typography.headlineSmall)
        Text(
            text = stringResource(StringKey.OnboardingDescription),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
        )
        MushroomImageDisclaimerBanner(modifier = Modifier.padding(bottom = 8.dp))
        CollectionPicker(
            items = uiState.collectionPickerItems,
            onToggleCollection = viewModel::toggleCollection,
            onToggleCategory = viewModel::setCategoryPicked,
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
        )
        Button(onClick = viewModel::finish, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Text(stringResource(StringKey.OnboardingContinueButton))
        }
    }
}
