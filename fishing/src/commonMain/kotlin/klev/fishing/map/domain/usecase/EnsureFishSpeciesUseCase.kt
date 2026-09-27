package klev.fishing.map.domain.usecase

import klev.fishing.map.data.catalog.FishCatalogSource
import klev.fishing.map.data.local.dao.SpeciesDao
import klev.fishing.map.data.local.entity.SpeciesEntity
import klev.fishing.map.domain.model.SpeciesSource

/**
 * Досеивает каталог в базу при запуске: новые виды добавляет, у существующих подтягивает латынь,
 * цвет и порядок.
 *
 * Чего НЕ делает — не трогает [SpeciesEntity.isActive]: это выбор пользователя («у меня хариус не
 * водится»), и обновление каталога не имеет права его отменять. По той же причине не удаляет
 * пропавшие из каталога виды: на них могут ссылаться записи улова.
 */
class EnsureFishSpeciesUseCase(
    private val dao: SpeciesDao,
    private val catalog: FishCatalogSource,
) {
    suspend operator fun invoke() {
        val existing = dao.getAll().associateBy { it.key }
        catalog.entries.forEach { entry ->
            val current = existing[entry.key]
            if (current == null) {
                dao.insert(
                    SpeciesEntity(
                        key = entry.key,
                        scientificName = entry.sci,
                        colorHex = entry.color,
                        iconRef = entry.image,
                        sortOrder = entry.order,
                        isActive = true,
                        source = SpeciesSource.APP,
                    )
                )
            } else {
                val updated = current.copy(
                    scientificName = entry.sci,
                    colorHex = entry.color,
                    iconRef = entry.image,
                    sortOrder = entry.order,
                )
                if (updated != current) dao.update(updated)
            }
        }
    }
}
