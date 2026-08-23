package compose.project.leshy.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import compose.project.leshy.data.platform.PhotoStorage
import compose.project.leshy.data.platform.ShareContent
import compose.project.leshy.data.platform.WalkFindMarker
import compose.project.leshy.data.platform.WalkThumbnailRenderer
import compose.project.leshy.data.platform.decodeScaledImage
import compose.project.leshy.data.platform.encodePng
import compose.project.leshy.data.platform.rememberShareLauncher
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.FieldMark
import compose.project.leshy.domain.model.GeoPoint
import compose.project.leshy.domain.model.MarkType
import compose.project.leshy.domain.model.Walk
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.categoryDisplayName
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.presentation.archive.CategoryCount
import compose.project.leshy.ui.map.LocalMushroomMarkerSizeScale
import compose.project.leshy.ui.util.formatDistanceKm
import compose.project.leshy.ui.util.formatDurationLabeled
import compose.project.leshy.ui.util.formatSpeedKmh
import kotlin.math.roundToInt
import kotlinx.coroutines.launch
import okio.FileSystem
import okio.Path.Companion.toPath
import org.koin.compose.koinInject

// Wide enough for MushroomDonutChart's own MAX_OUTER_DIAMETER (360.dp) plus its margin.
private val COMPOSITE_WIDTH = 400.dp

// Vertical gap above AND below the ring/cards (title→chart, chart→map) — kept equal on both sides.
private val DIAGRAM_SECTION_GAP = 20.dp

// Bigger than the 240px Archive-list thumbnail — this is the picture people actually look at once
// shared, not a list icon. "_share" keeps it a separate cached file from Walk.thumbnailPath.
private const val SHARE_MAP_SIZE_PX = 720
private const val SHARE_MAP_VARIANT = "_share"

// Baseline (LocalMushroomMarkerSizeScale == 1x) icon box for the share map, scaled by the same
// factor the live map applies to MUSHROOM_MARKER_BASE_SIZE (ui/map/MushroomMarkerIcon.kt) — the
// two bases are unrelated (dp on the live map vs. a fixed-pixel export canvas here), only the
// user's chosen multiplier carries across, so the exported map's icons feel proportionally the
// same size as what they see on the live map.
private const val BASE_SHARE_MARKER_ICON_SIZE_PX = 64

/**
 * "Which parts of this walk to share" — modeled on [WalksPickerDialog]'s shape (own draft state,
 * confirm/cancel at the bottom, no persistence, no `Destination` route needed). Assembles a text
 * message plus an optional single diagram+map PNG attachment and hands them to [rememberShareLauncher].
 */
