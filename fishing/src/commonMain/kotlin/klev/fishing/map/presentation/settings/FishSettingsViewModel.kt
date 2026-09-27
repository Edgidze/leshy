package klev.fishing.map.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import klev.fishing.map.data.repository.FishingSettingsRepository
import klev.fishing.map.domain.model.FishSpecies
import klev.fishing.map.domain.model.PressureUnit
import klev.fishing.map.domain.repository.SpeciesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.domain.model.ThemeMode
import leshy.mushrooms.map.domain.repository.SettingsRepository

data class FishSettingsUiState(
    val language: AppLanguage = AppLanguage.EN,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val pressureUnit: PressureUnit = PressureUnit.HPA,
    val species: List<FishSpecies> = emptyList(),
)

/**
 * Язык и оформление НЕ дублируются: берутся из `SettingsRepository` в `:shared` — тот же DataStore и
 * тот же список 42 языков, что у грибного приложения. Здесь своё только единицы давления и список
 * видов.
 */
class FishSettingsViewModel(
    private val settings: SettingsRepository,
    private val fishingSettings: FishingSettingsRepository,
    private val species: SpeciesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FishSettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settings.observeLanguage().collect { language -> _uiState.update { it.copy(language = language) } }
        }
        viewModelScope.launch {
            settings.observeThemeMode().collect { mode -> _uiState.update { it.copy(themeMode = mode) } }
        }
        viewModelScope.launch {
            fishingSettings.observePressureUnit().collect { unit -> _uiState.update { it.copy(pressureUnit = unit) } }
        }
        viewModelScope.launch {
            species.observeAll().collect { list -> _uiState.update { it.copy(species = list) } }
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { settings.setLanguage(language) }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settings.setThemeMode(mode) }
    }

    fun setPressureUnit(unit: PressureUnit) {
        viewModelScope.launch { fishingSettings.setPressureUnit(unit) }
    }

    fun setSpeciesActive(id: Long, isActive: Boolean) {
        viewModelScope.launch { species.setActive(id, isActive) }
    }
}
