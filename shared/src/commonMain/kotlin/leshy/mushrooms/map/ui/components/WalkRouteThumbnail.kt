package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.domain.model.GeoPoint
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max

// A minimum degrees-span floor so a walk with barely any GPS movement (or a single cluster of
// points) doesn't blow up the projection with a near-zero divisor.
private const val MIN_SPAN_DEGREES = 0.0003
private const val MIN_LON_SCALE = 0.15

/** Доля радиуса точки, уходящая в обводку, — та же, что у снимков с тайлами. */
private const val FIND_DOT_OUTLINE_FRACTION = 0.15f

/**
 * A small, static, offline route silhouette for archive list cards — Strava-style thumbnail,
 * but drawn as a plain [Canvas] polyline rather than an embedded map. A [LazyColumn] of walks can
 * run into dozens of cards; spinning up a real native MapLibre view (see [LiveTrackMap]) per row
 * would mean that many live map engine instances on screen at once, which is not something a
 * scrolling list should do.
 */
@Composable
fun WalkRouteThumbnail(track: List<GeoPoint>, findLocations: List<GeoPoint>, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        if (track.size < 2) return@Box

        val trackColor = MaterialTheme.colorScheme.primary
        val findColor = MaterialTheme.colorScheme.error
        Canvas(modifier = Modifier.matchParentSize()) {
            val allPoints = track + findLocations
            // Longitude degrees shrink towards the poles relative to latitude degrees; scale by
            // cos(latitude) so the thumbnail isn't stretched east-west at higher latitudes.
            val avgLatRad = allPoints.map { it.lat }.average() * (PI / 180.0)
            val lonScale = cos(avgLatRad).let { if (it < MIN_LON_SCALE) MIN_LON_SCALE else it }

            fun projectedX(point: GeoPoint) = point.lon * lonScale
            fun projectedY(point: GeoPoint) = point.lat

            val minX = allPoints.minOf(::projectedX)
            val maxX = allPoints.maxOf(::projectedX)
            val minY = allPoints.minOf(::projectedY)
            val maxY = allPoints.maxOf(::projectedY)
            val centerX = (minX + maxX) / 2
            val centerY = (minY + maxY) / 2
            val span = max(max(maxX - minX, maxY - minY), MIN_SPAN_DEGREES)

            val padding = 6.dp.toPx()
            val drawableSize = size.minDimension - padding * 2

            fun toOffset(point: GeoPoint): Offset {
                val nx = (projectedX(point) - centerX) / span
                val ny = (projectedY(point) - centerY) / span
                return Offset(
                    x = (size.width / 2 + nx * drawableSize).toFloat(),
                    y = (size.height / 2 - ny * drawableSize).toFloat(),
                )
            }

            val path = Path().apply {
                track.forEachIndexed { index, point ->
                    val offset = toOffset(point)
                    if (index == 0) moveTo(offset.x, offset.y) else lineTo(offset.x, offset.y)
                }
            }
            drawPath(
                path = path,
                color = trackColor,
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
            )

            // Заливка и обводка каждой точки — вместе и в этом порядке, как в снимках с настоящими
            // тайлами (`AndroidWalkThumbnailRenderer`, константа FIND_DOT_OUTLINE_FRACTION — там же
            // и о том, зачем обводка вообще). Здесь это тем более уместно: силуэт рисуется, когда
            // снимка нет, и остаётся единственной картинкой прогулки.
            val findRadius = 3.dp.toPx()
            findLocations.forEach { point ->
                val center = toOffset(point)
                drawCircle(color = findColor, radius = findRadius, center = center)
                drawCircle(
                    color = Color.White,
                    radius = findRadius,
                    center = center,
                    style = Stroke(width = findRadius * FIND_DOT_OUTLINE_FRACTION),
                )
            }
        }
    }
}
