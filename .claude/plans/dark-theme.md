# Тёмная тема

## Контекст

Приложение сейчас жёстко светлое. `ui/theme/Theme.kt` уже определяет обе
цветовые схемы:

```kotlin
private val LightColors = lightColorScheme(primary = LeshyGreen, ...)
private val DarkColors = darkColorScheme(primary = Color(0xFF8DCFA9), ...)

@Composable
fun LeshyTheme(useDarkTheme: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (useDarkTheme) DarkColors else LightColors,
        content = content,
    )
}
```

но единственный вызов, `App.kt:97`, — просто `LeshyTheme { ... }`, без
аргумента, так что `useDarkTheme` всегда резолвится в дефолтный `false`.
`isSystemInDarkTheme()` нигде в репозитории не используется (проверено
грепом). `DarkColors` — фактически мёртвый код, кем-то заведённый заранее,
но никогда не подключённый.

Задача — довести это до конца: дать пользователю выбор Светлая/Тёмная/
Системная в Настройках (тем же паттерном, что уже есть для языка —
`SingleChoiceSegmentedButtonRow` + DataStore), подключить выбор к
`LeshyTheme`, и поправить цвет иконок статус-бара на обеих платформах —
без этого тёмный экран получит светлые (default) иконки статус-бара,
нечитаемые на тёмном фоне.

### Решения, подтверждённые пользователем

- **Карта остаётся всегда светлой.** `OPEN_FREE_MAP_STYLE_URL`
  (`ui/map/MapStyle.kt`) не меняется, никакого второго стиля не заводится.
  Стиль карты сейчас жёстко закеширован/pinned — `MapStyleCacheRepository`
  фризит первый успешно скачанный `style.json`, `PinnedStyleInterceptor`
  и офлайн-скачивание регионов (`OfflineRegionRepositoryImpl.
  downloadRegion`) завязаны на этот же конкретный URL (см. `ui/map/
  CLAUDE.md`). Добавление тёмного варианта стиля потребовало бы отдельного
  pinned-слота, отдельного файла кеша и отдельной записи в
  `PinnedStyleInterceptor` — по сути mini-версия уже намеченного (но не
  реализованного) многоисточникового рефакторинга из
  `.claude/plans/sparkling-sparking-pie.md`. Сознательно не делаем в рамках
  этой задачи — карта плавающая светлым прямоугольником на тёмном фоне
  приложения — обычный, привычный паттерн у многих карто-приложений.
- **Три варианта в Настройках**: Светлая / Тёмная / Системная. Тем же UI-
  паттерном, что уже используется для языка на этом самом экране
  (`SingleChoiceSegmentedButtonRow` + `SegmentedButton`, `SettingsScreen.
  kt:59-69`), не радиокнопками и не одним тумблером Свет/Тьма.

### Аудит существующих "хардкод"-цветов — ничего чинить не нужно

Перед тем как писать этот план, целенаправленно проверены все места, где в
`ui/components/` встречаются буквальные `Color(0x...)`/`Color.White`/
`Color.Black` в обход `MaterialTheme.colorScheme` (изначально казались
подозрительными для тёмной темы):

- `MushroomTile.kt` `EdibilityBadge` (белый кружок + красная точка) и
  `MushroomLabel` (чёрная обводка + белая заливка текста поверх фото
  гриба) — фиксированная контрастная графика поверх ФОТО, а не поверх фона
  экрана. Их задача — быть читаемыми на ЛЮБОЙ фотографии независимо от
  темы приложения; перекраска под тёмную тему сделала бы их менее
  читаемыми, не более.
- `IconEditorDialog.kt` `CHECKER_LIGHT`/`CHECKER_DARK` — стандартная
  шахматка-индикатор прозрачности (как в любом фоторедакторе), намеренно
  одна и та же независимо от темы приложения — это её единственная
  функция, обозначать альфа-канал, а не оформление экрана.
- `SpeciesFormDialog.kt` (thumb цветового пикера, ~строка 317) — белое
  кольцо вокруг выбранного цвета, чтобы сам цвет-превью было видно на фоне
  спектра слайдера; не элемент оформления экрана.
- `MushroomDonutChart.kt` (~строка 251) — белый разделитель между цветными
  дольками донат-чарта (сами дольки — пользовательские `colorHex` видов
  грибов), нужен для визуального разделения произвольных цветов друг от
  друга, а не как элемент фона экрана.

Все `Card`/`Surface` в этих же файлах уже используют цвет темы автоматически
(дефолт Material3 `colorScheme.surface`) — правок не требуют. Вывод: в этой
части приложения дополнительных изменений для тёмной темы не нужно вообще.

## Реализация

### Шаг 1 — `ThemeMode`, новая доменная модель

Новый файл `shared/src/commonMain/kotlin/compose/project/leshy/domain/model/ThemeMode.kt`:

