package compose.project.leshy.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.domain.model.MUSHROOM_MARKER_SIZE_SCALE_DEFAULT
import compose.project.leshy.domain.model.MUSHROOM_MARKER_SIZE_SCALE_MAX
import compose.project.leshy.domain.model.MUSHROOM_MARKER_SIZE_SCALE_MIN
import compose.project.leshy.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val LANGUAGE_KEY = stringPreferencesKey("language")
private val MUSHROOM_MARKER_SIZE_SCALE_KEY = floatPreferencesKey("mushroom_marker_size_scale")
private val RESET_MUSHROOM_ORDER_ON_WALK_FINISH_KEY = booleanPreferencesKey("reset_mushroom_order_on_walk_finish")
private val FREEZE_MUSHROOM_ORDER_KEY = booleanPreferencesKey("freeze_mushroom_order")

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {
    override fun observeLanguage(): Flow<AppLanguage> = dataStore.data.map { prefs ->
        prefs[LANGUAGE_KEY]?.let { code -> AppLanguage.entries.find { it.code == code } } ?: AppLanguage.EN
    }

    override suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { prefs -> prefs[LANGUAGE_KEY] = language.code }
    }

    override fun observeMushroomMarkerSizeScale(): Flow<Float> = dataStore.data.map { prefs ->
        (prefs[MUSHROOM_MARKER_SIZE_SCALE_KEY] ?: MUSHROOM_MARKER_SIZE_SCALE_DEFAULT)
            .coerceIn(MUSHROOM_MARKER_SIZE_SCALE_MIN, MUSHROOM_MARKER_SIZE_SCALE_MAX)
    }

    override suspend fun setMushroomMarkerSizeScale(scale: Float) {
        dataStore.edit { prefs ->
            prefs[MUSHROOM_MARKER_SIZE_SCALE_KEY] = scale.coerceIn(
                MUSHROOM_MARKER_SIZE_SCALE_MIN,
                MUSHROOM_MARKER_SIZE_SCALE_MAX,
            )
        }
    }

    override fun observeResetMushroomOrderOnWalkFinish(): Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[RESET_MUSHROOM_ORDER_ON_WALK_FINISH_KEY] ?: false
    }

    override suspend fun setResetMushroomOrderOnWalkFinish(reset: Boolean) {
        dataStore.edit { prefs -> prefs[RESET_MUSHROOM_ORDER_ON_WALK_FINISH_KEY] = reset }
    }

    override fun observeFreezeMushroomOrder(): Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[FREEZE_MUSHROOM_ORDER_KEY] ?: false
    }

    override suspend fun setFreezeMushroomOrder(freeze: Boolean) {
        dataStore.edit { prefs -> prefs[FREEZE_MUSHROOM_ORDER_KEY] = freeze }
    }
}
