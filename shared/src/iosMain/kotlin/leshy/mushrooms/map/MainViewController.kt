package leshy.mushrooms.map

import androidx.compose.ui.window.ComposeUIViewController
import leshy.mushrooms.map.di.initKoin

/**
 * Вызывается из `SceneDelegate.scene(_:willConnectTo:options:)` — то есть НЕ обязательно один
 * раз за жизнь процесса: система вправе отключить сцену у свёрнутого приложения и позже
 * подключить её заново, и тогда этот вызов повторится в том же процессе. `startKoin` при
 * повторном вызове бросает `KoinApplicationAlreadyStartedException`, поэтому гейт обязателен —
 * без него переподключение сцены роняло бы приложение на ровном месте.
 *
 * Гейт живёт здесь, а не в общем `initKoin()`: на Android тот зовётся из
 * `LeshyApplication.onCreate`, ровно один раз на процесс по построению, и молчаливый
 * no-op на повторный вызов там скорее спрятал бы ошибку, чем помог.
 */
private var koinStarted = false

fun MainViewController() = run {
    if (!koinStarted) {
        initKoin()
        koinStarted = true
    }
    ComposeUIViewController { App() }
}
