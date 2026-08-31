# iosApp/ — Xcode-проект

## MapLibre через SPM, не CocoaPods

Осознанный выбор (CocoaPods прекращает поддержку новых версий пакетов в
конце 2026, плюс собственные демо/тесты `maplibre-compose` используют SPM).
`shared/build.gradle.kts`: плагин `io.github.frankois944.spmForKmp`
(версия `1.9.1` — та же, что использует `maplibre-compose` 0.13.0),
`iosTarget.swiftPackageConfig { dependency { remotePackageVersion(...) } }`
тянет `maplibre-gl-native-distribution` (`6.25.1`, тоже пиновка из
`maplibre-compose` 0.13.0). Плюс ручные `linkerOpts("-F...", "-rpath", ...)`
на `binaries.all` — без них Kotlin/Native framework не находит собранный
`MapLibre.xcframework`. `gradle.properties` требует
`kotlin.mpp.enableCInteropCommonization=true`.

## Хост — UIKit (`AppDelegate` + `UIWindow`), не SwiftUI

`iOSApp.swift` — обычный `@main class AppDelegate: UIResponder,
UIApplicationDelegate`, который кладёт `MainViewControllerKt.MainViewController()`
прямо корневым контроллером `UIWindow`. Раньше это был SwiftUI-шаблон
(`WindowGroup { ContentView() }`, внутри — `UIViewControllerRepresentable`
с `.ignoresSafeArea()`); `ContentView.swift` удалён.

Причина замены — репорт: на iPhone после поворота экрана в ландшафт и
обратно в портрет заголовки экранов «сползают» вверх к системной полосе
значков, иногда налезая на неё. То есть верхний инсет безопасной зоны
залипает на ландшафтном (нулевом) значении. Единственный правдоподобный
механизм: SwiftUI реализует `.ignoresSafeArea()` для вложенного
`UIViewController` через ОТРИЦАТЕЛЬНЫЕ `additionalSafeAreaInsets`, гасящие
его безопасную зону. Пересчёт этой поправки и доставка нового
`safeAreaInsets` от UIKit при повороте — два независимых события без
гарантий порядка; если ландшафтная (нулевая) поправка переживает возврат в
портрет, `safeAreaInsets.top` у контроллера Compose так и остаётся нулём.
У корневого контроллера окна чужой безопасной зоны, которую надо гасить,
нет вовсе — инсеты приходят прямо из UIKit, и этот класс расхождения
исчезает по построению. Полноэкранность (карта под статус-баром) при этом
сохраняется полностью.

### Окно создаёт `SceneDelegate` — иначе приложение не стартует

**Приложение не запускалось на реальном iPhone (iOS 15.8.8) после перехода на
UIKit-хост.** Причина: сборка генерирует `UIApplicationSceneManifest`
(`INFOPLIST_KEY_UIApplicationSceneManifest_Generation` в `project.pbxproj`), а
наличие этого ключа переводит приложение на жизненный цикл `UIScene`, при
котором UIKit **полностью игнорирует свойство `window` у делегата
приложения**. Первая версия UIKit-хоста создавала окно именно там — в
`application(_:didFinishLaunchingWithOptions:)`. Пока хостом был SwiftUI, это
было незаметно: `@main struct App` ставит собственный scene-делегат сам, и
пустой `UISceneConfigurations` в манифесте его не смущал.

Симулятор эту ошибку НЕ ловит: на iOS 26.5 приложение с тем же манифестом и
тем же кодом запускалось и рисовалось нормально — судя по всему, у новых
версий UIKit есть послабление для пустого `UISceneConfigurations`. На iOS 15
послабления нет. То есть «проверено на симуляторе» здесь не значит ничего —
любое изменение точки входа приложения проверять только на устройстве.

