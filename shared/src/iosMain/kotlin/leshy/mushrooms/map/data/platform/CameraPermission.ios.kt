package leshy.mushrooms.map.data.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberCameraPermissionRequester(
    onGranted: () -> Unit,
    onDenied: () -> Unit,
): () -> Unit {
    val granted = rememberUpdatedState(onGranted)
    val denied = rememberUpdatedState(onDenied)
    return remember {
        {
            if (AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo) == AVAuthorizationStatusAuthorized) {
                granted.value()
            } else {
                // Already-denied/restricted returns false through this same completion handler
                // without showing anything, so both refusals land on onDenied.
                AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { isGranted ->
                    dispatch_async(dispatch_get_main_queue()) {
                        if (isGranted) granted.value() else denied.value()
                    }
                }
            }
        }
    }
}
