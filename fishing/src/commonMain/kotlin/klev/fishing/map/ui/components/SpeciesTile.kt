package klev.fishing.map.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import klev.fishing.map.domain.model.FishSpecies
import leshy.mushrooms.map.ui.util.parseHexColor

/**
 * Плитка вида на экране записи. Пока это круг цвета вида: иллюстрации рисует владелец, и до тех пор
 * честнее показать цветную метку, чем подобранную наугад картинку. Место под иллюстрацию в модели
 * уже есть (`FishSpecies.iconRef`).
 *
 * Обрыв подписи — с многоточием и в две строки: при крупном системном шрифте иначе теряется конец
 * слова, и «Радужная форель» читается как другое, короткое название. Ровно та же правка, что была
 * сделана на грибной плитке.
 */
@Composable
fun SpeciesTile(
    species: FishSpecies,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(onClick = onClick, modifier = modifier.width(96.dp)) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(parseHexColor(species.colorHex)),
                )
                if (count > 0) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.surface,
                    )
                }
            }
            Text(
                text = speciesDisplayName(species),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
