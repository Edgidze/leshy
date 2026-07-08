package compose.project.leshy.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val LANGUAGE_KEY = stringPreferencesKey("language")

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {
    override fun observeLanguage(): Flow<AppLanguage> = dataStore.data.map { prefs ->
        prefs[LANGUAGE_KEY]?.let { code -> AppLanguage.entries.find { it.code == code } } ?: AppLanguage.EN
    }

    override suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { prefs -> prefs[LANGUAGE_KEY] = language.code }
    }
}
