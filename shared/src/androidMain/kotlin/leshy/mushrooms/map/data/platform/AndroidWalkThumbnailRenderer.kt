package leshy.mushrooms.map.data.platform

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.util.Log
import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.ui.map.OPEN_FREE_MAP_STYLE_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.maplibre.android.MapLibre
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.Style
import org.maplibre.android.snapshotter.MapSnapshot
import org.maplibre.android.snapshotter.MapSnapshotter
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume

private const val LOG_TAG = "WalkThumbnailRenderer"

private const val SNAPSHOT_PADDING_PX = 24

// A degenerate (near-zero-span) region — e.g. a walk that barely moved from its start point —
// would zoom the snapshot in absurdly far; pad it out to a reasonable minimum span instead.
private const val MIN_BOUNDS_SPAN_DEGREES = 0.0015

/**
 * Толщина линии маршрута и радиус точки находки — долями ширины снимка, не пикселями.
 *
 * Пикселями они и были заданы (5 и 6) — при единственном тогда размере снимка 240×240. Доли
 * получены из тех самых чисел, поэтому на глаз ничего не изменилось: и на заставке, и на карточке
 * архива снимок показывается растянутым или ужатым до нужной ширины, и линия постоянной доли
 * приходит на экран одной и той же толщины независимо от разрешения файла. Останься они
 * пикселями — на снимке 960 точек шириной маршрут превратился бы в волосок.
 */
private const val ROUTE_STROKE_FRACTION = 5f / 240f
private const val FIND_DOT_RADIUS_FRACTION = 6f / 240f

private const val ROUTE_COLOR = "#1B4332" // LeshyGreen, ui/theme/Theme.kt — not reachable from here.
private const val FIND_COLOR = "#B3261E" // Material3 baseline light colorScheme.error.

