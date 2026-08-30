package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import leshy.mushrooms.map.i18n.HelpTopic
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.helpResource
import leshy.mushrooms.map.i18n.stringResource

/**
 * Top bar shared by every top-level section (the side-drawer entries) — hamburger on the left, `?`
 * on the right. [help] is what the `?` shows: the section's own instructions, two or three
 * paragraphs of them ([HelpTopic]), in the interface language like any other text.
 */
@Composable
fun SectionScaffold(
    title: StringKey,
    help: HelpTopic,
    onMenuClick: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    var showHelpDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(title)) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = stringResource(StringKey.NavMenuContentDescription),
                            modifier = Modifier.size(36.dp),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showHelpDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = stringResource(StringKey.HelpContentDescription),
                        )
                    }
                },
            )
        },
        content = content,
    )

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            modifier = Modifier.fillMaxWidth(0.9f),
            properties = DialogProperties(usePlatformDefaultWidth = false),
            title = { Text(stringResource(StringKey.HelpDialogTitle)) },
            text = {
                // Three paragraphs of prose overflow a phone-height dialog on most sections (and
                // all of them once a language translates longer than Russian) — Material3 caps the
                // dialog's height but does not scroll the text slot itself, so the tail would
                // simply be cut off without this.
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    help.paragraphs.forEach { paragraph -> Text(helpResource(paragraph)) }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text(stringResource(StringKey.HelpDialogDismiss))
                }
            },
        )
    }
}
