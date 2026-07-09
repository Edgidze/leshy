package compose.project.leshy.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.EdibilityStatus
import compose.project.leshy.i18n.categoryDisplayName
import compose.project.leshy.ui.util.parseHexColor
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.allDrawableResources
import org.jetbrains.compose.resources.painterResource

@Composable
fun MushroomTile(
    category: Category,
    count: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val outlineColor = parseHexColor(category.colorHex)

    Card(
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(2.dp, outlineColor),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f),
            ) {
                val drawable = category.iconRef?.let { Res.allDrawableResources[it] }
                if (drawable != null) {
                    Image(
                        painter = painterResource(drawable),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                    )
                }

                EdibilityBadge(
                    status = category.edibilityStatus,
                    modifier = Modifier.align(Alignment.TopEnd).padding(6.dp),
                )

                MushroomLabel(
                    text = categoryDisplayName(category.nameKey),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(46.dp)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onRemove, enabled = count > 0) {
                    Icon(Icons.Filled.Remove, contentDescription = null)
                }
                Text(count.toString())
                IconButton(onClick = onAdd) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                }
            }
        }
    }
}

@Composable
private fun EdibilityBadge(status: EdibilityStatus, modifier: Modifier = Modifier) {
    val color = when (status) {
        EdibilityStatus.EDIBLE -> Color(0xFF3FA34D)
        EdibilityStatus.CONDITIONALLY_EDIBLE -> Color(0xFFE0B400)
        EdibilityStatus.INEDIBLE -> Color(0xFFD64545)
    }
    Box(
        modifier = modifier
            .size(18.dp)
            .clip(CircleShape)
            .background(Color.White)
            .padding(2.dp)
            .clip(CircleShape)
            .background(color),
    )
}

private val BASE_LABEL_STYLE = TextStyle(
    fontSize = 18.sp,
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Center,
)

/**
 * Renders [text] with a black outline over a white fill, so it stays readable over any photo.
 * Two stacked [Text] composables (not a manually measured/drawn Canvas) — measuring the same
 * string twice via one [androidx.compose.ui.text.TextMeasurer] with only color/drawStyle
 * differing let the second draw corrupt the first (shared/cached paragraph paint state); plain
 * [Text] calls each own their layout independently and don't hit that.
 */
@Composable
private fun MushroomLabel(text: String, modifier: Modifier = Modifier) {
    val strokeWidthPx = with(LocalDensity.current) { 3.dp.toPx() }
    Box(modifier = modifier, contentAlignment = Alignment.BottomCenter) {
        Text(
            text = text,
            style = BASE_LABEL_STYLE.copy(color = Color.Black, drawStyle = Stroke(width = strokeWidthPx)),
            maxLines = 2,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = text,
            style = BASE_LABEL_STYLE.copy(color = Color.White),
            maxLines = 2,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