Как сделано сейчас:
- `SceneDelegate` (`iOSApp.swift`) создаёт `UIWindow(windowScene:)` в
  `scene(_:willConnectTo:options:)`, ставит `MainViewController()` корневым и
  зовёт `makeKeyAndVisible()`. Последнее не только показывает окно, но и
  делает его **ключевым**: `UIApplication.keyWindow` — то, через что
  `iosMain` достаёт `rootViewController` для камеры, шаринга и файловых
  пикеров (`CameraLauncher.ios.kt`, `ShareLauncher.ios.kt`,
  `DataLocationPicker.ios.kt`, `GalleryPicker.ios.kt`), и без ключевого окна
  все они молча превращаются в no-op.
- Класс делегата сцены задаётся кодом (`AppDelegate.application(_:
  configurationForConnecting:options:)` → `configuration.delegateClass`), а
  не строкой с именем класса в plist: строку пришлось бы держать в синхроне
  с именем Swift-модуля, и опечатку в ней не поймала бы сборка.
- Сам `UIApplicationSceneManifest` переехал из генератора в наш
  `Info.plist` (`INFOPLIST_KEY_UIApplicationSceneManifest_Generation = NO`)
  ради одного значения: генератор всегда пишет
  `UIApplicationSupportsMultipleScenes = true`, а приложение однооконное —
  второе окно на iPad подняло бы вторую Compose-сцену со своим сбором GPS и
  своей картой.
- **`MainViewController()` теперь идемпотентен по Koin.**
  `scene(_:willConnectTo:)` вызывается не обязательно один раз за жизнь
  процесса: система вправе отключить сцену у свёрнутого приложения и позже
  подключить заново. Второй `startKoin` бросил бы
  `KoinApplicationAlreadyStartedException` — гейт стоит в
  `MainViewController.kt` (не в общем `initKoin()`: на Android тот зовётся из
  `LeshyApplication.onCreate` ровно один раз на процесс, и тихий no-op там
  прятал бы ошибку).

**Не воспроизведено на симуляторах.** Прогон портрет→ландшафт→портрет на
iPhone 17 и iPhone SE (2-го поколения), обе — iOS 26.5, до фикса даёт
пиксельно одинаковую шапку, то есть баг там не проявляется вовсе;
устройство из репорта — iPhone 8,4 на iOS 15.8.8. После фикса на тех же
симуляторах регрессии нет (шапка тоже пиксельно совпадает до и после
поворота), но подтвердить само исправление можно только на устройстве.

## `OTHER_LDFLAGS` — порядок фреймворков имеет значение

Build settings (Debug+Release): `OTHER_LDFLAGS = (-framework Shared,
-framework MapLibre)` — **`Shared` обязан идти первым**. В обратном порядке
ломается рендеринг текста Compose на iOS из-за конфликта символов HarfBuzz
(известная проблема самой библиотеки/JetBrains issue CMP-8882).

## `IPHONEOS_DEPLOYMENT_TARGET = 15.0`, не дефолт визарда 18.2

Проверено эмпирически подбором таргета: MapLibre требует только iOS 12.0,
`iOSApp.swift` (SwiftUI) — 14.0, но реальный потолок — сам `Shared.framework`
(Kotlin/Native + Compose Multiplatform из текущих версий) — линкер
предупреждает о несоответствии версий объектных файлов ниже 15.0.
15.0 расширяет поддержку с iPhone XS/XR+ до iPhone 6s/7/SE(2-го
поколения)+. Заголовки `Shared.framework` — plain Objective-C без
`@available`-аннотаций, так что скрытая зависимость от более нового API не
поймалась бы статически — единственная защита от неё — живой прогон на
реальном устройстве этой версии.

## IntelliJ не запускает на физических устройствах <iOS 16

Жёсткая проверка самого плагина Kotlin Multiplatform
(`Please update the device at least to version 16`), не связана с
`IPHONEOS_DEPLOYMENT_TARGET` проекта — Xcode этого ограничения не имеет.
На устройствах <iOS 16 запуск — только через Xcode; IntelliJ — для
редактирования кода и запуска на Android/симуляторе.
