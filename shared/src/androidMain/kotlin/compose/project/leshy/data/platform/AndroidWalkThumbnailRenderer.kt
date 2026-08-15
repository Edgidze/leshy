package compose.project.leshy.data.platform

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import compose.project.leshy.domain.model.GeoPoint
import compose.project.leshy.ui.map.OPEN_FREE_MAP_STYLE_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.Style
import org.maplibre.android.snapshotter.MapSnapshot
import org.maplibre.android.snapshotter.MapSnapshotter
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume

private const val THUMBNAIL_PIXELS = 240
private const val SNAPSHOT_PADDING_PX = 24

// A degenerate (near-zero-span) region — e.g. a walk that barely moved from its start point —
// would zoom the snapshot in absurdly far; pad it out to a reasonable minimum span instead.
private const val MIN_BOUNDS_SPAN_DEGREES = 0.0015

private const val ROUTE_COLOR = "#1B4332" // LeshyGreen, ui/theme/Theme.kt — not reachable from here.
private const val FIND_COLOR = "#B3261E" // Material3 baseline light colorScheme.error.

class AndroidWalkThumbnailRenderer(private val context: Context) : WalkThumbnailRenderer {

    override suspend fun render(walkId: Long, track: List<GeoPoint>, findLocations: List<GeoPoint>): String? {
        if (track.size < 2) return null
        return try {
            val snapshot = takeSnapshot(track, findLocations) ?: return null
            withContext(Dispatchers.IO) { writeAnnotated(walkId, snapshot, track, findLocations) }
        } catch (_: Exception) {
            null
        }
    }

    // MapSnapshotter is @UiThread-only ("for access to the main looper").
    private suspend fun takeSnapshot(track: List<GeoPoint>, findLocations: List<GeoPoint>): MapSnapshot? =
        withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                val boundsBuilder = LatLngBounds.Builder()
                (track + findLocations).forEach { boundsBuilder.include(LatLng(it.lat, it.lon)) }
                val region = padIfDegenerate(boundsBuilder.build())

                val options = MapSnapshotter.Options(THUMBNAIL_PIXELS, THUMBNAIL_PIXELS)
                    .withStyleBuilder(Style.Builder().fromUri(OPEN_FREE_MAP_STYLE_URL))
                    .withRegion(region)
                    .withPadding(SNAPSHOT_PADDING_PX, SNAPSHOT_PADDING_PX, SNAPSHOT_PADDING_PX, SNAPSHOT_PADDING_PX)

                val snapshotter = MapSnapshotter(context, options)
                snapshotter.start(
                    { snapshot -> if (continuation.isActive) continuation.resume(snapshot) },
                    { _ -> if (continuation.isActive) continuation.resume(null) },
                )
                continuation.invokeOnCancellation { snapshotter.cancel() }
            }
        }

    private fun padIfDegenerate(bounds: LatLngBounds): LatLngBounds {
        if (bounds.latitudeSpan >= MIN_BOUNDS_SPAN_DEGREES && bounds.longitudeSpan >= MIN_BOUNDS_SPAN_DEGREES) {
            return bounds
        }
        val center = bounds.center
        return LatLngBounds.Builder()
            .include(LatLng(center.latitude - MIN_BOUNDS_SPAN_DEGREES, center.longitude - MIN_BOUNDS_SPAN_DEGREES))
            .include(LatLng(center.latitude + MIN_BOUNDS_SPAN_DEGREES, center.longitude + MIN_BOUNDS_SPAN_DEGREES))
            .build()
    }

    private fun writeAnnotated(
        walkId: Long,
        snapshot: MapSnapshot,
        track: List<GeoPoint>,
        findLocations: List<GeoPoint>,
    ): String? {
        val mutableBitmap = snapshot.bitmap.copy(Bitmap.Config.ARGB_8888, true) ?: return null
        val canvas = Canvas(mutableBitmap)

        fun pixelOf(point: GeoPoint): PointF = snapshot.pixelForLatLng(LatLng(point.lat, point.lon))

        val routePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor(ROUTE_COLOR)
            style = Paint.Style.STROKE
            strokeWidth = 5f
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        val path = Path()
        track.forEachIndexed { index, point ->
            val pixel = pixelOf(point)
            if (index == 0) path.moveTo(pixel.x, pixel.y) else path.lineTo(pixel.x, pixel.y)
        }
        canvas.drawPath(path, routePaint)

        val findPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor(FIND_COLOR)
            style = Paint.Style.FILL
        }
        findLocations.forEach { point ->
            val pixel = pixelOf(point)
            canvas.drawCircle(pixel.x, pixel.y, 6f, findPaint)
        }

        val thumbnailsDir = File(context.filesDir, "thumbnails").apply { mkdirs() }
        val file = File(thumbnailsDir, "walk_$walkId.png")
        return try {
            FileOutputStream(file).use { out -> mutableBitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
            file.absolutePath
        } catch (_: Exception) {
            null
        }
    }
}
