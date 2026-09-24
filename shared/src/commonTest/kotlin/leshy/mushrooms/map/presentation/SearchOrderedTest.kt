package leshy.mushrooms.map.presentation

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Ранжировка поиска на строках-метках напрямую, без Koin: `categorySearchLabel` резолвит
 * `MushroomAliases` через контейнер, и поднимать его ради проверки самой ранжировки незачем — она
 * от источника метки не зависит. Проверяется главное свойство, на котором держится добавление
 * синонимов (2026-09-17): метка склеена как «основное имя + синонимы», поэтому совпадение по
 * основному имени обязано обгонять совпадение по синониму без единой правки в [searchOrdered].
 */
class SearchOrderedTest {

    private val podosinovik = "Подосиновик Красный Красноголовик"
    private val krasnushka = "Краснушка"
    private val belyi = "Белый гриб"

    @Test
    fun aliasMatchIsFoundAtAll() {
        val ordered = searchOrdered(listOf(belyi, podosinovik), "красно") { it }
        assertEquals(podosinovik, ordered.first())
    }

    @Test
    fun realNameBeatsAliasOnTheSameQuery() {
        // «Краснушка» начинается с запроса, у подосиновика он совпал лишь с синонимом.
        val ordered = searchOrdered(listOf(podosinovik, krasnushka), "красн") { it }
        assertEquals(listOf(krasnushka, podosinovik), ordered)
    }

    @Test
    fun nonMatchingEntriesStayReachableAtTheEnd() {
        val ordered = searchOrdered(listOf(belyi, podosinovik), "красный") { it }
        assertEquals(listOf(podosinovik, belyi), ordered)
    }

    @Test
    fun emptyQueryKeepsOriginalOrder() {
        val items = listOf(belyi, podosinovik, krasnushka)
        assertEquals(items, searchOrdered(items, "   ") { it })
    }
}
