package klev.fishing.map.presentation.species

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import klev.fishing.map.domain.model.FishSpecies
import klev.fishing.map.domain.repository.SpeciesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FishSpeciesUiState(
    val isLoading: Boolean = true,
    val species: List<FishSpecies> = emptyList(),
) {
    val shownCount: Int get() = species.count { it.isActive }
}

/**
 * Раздел «Виды рыб». Живёт отдельно от настроек, хотя переключает то же самое поле: список видов —
 * это лента плиток, а не строка настройки, и в свитке настроек он был самым длинным блоком экрана.
 * Сюда же потом приедут свои виды пользователя и подборки по регионам.
 */
class FishSpeciesViewModel(private val species: SpeciesRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(FishSpeciesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            species.observeAll().collect { list ->
                _uiState.update { it.copy(species = list, isLoading = false) }
            }
        }
    }

    fun toggle(id: Long) {
        val current = _uiState.value.species.firstOrNull { it.id == id } ?: return
        viewModelScope.launch { species.setActive(id, !current.isActive) }
    }
}
