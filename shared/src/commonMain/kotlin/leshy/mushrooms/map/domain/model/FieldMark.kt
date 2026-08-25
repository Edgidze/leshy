package leshy.mushrooms.map.domain.model

/** Верхний предел количества находок одного вида за одну прогулку — не даёт
 * случайному опечатанному вводу цифрой превратить статистику прогулки в
 * бессмыслицу. */
const val MAX_MUSHROOM_FINDS_PER_WALK = 999

enum class MarkType {
    MUSHROOM,
    PHOTO,
    POI,
}

data class FieldMark(
    val id: Long,
    val walkId: Long,
    val categoryId: Long,
    val lat: Double,
    val lon: Double,
    val timestamp: Long,
    val type: MarkType,
    val photoPath: String?,
    val name: String? = null,
    val description: String? = null,
)
