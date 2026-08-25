package leshy.mushrooms.map.data.export.dto

import kotlinx.serialization.json.Json

/** Shared (de)serializer for every entry in the export archive. `ignoreUnknownKeys` so an older
 * app version can still import an archive written by a newer one that added optional fields. */
val ExportJson = Json {
    ignoreUnknownKeys = true
}
