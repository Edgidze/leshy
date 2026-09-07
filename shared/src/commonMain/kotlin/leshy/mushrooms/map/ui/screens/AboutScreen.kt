package leshy.mushrooms.map.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import leshy.mushrooms.map.APP_VERSION_CODE
import leshy.mushrooms.map.APP_VERSION_NAME
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.stringResource
import leshy.shared.generated.resources.Res

/**
 * «О приложении» — версия, атрибуция карты и полный список зависимостей с текстами их лицензий.
 *
 * **Экран существует не для красоты, а потому что этого требуют лицензии зависимостей.** Apache
 * 2.0 §4(a) обязывает передать получателю копию лицензии, BSD — воспроизвести текст условий «in the
 * documentation and/or other materials provided with the distribution». Обязательство привязано к
 * тому, что доехало до пользователя ВМЕСТЕ с приложением, поэтому md-файл в репозитории или ссылка
 * на сайт его не закрывают, а этот экран закрывает: и список, и полные тексты лежат в самом
 * APK/IPA (`composeResources/files/aboutlibraries.json`).
 *
 * Список генерируется плагином AboutLibraries по РЕАЛЬНОМУ графу зависимостей — 177 записей против
 * тринадцати строк, которые до этого велись руками в `docs/release/licenses.md`: обязательства
 * висят и на транзитивных зависимостях тоже, и ручной список честно пропускал, например, MIT у
 * `org.maplibre.spatialk` и BSD-3 у `datastore-preferences-external-protobuf`. Генерация ручная,
 * файл коммитится — как пересобирать и что добавлено вручную, см. `shared/build.gradle.kts` и
 * `shared/config/README.md`.
 *
 * Экран — лист, а не шаг: попасть сюда можно только из «Настроек», и никто не обязан его открывать.
 * Тексты лицензий принципиально не переводятся — юридическую силу имеет оригинал.
 *
 * Ссылки на политику конфиденциальности здесь сознательно нет, хотя место напрашивается: она уже
 * стоит строкой выше, в самих «Настройках»
 * ([leshy.mushrooms.map.ui.components.PrivacyPolicyLink]), и дублировать её на соседнем экране
 * значит спрашивать у пользователя, чем эти две ссылки отличаются.
 */
@Composable
fun AboutScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Ресурс читается один раз на вход на экран: 74 КБ JSON разбираются заметно быстрее, чем
    // человек успевает доскроллить до списка, а держать разобранный результат в Koin-синглтоне
    // ради экрана, который открывают раз в жизни, значило бы занимать память постоянно.
    val libs by produceState<Libs?>(initialValue = null) {
        value = Libs.Builder()
            .withJson(Res.readBytes("files/aboutlibraries.json").decodeToString())
            .build()
    }
    var licenseDialogFor by remember { mutableStateOf<Library?>(null) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(StringKey.AboutTitle)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(StringKey.LanguagePickerBackContentDescription),
                        )
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(StringKey.AppName),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = "$APP_VERSION_NAME ($APP_VERSION_CODE)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            item {
                Section(
                    heading = stringResource(StringKey.AboutMapDataTitle),
                    body = stringResource(StringKey.AboutMapDataText),
                )
            }

            item {
                Section(
                    heading = stringResource(StringKey.AboutOpenSourceTitle),
                    body = stringResource(StringKey.AboutOpenSourceText),
                )
            }

            items(libs?.libraries.orEmpty(), key = { it.uniqueId }) { library ->
                LibraryRow(library = library, onClick = { licenseDialogFor = library })
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHighest)
            }
        }
    }

    licenseDialogFor?.let { library ->
        LicenseDialog(library = library, onDismiss = { licenseDialogFor = null })
    }
}

@Composable
private fun Section(heading: String, body: String) {
    Column(modifier = Modifier.padding(top = 24.dp, bottom = 8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = heading, style = MaterialTheme.typography.titleMedium)
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Строка списка. Координата (`uniqueId`) показывается наравне с именем: имён вроде «Activity» или
 * «Core» в списке из 177 записей по нескольку, и без группы с артефактом строка не опознаётся.
 */
@Composable
private fun LibraryRow(library: Library, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(text = library.name, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = library.uniqueId + (library.artifactVersion?.let { " · $it" } ?: ""),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = library.licenses.joinToString { it.spdxId ?: it.name },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

/**
 * Полный текст лицензии. Именно он и есть то, ради чего экран написан, — имени лицензии для
 * выполнения условий Apache/BSD недостаточно.
 */
@Composable
private fun LicenseDialog(library: Library, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f),
        title = { Text(library.name) },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 480.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                library.licenses.forEach { license ->
                    Text(text = license.name, style = MaterialTheme.typography.titleSmall)
                    Text(
                        // Тексты приходят из SPDX как есть; у части лицензий (тех, что плагин берёт
                        // не из SPDX, а из POM/сайта) внутри попадаются html-переносы — экран
                        // рисует обычный Text, поэтому они разворачиваются в настоящие переводы
                        // строки, иначе абзацы слиплись бы в одну простыню.
                        text = (license.licenseContent ?: license.url ?: "")
                            .replace("<br />", "\n")
                            .replace("<br>", "\n"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(StringKey.HelpDialogDismiss)) }
        },
    )
}
