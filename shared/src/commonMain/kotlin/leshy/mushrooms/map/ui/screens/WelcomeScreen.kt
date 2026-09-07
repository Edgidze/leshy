package leshy.mushrooms.map.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
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
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WelcomeScreen(
    language: AppLanguage,
    consentImagesAccepted: Boolean,
    consentEatingAccepted: Boolean,
    consentBatteryAccepted: Boolean,
    consentReminderCount: Int,
    onConsentImagesChange: (Boolean) -> Unit,
    onConsentEatingChange: (Boolean) -> Unit,
    onConsentBatteryChange: (Boolean) -> Unit,
    onLanguageClick: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Неудачное нажатие «Дальше» не только зажигает предупреждение, но и подтягивает к глазам сам
    // блок с галочками: страница длиннее экрана, и к моменту нажатия человек может стоять на любом
    // её месте — на одном тексте «поставьте галочки выше» ему пришлось бы искать их самому.
    // Ключ эффекта — счётчик нажатий, а не флаг: второе подряд нажатие обязано прокрутить снова.
    val consentRequester = remember { BringIntoViewRequester() }
    LaunchedEffect(consentReminderCount) {
        if (consentReminderCount > 0) consentRequester.bringIntoView()
    }

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
            Column(
                modifier = Modifier
                    // Слот bottomBar у Scaffold, в отличие от content, инсеты не получает — их
                    // добавляют себе сами панели Material (NavigationBar и подобные), а голая
                    // кнопка иначе уезжает под навигационную полосу телефона.
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                // Предупреждение стоит здесь, а не под галочками: галочки к этому моменту уже
                // могли уехать вверх за край экрана (страница прокручиваемая), а нажал человек
                // ровно сюда — сюда же и ответ.
                if (consentReminderCount > 0) {
                    Text(
                        text = stringResource(StringKey.WelcomeConsentWarning),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    )
                }
                LeshyButton(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(StringKey.WelcomeNextButton))
                }
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
            ConsentCard(
                imagesAccepted = consentImagesAccepted,
                eatingAccepted = consentEatingAccepted,
                batteryAccepted = consentBatteryAccepted,
                onImagesChange = onConsentImagesChange,
                onEatingChange = onConsentEatingChange,
                onBatteryChange = onConsentBatteryChange,
                modifier = Modifier.bringIntoViewRequester(consentRequester),
            )
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

/**
 * «Перед использованием» — три утверждения, с каждым из которых нужно согласиться галочкой, чтобы
 * «Дальше» сработала (проверка — в [leshy.mushrooms.map.presentation.onboarding.OnboardingViewModel.onWelcomeNext]).
 *
 * Карточка выделена цветом ошибки в приглушённом варианте (`errorContainer`), а не обычным
 * `surfaceContainer` соседних карточек: единственный блок страницы, который требует действия, а не
 * рассказывает, — и требования эти о безопасности.
 */
@Composable
private fun ConsentCard(
    imagesAccepted: Boolean,
    eatingAccepted: Boolean,
    batteryAccepted: Boolean,
    onImagesChange: (Boolean) -> Unit,
    onEatingChange: (Boolean) -> Unit,
    onBatteryChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(StringKey.WelcomeConsentTitle),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            Text(
                text = stringResource(StringKey.WelcomeConsentIntro),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            ConsentRow(
                checked = imagesAccepted,
                onCheckedChange = onImagesChange,
                text = stringResource(StringKey.WelcomeConsentImages),
            )
            ConsentRow(
                checked = eatingAccepted,
                onCheckedChange = onEatingChange,
                text = stringResource(StringKey.WelcomeConsentEating),
            )
            ConsentRow(
                checked = batteryAccepted,
                onCheckedChange = onBatteryChange,
                text = stringResource(StringKey.WelcomeConsentBattery),
            )
        }
    }
}

/**
 * Утверждение с галочкой. Нажимается вся строка целиком (`toggleable` на `Row`, у самого
 * `Checkbox` обработчик снят) — текст здесь в несколько строк, и цель размером с текст попадается
 * пальцем куда надёжнее, чем один квадратик 20 dp сбоку.
 */
@Composable
private fun ConsentRow(checked: Boolean, onCheckedChange: (Boolean) -> Unit, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .toggleable(value = checked, onValueChange = onCheckedChange, role = Role.Checkbox)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Checkbox(checked = checked, onCheckedChange = null)
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(start = 8.dp, top = 12.dp),
        )
    }
}

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
