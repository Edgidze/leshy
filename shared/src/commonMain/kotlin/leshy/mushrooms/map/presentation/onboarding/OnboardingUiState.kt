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
    val collectionPickerItems: List<CollectionPickerItem> = emptyList(),
)
