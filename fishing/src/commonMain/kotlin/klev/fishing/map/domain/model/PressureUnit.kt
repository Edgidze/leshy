package klev.fishing.map.domain.model

/** Единица показа давления. В базе давление ВСЕГДА в гПа — иначе смена настройки переписывала бы
 *  историю. */
enum class PressureUnit {
    HPA,
    MM_HG,
}

private const val MM_HG_PER_HPA = 0.750061683

fun Double.hpaTo(unit: PressureUnit): Double = when (unit) {
    PressureUnit.HPA -> this
    PressureUnit.MM_HG -> this * MM_HG_PER_HPA
}

fun Double.toHpaFrom(unit: PressureUnit): Double = when (unit) {
    PressureUnit.HPA -> this
    PressureUnit.MM_HG -> this / MM_HG_PER_HPA
}
