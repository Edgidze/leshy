package compose.project.leshy.data.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerMode
import platform.UIKit.UIDocumentPickerViewController
import platform.darwin.NSObject

// Older documentTypes/UTI-string initializer instead of the iOS-14+ UTType API, matching this
// project's existing preference for the simpler, longer-supported UIKit surface (see
// CameraLauncher.ios.kt's UIImagePickerController). "public.folder" and "public.zip-archive" are
// the plain Uniform Type Identifier strings for a folder and a zip archive respectively.
private const val FOLDER_UTI = "public.folder"
private const val ZIP_UTI = "public.zip-archive"

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberExportFolderPicker(onPicked: (PickedLocation) -> Unit): () -> Unit =
    rememberDocumentPicker(FOLDER_UTI, UIDocumentPickerMode.UIDocumentPickerModeOpen, onPicked)

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberImportFilePicker(onPicked: (PickedLocation) -> Unit): () -> Unit =
    rememberDocumentPicker(ZIP_UTI, UIDocumentPickerMode.UIDocumentPickerModeImport, onPicked)

@OptIn(ExperimentalForeignApi::class)
@Composable
private fun rememberDocumentPicker(
    allowedUti: String,
    mode: UIDocumentPickerMode,
    onPicked: (PickedLocation) -> Unit,
): () -> Unit {
    val callback = rememberUpdatedState(onPicked)

    val delegate = remember {
        object : NSObject(), UIDocumentPickerDelegateProtocol {
            override fun documentPicker(
                controller: UIDocumentPickerViewController,
                didPickDocumentsAtURLs: List<*>,
            ) {
                controller.dismissViewControllerAnimated(true, completion = null)
                val name = (didPickDocumentsAtURLs.firstOrNull() as? NSURL)?.lastPathComponent
                if (name != null) callback.value(PickedLocation(name))
            }

            override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
                controller.dismissViewControllerAnimated(true, completion = null)
            }
        }
    }

    return remember(allowedUti, mode) {
        {
            val picker = UIDocumentPickerViewController(documentTypes = listOf(allowedUti), inMode = mode)
            picker.delegate = delegate
            UIApplication.sharedApplication.keyWindow
                ?.rootViewController
                ?.presentViewController(picker, animated = true, completion = null)
        }
    }
}
