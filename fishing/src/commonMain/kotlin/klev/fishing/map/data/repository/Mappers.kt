package klev.fishing.map.data.repository

import klev.fishing.map.data.local.entity.CatchEntity
import klev.fishing.map.data.local.entity.SpeciesEntity
import klev.fishing.map.data.local.entity.TripEntity
import klev.fishing.map.data.local.entity.TripTrackPointEntity
import klev.fishing.map.domain.model.Catch
import klev.fishing.map.domain.model.FishSpecies
import klev.fishing.map.domain.model.SpeciesSource
import klev.fishing.map.domain.model.Trip
import klev.fishing.map.domain.model.TripTrackPoint
import klev.fishing.map.domain.model.TripWeather
import kotlinx.serialization.json.Json
import leshy.mushrooms.map.domain.model.AppLanguage

private val customNamesJson = Json { ignoreUnknownKeys = true }

/** Погода собирается из колонок рыбалки. `null`, если не записано НИЧЕГО: пустой объект со всеми
 *  `null` внутри отличить от «не спрашивали» было бы невозможно, а экран архива должен эти два
 *  случая различать. */
internal fun TripEntity.weatherOrNull(): TripWeather? {
    val anything = airTempC != null || waterTempC != null || pressureHpa != null ||
        pressureTrend != null || windSpeedMps != null || windDirection != null ||
        cloudiness != null || precipitation != null
    if (!anything) return null
    return TripWeather(
        airTempC = airTempC,
        waterTempC = waterTempC,
        pressureHpa = pressureHpa,
        pressureTrend = pressureTrend,
        windSpeedMps = windSpeedMps,
        windDirection = windDirection,
        cloudiness = cloudiness,
        precipitation = precipitation,
        provenance = weatherProvenance ?: klev.fishing.map.domain.model.WeatherProvenance.USER,
    )
}

internal fun TripEntity.toDomain(): Trip = Trip(
    id = id,
    startedAt = startTime,
    finishedAt = endTime,
    title = title,
    thumbnailPath = thumbnailPath,
    method = method,
    distanceMeters = distanceMeters,
    waterBody = waterBody,
    weather = weatherOrNull(),
)

internal fun CatchEntity.toDomain(): Catch = Catch(
    id = id,
    tripId = tripId,
    speciesId = speciesId,
    lat = lat,
    lon = lon,
    timestamp = timestamp,
    weightGrams = weightGrams,
    lengthMm = lengthMm,
    depthCm = depthCm,
    bait = bait,
    outcome = outcome,
    lostReason = lostReason,
    photoPath = photoPath,
    note = note,
)

internal fun Catch.toEntity(): CatchEntity = CatchEntity(
    id = id,
    tripId = tripId,
    speciesId = speciesId,
    lat = lat,
    lon = lon,
    timestamp = timestamp,
    weightGrams = weightGrams,
    lengthMm = lengthMm,
    depthCm = depthCm,
    bait = bait,
    outcome = outcome,
    lostReason = lostReason,
    photoPath = photoPath,
    note = note,
)

internal fun SpeciesEntity.toDomain(): FishSpecies = FishSpecies(
    id = id,
    key = key,
    scientificName = scientificName,
    colorHex = colorHex,
    iconRef = iconRef,
    order = sortOrder,
    isActive = isActive,
    source = source,
    customNames = customNames
        ?.let { runCatching { customNamesJson.decodeFromString<Map<String, String>>(it) }.getOrNull() }
        ?.mapNotNull { (code, name) ->
            AppLanguage.entries.firstOrNull { it.name == code }?.let { it to name }
        }
        ?.toMap()
        .orEmpty(),
)

internal fun TripTrackPointEntity.toDomain(): TripTrackPoint = TripTrackPoint(
    id = id,
    tripId = tripId,
    lat = lat,
    lon = lon,
    timestamp = timestamp,
)

internal fun SpeciesSource.isUser(): Boolean = this == SpeciesSource.USER
