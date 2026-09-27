package klev.fishing.map

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import klev.fishing.map.ui.FishingApp

class MainActivity : ComponentActivity() {
    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { /* Доступ к геопозиции проверяется защитно в любом случае. */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // CAMERA не запрашивается заранее — только по нажатию на снимок улова, как у грибного хоста.
        val permissions = buildList {
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        requestPermissions.launch(permissions.toTypedArray())

        setContent {
            FishingApp()
        }
    }
}
