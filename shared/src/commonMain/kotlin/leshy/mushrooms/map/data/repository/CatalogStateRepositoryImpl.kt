package leshy.mushrooms.map.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import leshy.mushrooms.map.domain.repository.CatalogStateRepository
import kotlinx.coroutines.flow.first

private val SEEDED_CATALOG_VERSION_KEY = intPreferencesKey("seeded_catalog_version")
private val SEEDED_COUNTRIES_VERSION_KEY = intPreferencesKey("seeded_countries_version")

class CatalogStateRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : CatalogStateRepository {
    override suspend fun getSeededCatalogVersion(): Int? = dataStore.data.first()[SEEDED_CATALOG_VERSION_KEY]

    override suspend fun setSeededCatalogVersion(version: Int) {
        dataStore.edit { prefs -> prefs[SEEDED_CATALOG_VERSION_KEY] = version }
    }

    override suspend fun getSeededCountriesVersion(): Int? = dataStore.data.first()[SEEDED_COUNTRIES_VERSION_KEY]

    override suspend fun setSeededCountriesVersion(version: Int) {
        dataStore.edit { prefs -> prefs[SEEDED_COUNTRIES_VERSION_KEY] = version }
    }
}