```kotlin
package compose.project.leshy.domain.model

enum class ThemeMode { LIGHT, DARK, SYSTEM }
```

Для сравнения — соседняя модель `AppLanguage.kt` (та же директория):
```kotlin
enum class AppLanguage(val code: String, val displayName: String) {
    RU("ru", "Русский"),
    EN("en", "English"),
}
```
`ThemeMode` не получает `displayName`-поле, в отличие от `AppLanguage` —
названия языков не переводятся (показываются на своём же языке), а
"Светлая"/"Тёмная"/"Системная" должны переводиться, поэтому текст идёт
через `StringKey` (см. Шаг 4), как у `MushroomSortOrder`.

### Шаг 2 — `SettingsRepository` — интерфейс

`shared/src/commonMain/kotlin/compose/project/leshy/domain/repository/SettingsRepository.kt`
— добавить рядом с `observeMushroomSortOrder`/`setMushroomSortOrder`:

```kotlin
/** Пользовательский выбор оформления: явно светлая/тёмная, либо следовать системе устройства. */
fun observeThemeMode(): Flow<ThemeMode>
suspend fun setThemeMode(mode: ThemeMode)
```
(плюс `import compose.project.leshy.domain.model.ThemeMode`)

### Шаг 3 — `SettingsRepositoryImpl` — DataStore-реализация

`shared/src/commonMain/kotlin/compose/project/leshy/data/repository/SettingsRepositoryImpl.kt`.
Текущий паттерн для enum-настройки (`MushroomSortOrder`):

```kotlin
private val MUSHROOM_SORT_ORDER_KEY = stringPreferencesKey("mushroom_sort_order")
...
override fun observeMushroomSortOrder(): Flow<MushroomSortOrder> = dataStore.data.map { prefs ->
    prefs[MUSHROOM_SORT_ORDER_KEY]?.let { name -> MushroomSortOrder.entries.find { it.name == name } }
        ?: MushroomSortOrder.ALPHABETICAL
}

override suspend fun setMushroomSortOrder(sortOrder: MushroomSortOrder) {
    dataStore.edit { prefs -> prefs[MUSHROOM_SORT_ORDER_KEY] = sortOrder.name }
}
```

Добавить один в один:

```kotlin
private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
...
override fun observeThemeMode(): Flow<ThemeMode> = dataStore.data.map { prefs ->
    prefs[THEME_MODE_KEY]?.let { name -> ThemeMode.entries.find { it.name == name } } ?: ThemeMode.SYSTEM
}

override suspend fun setThemeMode(mode: ThemeMode) {
    dataStore.edit { prefs -> prefs[THEME_MODE_KEY] = mode.name }
}
```

Фолбэк — `ThemeMode.SYSTEM` (не `LIGHT`): естественный дефолт для новой
настройки — следовать системному оформлению устройства, пока пользователь
явно не выберет иначе (сравнимо с тем, что большинство современных
приложений делают по умолчанию).

Никакой новой DI-регистрации не требуется — `SettingsRepositoryImpl`
переиспользует тот же `DataStore<Preferences>` singleton
(`di/PlatformModule.kt` + `di/DataModule.kt`), что и остальные настройки;
новая настройка = новый ключ, без схемы/миграции (см. `data/CLAUDE.md`,
раздел "Настройки — DataStore").

### Шаг 4 — Локализация: `i18n/StringKey.kt` + `i18n/Strings.kt`

`StringKey.kt` — добавить рядом с `SettingsLanguageTitle` (строка ~13):
```kotlin
SettingsThemeTitle,
SettingsThemeLight,
SettingsThemeDark,
SettingsThemeSystem,
```

`Strings.kt` — заполнить ОБА `when`-блока (RU-секция ~строка 90, EN-секция
~строка 378). Компилятор проверяет исчерпываемость обоих `when` — `else ->`
не добавлять, иначе новый недостающий перевод пройдёт молча.

RU (рядом со `StringKey.SettingsLanguageTitle -> "Язык"`):
```kotlin
StringKey.SettingsThemeTitle -> "Тема"
StringKey.SettingsThemeLight -> "Светлая"
StringKey.SettingsThemeDark -> "Тёмная"
StringKey.SettingsThemeSystem -> "Системная"
```

EN (рядом со `StringKey.SettingsLanguageTitle -> "Language"`):
```kotlin
StringKey.SettingsThemeTitle -> "Theme"
StringKey.SettingsThemeLight -> "Light"
StringKey.SettingsThemeDark -> "Dark"
StringKey.SettingsThemeSystem -> "System"
```

### Шаг 5 — `SettingsUiState` / `SettingsViewModel`

`presentation/settings/SettingsUiState.kt` — добавить поле:
```kotlin
val themeMode: ThemeMode = ThemeMode.SYSTEM,
```
(плюс `import compose.project.leshy.domain.model.ThemeMode`)