class AndroidWalkThumbnailRenderer(
    private val context: Context,
    private val photoStorage: PhotoStorage,
) : WalkThumbnailRenderer {

    override suspend fun render(
        walkId: Long,
        track: List<GeoPoint>,
        findLocations: List<GeoPoint>,
        anchor: GeoPoint?,
        widthPx: Int,
        heightPx: Int,
        variant: String,
        speciesMarkers: List<WalkFindMarker>,
        markerIconSizePx: Int,
    ): String? {
        if (track.isEmpty() && findLocations.isEmpty() && anchor == null) return null
        return try {
            val snapshot = takeSnapshot(track, findLocations, anchor, widthPx, heightPx) ?: return null
            withContext(Dispatchers.IO) {
                writeAnnotated(walkId, snapshot, track, findLocations, anchor, variant, speciesMarkers, markerIconSizePx)
            }
        } catch (e: Exception) {
            Log.w(LOG_TAG, "render($walkId) failed", e)
            null
        }
    }

    // MapSnapshotter is @UiThread-only ("for access to the main looper").
    private suspend fun takeSnapshot(
        track: List<GeoPoint>,
        findLocations: List<GeoPoint>,
        anchor: GeoPoint?,
        widthPx: Int,
        heightPx: Int,
    ): MapSnapshot? =
        withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                // Normally happens implicitly the first time some screen renders a live
                // MaplibreMap/OfflineManager (see ui/map/CLAUDE.md) — a walk finishing on Record
                // always goes through that first. Backfilling thumbnails for imported walks
                // (BackfillWalkThumbnailsUseCase, called from DataViewModel right after import)
                // has no such guarantee: if Data→Import is the very first screen touching maps in
                // this process, MapSnapshotter's native init never ran and it silently produces
                // nothing. getInstance() is idempotent (no-ops once already initialized), so
                // calling it unconditionally here is safe and closes that gap for good.
                MapLibre.getInstance(context)

                val boundsBuilder = LatLngBounds.Builder()
                (track + findLocations + listOfNotNull(anchor)).forEach { boundsBuilder.include(LatLng(it.lat, it.lon)) }
                val region = padIfDegenerate(boundsBuilder.build())

                val options = MapSnapshotter.Options(widthPx, heightPx)
                    .withStyleBuilder(Style.Builder().fromUri(OPEN_FREE_MAP_STYLE_URL))
                    .withRegion(region)
                    .withPadding(SNAPSHOT_PADDING_PX, SNAPSHOT_PADDING_PX, SNAPSHOT_PADDING_PX, SNAPSHOT_PADDING_PX)

                val snapshotter = MapSnapshotter(context, options)
                snapshotter.start(
                    { snapshot -> if (continuation.isActive) continuation.resume(snapshot) },
                    { error ->
                        Log.w(LOG_TAG, "MapSnapshotter error: $error")
                        if (continuation.isActive) continuation.resume(null)
                    },
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

    private suspend fun writeAnnotated(
        walkId: Long,
        snapshot: MapSnapshot,
        track: List<GeoPoint>,
        findLocations: List<GeoPoint>,
        anchor: GeoPoint?,
        variant: String,
        speciesMarkers: List<WalkFindMarker>,
        markerIconSizePx: Int,
    ): String? {
        val mutableBitmap = snapshot.bitmap.copy(Bitmap.Config.ARGB_8888, true) ?: return null
        val canvas = Canvas(mutableBitmap)
        val routeStrokeWidth = mutableBitmap.width * ROUTE_STROKE_FRACTION
        val findDotRadius = mutableBitmap.width * FIND_DOT_RADIUS_FRACTION

        fun pixelOf(point: GeoPoint): PointF = snapshot.pixelForLatLng(LatLng(point.lat, point.lon))

        val routePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor(ROUTE_COLOR)
            style = Paint.Style.STROKE
            strokeWidth = routeStrokeWidth
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        if (track.size >= 2) {
            val path = Path()
            track.forEachIndexed { index, point ->
                val pixel = pixelOf(point)
                if (index == 0) path.moveTo(pixel.x, pixel.y) else path.lineTo(pixel.x, pixel.y)
            }
            canvas.drawPath(path, routePaint)
        } else {
            // Too few track points for a route line (short walk) — mark the single known
            // location instead of leaving the map background bare.
            val locationDot = track.firstOrNull() ?: anchor
            if (locationDot != null) {
                val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.parseColor(ROUTE_COLOR)
                    style = Paint.Style.FILL
                }
                val pixel = pixelOf(locationDot)
                canvas.drawCircle(pixel.x, pixel.y, findDotRadius, fillPaint)
            }
        }

        val findPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor(FIND_COLOR)
            style = Paint.Style.FILL
        }

        if (speciesMarkers.isNotEmpty()) {
            for (marker in speciesMarkers) {
                val pixel = pixelOf(marker.location)
                val iconBitmap = resolveCategoryIconBytes(marker.category, photoStorage)
                    ?.let { bytes -> runCatching { BitmapFactory.decodeByteArray(bytes, 0, bytes.size) }.getOrNull() }
                if (iconBitmap != null) {
                    canvas.drawIconAspectFit(iconBitmap, pixel, markerIconSizePx)
                } else {
                    canvas.drawCircle(pixel.x, pixel.y, findDotRadius, findPaint)
                }
            }
        } else {
            findLocations.forEach { point ->
                val pixel = pixelOf(point)
                canvas.drawCircle(pixel.x, pixel.y, findDotRadius, findPaint)
            }
        }

        val thumbnailsDir = File(context.filesDir, "thumbnails").apply { mkdirs() }
        val file = File(thumbnailsDir, "walk_$walkId$variant.png")
        return try {
            FileOutputStream(file).use { out -> mutableBitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
            file.absolutePath
        } catch (_: Exception) {
            null
        }
    }

    /** Aspect-fits [icon] into a [boxPx]×[boxPx] square centered at [center] and draws it there —
     * the plain-`Canvas` equivalent of `MushroomMarkerPainter`'s `DrawScope` fit math in
     * `ui/map/MushroomMarkerIcon.kt`, which can't be invoked outside a `DrawScope`. */
    private fun Canvas.drawIconAspectFit(icon: Bitmap, center: PointF, boxPx: Int) {
        val scale = minOf(boxPx.toFloat() / icon.width, boxPx.toFloat() / icon.height)
        val drawWidth = icon.width * scale
        val drawHeight = icon.height * scale
        val destRect = Rect(
            (center.x - drawWidth / 2f).toInt(),
            (center.y - drawHeight / 2f).toInt(),
            (center.x + drawWidth / 2f).toInt(),
            (center.y + drawHeight / 2f).toInt(),
        )
        drawBitmap(icon, null, destRect, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
    }
}
