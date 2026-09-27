package klev.fishing.map.data.local

import androidx.room.TypeConverter
import klev.fishing.map.domain.model.CatchOutcome
import klev.fishing.map.domain.model.Cloudiness
import klev.fishing.map.domain.model.FishingMethod
import klev.fishing.map.domain.model.LostReason
import klev.fishing.map.domain.model.Precipitation
import klev.fishing.map.domain.model.PressureTrend
import klev.fishing.map.domain.model.SpeciesSource
import klev.fishing.map.domain.model.WeatherProvenance
import klev.fishing.map.domain.model.WindDirection

/**
 * Перечисления хранятся ИМЕНАМИ, а не порядковыми номерами: добавление варианта в середину enum'а
 * иначе молча переименовало бы все существующие записи. Та же причина и то же решение, что в
 * `:shared` (`data/local/Converters.kt`).
 */
class Converters {
    @TypeConverter fun fromMethod(v: FishingMethod): String = v.name
    @TypeConverter fun toMethod(v: String): FishingMethod = FishingMethod.valueOf(v)

    @TypeConverter fun fromOutcome(v: CatchOutcome): String = v.name
    @TypeConverter fun toOutcome(v: String): CatchOutcome = CatchOutcome.valueOf(v)

    @TypeConverter fun fromLostReason(v: LostReason?): String? = v?.name
    @TypeConverter fun toLostReason(v: String?): LostReason? = v?.let { LostReason.valueOf(it) }

    @TypeConverter fun fromSpeciesSource(v: SpeciesSource): String = v.name
    @TypeConverter fun toSpeciesSource(v: String): SpeciesSource = SpeciesSource.valueOf(v)

    @TypeConverter fun fromPressureTrend(v: PressureTrend?): String? = v?.name
    @TypeConverter fun toPressureTrend(v: String?): PressureTrend? = v?.let { PressureTrend.valueOf(it) }

    @TypeConverter fun fromWindDirection(v: WindDirection?): String? = v?.name
    @TypeConverter fun toWindDirection(v: String?): WindDirection? = v?.let { WindDirection.valueOf(it) }

    @TypeConverter fun fromCloudiness(v: Cloudiness?): String? = v?.name
    @TypeConverter fun toCloudiness(v: String?): Cloudiness? = v?.let { Cloudiness.valueOf(it) }

    @TypeConverter fun fromPrecipitation(v: Precipitation?): String? = v?.name
    @TypeConverter fun toPrecipitation(v: String?): Precipitation? = v?.let { Precipitation.valueOf(it) }

    @TypeConverter fun fromProvenance(v: WeatherProvenance?): String? = v?.name
    @TypeConverter fun toProvenance(v: String?): WeatherProvenance? = v?.let { WeatherProvenance.valueOf(it) }
}
