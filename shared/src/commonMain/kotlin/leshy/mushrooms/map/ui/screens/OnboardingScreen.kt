package leshy.mushrooms.map.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.presentation.onboarding.OnboardingStep
import leshy.mushrooms.map.presentation.onboarding.OnboardingViewModel
import leshy.mushrooms.map.ui.components.CollectionPicker
import leshy.mushrooms.map.ui.components.LeshyButton
import leshy.mushrooms.map.ui.components.MushroomImageDisclaimerBanner
import org.koin.compose.viewmodel.koinViewModel

/**
 * First-run flow shown once before Home — see `.claude/plans/mushroom-collections.md`, Phase 3.
 * Четыре шага в одном composable, а не четыре маршрута навигации, по той же причине, по какой
 * весь онбординг вообще не является destination (ниже): обзор приложения ([WelcomeScreen]),
 * конфиденциальность ([LegalScreen]), выбор стран, чьи грибы отслеживать. Выбор языка
 * ([LanguagePickerScreen], тот же, что в Настройках) — не шаг в этом ряду, а отступление в
 * сторону с любого из них; порядок и причины — [OnboardingStep].
 *
 * Not a NavHost destination: [leshy.mushrooms.map.App] renders this in place of the whole nav
 * graph until the onboarding flag is set, so Home stays the graph's only real startDestination
 * (see `ui/navigation/CLAUDE.md` on why that matters for `navigateToTopLevel`).
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OnboardingScreen(modifier: Modifier = Modifier, viewModel: OnboardingViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    // Системная «назад» внутри онбординга ходит по шагам. Выключен на первом шаге целиком, а не
    // просто игнорирует нажатие: обработчик, который «съедает» жест и ничего не делает, оставил бы
    // на первом экране свежей установки жест, которым из приложения нельзя выйти.
    BackHandler(enabled = uiState.step != OnboardingStep.WELCOME) { viewModel.onBack() }

    when (uiState.step) {
        OnboardingStep.WELCOME -> WelcomeScreen(
            language = uiState.language,
            consentImagesAccepted = uiState.consentImagesAccepted,
            consentEatingAccepted = uiState.consentEatingAccepted,
            consentReminderCount = uiState.consentReminderCount,
            onConsentImagesChange = viewModel::setConsentImagesAccepted,
            onConsentEatingChange = viewModel::setConsentEatingAccepted,
            onLanguageClick = viewModel::onOpenLanguagePicker,
            onNext = viewModel::onWelcomeNext,
            modifier = modifier,
        )

        OnboardingStep.LANGUAGE -> LanguagePickerScreen(
            currentLanguage = uiState.language,
            onConfirm = viewModel::onLanguageConfirmed,
            onBack = viewModel::onLanguageDismissed,
        )

        OnboardingStep.LEGAL -> LegalScreen(
            onBack = viewModel::onBack,
            onNext = viewModel::onLegalNext,
            modifier = modifier,
        )

        OnboardingStep.COLLECTIONS -> CollectionsStep(viewModel = viewModel, modifier = modifier)
    }
}

@Composable
private fun CollectionsStep(viewModel: OnboardingViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsState()

    var collectionQuery by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).padding(16.dp)) {
        // Заголовок — единственное, что на этом шаге стоит на месте: всё остальное содержимое, а не
        // один только список подборок, уезжает под него при прокрутке (см. колонку ниже).
        Text(text = stringResource(StringKey.SpeciesCollectionsTitle), style = MaterialTheme.typography.headlineSmall)

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            // Та же кнопка языка, что и на обзорной странице, и с тем же смыслом: названия стран
            // читаются только на понятном языке, а вернуться она обязана сюда же (languageReturnStep).
            // Стоит первой под шапкой, а не под описанием с баннером: на обзорной странице она была в
            // шапке, то есть на виду сразу, и человек, который пришёл сюда именно за сменой языка
            // (названия стран в списке ему не читаются), обязан находить её там же, не вычитывая
            // сначала два абзаца.
            TextButton(
                onClick = viewModel::onOpenLanguagePicker,
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Language,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(uiState.language.endonym, style = MaterialTheme.typography.labelLarge)
            }
            Text(
                text = stringResource(StringKey.OnboardingDescription),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp),
            )
            MushroomImageDisclaimerBanner(modifier = Modifier.padding(bottom = 8.dp))
            // Прокрутка охватывает описание, баннер и кнопку языка, а не начинается с поля поиска:
            // это же и есть то, ради чего поле само подтягивается к верхней кромке окна прокрутки в
            // фокусе (`BringIntoViewRequester` внутри `CollectionPicker`). Раз человек нажал на
            // поиск, вводную часть он уже прочитал — и она уходит вверх, освобождая под список весь
            // экран между шапкой и клавиатурой. Пока пикер владел прокруткой сам, поле стояло в её
            // начале и подтягивать его было некуда.
            CollectionPicker(
                items = uiState.collectionPickerItems,
                query = collectionQuery,
                onQueryChange = { collectionQuery = it },
                onToggleCollection = viewModel::toggleCollection,
                onToggleCategory = viewModel::setCategoryPicked,
            )
        }
        // Предупреждение — над кнопкой, а не под списком: список прокручиваемый и к моменту
        // нажатия может стоять на любом своём месте, а нажал человек ровно сюда.
        if (uiState.collectionsReminderCount > 0) {
            Text(
                text = stringResource(StringKey.OnboardingNothingPickedWarning),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            )
        }
        // Пока не выбран ни один гриб, кнопка нарисована выключенной, но нажатие принимает —
        // иначе на тап она бы просто молчала, и объяснить, чего от человека ждут, было бы нечем
        // (`dimmed`, см. LeshyButton; проверка — OnboardingViewModel.onCollectionsNext).
        LeshyButton(
            onClick = viewModel::onCollectionsNext,
            dimmed = !uiState.hasPickedSpecies,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        ) {
            Text(stringResource(StringKey.OnboardingContinueButton))
        }
    }
}
