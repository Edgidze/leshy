package klev.fishing.map.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Phishing
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import klev.fishing.map.i18n.FishStringKey
import klev.fishing.map.i18n.fishStringResource
import klev.fishing.map.ui.navigation.FishDestination
import klev.fishing.map.ui.navigation.FishNavHost
import klev.fishing.map.ui.navigation.navigateToTopLevel
import klev.fishing.map.ui.theme.FishingTheme
import kotlinx.coroutines.launch
import leshy.mushrooms.map.data.platform.currentDeviceLanguage
import leshy.mushrooms.map.domain.model.EditionLanguages
import leshy.mushrooms.map.domain.model.ThemeMode
import leshy.mushrooms.map.data.repository.MapStyleCacheRepository
import leshy.mushrooms.map.domain.repository.SettingsRepository
import leshy.mushrooms.map.i18n.LocalAppLanguage
import leshy.mushrooms.map.ui.theme.isDark
import org.koin.compose.koinInject

private data class DrawerEntry(
    val destination: FishDestination,
    val labelKey: FishStringKey,
    val icon: ImageVector,
)

/**
 * Разделы — боковое выдвижное меню, а не нижняя панель (решение владельца 2026-09-28). Причина в
 * том, чего в панели ещё нет: экспорт/импорт, предзагрузка офлайн-карты, подборки видов по регионам
 * — четыре пункта в bottom bar влезают только потому, что их пока четыре.
 *
 * Пункты заводятся по мере готовности разделов: пустых заглушек в меню нет намеренно — пункт,
 * который открывает «пока ничего», хуже отсутствующего.
 */
private val drawerEntries = listOf(
    DrawerEntry(FishDestination.Record, FishStringKey.NavRecord, Icons.Filled.Phishing),
    DrawerEntry(FishDestination.Archive, FishStringKey.NavArchive, Icons.AutoMirrored.Filled.List),
    DrawerEntry(FishDestination.Map, FishStringKey.NavMap, Icons.Filled.Map),
    DrawerEntry(FishDestination.Species, FishStringKey.NavSpecies, Icons.Filled.SetMeal),
    DrawerEntry(FishDestination.Settings, FishStringKey.NavSettings, Icons.Filled.Settings),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FishingApp() {
    val settings = koinInject<SettingsRepository>()
    val editionLanguages = koinInject<EditionLanguages>()
    // Начальное значение — язык системы, а не EN: `initial` видно ровно до первой эмиссии
    // DataStore, и это кадры холодного старта. Тот же приём, что в грибном `App()`.
    val language by settings.observeLanguage().collectAsState(initial = currentDeviceLanguage(editionLanguages))
    val themeMode by settings.observeThemeMode().collectAsState(initial = ThemeMode.SYSTEM)

    val useDarkTheme = themeMode.isDark()
    // Стиль карты — общий с грибным приложением и живёт в `:shared`, поэтому и обслуживается так же,
    // как там (`App()`): подписи на языке интерфейса, тёмный вариант под тёмную тему, загрузка
    // закреплённого стиля до того, как открыт хоть один экран с картой. Без этих трёх строк карта
    // в тёмной теме оставалась светлым пятном на тёмном экране (видно на эмуляторе 2026-09-28), а
    // подписи — на языке тайлов. Ни одна из них не ходит в сеть без нужды и не трогает офлайн-пакеты
    // — разбор в KDoc самого репозитория.
    val mapStyleCache = koinInject<MapStyleCacheRepository>()
    LaunchedEffect(language) { mapStyleCache.setLabelLanguage(language) }
    LaunchedEffect(useDarkTheme) { mapStyleCache.setDarkTheme(useDarkTheme) }
    LaunchedEffect(Unit) { mapStyleCache.ensureLoaded() }

    CompositionLocalProvider(LocalAppLanguage provides language) {
        FishingTheme(useDarkTheme = useDarkTheme) {
            val navController = rememberNavController()
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = backStackEntry?.destination
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            ModalNavigationDrawer(
                drawerState = drawerState,
                // Свайп от левого края отключён: он спорит с панорамированием карты на «Рыбалке» —
                // тот же конфликт и то же решение, что у грибного приложения. Панель открывается
                // только кнопкой-гамбургером.
                gesturesEnabled = false,
                drawerContent = {
                    ModalDrawerSheet {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        ) {
                            IconButton(onClick = { scope.launch { drawerState.close() } }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                            }
                            Text(
                                text = fishStringResource(FishStringKey.AppName),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        drawerEntries.forEach { entry ->
                            val selected = currentDestination?.hierarchy?.any {
                                it.hasRoute(entry.destination::class)
                            } == true
                            NavigationDrawerItem(
                                selected = selected,
                                label = { Text(fishStringResource(entry.labelKey)) },
                                icon = { Icon(entry.icon, contentDescription = null) },
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    navController.navigateToTopLevel(entry.destination)
                                },
                                modifier = Modifier.padding(horizontal = 12.dp),
                            )
                        }
                    }
                },
            ) {
                FishNavHost(
                    navController = navController,
                    onMenuClick = { scope.launch { drawerState.open() } },
                )
            }

            // ПОСЛЕ `ModalNavigationDrawer`, а не до: диспетчер «назад» отдаёт приоритет
            // ЗАРЕГИСТРИРОВАННОМУ ПОЗЖЕ обработчику, а `NavHost` внутри регистрирует свой. Свой
            // обработчик выше панели означал бы, что системное «назад» с открытой панелью
            // переключает экран под ней, а панель остаётся открытой (живой баг грибного
            // приложения). Сама KMP-версия `ModalNavigationDrawer`, в отличие от Android-only,
            // «назад» не обрабатывает вообще — поэтому обработчик нужен явный.
            BackHandler(enabled = drawerState.isOpen) { scope.launch { drawerState.close() } }
        }
    }
}
