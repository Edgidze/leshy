package compose.project.leshy.presentation.species

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.CategorySource
import compose.project.leshy.domain.model.EdibilityStatus
import compose.project.leshy.domain.repository.CategoryRepository
import compose.project.leshy.domain.repository.CollectionRepository
import compose.project.leshy.domain.repository.SettingsRepository
import compose.project.leshy.domain.usecase.CreateOrUpdateUserSpeciesUseCase
import compose.project.leshy.domain.usecase.EnsureDefaultCategoriesUseCase
import compose.project.leshy.domain.usecase.EnsureDefaultCollectionsUseCase
import compose.project.leshy.domain.usecase.RecalculateFilterEligibilityUseCase
import compose.project.leshy.domain.usecase.SetCategoryPickedUseCase
import compose.project.leshy.domain.usecase.SetCollectionPickedUseCase
import compose.project.leshy.domain.usecase.ToggleUserSpeciesVisibilityUseCase
import compose.project.leshy.presentation.CollectionPickState
import compose.project.leshy.presentation.CollectionPickerItem
import compose.project.leshy.presentation.buildCollectionPickerItems
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

    fun saveSpecies(
        existing: Category?,
        name: String,
        scientificNameInput: String?,
        edibilityStatus: EdibilityStatus,
        colorHex: String,
        iconPngBytes: ByteArray?,
    ) {
        val language = _uiState.value.language
        viewModelScope.launch {
            createOrUpdateUserSpecies(existing, name, scientificNameInput, language, edibilityStatus, colorHex, iconPngBytes)
        }
    }
}
