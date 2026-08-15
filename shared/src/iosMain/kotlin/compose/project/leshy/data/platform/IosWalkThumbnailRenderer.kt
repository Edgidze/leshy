package compose.project.leshy.data.platform

import MapLibre.MLNCoordinateBoundsMake
import MapLibre.MLNMapCamera
import MapLibre.MLNMapSnapshot
import MapLibre.MLNMapSnapshotOptions
import MapLibre.MLNMapSnapshotter
import compose.project.leshy.domain.model.GeoPoint
import compose.project.leshy.ui.map.OPEN_FREE_MAP_STYLE_URL
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreGraphics.CGPointMake
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.writeToFile
import platform.UIKit.UIBezierPath
import platform.UIKit.UIColor
import platform.UIKit.UIGraphicsImageRenderer
import platform.UIKit.UIImagePNGRepresentation
import kotlin.coroutines.resume
import kotlin.math.max
import kotlin.math.min

private const val THUMBNAIL_POINTS = 240.0

// Same rationale as AndroidWalkThumbnailRenderer: a near-zero-span region (a walk that barely
// moved from its start point) would otherwise zoom the snapshot in absurdly far.
private const val MIN_BOUNDS_SPAN_DEGREES = 0.0015

private const val ROUTE_RED = 0x1B / 255.0
private const val ROUTE_GREEN = 0x43 / 255.0
private const val ROUTE_BLUE = 0x32 / 255.0 // LeshyGreen, ui/theme/Theme.kt — not reachable from here.

private const val FIND_RED = 0xB3 / 255.0
private const val FIND_GREEN = 0x26 / 255.0
private const val FIND_BLUE = 0x1E / 255.0 // Material3 baseline light colorScheme.error.

class IosWalkThumbnailRenderer : WalkThumbnailRenderer {

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun render(walkId: Long, track: List<GeoPoint>, findLocations: List<GeoPoint>): String? {
        if (track.size < 2) return null
        return try {
            val snapshot = takeSnapshot(track, findLocations) ?: return null
            writeAnnotated(walkId, snapshot, track, findLocations)
        } catch (_: Throwable) {
            null
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private suspend fun takeSnapshot(track: List<GeoPoint>, findLocations: List<GeoPoint>): MLNMapSnapshot? =
        suspendCancellableCoroutine { continuation ->
            val allPoints = track + findLocations
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
                size = CGSizeMake(THUMBNAIL_POINTS, THUMBNAIL_POINTS),
            )
            options.coordinateBounds = bounds

            val snapshotter = MLNMapSnapshotter(options = options)
            snapshotter.startWithCompletionHandler { snapshot, _ ->
                if (continuation.isActive) continuation.resume(snapshot)
            }
            continuation.invokeOnCancellation { snapshotter.cancel() }
        }

    @OptIn(ExperimentalForeignApi::class)
    private fun writeAnnotated(
        walkId: Long,
        snapshot: MLNMapSnapshot,
        track: List<GeoPoint>,
        findLocations: List<GeoPoint>,
    ): String? {
        val baseImage = snapshot.image
        val renderer = UIGraphicsImageRenderer(size = baseImage.size)
        val annotated = renderer.imageWithActions { _ ->
            baseImage.drawAtPoint(CGPointMake(0.0, 0.0))

            val routePath = UIBezierPath()
            track.forEachIndexed { index, point ->
                val cgPoint = snapshot.pointForCoordinate(CLLocationCoordinate2DMake(point.lat, point.lon))
                if (index == 0) routePath.moveToPoint(cgPoint) else routePath.addLineToPoint(cgPoint)
            }
            routePath.lineWidth = 3.0
            UIColor.colorWithRed(ROUTE_RED, ROUTE_GREEN, ROUTE_BLUE, 1.0).setStroke()
            routePath.stroke()

            findLocations.forEach { point ->
                val cgPoint = snapshot.pointForCoordinate(CLLocationCoordinate2DMake(point.lat, point.lon))
                cgPoint.useContents {
                    val dotRect = CGRectMake(x - 4.0, y - 4.0, 8.0, 8.0)
                    UIColor.colorWithRed(FIND_RED, FIND_GREEN, FIND_BLUE, 1.0).setFill()
                    UIBezierPath.bezierPathWithOvalInRect(dotRect).fill()
                }
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
        val filePath = "$thumbnailsDir/walk_$walkId.png"
        return if (data.writeToFile(filePath, atomically = true)) filePath else null
    }
}
