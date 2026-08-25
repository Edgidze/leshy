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
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import kotlin.coroutines.resume
import kotlin.math.max
import kotlin.math.min

// Same rationale as AndroidWalkThumbnailRenderer: a near-zero-span region (a walk that barely
// moved from its start point) would otherwise zoom the snapshot in absurdly far.
private const val MIN_BOUNDS_SPAN_DEGREES = 0.0015

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
        sizePx: Int,
        variant: String,
        speciesMarkers: List<WalkFindMarker>,
        markerIconSizePx: Int,
    ): String? {
        if (track.isEmpty() && findLocations.isEmpty() && anchor == null) return null
        return try {
            val snapshot = takeSnapshot(track, findLocations, anchor, sizePx.toDouble()) ?: return null
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
        sizePoints: Double,
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
                size = CGSizeMake(sizePoints, sizePoints),
            )
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

        // Icon bytes are resolved up front (suspend, off the UIGraphicsImageRenderer closure —
        // imageWithActions's block isn't a suspend context) into plain UIImages the draw block
        // below can use synchronously, same split as Android's decode-then-draw.
        val markerIcons = speciesMarkers.map { marker ->
            marker to resolveCategoryIconBytes(marker.category, photoStorage)?.let { bytes -> UIImage.imageWithData(bytes.toNSData()) }
        }

        val renderer = UIGraphicsImageRenderer(size = baseImage.size)
        val annotated = renderer.imageWithActions { _ ->
            baseImage.drawAtPoint(CGPointMake(0.0, 0.0))

            if (track.size >= 2) {
                val routePath = UIBezierPath()
                track.forEachIndexed { index, point ->
                    val cgPoint = snapshot.pointForCoordinate(CLLocationCoordinate2DMake(point.lat, point.lon))
                    if (index == 0) routePath.moveToPoint(cgPoint) else routePath.addLineToPoint(cgPoint)
                }
                routePath.lineWidth = 3.0
                UIColor.colorWithRed(ROUTE_RED, ROUTE_GREEN, ROUTE_BLUE, 1.0).setStroke()
                routePath.stroke()
            } else {
                // Too few track points for a route line (short walk) — mark the single known
                // location instead of leaving the map background bare.
                val locationDot = track.firstOrNull() ?: anchor
                if (locationDot != null) {
                    val cgPoint = snapshot.pointForCoordinate(CLLocationCoordinate2DMake(locationDot.lat, locationDot.lon))
                    cgPoint.useContents {
                        val dotRect = CGRectMake(x - 4.0, y - 4.0, 8.0, 8.0)
                        UIColor.colorWithRed(ROUTE_RED, ROUTE_GREEN, ROUTE_BLUE, 1.0).setFill()
                        UIBezierPath.bezierPathWithOvalInRect(dotRect).fill()
                    }
                }
            }

            fun drawFindDot(point: GeoPoint) {
                val cgPoint = snapshot.pointForCoordinate(CLLocationCoordinate2DMake(point.lat, point.lon))
                cgPoint.useContents {
                    val dotRect = CGRectMake(x - 4.0, y - 4.0, 8.0, 8.0)
                    UIColor.colorWithRed(FIND_RED, FIND_GREEN, FIND_BLUE, 1.0).setFill()
                    UIBezierPath.bezierPathWithOvalInRect(dotRect).fill()
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
