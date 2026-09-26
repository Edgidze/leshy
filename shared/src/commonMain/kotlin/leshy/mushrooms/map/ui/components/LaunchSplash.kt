package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.ui.theme.LeshyTheme
import org.jetbrains.compose.resources.painterResource

/** Сторона значка на заставке. Та же, что у значка на приветственном экране. */
private val SPLASH_LOGO_SIZE = 112.dp

/**
 * Заставка холодного старта: земля и значок приложения поверх неё.
 *
 * ## Что именно она закрывает
 *
 * Между первым кадром Compose и первым настоящим экраном есть окно, пока из хранилища читается
 * флаг пройденного онбординга (`App.kt`, `onboardingCompleted == null`). Раньше в этом окне не
 * рисовалось ничего — сплошной цвет фона.
 *
 * ## Почему это НЕ системный сплэш-экран Android
 *
 * Системный сплэш (API 31+) умеет только **цвет** фона: `windowSplashScreenBackground` принимает
 * цвет, а не картинку, и деревянной текстуры там не бывает в принципе. Ближайшее, что можно
 * сделать снаружи приложения, — задать ему тон земли, и это сделано темой
 * (`androidApp/src/russia/res/values/themes.xml`); дерево появляется здесь, первым же кадром
 * самого приложения, и дальше остаётся на месте — та же земля лежит под всеми экранами.
 *
 * На iOS ограничение то же по сути: launch screen там — статическая раскладка бандла, и
 * держать в ней второй экземпляр текстуры незачем.
 *
 * ## Мировая редакция заставки не получает
 *
 * `splashLogo == null` — не рисуется ничего, то есть остаётся прежний пустой кадр. Это не
 * упущение: заставка осмысленна ровно потому, что редакции есть что показать — землю, на которой
 * лежит весь её интерфейс. У мирового «Лешего» такой земли нет, и значок, мелькающий на
 * сплошном фоне, был бы новым поведением там, где менять ничего не просили.
 */
@Composable
fun LaunchSplash(modifier: Modifier = Modifier) {
    val logo = LeshyTheme.tokens.splashLogo ?: return
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Земля рисуется своим полотном в `LeshyTheme`, под всем содержимым, — здесь остаётся
        // только значок. Второй экземпляр текстуры означал бы вторую декодированную картинку на
        // старте, ровно в ту секунду, когда приложение и так занято.
        Image(
            painter = painterResource(logo),
            contentDescription = null,
            modifier = Modifier.size(SPLASH_LOGO_SIZE),
        )
    }
}
