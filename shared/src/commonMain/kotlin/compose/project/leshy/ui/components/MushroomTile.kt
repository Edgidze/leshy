package compose.project.leshy.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.MAX_MUSHROOM_FINDS_PER_WALK
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.categoryDisplayName
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.ui.theme.LeshyTheme
import compose.project.leshy.ui.util.parseHexColor
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val MUSHROOM_COUNT_BUTTON_SIZE = 40.dp

/** Holding the + button this long opens the bulk-add dialog instead of logging a single find. */
private val MUSHROOM_BULK_ADD_HOLD_DURATION = 2.seconds

/** Width [MushroomTile] is displayed at on the record screen — other tiles size themselves relative to it. */
val RECORD_MUSHROOM_TILE_WIDTH = 120.dp

@Composable
fun MushroomTile(
    category: Category,
    count: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    onBulkAdd: () -> Unit = {},
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
                MushroomAddButton(
                    onClick = onAdd,
                    onLongHold = onBulkAdd,
                    enabled = count < MAX_MUSHROOM_FINDS_PER_WALK,
                )
            }
            MushroomPhoto(category = category, modifier = Modifier.fillMaxWidth().aspectRatio(1.5f))
        }
    }
}

/**
 * Rightmost, permanent entry in Record's tile feed (`.claude/plans/user-mushrooms.md`, Phase 4) —
 * not backed by a [Category], just an oversized "+" and a label, opening the species creation form
 * right there on the record screen so a walk in progress is never interrupted. Same footprint as
 * [MushroomTile] (border + rounded card) so it reads as part of the same strip, not a stray button.
 */
@Composable
fun AddSpeciesTile(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().aspectRatio(1f).padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp),
            )
            Text(
                text = stringResource(StringKey.SpeciesAddButton),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

/**
 * The + button — a plain tap logs one find ([onClick]), holding it for
 * [MUSHROOM_BULK_ADD_HOLD_DURATION] opens the bulk-add dialog instead ([onLongHold]). Built from
 * raw [pointerInput] rather than `combinedClickable` because the latter's long-press timeout isn't
 * configurable and defaults to far under a second — same [awaitFirstDown]/
 * [waitForUpOrCancellation] race already used for the map's marker long-press
 * (`MarkerLongPressOverlay.kt`), with the press state fed into [LocalIndication] by hand so the
 * button still shows the normal ripple while held.
 *
 * [onClick]/[onLongHold]/[enabled] are all read through [rememberUpdatedState] and [pointerInput]
 * is keyed on `Unit`, NOT on any of them — while a walk is actively recording, every GPS fix
 * updates `trackPoints`/`distanceMeters` on the record screen, which recreates these closures on
 * each recomposition of the tile feed. Keying `pointerInput` on the lambdas (the first version of
 * this button did) restarted the gesture-detection coroutine on every one of those fixes, wiping
 * out the in-flight 2s hold before it could ever complete — reproduced live: the long-press only
 * "worked" while paused, when nothing was recomposing the tiles fast enough to interrupt it. Once
 * [MAX_MUSHROOM_FINDS_PER_WALK] is reached, [enabled] goes `false` — the gesture is still detected
 * (so the loop doesn't need restarting once the count drops back below the cap) but fires neither
 * callback nor any ripple, and the icon is shown dimmed.
 */
@Composable
private fun MushroomAddButton(
    onClick: () -> Unit,
    onLongHold: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val currentOnClick = rememberUpdatedState(onClick)
    val currentOnLongHold = rememberUpdatedState(onLongHold)
    val currentEnabled = rememberUpdatedState(enabled)
    Box(
        modifier = modifier
            .size(MUSHROOM_COUNT_BUTTON_SIZE)
            .clip(CircleShape)
            .indication(interactionSource, LocalIndication.current)
            .pointerInput(Unit) {
                while (true) {
                    val down = awaitPointerEventScope { awaitFirstDown(requireUnconsumed = false) }
                    if (!currentEnabled.value) {
                        awaitPointerEventScope { waitForUpOrCancellation() }
                        continue
                    }
                    val press = PressInteraction.Press(down.position)
                    interactionSource.tryEmit(press)
                    var longHoldFired = false
                    val up = coroutineScope {
                        val longHoldJob = launch {
                            delay(MUSHROOM_BULK_ADD_HOLD_DURATION)
                            longHoldFired = true
                            currentOnLongHold.value()
                        }
                        val result = awaitPointerEventScope { waitForUpOrCancellation() }
                        longHoldJob.cancel()
                        result
                    }
                    if (up != null) {
                        interactionSource.tryEmit(PressInteraction.Release(press))
                        if (!longHoldFired) currentOnClick.value()
                    } else {
                        interactionSource.tryEmit(PressInteraction.Cancel(press))
                    }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Filled.Add,
            contentDescription = null,
            tint = LocalContentColor.current.copy(alpha = if (enabled) 1f else 0.38f),
            modifier = Modifier.size(32.dp),
        )
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
        CategoryIcon(category = category, modifier = Modifier.fillMaxSize())

        MushroomLabel(
            text = categoryDisplayName(category),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(46.dp)
                .padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
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
            ),
            count = 0,
            onAdd = {},
            onRemove = {},
            modifier = Modifier
        )
    }
}
