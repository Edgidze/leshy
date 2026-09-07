package leshy.mushrooms.map.data.export.dto

import kotlinx.serialization.Serializable

const val COLLECTIONS_ENTRY_NAME = "collections/collections.json"

/**
 * Пользовательские подборки, в которых лежат уехавшие в архив грибы — v3 формата
 * (`.claude/plans/user-collections.md`). Страновые подборки сюда не попадают никогда: они
 * пересеваются из `countries.json` на каждом запуске, ровно как каталожные виды не попадают в
 * `categories/categories.json`.
 *
 * [nameKey] — тот же контракт, что у `CategoryExportDto.nameKey`: единственный идентификатор, по
 * которому импорту разрешено сливать подборки напрямую. У служебной «Другие» он фиксированный
 * (`collection_user_other`), поэтому «Другие» с любых устройств сходятся в одну подборку сами.
 * [name] у неё `null` — имя приходит из языка интерфейса, а не из архива.
 *
 * [memberNameKeys] ссылаются на `CategoryExportDto.nameKey` из того же архива и, как и он, включают
 * только виды, реально встреченные на выгруженных прогулках: подборка приезжает такой, какая
 * помещается в этот архив, а не такой, какая была на исходном телефоне целиком.
 */
@Serializable
data class CollectionExportDto(
    val nameKey: String,
    val name: String?,
    val memberNameKeys: List<String>,
)
