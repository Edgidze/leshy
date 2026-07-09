package compose.project.leshy.data.local

import androidx.room.TypeConverter
import compose.project.leshy.data.local.entity.ObjectType
import compose.project.leshy.domain.model.EdibilityStatus

class Converters {
    @TypeConverter
    fun fromObjectType(value: ObjectType): String = value.name

    @TypeConverter
    fun toObjectType(value: String): ObjectType = ObjectType.valueOf(value)

    @TypeConverter
    fun fromEdibilityStatus(value: EdibilityStatus): String = value.name

    @TypeConverter
    fun toEdibilityStatus(value: String): EdibilityStatus = EdibilityStatus.valueOf(value)
}
