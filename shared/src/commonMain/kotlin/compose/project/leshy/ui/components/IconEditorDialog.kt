package compose.project.leshy.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import compose.project.leshy.data.platform.EDITOR_IMAGE_MAX_DIMENSION
import compose.project.leshy.data.platform.decodeScaledImage
import compose.project.leshy.data.platform.encodePng
import compose.project.leshy.domain.usecase.CATEGORY_ICON_MAX_DIMENSION
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.ui.util.scaledToMaxDimension
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private val CHECKER_TILE = 12.dp
private val CHECKER_LIGHT = Color(0xFFE0E0E0)
private val CHECKER_DARK = Color(0xFFBDBDBD)
private val CROP_HANDLE_TOUCH_RADIUS = 24.dp
private val CROP_HANDLE_DRAW_RADIUS = 6.dp
private const val MIN_BRUSH_FRACTION = 0.015f
private const val MAX_BRUSH_FRACTION = 0.12f
private const val DEFAULT_BRUSH_FRACTION = 0.05f
private const val MIN_CROP_SIZE_FRACTION = 0.15f
private const val DEFAULT_CROP_INSET_FRACTION = 0.08f
private val MAGNIFIER_DIAMETER = 128.dp
private val MAGNIFIER_VERTICAL_GAP = 24.dp
private const val MAGNIFIER_SOURCE_FRACTION = 0.18f

private enum class EditorTool { ERASER, CROP }

/** Which shape [CropRect]'s bounding box is rendered/exported as — chosen independently via its own control in the bottom bar while [EditorTool.CROP] is active, so it survives switching back to ERASER and isn't tied to how CROP itself was entered. */
private enum class CropShape { RECTANGLE, OVAL }

/** One committed (or in-progress) erase gesture, in fractions (0f..1f) of the photo's own bounds — resolution-independent, so the same stroke list renders correctly both for the live on-screen preview and for the final full-resolution bake. */
private data class EraseStroke(val points: List<Offset>, val widthFraction: Float)

private fun EraseStroke.toPath(targetWidth: Float, targetHeight: Float): Path {
    val path = Path()
    val first = points.first()
    path.moveTo(first.x * targetWidth, first.y * targetHeight)
    for (index in 1 until points.size) {
        val point = points[index]
        path.lineTo(point.x * targetWidth, point.y * targetHeight)
    }
    return path
}

private data class CropRect(val left: Float, val top: Float, val right: Float, val bottom: Float) {
    companion object {
        /** True starting state: no crop applied if the user never opens the Crop tool at all. */
        val Full = CropRect(0f, 0f, 1f, 1f)

        // An exactly full-frame rect makes the crop tool look broken the moment it's opened:
        // dragging inside it is a no-op (nowhere to move a rect that already spans the whole
        // range), and the corner handles sit exactly on the rounded-corner clip, mostly invisible.
        // [IconEditorDialog] swaps `Full` for this the first time the Crop tool is selected, so the
        // tool visibly *does something* right away — a handle to grab, a scrim showing what's
        // excluded — without silently trimming the photo for anyone who never opens the tool.
        val Default = CropRect(
            DEFAULT_CROP_INSET_FRACTION,
            DEFAULT_CROP_INSET_FRACTION,
            1f - DEFAULT_CROP_INSET_FRACTION,
            1f - DEFAULT_CROP_INSET_FRACTION,
        )
    }
}

private enum class CropCorner { TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT }

private sealed interface CropDrag {
    data class Corner(val corner: CropCorner) : CropDrag
    data class Move(val startPointerFraction: Offset, val startRect: CropRect) : CropDrag
}

