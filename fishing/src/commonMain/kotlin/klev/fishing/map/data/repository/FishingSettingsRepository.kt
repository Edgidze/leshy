package klev.fishing.map.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import klev.fishing.map.domain.model.FishingMethod
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
 *
 * Второе своё — СПОСОБЫ ЛОВЛИ, которыми пользуется этот человек. Приложение не выбирает за него
 * «главную» рыбалку (решение владельца 2026-09-28): кто ловит только с берега, тот не должен видеть
 * лодку и лёд при каждом старте, а кто ездит и так и так — видит оба.
 */
class FishingSettingsRepository(private val dataStore: DataStore<Preferences>) {
    private val pressureUnitKey = stringPreferencesKey("fishing_pressure_unit")
    private val methodsKey = stringPreferencesKey("fishing_methods")
    private val countryKey = stringPreferencesKey("fishing_country")

    fun observePressureUnit(): Flow<PressureUnit> = dataStore.data.map { prefs ->
        prefs[pressureUnitKey]?.let { stored -> PressureUnit.entries.firstOrNull { it.name == stored } }
            ?: PressureUnit.HPA
    }

    suspend fun setPressureUnit(unit: PressureUnit) {
        dataStore.edit { it[pressureUnitKey] = unit.name }
    }

    /**
     * Способы, которыми ловит этот человек. Пусто в хранилище — значит выбора ещё не делали, и
     * доступны все: молчание настройки не должно выглядеть как «не ловлю ничем».
     *
     * Неизвестные имена отбрасываются молча (переименование enum), а если после разбора не осталось
     * ни одного — снова все. **Пустое множество не отдаётся никогда**: иначе экран старта остался бы
     * без единой кнопки, и рыбалку стало бы нельзя начать вообще.
     */
    fun observeMethods(): Flow<Set<FishingMethod>> = dataStore.data.map { prefs ->
        val stored = prefs[methodsKey]
            ?.split(',')
            ?.mapNotNull { name -> FishingMethod.entries.firstOrNull { it.name == name } }
            ?.toSet()
            .orEmpty()
        stored.ifEmpty { FishingMethod.entries.toSet() }
    }

    /**
     * Код страны последней применённой подборки видов. Хранится ради одной вещи — показать в
     * разделе «Виды рыб», по какой подборке собрана лента: без этого человек, открывший приложение
     * через месяц, видит короткий список и не помнит, сам он его правил или это подборка.
     *
     * `null` — подборку не применяли ни разу (или после неё лента правилась руками, см.
     * `clearCountry`).
     */
    fun observeCountry(): Flow<String?> = dataStore.data.map { prefs -> prefs[countryKey] }

    suspend fun setCountry(code: String) {
        dataStore.edit { it[countryKey] = code }
    }

    /** Ленту правили руками — подборка больше не описывает её состав, и врать об этом незачем. */
    suspend fun clearCountry() {
        dataStore.edit { it.remove(countryKey) }
    }

    suspend fun setMethods(methods: Set<FishingMethod>) {
        dataStore.edit { prefs ->
            // Снятие последней галочки трактуется как «пусть будут все», а не как пустой список —
            // см. `observeMethods`.
            prefs[methodsKey] = methods.joinToString(",") { it.name }
        }
    }
}
