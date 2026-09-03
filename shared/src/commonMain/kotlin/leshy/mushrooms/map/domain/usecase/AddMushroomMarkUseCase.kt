package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.domain.model.FieldMark
import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.domain.model.MarkType
import leshy.mushrooms.map.domain.repository.FieldMarkRepository
import leshy.mushrooms.map.domain.repository.WalkRepository

/**
 * **[location] не nullable, и это главное в этом классе.** Раньше было `GeoPoint?` с подстановкой
 * `?: 0.0`, то есть «координат нет» и «координаты есть» приводились к одному и тому же — и находка
 * без фикса записывалась в точку (0, 0), в Гвинейский залив. Такая запись хуже потерянной: она
 * выглядит как настоящая, ложится на карту находок, попадает в снимок маршрута и в экспорт, а
 * отличить её от честной постфактум нельзя ничем, кроме подозрительно круглых координат.
 *
 * Проверка «есть ли фикс» поэтому вынесена туда, где на неё ещё можно как-то ответить
 * пользователю (`RecordViewModel` и экран «Записи»), а сюда попадают только координаты, которые
 * действительно откуда-то взялись. Тип и делает это правилом, а не договорённостью.
 */
class AddMushroomMarkUseCase(
    private val fieldMarkRepository: FieldMarkRepository,
    private val walkRepository: WalkRepository,
) {
    suspend operator fun invoke(walkId: Long, categoryId: Long, location: GeoPoint, timestamp: Long): FieldMark {
        val mark = FieldMark(
            id = 0,
            walkId = walkId,
            categoryId = categoryId,
            lat = location.lat,
            lon = location.lon,
            timestamp = timestamp,
            type = MarkType.MUSHROOM,
            photoPath = null,
        )
        val id = fieldMarkRepository.addMark(mark)
        val walk = walkRepository.getById(walkId)
        if (walk != null) {
            walkRepository.update(walk.copy(mushroomCount = walk.mushroomCount + 1))
        }
        return mark.copy(id = id)
    }
}
