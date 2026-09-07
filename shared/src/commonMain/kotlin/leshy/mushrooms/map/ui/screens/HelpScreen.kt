package leshy.mushrooms.map.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.i18n.HelpTopic
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.helpResource
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.ui.components.HelpIllustration

/**
 * Справка по одному разделу: то, что открывает кнопка «?» в шапке любого раздела
 * ([leshy.mushrooms.map.ui.components.SectionScaffold]).
 *
 * **Экран, а не диалог — намеренно.** Справка состоит из блоков «картинка элемента интерфейса плюс
 * пара предложений про него» (`HelpKey`), и таких блоков у раздела пять-восемь. В диалоге
 * `AlertDialog` — а именно им справка была раньше — они жались в 90% ширины и в неполную высоту
 * телефона, то есть картинки выходили с ноготь, а прокрутка начиналась с первого же блока. Экран
 * даёт полную ширину под макеты и обычную прокрутку; заодно системное «назад» работает само, без
 * своего обработчика.
 *
 * Экран — лист, а не top-level раздел: открывается обычным `navigate()` из шапки раздела, как
 * `AboutScreen` из «Настроек». Правило `navigateToTopLevel` (`ui/navigation/CLAUDE.md`) его не
 * касается — и не должно: кнопка «?» когда-то ходила именно через top-level переход и ровно этим
 * ломала save/restore state остальных разделов (инцидент №2 в том же файле).
 */
@Composable
fun HelpScreen(
    topic: HelpTopic,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(StringKey.HelpDialogTitle)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            // Та же строка «Назад», что у «О приложении» и выбора языка: отдельный
                            // ключ на каждую кнопку возврата означал бы одно и то же слово,
                            // переведённое заново на каждом из языков интерфейса.
                            contentDescription = stringResource(StringKey.LanguagePickerBackContentDescription),
                        )
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item {
                Text(
                    text = stringResource(topic.title),
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
            items(topic.blocks) { block ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    HelpIllustration(block)
                    Text(
                        text = helpResource(block),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
