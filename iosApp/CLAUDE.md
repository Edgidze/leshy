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

### Окно ОБЯЗАНО создаваться из `UIWindowScene` — от этого зависит GPS

Не только показ окна. `ComposeContainerLifecycleDelegate` (CMP,
`iosMain/androidx/compose/ui/window/`) выводит состояние Compose-жизненного
цикла ИСКЛЮЧИТЕЛЬНО из сцены:

```
isViewAppeared && isSceneInForeground && isSceneActive -> RESUMED
```

а `isSceneInForeground`/`isSceneActive` считают `SceneActiveStateListener`/
`SceneForegroundStateListener`, фильтруя нотификации по конкретному объекту
`UIWindowScene`, который приходит из `ComposeContainer.onDidMoveToWindow` как
`window.windowScene`.

Отсюда следствие, не видное по коду ни задачи 5, ни задачи 6: окно,
созданное как `UIWindow(frame:)` без сцены (ровно первая, сломанная версия
UIKit-хоста), дало бы `window.windowScene == null`, Compose никогда не дошёл
бы до `RESUMED`, и `LifecycleResumeEffect` на экране «Запись» —
единственное, что теперь включает подписку на GPS (задача 6) — не сработал
бы ни разу. То есть **на iOS геолокация вообще не завелась бы**, причём
молча. `UIWindow(windowScene:)` в `SceneDelegate` закрывает это по
построению, но при любой будущей правке хоста проверять надо именно GPS на
«Записи», а не только «нарисовалось ли что-нибудь».

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

## Диагностика зависаний: архив символов + MetricKit

Обе вещи существуют ради одного расследования —
`.claude/investigations/ios-maplibre-background-watchdog/README.md` (шаги 0 и 4).
Там же причина: из трёх инцидентов два оказались нечитаемыми, потому что
сборка была перезаписана, а crash-репорт самого приложения система записать
не успела.

### Фаза `Archive symbols for crash reports`

`iosApp/Scripts/archive-symbols.sh`, последняя фаза таргета. Кладёт Mach-O
каждой сборки в `~/Library/Developer/leshy-symbols/` (переопределяется
`LESHY_SYMBOL_ARCHIVE`), последние 10 сборок.

- **Только `PLATFORM_NAME = iphoneos`.** Симуляторная сборка на телефон не
  попадает, значит и в репорт с устройства тоже.
- **Копируются только бинари, не `.app` целиком** — для `atos`/`dwarfdump`
  ресурсы не нужны, а с ними архив был бы на порядок толще.
  `MapLibre.framework` включён намеренно: именно его UUID понадобился, чтобы
  расшифровать зависшие кадры (`otool -oV` → `imp` метода → `imageOffset` из
  репорта).
- **Ключевой бинарь в Debug — `leshy.debug.dylib`, а не `leshy`.** Xcode 16
  выносит весь код в debug-dylib, `leshy` остаётся тонким загрузчиком; UUID
  из репорта — от dylib. По нему же именуется папка архива.
- `.dSYM` копируются только в Release: в Debug `DEBUG_INFORMATION_FORMAT =
  dwarf`, отладочная информация лежит внутри самого бинаря.
- Фаза стоит ДО подписи кода, но это безразлично: `codesign` не трогает
  `LC_UUID`.
- Поиск сборки по UUID из `.ips`:
  `grep -ril <UUID> ~/Library/Developer/leshy-symbols/*/uuids.txt`
- **Порядок цены — ~85 МБ на сборку** (из них 69 МБ — `leshy.debug.dylib`: в
  него статически влинкован весь `Shared.framework`, отдельным фреймворком в
  бандле его нет). Десять сборок ≈ 850 МБ; если жалко — `KEEP` в начале
  скрипта.

### `DiagnosticsArchive` (MetricKit)

`iosApp/iosApp/DiagnosticsArchive.swift`, подписка ставится из
`AppDelegate.application(_:didFinishLaunchingWithOptions:)` — одна на процесс,
а не из `SceneDelegate` (сцен может быть несколько).

- **Файлы — в `Application Support/diagnostics/`**, а не в `Documents`:
  открывать `Documents` наружу через `UIFileSharingEnabled` пришлось бы
  вместе с данными пользователя. Забирать — Xcode → Devices and Simulators →
  выгрузка контейнера. Краткая сводка (длительность зависания, CPU) при этом
  дублируется в syslog, её видно сразу через `idevicesyslog` без выгрузки.
- **Доставку расписывает система: не чаще раза в сутки, обычно на следующем
  запуске после события.** Сразу после зависания файла не будет — это не
  поломка. Проверять код — Xcode → Debug → Simulate MetricKit Payloads (только
  на подключённом устройстве). На старте дополнительно вычитываются
  `pastDiagnosticPayloads`/`pastPayloads` — системный буфер за 7 суток.
- `MXMetricPayload` сохраняется наравне с диагностикой намеренно: в нём
  `cpuMetrics.cumulativeCPUTime` за сутки реального пользования — то самое
  измерение расхода CPU из шага 1 плана, но в поле, а не в Instruments.
