package compose.project.leshy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import compose.project.leshy.data.platform.EDITOR_IMAGE_MAX_DIMENSION
import compose.project.leshy.data.platform.decodeScaledImage
import compose.project.leshy.data.platform.encodePng
import compose.project.leshy.data.platform.rememberCameraLauncher
import compose.project.leshy.data.platform.rememberCameraPermissionRequester
import compose.project.leshy.data.platform.rememberGalleryPicker
import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.EdibilityStatus
import compose.project.leshy.domain.usecase.CATEGORY_ICON_MAX_DIMENSION
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.ui.util.parseHexColor
import compose.project.leshy.ui.util.scaledToMaxDimension
import kotlinx.coroutines.launch

/** No shared color palette exists elsewhere in the project (every catalog species hardcodes its own
 * one-off hex) — this is a small curated set for the species form's color picker. */
private val SPECIES_COLOR_PALETTE = listOf(
    "#7B4DBC", "#C2185B", "#D32F2F", "#E64A19", "#F9A825",
    "#689F38", "#2E7D32", "#00796B", "#0288D1", "#303F9F",
    "#5D4037", "#616161",
)

private val PHOTO_PREVIEW_SIZE = 96.dp
private val COLOR_SWATCH_SIZE = 40.dp

/**
 * Shared create/edit form for a user species (`.claude/plans/user-mushrooms.md`, Phase 4) — used
 * both by the "Грибы" section's "Добавить гриб" button/list-row edit and by the same-named tile at
 * the end of Record's feed. Renders as a plain [Dialog] overlay, same shape as
 * [MushroomSearchDialog]/`MushroomBulkAddDialog` in `RecordScreen.kt` — never a navigation route, so
 * opening it from Record never leaves the walk in progress.
 *
 * There's no eraser/crop step yet (Phase 3 of the plan) — a picked photo is just downscaled and
 * PNG-encoded as-is, same pipeline the temporary debug button in Settings used to exercise. A photo
 * is optional: [CategoryIcon] already renders a species with neither `iconFile` nor `iconRef` as
 * empty space, so skipping it doesn't break any layout downstream.
 */
@Composable
fun SpeciesFormDialog(
    existing: Category?,
    language: AppLanguage,
    onSave: (
        name: String,
        scientificNameInput: String?,
        edibilityStatus: EdibilityStatus,
        colorHex: String,
        iconPngBytes: ByteArray?,
    ) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf(existing?.customNames?.get(language).orEmpty()) }
    var scientificName by remember { mutableStateOf(existing?.scientificName.orEmpty()) }
    var edibility by remember { mutableStateOf(existing?.edibilityStatus ?: EdibilityStatus.EDIBLE) }
    var colorHex by remember { mutableStateOf(existing?.colorHex ?: SPECIES_COLOR_PALETTE.first()) }
    var pendingPhotoPath by remember { mutableStateOf<String?>(null) }
    var pendingIconBytes by remember { mutableStateOf<ByteArray?>(null) }

    fun onPhotoPicked(path: String) {
        pendingPhotoPath = path
        scope.launch {
            val source = decodeScaledImage(path, EDITOR_IMAGE_MAX_DIMENSION) ?: return@launch
            pendingIconBytes = encodePng(source.scaledToMaxDimension(CATEGORY_ICON_MAX_DIMENSION))
        }
    }

    val takePhoto = rememberCameraLauncher(::onPhotoPicked)
    val requestPhoto = rememberCameraPermissionRequester(onGranted = takePhoto)
    val pickFromGallery = rememberGalleryPicker(::onPhotoPicked)

    val palette = remember(existing?.colorHex) {
        if (existing != null && existing.colorHex !in SPECIES_COLOR_PALETTE) {
            listOf(existing.colorHex) + SPECIES_COLOR_PALETTE
        } else {
            SPECIES_COLOR_PALETTE
        }
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
                Box(
                    modifier = Modifier
                        .size(PHOTO_PREVIEW_SIZE)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    val previewPath = pendingPhotoPath
                    if (previewPath != null) {
                        AsyncImage(
                            model = "file://$previewPath",
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                            contentScale = ContentScale.Crop,
                        )
                    } else if (existing != null) {
                        CategoryIcon(category = existing, modifier = Modifier.fillMaxWidth().aspectRatio(1f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = requestPhoto) {
                        Text(stringResource(StringKey.SpeciesFormTakePhotoButton))
                    }
                    OutlinedButton(onClick = pickFromGallery) {
                        Text(stringResource(StringKey.SpeciesFormPickPhotoButton))
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
                Text(stringResource(StringKey.SpeciesFormEdibilityLabel))
                Spacer(modifier = Modifier.height(4.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    EdibilityStatus.entries.forEachIndexed { index, status ->
                        SegmentedButton(
                            selected = edibility == status,
                            onClick = { edibility = status },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = EdibilityStatus.entries.size),
                        ) {
                            Text(
                                stringResource(
                                    when (status) {
                                        EdibilityStatus.EDIBLE -> StringKey.SpeciesFormEdibilityEdible
                                        EdibilityStatus.CONDITIONALLY_EDIBLE -> StringKey.SpeciesFormEdibilityConditionallyEdible
                                        EdibilityStatus.INEDIBLE -> StringKey.SpeciesFormEdibilityInedible
                                    },
                                ),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(StringKey.SpeciesFormColorLabel))
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(palette) { hex ->
                        val selected = hex == colorHex
                        Box(
                            modifier = Modifier
                                .size(COLOR_SWATCH_SIZE)
                                .clip(CircleShape)
                                .background(parseHexColor(hex))
                                .border(
                                    width = if (selected) 3.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    shape = CircleShape,
                                )
                                .clickable { colorHex = hex },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        onSave(name.trim(), scientificName.trim().ifBlank { null }, edibility, colorHex, pendingIconBytes)
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
