package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.data.catalog.SpeciesSetsSource
import leshy.mushrooms.map.domain.model.CollectionSource
import leshy.mushrooms.map.domain.repository.CatalogStateRepository
import leshy.mushrooms.map.domain.repository.CategoryRepository
import leshy.mushrooms.map.domain.repository.CollectionRepository
import leshy.mushrooms.map.domain.repository.FieldMarkRepository
import kotlinx.coroutines.flow.first

/**
 * Разовая уборка видов, объявленных ошибкой, — дублей, снятых из наборов редакции
 * ([SpeciesSetsSource.droppedKeys]).
 *
 * **Зачем вообще.** Досев данных аддитивен: новые строки и членства появляются, старые не
 * удаляются никогда, а `isPicked` человека не трогается вовсе. Поэтому вид, убранный нами из
 * набора, у уже установленного приложения остаётся и в наборе, и отмеченным — то есть вторая
 * неразличимая плитка в ленте никуда не девается, и починка достаётся одним новым установкам.
 * Владелец наблюдал это на трёх снятых дублях подряд (`docs/russia-edition/session-2026-09-26.md`).
 *
 * **Почему по явному списку, а не сверкой с данными.** Решение владельца из трёх вариантов:
 * обновление убирает ТОЛЬКО то, что мы сами объявили ошибкой. Сверка «чего нет в данных — убрать»
 * била бы куда шире: вид, выпавший из подборки страны по уточнению ареала, исчез бы и у того, кто
 * его сам отмечал. Второе решение того же разговора — новый вид каталога галочку НЕ получает,
 * поэтому обратной половины (дотикивания) здесь нет и не предполагается.
 *
 * **Кого не трогает.** Двух людей, и обоих намеренно:
 * - у кого есть находки этого вида — вид в работе, и убирать его из ленты значит спорить с
 *   собственной историей человека; дубль у него останется, и это меньшее из двух зол;
 * - кто сам положил вид в СВОЮ подборку — он его выбрал явно, явнее некуда.
 *
 * **Почему нельзя просто повторять уборку каждый запуск.** Снятый вид остаётся в каталоге и
 * находится поиском, то есть человек вправе отметить его снова. Отметка «по этому ключу мы уже
 * прошли» ([CatalogStateRepository.getRetiredSpeciesKeys]) — единственное, что не даёт приложению
 * снимать галочку второй раз; ставится она и для пропущенных ключей тоже, иначе пропуск
 * пересчитывался бы вечно.
 *
 * Зовётся один раз на старте приложения, рядом с [RepairPhotoPathsUseCase] (`App.kt`) — это
 * такая же разовая починка состояния, а не часть посева. На свежей установке — no-op: снятых
 * видов там нет ни в наборах, ни в отмеченных.
 */
class RetireDroppedSpeciesUseCase(
    private val categoryRepository: CategoryRepository,
    private val collectionRepository: CollectionRepository,
    private val fieldMarkRepository: FieldMarkRepository,
    private val speciesSetsSource: SpeciesSetsSource,
    private val catalogStateRepository: CatalogStateRepository,
) {
    suspend operator fun invoke() {
        val dropped = speciesSetsSource.droppedKeys.toSet()
        if (dropped.isEmpty()) return
        val pending = dropped - catalogStateRepository.getRetiredSpeciesKeys()
        if (pending.isEmpty()) return

        val markedCategoryIds = fieldMarkRepository.observeAll().first().mapTo(mutableSetOf()) { it.categoryId }
        val collectionSourceById = collectionRepository.getAll().associate { it.id to it.source }

        for (key in pending) {
            val category = categoryRepository.getByNameKey(key) ?: continue
            if (category.id in markedCategoryIds) continue
            val memberOf = collectionRepository.getMemberCollectionIds(category.id)
            if (memberOf.any { collectionSourceById[it] == CollectionSource.USER }) continue

            memberOf.forEach { collectionRepository.removeMember(category.id, it) }
            if (category.isPicked || category.isActive || category.isFilterEligible) {
                categoryRepository.upsert(
                    category.copy(isPicked = false, isActive = false, isFilterEligible = false),
                )
            }
        }
        // Отмечаются ВСЕ обработанные ключи, включая пропущенные по двум правилам выше: пропуск —
        // это тоже решение, и принимать его заново на каждом запуске незачем.
        catalogStateRepository.addRetiredSpeciesKeys(pending)
    }
}
