package leshy.mushrooms.map

import androidx.compose.ui.window.ComposeUIViewController
import leshy.mushrooms.map.di.initKoin

fun MainViewController() = run {
    initKoin()
    ComposeUIViewController { App() }
}