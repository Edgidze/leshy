package leshy.mushrooms.map.data.platform

import MapLibre.MLNCoordinateBoundsMake
import MapLibre.MLNMapCamera
import MapLibre.MLNMapSnapshot
import MapLibre.MLNMapSnapshotOptions
import MapLibre.MLNMapSnapshotter
import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.ui.map.OPEN_FREE_MAP_STYLE_URL
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.useContents
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreGraphics.CGPointMake
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.writeToFile
import platform.UIKit.UIBezierPath
import platform.UIKit.UIColor
import platform.UIKit.UIGraphicsImageRenderer
import platform.UIKit.UIGraphicsImageRendererFormat
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import kotlin.coroutines.resume
import kotlin.math.max
import kotlin.math.min

// Same rationale as AndroidWalkThumbnailRenderer: a near-zero-span region (a walk that barely
// moved from its start point) would otherwise zoom the snapshot in absurdly far.
private const val MIN_BOUNDS_SPAN_DEGREES = 0.0015

/**
 * Толщина линии маршрута и радиус точки находки — долями ширины снимка, не пикселями; ровно та же
 * правка и по той же причине, что в `AndroidWalkThumbnailRenderer`, см. её там.
 *
 * Числа взяты из прежних здешних (3.0 и 4.0 при снимке 240 точек шириной) и потому НЕ совпадают с
 * андроидными: линия тут исторически тоньше — 1.25% ширины против 2.08%. Расхождение оставлено
 * как есть намеренно: эта правка меняет разрешение снимка, а не его вид, и приводить две
 * платформы к одному числу здесь значило бы заодно менять внешность iOS-снимка, ни разу её не
 * увидев. Сводить — отдельной задачей и с картинками обеих платформ перед глазами.
 */
private const val ROUTE_STROKE_FRACTION = 3.0 / 240.0
private const val FIND_DOT_RADIUS_FRACTION = 4.0 / 240.0

/**
 * Обводка вокруг точки находки, долей её радиуса. Зачем она и почему белая — в
 * `AndroidWalkThumbnailRenderer`, у одноимённой константы; здесь то же самое и теми же числами:
 * это одна картинка, которая обязана выглядеть одинаково на обеих платформах.
 */
private const val FIND_DOT_OUTLINE_FRACTION = 0.15

private const val ROUTE_RED = 0x1B / 255.0
private const val ROUTE_GREEN = 0x43 / 255.0
private const val ROUTE_BLUE = 0x32 / 255.0 // LeshyGreen, ui/theme/Theme.kt — not reachable from here.

private const val FIND_RED = 0xB3 / 255.0
private const val FIND_GREEN = 0x26 / 255.0
private const val FIND_BLUE = 0x1E / 255.0 // Material3 baseline light colorScheme.error.

class IosWalkThumbnailRenderer(private val photoStorage: PhotoStorage) : WalkThumbnailRenderer {

