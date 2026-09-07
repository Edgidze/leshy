package leshy.mushrooms.map.presentation

import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.domain.model.CategoryCollectionMembership
import leshy.mushrooms.map.domain.model.CategorySource
import leshy.mushrooms.map.domain.model.Collection
import leshy.mushrooms.map.domain.model.CollectionSource
import leshy.mushrooms.map.domain.usecase.OTHER_COLLECTION_NAME_KEY

/** Одна пользовательская подборка со своими грибами — блок «Добавленных грибов» на экране «Мои
 * грибы» (`.claude/plans/user-collections.md`). */
data class UserSpeciesGroup(val collection: Collection, val species: List<Category>)

/** Синтетические «Другие» на случай, когда гриб не состоит ни в одной пользовательской подборке, а
 * строки самой подборки в базе ещё нет. Такого быть не должно (миграция раскладывает старые грибы,
 * `AssignSpeciesToCollectionUseCase` — новые), но «гриб пропал с экрана» — слишком дорогая цена за
 * невозможную ситуацию, поэтому здесь именно запасной ящик, а не `require`. `id = 0` наружу не
 * уходит: строка создастся при первом же сохранении в неё. */
private val FALLBACK_OTHER = Collection(
    id = 0,
    nameKey = OTHER_COLLECTION_NAME_KEY,
    order = Int.MAX_VALUE,
    source = CollectionSource.USER,
    name = null,
)

/**
 * Раскладывает пользовательские и импортированные виды по их пользовательским подборкам.
 *
 * Вид состоит ровно в одной такой подборке (это держит [AssignSpeciesToCollectionUseCase]
 * [leshy.mushrooms.map.domain.usecase.AssignSpeciesToCollectionUseCase]), но связь в базе —
 * many-to-many, поэтому здесь берётся первая подходящая, а не `single()`: перекос в данных обязан
 * показать гриб один раз, а не уронить экран.
 *
 * Пустых подборок в списке не бывает — они удаляются вместе с последним ушедшим грибом, и
 * подстраховка тут та же по смыслу, что [FALLBACK_OTHER] с другой стороны.
 */
fun buildUserSpeciesGroups(
    collections: List<Collection>,
    memberships: List<CategoryCollectionMembership>,
    userSpecies: List<Category>,
): List<UserSpeciesGroup> {
    val userCollectionsById = collections.filter { it.source != CollectionSource.COUNTRY }.associateBy { it.id }
    val collectionIdsByCategory = memberships.groupBy({ it.categoryId }, { it.collectionId })

    val other = userCollectionsById.values.firstOrNull { it.nameKey == OTHER_COLLECTION_NAME_KEY } ?: FALLBACK_OTHER
    val speciesByCollection = userSpecies.groupBy { species ->
        collectionIdsByCategory[species.id].orEmpty()
            .firstNotNullOfOrNull { userCollectionsById[it] }
            ?: other
    }

    return speciesByCollection.entries
        .sortedWith(compareBy({ it.key.order }, { it.key.name?.lowercase().orEmpty() }))
        .map { (collection, species) ->
            UserSpeciesGroup(
                collection = collection,
                species = species.sortedWith(compareBy({ it.source != CategorySource.USER }, { it.order })),
            )
        }
}
