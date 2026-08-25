package leshy.mushrooms.map

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { /* Location/notification use is guarded defensively regardless of the outcome. */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // CAMERA is deliberately not requested here — it's requested lazily, only when the user
        // taps the add-place photo placeholder (see rememberCameraPermissionRequester), not
        // proactively at launch.
        val permissions = buildList {
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            // Without this, the background-recording notification silently fails to show on
            // Android 13+ — the foreground service (and thus background GPS tracking) still runs.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        requestPermissions.launch(permissions.toTypedArray())

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}