    @OptIn(ExperimentalForeignApi::class)
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
            val snapshot = takeSnapshot(track, findLocations, anchor, widthPx.toDouble(), heightPx.toDouble())
                ?: return null
            writeAnnotated(walkId, snapshot, track, findLocations, anchor, variant, speciesMarkers, markerIconSizePx.toDouble())
        } catch (_: Throwable) {
            null
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private suspend fun takeSnapshot(
        track: List<GeoPoint>,
        findLocations: List<GeoPoint>,
        anchor: GeoPoint?,
        widthPoints: Double,
        heightPoints: Double,
    ): MLNMapSnapshot? =
        suspendCancellableCoroutine { continuation ->
            val allPoints = track + findLocations + listOfNotNull(anchor)
            var minLat = allPoints.first().lat
            var maxLat = minLat
            var minLon = allPoints.first().lon
            var maxLon = minLon
            allPoints.forEach { point ->
                minLat = min(minLat, point.lat)
                maxLat = max(maxLat, point.lat)
                minLon = min(minLon, point.lon)
                maxLon = max(maxLon, point.lon)
            }
            if (maxLat - minLat < MIN_BOUNDS_SPAN_DEGREES) {
                val centerLat = (minLat + maxLat) / 2
                minLat = centerLat - MIN_BOUNDS_SPAN_DEGREES / 2
                maxLat = centerLat + MIN_BOUNDS_SPAN_DEGREES / 2
            }
            if (maxLon - minLon < MIN_BOUNDS_SPAN_DEGREES) {
                val centerLon = (minLon + maxLon) / 2
                minLon = centerLon - MIN_BOUNDS_SPAN_DEGREES / 2
                maxLon = centerLon + MIN_BOUNDS_SPAN_DEGREES / 2
            }

            val bounds = MLNCoordinateBoundsMake(
                CLLocationCoordinate2DMake(minLat, minLon),
                CLLocationCoordinate2DMake(maxLat, maxLon),
            )

            val options = MLNMapSnapshotOptions(
                styleURL = NSURL(string = OPEN_FREE_MAP_STYLE_URL),
                camera = MLNMapCamera.camera(),
                size = CGSizeMake(widthPoints, heightPoints),
            )
            // `size` у MLNMapSnapshotOptions — в ТОЧКАХ, и множителем по умолчанию берётся масштаб
            // экрана устройства. То есть до этой строки запрошенные 240 превращались в файл 720×720
            // на телефоне с масштабом 3 и 480×480 на телефоне с масштабом 2 — разрешение снимка
            // зависело от того, на каком телефоне он снят, а параметр с именем `widthPx` означал
            // пиксели только на Android. Явная единица делает точку пикселем: сколько запрошено,
            // столько и получится, одинаково на обеих платформах и на любом устройстве.
            options.scale = 1.0
            options.coordinateBounds = bounds

            val snapshotter = MLNMapSnapshotter(options = options)
            snapshotter.startWithCompletionHandler { snapshot, _ ->
                if (continuation.isActive) continuation.resume(snapshot)
            }
            continuation.invokeOnCancellation { snapshotter.cancel() }
        }

    @OptIn(ExperimentalForeignApi::class)
    private suspend fun writeAnnotated(
        walkId: Long,
        snapshot: MLNMapSnapshot,
        track: List<GeoPoint>,
        findLocations: List<GeoPoint>,
        anchor: GeoPoint?,
        variant: String,
        speciesMarkers: List<WalkFindMarker>,
        markerIconSizePoints: Double,
    ): String? {
        val baseImage = snapshot.image
        val imageWidth = baseImage.size.useContents { width }
        val routeLineWidth = imageWidth * ROUTE_STROKE_FRACTION
        val findDotRadius = imageWidth * FIND_DOT_RADIUS_FRACTION

        // Icon bytes are resolved up front (suspend, off the UIGraphicsImageRenderer closure —
        // imageWithActions's block isn't a suspend context) into plain UIImages the draw block
        // below can use synchronously, same split as Android's decode-then-draw.
        val markerIcons = speciesMarkers.map { marker ->
            marker to resolveCategoryIconBytes(marker.category, photoStorage)?.let { bytes -> UIImage.imageWithData(bytes.toNSData()) }
        }

        // Тот же множитель, что и у самого снимка выше, и по той же причине: у формата по
        // умолчанию он опять экранный, и слой с маршрутом и находками поверх снимка вернул бы
        // зависимость размера файла от устройства — уже на выходе, после того как её убрали на входе.
        val rendererFormat = UIGraphicsImageRendererFormat.defaultFormat().apply { scale = 1.0 }
        val renderer = UIGraphicsImageRenderer(size = baseImage.size, format = rendererFormat)
        val annotated = renderer.imageWithActions { _ ->
            baseImage.drawAtPoint(CGPointMake(0.0, 0.0))

            fun drawDot(point: GeoPoint, fill: UIColor) {
                val cgPoint = snapshot.pointForCoordinate(CLLocationCoordinate2DMake(point.lat, point.lon))
                cgPoint.useContents {
                    val dotRect = CGRectMake(
                        x - findDotRadius,
                        y - findDotRadius,
                        findDotRadius * 2,
                        findDotRadius * 2,
                    )
                    val dotPath = UIBezierPath.bezierPathWithOvalInRect(dotRect)
                    fill.setFill()
                    dotPath.fill()
                    // Обводка сразу за своей заливкой, а не отдельным проходом по всем точкам:
                    // порядок и есть то, ради чего обводка добавлена, см. FIND_DOT_OUTLINE_FRACTION.
                    dotPath.lineWidth = findDotRadius * FIND_DOT_OUTLINE_FRACTION
                    UIColor.whiteColor.setStroke()
                    dotPath.stroke()
                }
            }

            fun drawFindDot(point: GeoPoint) =
                drawDot(point, UIColor.colorWithRed(FIND_RED, FIND_GREEN, FIND_BLUE, 1.0))

            if (track.size >= 2) {
                val routePath = UIBezierPath()
                track.forEachIndexed { index, point ->
                    val cgPoint = snapshot.pointForCoordinate(CLLocationCoordinate2DMake(point.lat, point.lon))
                    if (index == 0) routePath.moveToPoint(cgPoint) else routePath.addLineToPoint(cgPoint)
                }
                routePath.lineWidth = routeLineWidth
                UIColor.colorWithRed(ROUTE_RED, ROUTE_GREEN, ROUTE_BLUE, 1.0).setStroke()
                routePath.stroke()
            } else {
                // Too few track points for a route line (short walk) — mark the single known
                // location instead of leaving the map background bare.
                val locationDot = track.firstOrNull() ?: anchor
                if (locationDot != null) {
                    drawDot(locationDot, UIColor.colorWithRed(ROUTE_RED, ROUTE_GREEN, ROUTE_BLUE, 1.0))
                }
            }

            if (markerIcons.isNotEmpty()) {
                markerIcons.forEach { (marker, icon) ->
                    if (icon != null) {
                        val cgPoint = snapshot.pointForCoordinate(
                            CLLocationCoordinate2DMake(marker.location.lat, marker.location.lon),
                        )
                        cgPoint.useContents {
                            val (width, height) = icon.size.useContents { width to height }
                            val scale = min(markerIconSizePoints / width, markerIconSizePoints / height)
                            val drawWidth = width * scale
                            val drawHeight = height * scale
                            icon.drawInRect(CGRectMake(x - drawWidth / 2.0, y - drawHeight / 2.0, drawWidth, drawHeight))
                        }
                    } else {
                        drawFindDot(marker.location)
                    }
                }
            } else {
                findLocations.forEach { point -> drawFindDot(point) }
            }
        }

        val data = UIImagePNGRepresentation(annotated) ?: return null
        val documentsPath = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )?.path ?: return null
        val thumbnailsDir = "$documentsPath/thumbnails"
        NSFileManager.defaultManager.createDirectoryAtPath(
            thumbnailsDir,
            withIntermediateDirectories = true,
            attributes = null,
            error = null,
        )
        val filePath = "$thumbnailsDir/walk_$walkId$variant.png"
        return if (data.writeToFile(filePath, atomically = true)) filePath else null
    }
}

/** Same bridging as `IosHttpTextFetcher`/`ImageCodec.ios.kt`'s `NSData.toByteArray()`, reversed —
 * avoids `NSString`/toll-free-bridging pitfalls those files already document. */
@OptIn(ExperimentalForeignApi::class, kotlinx.cinterop.BetaInteropApi::class)
private fun ByteArray.toNSData(): NSData =
    if (isEmpty()) NSData() else usePinned { pinned -> NSData.create(bytes = pinned.addressOf(0), length = size.convert()) }
