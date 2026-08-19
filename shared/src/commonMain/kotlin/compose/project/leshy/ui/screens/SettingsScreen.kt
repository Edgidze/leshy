package compose.project.leshy.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import compose.project.leshy.data.platform.EDITOR_IMAGE_MAX_DIMENSION
import compose.project.leshy.data.platform.decodeScaledImage
import compose.project.leshy.data.platform.encodePng
import compose.project.leshy.data.platform.rememberGalleryPicker
import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.MUSHROOM_MARKER_SIZE_SCALE_MAX
import compose.project.leshy.domain.model.MUSHROOM_MARKER_SIZE_SCALE_MIN
import compose.project.leshy.domain.model.MushroomSortOrder
import compose.project.leshy.domain.usecase.CATEGORY_ICON_MAX_DIMENSION
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.categoryDisplayName
import compose.project.leshy.i18n.regionsUnitLabel
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.presentation.settings.SettingsViewModel
import compose.project.leshy.ui.components.CategoryIcon
import compose.project.leshy.ui.components.CollectionPicker
import compose.project.leshy.ui.map.MUSHROOM_MARKER_BASE_SIZE
import compose.project.leshy.ui.util.scaledToMaxDimension
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.leshy_logo
import org.jetbrains.compose.resources.painterResource
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(modifier: Modifier = Modifier, viewModel: SettingsViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Image(
                painter = painterResource(Res.drawable.leshy_logo),
                contentDescription = stringResource(StringKey.AppName),
                modifier = Modifier.size(96.dp),
            )
        }

        Text(
            stringResource(StringKey.SettingsLanguageTitle),
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
        )
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            AppLanguage.entries.forEachIndexed { index, language ->
                SegmentedButton(
                    selected = uiState.language == language,
                    onClick = { viewModel.setLanguage(language) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = AppLanguage.entries.size),
                ) {
                    Text(language.displayName)
                }
            }
        }

        Text(
            stringResource(StringKey.SettingsMushroomSizeTitle),
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
        )
        MushroomMarkerSizeSlider(
            scale = uiState.mushroomMarkerSizeScale,
            previewCategory = uiState.previewCategory,
            onScaleChangeFinished = viewModel::setMushroomMarkerSizeScale,
        )

        Text(
            stringResource(StringKey.SettingsMushroomSortTitle),
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
        )
        MushroomSortOrderOptions(
            selected = uiState.mushroomSortOrder,
            onSelected = viewModel::setMushroomSortOrder,
        )
        ResetMushroomOrderOnWalkFinishOption(
            checked = uiState.resetMushroomOrderOnWalkFinish,
            onCheckedChange = viewModel::setResetMushroomOrderOnWalkFinish,
        )

        Text(
            stringResource(StringKey.SettingsCollectionsTitle),
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
        )
        CollectionPicker(
            items = uiState.collectionPickerItems,
            onToggleCollection = viewModel::toggleCollection,
            onToggleCategory = viewModel::setCategoryPicked,
        )

        Text(
            stringResource(StringKey.SettingsMapDataTitle),
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
        )
        Text(
            stringResource(StringKey.SettingsMapDataDescription),
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Button(onClick = viewModel::refreshMapData, enabled = !uiState.isRefreshingMapData) {
            if (uiState.isRefreshingMapData) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text(stringResource(StringKey.SettingsRefreshMapDataButton))
            }
        }
        if (uiState.mapDataRefreshFailed) {
            Text(
                stringResource(StringKey.SettingsMapDataRefreshError),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        if (uiState.mapDataRegionsRedownloading > 0) {
            val count = uiState.mapDataRegionsRedownloading
            Text(
                "${stringResource(StringKey.SettingsMapDataRedownloadingPrefix)} " +
                    "$count ${regionsUnitLabel(count)} " +
                    stringResource(StringKey.SettingsMapDataRedownloadingSuffix),
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        DebugUserCategoryButton(
            debugCategory = uiState.debugUserCategory,
            onClick = viewModel::toggleDebugUserCategory,
        )
        if (uiState.debugUserCategory != null) {
            DebugUserCategoryIconButton(onIconReady = viewModel::setDebugUserCategoryIcon)
        }
    }
}

/**
 * TEMPORARY (Phase 1, `.claude/plans/user-mushrooms.md`) — **delete along with
 * [compose.project.leshy.domain.usecase.DebugUserCategoryUseCase] once Phase 4 adds the real
 * "My mushrooms" section here.** Until then this is the only way to get a user-created species
 * onto a device, so the icon/name resolver this phase introduced can be seen working on the
 * Record tiles, the map markers, the Filter dialog and the walk-detail donut.
 *
 * The illustration is borrowed from the bundled catalog (Phase 2 replaces it with camera/gallery),
 * read here rather than in the ViewModel because `Res` is a UI-layer resource accessor.
 */
/**
 * TEMPORARY (Phase 2, `.claude/plans/user-mushrooms.md`) — **delete with the rest of the debug
 * block in Phase 4.** Runs the platform image path end to end without the editor screen that
 * Phase 3 adds: system gallery picker → decode downscaled to [EDITOR_IMAGE_MAX_DIMENSION] with the
 * photo's own orientation applied → plain downscale to [CATEGORY_ICON_MAX_DIMENSION] (Phase 3 puts
 * the eraser/crop here instead) → PNG → stored icon. The picture then shows up wherever the
 * species does, through the Phase 1 resolver.
 *
 * The image work sits in the UI layer on purpose: [ImageBitmap] is a Compose type, and the
 * ViewModel/domain only ever see finished PNG bytes.
 */
@Composable
private fun DebugUserCategoryIconButton(onIconReady: (ByteArray) -> Unit) {
    val scope = rememberCoroutineScope()
    val pickFromGallery = rememberGalleryPicker { path ->
        scope.launch {
            val source = decodeScaledImage(path, EDITOR_IMAGE_MAX_DIMENSION) ?: return@launch
            val png = encodePng(source.scaledToMaxDimension(CATEGORY_ICON_MAX_DIMENSION)) ?: return@launch
            onIconReady(png)
        }
    }
    Button(onClick = pickFromGallery, modifier = Modifier.padding(top = 8.dp)) {
        Text(stringResource(StringKey.SettingsDebugUserCategoryPickIcon))
    }
}

@Composable
private fun DebugUserCategoryButton(debugCategory: Category?, onClick: (ByteArray) -> Unit) {
    val scope = rememberCoroutineScope()
    Button(
        onClick = { scope.launch { onClick(Res.readBytes("drawable/amanita_muscaria.webp")) } },
        modifier = Modifier.padding(top = 24.dp),
    ) {
        Text(
            stringResource(
                when {
                    debugCategory == null -> StringKey.SettingsDebugUserCategoryCreate
                    debugCategory.isPicked -> StringKey.SettingsDebugUserCategoryHide
                    else -> StringKey.SettingsDebugUserCategoryShow
                },
            ),
        )
    }
}

@Composable
private fun ResetMushroomOrderOnWalkFinishOption(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = checked, onValueChange = onCheckedChange, role = Role.Checkbox)
            .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = checked, onCheckedChange = null)
        Text(
            text = stringResource(StringKey.SettingsResetMushroomOrderOnWalkFinish),
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
private fun MushroomSortOrderOptions(selected: MushroomSortOrder, onSelected: (MushroomSortOrder) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().selectableGroup()) {
        MushroomSortOrder.entries.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selected == option,
                        onClick = { onSelected(option) },
                        role = Role.RadioButton,
                    )
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = selected == option, onClick = null)
                Text(
                    text = stringResource(
                        when (option) {
                            MushroomSortOrder.EDIBILITY_THEN_ALPHABETICAL ->
                                StringKey.SettingsMushroomSortByEdibilityThenAlphabetical
                            MushroomSortOrder.ALPHABETICAL -> StringKey.SettingsMushroomSortByAlphabetical
                        },
                    ),
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}

/**
 * A single-thumb [Slider] plus a preview photo below it that's resized live as the thumb is
 * dragged — `sliderValue` (not [scale]) drives the preview, so it tracks the drag with zero lag;
 * [scale] only commits to the [SettingsViewModel] (and from there, `DataStore`) once the drag ends,
 * same "local slider state + `onValueChangeFinished`" pattern `MapFilterDialog`'s range sliders use.
 */
@Composable
private fun MushroomMarkerSizeSlider(
    scale: Float,
    previewCategory: Category?,
    onScaleChangeFinished: (Float) -> Unit,
) {
    var sliderValue by remember(scale) { mutableStateOf(scale) }

    Slider(
        value = sliderValue,
        onValueChange = { sliderValue = it },
        valueRange = MUSHROOM_MARKER_SIZE_SCALE_MIN..MUSHROOM_MARKER_SIZE_SCALE_MAX,
        onValueChangeFinished = { onScaleChangeFinished(sliderValue) },
        modifier = Modifier.fillMaxWidth(),
        // Material3's current default track draws a gap around the thumb plus a stop-indicator
        // dot at the end — reads as a broken/disconnected track for a plain 0..1 scale slider
        // with no steps, so both are turned off for a classic continuous look.
        track = { sliderState ->
            SliderDefaults.Track(sliderState = sliderState, drawStopIndicator = null, thumbTrackGapSize = 0.dp)
        },
    )

    if (previewCategory != null) {
        Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), contentAlignment = Alignment.Center) {
            CategoryIcon(
                category = previewCategory,
                modifier = Modifier.size(MUSHROOM_MARKER_BASE_SIZE * sliderValue),
                contentDescription = categoryDisplayName(previewCategory),
            )
        }
    }
}
