package klev.fishing.map.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import klev.fishing.map.i18n.FishStringKey
import klev.fishing.map.i18n.fishStringResource
import klev.fishing.map.presentation.species.FishSpeciesViewModel
import klev.fishing.map.ui.components.FishSectionScaffold
import klev.fishing.map.ui.components.RECORD_FISH_TILE_WIDTH
import klev.fishing.map.ui.components.SpeciesTile
import org.koin.compose.viewmodel.koinViewModel

/**
 * Виды рыб плитками, касание — «в ленте»/«скрыт».
 *
 * Сетка адаптивная по ширине плитки, а не в фиксированное число столбцов: плитка вида имеет
 * собственную ширину под формат иллюстрации (2:1), и растягивать её под ширину экрана нельзя —
 * картинка поехала бы (правило проекта про потолок ширины у растров).
 */
@Composable
fun FishSpeciesScreen(
    onMenuClick: () -> Unit,
    viewModel: FishSpeciesViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FishSectionScaffold(title = FishStringKey.SpeciesTitle, onMenuClick = onMenuClick) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = RECORD_FISH_TILE_WIDTH),
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                Text(
                    text = fishStringResource(FishStringKey.SpeciesHint),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
            items(uiState.species, key = { it.id }) { species ->
                SpeciesTile(
                    species = species,
                    count = 0,
                    hidden = !species.isActive,
                    onClick = { viewModel.toggle(species.id) },
                )
            }
        }
    }
}
