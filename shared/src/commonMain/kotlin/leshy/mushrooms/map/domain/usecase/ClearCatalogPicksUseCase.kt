package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.domain.model.CategorySource
import leshy.mushrooms.map.domain.repository.CategoryRepository

/**
 * Снимает отметку со ВСЕХ каталожных видов разом — кнопка «снять все отметки» в пикере подборок
 * («Мои грибы» и онбординг, обе редакции).
 *
 * Зачем отдельная кнопка, когда есть галочка у каждой подборки: подборки пересекаются, и снятие
 * их одну за другой не приводит ленту в пустое состояние — [SetCollectionPickedUseCase]
 * намеренно оставляет виды, которые держит другая полностью отмеченная подборка. Плюс вид,
 * отмеченный поиском поштучно, не принадлежит ни одной снимаемой подборке вовсе. Человеку,
 * который хочет начать выбор заново, до этой правки приходилось перещёлкивать каталог руками.
 *
 * **Свои виды пользователя не трогаются** ([CategorySource.APP] — только каталог). Их он завёл
 * сам, они лежат в отдельном блоке экрана со своими галочками, и «сброс подборок», уносящий
 * собственноручно добавленный вид, был бы сбросом не того, что просили.
 *
 * Пишет `isPicked` и `isActive` вместе — по той же причине, что и [SetCategoryPickedUseCase]:
 * каскад пересчёта сам по себе `isActive` не гасит у вида, у которого есть находки. Находок и
 * прогулок не касается вовсе: снятый вид исчезает из ленты «Записи» и из предложения фильтра, а
 * всё уже записанное остаётся на месте.
 */
class ClearCatalogPicksUseCase(
    private val categoryRepository: CategoryRepository,
    private val recalculateFilterEligibility: RecalculateFilterEligibilityUseCase,
) {
    suspend operator fun invoke() {
        val cleared = categoryRepository.getAll()
            .filter { it.source == CategorySource.APP && (it.isPicked || it.isActive) }
            .map { it.copy(isPicked = false, isActive = false) }
        if (cleared.isEmpty()) return
        categoryRepository.upsertAll(cleared)
        recalculateFilterEligibility()
    }
}
