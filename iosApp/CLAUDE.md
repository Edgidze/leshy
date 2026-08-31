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
