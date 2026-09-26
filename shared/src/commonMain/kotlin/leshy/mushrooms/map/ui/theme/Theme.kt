package leshy.mushrooms.map.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.NavigationDrawerItemColors
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.domain.model.Edition

private val LeshyGreen = Color(0xFF1B4332)

// Earthy forest palette anchored on the logo green (UI_REVIEW.md #5) — replaces
// the Material3 baseline lavender/purple that unspecified lightColorScheme()/
// darkColorScheme() params fall back to.
private val LightColors = lightColorScheme(
    primary = LeshyGreen,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFA8D5BA),
    onPrimaryContainer = Color(0xFF06261A),
    inversePrimary = Color(0xFF8DCFA9),
    secondary = Color(0xFF6F4E37),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEAD9C8),
    onSecondaryContainer = Color(0xFF2B1B0F),
    tertiary = Color(0xFF6B7A3F),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFDCE6C0),
    onTertiaryContainer = Color(0xFF263300),
    background = Color(0xFFF4F1E8),
    onBackground = Color(0xFF201C15),
    surface = Color(0xFFF4F1E8),
    onSurface = Color(0xFF201C15),
    surfaceVariant = Color(0xFFE3DCC8),
    onSurfaceVariant = Color(0xFF4C4739),
    surfaceTint = LeshyGreen,
    inverseSurface = Color(0xFF34302A),
    inverseOnSurface = Color(0xFFF5F0E7),
    outline = Color(0xFF7C7666),
    outlineVariant = Color(0xFFCDC6B4),
    scrim = Color(0xFF000000),
    surfaceBright = Color(0xFFF4F1E8),
    surfaceDim = Color(0xFFD8D3C4),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFEEEADD),
    surfaceContainer = Color(0xFFE8E3D4),
    surfaceContainerHigh = Color(0xFFE2DDCE),
    surfaceContainerHighest = Color(0xFFDCD7C8),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8DCFA9),
    onPrimary = Color(0xFF08361F),
    primaryContainer = LeshyGreen,
    onPrimaryContainer = Color(0xFFA8D5BA),
    inversePrimary = LeshyGreen,
    secondary = Color(0xFFD8C0A8),
    onSecondary = Color(0xFF3E2B1A),
    secondaryContainer = Color(0xFF56402C),
    onSecondaryContainer = Color(0xFFEAD9C8),
    tertiary = Color(0xFFC0CC9E),
    onTertiary = Color(0xFF2C3300),
    tertiaryContainer = Color(0xFF414A20),
    onTertiaryContainer = Color(0xFFDCE6C0),
    background = Color(0xFF1C1B15),
    onBackground = Color(0xFFE6E1D3),
    surface = Color(0xFF1C1B15),
    onSurface = Color(0xFFE6E1D3),
    surfaceVariant = Color(0xFF4C4739),
    onSurfaceVariant = Color(0xFFCDC6B4),
    surfaceTint = Color(0xFF8DCFA9),
    inverseSurface = Color(0xFFE6E1D3),
    inverseOnSurface = Color(0xFF34302A),
    outline = Color(0xFF96907E),
    outlineVariant = Color(0xFF4C4739),
    scrim = Color(0xFF000000),
    surfaceBright = Color(0xFF43413A),
    surfaceDim = Color(0xFF1C1B15),
    surfaceContainerLowest = Color(0xFF16150F),
    surfaceContainerLow = Color(0xFF201F19),
    surfaceContainer = Color(0xFF24231C),
    surfaceContainerHigh = Color(0xFF2F2E26),
    surfaceContainerHighest = Color(0xFF3A3831),
)

