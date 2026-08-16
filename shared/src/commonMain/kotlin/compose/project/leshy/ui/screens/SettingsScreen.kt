package compose.project.leshy.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.presentation.settings.SettingsViewModel
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.leshy_logo
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Image(
                painter = painterResource(Res.drawable.leshy_logo),
                contentDescription = stringResource(StringKey.AppName),
                modifier = Modifier.size(96.dp),
            )
        }

        Text(
            stringResource(StringKey.SettingsLanguageTitle),
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
        )
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            AppLanguage.entries.forEachIndexed { index, language ->
                SegmentedButton(
                    selected = uiState.language == language,
                    onClick = { viewModel.setLanguage(language) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = AppLanguage.entries.size),
                ) {
                    Text(language.displayName)
                }
            }
        }
    }
}
