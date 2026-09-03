package leshy.mushrooms.map.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import leshy.mushrooms.map.domain.model.ThemeMode
import platform.UIKit.UIApplication
import platform.UIKit.UIUserInterfaceStyle

/**
 * Кроссплатформенного Compose-API на `UIStatusBarStyle` нет, а `MainViewController.kt`
 * (`ComposeUIViewController { App() }`) не даёт добраться до `preferredStatusBarStyle` контроллера
 * без правки Swift-хоста. Вместо этого переключается `overrideUserInterfaceStyle` окна: статус-бар
 * со стилем `Default` сам выбирает светлые/тёмные значки по трейтам окна, без ручного
 * `setNeedsStatusBarAppearanceUpdate()` и без сабклассинга контроллера — заодно под нужный вид
 * подстраиваются и все системные UIKit-элементы (клавиатура, шиты выбора файла/галереи).
 *
 * `keyWindow` — та же идиома, что у всего остального iOS-кода в проекте
 * (`ShareLauncher`/`CameraLauncher`/`GalleryPicker`/`DataLocationPicker`); приложение
 * однооконное, и `SceneDelegate` явно делает своё окно ключевым (`iosApp/CLAUDE.md`).
 *
 * `Unspecified` в ветке SYSTEM — не «ничего не делать», а обязательный сброс: без него окно,
 * которому один раз прописали Light/Dark, перестаёт наследовать системную тему навсегда (см.
 * `ApplySystemBarsAppearance` в commonMain).
 */
@Composable
actual fun ApplySystemBarsAppearance(mode: ThemeMode) {
    LaunchedEffect(mode) {
        UIApplication.sharedApplication.keyWindow?.overrideUserInterfaceStyle = when (mode) {
            ThemeMode.LIGHT -> UIUserInterfaceStyle.UIUserInterfaceStyleLight
            ThemeMode.DARK -> UIUserInterfaceStyle.UIUserInterfaceStyleDark
            ThemeMode.SYSTEM -> UIUserInterfaceStyle.UIUserInterfaceStyleUnspecified
        }
    }
}
