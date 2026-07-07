package compose.project.leshy.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import compose.project.leshy.presentation.record.RecordViewModel
import compose.project.leshy.ui.map.LiveTrackMap
import compose.project.leshy.ui.map.MapMarker
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.record_map_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun RecordMapScreen(viewModel: RecordViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val categoryColorById = uiState.categories.associate { it.id to it.colorHex }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.record_map_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { padding ->
        LiveTrackMap(
            track = uiState.trackPoints,
            markers = uiState.marks.map { mark ->
                MapMarker(
                    lat = mark.lat,
                    lon = mark.lon,
                    colorHex = categoryColorById[mark.categoryId] ?: "#808080",
                )
            },
            currentLocation = uiState.currentLocation,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
