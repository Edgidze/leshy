package leshy.mushrooms.map.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Адреса — контракт с сервером, а не деталь реализации: у российской редакции он зафиксирован в
 * `docs/russia-edition/README.md` до сборки и после публикации не меняется, у мировой уже
 * опубликован. Поэтому они проверяются буквально, строкой: опечатка здесь означает приложение,
 * которое ходит не туда, и заметить её можно только на устройстве.
 */
class EditionEndpointsTest {
    @Test
    fun worldKeepsTheAddressesItShippedWith() {
        val world = editionEndpointsFor(Edition.WORLD)
        assertEquals("https://tiles.openfreemap.org/styles/liberty", world.mapStyleUrl)
        assertEquals("tiles.openfreemap.org", world.mapHost)
        assertEquals("https://leshy-mapper.github.io/mushrooms-map/privacy.html", world.privacyPolicyUrl)
    }

    @Test
    fun russiaMatchesTheServerContract() {
        val russia = editionEndpointsFor(Edition.RUSSIA)
        assertEquals("https://tiles.gribnye-progulki.ru/styles/liberty", russia.mapStyleUrl)
        assertEquals("tiles.gribnye-progulki.ru", russia.mapHost)
        assertEquals("https://gribnye-progulki.ru/privacy", russia.privacyPolicyUrl)
    }

    /** Хост выводится из адреса стиля — разойтись они не должны ни при какой правке. */
    @Test
    fun hostIsAlwaysTheHostOfTheStyleUrl() {
        Edition.entries.forEach { edition ->
            val endpoints = editionEndpointsFor(edition)
            assertTrue(
                endpoints.mapStyleUrl.startsWith("https://${endpoints.mapHost}/"),
                "$edition: ${endpoints.mapStyleUrl} не начинается с хоста ${endpoints.mapHost}",
            )
        }
    }

    /** Ни одна редакция не должна нечаянно унаследовать чужой хост. */
    @Test
    fun editionsDoNotShareAHost() {
        val hosts = Edition.entries.map { editionEndpointsFor(it).mapHost }
        assertEquals(hosts.size, hosts.toSet().size, "у редакций совпал тайл-хост: $hosts")
    }
}
