package klev.fishing.map.data.catalog

import klev.fishing.generated.resources.Res
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val FISH_CATALOG_PATH = "files/catalog/fish.json"

private val FishJson = Json { ignoreUnknownKeys = true }

/** Одна запись `composeResources/files/catalog/fish.json`. */
@Serializable
data class FishCatalogEntry(
    val key: String,
    val sci: String,
    val color: String,
    val order: Int,
    /** Имя файла иллюстрации. Пока не заполнено ни у одного вида — арт рисует владелец; плитка до
     *  тех пор показывает круг цвета [color]. Поле есть заранее, чтобы подключение иллюстраций было
     *  правкой данных, а не кода. */
    val image: String? = null,
)

/**
 * Разбирает каталог один раз и кеширует. Устроено как `CatalogSource` в `:shared`: `Res.readBytes`
 * существует только как `suspend`, поэтому первое обращение блокирует свой поток через
 * [runBlocking] — файл на 26 строк делает это незаметным, а всё последующее уже поиск в памяти.
 */
class FishCatalogSource {
    private val parsed: List<FishCatalogEntry> by lazy {
        val bytes = runBlocking { Res.readBytes(FISH_CATALOG_PATH) }
        FishJson.decodeFromString(bytes.decodeToString())
    }

    val entries: List<FishCatalogEntry> get() = parsed
}
