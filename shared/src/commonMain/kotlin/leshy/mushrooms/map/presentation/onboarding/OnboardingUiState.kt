package leshy.mushrooms.map.presentation.onboarding

import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.presentation.CollectionPickerItem

/**
 * Первый запуск — четыре шага в этом порядке: обзорная страница о приложении ([WELCOME]),
 * соглашение и конфиденциальность ([LEGAL]), выбор подборок грибов по странам ([COLLECTIONS]).
 * [LANGUAGE] в этот ряд не входит — это отступление в сторону с любого из трёх (см.
 * [OnboardingUiState.languageReturnStep]).
 *
 * Обзорная страница идёт ПЕРЕД выбором языка, хотя раньше выбор языка был самым первым экраном:
 * интерфейс и так открывается на языке системы ([leshy.mushrooms.map.data.platform.currentDeviceLanguage]),
 * поэтому подавляющему большинству язык выбирать не нужно вовсе, а список из 33 языков в качестве
 * самого первого, что человек видит после установки, не объясняет ему ничего о приложении.
 */
enum class OnboardingStep { WELCOME, LANGUAGE, LEGAL, COLLECTIONS }

data class OnboardingUiState(
    val step: OnboardingStep = OnboardingStep.WELCOME,
    /**
     * Куда возвращает экран выбора языка — на тот шаг, с которого его открыли. Кнопка языка есть и
     * на [OnboardingStep.WELCOME], и на [OnboardingStep.COLLECTIONS] (там названия стран читаются
     * только на понятном языке), и «назад» с языкового экрана обязано возвращать туда, откуда
     * пришли, а не на фиксированный шаг.
     */
    val languageReturnStep: OnboardingStep = OnboardingStep.WELCOME,
    val language: AppLanguage = AppLanguage.EN,
    /**
     * Галочки блока «Перед использованием» на обзорной странице: «не определяю грибы по картинкам»
     * и «не ем незнакомые грибы». Обе обязательны — пока хоть одна снята, «Дальше» не уводит с
     * экрана, а прокручивает страницу к самому блоку и показывает предупреждение
     * ([consentReminderCount]).
     *
     * Живут в состоянии онбординга, а не в DataStore: экран показывается ровно один раз за
     * установку, и единственное, что должно пережить перезапуск, — уже существующий флаг
     * завершения онбординга (см. [OnboardingViewModel.finish]). Возврат сюда системной «назад» с
     * соглашения галочки сохраняет — ViewModel живёт всё время онбординга.
     */
    val consentImagesAccepted: Boolean = false,
    val consentEatingAccepted: Boolean = false,
    /**
     * Сколько раз нажали «Дальше» с неполным согласием; `0` — предупреждения нет. Счётчик, а не
     * флаг: каждый повторный промах обязан заново прокрутить страницу к галочкам, а повторное
     * присвоение `true` тому же флагу состояние не меняет и `LaunchedEffect` в
     * [leshy.mushrooms.map.ui.screens.WelcomeScreen] не перезапускает.
     */
    val consentReminderCount: Int = 0,
    val collectionPickerItems: List<CollectionPickerItem> = emptyList(),
)
