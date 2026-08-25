package leshy.mushrooms.map.data.platform

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual suspend fun decodeScaledImage(path: String, maxDimension: Int): ImageBitmap? =
    withContext(Dispatchers.IO) {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return@withContext null

        // Smallest power-of-two sample size that already brings the longest side within the limit,
        // so the full-resolution bitmap never exists in memory at all. Powers of two are what
        // BitmapFactory honours exactly; anything else it rounds down, silently decoding bigger.
        var sampleSize = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / sampleSize > maxDimension) sampleSize *= 2

        val decoded = BitmapFactory.decodeFile(path, BitmapFactory.Options().apply { inSampleSize = sampleSize })
            ?: return@withContext null
        decoded.applyExifOrientation(path).asImageBitmap()
    }

/**
 * BitmapFactory ignores a JPEG's EXIF orientation tag, so a photo taken in any orientation but the
 * sensor's native one decodes sideways. Coil applies this automatically wherever the app *displays*
 * photos — this is the manual equivalent for the one path that decodes by hand.
 *
 * `android.media.ExifInterface` (deprecated in favour of the AndroidX one) is used deliberately:
 * it's part of the framework, and the AndroidX artifact would be a new dependency for a handful of
 * constants — see the zero-new-dependencies decision in `.claude/plans/user-mushrooms.md`.
 */
@Suppress("DEPRECATION")
private fun Bitmap.applyExifOrientation(path: String): Bitmap {
    val orientation = runCatching {
        ExifInterface(path).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    }.getOrDefault(ExifInterface.ORIENTATION_NORMAL)

    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
        else -> return this
    }
    val rotated = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    if (rotated != this) recycle()
    return rotated
}

actual fun encodePng(bitmap: ImageBitmap): ByteArray? {
    val stream = ByteArrayOutputStream()
    val encoded = bitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream)
    return if (encoded) stream.toByteArray() else null
}
