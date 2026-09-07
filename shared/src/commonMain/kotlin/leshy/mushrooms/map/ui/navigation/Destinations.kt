package leshy.mushrooms.map.ui.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable
import leshy.mushrooms.map.i18n.HelpTopic

sealed interface Destination {
    @Serializable
    data object Record : Destination

    @Serializable
    data object Archive : Destination

    @Serializable
    data class WalkDetail(val walkId: Long) : Destination

    @Serializable
    data class WalkMap(val walkId: Long) : Destination

    @Serializable
    data class WalkDescriptionEdit(val walkId: Long) : Destination

    @Serializable
    data object Map : Destination

    /**
     * Сводная карта во весь экран. НЕ пункт бокового меню — вложенный экран раздела [Map],
     * открывается с его заставки обычным `navigate()`, как [WalkMap] у [WalkDetail].
     */
    @Serializable
    data object FindsMap : Destination

    @Serializable
    data object Preparation : Destination

    @Serializable
    data object Settings : Destination

    @Serializable
    data object LanguagePicker : Destination

    /**
     * «О приложении». НЕ пункт бокового меню — лист, открываемый строкой из «Настроек» обычным
     * `navigate()`, как [LanguagePicker] оттуда же.
     */
    @Serializable
    data object About : Destination

    /**
     * Справка по разделу — лист, открываемый кнопкой «?» из шапки самого раздела обычным
     * `navigate()`, как [About] из «Настроек».
     *
     * **Раздел едет строкой, а не значением [HelpTopic], и это не стиль, а требование iOS.**
     * `navigation-common` разбирает enum-аргумент маршрута рефлексией, которой на нативных
     * целях нет: `SerialDescriptor.parseEnum()` там — `actual`, возвращающий `UNKNOWN`
     * (`NavTypeConverter.nonAndroid.kt`), после чего `RouteSerializer` бросает
     * `IllegalArgumentException` про «unknown NavType» при построении графа. На Android то же
     * самое работает, так что цена ошибки — приложение, собирающееся и работающее на одной
     * платформе из двух. `NavType.StringType` есть на обеих.
     *
     * Наружу класс всё равно выглядит типизированным: конструктор принимает [HelpTopic],
     * свойство [topic] возвращает его же обратно.
     */
    @Serializable
    data class Help(val topicName: String) : Destination {
        constructor(topic: HelpTopic) : this(topic.name)

        /** Имя в маршруте кладём только мы сами (кнопкой «?»), внешних ссылок на маршруты у
         * приложения нет — поэтому `valueOf` здесь не про валидацию чужого ввода, а про то, что
         * несуществующее имя означает нашу же опечатку и должно падать громко. */
        val topic: HelpTopic get() = HelpTopic.valueOf(topicName)
    }

    @Serializable
    data object Data : Destination

    @Serializable
    data object Species : Destination
}

/**
 * All top-level section destinations (side-drawer entries) must navigate through this
 * same pop/save/restore scheme. Mixing a plain `navigate()` for one of them corrupts the
 * saved-state cache the others rely on to survive tab switches.
 *
 * `inclusive = false` keeps `Record` (the graph's start destination / home screen) anchored
 * at the bottom of the back stack rather than removing it — that's what makes back-from-a-
 * section land on Record, and back-from-Record fall through to the platform default (app
 * exit) instead of a custom exit handler.
 */
fun NavHostController.navigateToTopLevel(destination: Destination) {
    navigate(destination) {
        popUpTo(graph.findStartDestination().id) {
            inclusive = false
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