`presentation/settings/SettingsViewModel.kt`:
- В `init`, рядом с существующей подпиской на язык:
  ```kotlin
  viewModelScope.launch {
      settingsRepository.observeThemeMode().collect { mode ->
          _uiState.update { it.copy(themeMode = mode) }
      }
  }
  ```
- Новый публичный метод рядом с `setLanguage`:
  ```kotlin
  fun setThemeMode(mode: ThemeMode) {
      viewModelScope.launch { settingsRepository.setThemeMode(mode) }
  }
  ```

### Шаг 6 — `SettingsScreen.kt` — UI выбора темы

Текущий блок языка (`SettingsScreen.kt:58-69`) — образец для копирования:
```kotlin
SettingsSectionTitle(stringResource(StringKey.SettingsLanguageTitle))
SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
    AppLanguage.entries.forEachIndexed { index, language ->
        SegmentedButton(
            selected = uiState.language == language,
            onClick = { viewModel.setLanguage(language) },
            shape = SegmentedButtonDefaults.itemShape(index = index, count = AppLanguage.entries.size),
        ) {
            Text(language.displayName)
        }
    }
}
```

Новая секция — сразу после неё (перед блоком `SettingsMushroomSizeTitle`):
```kotlin
SettingsSectionTitle(stringResource(StringKey.SettingsThemeTitle))
SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
    ThemeMode.entries.forEachIndexed { index, mode ->
        SegmentedButton(
            selected = uiState.themeMode == mode,
            onClick = { viewModel.setThemeMode(mode) },
            shape = SegmentedButtonDefaults.itemShape(index = index, count = ThemeMode.entries.size),
        ) {
            Text(
                stringResource(
                    when (mode) {
                        ThemeMode.LIGHT -> StringKey.SettingsThemeLight
                        ThemeMode.DARK -> StringKey.SettingsThemeDark
                        ThemeMode.SYSTEM -> StringKey.SettingsThemeSystem
                    },
                ),
            )
        }
    }
}
```
(плюс `import compose.project.leshy.domain.model.ThemeMode`)

### Шаг 7 — Статус-бар: новый `expect`/`actual` (правило 6 CLAUDE.md — нативный
   код только когда единого кроссплатформенного решения нет)

Ни на Android, ни на iOS сейчас нет НИКАКОЙ обработки статус-бара —
сейчас это незаметно (фон всегда светлый, дефолтные тёмные иконки на нём
читаемы), но с появлением тёмной темы дефолтные иконки станут невидимыми
на тёмном фоне.

Новый файл (commonMain), `shared/src/commonMain/kotlin/compose/project/leshy/ui/theme/StatusBarAppearance.kt`:
```kotlin
package compose.project.leshy.ui.theme

import androidx.compose.runtime.Composable

@Composable
expect fun ApplyStatusBarAppearance(darkTheme: Boolean)
```

**Android actual** — `shared/src/androidMain/kotlin/compose/project/leshy/ui/theme/StatusBarAppearance.android.kt`:
```kotlin
package compose.project.leshy.ui.theme

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
actual fun ApplyStatusBarAppearance(darkTheme: Boolean) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
}
```
Сейчас `MainActivity.kt` только зовёт `enableEdgeToEdge()` один раз в
`onCreate()`, до `setContent` — никакой реактивности на смену темы в
рантайме нет вообще. Этот `actual` — единственное место, добавляющее её,
через `LocalView`/`SideEffect`, стандартный Compose-паттерн для side-effect
на реальный Android `Window`.

**iOS actual** — `shared/src/iosMain/kotlin/compose/project/leshy/ui/theme/StatusBarAppearance.ios.kt`:
```kotlin
package compose.project.leshy.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import platform.UIKit.UIApplication
import platform.UIKit.UIUserInterfaceStyle
import platform.UIKit.UIWindow

@Composable
actual fun ApplyStatusBarAppearance(darkTheme: Boolean) {
    LaunchedEffect(darkTheme) {
        (UIApplication.sharedApplication.windows.firstOrNull() as? UIWindow)?.overrideUserInterfaceStyle =
            if (darkTheme) UIUserInterfaceStyle.UIUserInterfaceStyleDark
            else UIUserInterfaceStyle.UIUserInterfaceStyleLight
    }
}
```
Кроссплатформенного Compose API на управление `UIStatusBarStyle` нет;
`MainViewController.kt` (`ComposeUIViewController { App() }`) не даёт
прямого доступа к `preferredStatusBarStyle` контроллера без правки
Swift-файлов. Самый простой путь без касания `iosApp/` вообще —
`overrideUserInterfaceStyle` на текущем `UIWindow`
(`UIApplication.sharedApplication.windows.firstOrNull()` — приложение
однооконное) — при этом системный статус-бар и вообще все системные
UIKit-элементы автоматически меняют цвет под нужный вид, без ручного
`setNeedsStatusBarAppearanceUpdate()`/сабклассинга контроллера.