/**
 * **`Surface` внутри темы — не оформление, а единственное, что делает тёмную тему работающей на
 * экране без собственного скаффолда.** Material3 держит `LocalContentColor` с дефолтом
 * `Color.Black`, и цвет темы в него кладёт ближайший `Surface`/`Scaffold` — больше никто.
 * Экран без того и другого (шаг «выбор подборок» онбординга — голая `Column`) рисовал текст
 * буквальным чёрным и не красил фон вовсе, то есть показывал фон хост-контроллера: на iOS в
 * тёмном режиме — чёрный. Чёрное по чёрному, репорт тестировщиц 2026-09-17. Явно заданные цвета
 * при этом работали (баннер `onErrorContainer`, подпись `TextField`) — отсюда и след, по
 * которому нашлась причина.
 *
 * На Android дефект не проявлялся: окно `Activity` красится темой из манифеста, и чёрный текст на
 * ней читался. Разбираться с этим здесь, а не заплаткой на экран, — чтобы следующий экран без
 * скаффолда не воспроизвёл то же самое заново.
 */
@Composable
fun LeshyTheme(edition: Edition, useDarkTheme: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = colorSchemeFor(edition, useDarkTheme),
        shapes = shapesFor(edition),
    ) {
        CompositionLocalProvider(
            LocalLeshyTokens provides leshyTokensFor(edition, useDarkTheme),
            LocalEdition provides edition,
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
                content = content,
            )
        }
    }
}

/**
 * Доступ к набору величин редакции из любого места композиции — `LeshyTheme.tokens.shapeDialog`.
 *
 * Функция и объект с одним именем сосуществуют штатно (классификаторы и функции в Kotlin живут в
 * разных пространствах имён); это тот же приём, которым сделан `MaterialTheme`, и здесь он взят
 * ради того, чтобы обращение к своим величинам читалось рядом с `MaterialTheme.colorScheme` как
 * такая же часть темы, а не как обращение к чужому синглтону.
 */
/**
 * Палитра редакции. Вторая схема лежит отдельным файлом (`RussiaColors.kt`), а не ветвлением
 * внутри него, — условие мержабельности из раздела 5 `.claude/plans/russia-edition.md`.
 */
private fun colorSchemeFor(edition: Edition, useDarkTheme: Boolean) = when (edition) {
    Edition.WORLD -> if (useDarkTheme) DarkColors else LightColors
    Edition.RUSSIA -> russiaColorScheme(useDarkTheme)
}

/**
 * Формы компонентов Material — диалогов, карточек, полей ввода, меню.
 *
 * Дублирование `LeshyTokens`? Нет: токены — это формы, которые задаёт НАШ код, а сюда смотрят
 * компоненты Material, до чьих скруглений наш код не дотягивается вовсе (`AlertDialog` берёт
 * `extraLarge`, `Card` — `medium`, `TextField` и `DropdownMenu` — `extraSmall`). Без этой подмены
 * российская редакция получила бы квадратные свои элементы вперемешку с закруглёнными чужими.
 *
 * Кнопки сюда НЕ входят и не могут: `ButtonDefaults.shape` приходит не из `MaterialTheme.shapes`,
 * а из жёстко зашитого `CornerFull`. Их форму несёт токен `shapeButton`.
 *
 * У мировой редакции — `Shapes()`, то есть ровно дефолты Material: 4/8/12/16/28. Передать их явно
 * и не передавать вовсе — одно и то же, `LeshyTheme` здесь корневая тема.
 */
