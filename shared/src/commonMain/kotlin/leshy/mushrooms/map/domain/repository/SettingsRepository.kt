package leshy.mushrooms.map.domain.repository

import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeLanguage(): Flow<AppLanguage>
    suspend fun setLanguage(language: AppLanguage)

    /** Оформление: явно светлое/тёмное, либо следовать системной настройке устройства. */
    fun observeThemeMode(): Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)

    /** Multiplier applied to the base mushroom marker icon size shown on maps. */
    fun observeMushroomMarkerSizeScale(): Flow<Float>
    suspend fun setMushroomMarkerSizeScale(scale: Float)

    /**
     * Whether the Record screen's tile feed should drop its "most recently tapped first" order
     * and fall back to the catalog's alphabetical order once a walk finishes. Off by default — the
     * feed order built up during a walk carries over into the next one.
     */
    fun observeResetMushroomOrderOnWalkFinish(): Flow<Boolean>
    suspend fun setResetMushroomOrderOnWalkFinish(reset: Boolean)

    /**
     * Whether the Record screen's tile feed should stop bumping a tile to the front when it's
     * tapped (+/-) — the feed stays in its alphabetical order regardless of new finds. Off by
     * default. Doesn't affect the deliberate jump-to-tile from the search dialog or right after
     * creating a new species — those aren't "reordering because of a new find", they're a jump to
     * a tile the user just explicitly picked.
     */
    fun observeFreezeMushroomOrder(): Flow<Boolean>
    suspend fun setFreezeMushroomOrder(freeze: Boolean)

    /**
     * Сам накопленный порядок ленты плиток «Записи» — id видов, самый свежий первым.
     *
     * Единственное здесь, что не является выбором пользователя, и живёт тут всё равно: это
     * состояние, которым управляют две соседние настройки выше
     * ([observeResetMushroomOrderOnWalkFinish] и [observeFreezeMushroomOrder]), и разносить
     * данные и правила их применения по разным хранилищам не за что.
     *
     * До появления этих методов порядок жил только в памяти `RecordViewModel` — то есть
     * обещание «порядок переносится в следующую прогулку» (см. KDoc
     * [observeResetMushroomOrderOnWalkFinish]) держалось лишь до закрытия приложения, и
     * наутро лента возвращалась к алфавиту при выключенной галочке сброса. Репорт с
     * устройства, 2026-09-17.
     */
    fun observeMushroomTileOrder(): Flow<List<Long>>
    suspend fun setMushroomTileOrder(order: List<Long>)
}
