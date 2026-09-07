package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.data.platform.currentTimeMillis
import leshy.mushrooms.map.domain.model.Collection
import leshy.mushrooms.map.domain.model.CollectionSource
import leshy.mushrooms.map.domain.repository.CollectionRepository
import kotlin.random.Random

/** Служебная подборка «Другие»: гриб, для которого пользователь не назвал подборку, и все грибы,
 * добавленные до того, как подборки вообще появились (бэкфилл в `MIGRATION_12_13`). Ключ
 * фиксированный, а не сгенерированный, ровно ради двух вещей: имя переводится вместе с интерфейсом
 * (в отличие от именованных подборок, где хранится литеральный пользовательский ввод), и при
 * импорте «Другие» с любого устройства сходятся в одну подборку сами собой. */
const val OTHER_COLLECTION_NAME_KEY = "collection_user_other"

/** Полоса `order` у пользовательских подборок — выше любой страновой (те занимают 0..N по индексу
 * в `countries.json`), чтобы пересев стран никогда не пересекался с ними по значению. «Другие»
 * стоят ещё выше и потому всегда идут последними в списке. */
internal const val USER_COLLECTION_ORDER = 1000
internal const val OTHER_COLLECTION_ORDER = 2000

/** Тот же контракт, что у `generateUserNameKey` для видов: `nameKey` — идентификатор для мержа при
 * импорте, а не текст. Отображаемое имя живёт в [Collection.name]. */
private fun generateUserCollectionNameKey(): String =
    "usercoll_${currentTimeMillis()}_${Random.nextInt(100_000, 999_999)}"

/**
 * Кладёт вид в пользовательскую подборку по её ИМЕНИ — так, как его набрали в диалоге после
 * сохранения гриба (`.claude/plans/user-collections.md`). Пустое имя означает «Другие».
 *
 * Подборка с таким именем ищется среди уже существующих без учёта регистра и краевых пробелов:
 * человек набирает название руками каждый раз, и «Мои белые» с «мои Белые» обязаны оказаться одной
 * подборкой, иначе список расползается на двойники (подсказки под полем ввода снижают вероятность
 * этого, но не убирают её).
 *
 * Вид всегда состоит ровно в одной пользовательской подборке: прежнее членство снимается. Строки
 * членства в СТРАНОВЫХ подборках при этом не трогаются — пользовательский вид в них не состоит
 * никогда, но каскад тут не наш, и снести чужое членство «на всякий случай» дороже, чем не трогать.
 * Опустевшая после переноса подборка удаляется — пустых пользовательских подборок в приложении не
 * существует.
 */
class AssignSpeciesToCollectionUseCase(
    private val collectionRepository: CollectionRepository,
) {
    suspend operator fun invoke(categoryId: Long, collectionName: String?) {
        val target = resolveTarget(collectionName?.trim().orEmpty())

        val previous = collectionRepository.getMemberCollectionIds(categoryId)
            .filter { it != target.id }
            .mapNotNull { collectionRepository.getById(it) }
            .filter { it.source != CollectionSource.COUNTRY }
        for (collection in previous) {
            collectionRepository.removeMember(categoryId, collection.id)
        }

        collectionRepository.addMember(categoryId, target.id)

        // После addMember, а не до: если целевая подборка и есть единственная прежняя, она в этот
        // момент уже непустая и удалять её нечего.
        for (collection in previous) deleteIfEmpty(collection)
    }

    private suspend fun resolveTarget(name: String): Collection {
        if (name.isEmpty()) {
            return collectionRepository.getByNameKey(OTHER_COLLECTION_NAME_KEY) ?: create(
                nameKey = OTHER_COLLECTION_NAME_KEY,
                order = OTHER_COLLECTION_ORDER,
                name = null,
            )
        }
        val existing = collectionRepository.getAll().firstOrNull {
            it.source != CollectionSource.COUNTRY && it.name.equals(name, ignoreCase = true)
        }
        return existing ?: create(
            nameKey = generateUserCollectionNameKey(),
            order = USER_COLLECTION_ORDER,
            name = name,
        )
    }

    private suspend fun create(nameKey: String, order: Int, name: String?): Collection {
        val collection = Collection(
            id = 0,
            nameKey = nameKey,
            order = order,
            source = CollectionSource.USER,
            name = name,
        )
        return collection.copy(id = collectionRepository.upsert(collection))
    }

    private suspend fun deleteIfEmpty(collection: Collection) {
        if (collectionRepository.countMembers(collection.id) == 0) collectionRepository.delete(collection)
    }
}
