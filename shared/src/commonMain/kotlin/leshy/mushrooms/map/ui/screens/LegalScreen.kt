package leshy.mushrooms.map.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.ui.components.LeshyButton
import leshy.mushrooms.map.ui.components.PrivacyPolicyLink

/**
 * Конфиденциальность — второй шаг онбординга, между обзорной страницей и выбором подборок
 * ([leshy.mushrooms.map.presentation.onboarding.OnboardingStep.LEGAL]).
 *
 * **Пользовательского соглашения (EULA) здесь нет, и это решение, а не недоделка.** Ни Google Play,
 * ни App Store не требуют собственного соглашения от бесплатного приложения без аккаунтов, покупок
 * и пользовательского контента: на iOS по умолчанию действует стандартный EULA Apple, на Android —
 * Google Play Developer Distribution Agreement. Единственное, ради чего соглашение стоило бы
 * заводить, — дисклеймер о съедобности, а он уже стоит на предыдущем экране в более сильной форме,
 * двумя обязательными галочками ([WelcomeScreen]), которые без согласия не пускают дальше.
 *
 * Обязательна же ровно политика конфиденциальности — и обязательна она **публичной страницей**
 * (URL в Play Console и App Store Connect), а не текстом в приложении; Google дополнительно требует
 * ссылку на неё внутри приложения для всего, что трогает геолокацию и камеру. Поэтому экран —
 * короткая суть в три строки плюс ссылка ([PrivacyPolicyLink], адрес и его судьба —
 * `ui/components/PrivacyPolicyLink.kt`), а не полотно юридического текста, которое всё равно никто
 * не читает на первом запуске. Вторая точка входа к той же ссылке — «Настройки», чтобы страница
 * оставалась достижимой и после онбординга.
 *
 * Кнопка внизу — «Дальше» ([StringKey.WelcomeNextButton]), а не «Принимаю»: политика
 * конфиденциальности сообщает, а не спрашивает согласия, — соглашаться тут не с чем. Отдельного
 * флага в DataStore по той же причине нет, см.
 * [leshy.mushrooms.map.presentation.onboarding.OnboardingViewModel.onLegalNext].
 */
@Composable
fun LegalScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(StringKey.LegalTitle)) },
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
        bottomBar = {
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
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(StringKey.LegalPrivacyText),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
            PrivacyPolicyLink()
        }
    }
}
