package klev.fishing.map.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import klev.fishing.map.domain.model.FishSpecies
import leshy.mushrooms.map.ui.components.MushroomOutlinedText
import leshy.mushrooms.map.ui.util.parseHexColor

/**
 * Соотношение сторон площадки под рыбу — 2:1, и это число про будущие иллюстрации, а не про
 * вёрстку: владелец рисует картинки видов в этом формате (решение 2026-09-28). Рыба вытянута
 * горизонтально, и грибные 1.26 ей не подходят — в почти квадратной площадке длинная рыба заняла бы
 * половину высоты, оставив полосы пустоты сверху и снизу (ровно та беда, которую грибная площадка
 * уже проходила, см. KDoc `MUSHROOM_PHOTO_ASPECT_RATIO` в `:shared`).
 */
const val FISH_PLATE_ASPECT_RATIO = 2f

/**
 * Ширина плитки вида в ленте «Записи». Шире грибной (120dp) ровно потому, что площадка вытянутая:
 * при 120dp высота площадки была бы 60dp, и двухстрочная подпись поверх закрывала бы рыбу целиком.
 */
val RECORD_FISH_TILE_WIDTH = 160.dp

/**
 * Кегль счётчика улова на плитке. Крупнее подписи с названием (18sp) по той же причине, по которой
 * он крупнее на грибной плитке находок: обе надписи набраны одним приёмом — белым по чёрной обводке
 * — и обязаны различаться старшинством, а не спорить.
 */
private val SPECIES_COUNT_FONT_SIZE = 28.sp

/** Отступ счётчика от правого верхнего угла площадки. */
private val SPECIES_COUNT_PADDING = 6.dp

/**
 * Добавка к правому отступу счётчика, долей кегля: текстовый блок не облегает цифру, сверху внутри
 * него лежит подъём шрифта, справа — только узкий боковой зазор глифа. Полный разбор числа — у
 * `FIND_TILE_COUNT_INK_OVERHANG_EM` в `ui/components/StatsBlocks.kt` (`:shared`); та константа
 * приватная, здесь повторено значение, а не сам вывод.
 */
private const val SPECIES_COUNT_INK_OVERHANG_EM = 0.15f

/** Прозрачность снятой с ленты плитки: гашение, а не серый цвет — цвет вида остаётся узнаваемым. */
private const val HIDDEN_TILE_ALPHA = 0.4f

/** Отступ силуэта от краёв площадки — чтобы рыба не касалась рамки плитки. */
private val FISH_PLATE_INSET = 6.dp

/**
 * Плитка вида в ленте «Записи»: площадка формата [FISH_PLATE_ASPECT_RATIO] с рыбой, счётчик улова в
 * правом верхнем углу, название — подписью ПОВЕРХ площадки.
 *
 * **Почему подпись поверх, а не строкой под площадкой.** Строкой под картинкой плитка тем выше, чем
 * длиннее название: в английском «Rainbow trout» и «Perch» дают плитки разной высоты, и лента
 * рассыпается в неровную гребёнку (репорт владельца 2026-09-28). Поверх — высоту задаёт только
 * площадка, она одна на все виды, а длинное название растёт ВВЕРХ на рыбу, не раздвигая плитку.
 * Приём взят у грибных плиток (`MushroomPhoto`, `FindTile`) вместе с самим способом набора —
 * белым по чёрной обводке, читается на любом цвете.
 *
 * Фиксированной высоты у подписи нет намеренно: при крупном системном шрифте она обрезала бы вторую
 * строку — та же грабля, что уже исправлена на грибной плитке.
 *
 * @param hidden вид снят с ленты «Рыбалки» (`FishSpecies.isActive == false`). Плитка гаснет и
 *   получает перечёркнутый глаз в углу: в разделе «Виды рыб» касание плитки переключает именно это,
 *   и состояние обязано быть видно на самой плитке, а не только по отсутствию её в другом экране.
 *
 * Иллюстраций видов пока нет ни у одного (`FishSpecies.iconRef` не заполнен) — на площадке лежит
 * [FishSilhouette] цвета вида. Когда картинки появятся, подключение — это данные каталога, а не
 * правка этого файла.
 */
@Composable
fun SpeciesTile(
    species: FishSpecies,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hidden: Boolean = false,
) {
    val plateColor = MaterialTheme.colorScheme.surfaceContainerHighest
    Card(
        onClick = onClick,
        modifier = modifier.width(RECORD_FISH_TILE_WIDTH).alpha(if (hidden) HIDDEN_TILE_ALPHA else 1f),
        colors = CardDefaults.cardColors(containerColor = plateColor),
    ) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(FISH_PLATE_ASPECT_RATIO)) {
            FishSilhouette(
                color = parseHexColor(species.colorHex),
                eyeColor = plateColor,
                modifier = Modifier.fillMaxSize().padding(FISH_PLATE_INSET),
            )
            if (count > 0) {
                val countInkOverhang = with(LocalDensity.current) {
                    (SPECIES_COUNT_FONT_SIZE.toPx() * SPECIES_COUNT_INK_OVERHANG_EM).toDp()
                }
                MushroomOutlinedText(
                    text = count.toString(),
                    fontSize = SPECIES_COUNT_FONT_SIZE,
                    maxLines = 1,
                    contentAlignment = Alignment.TopEnd,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(
                            top = SPECIES_COUNT_PADDING,
                            end = SPECIES_COUNT_PADDING + countInkOverhang,
                        ),
                )
            }
            if (hidden) {
                Icon(
                    imageVector = Icons.Filled.VisibilityOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.TopStart).padding(SPECIES_COUNT_PADDING),
                )
            }
            MushroomOutlinedText(
                text = speciesDisplayName(species),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(start = 6.dp, top = 2.dp, end = 6.dp, bottom = 4.dp),
            )
        }
    }
}
