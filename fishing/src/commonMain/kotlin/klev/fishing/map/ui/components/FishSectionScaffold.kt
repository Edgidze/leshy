package klev.fishing.map.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import klev.fishing.map.i18n.FishStringKey
import klev.fishing.map.i18n.fishStringResource

/**
 * Шапка раздела: название и кнопка-гамбургер. Одна на все разделы — чтобы «где я» читалось на
 * любом экране, чего у прототипа с нижней панелью не было вовсе.
 *
 * **[onMenuClick] только открывает панель и ничего не навигирует.** У грибов явная кнопка «домой»
 * в общей шапке однажды ходила `popBackStack` без `saveState` и убивала ViewModel активной
 * записи — прогулка оставалась в архиве незакрытой. Поэтому общая шапка здесь, как и там, не знает
 * никакого `NavHostController`: соблазну навигировать из неё не из чего возникнуть.
 *
 * @param snackbarHostState нужен разделам, которые сообщают о сделанном («Окунь записан»,
 *   «Отменить») — снэкбар обязан жить в `Scaffold`, иначе он не поднимется над нижними кнопками.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FishSectionScaffold(
    title: FishStringKey,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                title = {
                    Text(
                        text = fishStringResource(title),
                        style = MaterialTheme.typography.titleLarge,
                        // Две строки, не одна: названия разделов в других языках длиннее русских, а
                        // обрывать имя раздела многоточием некрасиво и незачем — место есть.
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = fishStringResource(FishStringKey.NavMenu),
                            // 36dp, как у грибной шапки: кнопка открытия меню — самая частая цель
                            // на экране, и Material-дефолт 24dp для неё мелковат.
                            modifier = Modifier.size(36.dp),
                        )
                    }
                },
                actions = actions,
            )
        },
        snackbarHost = { if (snackbarHostState != null) SnackbarHost(snackbarHostState) },
        content = content,
    )
}
