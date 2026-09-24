package leshy.mushrooms.map.presentation

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Правило порядка ленты по умолчанию (`sortCategories`, пункты 2–4 её KDoc) на голых ключах:
 * выемку значений из каталога тест не трогает — она ходит в Koin за `Res.readBytes`, который на
 * Android-хосте не выполняется. Проверяется ровно то, в чём легко ошибиться, — взаимное старшинство
 * трёх признаков.
 */
class DefaultOrderTest {

    private fun key(name: String, sortsLast: Boolean = false, score: Double = 0.0) =
        SpeciesOrderKey(sortsLast = sortsLast, frequencyScore = score, displayName = name)

    @Test
    fun sortLastGoesAfterEverythingElseEvenWhenFrequent() {
        val poisonousAndFrequent = key("Бледная поганка", sortsLast = true, score = 105.0)
        val plainEdible = key("Сыроежка", score = 3.0)
        assertEquals(
            listOf(plainEdible, poisonousAndFrequent),
            listOf(poisonousAndFrequent, plainEdible).sortedWith(DEFAULT_ORDER),
        )
    }

    @Test
    fun frequentBeatsMerelyImportant() {
        // Страновая частотность весит больше любой глобальной значимости — бонус 100 заведомо
        // выше максимального importance 5.
        val frequentButMinor = key("Козляк", score = 103.0)
        val importantButNotLocal = key("Трюфель", score = 5.0)
        assertEquals(
            listOf(frequentButMinor, importantButNotLocal),
            listOf(importantButNotLocal, frequentButMinor).sortedWith(DEFAULT_ORDER),
        )
    }

    @Test
    fun importanceOrdersWithinTheFrequentGroup() {
        val top = key("Белый гриб", score = 105.0)
        val lesser = key("Моховик", score = 103.0)
        assertEquals(listOf(top, lesser), listOf(lesser, top).sortedWith(DEFAULT_ORDER))
    }

    @Test
    fun alphabetBreaksFullTies() {
        val b = key("Белый гриб", score = 105.0)
        val v = key("Волнушка", score = 105.0)
        assertEquals(listOf(b, v), listOf(v, b).sortedWith(DEFAULT_ORDER))
    }

    @Test
    fun sortLastGroupIsOrderedByTheSameRulesInside() {
        val frequent = key("Мухомор красный", sortsLast = true, score = 105.0)
        val rare = key("Говорушка", sortsLast = true, score = 3.0)
        assertEquals(listOf(frequent, rare), listOf(rare, frequent).sortedWith(DEFAULT_ORDER))
    }
}
