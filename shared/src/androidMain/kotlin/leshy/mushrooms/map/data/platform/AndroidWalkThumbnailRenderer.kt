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
import kotlin.math.roundToInt
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

/**
 * Поля вокруг маршрута — долей меньшей стороны снимка, не пикселями.
 *
 * Пикселями они и были (24) — при единственном тогда размере снимка 240×240, то есть десятой его
 * доли. Когда снимок вырос до 960×540, те же 24 пикселя стали сороковой долей, и маршрут поехал
 * впритык к рамке: крайняя точка находки (её радиус ровно 24 пикселя и есть) касалась края, а на
 * заставке во всю ширину это читается как обрезанный маршрут. Доля взята прежняя, поэтому поля
 * выглядят так же, как выглядели на снимке 240×240.
 */
private const val SNAPSHOT_PADDING_FRACTION = 24f / 240f

// A degenerate (near-zero-span) region — e.g. a walk that barely moved from its start point —
// would zoom the snapshot in absurdly far; pad it out to a reasonable minimum span instead.
private const val MIN_BOUNDS_SPAN_DEGREES = 0.0015

// Пределы координат — раздутая рамка не должна выходить за края мира у полюсов и антимеридиана.
private const val MIN_LATITUDE = -90.0
private const val MAX_LATITUDE = 90.0
private const val MIN_LONGITUDE = -180.0
private const val MAX_LONGITUDE = 180.0

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

/**
 * Обводка вокруг точки находки, долей её радиуса, и её цвет.
 *
 * Без обводки густые находки сливались в сплошное красное пятно: у прогулки на сотню грибов,
 * собранных с одной поляны, точки перекрывают друг друга, и там, где их двадцать, картинка
 * неотличима от той, где их пять. Обводка это чинит сама собой, без единой новой сущности: точки
 * рисуются по очереди, каждая своей заливкой поверх обводки предыдущих, — и плотное место читается
 * чешуёй перекрывающихся кружков, то есть ровно тем, чем оно и является.
 *
 * Белая, потому что снимок всегда светлый: тайлы берутся по [OPEN_FREE_MAP_STYLE_URL] — светлому
 * стилю — независимо от темы приложения (снимок рисуется один раз, на «Финише», и переключение
 * темы его не перерисовывает, см. `MapStyleCacheRepository`).
 *
 * Доля радиуса, а не диаметра, и обводка идёт по самой окружности, а не снаружи неё, — то есть
 * половина её толщины съедает край заливки. При первой попытке здесь стояло 0.4, и владелец на
 * устройстве увидел ровно то, что из этих чисел и следует: обводка занимала больше места, чем сама
 * точка. Нужна тонкая линия, только чтобы разделить соседние кружки, а не ореол вокруг каждого.
 */
private const val FIND_DOT_OUTLINE_FRACTION = 0.15f
private const val FIND_DOT_OUTLINE_COLOR = "#FFFFFF"

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

                val region = regionOf(track + findLocations + listOfNotNull(anchor))

                // По меньшей стороне: поле обязано быть заметным на той оси, которая и определяет
                // вписывание, а это всегда более тесная из двух.
                val padding = (minOf(widthPx, heightPx) * SNAPSHOT_PADDING_FRACTION).roundToInt()
                val options = MapSnapshotter.Options(widthPx, heightPx)
                    .withStyleBuilder(Style.Builder().fromUri(OPEN_FREE_MAP_STYLE_URL))
                    .withRegion(region)
                    .withPadding(padding, padding, padding, padding)

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

    /**
     * Рамка снимка по всем известным точкам, сразу с минимальным размахом [MIN_BOUNDS_SPAN_DEGREES].
     *
     * **Считается по краям вручную, а не скармливанием точек в `LatLngBounds.Builder`, и это не
     * стилистика.** `Builder.build()` бросает `InvalidLatLngBoundsException`, если ему дали МЕНЬШЕ
     * ДВУХ точек, — а ровно одна точка бывает сплошь и рядом: прогулка с единственной находкой, у
     * которой ещё не успело набраться ни одной точки трека. Исключение ловил `catch` в [render],
     * снимок молча не рисовался, и `BackfillWalkThumbnailsUseCase` при каждом следующем заходе в
     * архив честно повторял то же самое с тем же результатом — миниатюра не появлялась уже
     * никогда. Владелец нашёл это по симптому, который сам по себе выглядит бессмыслицей: с одним
     * грибом миниатюры нет, с двумя — есть. Две одинаковые точки для `Builder` — уже «два
     * элемента», и он не возражает.
     *
     * Здесь же и прежний [padIfDegenerate]: раздувать вырожденную рамку всё равно приходится
     * (одна точка или несколько совпавших дают нулевой размах, а снимок нулевой области — ничто),
     * и делать это одним действием с построением честнее, чем чинить уже построенное.
     */
    private fun regionOf(points: List<GeoPoint>): LatLngBounds {
        val halfSpan = MIN_BOUNDS_SPAN_DEGREES / 2
        val south = (points.minOf { it.lat } - halfSpan).coerceAtLeast(MIN_LATITUDE)
        val north = (points.maxOf { it.lat } + halfSpan).coerceAtMost(MAX_LATITUDE)
        val west = (points.minOf { it.lon } - halfSpan).coerceAtLeast(MIN_LONGITUDE)
        val east = (points.maxOf { it.lon } + halfSpan).coerceAtMost(MAX_LONGITUDE)
        return LatLngBounds.Builder()
            .include(LatLng(south, west))
            .include(LatLng(north, east))
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
                val outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.parseColor(FIND_DOT_OUTLINE_COLOR)
                    style = Paint.Style.STROKE
                    strokeWidth = findDotRadius * FIND_DOT_OUTLINE_FRACTION
                }
                val pixel = pixelOf(locationDot)
                canvas.drawCircle(pixel.x, pixel.y, findDotRadius, fillPaint)
                canvas.drawCircle(pixel.x, pixel.y, findDotRadius, outlinePaint)
            }
        }

        val findPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor(FIND_COLOR)
            style = Paint.Style.FILL
        }
        val findOutlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor(FIND_DOT_OUTLINE_COLOR)
            style = Paint.Style.STROKE
            strokeWidth = findDotRadius * FIND_DOT_OUTLINE_FRACTION
        }

        // Заливка и обводка одной точки — вместе и в этом порядке, поэтому и вынесены: разнеси их
        // по двум проходам (все заливки, потом все обводки), и обводки легли бы поверх соседних
        // точек сплошной сеткой, а перекрытия перестали бы читаться.
        fun drawFindDot(pixel: PointF) {
            canvas.drawCircle(pixel.x, pixel.y, findDotRadius, findPaint)
            canvas.drawCircle(pixel.x, pixel.y, findDotRadius, findOutlinePaint)
        }

        if (speciesMarkers.isNotEmpty()) {
            for (marker in speciesMarkers) {
                val pixel = pixelOf(marker.location)
                val iconBitmap = resolveCategoryIconBytes(marker.category, photoStorage)
                    ?.let { bytes -> runCatching { BitmapFactory.decodeByteArray(bytes, 0, bytes.size) }.getOrNull() }
                if (iconBitmap != null) {
                    canvas.drawIconAspectFit(iconBitmap, pixel, markerIconSizePx)
                } else {
                    drawFindDot(pixel)
                }
            }
        } else {
            findLocations.forEach { point -> drawFindDot(pixelOf(point)) }
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
