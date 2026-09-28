package klev.fishing.map.domain.usecase

import klev.fishing.map.data.catalog.FishCountriesSource
import klev.fishing.map.data.repository.FishingSettingsRepository
import klev.fishing.map.domain.repository.SpeciesRepository

/**
 * Применить подборку страны: виды из неё остаются в ленте «Рыбалки», остальные уходят.
 *
 * **Ничего не удаляет и не добавляет в каталог** — только переключает `isActive`. Это важно: улов,
 * записанный на вид, который потом ушёл из ленты, никуда не девается и виден в архиве, а вернуть
 * вид — одно касание в разделе «Виды рыб». Подборка здесь — фильтр ленты, а не состав каталога.
 *
 * Свои виды пользователя ([klev.fishing.map.domain.model.SpeciesSource.USER], когда они появятся)
 * подборка не трогает: их добавили руками, и решать за человека, что его собственный вид в этой
 * стране не водится, приложению не по чину.
 */
class ApplyCountryCollectionUseCase(
    private val species: SpeciesRepository,
    private val countries: FishCountriesSource,
    private val settings: FishingSettingsRepository,
) {
    /** @return `false`, если подборки для такой страны нет — вызывающий показывает это как есть. */
    suspend operator fun invoke(countryCode: String): Boolean {
        val keys = countries.keysFor(countryCode)?.toSet() ?: return false
        species.getAll().forEach { item ->
            if (item.source != klev.fishing.map.domain.model.SpeciesSource.APP) return@forEach
            val shouldBeActive = item.key in keys
            if (item.isActive != shouldBeActive) species.setActive(item.id, shouldBeActive)
        }
        settings.setCountry(countryCode)
        return true
    }
}
