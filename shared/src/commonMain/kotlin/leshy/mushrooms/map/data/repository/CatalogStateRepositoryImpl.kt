package leshy.mushrooms.map.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import leshy.mushrooms.map.domain.repository.CatalogStateRepository
import kotlinx.coroutines.flow.first

private val SEEDED_CATALOG_VERSION_KEY = intPreferencesKey("seeded_catalog_version")
private val SEEDED_COUNTRIES_VERSION_KEY = intPreferencesKey("seeded_countries_version")
private val RETIRED_SPECIES_KEYS = stringSetPreferencesKey("retired_species_keys")

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

    override suspend fun getRetiredSpeciesKeys(): Set<String> =
        dataStore.data.first()[RETIRED_SPECIES_KEYS].orEmpty()

    override suspend fun addRetiredSpeciesKeys(keys: Set<String>) {
        dataStore.edit { prefs -> prefs[RETIRED_SPECIES_KEYS] = prefs[RETIRED_SPECIES_KEYS].orEmpty() + keys }
    }
}