private fun resolveCropDrag(offset: Offset, size: IntSize, rect: CropRect, handleRadiusPx: Float): CropDrag? {
    val corners = mapOf(
        CropCorner.TOP_LEFT to Offset(rect.left * size.width, rect.top * size.height),
        CropCorner.TOP_RIGHT to Offset(rect.right * size.width, rect.top * size.height),
        CropCorner.BOTTOM_LEFT to Offset(rect.left * size.width, rect.bottom * size.height),
        CropCorner.BOTTOM_RIGHT to Offset(rect.right * size.width, rect.bottom * size.height),
    )
    val nearest = corners.entries.minByOrNull { (offset - it.value).getDistance() }
    if (nearest != null && (offset - nearest.value).getDistance() <= handleRadiusPx) {
        return CropDrag.Corner(nearest.key)
    }
    val leftPx = rect.left * size.width
    val topPx = rect.top * size.height
    val rightPx = rect.right * size.width
    val bottomPx = rect.bottom * size.height
    if (offset.x in leftPx..rightPx && offset.y in topPx..bottomPx) {
        return CropDrag.Move(Offset(offset.x / size.width, offset.y / size.height), rect)
    }
    return null
}

private fun applyCropDrag(drag: CropDrag?, pointerPosition: Offset, size: IntSize, current: CropRect): CropRect {
    if (drag == null) return current
    val fx = (pointerPosition.x / size.width).coerceIn(0f, 1f)
    val fy = (pointerPosition.y / size.height).coerceIn(0f, 1f)
    return when (drag) {
        is CropDrag.Corner -> when (drag.corner) {
            CropCorner.TOP_LEFT -> current.copy(
                left = fx.coerceAtMost(current.right - MIN_CROP_SIZE_FRACTION),
                top = fy.coerceAtMost(current.bottom - MIN_CROP_SIZE_FRACTION),
            )
            CropCorner.TOP_RIGHT -> current.copy(
                right = fx.coerceAtLeast(current.left + MIN_CROP_SIZE_FRACTION),
                top = fy.coerceAtMost(current.bottom - MIN_CROP_SIZE_FRACTION),
            )
            CropCorner.BOTTOM_LEFT -> current.copy(
                left = fx.coerceAtMost(current.right - MIN_CROP_SIZE_FRACTION),
                bottom = fy.coerceAtLeast(current.top + MIN_CROP_SIZE_FRACTION),
            )
            CropCorner.BOTTOM_RIGHT -> current.copy(
                right = fx.coerceAtLeast(current.left + MIN_CROP_SIZE_FRACTION),
                bottom = fy.coerceAtLeast(current.top + MIN_CROP_SIZE_FRACTION),
            )
        }
        is CropDrag.Move -> {
            val width = drag.startRect.right - drag.startRect.left
            val height = drag.startRect.bottom - drag.startRect.top
            val newLeft = (drag.startRect.left + (fx - drag.startPointerFraction.x)).coerceIn(0f, 1f - width)
            val newTop = (drag.startRect.top + (fy - drag.startPointerFraction.y)).coerceIn(0f, 1f - height)
            current.copy(left = newLeft, top = newTop, right = newLeft + width, bottom = newTop + height)
        }
    }
}

/**
 * Bakes [strokes] (`BlendMode.Clear`, same mechanism as the live preview) into [photo] at its own
 * resolution, then crops to [crop] — as a plain rectangle for [CropShape.RECTANGLE], or additionally
 * masked to the oval inscribed in that same bounding box for [CropShape.OVAL] (the mask is applied
 * with `BlendMode.Clear` via an even-odd-filled outer-rect-minus-oval [Path], the same "clear
 * everything not wanted" idiom already used for the eraser strokes above). Kept as a plain function,
 * not a `DrawScope` extension, because it draws into an offscreen [ImageBitmap] via
 * [androidx.compose.ui.graphics.Canvas] rather than onto the screen.
 */
