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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.EdibilityStatus
import compose.project.leshy.i18n.categoryDisplayName
import compose.project.leshy.ui.theme.LeshyTheme
import compose.project.leshy.ui.util.parseHexColor
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.allDrawableResources
import org.jetbrains.compose.resources.painterResource

private val MUSHROOM_COUNT_BUTTON_SIZE = 40.dp

/** Width [MushroomTile] is displayed at on the record screen — other tiles size themselves relative to it. */
val RECORD_MUSHROOM_TILE_WIDTH = 120.dp

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = onRemove,
                    enabled = count > 0,
                    modifier = Modifier.size(MUSHROOM_COUNT_BUTTON_SIZE),
                ) {
                    Icon(Icons.Filled.Remove, contentDescription = null, Modifier.size(32.dp))
                }
                Text(
                    text = count.toString(),
                    fontSize = if (count.toString().length >= 3) 14.sp else 20.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier.width(28.dp),
                )
                IconButton(onClick = onAdd, modifier = Modifier.size(MUSHROOM_COUNT_BUTTON_SIZE)) {
                    Icon(Icons.Filled.Add, contentDescription = null, Modifier.size(32.dp))
                }
            }
            MushroomPhoto(category = category, modifier = Modifier.fillMaxWidth().aspectRatio(1.5f))
        }
    }
}

/**
 * The photo/badge/label portion of [MushroomTile] (everything below its count row), reused as-is
 * by [compose.project.leshy.ui.components.MushroomLegendTile] for the walk-detail donut chart's
 * legend — same bordered-plate look, minus the count row that doesn't apply there.
 */
@Composable
fun MushroomPhoto(category: Category, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
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
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 6.dp, end = 6.dp),
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
    Box(modifier = modifier.padding(bottom=2.dp), contentAlignment = Alignment.BottomCenter) {
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

@Preview
@Composable
fun MushroomTilePreview(){
    LeshyTheme {
        MushroomTile(
            category = Category(
                1,
                "category_boletus_edulis",
                "#A95620",
                "boletus_edulis",
                0,
                true,
                EdibilityStatus.EDIBLE
            ),
            count = 0,
            onAdd = {},
            onRemove = {},
            modifier = Modifier
        )
    }
}
