package leshy.mushrooms.map

import androidx.compose.ui.uikit.OnFocusBehavior
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

/**
 * `onFocusBehavior = DoNothing` — единственная настройка, и она принципиальна. По умолчанию
 * `ComposeUIViewController` идёт с `FocusableAboveKeyboard`, и «поднять сфокусированное поле над
 * клавиатурой» на iOS реализовано сдвигом ВСЕЙ сцены вверх
 * (`ComposeSceneMediator.FocusAboveKeyboardIfNeeded` → `OffsetToFocusedRect`): вместе с полем
 * уезжает и шапка экрана — на iPhone SE она вылезала под статус-бар (репорт 2026-09-07).
 * Шапка обязана стоять на месте всегда, поэтому сдвиг снят целиком, а место над клавиатурой
 * освобождают сами экраны и диалоги — `imePadding()` (или `WindowInsets.safeDrawing`, он на iOS
 * включает `ime`). Правило на будущее: **у любого нового поля ввода должен быть свой
 * `imePadding`** — подстраховки от рантайма больше нет.
 */
fun MainViewController() = run {
    if (!koinStarted) {
        initKoin()
        koinStarted = true
    }
    ComposeUIViewController(configure = { onFocusBehavior = OnFocusBehavior.DoNothing }) { App() }
}
