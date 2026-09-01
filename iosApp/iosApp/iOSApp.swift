import UIKit
import Shared

/// UIKit-хост, а не SwiftUI-обёртка (`UIViewControllerRepresentable` + `.ignoresSafeArea()`).
///
/// Так `ComposeUIViewController` становится корневым контроллером окна напрямую, и его
/// `safeAreaInsets` приходят прямо из UIKit. В SwiftUI-варианте они приходили через
/// `additionalSafeAreaInsets`, которые SwiftUI выставляет отрицательными, чтобы «отменить»
/// безопасную зону для вложенного контроллера — и после поворота экрана туда-обратно
/// (портрет → ландшафт → портрет) отрицательная поправка от ландшафта могла пережить
/// возврат, оставив верхний инсет нулевым: заголовки экранов «сползали» вверх и налезали
/// на системную полосу значков. Полноэкранность (карта под статус-баром) при этом
/// сохраняется: у корневого контроллера окна нет чужой безопасной зоны, которую надо было
/// бы игнорировать.
///
/// **Окно создаёт `SceneDelegate`, а не этот класс.** У приложения есть
/// `UIApplicationSceneManifest` (его генерирует сборка, `INFOPLIST_KEY_
/// UIApplicationSceneManifest_Generation = YES`), а значит iOS ведёт жизненный цикл через
/// `UIScene` и свойство `window` у делегата приложения попросту игнорирует. Пока хостом был
/// SwiftUI, это было незаметно: `@main struct App` ставит собственный scene-делегат сам.
/// Подробности и симптом — в `iosApp/CLAUDE.md`.
@main
class AppDelegate: UIResponder, UIApplicationDelegate {

    /// Подписка на MetricKit ставится здесь, а не в `SceneDelegate`: она нужна одна на
    /// процесс, а сцен может быть несколько. Метод вызывается и при жизненном цикле
    /// `UIScene` — раньше, чем подключается первая сцена.
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        DiagnosticsArchive.shared.start()
        return true
    }

    /// Сцены в манифесте перечислены пустым списком (`UISceneConfigurations = {}`), поэтому
    /// класс делегата задаётся здесь кодом, а не строкой с именем класса в plist: строку
    /// пришлось бы держать в синхроне с именем Swift-модуля, и её опечатку никто бы не поймал
    /// на сборке.
    func application(
        _ application: UIApplication,
        configurationForConnecting connectingSceneSession: UISceneSession,
        options: UIScene.ConnectionOptions
    ) -> UISceneConfiguration {
        let configuration = UISceneConfiguration(
            name: "Default Configuration",
            sessionRole: connectingSceneSession.role
        )
        configuration.delegateClass = SceneDelegate.self
        return configuration
    }
}

class SceneDelegate: UIResponder, UIWindowSceneDelegate {
    var window: UIWindow?

    func scene(
        _ scene: UIScene,
        willConnectTo session: UISceneSession,
        options connectionOptions: UIScene.ConnectionOptions
    ) {
        guard let windowScene = scene as? UIWindowScene else { return }
        let window = UIWindow(windowScene: windowScene)
        window.rootViewController = MainViewControllerKt.MainViewController()
        // Не только показывает окно, но и делает его ключевым: `UIApplication.keyWindow`
        // (устаревшее, но всё ещё то, через что iosMain достаёт rootViewController для
        // камеры/шаринга/файловых пикеров) без этого остаётся nil.
        window.makeKeyAndVisible()
        self.window = window
    }
}
