package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import leshy.mushrooms.map.domain.model.CollectionSource
import leshy.mushrooms.map.domain.repository.CollectionRepository
import leshy.mushrooms.map.i18n.LocalAppLanguage
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.collectionDisplayName
import leshy.mushrooms.map.i18n.collidesWithCountryName
import leshy.mushrooms.map.i18n.isOtherCollectionName
import leshy.mushrooms.map.i18n.stringResource
import kotlinx.coroutines.flow.map
import org.koin.compose.koinInject

/**
 * Второй шаг сохранения пользовательского гриба (`.claude/plans/user-collections.md`): в какую
 * подборку он ложится. Открывается поверх [SpeciesFormDialog] и при создании, и при
 * редактировании — переноса гриба между подборками нет больше нигде, только здесь.
 *
 * Пустое поле — не ошибка, а самый частый исход: гриб уходит в служебную «Другие», её имя стоит в
 * поле подсказкой (`placeholder`, не `label` — подсказка обязана исчезать при вводе, ведь она
 * показывает, что будет ПРИ ПУСТОМ поле).
 *
 * Список уже созданных подборок под полем — не украшение: имя набирается руками каждый раз, и одна
 * опечатка молча создаёт двойника. Тап подставляет имя целиком.
 *
 * Совпадение со страной запрещено ([collidesWithCountryName]) — страновые подборки живут на том же
 * экране выше и не переименовываются, две одинаковые надписи в двух блоках подряд читались бы как
 * ошибка приложения. Проверяются текущий язык интерфейса и английский, не все 42.
 *
 * Клавиатура поднимается сразу, фокус — на единственном поле; `imePadding()` на [Surface]
 * обязателен, на iOS сцену к полю больше не двигают (см. `MainViewController.kt`).
 */
@Composable
fun CollectionNameDialog(
    initialName: String,
    onBack: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var name by remember { mutableStateOf(initialName) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val language = LocalAppLanguage.current
    val collectionRepository = koinInject<CollectionRepository>()
    val existing by remember(collectionRepository, language) {
        collectionRepository.observeAll().map { collections ->
            collections.filter { it.source != CollectionSource.COUNTRY }
                .map { collectionDisplayName(it, language) }
        }
    }.collectAsState(initial = emptyList())

    val isCountryName = collidesWithCountryName(name, language)

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Dialog(
        onDismissRequest = onBack,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = true),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.92f).imePadding(),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 4.dp,
        ) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(
                                StringKey.SpeciesCollectionDialogBackContentDescription,
                            ),
                        )
                    }
                    Text(
                        text = stringResource(StringKey.SpeciesCollectionDialogTitle),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    )
                    IconButton(
                        // «Другие», набранные руками, — это служебная подборка, а не новая с
                        // таким же именем: дальше по коду пустая строка и означает её.
                        onClick = {
                            val typed = name.trim()
                            onConfirm(if (isOtherCollectionName(typed, language)) "" else typed)
                        },
                        enabled = !isCountryName,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = stringResource(
                                StringKey.SpeciesCollectionDialogSaveContentDescription,
                            ),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text(stringResource(StringKey.CollectionOtherName)) },
                    singleLine = true,
                    isError = isCountryName,
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                )
                if (isCountryName) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(StringKey.SpeciesCollectionNameIsCountry),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                if (existing.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    // Список короткий по природе (свои подборки человек заводит штуками, не
                    // сотнями), поэтому обычная колонка, а не LazyColumn: диалог и так внутри
                    // verticalScroll, вложенная ленивая прокрутка тут только мешала бы.
                    existing.forEach { suggestion ->
                        Text(
                            text = suggestion,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { name = suggestion }
                                .heightIn(min = 48.dp)
                                .padding(vertical = 12.dp),
                        )
                    }
                }
            }
        }
    }
}
