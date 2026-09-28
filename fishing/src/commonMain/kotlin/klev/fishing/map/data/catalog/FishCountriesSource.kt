package klev.fishing.map.data.catalog

import klev.fishing.generated.resources.Res
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val FISH_COUNTRIES_PATH = "files/catalog/fish_countries.json"

private val FishCountriesJson = Json { ignoreUnknownKeys = true }

/**
 * Одна подборка: страна и ключи видов, которые в ней реально ловят.
 *
 * **Это не академический чеклист присутствия.** Вид, занесённый в местную Красную книгу или
 * встречающийся единично, в подборку не идёт: подборка отвечает на вопрос «что я тут поймаю», а не
 * «что тут когда-либо находили». Откуда взяты списки и какие позиции спорные —
 * `docs/research/fish-countries/README.md`; сам файл собирается скриптом
 * `tools/build_fish_countries.py`, руками его не правят.
 */
@Serializable
data class FishCountryEntry(val code: String, val keys: List<String>)

/** Разбирает `fish_countries.json` один раз и кеширует — как [FishCatalogSource]. */
class FishCountriesSource {
    private val parsed: List<FishCountryEntry> by lazy {
        val bytes = runBlocking { Res.readBytes(FISH_COUNTRIES_PATH) }
        FishCountriesJson.decodeFromString(bytes.decodeToString())
    }

    val entries: List<FishCountryEntry> get() = parsed

    fun keysFor(code: String): List<String>? = parsed.firstOrNull { it.code == code }?.keys
}
