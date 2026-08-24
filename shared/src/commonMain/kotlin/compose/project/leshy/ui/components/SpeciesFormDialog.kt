package compose.project.leshy.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import compose.project.leshy.data.platform.PhotoStorage
import compose.project.leshy.data.platform.rememberCameraLauncher
import compose.project.leshy.data.platform.rememberCameraPermissionRequester
import compose.project.leshy.data.platform.rememberGalleryPicker
import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.CategorySource
import compose.project.leshy.domain.repository.CategoryRepository
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.ui.util.colorToHex
import compose.project.leshy.ui.util.hueOf
import compose.project.leshy.ui.util.parseHexColor
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import leshy.shared.generated.resources.Res
import okio.FileSystem
import okio.Path.Companion.toPath
import org.koin.compose.koinInject

private val PHOTO_PREVIEW_SIZE = 96.dp

// Reused (overwritten, not uniquely named) scratch file for reopening a photo that was already
// edited this session but not saved yet — [IconEditorDialog] only takes a source *path*, so the
// in-memory pending PNG bytes need a file to reopen from. Only one `SpeciesFormDialog` is ever open
// at a time (plain `Dialog` overlay, never a nav route), so a fixed name can't collide.
private const val ICON_EDIT_REOPEN_SCRATCH_FILE = "species_form_icon_edit_scratch.png"

// Scratch copy of a catalog illustration picked via the "Картинки" button — [IconEditorDialog]
// only takes a source *path* (see above), but a catalog image lives as a composeResources drawable,
// not a file. Reused/overwritten the same way as [ICON_EDIT_REOPEN_SCRATCH_FILE] and for the same
// reason (only one `SpeciesFormDialog` open at a time).
private const val CATALOG_PICK_SCRATCH_FILE = "species_form_catalog_pick_scratch.webp"
private val COLOR_SWATCH_SIZE = 40.dp
private val SPECTRUM_TRACK_HEIGHT = 28.dp
private val SPECTRUM_THUMB_SIZE = 28.dp

// Fixed saturation/value for the spectrum slider (`SpeciesFormDialog`'s color picker) — only hue
// varies, so every pickable color stays a legible, similarly-weighted marker/tile accent instead
// of running into washed-out (low S) or blinding (S=V=1) extremes at some positions on the track.
private const val SPECIES_COLOR_SATURATION = 0.75f
private const val SPECIES_COLOR_VALUE = 0.62f
private const val DEFAULT_SPECIES_HUE = 30f

private val SPECTRUM_GRADIENT_BRUSH = Brush.horizontalGradient(
    colors = (0..12).map { Color.hsv(it * 30f, SPECIES_COLOR_SATURATION, SPECIES_COLOR_VALUE) },
)

/**
 * Cheap dominant-hue estimate over the already-downscaled, already-edited icon bitmap
 * [IconEditorDialog] hands back — one pass, no libraries: bucket every pixel's hue into 10°-wide
 * bins weighted by saturation, skip near-gray/near-black/near-white pixels (background/shadow/highlight, not the
 * mushroom's own color), return the bucket with the most weight. `null` if nothing passed the
 * filter (e.g. a fully desaturated photo) — caller then leaves the hue untouched.
 */
private fun dominantHue(bitmap: ImageBitmap): Float? {
    val pixels = bitmap.toPixelMap()
    val bucketWeights = FloatArray(36)
    var sawAny = false
    for (y in 0 until pixels.height) {
        for (x in 0 until pixels.width) {
            val color = pixels[x, y]
            val max = maxOf(color.red, color.green, color.blue)
            val min = minOf(color.red, color.green, color.blue)
            val saturation = if (max == 0f) 0f else (max - min) / max
            if (saturation < 0.15f || max < 0.12f || max > 0.97f) continue
            val bucket = (hueOf(color) / 10f).toInt().coerceIn(0, 35)
            bucketWeights[bucket] += saturation
            sawAny = true
        }
    }
    if (!sawAny) return null
    return bucketWeights.indices.maxBy { bucketWeights[it] } * 10f + 5f
}

/**
 * Shared create/edit form for a user species (`.claude/plans/user-mushrooms.md`, Phase 4) — used
 * both by the "Грибы" section's "Добавить гриб" button/list-row edit and by the same-named tile at
 * the end of Record's feed. Renders as a plain [Dialog] overlay, same shape as
 * [MushroomSearchDialog]/`MushroomBulkAddDialog` in `RecordScreen.kt` — never a navigation route, so
 * opening it from Record never leaves the walk in progress.
 *
 * A picked photo opens [IconEditorDialog] (Phase 3 of the plan) before it becomes the pending icon —
 * this dialog only holds the already-edited PNG bytes and its preview bitmap, never a raw photo
 * path. A photo is optional: [CategoryIcon] already renders a species with neither `iconFile` nor
 * `iconRef` as empty space, so skipping it doesn't break any layout downstream.
 */
