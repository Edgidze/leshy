package compose.project.leshy.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.presentation.archive.ArchiveViewModel
import compose.project.leshy.ui.components.WalkCard
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ArchiveScreen(onWalkClick: (Long) -> Unit, viewModel: ArchiveViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.walks.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(stringResource(StringKey.ArchiveEmpty))
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(uiState.walks, key = { it.id }) { walk ->
                WalkCard(walk = walk, onClick = { onWalkClick(walk.id) })
            }
        }
    }
}
