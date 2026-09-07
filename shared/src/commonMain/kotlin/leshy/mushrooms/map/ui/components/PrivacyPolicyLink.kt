package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.stringResource

/**
 * Публичная страница политики конфиденциальности — тот же адрес, что уходит в Play Console и
 * App Store Connect (оба магазина требуют URL в карточке приложения; Google, кроме того, требует
 * ссылку и внутри приложения для всего, что трогает геолокацию/камеру — отсюда две точки входа,
 * [PrivacyPolicyLink] в онбординге и в «Настройках»).
 *
 * Исходник страницы лежит в репозитории (`site/privacy.html`) и публикуется на GitHub Pages —
 * как именно, см. `site/README.md`. Поменяется хостинг — правится эта константа, больше нигде
 * адрес не встречается.
 */
const val PRIVACY_POLICY_URL: String = "https://leshy-mapper.github.io/mushrooms-map/privacy.html"

/**
 * Строка-ссылка «Политика конфиденциальности», открывающая [PRIVACY_POLICY_URL] во внешнем
 * браузере.
 *
 * `LocalUriHandler` — общий для Android и iOS (`UriHandler` в Compose Multiplatform поверх
 * `Intent.ACTION_VIEW`/`UIApplication.openURL`), поэтому никакого `expect`/`actual` под открытие
 * ссылки заводить не нужно.
 */
@Composable
fun PrivacyPolicyLink(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { uriHandler.openUri(PRIVACY_POLICY_URL) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(StringKey.LegalPrivacyLink),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp),
        )
    }
}
