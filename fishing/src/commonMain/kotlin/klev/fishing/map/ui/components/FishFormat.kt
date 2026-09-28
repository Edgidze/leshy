package klev.fishing.map.ui.components

import androidx.compose.runtime.Composable
import klev.fishing.map.domain.model.FishSpecies
import klev.fishing.map.domain.model.PressureUnit
import klev.fishing.map.domain.model.hpaTo
import klev.fishing.map.i18n.FishStringKey
import klev.fishing.map.i18n.fishCatalogName
import klev.fishing.map.i18n.fishStringResource
import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.i18n.LocalAppLanguage
import androidx.compose.runtime.remember
import kotlin.math.roundToInt

/**
 * Имя вида на языке интерфейса. Порядок как у грибов: своё имя пользователя → каталожное →
 * латынь. Латынь, а не английское имя: она одинаково опознаваема всюду.
 */
@Composable
fun speciesDisplayName(species: FishSpecies): String {
    val language = LocalAppLanguage.current
    return remember(species, language) { speciesDisplayName(species, language) }
}

fun speciesDisplayName(species: FishSpecies, language: AppLanguage): String =
    species.customNames[language]
        ?: fishCatalogName(species.key, language)
        ?: species.scientificName
        ?: species.key

/*
 * Длительности и расстояния НЕ форматируются здесь: в `:shared` это уже есть
 * (`ui/util/Formatting.kt` — `formatDurationShort`, `formatDistanceKm`) и уже переведено на 42
 * языка. Своя копия разошлась бы с грибной при первой же правке.
 */

/**
 * Вес: до килограмма — в граммах, дальше — в килограммах с одним знаком. Ровно так его и называют:
 * «триста грамм», «полтора кило».
 */
@Composable
fun formatWeight(grams: Int): String = if (grams < 1000) {
    "$grams ${fishStringResource(FishStringKey.UnitGram)}"
} else {
    val kg = (grams / 100.0).roundToInt() / 10.0
    "$kg ${fishStringResource(FishStringKey.UnitKilogram)}"
}

/**
 * Глубина в метрах с одним знаком: «3,5 м». Второй знак после запятой не значит ничего — эхолот на
 * ходу и сам столько не держит, а с берега глубину и вовсе называют на глаз.
 */
@Composable
fun formatDepth(centimetres: Int): String {
    val metres = (centimetres / 10.0).roundToInt() / 10.0
    return "$metres ${fishStringResource(FishStringKey.UnitMeter)}"
}

@Composable
fun formatLength(millimetres: Int): String {
    val cm = (millimetres / 10.0 * 10).roundToInt() / 10.0
    return "$cm ${fishStringResource(FishStringKey.UnitCentimeter)}"
}

/** Давление в выбранных единицах. В базе всегда гПа — см. [PressureUnit]. */
@Composable
fun formatPressure(hpa: Double, unit: PressureUnit): String {
    val value = hpa.hpaTo(unit)
    val unitLabel = when (unit) {
        PressureUnit.HPA -> fishStringResource(FishStringKey.SettingsPressureHpa)
        PressureUnit.MM_HG -> fishStringResource(FishStringKey.SettingsPressureMmHg)
    }
    return "${value.roundToInt()} $unitLabel"
}

@Composable
fun formatTemperature(celsius: Double): String =
    "${(celsius * 10).roundToInt() / 10.0} ${fishStringResource(FishStringKey.UnitCelsius)}"

@Composable
fun formatWind(metresPerSecond: Double): String =
    "${(metresPerSecond * 10).roundToInt() / 10.0} ${fishStringResource(FishStringKey.UnitMeterPerSecond)}"
