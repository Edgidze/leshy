package leshy.mushrooms.map.data.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.launch
import okio.BufferedSink
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerMode
import platform.UIKit.UIDocumentPickerViewController
import platform.darwin.NSObject

// Older documentTypes/UTI-string initializer instead of the iOS-14+ UTType API, matching this
// project's existing preference for the simpler, longer-supported UIKit surface (see
// CameraLauncher.ios.kt's UIImagePickerController). "public.zip-archive" is the plain Uniform Type
// Identifier string for a zip archive.
private const val ZIP_UTI = "public.zip-archive"

/**
 * No iOS dialog combines "pick a destination" with "write bytes there" the way Android's
 * `CreateDocument` does, so the archive is written to a throwaway temp file first, then
 * `UIDocumentPickerViewController` in `.ExportToService` mode (the older single-URL export API,
 * not the newer `forExporting:` one — same "simpler, longer-supported UIKit surface" preference as
 * elsewhere in this file) lets the user copy that temp file wherever they want. No security-scoped
 * bookmarks needed since the destination is picked after the file already exists, not before.
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberExportLauncher(
    suggestedFileName: String,
    writeArchive: suspend (BufferedSink) -> Unit,
    onResult: (Result<Unit>) -> Unit,
): () -> Unit {
    val scope = rememberCoroutineScope()
    val callback = rememberUpdatedState(onResult)

    val delegate = remember {
        object : NSObject(), UIDocumentPickerDelegateProtocol {
            override fun documentPicker(
                controller: UIDocumentPickerViewController,
                didPickDocumentsAtURLs: List<*>,
            ) {
                controller.dismissViewControllerAnimated(true, completion = null)
                callback.value(Result.success(Unit))
            }

            override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
                controller.dismissViewControllerAnimated(true, completion = null)
                // The archive was already written to tempPath by the time this sheet shows — the
                // user only declined to place a copy anywhere else — so this still counts as a
                // completed (successful) write, not a failure. Must still resolve onResult either
                // way: leaving it uncalled here would leave DataViewModel's isProcessing stuck.
                callback.value(Result.success(Unit))
            }
        }
    }

    return remember(suggestedFileName) {
        {
            scope.launch {
                val tempPath = NSTemporaryDirectory() + suggestedFileName
                val writeResult = runCatching {
                    // BufferedSink doesn't conform to kotlin.AutoCloseable on Kotlin/Native, so
                    // kotlin.io.use (which works fine for it on the JVM/Android side) isn't
                    // available here — close it manually instead.
                    val sink = FileSystem.SYSTEM.sink(tempPath.toPath()).buffer()
                    try {
                        writeArchive(sink)
                    } finally {
                        sink.close()
                    }
                }
                if (writeResult.isFailure) {
                    callback.value(writeResult)
                    return@launch
                }
                val picker = UIDocumentPickerViewController(
                    uRL = NSURL.fileURLWithPath(tempPath),
                    inMode = UIDocumentPickerMode.UIDocumentPickerModeExportToService,
                )
                picker.delegate = delegate
                UIApplication.sharedApplication.keyWindow
                    ?.rootViewController
                    ?.presentViewController(picker, animated = true, completion = null)
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberImportFilePicker(onPicked: (PickedLocation) -> Unit): () -> Unit {
    val callback = rememberUpdatedState(onPicked)

    val delegate = remember {
        object : NSObject(), UIDocumentPickerDelegateProtocol {
            override fun documentPicker(
                controller: UIDocumentPickerViewController,
                didPickDocumentsAtURLs: List<*>,
            ) {
                controller.dismissViewControllerAnimated(true, completion = null)
                val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL
                val name = url?.lastPathComponent
                val path = url?.path
                if (name != null && path != null) callback.value(PickedLocation(name, path))
            }

            override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
                controller.dismissViewControllerAnimated(true, completion = null)
            }
        }
    }

    return remember {
        {
            val picker = UIDocumentPickerViewController(
                documentTypes = listOf(ZIP_UTI),
                inMode = UIDocumentPickerMode.UIDocumentPickerModeImport,
            )
            picker.delegate = delegate
            UIApplication.sharedApplication.keyWindow
                ?.rootViewController
                ?.presentViewController(picker, animated = true, completion = null)
        }
    }
}
