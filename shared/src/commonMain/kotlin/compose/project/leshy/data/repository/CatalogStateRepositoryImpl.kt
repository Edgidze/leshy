package compose.project.leshy.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import compose.project.leshy.domain.repository.CatalogStateRepository
import kotlinx.coroutines.flow.first

private val SEEDED_CATALOG_VERSION_KEY = intPreferencesKey("seeded_catalog_version")

class CatalogStateRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : CatalogStateRepository {
    override suspend fun getSeededCatalogVersion(): Int? = dataStore.data.first()[SEEDED_CATALOG_VERSION_KEY]

    override suspend fun setSeededCatalogVersion(version: Int) {
        dataStore.edit { prefs -> prefs[SEEDED_CATALOG_VERSION_KEY] = version }
    }
}
