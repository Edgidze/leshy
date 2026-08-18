package compose.project.leshy.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.MUSHROOM_MARKER_SIZE_SCALE_MAX
import compose.project.leshy.domain.model.MUSHROOM_MARKER_SIZE_SCALE_MIN
import compose.project.leshy.domain.model.MushroomSortOrder
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.categoryDisplayName
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.presentation.settings.SettingsViewModel
import compose.project.leshy.ui.map.MUSHROOM_MARKER_BASE_SIZE
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.allDrawableResources
import leshy.shared.generated.resources.leshy_logo
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(modifier: Modifier = Modifier, viewModel: SettingsViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
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
    )

    val drawable = previewCategory?.iconRef?.let { Res.allDrawableResources[it] }
    if (previewCategory != null && drawable != null) {
        Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(drawable),
                contentDescription = categoryDisplayName(previewCategory.nameKey),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(MUSHROOM_MARKER_BASE_SIZE * sliderValue),
            )
        }
    }
}
