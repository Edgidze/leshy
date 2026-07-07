package compose.project.leshy.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.screen_record_placeholder
import org.jetbrains.compose.resources.stringResource

@Composable
fun RecordScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(stringResource(Res.string.screen_record_placeholder))
    }
}
