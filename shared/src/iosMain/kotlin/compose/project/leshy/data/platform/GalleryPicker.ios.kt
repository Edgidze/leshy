package compose.project.leshy.data.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import org.koin.compose.koinInject

/** Uniform Type Identifier covering every still-image format the photo library can hand back —
 * JPEG, PNG and (very common on an iPhone) HEIC. */
private const val IMAGE_UTI = "public.image"

/**
 * `PHPickerViewController` (iOS 14+, and this app targets iOS 15) rather than the
 * `UIImagePickerController` that `rememberCameraLauncher` still uses for the camera: it runs out
 * of process, so it needs no photo-library permission and no `NSPhotoLibraryUsageDescription` in
 * `Info.plist`, and the app only ever sees the single image the user chose.
 *
 * The delegate is held by [remember], i.e. by the composition, exactly like the camera and
 * document pickers in this package — that's what keeps the ARC trap from `iosMain/CLAUDE.md` away:
 * an Objective-C `delegate` property is `weak`, so a delegate that only existed as a local `val`
 * in the launching lambda would be freed before the picker ever called back.
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberGalleryPicker(onPhotoPicked: (String) -> Unit): () -> Unit {
    val photoStorage = koinInject<PhotoStorage>()
    val callback = rememberUpdatedState(onPhotoPicked)

    val delegate = remember(photoStorage) {
        object : NSObject(), PHPickerViewControllerDelegateProtocol {
            override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
                picker.dismissViewControllerAnimated(true, completion = null)
                val provider = (didFinishPicking.firstOrNull() as? PHPickerResult)?.itemProvider ?: return
                // The file representation (rather than loadObjectOfClass(UIImage)) keeps the
                // original file — including HEIC, which the library is full of — instead of
                // decoding a possibly-12MP image into memory just to re-encode it. Its URL is a
                // temporary one the system deletes as soon as this handler returns, so the copy
                // has to happen right here.
                provider.loadFileRepresentationForTypeIdentifier(IMAGE_UTI) { url, _ ->
                    val path = url?.let { copyIntoPhotoStorage(it, photoStorage) } ?: return@loadFileRepresentationForTypeIdentifier
                    // NSItemProvider completes on a private queue; everything downstream is
                    // Compose state, so hop back to the main thread first.
                    dispatch_async(dispatch_get_main_queue()) { callback.value(path) }
                }
            }
        }
    }

    return remember {
        {
            val configuration = PHPickerConfiguration().apply {
                selectionLimit = 1
                filter = PHPickerFilter.imagesFilter()
            }
            val picker = PHPickerViewController(configuration = configuration)
            picker.delegate = delegate
            UIApplication.sharedApplication.keyWindow
                ?.rootViewController
                ?.presentViewController(picker, animated = true, completion = null)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun copyIntoPhotoStorage(url: NSURL, photoStorage: PhotoStorage): String? {
    val sourcePath = url.path ?: return null
    val extension = url.pathExtension?.takeIf { it.isNotEmpty() } ?: "jpg"
    val destination = photoStorage.resolvePath("picked_${currentTimeMillis()}.$extension")
    val copied = NSFileManager.defaultManager.copyItemAtPath(sourcePath, toPath = destination, error = null)
    return if (copied) destination else null
}
