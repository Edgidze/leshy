# План: снять наблюдателя `pauseFileSource:` у MapLibre на iOS

Статус: **не начато.** Одна фаза, одна сессия. Требует физического iPhone для
проверки — на симуляторе сценарий не воспроизводится. Когда сделано и
проверено вживую — файл удалить, итог записать в `ui/map/CLAUDE.md` и в
`.claude/investigations/ios-maplibre-background-watchdog/README.md`.

Это «шаг 2» плана расследования. Полная фактура — там же, в README
(разделы 3, 5, 6, «Шаг 2»); здесь ровно то, что нужно, чтобы выполнить шаг с
нуля, не перечитывая 900 строк.

## Зачем

`+[MLNOfflineStorage sharedOfflineStorage]` внутри своего `dispatch_once`
подписывается на `UIApplicationDidEnterBackgroundNotification`. По этой
нотификации он синхронно зовёт `pauseFileSource:` →
`OnlineFileSource::pause()` / `DatabaseFileSource::pause()` →
`mbgl::util::Thread<T>::pause()`, а тот заканчивается на

```cpp
pausing.get();   // std::future<void>::get() на ГЛАВНОМ потоке. Без таймаута.
```

Таймаута нет, отменить нельзя, приоритет не донорится (обычный
`std::condition_variable`). Если поток-адресат в этот момент не получает CPU,
главный поток стоит, `applicationDidEnterBackground` не возвращается, и
FRONTBOARD-watchdog убивает приложение `SIGKILL 0x8BADF00D` — это инцидент №1
расследования, 2026-08-17.

Подписка не наша и не опциональна: `MLNMapView` при инициализации дёргает
`MLNOfflineStorage.sharedOfflineStorage.databasePath` (`MLNMapView.mm:718`),
так что наблюдателя получает любое приложение, показавшее карту, даже без
офлайн-регионов. Апстрим-issue — maplibre/maplibre-native#2328; дедлок
предсказали ещё в mapbox/mapbox-gl-native#8125 (2017) и всё равно смёржили.

**Селектор `pauseFileSource:` приватный**, в публичных заголовках
`MapLibre.framework` его нет — поэтому подменить/обернуть сам вызов снаружи
нельзя. Единственный рычаг из нашего кода — снять подписку.

## Что сделать

Один новый `single(createdAtStart = true)` в
`shared/src/iosMain/kotlin/leshy/mushrooms/map/di/PlatformModule.ios.kt`,
рядом с уже существующим `IosPinnedStyleInterceptor` (у него та же
особенность: должен отработать до первого `MLNMapView`). Смысл кода:

```kotlin
// Сначала ОБРАТИТЬСЯ к синглтону: наблюдателя вешает его собственный
// dispatch_once, до первого обращения снимать нечего.
val storage = MLNOfflineStorage.sharedOfflineStorage
NSNotificationCenter.defaultCenter.removeObserver(
    observer = storage,
    name = UIApplicationDidEnterBackgroundNotification,
    `object` = null,
)
```

Проверено при написании плана: `sharedOfflineStorage` объявлен в
`MLNOfflineStorage.h` как `@property (class, nonatomic, readonly)`, то есть в
Kotlin доступен как `MLNOfflineStorage.sharedOfflineStorage` (импорт —
`import MapLibre.MLNOfflineStorage`, ровно как `MLNNetworkConfiguration` в
`IosPinnedStyleInterceptor.kt`).

Парный `unpauseFileSource:` на `UIApplicationWillEnterForegroundNotification`
трогать **не надо** — он сам выходит по `if (!self.isPaused) return`.

Порядок регистрации в Koin относительно `IosPinnedStyleInterceptor`
безразличен (разные подсистемы), но оба обязаны быть `createdAtStart = true`.

## Цена и за чем следить

Пауза вводилась не просто так: чтобы MapLibre не лез в SQLite и сеть, пока
приложение работает в фоне на заблокированном устройстве — при Data
Protection файл БД может быть недоступен, отсюда исторические
«SQLite disk I/O error».

У нас риск не теоретический: в `Info.plist` есть `UIBackgroundModes:
location`, и на активной записи включается `allowsBackgroundLocationUpdates`,
то есть процесс в фоне живой и карта продолжает существовать.

Смягчает то, что по умолчанию файлы контейнера идут с
`NSFileProtectionCompleteUntilFirstUserAuthentication` — после первой
разблокировки с момента загрузки телефона БД доступна и на заблокированном
экране. Практическая проверка: во время долгих прогулок с заблокированным
экраном смотреть `idevicesyslog` на предмет `disk I/O error`.

Если ошибки появятся — не откатывать вслепую, а сузить: снимать наблюдателя
только на время активной записи трека (когда фон нам действительно нужен) и
возвращать его на `finish()`.

## Как проверять (делает пользователь, на устройстве)

1. Экран «Запись», карта на экране, **активная запись трека**.
2. Свернуть и развернуть приложение **20 раз подряд**.

До фикса это ровно тот сценарий, который трижды убил приложение или телефон.
Успех — 20 циклов без вылета и без зависания.

Дополнительно, пассивно: после дня обычного пользования стянуть репорты и
убедиться, что нет новых `.ips` по `leshy`:

```bash
idevicecrashreport -k <папка>     # -k = копировать, НЕ удаляя с телефона
```

## Чего этот шаг НЕ даёт

- **Не влияет на торможение.** Расход CPU/пробуждений — другая ветка (шаг 3
  расследования; неинвазивная часть 3.2 сделана 2026-09-02, см. README).
- **Не спасает от инцидентов №2 и №3** — тех, что закончились перезагрузкой
  телефона: там приложение вообще не уходило в фон (`flags: ["foreground"]`),
  системные демоны голодали от самой загрузки CPU.

То есть это снятие острого риска, а не первопричины.

## Откат

Одна правка в одном файле, зависимостей нет — удалить добавленный `single`.
Отката данных/миграций не требуется.

## Готово, когда

- [ ] `single` добавлен в `PlatformModule.ios.kt`, `:shared:compileKotlinIosArm64` зелёный
- [ ] 20 циклов сворачивания на устройстве при активной записи — без вылета
- [ ] сутки обычного пользования без `disk I/O error` в syslog
- [ ] итог записан в `ui/map/CLAUDE.md`, шаг 2 в README расследования отмечен сделанным
- [ ] (опционально) дописано в maplibre/maplibre-native#2328 — см. «Шаг 5» README
