package compose.project.leshy.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.stringResource

/**
 * Floating "Filters: N" button, styled with the same tonal-button language as the Карта/
 * Статистика [androidx.compose.material3.SegmentedButton]s above it on the Map screen — reused
 * identically on the Record screen's live map. Border matches the record screen's round side
 * buttons so the two button families read as one visual language there.
 */
@Composable
fun MapFilterButton(filterCount: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Icon(imageVector = Icons.Filled.FilterList, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("${stringResource(StringKey.MapFilterButtonLabel)}: $filterCount")
    }
}
