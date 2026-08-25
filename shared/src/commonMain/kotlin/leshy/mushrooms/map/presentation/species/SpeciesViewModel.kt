package leshy.mushrooms.map.presentation.species

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.domain.model.CategorySource
import leshy.mushrooms.map.domain.repository.CategoryRepository
import leshy.mushrooms.map.domain.repository.CollectionRepository
import leshy.mushrooms.map.domain.repository.SettingsRepository
import leshy.mushrooms.map.domain.usecase.CreateOrUpdateUserSpeciesUseCase
import leshy.mushrooms.map.domain.usecase.DeleteUserSpeciesUseCase
import leshy.mushrooms.map.domain.usecase.EnsureDefaultCategoriesUseCase
import leshy.mushrooms.map.domain.usecase.EnsureDefaultCollectionsUseCase
import leshy.mushrooms.map.domain.usecase.RecalculateFilterEligibilityUseCase
import leshy.mushrooms.map.domain.usecase.SetCategoryPickedUseCase
import leshy.mushrooms.map.domain.usecase.SetCollectionPickedUseCase
import leshy.mushrooms.map.domain.usecase.ToggleUserSpeciesVisibilityUseCase
import leshy.mushrooms.map.presentation.CollectionPickState
import leshy.mushrooms.map.presentation.CollectionPickerItem
import leshy.mushrooms.map.presentation.buildCollectionPickerItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Backs the "Грибы" ("Mushrooms") section (`.claude/plans/user-mushrooms.md`, Phase 4) —
 * collections (moved here from `SettingsViewModel`, which used to host the collection picker) plus
 * the "My mushrooms" list of user-created/imported species.
 */
class SpeciesViewModel(
    private val settingsRepository: SettingsRepository,
    private val categoryRepository: CategoryRepository,
    private val collectionRepository: CollectionRepository,
    private val ensureDefaultCategories: EnsureDefaultCategoriesUseCase,
    private val ensureDefaultCollections: EnsureDefaultCollectionsUseCase,
    private val recalculateFilterEligibility: RecalculateFilterEligibilityUseCase,
    private val setCollectionPickedUseCase: SetCollectionPickedUseCase,
    private val setCategoryPickedUseCase: SetCategoryPickedUseCase,
    private val toggleUserSpeciesVisibility: ToggleUserSpeciesVisibilityUseCase,
    private val createOrUpdateUserSpecies: CreateOrUpdateUserSpeciesUseCase,
    private val deleteUserSpecies: DeleteUserSpeciesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpeciesUiState())
    val uiState: StateFlow<SpeciesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // The "Грибы" section can be the very first screen a user ever opens — same
            // idempotent seeding RecordViewModel/SettingsViewModel already do on their own init.
            ensureDefaultCategories()
            ensureDefaultCollections()
            recalculateFilterEligibility()
        }
        viewModelScope.launch {
            combine(
                collectionRepository.observeAll(),
                categoryRepository.observeAll(),
                collectionRepository.observeAllMemberships(),
            ) { collections, categories, memberships ->
                buildCollectionPickerItems(collections, categories, memberships)
            }.collect { items ->
                _uiState.update { it.copy(collectionPickerItems = items) }
            }
        }
        viewModelScope.launch {
            categoryRepository.observeNonCatalog().collect { species ->
                val ordered = species.sortedWith(compareBy({ it.source != CategorySource.USER }, { it.order }))
                _uiState.update { it.copy(userSpecies = ordered) }
            }
        }
        viewModelScope.launch {
            settingsRepository.observeLanguage().collect { language ->
                _uiState.update { it.copy(language = language) }
            }
        }
    }

    /** Tri-state click convention: anything short of fully picked selects every member, only a
     * fully-picked collection deselects them all. */
    fun toggleCollection(item: CollectionPickerItem) {
        val picked = item.pickState != CollectionPickState.ALL
        viewModelScope.launch { setCollectionPickedUseCase(item.collection.id, picked) }
    }

    fun setCategoryPicked(category: Category, picked: Boolean) {
        viewModelScope.launch { setCategoryPickedUseCase(category, picked) }
    }

    fun toggleSpeciesVisibility(category: Category) {
        viewModelScope.launch { toggleUserSpeciesVisibility(category, !category.isPicked) }
    }

    fun onDeleteSpeciesClick(category: Category) {
        _uiState.update { it.copy(pendingDelete = category) }
    }

    fun onDeleteSpeciesDismiss() {
        _uiState.update { it.copy(pendingDelete = null) }
    }

    fun onDeleteSpeciesConfirm() {
        val target = _uiState.value.pendingDelete ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(pendingDelete = null) }
            deleteUserSpecies(target)
        }
    }

    fun saveSpecies(
        existing: Category?,
        name: String,
        scientificNameInput: String?,
        colorHex: String,
        iconPngBytes: ByteArray?,
    ) {
        val language = _uiState.value.language
        viewModelScope.launch {
            createOrUpdateUserSpecies(existing, name, scientificNameInput, language, colorHex, iconPngBytes)
        }
    }
}
