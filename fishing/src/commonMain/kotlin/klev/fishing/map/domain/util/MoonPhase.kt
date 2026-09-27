package klev.fishing.map.domain.util

import kotlin.math.floor

/**
 * Фаза Луны на дату. Считается на месте, а не спрашивается у сервиса: это чистая астрономия,
 * одинаковая во всём мире, и зависимость от сети ради неё была бы бессмысленной — офлайн она
 * обязана работать, потому что офлайн происходит сама рыбалка.
 *
 * Метод — синодический период от известного новолуния. Точность около половины суток, чего для
 * восьми фаз с запасом достаточно; астрономической задачи здесь нет, есть вопрос «молодая или
 * полная».
 */
enum class MoonPhase {
    NEW,
    WAXING_CRESCENT,
    FIRST_QUARTER,
    WAXING_GIBBOUS,
    FULL,
    WANING_GIBBOUS,
    LAST_QUARTER,
    WANING_CRESCENT,
}

/** Новолуние 2000-01-06 18:14 UTC — опорная точка отсчёта. */
private const val REFERENCE_NEW_MOON_MILLIS = 947_182_440_000L

/** Синодический месяц, 29.530588853 суток. */
private const val SYNODIC_MONTH_MILLIS = 2_551_442_876L

/** Доля синодического месяца, прошедшая к [epochMillis]: 0.0 — новолуние, 0.5 — полнолуние. */
fun moonAge(epochMillis: Long): Double {
    val elapsed = (epochMillis - REFERENCE_NEW_MOON_MILLIS).toDouble() / SYNODIC_MONTH_MILLIS
    return elapsed - floor(elapsed)
}

fun moonPhaseAt(epochMillis: Long): MoonPhase {
    // Границы по 1/16 месяца вокруг каждой из четырёх «точных» фаз: новолуние занимает не мгновение,
    // а примерно двое суток по обе стороны, иначе NEW не попадался бы почти никогда.
    val sixteenths = floor(moonAge(epochMillis) * 16.0 + 0.5).toInt() % 16
    return when (sixteenths) {
        0, 15 -> MoonPhase.NEW
        1, 2 -> MoonPhase.WAXING_CRESCENT
        3, 4 -> MoonPhase.FIRST_QUARTER
        5, 6 -> MoonPhase.WAXING_GIBBOUS
        7, 8 -> MoonPhase.FULL
        9, 10 -> MoonPhase.WANING_GIBBOUS
        11, 12 -> MoonPhase.LAST_QUARTER
        else -> MoonPhase.WANING_CRESCENT
    }
}
