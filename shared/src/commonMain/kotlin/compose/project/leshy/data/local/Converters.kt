package compose.project.leshy.data.local

import androidx.room.TypeConverter
import compose.project.leshy.data.local.entity.ObjectType

class Converters {
    @TypeConverter
    fun fromObjectType(value: ObjectType): String = value.name

    @TypeConverter
    fun toObjectType(value: String): ObjectType = ObjectType.valueOf(value)
}
