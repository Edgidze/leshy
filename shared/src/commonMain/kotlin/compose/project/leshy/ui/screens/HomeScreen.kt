package compose.project.leshy.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.ui.components.HomeNavButton
import compose.project.leshy.ui.navigation.Destination
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.leshy_logo
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeScreen(onNavigate: (Destination) -> Unit) {
    Scaffold(
        topBar = {
            Column {
                HomeHeader()
                // Compose's real elevation shadow spreads out and gets *paler* the higher it
                // goes (a physically-lit shadow from something floating higher is bigger and
                // softer, not darker) — the opposite of what "more raised than the buttons"
                // needs to read as. A hand-drawn gradient gives a shadow strength we control
                // directly instead of fighting the elevation API for it.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(HOME_HEADER_SHADOW_HEIGHT)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(HOME_HEADER_SHADOW_COLOR, Color.Transparent),
                            ),
                        ),
                )
            }
        },
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            HomeNavButton(
                label = StringKey.NavRecord,
                icon = Icons.Filled.Hiking,
                onClick = { onNavigate(Destination.Record) },
            )
            HomeNavButton(
                label = StringKey.NavArchive,
                icon = Icons.AutoMirrored.Filled.List,
                onClick = { onNavigate(Destination.Archive) },
            )
            HomeNavButton(
                label = StringKey.NavMap,
                icon = Icons.Filled.Place,
                onClick = { onNavigate(Destination.Map) },
            )
            HomeNavButton(
                label = StringKey.SettingsTitle,
                icon = Icons.Filled.Settings,
                onClick = { onNavigate(Destination.Settings) },
            )
            // Export/import of recorded data — not implemented yet, tapping does nothing.
            HomeNavButton(
                label = StringKey.NavData,
                icon = Icons.Filled.ImportExport,
                onClick = {},
            )
        }
    }
}

private val HOME_HEADER_HEIGHT = 88.dp
private val HOME_HEADER_LOGO_SIZE = 60.dp
private val HOME_HEADER_SHADOW_HEIGHT = 20.dp

// Noticeably darker at its peak than a HomeNavButton's own (subtle, ambient) shadow, so the
// header reads as sitting higher above the page than the buttons do.
private val HOME_HEADER_SHADOW_COLOR = Color.Black.copy(alpha = 0.35f)

@Composable
private fun HomeHeader() {
    // A real TopAppBar (same component SectionScaffold uses) so the header sits at the exact
    // same level as every other screen's app bar, instead of a custom Card that ignored the
    // status-bar inset TopAppBar applies automatically — just taller and darker than section
    // bars/button tiles to read as the page's own distinct header.
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(StringKey.AppName),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            )
        },
        navigationIcon = {
            Image(
                painter = painterResource(Res.drawable.leshy_logo),
                contentDescription = null,
                modifier = Modifier.padding(start = 12.dp).size(HOME_HEADER_LOGO_SIZE),
            )
        },
        actions = {
            Image(
                painter = painterResource(Res.drawable.leshy_logo),
                contentDescription = null,
                modifier = Modifier.padding(end = 12.dp).size(HOME_HEADER_LOGO_SIZE),
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        expandedHeight = HOME_HEADER_HEIGHT,
    )
}
