package leshy.mushrooms.map.presentation.onboarding

import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.presentation.CollectionPickerItem

/**
 * The first run is two steps, in this order: pick the interface language, then pick the countries
 * whose mushrooms to track. Language first because everything on the second step — the country
 * names, the species names, the explanatory text — is only readable once it is in a language the
 * user actually knows.
 */
enum class OnboardingStep { LANGUAGE, COLLECTIONS }

data class OnboardingUiState(
    val step: OnboardingStep = OnboardingStep.LANGUAGE,
    val language: AppLanguage = AppLanguage.EN,
    val collectionPickerItems: List<CollectionPickerItem> = emptyList(),
)
