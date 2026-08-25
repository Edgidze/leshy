package leshy.mushrooms.map.data.local

import androidx.room.TypeConverter
import leshy.mushrooms.map.data.local.entity.ObjectType
import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.domain.model.CategorySource
import kotlinx.serialization.json.Json

private val customNamesJson = Json

class Converters {
    @TypeConverter
    fun fromObjectType(value: ObjectType): String = value.name

    @TypeConverter
    fun toObjectType(value: String): ObjectType = ObjectType.valueOf(value)

    @TypeConverter
    fun fromCategorySource(value: CategorySource): String = value.name

    @TypeConverter
    fun toCategorySource(value: String): CategorySource = CategorySource.valueOf(value)

    /** Keyed by [AppLanguage.code] rather than the enum's own name so the stored JSON stays
     * readable and stable if the enum is ever renamed — and so it matches the shape the export
     * archive will use (`.claude/plans/user-mushrooms.md`, Phase 6). */
    @TypeConverter
    fun fromCustomNames(value: Map<AppLanguage, String>): String =
        customNamesJson.encodeToString(value.mapKeys { (language, _) -> language.code })

    /** Unknown/removed language codes are dropped rather than failing the read — a name for a
     * language this build no longer has is not worth crashing a whole query over. */
    @TypeConverter
    fun toCustomNames(value: String): Map<AppLanguage, String> {
        val byCode = runCatching {
            customNamesJson.decodeFromString<Map<String, String>>(value)
        }.getOrElse { return emptyMap() }
        return byCode.mapNotNull { (code, name) ->
            AppLanguage.entries.firstOrNull { it.code == code }?.let { it to name }
        }.toMap()
    }
}