@Composable
fun WalkShareDialog(
    walk: Walk,
    mushroomCounts: List<CategoryCount>,
    track: List<GeoPoint>,
    marks: List<FieldMark>,
    categories: List<Category>,
    onDismiss: () -> Unit,
) {
    var includeName by remember { mutableStateOf(true) }
    var includeStats by remember { mutableStateOf(true) }
    var includeDescription by remember { mutableStateOf(true) }
    var includeDiagram by remember { mutableStateOf(true) }
    var includeMap by remember { mutableStateOf(false) }
    var isPreparing by remember { mutableStateOf(false) }

    // Rendering the map is async (native snapshotter) — kicked off as soon as the box is checked,
    // not deferred to the share click, and cached once loaded so re-toggling the checkbox doesn't
    // re-render. "Поделиться" stays disabled while this is in flight (isMapLoading), so by the time
    // it's clickable the composite below has already drawn the map on some earlier, already-elapsed
    // frame — no reliance on same-frame recomposition timing.
    var mapBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var isMapLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val shareLauncher = rememberShareLauncher()
    val photoStorage = koinInject<PhotoStorage>()
    val walkThumbnailRenderer = koinInject<WalkThumbnailRenderer>()
    val compositeGraphicsLayer = rememberGraphicsLayer()
    val markerSizeScale = LocalMushroomMarkerSizeScale.current

    LaunchedEffect(includeMap) {
        if (!includeMap || mapBitmap != null) return@LaunchedEffect
        isMapLoading = true
        val mushroomMarks = marks.filter { it.type == MarkType.MUSHROOM }
        val findLocations = mushroomMarks.map { GeoPoint(it.lat, it.lon, null, it.timestamp) }
        val speciesMarkers = mushroomMarks.mapNotNull { mark ->
            categories.find { it.id == mark.categoryId }?.let { category ->
                WalkFindMarker(GeoPoint(mark.lat, mark.lon, null, mark.timestamp), category)
            }
        }
        val mapPath = walkThumbnailRenderer.render(
            walkId = walk.id,
            track = track,
            findLocations = findLocations,
            anchor = anchorOf(walk),
            sizePx = SHARE_MAP_SIZE_PX,
            variant = SHARE_MAP_VARIANT,
            speciesMarkers = speciesMarkers,
            markerIconSizePx = (BASE_SHARE_MARKER_ICON_SIZE_PX * markerSizeScale).roundToInt(),
        )
        mapBitmap = mapPath?.let { decodeScaledImage(it, SHARE_MAP_SIZE_PX) }
        isMapLoading = false
    }

    val statsText = buildList {
        add("${stringResource(StringKey.WalkDetailDistance)}: ${formatDistanceKm(walk.distanceMeters)}")
        walk.endTime?.let { endTime ->
            add("${stringResource(StringKey.WalkDetailDuration)}: ${formatDurationLabeled(endTime - walk.startTime)}")
            add("${stringResource(StringKey.WalkDetailAvgSpeed)}: ${formatSpeedKmh(walk.avgSpeed)}")
        }
        if (mushroomCounts.isNotEmpty()) {
            add("")
            add("${stringResource(StringKey.WalkDetailFindsTitle)}:")
            mushroomCounts.forEach { entry -> add("${categoryDisplayName(entry.category)}: ${entry.count}") }
        }
    }.joinToString("\n")

    val descriptionText = walk.description?.ifBlank { null }
    val footerText = stringResource(StringKey.WalkShareFooter)

    // Footer is unconditional — always the last section, separated by a blank line from whatever
    // precedes it (or standalone if every other section is unchecked/empty).
    val shareText = buildList {
        if (includeName) add(walk.name)
        if (includeStats) add(statsText)
        if (includeDescription && descriptionText != null) add(descriptionText)
        add(footerText)
    }.joinToString("\n\n")

    val showDiagram = includeDiagram && mushroomCounts.isNotEmpty()
    val showMap = includeMap && mapBitmap != null

    // Mounted off-screen for the dialog's whole lifetime, showing whatever combination of
    // diagram/map is currently selected (0, 1, or both, stacked) — reusing the exact same
    // Compose-composable capture (GraphicsLayer.record{} inside drawWithContent) already proven
    // safe for the plain diagram case. Deliberately NOT hand-compositing separately captured
    // ImageBitmaps via a raw Canvas/CanvasDrawScope outside normal composition — an earlier
    // version did that and crashed on Android (a captured layer's bitmap can be hardware-backed,
    // and drawing a hardware bitmap into a software-backed destination canvas outside Compose's
    // own rendering pipeline throws `Software rendering doesn't support hardware bitmaps`, the
    // same class of crash already documented for MapLibre marker baking — see ui/map/CLAUDE.md).
    // Going through a normal Image()/MushroomDonutChart() composable + GraphicsLayer capture next
    // to it avoids that entirely, the same way this app already safely displays photo bitmaps
    // everywhere else on screen.
    // Wrapped in its own fillMaxSize() Box so the offset child below can never inflate this
    // screen's own layout size — Box without an explicit size modifier sizes itself to the union
    // of its children's MEASURED (pre-offset) dimensions, not their post-offset placement, so an
    // unwrapped offset child would otherwise still claim that space from whatever real container
    // this composable is embedded in.
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .offset { IntOffset(-10_000, -10_000) }
                .width(COMPOSITE_WIDTH)
                // With title + chart + map + caption all stacked, the composite's natural height
                // can exceed a typical phone screen's own height — and since this Box has no
                // explicit height, it would otherwise only be measured up to whatever height the
                // real screen leaves available (inherited from the fillMaxSize() wrapper above),
                // silently clipping away the bottom-most content (the caption). Same class of bug,
                // and same fix, as the earlier width-overflow issue with the side-by-side layout —
                // wrapContentHeight(unbounded = true) measures this subtree with an effectively
                // infinite max height so it's never constrained by the real screen.
                .wrapContentHeight(unbounded = true)
                // .background() must sit AFTER .drawWithContent() in the chain (i.e. draw inside
                // it), not before — modifiers earlier in the chain draw themselves onto the real
                // canvas before reaching drawWithContent, so record{}'s drawContent() call (which
                // only replays the REMAINING chain from drawWithContent onward) would never see a
                // background placed before it.
                .drawWithContent {
                    compositeGraphicsLayer.record { this@drawWithContent.drawContent() }
                    drawLayer(compositeGraphicsLayer)
                }
                .background(Color.White),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Title (above) and caption (below) bracket whatever image content is selected —
                // diagram alone, map alone, or both — not just the diagram case.
                val showAnyImage = showDiagram || showMap
                if (showAnyImage) {
                    Text(
                        text = walk.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        textDecoration = TextDecoration.Underline,
                        // Same gap used below (chart→map, and content→caption), so spacing stays
                        // visually even throughout.
                        modifier = Modifier.fillMaxWidth().padding(bottom = DIAGRAM_SECTION_GAP),
                    )
                }
                if (showDiagram) {
                    MushroomDonutChart(counts = mushroomCounts, modifier = Modifier.fillMaxWidth())
                }
                val bitmap = mapBitmap
                if (showMap && bitmap != null) {
                    if (showDiagram) Spacer(modifier = Modifier.height(DIAGRAM_SECTION_GAP))
                    Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                if (showAnyImage) {
                    Text(
                        text = stringResource(StringKey.WalkShareImageFooter),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = DIAGRAM_SECTION_GAP),
                    )
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = !isPreparing),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.92f),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 4.dp,
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(stringResource(StringKey.WalkShareDialogTitle), style = MaterialTheme.typography.titleMedium)

                Column(modifier = Modifier.padding(top = 12.dp)) {
                    ShareOptionRow(
                        label = stringResource(StringKey.WalkShareOptionName),
                        checked = includeName,
                        onCheckedChange = { includeName = it },
                    )
                    ShareOptionRow(
                        label = stringResource(StringKey.WalkShareOptionStats),
                        checked = includeStats,
                        onCheckedChange = { includeStats = it },
                    )
                    ShareOptionRow(
                        label = stringResource(StringKey.WalkShareOptionDescription),
                        checked = includeDescription,
                        onCheckedChange = { includeDescription = it },
                    )
                    ShareOptionRow(
                        label = stringResource(StringKey.WalkShareOptionDiagram),
                        checked = includeDiagram,
                        enabled = mushroomCounts.isNotEmpty(),
                        onCheckedChange = { includeDiagram = it },
                    )
                    ShareOptionRow(
                        label = stringResource(StringKey.WalkShareOptionMap),
                        checked = includeMap,
                        enabled = walk.thumbnailPath != null,
                        onCheckedChange = { includeMap = it },
                    )
                    if (includeMap) {
                        Text(
                            stringResource(StringKey.WalkShareMapWarning),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 48.dp, end = 8.dp, top = 2.dp, bottom = 4.dp),
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    OutlinedButton(onClick = onDismiss, enabled = !isPreparing) {
                        Text(stringResource(StringKey.WalkShareCancelButton))
                    }
                    Button(
                        modifier = Modifier.padding(start = 12.dp),
                        enabled = !isPreparing && !isMapLoading,
                        onClick = {
                            isPreparing = true
                            coroutineScope.launch {
                                val imagePaths = mutableListOf<String>()

                                if (showDiagram || showMap) {
                                    val pngBytes = encodePng(compositeGraphicsLayer.toImageBitmap())
                                    if (pngBytes != null) {
                                        val path = photoStorage.resolvePath("walk_${walk.id}_share.png")
                                        val written = runCatching {
                                            FileSystem.SYSTEM.write(path.toPath()) { write(pngBytes) }
                                        }.isSuccess
                                        if (written) imagePaths += path
                                    }
                                }

                                isPreparing = false
                                shareLauncher(ShareContent(shareText, imagePaths))
                                onDismiss()
                            }
                        },
                    ) {
                        if (isPreparing || isMapLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Text(stringResource(StringKey.WalkShareConfirmButton))
                        }
                    }
                }
            }
        }
    }
}

// Same walk.endLat/endLon → walk.startLat/startLon → null fallback as
// BackfillWalkThumbnailsUseCase.anchorOf — walk.startLat/startLon default to (0.0, 0.0) when
// Start was pressed before GPS produced a fix, so that sentinel is only used as a last resort.
private fun anchorOf(walk: Walk): GeoPoint? = when {
    walk.endLat != null && walk.endLon != null ->
        GeoPoint(walk.endLat, walk.endLon, null, walk.endTime ?: walk.startTime)
    walk.startLat != 0.0 || walk.startLon != 0.0 ->
        GeoPoint(walk.startLat, walk.startLon, null, walk.startTime)
    else -> null
}

@Composable
private fun ShareOptionRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = checked, enabled = enabled, onValueChange = onCheckedChange, role = Role.Checkbox)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = checked, onCheckedChange = null, enabled = enabled)
        Text(label, modifier = Modifier.padding(start = 8.dp))
    }
}
