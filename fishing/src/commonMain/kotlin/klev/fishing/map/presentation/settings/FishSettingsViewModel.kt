package klev.fishing.map.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import klev.fishing.map.data.repository.FishingSettingsRepository
import klev.fishing.map.domain.model.FishingMethod
import klev.fishing.map.domain.model.PressureUnit
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
    val methods: Set<FishingMethod> = FishingMethod.entries.toSet(),
)

/**
 * Язык и оформление НЕ дублируются: берутся из `SettingsRepository` в `:shared` — тот же DataStore и
 * тот же список 42 языков, что у грибного приложения. Здесь своё — единицы давления и способы ловли.
 *
 * Списка видов здесь больше нет: он уехал в свой раздел «Виды рыб» — десятки плиток в свитке
 * настроек были самым длинным блоком экрана, и искать в нём настройку давления приходилось мимо них.
 */
class FishSettingsViewModel(
    private val settings: SettingsRepository,
    private val fishingSettings: FishingSettingsRepository,
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
            fishingSettings.observeMethods().collect { methods -> _uiState.update { it.copy(methods = methods) } }
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

    /**
     * Переключение одного способа. Снятие ПОСЛЕДНЕГО не запрещается кнопкой, а трактуется хранилищем
     * как «все» (`FishingSettingsRepository.observeMethods`): запрещать сложнее и объяснять нечем, а
     * экран старта без единой кнопки — настоящая поломка.
     */
    fun toggleMethod(method: FishingMethod) {
        val current = _uiState.value.methods
        val next = if (method in current) current - method else current + method
        viewModelScope.launch { fishingSettings.setMethods(next) }
    }
}
