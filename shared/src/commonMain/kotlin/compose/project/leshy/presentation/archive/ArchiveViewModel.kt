package compose.project.leshy.presentation.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.leshy.domain.repository.WalkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArchiveViewModel(
    private val walkRepository: WalkRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArchiveUiState())
    val uiState: StateFlow<ArchiveUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            walkRepository.observeAll().collect { walks ->
                _uiState.update { it.copy(walks = walks) }
            }
        }
    }
}
