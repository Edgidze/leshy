# iosMain/ — iOS-специфичные `actual`

## ARC-баг (главные грабли): делегат `CLLocationManager` — только `var` на классе

`IosLocationTracker.track()` строился как `callbackFlow` с ЛОКАЛЬНЫМИ
`val manager`/`val delegate` внутри билдера. `CLLocationManager.delegate` —
`weak`-свойство (Objective-C); `delegate` нигде не использовался ПОСЛЕ
`manager.delegate = delegate`, и Kotlin/Native не обязан держать сильную
ссылку на локальную переменную дольше её последнего использования — ARC
освобождал объект-делегат сразу после первого колбэка. Симптом: GPS
обновлялся ровно один раз, все находки за прогулку падали в одну точку.
(Маскировался ограничением `simctl location` на симуляторе, из-за чего
нашёлся только на реальном iPhone.)

**Фикс: `manager` и `delegate` — `private var`-поля самого класса**
(Koin-синглтон, живёт весь сеанс приложения), не локальные переменные
корутин-билдера. Тот же паттерн применим к любому будущему `callbackFlow`
на iOS, оборачивающему delegate-based Objective-C API.

`AndroidLocationTracker` этому классу багов не подвержен — там listener и
так захватывается внутри `awaitClose`-замыкания.

## Фоновая геолокация

`allowsBackgroundLocationUpdates`/`pausesLocationUpdatesAutomatically`
должны включаться/выключаться СИНХРОННО с реальным стартом/стопом записи
(`IosLocationTracker.setBackgroundUpdatesEnabled(Boolean)`, вызывается из
`IosBackgroundRecordingController.start()/stop()`), не один раз безусловно
при первой подписке — иначе приложение продолжает использовать фоновую
геолокацию (с системным индикатором) даже когда прогулка не пишется.
Требует `UIBackgroundModes: [location]` в `Info.plist` — без записи
включение флага сразу кидает `NSInvalidArgumentException`.

`LocationTracker` и `BackgroundRecordingController` в DI должны резолвиться
на ОДИН экземпляр `IosLocationTracker`, иначе у каждого будет свой
независимый `CLLocationManager`.

`manager.location` (кэшированное свойство) эмитится сразу при подписке —
тот же смысл, что `getLastKnownLocation` на Android.

## `simctl location` — ограничение инструмента, не приложения

Доставляет доступному процессу только ОДИН кэшированный фикс на старте;
`simctl location set/start`, вызванный после запуска, надёжно доставляет
новые точки только если ARC-баг выше уже исправлен. Для честной непрерывной
симуляции маршрута Apple рассчитывает на GPX через Xcode Scheme
(`Edit Scheme → Run → Options → Core Location`), не на постфактумный
`simctl` к процессу, запущенному через `xcodebuild`/`simctl launch`.

## Прочее

- Камера — `UIImagePickerController` + сохранение в Documents.
- `MLNMapSnapshotter` для миниатюр прогулок — в отличие от Android сам
  помещает атрибуцию на маленьком снапшоте (см. `ui/map/CLAUDE.md`).
- `IPHONEOS_DEPLOYMENT_TARGET`/линковка `Shared.framework`+`MapLibre` —
  см. `iosApp/CLAUDE.md`.
