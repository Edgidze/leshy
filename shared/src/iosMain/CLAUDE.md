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

## Экспорт/импорт (`DataLocationPicker.ios.kt`)

Нет диалога, совмещающего «выбрать место» и «записать байты», как Android'ов
`CreateDocument` — архив сначала целиком пишется во временный файл
(`NSTemporaryDirectory()`), и только потом показывается
`UIDocumentPickerViewController` в режиме `.ExportToService` (старый
однo-`URL`-инициализатор `initWithURL:inMode:`, тот же принцип, что и у
импорт-диалога в этом файле — простой, дольше поддерживаемый API вместо
iOS-14+ `forExporting:`/`UTType`), который копирует уже готовый файл туда,
куда укажет пользователь — без security-scoped bookmarks, т.к. место
выбирается уже после того, как файл существует. `BufferedSink` не реализует
`kotlin.AutoCloseable` на Kotlin/Native (в отличие от JVM, где Okio
`Closeable` — это `java.io.Closeable`) — `kotlin.io.use` для него здесь не
резолвится, закрывать вручную через `try`/`finally { sink.close() }`.

Именованный параметр конструктора `UIDocumentPickerViewController` для
`NSURL` — `uRL`, не `url` (K/N капитализирует `URL` в camelCase как `uRL`).

## Фото/превьюшки хранятся абсолютным путём — контейнер песочницы может смениться

`saveImage` (`CameraLauncher.ios.kt`), `IosPhotoStorage`,
`IosWalkThumbnailRenderer` резолвят `NSDocumentDirectory` через
`NSFileManager` в момент сохранения и кладут **абсолютный** путь (с UUID
контейнера) в `ObjectEntity.photoPath`/`WalkEntity.thumbnailPath`. При
некоторых событиях (обновление приложения через App Store/TestFlight,
восстановление) iOS может пересоздать UUID контейнера — сам `Documents`
физически переносится вместе с содержимым, но все уже сохранённые
абсолютные пути со старым UUID мгновенно становятся нерабочими; новые фото
пишутся уже с текущим UUID и работают.

Полноценный фикс (переход на относительные пути) не делали — слишком
инвазивно ради разового события. Вместо этого — самовосстановление:
`repairStalePhotoPath` (`PhotoPathRepair.ios.kt`) по «висящему» пути ищет
сегмент после `/Documents/` (стабилен при переносе контейнера) и
пересобирает его относительно ТЕКУЩЕГО `NSDocumentDirectory`, проверяя, что
файл действительно там лежит. `RepairPhotoPathsUseCase` гоняет это разово
по всем `photoPath`/`thumbnailPath` при каждом холодном запуске приложения
(тот же идемпотентный паттерн, что `BackfillWalkThumbnailsUseCase`) и
перезаписывает исправленный путь в БД. `AndroidPhotoStorage`-эквивалент
(`repairStalePhotoPath` на Android) — no-op: `Context.filesDir` так не
переезжает.

**Вызывается из `App.kt` (корень композиции), не из `ArchiveViewModel`/
`MapViewModel`.** Раньше сидел в `init` этих двух ViewModel'ей — баг живьём:
`RecordViewModel` (экран «Запись», где `LiveTrackMap` тоже рисует фото на
исторических маркерах мест) этот use case не вызывал вообще, а стартовый
экран приложения — `Home`, не `Record` (`ui/navigation/LeshyNavHost.kt`), так
что пользователь легко попадал на «Запись», ни разу не зайдя на Архив/Карту
— пути там оставались непочиненными. Централизация в `App.kt`
(`LaunchedEffect(Unit)`, композится ровно раз за процесс) чинит это для
любого экрана независимо от того, куда пользователь пойдёт первым, и заодно
убирает дублирующийся проход по таблице находок с трёх мест до одного.

## Галерея и картинки (`GalleryPicker.ios.kt`, `ImageCodec.ios.kt`)

- **Пикер галереи — `PHPickerViewController`** (не `UIImagePickerController`,
  которым осталась камера): работает вне процесса приложения, поэтому не
  требует ни разрешения на фотобиблиотеку, ни `NSPhotoLibraryUsageDescription`
  в `Info.plist`. Делегат держится `remember`-ом (композицией) — то же, что
  «`var` на классе» из раздела про ARC выше; локального `val` в лямбде хватило
  бы ровно до первого освобождения.
- **`NSItemProvider.canLoadObjectOfClass`/`loadObjectOfClass` из K/N
  недоступны по типам:** в биндинге параметр — `NSItemProviderReadingProtocol`
  (инстанс), а не класс-объект, и `UIImage.Companion` туда не подходит
  (`Argument type mismatch`). Рабочий путь —
  `loadFileRepresentationForTypeIdentifier("public.image") { url, _ -> }`
  (параметр — обычная строка). **URL временный: система удаляет файл сразу
  после возврата из хендлера**, копировать нужно прямо в нём
  (`NSFileManager.copyItemAtPath`). Побочная польза — оригинальный файл (часто
  HEIC) не декодируется в память ради того, чтобы быть тут же перекодированным.
- Колбэк `NSItemProvider` приходит на приватной очереди — прыжок на главный
  поток (`dispatch_async(dispatch_get_main_queue())`) обязателен, дальше идёт
  Compose-состояние.
- **Декодирование — через UIKit (`UIImage.drawInRect` в
  `UIGraphicsBeginImageContextWithOptions`), а не Skia напрямую.** Skia
  `Image.makeFromEncoded` игнорирует EXIF-ориентацию (фото с телефона почти
  всегда лежит на боку) и не умеет HEIC; `UIImage` знает и то, и другое и
  применяет ориентацию при отрисовке. Итог доезжает до `ImageBitmap` одним
  PNG-round-trip'ом — цена за то, чтобы не мостить `CGImage`→Skia вручную.
  `UIImage.imageWithContentsOfFile(path)` — **класс-метод, не конструктор
  `UIImage(contentsOfFile = ...)`**: K/N типизирует конструктор как non-null,
  хотя тот реально возвращает nil на недекодируемом файле.
- `encodePng` — Skia (`Image.makeFromBitmap(bitmap.asSkiaBitmap())
  .encodeToData(PNG)`), она же бэкенд `ImageBitmap` на этой платформе.

## Прочее

- Камера — `UIImagePickerController` + сохранение в Documents
  (`saveImageToDocuments`, общий с пикером галереи).
- `MLNMapSnapshotter` для миниатюр прогулок — в отличие от Android сам
  помещает атрибуцию на маленьком снапшоте (см. `ui/map/CLAUDE.md`).
- `IPHONEOS_DEPLOYMENT_TARGET`/линковка `Shared.framework`+`MapLibre` —
  см. `iosApp/CLAUDE.md`.
