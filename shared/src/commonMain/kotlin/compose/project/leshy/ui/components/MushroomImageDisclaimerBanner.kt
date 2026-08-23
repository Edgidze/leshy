package compose.project.leshy.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.stringResource

/**
 * Shown above any collection/species picker that displays catalog icons, so users don't mistake
 * the app's illustrative icons for a real-world identification aid.
 */
@Composable
fun MushroomImageDisclaimerBanner(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
    ) {
        Text(
            text = stringResource(StringKey.MushroomImagesDisclaimer),
            modifier = Modifier.padding(12.dp),
            color = MaterialTheme.colorScheme.onErrorContainer,
        )
    }
}
