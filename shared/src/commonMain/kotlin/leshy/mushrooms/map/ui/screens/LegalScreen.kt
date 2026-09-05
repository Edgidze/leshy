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

/**
 * Соглашение и конфиденциальность — второй шаг онбординга, между обзорной страницей и выбором
 * подборок ([leshy.mushrooms.map.presentation.onboarding.OnboardingStep.LEGAL]).
 *
 * **Тексты — заглушки.** Оба ключа (`LegalTermsText`/`LegalPrivacyText`) сейчас говорят прямым
 * текстом, что полная редакция появится до публикации, и дают короткую суть — ту, что уже верна
 * по устройству приложения (данные не покидают устройство, кроме запросов участков карты; вопрос
 * съедобности приложение на себя не берёт). Заглушка сделана честной, а не «Lorem ipsum»,
 * сознательно: пользователь первой сборки должен прочитать что-то осмысленное, а не пустой экран.
 * Настоящая редакция заменит значения этих же ключей во всех 33 языках; экран менять не придётся.
 *
 * Отдельного флага «согласие принято» в DataStore нет — почему, см.
 * [leshy.mushrooms.map.presentation.onboarding.OnboardingViewModel.onLegalAccepted].
 */
@Composable
fun LegalScreen(
    onBack: () -> Unit,
    onAccept: () -> Unit,
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
                onClick = onAccept,
                modifier = Modifier
                    .fillMaxWidth()
                    // Слот bottomBar у Scaffold, в отличие от content, инсеты не получает — их
                    // добавляют себе сами панели Material (NavigationBar и подобные), а голая
                    // кнопка иначе уезжает под навигационную полосу телефона.
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Text(stringResource(StringKey.LegalAcceptButton))
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
            Section(
                heading = stringResource(StringKey.LegalTermsHeading),
                body = stringResource(StringKey.LegalTermsText),
            )
            Section(
                heading = stringResource(StringKey.LegalPrivacyHeading),
                body = stringResource(StringKey.LegalPrivacyText),
            )
        }
    }
}

@Composable
private fun Section(heading: String, body: String) {
    Text(
        text = heading,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 8.dp),
    )
    Text(
        text = body,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
