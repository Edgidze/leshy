package compose.project.leshy.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import compose.project.leshy.domain.model.Category
import compose.project.leshy.i18n.categoryDisplayName
import compose.project.leshy.i18n.mushroomsUnitLabel
import compose.project.leshy.presentation.archive.CategoryCount
import compose.project.leshy.ui.util.parseHexColor
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.allDrawableResources
import org.jetbrains.compose.resources.painterResource
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin

private const val RING_START_ANGLE_DEGREES = -90f
private const val RING_SHRINK_FACTOR = 1.5f
private const val CARD_SIZE_OF_RECORD_TILE = 0.75f
private const val FRONT_Z_INDEX = Float.MAX_VALUE

/**
 * Per-position z-index nudge for tie-breaking equal-count sectors — stays well under 1 even for
 * the full ~30-species catalog, so it can never push a smaller sector's card above a bigger one's.
 */
private const val ORDER_TIE_BREAK_Z_INDEX = 0.01f

/**
 * How hard two barely-overlapping cards get pulled together, as a multiple of [CARD_BORDER_WIDTH]
 * — a plain edge-to-edge graze (near-zero overlap) reads as a rendering glitch, not an intentional
 * stack, so [resolveCardAngles] snaps any such pair to overlap by at least this much instead.
 */
private const val MIN_OVERLAP_BORDER_WIDTHS = 10f

private val RING_WIDTH = 28.dp
private val RING_DIVIDER_WIDTH = 2.dp
private val OUTER_HORIZONTAL_MARGIN = 16.dp
private val MAX_OUTER_DIAMETER = 320.dp
private val CARD_GAP = 4.dp
private val CARD_SIZE = RECORD_MUSHROOM_TILE_WIDTH * CARD_SIZE_OF_RECORD_TILE
private val CARD_BORDER_WIDTH = 2.dp

private data class RingSlice(
    val categoryCount: CategoryCount,
    val startAngle: Float,
    val sweepAngle: Float,
    val orderIndex: Int,
)

/**
 * Donut chart of mushroom species found on a walk: one ring segment per [CategoryCount], sized by
 * count and colored by [Category.colorHex], with the walk's total find count in the hole. The ring
 * itself is drawn [RING_SHRINK_FACTOR] smaller than the chart's outer bounds, freeing an annular
 * band around it where every species gets a bordered photo card next to its own sector — no
 * count/buttons/edibility badge/name on the card (too little room at this size, and the finds list
 * above this chart already has name+count per species); tapping a card reports the species'
 * localized display name via [onMushroomClick] so the caller can surface it (walk detail shows it
 * as a bottom snackbar, Toast-style).
 *
 * Cards are large enough, relative to the shrunk ring, that they routinely overlap each other —
 * by default the card for a bigger sector draws in front of a smaller one's (higher [zIndex] via
 * the slice's own count); among equal-count sectors, the one placed earlier around the ring (i.e.
 * closer to the bigger sectors) wins the tie. Tapping a (possibly partially hidden) card brings
 * *that* card to the very front so its full artwork becomes visible.
 *
 * Renders nothing for zero species.
 */
@Composable
fun MushroomDonutChart(counts: List<CategoryCount>, modifier: Modifier = Modifier, onMushroomClick: (String) -> Unit = {}) {
    val ordered = counts.filter { it.count > 0 }.sortedByDescending { it.count }
    val total = ordered.sumOf { it.count }
    if (total <= 0) return

    var angleCursor = RING_START_ANGLE_DEGREES
    val slices = ordered.mapIndexed { index, categoryCount ->
        val sweep = 360f * categoryCount.count / total
        RingSlice(categoryCount, angleCursor, sweep, index).also { angleCursor += sweep }
    }

    var frontCategoryId by remember { mutableStateOf<Long?>(null) }

    BoxWithConstraints(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        val ringDiameter = (maxWidth - OUTER_HORIZONTAL_MARGIN).coerceAtMost(MAX_OUTER_DIAMETER) / RING_SHRINK_FACTOR
        val cardCenterRadius = ringDiameter / 2f + CARD_GAP + CARD_SIZE / 2f
        val containerDiameter = ((cardCenterRadius + CARD_SIZE / 2f) * 2f).coerceAtMost(maxWidth)
        val cardAngles = resolveCardAngles(slices, cardCenterRadius)

        Box(modifier = Modifier.size(containerDiameter), contentAlignment = Alignment.Center) {
            DonutRing(slices = slices, diameter = ringDiameter)

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(total.toString(), fontSize = 44.sp, fontWeight = FontWeight.Bold)
                Text(mushroomsUnitLabel(total), fontSize = 16.sp)
            }

            // zIndex alone decides stacking order here (ties are only ever broken by composition
            // order, and there are none left to break — see ORDER_TIE_BREAK_Z_INDEX), so the loop
            // itself can iterate slices in any order.
            for (slice in slices) {
                val category = slice.categoryCount.category
                val midAngleRad = cardAngles[slice.orderIndex] * (PI.toFloat() / 180f)
                val xOffset = cardCenterRadius * cos(midAngleRad)
                val yOffset = cardCenterRadius * sin(midAngleRad)
                val displayName = categoryDisplayName(category.nameKey)
                val cardZIndex = if (category.id == frontCategoryId) {
                    FRONT_Z_INDEX
                } else {
                    slice.categoryCount.count.toFloat() - slice.orderIndex * ORDER_TIE_BREAK_Z_INDEX
                }

                MushroomLegendCard(
                    category = category,
                    onClick = {
                        frontCategoryId = category.id
                        onMushroomClick(displayName)
                    },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = xOffset, y = yOffset)
                        .zIndex(cardZIndex)
                        .size(CARD_SIZE),
                )
            }
        }
    }
}

