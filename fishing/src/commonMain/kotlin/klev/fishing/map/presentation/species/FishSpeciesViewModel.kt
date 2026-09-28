package klev.fishing.map.presentation.species

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import klev.fishing.map.data.catalog.FishCountriesSource
import klev.fishing.map.data.repository.FishingSettingsRepository
import klev.fishing.map.domain.model.FishSpecies
import klev.fishing.map.domain.repository.SpeciesRepository
import klev.fishing.map.domain.usecase.ApplyCountryCollectionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import leshy.mushrooms.map.data.platform.currentDeviceRegionCode

data class FishSpeciesUiState(
    val isLoading: Boolean = true,
    val species: List<FishSpecies> = emptyList(),
    /** Коды стран, для которых есть подборка, — из `fish_countries.json`. */
    val countryCodes: List<String> = emptyList(),
    /** Код страны последней применённой подборки; `null` — ленту собирали руками. */
    val appliedCountry: String? = null,
    /** Страна устройства, если для неё подборка есть: с неё открывается список выбора. */
    val deviceCountry: String? = null,
) {
    val shownCount: Int get() = species.count { it.isActive }
}

/**
 * Раздел «Виды рыб»: что стоит в ленте «Рыбалки», и подборки по странам.
 *
 * Живёт отдельно от настроек, хотя переключает то же поле `isActive`: список видов — это лента
 * плиток, а не строка настройки, и в свитке настроек он был самым длинным блоком экрана. Сюда же
 * потом приедут свои виды пользователя.
 */
class FishSpeciesViewModel(
    private val species: SpeciesRepository,
    private val countries: FishCountriesSource,
    private val applyCountry: ApplyCountryCollectionUseCase,
    private val settings: FishingSettingsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FishSpeciesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val codes = countries.entries.map { it.code }
        val device = currentDeviceRegionCode()?.uppercase()?.takeIf { it in codes }
        _uiState.update { it.copy(countryCodes = codes, deviceCountry = device) }
        viewModelScope.launch {
            species.observeAll().collect { list ->
                _uiState.update { it.copy(species = list, isLoading = false) }
            }
        }
        viewModelScope.launch {
            settings.observeCountry().collect { code ->
                _uiState.update { it.copy(appliedCountry = code) }
            }
        }
    }

    /**
     * Ручное переключение вида. Оно же снимает отметку о подборке: после правки руками лента
     * больше не равна подборке, и показывать «Подборка: Россия» над списком, который человек с тех
     * пор поменял, значит врать.
     */
    fun toggle(id: Long) {
        val current = _uiState.value.species.firstOrNull { it.id == id } ?: return
        viewModelScope.launch {
            species.setActive(id, !current.isActive)
            settings.clearCountry()
        }
    }

    fun applyCountryCollection(code: String) {
        viewModelScope.launch { applyCountry(code) }
    }

    /** Показать все виды каталога — выход из любой подборки. */
    fun showAll() {
        viewModelScope.launch {
            species.getAll().forEach { item ->
                if (!item.isActive) species.setActive(item.id, true)
            }
            settings.clearCountry()
        }
    }
}
