package leshy.mushrooms.map.data.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.Foundation.writeToFile
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberCameraLauncher(onPhotoCaptured: (String) -> Unit): () -> Unit {
    val callback = rememberUpdatedState(onPhotoCaptured)

    val delegate = remember {
        object : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
            override fun imagePickerController(
                picker: UIImagePickerController,
                didFinishPickingMediaWithInfo: Map<Any?, *>,
            ) {
                picker.dismissViewControllerAnimated(true, completion = null)
                val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
                val path = image?.let { saveImageToDocuments(it, "mark") }
                if (path != null) callback.value(path)
            }

            override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
                picker.dismissViewControllerAnimated(true, completion = null)
            }
        }
    }

    return remember {
        {
            val picker = UIImagePickerController()
            picker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
            picker.delegate = delegate
            UIApplication.sharedApplication.keyWindow
                ?.rootViewController
                ?.presentViewController(picker, animated = true, completion = null)
        }
    }
}

/**
 * Writes [image] into this app's Documents directory as JPEG and returns its absolute path, or
 * null if either step fails. Shared with [rememberGalleryPicker] (`GalleryPicker.ios.kt`), which
 * has the identical job with a different [fileNamePrefix] — both hand the caller a plain file path,
 * the contract `rememberCameraLauncher` established.
 */
@OptIn(ExperimentalForeignApi::class)
internal fun saveImageToDocuments(image: UIImage, fileNamePrefix: String): String? {
    val data = UIImageJPEGRepresentation(image, 0.9) ?: return null
    val documentsPath = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )?.path ?: return null
    val filePath = "$documentsPath/${fileNamePrefix}_${currentTimeMillis()}.jpg"
    return if (data.writeToFile(filePath, atomically = true)) filePath else null
}
