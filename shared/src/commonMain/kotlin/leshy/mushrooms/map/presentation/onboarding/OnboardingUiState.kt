package leshy.mushrooms.map.presentation.onboarding

import leshy.mushrooms.map.presentation.CollectionPickerItem

data class OnboardingUiState(
    val collectionPickerItems: List<CollectionPickerItem> = emptyList(),
)