@Composable
fun SpeciesFormDialog(
    existing: Category?,
    language: AppLanguage,
    onSave: (
        name: String,
        scientificNameInput: String?,
        colorHex: String,
        iconPngBytes: ByteArray?,
    ) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var name by remember { mutableStateOf(existing?.customNames?.get(language).orEmpty()) }
    var scientificName by remember { mutableStateOf(existing?.scientificName.orEmpty()) }
    var hue by remember { mutableStateOf(existing?.colorHex?.let { hueOf(parseHexColor(it)) } ?: DEFAULT_SPECIES_HUE) }
    // An explicitly picked/edited color (existing species, or the slider already touched this
    // session) must never be silently overwritten by a new photo's auto-detected hue.
    var hueManuallySet by remember { mutableStateOf(existing != null) }
    val colorHex = colorToHex(Color.hsv(hue, SPECIES_COLOR_SATURATION, SPECIES_COLOR_VALUE))
    var pendingIconPreview by remember { mutableStateOf<ImageBitmap?>(null) }
    var pendingIconBytes by remember { mutableStateOf<ByteArray?>(null) }
    var editorSourcePath by remember { mutableStateOf<String?>(null) }
    val photoStorage = koinInject<PhotoStorage>()
    val categoryRepository = koinInject<CategoryRepository>()
    val coroutineScope = rememberCoroutineScope()
    var showCatalogPicker by remember { mutableStateOf(false) }
    val catalogCategories by remember(categoryRepository) {
        categoryRepository.observeAll().map { list -> list.filter { it.source == CategorySource.APP && it.iconRef != null } }
    }.collectAsState(initial = emptyList())

    val takePhoto = rememberCameraLauncher { path -> editorSourcePath = path }
    val requestPhoto = rememberCameraPermissionRequester(onGranted = takePhoto)
    val pickFromGallery = rememberGalleryPicker { path -> editorSourcePath = path }
    val pickFromCatalog: () -> Unit = { showCatalogPicker = true }

    if (showCatalogPicker) {
        CatalogPhotoPickerDialog(
            categories = catalogCategories,
            onSelect = { picked ->
                showCatalogPicker = false
                val iconRef = picked.iconRef ?: return@CatalogPhotoPickerDialog
                coroutineScope.launch {
                    val bytes = Res.readBytes("drawable/$iconRef.webp")
                    val path = photoStorage.resolvePath(CATALOG_PICK_SCRATCH_FILE)
                    FileSystem.SYSTEM.write(path.toPath()) { write(bytes) }
                    editorSourcePath = path
                }
            },
            onDismissRequest = { showCatalogPicker = false },
        )
    }

    editorSourcePath?.let { sourcePath ->
        IconEditorDialog(
            sourcePath = sourcePath,
            onDone = { bytes, preview ->
                pendingIconBytes = bytes
                pendingIconPreview = preview
                if (!hueManuallySet) {
                    dominantHue(preview)?.let { hue = it }
                }
                editorSourcePath = null
            },
            onCancel = { editorSourcePath = null },
        )
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = true),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.92f),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 4.dp,
        ) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDismissRequest) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(StringKey.SpeciesFormCancelContentDescription),
                        )
                    }
                    Text(
                        text = stringResource(
                            if (existing == null) StringKey.SpeciesFormTitleCreate else StringKey.SpeciesFormTitleEdit,
                        ),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                val canReopenEditor = pendingIconPreview != null || existing?.iconFile != null
                Box(
                    modifier = Modifier
                        .size(PHOTO_PREVIEW_SIZE)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable(enabled = canReopenEditor) {
                            val bytes = pendingIconBytes
                            val existingIconFile = existing?.iconFile
                            editorSourcePath = when {
                                bytes != null -> {
                                    val path = photoStorage.resolvePath(ICON_EDIT_REOPEN_SCRATCH_FILE)
                                    FileSystem.SYSTEM.write(path.toPath()) { write(bytes) }
                                    path
                                }
                                existingIconFile != null -> photoStorage.resolvePath(existingIconFile)
                                else -> null
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    val preview = pendingIconPreview
                    if (preview != null) {
                        Image(
                            bitmap = preview,
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                            contentScale = ContentScale.Fit,
                        )
                    } else if (existing != null) {
                        CategoryIcon(category = existing, modifier = Modifier.fillMaxWidth().aspectRatio(1f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(onClick = requestPhoto) {
                        Text(stringResource(StringKey.SpeciesFormTakePhotoButton))
                    }
                    OutlinedButton(onClick = pickFromGallery) {
                        Text(stringResource(StringKey.SpeciesFormPickPhotoButton))
                    }
                    OutlinedButton(onClick = pickFromCatalog) {
                        Text(stringResource(StringKey.SpeciesFormPickCatalogButton))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(StringKey.SpeciesFormNameHint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = scientificName,
                    onValueChange = { scientificName = it },
                    label = { Text(stringResource(StringKey.SpeciesFormScientificNameHint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(StringKey.SpeciesFormColorLabel))
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(COLOR_SWATCH_SIZE)
                            .clip(CircleShape)
                            .background(parseHexColor(colorHex))
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                    )
                    Slider(
                        value = hue,
                        onValueChange = {
                            hue = it
                            hueManuallySet = true
                        },
                        valueRange = 0f..360f,
                        modifier = Modifier.weight(1f),
                        track = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(SPECTRUM_TRACK_HEIGHT)
                                    .clip(RoundedCornerShape(SPECTRUM_TRACK_HEIGHT / 2))
                                    .background(SPECTRUM_GRADIENT_BRUSH),
                            )
                        },
                        thumb = {
                            Box(
                                modifier = Modifier
                                    .size(SPECTRUM_THUMB_SIZE)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .padding(3.dp)
                                    .clip(CircleShape)
                                    .background(parseHexColor(colorHex)),
                            )
                        },
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        onSave(name.trim(), scientificName.trim().ifBlank { null }, colorHex, pendingIconBytes)
                        onDismissRequest()
                    },
                    enabled = name.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(StringKey.SpeciesFormSaveButton))
                }
            }
        }
    }
}
