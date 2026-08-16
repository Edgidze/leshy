package compose.project.leshy.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.observeLanguage().collect { language ->
                _uiState.update { it.copy(language = language) }
            }
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { settingsRepository.setLanguage(language) }
    }
}
