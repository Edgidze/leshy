package klev.fishing.map.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import klev.fishing.map.domain.model.PressureUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Настройки, которых нет у грибов. Язык и оформление НЕ дублируются: они берутся из
 * `SettingsRepository` в `:shared` — тот же DataStore, тот же механизм, уже переведённый на 42
 * языка. Здесь только своё: единицы давления.
 *
 * Мм рт. ст. — не экзотика: в России давление обсуждают именно в них, и рыбацкий дневник без этой
 * настройки заставлял бы пересчитывать в голове каждую запись.
 */
class FishingSettingsRepository(private val dataStore: DataStore<Preferences>) {
    private val pressureUnitKey = stringPreferencesKey("fishing_pressure_unit")

    fun observePressureUnit(): Flow<PressureUnit> = dataStore.data.map { prefs ->
        prefs[pressureUnitKey]?.let { stored -> PressureUnit.entries.firstOrNull { it.name == stored } }
            ?: PressureUnit.HPA
    }

    suspend fun setPressureUnit(unit: PressureUnit) {
        dataStore.edit { it[pressureUnitKey] = unit.name }
    }
}