/**
 * Nudges each card's placement angle (indexed like [slices], i.e. by [RingSlice.orderIndex]) so
 * that any two angularly-adjacent cards which barely graze each other — chord distance just under
 * [CARD_SIZE], overlapping by less than [MIN_OVERLAP_BORDER_WIDTHS] border widths — get pulled
 * together instead, until they overlap by at least that much. Pairs that don't overlap at all, or
 * already overlap comfortably, are left untouched. All cards sit on the same [radius], so the
 * chord-distance-to-angle relationship is exact (no square/circle overlap approximation needed).
 */
private fun resolveCardAngles(slices: List<RingSlice>, radius: Dp): FloatArray {
    val midAngles = FloatArray(slices.size) { slices[it].startAngle + slices[it].sweepAngle / 2f }
    if (slices.size < 2) return midAngles

    fun chordAngleDegrees(chordLength: Dp): Float {
        val ratio = (chordLength.value / (2f * radius.value)).coerceIn(-1f, 1f)
        return 2f * asin(ratio) * (180f / PI.toFloat())
    }

    val touchingGapDegrees = chordAngleDegrees(CARD_SIZE)
    val solidOverlapGapDegrees = chordAngleDegrees(CARD_SIZE - CARD_BORDER_WIDTH * MIN_OVERLAP_BORDER_WIDTHS)

    val adjustments = FloatArray(slices.size)
    for (i in slices.indices) {
        val j = (i + 1) % slices.size
        val gap = if (j != 0) midAngles[j] - midAngles[i] else (midAngles[j] + 360f) - midAngles[i]
        if (gap in solidOverlapGapDegrees..touchingGapDegrees) {
            val deficit = gap - solidOverlapGapDegrees
            adjustments[i] += deficit / 2f
            adjustments[j] -= deficit / 2f
        }
    }

    return FloatArray(slices.size) { midAngles[it] + adjustments[it] }
}

@Composable
private fun DonutRing(slices: List<RingSlice>, diameter: Dp) {
    val ringWidthPx = with(LocalDensity.current) { RING_WIDTH.toPx() }
    val dividerWidthPx = with(LocalDensity.current) { RING_DIVIDER_WIDTH.toPx() }

    Canvas(modifier = Modifier.size(diameter)) {
        val inset = ringWidthPx / 2
        val arcSize = Size(size.width - ringWidthPx, size.height - ringWidthPx)
        val topLeft = Offset(inset, inset)
        slices.forEach { slice ->
            drawArc(
                color = parseHexColor(slice.categoryCount.category.colorHex),
                startAngle = slice.startAngle,
                sweepAngle = slice.sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = ringWidthPx),
            )
            if (slices.size > 1) {
                drawArc(
                    color = Color.White,
                    startAngle = slice.startAngle,
                    sweepAngle = slice.sweepAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = dividerWidthPx),
                )
            }
        }
    }
}

@Composable
private fun MushroomLegendCard(category: Category, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val drawable = category.iconRef?.let { Res.allDrawableResources[it] }
    Card(
        modifier = modifier.clickable(onClick = onClick),
        border = BorderStroke(CARD_BORDER_WIDTH, parseHexColor(category.colorHex)),
    ) {
        if (drawable != null) {
            Image(
                painter = painterResource(drawable),
                contentDescription = categoryDisplayName(category.nameKey),
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize().padding(4.dp),
            )
        }
    }
}
