package compose.project.leshy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.project.leshy.data.platform.rememberExportFolderPicker
import compose.project.leshy.data.platform.rememberImportFilePicker
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.presentation.data.DataMode
import compose.project.leshy.presentation.data.DataUiState
import compose.project.leshy.presentation.data.DataViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DataScreen(modifier: Modifier = Modifier, viewModel: DataViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp)) {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            DataMode.entries.forEachIndexed { index, mode ->
                SegmentedButton(
                    selected = uiState.mode == mode,
                    onClick = { viewModel.setMode(mode) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = DataMode.entries.size),
                ) {
                    Text(
                        stringResource(
                            when (mode) {
                                DataMode.EXPORT -> StringKey.DataExportOption
                                DataMode.IMPORT -> StringKey.DataImportOption
                            },
                        ),
                    )
                }
            }
        }

        when (uiState.mode) {
            DataMode.EXPORT -> ExportSection(uiState, viewModel)
            DataMode.IMPORT -> ImportSection(uiState, viewModel)
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
        ) {
            // Placeholders only — export/import processing isn't implemented yet.
            OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) {
                Text(stringResource(StringKey.DataCancelButton))
            }
            Button(onClick = {}, modifier = Modifier.weight(1f)) {
                Text(stringResource(StringKey.DataDoneButton))
            }
        }
    }
}

@Composable
private fun ExportSection(uiState: DataUiState, viewModel: DataViewModel) {
    val pickFolder = rememberExportFolderPicker(onPicked = viewModel::onExportFolderPicked)

    Column(modifier = Modifier.padding(top = 24.dp)) {
        OutlinedButton(onClick = pickFolder, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.Folder, contentDescription = null)
            Text(stringResource(StringKey.DataChooseFolderButton), modifier = Modifier.padding(start = 8.dp))
        }
        Text(
            "${stringResource(StringKey.DataFolderStatusLabel)}: " +
                (uiState.exportFolderName ?: stringResource(StringKey.DataFolderNotSelected)),
            modifier = Modifier.padding(top = 8.dp),
        )

        OutlinedTextField(
            value = uiState.exportArchiveName,
            onValueChange = viewModel::setExportArchiveName,
            label = { Text(stringResource(StringKey.DataArchiveNameLabel)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        )
    }
}

@Composable
private fun ImportSection(uiState: DataUiState, viewModel: DataViewModel) {
    val pickFile = rememberImportFilePicker(onPicked = viewModel::onImportFilePicked)

    Column(modifier = Modifier.padding(top = 24.dp)) {
        OutlinedButton(onClick = pickFile, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.FileOpen, contentDescription = null)
            Text(stringResource(StringKey.DataChooseFileButton), modifier = Modifier.padding(start = 8.dp))
        }
        Text(
            "${stringResource(StringKey.DataFileStatusLabel)}: " +
                (uiState.importFileName ?: stringResource(StringKey.DataFileNotSelected)),
            modifier = Modifier.padding(top = 8.dp),
        )

        OutlinedTextField(
            value = uiState.importWalkLabel,
            onValueChange = viewModel::setImportWalkLabel,
            label = { Text(stringResource(StringKey.DataImportLabelFieldLabel)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        )
    }
}