private fun shapesFor(edition: Edition): Shapes = when (edition) {
    Edition.WORLD -> Shapes()
    Edition.RUSSIA -> Shapes(
        extraSmall = RoundedCornerShape(2.dp),
        small = RoundedCornerShape(3.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(4.dp),
        extraLarge = RoundedCornerShape(6.dp),
    )
}

/**
 * Редакция, доступная из любого места композиции.
 *
 * Производная от того единственного значения, что положил хост в Koin (`initKoin(edition)`), — не
 * второй источник правды, а способ не тащить `koinInject` в каждый файл, которому нужно выбрать
 * строку или цвет. Репозитории и снапшоттеры по-прежнему берут редакцию из Koin: композиции они
 * не видят.
 */
val LocalEdition = staticCompositionLocalOf<Edition> {
    error("LocalEdition не предоставлен — композиция должна быть внутри LeshyTheme")
}

/**
 * Цвета шапки экрана.
 *
 * У российской редакции шапка — это **земля**, а не мат: `background`, тот же охристый тон, что
 * под карточками и в выдвижном меню (`design.md`, раздел 3 — «Земля … фон экрана под карточками,
 * выдвижное меню, шапка»). Дефолт Material красит её в `surface`, то есть в почти белый мат, и на
 * охряном фоне это читалось как чужая белая плашка сверху экрана (репорт владельца 2026-09-26).
 *
 * `scrolledContainerColor` тот же: у земли нет причины менять тон под прокруткой — она не
 * поднимается над содержимым, содержимое лежит на ней.
 *
 * С появлением текстуры (`SectionScaffold`) оба цвета стали ПРОЗРАЧНЫМИ, а не охристыми: полотно
 * земли рисуется под всем экраном разом, и непрозрачная шапка того же тона резала бы по нему
 * границу — два куска дерева со своим рисунком встык. Прозрачность безопасна и без текстуры: под
 * шапкой тогда тот же `background`, что стоял тут раньше.
 *
 * Мировая редакция получает ровно `TopAppBarDefaults.topAppBarColors()`, то есть не меняется.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun leshyTopAppBarColors(): TopAppBarColors = when (LocalEdition.current) {
    Edition.WORLD -> TopAppBarDefaults.topAppBarColors()
    Edition.RUSSIA -> TopAppBarDefaults.topAppBarColors(
        containerColor = Color.Transparent,
        scrolledContainerColor = Color.Transparent,
    )
}

/**
 * Цвет полотна выдвижного меню.
 *
 * У российской редакции это земля — тот же охристый `background`, что под карточками и в шапке
 * (`design.md`, раздел 3). Мировая получает дефолт Material (`surfaceContainerLow`), и это НЕ то
 * же самое, что `background`: у мировой схемы они разных тонов, и подстановка `background`
 * «заодно» сдвинула бы мировое меню на полтона.
 */
@Composable
fun leshyDrawerContainerColor(): Color = when (LocalEdition.current) {
    Edition.WORLD -> DrawerDefaults.modalContainerColor
    Edition.RUSSIA -> MaterialTheme.colorScheme.background
}

/**
 * Цвета пункта выдвижного меню.
 *
 * **Репорт владельца 2026-09-26:** у последнего посещённого раздела вместо земли кремовый
 * прямоугольник. Так и есть: Material метит выбранный пункт заливкой `secondaryContainer`, а это
 * светлая плашка — поверх деревянного полотна она читается как приклеенная бумажка, причём
 * единственная на весь экран.
 *
 * Промежуточной версией было притенение самой доски полупрозрачным `onBackground`, но владелец
 * снял вопрос целиком: **пометки выбранного раздела в меню быть не должно вовсе.** Довод
 * продуктовый, и он верен: меню открывается ПОВЕРХ того самого раздела, который помечен, —
 * пользователь и так на него смотрит, а подсветка в списке из семи пунктов притягивает взгляд к
 * тому, куда идти не нужно.
 *
 * **Обеим редакциям, а не только российской** — решение владельца, отдельным коммитом: в мировом
 * приложении подсветка стоит ровно с тем же смыслом и так же не нужна. Поэтому здесь нет развилки
 * по редакции: цвета подписи берутся из схемы и различаются сами собой.
 */
@Composable
fun leshyDrawerItemColors(): NavigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
    selectedContainerColor = Color.Transparent,
    // Значок и подпись тоже перестают различаться: убрать заливку и оставить выбранному пункту
    // свой оттенок текста значило бы поменять заметную пометку на невнятную, а не убрать её.
    selectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    selectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
)

object LeshyTheme {
    val tokens: LeshyTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalLeshyTokens.current
}
