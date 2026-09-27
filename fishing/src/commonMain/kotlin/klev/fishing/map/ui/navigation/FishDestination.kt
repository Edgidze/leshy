package klev.fishing.map.ui.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable

/**
 * Маршруты рыбацкого приложения. Свои, а не грибной `Destination`: наборы экранов у продуктов
 * разные, а общий sealed-интерфейс заставил бы каждый обрабатывать чужие маршруты.
 *
 * [Record] — домашний экран И единственный `startDestination` графа одновременно, как `Record` у
 * грибов. Это не стилистическое совпадение: на этом держится [navigateToTopLevel] (см. его KDoc),
 * и заводить условный «экран до Record» маршрутом нельзя — только условным рендером в `FishingApp`.
 */
sealed interface FishDestination {
    @Serializable
    data object Record : FishDestination

    @Serializable
    data object Archive : FishDestination

    @Serializable
    data object Map : FishDestination

    @Serializable
    data object Species : FishDestination

    @Serializable
    data object Settings : FishDestination

    /** Детализация выезда. НЕ пункт меню — открывается из архива обычным `navigate()`. */
    @Serializable
    data class TripDetail(val tripId: Long) : FishDestination
}

/**
 * Единственный разрешённый способ перехода между РАЗДЕЛАМИ (пунктами бокового меню).
 *
 * Копия грибного `navigateToTopLevel` вместе с причиной, по которой там это жёсткое правило, а не
 * стиль: смешивание голого `navigate()` для одного раздела с `popUpTo + saveState/restoreState` у
 * остальных портит кэш сохранённого состояния, и у грибного приложения это стоило нескольких
 * настоящих багов — от «вкладка „Запись“ перестала открываться после любого захода в настройки» до
 * потери активной записи прогулки вместе с её ViewModel. Полный разбор инцидентов —
 * `shared/src/commonMain/kotlin/leshy/mushrooms/map/ui/navigation/CLAUDE.md`.
 *
 * `inclusive = false` держит [FishDestination.Record] внизу бэкстека: «назад» с любого раздела
 * попадает на «Рыбалку», а «назад» на самой «Рыбалке» выходит из приложения штатным поведением
 * `NavHost`, без своего `BackHandler`.
 */
fun NavHostController.navigateToTopLevel(destination: FishDestination) {
    navigate(destination) {
        popUpTo(graph.findStartDestination().id) {
            inclusive = false
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
