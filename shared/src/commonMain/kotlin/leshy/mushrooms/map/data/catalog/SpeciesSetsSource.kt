package leshy.mushrooms.map.data.catalog

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.domain.model.Edition
import leshy.shared.generated.resources.Res

private val SetsJson = Json { ignoreUnknownKeys = true }

/** Один набор: идентификатор, имя на языках редакции и ключи видов. */
@Serializable
data class SpeciesSetEntry(
    val id: String,
    val name: Map<String, String>,
    val keys: List<String>,
)

@Serializable
private data class SpeciesSetsFile(
    /** Страна, чью подборку эти наборы заменяют, — кодом ISO. */
    val country: String,
    /** Что отмечено на свежей установке. */
    val defaults: List<String>,
    /** Набор, принимающий вид, отмеченный поиском и не входящий больше никуда, — см.
     * [SpeciesSetsSource.pickedFallbackSetId]. */
    val picked_fallback: String? = null,
    val sets: List<SpeciesSetEntry>,
)

/** Префикс, общий у всех [leshy.mushrooms.map.domain.model.Collection.nameKey] наборов редакции. */
private const val SPECIES_SET_PREFIX = "collection_set_"

fun speciesSetCollectionNameKey(setId: String): String = SPECIES_SET_PREFIX + setId

/** `null`, если [nameKey] — не набор редакции (страновая подборка, своя подборка пользователя). */
fun speciesSetIdForCollectionNameKey(nameKey: String): String? =
    nameKey.takeIf { it.startsWith(SPECIES_SET_PREFIX) }?.removePrefix(SPECIES_SET_PREFIX)

/**
 * Наборы видов редакции — то, чем у редакции, сделанной под конкретную страну, заменяется одна
 * подборка этой страны.
 *
 * ## Зачем наборы вообще
 *
 * У российской редакции 171 вид с русским названием, и одной подборкой это лента, которую
 * приходится листать (`docs/russia-edition/brief-mushroom-sets.md`). Вместо неё — базовый набор,
 * отмеченный по умолчанию, плюс дополнения под тип леса, куда человек идёт.
 *
 * Резать по регионам России **не вышло, и это проверено данными**, а не мнением: почти все 171 вид
 * задокументированы по всей лесной зоне, и в «базовый» по правилу «четыре региона из шести» ушло
 * 138 (`report-mushroom-regions.md`). От региональной оси остался один набор — «Юг и Кавказ».
 *
 * ## Почему это данные, а не код
 *
 * Состав наборов — правило, применённое к разметке исследования; и то и другое меняется. Правило
 * живёт в `tools/build_russia_sets.py`, его результат — в `files/catalog/sets-ru.json`, а здесь
 * только чтение. Экраны о наборах не знают вовсе: для них это обычные подборки, как страновые, и
 * ни одного ветвления по редакции в интерфейсе не появляется.
 *
 * ## Мировая редакция
 *
 * [Edition.speciesSetsPath] у неё `null` — файл не читается, [sets] пуст, и всё, что ниже по
 * коду, ведёт себя ровно как до появления этого класса: подборки собираются по странам.
 */
class SpeciesSetsSource(private val edition: Edition) {
    private class Parsed(val file: SpeciesSetsFile?, val version: Int)

    private val parsed: Parsed by lazy {
        val path = edition.speciesSetsPath ?: return@lazy Parsed(null, 0)
        val bytes = runBlocking { Res.readBytes(path) }
        Parsed(SetsJson.decodeFromString(bytes.decodeToString()), bytes.contentHashCode())
    }

    /** Наборы редакции; пустой список означает «у этой редакции наборов нет». */
    val sets: List<SpeciesSetEntry> get() = parsed.file?.sets.orEmpty()

    /** Страна, чью подборку наборы заменяют, — её страновая подборка при этом не заводится. */
    val countryCode: String? get() = parsed.file?.country

    /** Идентификаторы наборов, отмеченных на свежей установке. */
    val defaultSetIds: List<String> get() = parsed.file?.defaults.orEmpty()

    /**
     * Набор, в который попадает каталожный вид, отмеченный поиском и не входящий ни в одну
     * подборку, — `null` у редакции без наборов.
     *
     * Зачем: у редакции со своими наборами каталог покрыт ими не целиком (168 ключей из 409), и
     * до остальных ведёт только поиск по «Моим грибам». Отмеченный там вид появлялся в ленте
     * «Записи», но ни в одной подборке того же экрана не показывался — «в ленте есть, в подборках
     * нет». Теперь он виден там, где его и ищут глазами, и снять галочку можно тем же способом,
     * что у любого другого вида.
     *
     * Живёт в данных (`sets-ru.json`), а не константой в коде: какой набор играет роль «прочего»,
     * решает разметка наборов, а не приложение.
     */
    val pickedFallbackSetId: String? get() = parsed.file?.picked_fallback

    /** Отпечаток файла — тем же гейтом, что и у `countries.json`, сторожит пересев подборок. */
    val version: Int get() = parsed.version

    /**
     * Имя набора на [language], с откатом на английский и, если нет и его, на идентификатор.
     *
     * Имена лежат в самом файле, а не в `StringKey`, по той же причине, по какой там лежат имена
     * стран: это **контент**, а не строки интерфейса, и exhaustive `when` по конечному enum'у для
     * него не существует (`i18n/CLAUDE.md`). Языков у редакции с наборами два, так что и перевода
     * тут ровно два.
     */
    fun nameFor(setId: String, language: AppLanguage): String? {
        val entry = sets.firstOrNull { it.id == setId } ?: return null
        return entry.name[language.code] ?: entry.name[AppLanguage.EN.code] ?: setId
    }
}