private fun renderErasedAndCropped(
    photo: ImageBitmap,
    strokes: List<EraseStroke>,
    crop: CropRect,
    shape: CropShape,
): ImageBitmap {
    val baked = ImageBitmap(photo.width, photo.height)
    val canvas = Canvas(baked)
    canvas.drawImage(photo, Offset.Zero, Paint())
    val erasePaint = Paint().apply {
        style = PaintingStyle.Stroke
        strokeCap = StrokeCap.Round
        strokeJoin = StrokeJoin.Round
        blendMode = BlendMode.Clear
    }
    val maxDimension = max(photo.width, photo.height).toFloat()
    for (stroke in strokes) {
        erasePaint.strokeWidth = stroke.widthFraction * maxDimension
        canvas.drawPath(stroke.toPath(photo.width.toFloat(), photo.height.toFloat()), erasePaint)
    }

    val cropLeft = (crop.left * photo.width).toInt().coerceIn(0, photo.width - 1)
    val cropTop = (crop.top * photo.height).toInt().coerceIn(0, photo.height - 1)
    val cropRight = (crop.right * photo.width).toInt().coerceIn(cropLeft + 1, photo.width)
    val cropBottom = (crop.bottom * photo.height).toInt().coerceIn(cropTop + 1, photo.height)
    val isFullFrame = cropLeft == 0 && cropTop == 0 && cropRight == photo.width && cropBottom == photo.height
    if (shape == CropShape.RECTANGLE && isFullFrame) return baked

    val cropWidth = cropRight - cropLeft
    val cropHeight = cropBottom - cropTop
    val cropped = ImageBitmap(cropWidth, cropHeight)
    val croppedCanvas = Canvas(cropped)
    croppedCanvas.drawImageRect(
        image = baked,
        srcOffset = IntOffset(cropLeft, cropTop),
        srcSize = IntSize(cropWidth, cropHeight),
        dstOffset = IntOffset.Zero,
        dstSize = IntSize(cropWidth, cropHeight),
        paint = Paint().apply { filterQuality = FilterQuality.High },
    )
    if (shape == CropShape.OVAL) {
        val bounds = Rect(0f, 0f, cropWidth.toFloat(), cropHeight.toFloat())
        val maskPath = Path().apply {
            fillType = PathFillType.EvenOdd
            addRect(bounds)
            addOval(bounds)
        }
        croppedCanvas.drawPath(maskPath, Paint().apply { blendMode = BlendMode.Clear })
    }
    return cropped
}

/**
 * Manual erase/crop editor for a freshly picked species photo (`.claude/plans/user-mushrooms.md`,
 * Phase 3) — opened by [SpeciesFormDialog] between picking a photo and encoding the final icon.
 * Isolated by design: takes a photo path in, produces the finished icon's PNG bytes and preview
 * bitmap out, and knows nothing about `Category`.
 *
 * Erasing composites strokes with `BlendMode.Clear` inside a
 * `Modifier.graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)` layer, so clearing
 * only ever punches through the photo itself, never the checkerboard drawn behind it or the crop
 * scrim drawn on top. Strokes are kept as raw point lists (not committed into a raster) for the
 * whole session — undo/redo is just popping/pushing list entries. If this turns out to visibly lag
 * on a real device with a very long erase session, the mitigation the plan already anticipated is
 * baking older strokes into a raster every N strokes; not done upfront since it wasn't needed to
 * make the feature work.
 */
