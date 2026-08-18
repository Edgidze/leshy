package compose.project.leshy.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import compose.project.leshy.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val COLLECTION_PICKER_COMPLETED_KEY = booleanPreferencesKey("onboarding_collection_picker_completed")

class OnboardingRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : OnboardingRepository {
    override fun observeCollectionPickerCompleted(): Flow<Boolean> =
        dataStore.data.map { prefs -> prefs[COLLECTION_PICKER_COMPLETED_KEY] ?: false }

    override suspend fun setCollectionPickerCompleted() {
        dataStore.edit { prefs -> prefs[COLLECTION_PICKER_COMPLETED_KEY] = true }
    }
}
