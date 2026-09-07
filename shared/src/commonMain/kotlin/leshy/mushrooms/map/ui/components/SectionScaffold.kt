package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.i18n.HelpTopic
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.stringResource

/**
 * Top bar shared by every top-level section (the side-drawer entries) — hamburger on the left, `?`
 * on the right. [help] is what the `?` opens: the section's own instructions, block by block
 * ([HelpTopic]), on [leshy.mushrooms.map.ui.screens.HelpScreen].
 *
 * The `?` navigates instead of opening a dialog here (it used to raise an `AlertDialog` with three
 * paragraphs of prose) — the reasoning is in `HelpScreen`'s own doc comment. Navigating is the
 * caller's business, hence [onHelpClick]: this composable has no `NavHostController` and is not
 * about to grow one, see the incidents in `ui/navigation/CLAUDE.md`.
 */
@Composable
fun SectionScaffold(
    title: StringKey,
    help: HelpTopic,
    onMenuClick: () -> Unit,
    onHelpClick: (HelpTopic) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
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
                    IconButton(onClick = { onHelpClick(help) }) {
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
}
