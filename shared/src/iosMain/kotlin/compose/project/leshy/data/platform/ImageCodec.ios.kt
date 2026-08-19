package compose.project.leshy.data.platform

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlin.math.min
import kotlin.math.roundToInt
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.posix.memcpy

/**
 * Redraws the photo through UIKit at the target size instead of decoding it with Skia directly.
 * Two reasons, both about correctness rather than speed: `UIImage` knows the photo's orientation
 * and applies it while drawing (Skia's `Image.makeFromEncoded` ignores EXIF), and it decodes
 * formats Skia can't — an iPhone's own library is full of HEIC.
 *
 * The redrawn image then makes one PNG round-trip to reach Skia, which is what backs
 * [ImageBitmap] on this platform. That's an encode+decode of an already-downscaled image (a few
 * hundred KB), the price for not reimplementing `CGImage` → Skia pixel bridging by hand.
 */
@OptIn(ExperimentalForeignApi::class)
actual suspend fun decodeScaledImage(path: String, maxDimension: Int): ImageBitmap? =
    withContext(Dispatchers.Default) {
        // The class method, not the UIImage(contentsOfFile = ...) constructor: Kotlin/Native types
        // the constructor's result non-null even though it really does return nil for a file that
        // isn't a decodable image.
        val source = UIImage.imageWithContentsOfFile(path) ?: return@withContext null
        val (width, height) = source.size.useContents { width to height }
        if (width <= 0.0 || height <= 0.0) return@withContext null

        val scale = min(1.0, maxDimension / maxOf(width, height))
        val targetWidth = (width * scale).roundToInt().coerceAtLeast(1)
        val targetHeight = (height * scale).roundToInt().coerceAtLeast(1)

        // opaque = false keeps any alpha the source had; scale = 1.0 makes the context's pixel
        // size exactly the size asked for, instead of multiplying it by the screen's scale factor.
        UIGraphicsBeginImageContextWithOptions(
            size = CGSizeMake(targetWidth.toDouble(), targetHeight.toDouble()),
            opaque = false,
            scale = 1.0,
        )
        source.drawInRect(CGRectMake(0.0, 0.0, targetWidth.toDouble(), targetHeight.toDouble()))
        val resized = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()

        val png = resized?.let { UIImagePNGRepresentation(it) } ?: return@withContext null
        runCatching { Image.makeFromEncoded(png.toByteArray()).toComposeImageBitmap() }.getOrNull()
    }

actual fun encodePng(bitmap: ImageBitmap): ByteArray? =
    Image.makeFromBitmap(bitmap.asSkiaBitmap())
        .encodeToData(EncodedImageFormat.PNG)
        ?.bytes

/** `NSString.create(data:encoding:)`-free bridging, same as `IosHttpTextFetcher` — see
 * `ui/map/CLAUDE.md` for why the NSString path is avoided in this project. */
@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    val result = ByteArray(size)
    if (size > 0) {
        result.usePinned { pinned -> memcpy(pinned.addressOf(0), bytes, size.convert()) }
    }
    return result
}
