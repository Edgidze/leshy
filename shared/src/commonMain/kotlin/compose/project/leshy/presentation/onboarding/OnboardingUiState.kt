package compose.project.leshy.presentation.onboarding

import compose.project.leshy.presentation.CollectionPickerItem

data class OnboardingUiState(
    val collectionPickerItems: List<CollectionPickerItem> = emptyList(),
)
