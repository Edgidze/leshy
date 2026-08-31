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
@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        let window = UIWindow(frame: UIScreen.main.bounds)
        window.rootViewController = MainViewControllerKt.MainViewController()
        window.makeKeyAndVisible()
        self.window = window
        return true
    }
}