@Composable
fun IconEditorDialog(
    sourcePath: String,
    onDone: (pngBytes: ByteArray, preview: ImageBitmap) -> Unit,
    onCancel: () -> Unit,
) {
    var workingBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var decodeFailed by remember { mutableStateOf(false) }
    LaunchedEffect(sourcePath) {
        val decoded = decodeScaledImage(sourcePath, EDITOR_IMAGE_MAX_DIMENSION)
        if (decoded == null) decodeFailed = true else workingBitmap = decoded
    }
    LaunchedEffect(decodeFailed) { if (decodeFailed) onCancel() }

    var activeTool by remember { mutableStateOf(EditorTool.ERASER) }
    var brushWidthFraction by remember { mutableStateOf(DEFAULT_BRUSH_FRACTION) }
    val strokes = remember { mutableStateListOf<EraseStroke>() }
    val redoStack = remember { mutableStateListOf<EraseStroke>() }
    var currentPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var cropRect by remember { mutableStateOf(CropRect.Full) }
    var cropShape by remember { mutableStateOf(CropShape.RECTANGLE) }
    var cropDrag by remember { mutableStateOf<CropDrag?>(null) }
    // Raw px position (in the photo box's own coordinate space, not fraction) of the finger while
    // actively erasing — drives the magnifier loupe below. Cleared once the drag ends, since the
    // loupe is only useful while a stroke is actually being placed.
    var magnifierAnchor by remember { mutableStateOf<Offset?>(null) }
    var photoBoxSize by remember { mutableStateOf(IntSize.Zero) }

    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = true),
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(StringKey.SpeciesFormCancelContentDescription),
                        )
                    }
                    Text(
                        text = stringResource(StringKey.IconEditorTitle),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(
                        onClick = {
                            val bitmap = workingBitmap ?: return@IconButton
                            val edited = renderErasedAndCropped(bitmap, strokes, cropRect, cropShape)
                                .scaledToMaxDimension(CATEGORY_ICON_MAX_DIMENSION)
                            val bytes = encodePng(edited)
                            if (bytes != null) onDone(bytes, edited)
                        },
                        enabled = workingBitmap != null,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = stringResource(StringKey.IconEditorDoneContentDescription),
                        )
                    }
                }

                // Fixed-size top bar above, fixed-size tool controls below — this middle box takes
                // whatever space is left between them and never more, so both stay reachable no
                // matter the aspect ratio of the available space (landscape included, where the
                // previous `fillMaxWidth().aspectRatio(...)` sizing could make the photo area taller
                // than the screen and push the controls out of reach with no way to scroll to them).
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    val bitmap = workingBitmap
                    if (bitmap == null) {
                        CircularProgressIndicator()
                    } else {
                        val bitmapAspect = bitmap.width.toFloat() / bitmap.height.toFloat()
                        BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            // Fit the photo's own aspect ratio inside whatever space this box was
                            // actually given, constrained by whichever of width/height is tighter.
                            val availableAspect = maxWidth.value / maxHeight.value
                            val fitWidth = if (bitmapAspect > availableAspect) maxWidth else maxHeight * bitmapAspect
                            val fitHeight = if (bitmapAspect > availableAspect) maxWidth / bitmapAspect else maxHeight
                            // Outer box is deliberately NOT clipped — the magnifier loupe below
                            // floats above the finger and would get cut off by the inner box's
                            // rounded corners if it lived inside the clipped layer.
                            Box(
                                modifier = Modifier
                                    .width(fitWidth)
                                    .height(fitHeight)
                                    .onSizeChanged { photoBoxSize = it },
                            ) {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clip(RoundedCornerShape(12.dp)),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .checkerboardBackground(),
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                                            .drawWithContent {
                                                drawContent()
                                                val maxDimension = max(size.width, size.height)
                                                val liveStroke = if (currentPoints.size >= 2) {
                                                    EraseStroke(currentPoints, brushWidthFraction)
                                                } else {
                                                    null
                                                }
                                                val allStrokes = if (liveStroke != null) {
                                                    strokes + liveStroke
                                                } else {
                                                    strokes
                                                }
                                                for (stroke in allStrokes) {
                                                    drawPath(
                                                        path = stroke.toPath(size.width, size.height),
                                                        color = Color.Black,
                                                        style = Stroke(
                                                            width = stroke.widthFraction * maxDimension,
                                                            cap = StrokeCap.Round,
                                                            join = StrokeJoin.Round,
                                                        ),
                                                        blendMode = BlendMode.Clear,
                                                    )
                                                }
                                            },
                                    ) {
                                        Image(
                                            bitmap = bitmap,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.FillBounds,
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .pointerInputEditor(
                                                activeTool = activeTool,
                                                onEraseStart = { point, size ->
                                                    currentPoints =
                                                        listOf(point.toFraction(size), point.toFraction(size))
                                                    magnifierAnchor = point
                                                },
                                                onEraseDrag = { point, size ->
                                                    currentPoints = currentPoints + point.toFraction(size)
                                                    magnifierAnchor = point
                                                },
                                                onEraseEnd = {
                                                    if (currentPoints.size >= 2) {
                                                        strokes.add(EraseStroke(currentPoints, brushWidthFraction))
                                                        redoStack.clear()
                                                    }
                                                    currentPoints = emptyList()
                                                    magnifierAnchor = null
                                                },
                                                onFrameStart = { offset, size, handleRadiusPx ->
                                                    cropDrag =
                                                        resolveCropDrag(offset, size, cropRect, handleRadiusPx)
                                                },
                                                onFrameDrag = { offset, size ->
                                                    cropRect = applyCropDrag(cropDrag, offset, size, cropRect)
                                                },
                                                onFrameEnd = { cropDrag = null },
                                            )
                                            .drawWithContent {
                                                drawContent()
                                                if (activeTool == EditorTool.CROP) {
                                                    drawFrameOverlay(cropRect, cropShape)
                                                }
                                            },
                                    )
                                }

                                val anchor = magnifierAnchor
                                if (activeTool == EditorTool.ERASER &&
                                    anchor != null &&
                                    photoBoxSize != IntSize.Zero
                                ) {
                                    val liveStroke = if (currentPoints.size >= 2) {
                                        EraseStroke(currentPoints, brushWidthFraction)
                                    } else {
                                        null
                                    }
                                    MagnifierLoupe(
                                        bitmap = bitmap,
                                        strokes = strokes,
                                        liveStroke = liveStroke,
                                        anchor = anchor,
                                        boxSize = photoBoxSize,
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    EditorTool.entries.forEachIndexed { index, tool ->
                        SegmentedButton(
                            selected = activeTool == tool,
                            onClick = {
                                activeTool = tool
                                // First-ever entry into Crop: swap the invisible full-frame rect
                                // for a visible, draggable one. Leaves it alone on later re-entries
                                // so an already-adjusted frame isn't reset by switching tools.
                                if (tool == EditorTool.CROP && cropRect == CropRect.Full) {
                                    cropRect = CropRect.Default
                                }
                            },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = EditorTool.entries.size),
                        ) {
                            Text(
                                stringResource(
                                    when (tool) {
                                        EditorTool.ERASER -> StringKey.IconEditorToolEraser
                                        EditorTool.CROP -> StringKey.IconEditorToolCrop
                                    },
                                ),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                if (activeTool == EditorTool.ERASER) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                            IconButton(onClick = { strokes.removeLastOrNull()?.let { redoStack.add(it) } }, enabled = strokes.isNotEmpty()) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Undo,
                                    contentDescription = stringResource(StringKey.IconEditorUndoContentDescription),
                                )
                            }
                            IconButton(onClick = { redoStack.removeLastOrNull()?.let { strokes.add(it) } }, enabled = redoStack.isNotEmpty()) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Redo,
                                    contentDescription = stringResource(StringKey.IconEditorRedoContentDescription),
                                )
                            }
                        }
                        Text(stringResource(StringKey.IconEditorBrushSizeLabel))
                        Slider(
                            value = brushWidthFraction,
                            onValueChange = { brushWidthFraction = it },
                            valueRange = MIN_BRUSH_FRACTION..MAX_BRUSH_FRACTION,
                            modifier = Modifier.weight(1f),
                        )
                    }
                } else {
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        CropShape.entries.forEachIndexed { index, shape ->
                            SegmentedButton(
                                selected = cropShape == shape,
                                onClick = { cropShape = shape },
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = CropShape.entries.size),
                            ) {
                                Text(
                                    stringResource(
                                        when (shape) {
                                            CropShape.RECTANGLE -> StringKey.IconEditorShapeRectangle
                                            CropShape.OVAL -> StringKey.IconEditorShapeOval
                                        },
                                    ),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Floating circular zoom preview shown above the finger while actively erasing (a small photo may
 * need edits finer than a fingertip can aim at directly). Not a screenshot of the main canvas —
 * cheaper: crops a small square out of [bitmap] itself around [anchor] via `drawImage`'s src/dst
 * rects, then re-draws [strokes]/[liveStroke] scaled into that same cropped-and-zoomed coordinate
 * space with the same `BlendMode.Clear` mechanism as the main canvas, so the loupe shows the photo
 * exactly as erased so far, not the untouched original.
 */
@Composable
private fun MagnifierLoupe(
    bitmap: ImageBitmap,
    strokes: List<EraseStroke>,
    liveStroke: EraseStroke?,
    anchor: Offset,
    boxSize: IntSize,
) {
    Box(
        modifier = Modifier
            .offset {
                val diameterPx = MAGNIFIER_DIAMETER.toPx()
                IntOffset(
                    (anchor.x - diameterPx / 2).roundToInt(),
                    (anchor.y - diameterPx - MAGNIFIER_VERTICAL_GAP.toPx()).roundToInt(),
                )
            }
            .size(MAGNIFIER_DIAMETER)
            .clip(CircleShape)
            .border(2.dp, Color.White, CircleShape),
    ) {
        // Checkerboard and photo+strokes MUST be separate sibling layers, not one chained onto the
        // other — chaining `checkerboardBackground()`'s own `drawWithContent` directly onto this
        // box's photo-drawing `drawWithContent` painted the checker tiles *after* `drawContent()`,
        // i.e. on top of the opaque photo everywhere, not only behind its transparent parts. As two
        // siblings (same structure as the main canvas), the checkerboard is simply composited first
        // and the offscreen photo+erase layer normally covers it wherever it's opaque, showing
        // through only where actually erased. Same tile size as the main canvas — it's a screen-
        // space overlay, not something that zooms with the sampled photo region, so there's no
        // reason for it to look different here.
        Box(modifier = Modifier.matchParentSize().checkerboardBackground())
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                .drawWithContent {
                    drawContent()
                    val maxBitmapDimension = max(bitmap.width, bitmap.height).toFloat()
                    val sourceSide = (maxBitmapDimension * MAGNIFIER_SOURCE_FRACTION)
                        .coerceAtMost(min(bitmap.width, bitmap.height).toFloat())
                    val centerX = (anchor.x / boxSize.width).coerceIn(0f, 1f) * bitmap.width
                    val centerY = (anchor.y / boxSize.height).coerceIn(0f, 1f) * bitmap.height
                    val srcLeft = (centerX - sourceSide / 2).coerceIn(0f, bitmap.width - sourceSide)
                    val srcTop = (centerY - sourceSide / 2).coerceIn(0f, bitmap.height - sourceSide)
                    val zoom = size.width / sourceSide
                    drawImage(
                        image = bitmap,
                        srcOffset = IntOffset(srcLeft.roundToInt(), srcTop.roundToInt()),
                        srcSize = IntSize(sourceSide.roundToInt(), sourceSide.roundToInt()),
                        dstOffset = IntOffset.Zero,
                        dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt()),
                    )
                    val allStrokes = if (liveStroke != null) strokes + liveStroke else strokes
                    for (stroke in allStrokes) {
                        val path = Path()
                        stroke.points.forEachIndexed { index, point ->
                            val local = Offset(
                                (point.x * bitmap.width - srcLeft) * zoom,
                                (point.y * bitmap.height - srcTop) * zoom,
                            )
                            if (index == 0) path.moveTo(local.x, local.y) else path.lineTo(local.x, local.y)
                        }
                        drawPath(
                            path = path,
                            color = Color.Black,
                            style = Stroke(
                                width = stroke.widthFraction * maxBitmapDimension * zoom,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round,
                            ),
                            blendMode = BlendMode.Clear,
                        )
                    }
                },
        )
    }
}

private fun Offset.toFraction(size: IntSize): Offset =
    Offset((x / size.width).coerceIn(0f, 1f), (y / size.height).coerceIn(0f, 1f))

private fun Modifier.checkerboardBackground(): Modifier =
    this.background(CHECKER_LIGHT).drawWithContent {
        drawContent()
        val tilePx = CHECKER_TILE.toPx()
        var y = 0f
        var row = 0
        while (y < size.height) {
            var x = 0f
            var col = row
            while (x < size.width) {
                if (col % 2 != 0) {
                    drawRect(
                        color = CHECKER_DARK,
                        topLeft = Offset(x, y),
                        size = Size(
                            min(tilePx, size.width - x),
                            min(tilePx, size.height - y),
                        ),
                    )
                }
                x += tilePx
                col++
            }
            y += tilePx
            row++
        }
    }

private fun ContentDrawScope.drawFrameOverlay(crop: CropRect, shape: CropShape) {
    when (shape) {
        CropShape.RECTANGLE -> drawRectangularFrameOverlay(crop)
        CropShape.OVAL -> drawOvalFrameOverlay(crop)
    }
}

private fun ContentDrawScope.drawRectangularFrameOverlay(crop: CropRect) {
    val leftPx = crop.left * size.width
    val topPx = crop.top * size.height
    val rightPx = crop.right * size.width
    val bottomPx = crop.bottom * size.height
    val scrim = Color.Black.copy(alpha = 0.55f)
    drawRect(scrim, topLeft = Offset.Zero, size = Size(size.width, topPx))
    drawRect(scrim, topLeft = Offset(0f, bottomPx), size = Size(size.width, size.height - bottomPx))
    drawRect(scrim, topLeft = Offset(0f, topPx), size = Size(leftPx, bottomPx - topPx))
    drawRect(scrim, topLeft = Offset(rightPx, topPx), size = Size(size.width - rightPx, bottomPx - topPx))
    drawRect(
        color = Color.White,
        topLeft = Offset(leftPx, topPx),
        size = Size(rightPx - leftPx, bottomPx - topPx),
        style = Stroke(width = 2.dp.toPx()),
    )
    val handleRadius = CROP_HANDLE_DRAW_RADIUS.toPx()
    for (corner in listOf(Offset(leftPx, topPx), Offset(rightPx, topPx), Offset(leftPx, bottomPx), Offset(rightPx, bottomPx))) {
        drawCircle(color = Color.White, radius = handleRadius, center = corner)
    }
}

/**
 * Same draggable bounding box as [drawRectangularFrameOverlay], but the scrim/outline follow the
 * oval inscribed in that box rather than the box itself — an even-odd-filled outer-canvas-minus-oval
 * [Path] darkens everything outside the oval (including the bounding box's own corners) in one pass.
 * Corner handles still sit on the rectangle's corners since dragging always resizes that same
 * rectangle; only the visible/exported shape is the oval within it.
 */
private fun ContentDrawScope.drawOvalFrameOverlay(crop: CropRect) {
    val leftPx = crop.left * size.width
    val topPx = crop.top * size.height
    val rightPx = crop.right * size.width
    val bottomPx = crop.bottom * size.height
    val scrim = Color.Black.copy(alpha = 0.55f)
    val ovalBounds = Rect(leftPx, topPx, rightPx, bottomPx)
    val scrimPath = Path().apply {
        fillType = PathFillType.EvenOdd
        addRect(Rect(Offset.Zero, size))
        addOval(ovalBounds)
    }
    drawPath(scrimPath, color = scrim)
    drawOval(
        color = Color.White,
        topLeft = ovalBounds.topLeft,
        size = ovalBounds.size,
        style = Stroke(width = 2.dp.toPx()),
    )
    val handleRadius = CROP_HANDLE_DRAW_RADIUS.toPx()
    for (corner in listOf(Offset(leftPx, topPx), Offset(rightPx, topPx), Offset(leftPx, bottomPx), Offset(rightPx, bottomPx))) {
        drawCircle(color = Color.White, radius = handleRadius, center = corner)
    }
}

/**
 * Single `pointerInput` block shared by both tools (keyed on [activeTool] so switching tools tears
 * down and rebuilds gesture detection): eraser drag callbacks build/commit a stroke, frame drag
 * callbacks resize/move [CropRect] via [resolveCropDrag]/[applyCropDrag] — [CropShape] only affects
 * how that same bounding box is drawn/exported, not how it's dragged.
 */
private fun Modifier.pointerInputEditor(
    activeTool: EditorTool,
    onEraseStart: (Offset, IntSize) -> Unit,
    onEraseDrag: (Offset, IntSize) -> Unit,
    onEraseEnd: () -> Unit,
    onFrameStart: (Offset, IntSize, Float) -> Unit,
    onFrameDrag: (Offset, IntSize) -> Unit,
    onFrameEnd: () -> Unit,
): Modifier = this.then(
    Modifier.pointerInput(activeTool) {
        val handleRadiusPx = CROP_HANDLE_TOUCH_RADIUS.toPx()
        when (activeTool) {
            EditorTool.ERASER -> detectDragGestures(
                onDragStart = { offset -> onEraseStart(offset, size) },
                onDrag = { change, _ -> change.consume(); onEraseDrag(change.position, size) },
                onDragEnd = onEraseEnd,
                onDragCancel = onEraseEnd,
            )
            EditorTool.CROP -> detectDragGestures(
                onDragStart = { offset -> onFrameStart(offset, size, handleRadiusPx) },
                onDrag = { change, _ -> change.consume(); onFrameDrag(change.position, size) },
                onDragEnd = onFrameEnd,
                onDragCancel = onFrameEnd,
            )
        }
    },
)
