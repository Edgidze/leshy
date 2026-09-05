package leshy.mushrooms.map.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.ui.components.ArchiveVignette
import leshy.mushrooms.map.ui.components.HelpVignette
import leshy.mushrooms.map.ui.components.LeshyButton
import leshy.mushrooms.map.ui.components.MenuVignette
import leshy.mushrooms.map.ui.components.RecordVignette
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.leshy_icon
import org.jetbrains.compose.resources.painterResource

/**
 * Самый первый экран свежей установки — обзор приложения: чем оно помогает и что в нём где лежит.
 * Первый шаг онбординга ([leshy.mushrooms.map.presentation.onboarding.OnboardingStep.WELCOME]),
 * не destination навигации, как и весь онбординг (`ui/navigation/CLAUDE.md`).
 *
 * Открывается на языке системы: язык интерфейса, пока его не выбирали руками, берётся у устройства
 * (`SettingsRepositoryImpl.observeLanguage` → `currentDeviceLanguage()`), поэтому кнопка языка
 * здесь — не обязательный шаг, а запасной выход для тех, кому система угадала не тот язык.
 */
@Composable
fun WelcomeScreen(
    language: AppLanguage,
    onLanguageClick: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(StringKey.AppName)) },
                actions = {
                    // Кнопка с текстом, а не голая иконка: подпись — эндоним текущего языка
                    // («Русский», «Türkçe»), и она же служит ответом на вопрос «а на каком языке
                    // я это читаю» человеку, которому система выбрала язык за него.
                    TextButton(onClick = onLanguageClick, modifier = Modifier.padding(end = 4.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Language,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(language.endonym)
                    }
                },
            )
        },
        bottomBar = {
            // «Дальше» вынесена в bottomBar, а не в конец прокручиваемой колонки: страница длиннее
            // экрана на любом телефоне, и кнопка, до которой надо ещё доскроллить, читалась бы как
            // «выхода нет».
            LeshyButton(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    // Слот bottomBar у Scaffold, в отличие от content, инсеты не получает — их
                    // добавляют себе сами панели Material (NavigationBar и подобные), а голая
                    // кнопка иначе уезжает под навигационную полосу телефона.
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Text(stringResource(StringKey.WelcomeNextButton))
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Hero()
            FeatureCard(
                title = stringResource(StringKey.WelcomeRecordTitle),
                text = stringResource(StringKey.WelcomeRecordText),
            ) { RecordVignette() }
            FeatureCard(
                title = stringResource(StringKey.WelcomeArchiveTitle),
                text = stringResource(StringKey.WelcomeArchiveText),
            ) { ArchiveVignette() }
            FeatureCard(
                title = stringResource(StringKey.WelcomeHelpTitle),
                text = stringResource(StringKey.WelcomeHelpText),
            ) { HelpVignette() }
            FeatureCard(
                title = stringResource(StringKey.WelcomeMenuTitle),
                text = stringResource(StringKey.WelcomeMenuText),
            ) { MenuVignette() }
            Spacer(modifier = Modifier.size(4.dp))
        }
    }
}

/** Значок приложения, приветствие и одно предложение о том, зачем всё это. */
@Composable
private fun Hero() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Значок приложения — ровно тот, что пользователь только что нажал на домашнем экране
        // (`leshy_icon.webp` — уменьшенная копия опубликованной иконки,
        // `iosApp/.../AppIcon.appiconset/app-icon-1024.png`), а не старый логотип-рожица: первая
        // страница обязана узнаваться тем же образом, каким приложение было открыто.
        //
        // Скругление накладывается здесь, а не запечено в файле: у ресурса углы залиты тем же
        // тёмно-зелёным, что и весь фон значка, поэтому картинка остаётся честным квадратом и не
        // тащит за собой ни альфа-канал, ни фиксированный радиус. Подложки под ней не нужно —
        // значок несёт собственный непрозрачный фон и одинаково читается в обеих темах.
        Image(
            painter = painterResource(Res.drawable.leshy_icon),
            contentDescription = null,
            modifier = Modifier.size(112.dp).clip(RoundedCornerShape(APP_ICON_CORNER)),
        )
        Text(
            text = stringResource(StringKey.OnboardingTitle),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp),
        )
        Text(
            text = stringResource(StringKey.WelcomeIntro),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

/** Скругление значка — примерно та же доля стороны (≈22%), с какой iOS скругляет иконки. */
private val APP_ICON_CORNER = 25.dp

@Composable
private fun FeatureCard(title: String, text: String, vignette: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            vignette()
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