### Шаг 8 — `App.kt` — подключение к `LeshyTheme`

Рядом с уже читаемыми `language`/`mushroomMarkerSizeScale` (`App.kt:81-83`):
```kotlin
val settingsRepository = koinInject<SettingsRepository>()
val language by settingsRepository.observeLanguage().collectAsState(initial = AppLanguage.EN)
val mushroomMarkerSizeScale by settingsRepository.observeMushroomMarkerSizeScale()
    .collectAsState(initial = MUSHROOM_MARKER_SIZE_SCALE_DEFAULT)
```
добавить:
```kotlin
val themeMode by settingsRepository.observeThemeMode().collectAsState(initial = ThemeMode.SYSTEM)
val useDarkTheme = when (themeMode) {
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
}
```
`isSystemInDarkTheme()` — `androidx.compose.foundation`, доступен из
`commonMain` без `expect`/`actual` (уже кроссплатформенный).

Строка 97, `LeshyTheme { ... }`, становится:
```kotlin
LeshyTheme(useDarkTheme = useDarkTheme) {
    ApplyStatusBarAppearance(useDarkTheme)
    when (onboardingCompleted) {
        ...
```
`ApplyStatusBarAppearance` вызывается ДО `when (onboardingCompleted)`, а не
внутри веток — так статус-бар подхватывает тему даже на экране
онбординга (`OnboardingScreen`), а не только после того, как онбординг уже
пройден.

Новые импорты в `App.kt`: `androidx.compose.foundation.isSystemInDarkTheme`,
`compose.project.leshy.domain.model.ThemeMode`,
`compose.project.leshy.ui.theme.ApplyStatusBarAppearance`.

### Известный некритичный пробел — не чинить в рамках этой задачи

`androidApp/src/main/AndroidManifest.xml`:
```xml
android:theme="@android:style/Theme.Material.Light.NoActionBar"
```
— нативная тема Activity, определяет цвет фона окна на долю секунды до
первой отрисовки Compose (в проекте вообще нет сплэш-экрана). При тёмной
теме это даст короткую светлую вспышку при холодном старте. Эффект не
специфичен для этой задачи (такой же был бы у любого КМП-приложения без
сплэша) — чинить отдельно через `androidx.core:core-splashscreen`, если
станет заметной проблемой на практике.

## Порядок изменений (файлы)

1. `domain/model/ThemeMode.kt` — новый файл
2. `domain/repository/SettingsRepository.kt` — новые методы интерфейса
3. `data/repository/SettingsRepositoryImpl.kt` — новый ключ + реализация
4. `i18n/StringKey.kt`, `i18n/Strings.kt` — 4 новых ключа, RU+EN
5. `presentation/settings/SettingsUiState.kt` — новое поле
6. `presentation/settings/SettingsViewModel.kt` — подписка + сеттер
7. `ui/screens/SettingsScreen.kt` — новая секция UI
8. `ui/theme/StatusBarAppearance.kt` (commonMain expect) — новый файл
9. `ui/theme/StatusBarAppearance.android.kt` — новый файл
10. `ui/theme/StatusBarAppearance.ios.kt` — новый файл
11. `App.kt` — подключение `useDarkTheme` к `LeshyTheme` + вызов `ApplyStatusBarAppearance`

## Проверка

- `./gradlew :shared:compileAndroidMain` и
  `:shared:compileKotlinIosSimulatorArm64` — оба платформенных `actual`
  компилируются (полный `./gradlew build` не нужен для этой проверки, см.
  корневой `CLAUDE.md` про OOM).
- **Android**: `./gradlew :androidApp:assembleDebug`, установить на
  устройство/эмулятор. В Настройках переключить Светлая → Тёмная →
  Системная:
  - весь UI (включая AlertDialog'и, фон Card'ов, сам экран Настроек)
    перекрашивается;
  - статус-бар остаётся читаемым в обоих режимах;
  - карта (экраны Запись/Карта/Подготовка) остаётся светлой в любом
    режиме темы приложения;
  - `Системная` — переключить тёмный режим устройства в Быстрых настройках
    без перезапуска приложения, UI должен среагировать сразу.
- **iOS**: запуск на симуляторе (Xcode или IntelliJ) — та же проверка
  переключателя; для `Системная` — Settings → Developer →
  Dark Appearance на симуляторе, или Пункт управления на реальном
  устройстве.
- Перезапустить приложение после выбора темы — значение должно быть
  восстановлено из DataStore (проверка персистентности).
- Убедиться, что выбор языка (соседняя секция) по-прежнему работает
  независимо — обе настройки используют общий `DataStore` singleton, но
  разные ключи.
