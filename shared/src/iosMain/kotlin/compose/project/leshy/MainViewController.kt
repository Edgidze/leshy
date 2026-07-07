package compose.project.leshy

import androidx.compose.ui.window.ComposeUIViewController
import compose.project.leshy.di.initKoin

fun MainViewController() = run {
    initKoin()
    ComposeUIViewController { App() }
}