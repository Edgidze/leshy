package leshy.mushrooms.map.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import leshy.mushrooms.map.data.catalog.CountriesSource
import leshy.mushrooms.map.data.catalog.countryCodeForCollectionNameKey
import leshy.mushrooms.map.data.catalog.countryCollectionNameKey
import leshy.mushrooms.map.data.platform.currentDeviceRegionCode
import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.domain.repository.CategoryRepository
import leshy.mushrooms.map.domain.repository.CollectionRepository
import leshy.mushrooms.map.domain.repository.OnboardingRepository
import leshy.mushrooms.map.domain.repository.SettingsRepository
import leshy.mushrooms.map.domain.usecase.EnsureDefaultCategoriesUseCase
import leshy.mushrooms.map.domain.usecase.EnsureDefaultCollectionsUseCase
import leshy.mushrooms.map.domain.usecase.RecalculateFilterEligibilityUseCase
import leshy.mushrooms.map.domain.usecase.SetCategoryPickedUseCase
import leshy.mushrooms.map.domain.usecase.SetCollectionPickedUseCase
import leshy.mushrooms.map.presentation.CollectionPickState
import leshy.mushrooms.map.presentation.CollectionPickerItem
import leshy.mushrooms.map.presentation.buildCollectionPickerItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * First-run flow shown once before Home (see `.claude/plans/mushroom-collections.md`, Phase 3) —
 * four steps in one ViewModel and one composable, see [OnboardingStep]. Reuses the same
 * [leshy.mushrooms.map.ui.components.CollectionPicker] composable and picker use cases as
 * Settings — this screen owns no picker logic of its own beyond the one-time "completed" flag.
 */
class OnboardingViewModel(
    private val categoryRepository: CategoryRepository,
    private val collectionRepository: CollectionRepository,
    private val onboardingRepository: OnboardingRepository,
    private val settingsRepository: SettingsRepository,
    private val countriesSource: CountriesSource,
    private val ensureDefaultCategories: EnsureDefaultCategoriesUseCase,
    private val ensureDefaultCollections: EnsureDefaultCollectionsUseCase,
    private val recalculateFilterEligibility: RecalculateFilterEligibilityUseCase,
    private val setCollectionPickedUseCase: SetCollectionPickedUseCase,
    private val setCategoryPickedUseCase: SetCategoryPickedUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Onboarding is, by construction, the very first screen a user can ever open — same
            // idempotent seeding as Record/Settings init, see data/CLAUDE.md.
            ensureDefaultCategories()
            ensureDefaultCollections()
            recalculateFilterEligibility()
            preselectByDeviceRegion()
        }
        viewModelScope.launch {
            combine(
                collectionRepository.observeAll(),
                categoryRepository.observeAll(),
                collectionRepository.observeAllMemberships(),
                settingsRepository.observeLanguage(),
            ) { collections, categories, memberships, language ->
                language to buildCollectionPickerItems(collections, categories, memberships)
            }.collect { (language, items) ->
                _uiState.update {
                    it.copy(language = language, collectionPickerItems = sortByLanguage(items, language))
                        .withCollectionsReminderClearedIfPicked()
                }
            }
        }
    }

    /**
     * Countries that speak the current interface language float to the top; everything
     * else keeps the ordinary [leshy.mushrooms.map.domain.model.Collection.order] below them. This
     * is a default ORDER, not a filter — every one of the 40 countries is still in the list, and
     * the search field still finds any of them.
     *
     * Inside the floated group, the countries the language is *titular* in come first — those whose
     * [leshy.mushrooms.map.data.catalog.CountryEntry.langs] starts with it. Without that split the
     * group is ordered by country code, and picking Russian would put `RU` ninth, below `AM`, `AZ`,
     * `BY`, `EE`, `KG`, `KZ`, `LV`, `MD` — every country where Russian is merely widely spoken.
     * That was already slightly wrong at 6 such countries and became clearly wrong at 13, when
     * `.claude/plans/post-soviet-countries.md` added Russian to all seven Central-Asian and
     * Caucasian presets. The same split does the right thing for every other language too: `uk`
     * puts `UA` first, `kk` puts `KZ` first.
     *
     * Only the onboarding step reorders like this. The same picker on the "Грибы" screen keeps the
     * plain alphabetical/catalog order: there, the user is looking for a specific country they
     * already have in mind, and a list that silently reshuffles itself around the interface
     * language would just make it harder to find.
     */
    private fun sortByLanguage(
        items: List<CollectionPickerItem>,
        language: AppLanguage,
    ): List<CollectionPickerItem> {
        val speaking = countriesSource.entries.filter { language.code in it.langs }
        if (speaking.isEmpty()) return items
        val titularCodes = speaking.filter { it.langs.firstOrNull() == language.code }
            .map { it.code }
            .toSet()
        val otherCodes = speaking.map { it.code }.toSet() - titularCodes

        // partition preserves the relative order inside each half, so each group stays in the
        // collections' own order and the untouched tail keeps it too.
        val (titular, rest) = items.partition { item ->
            countryCodeForCollectionNameKey(item.collection.nameKey) in titularCodes
        }
        val (alsoSpoken, others) = rest.partition { item ->
            countryCodeForCollectionNameKey(item.collection.nameKey) in otherCodes
        }
        return titular + alsoSpoken + others
    }

    /**
     * «Дальше» с обзорной страницы — к соглашению, но только с обеими галочками блока «Перед
     * использованием» ([OnboardingUiState.consentImagesAccepted]/
     * [OnboardingUiState.consentEatingAccepted]). Иначе шаг не меняется, а растёт
     * [OnboardingUiState.consentReminderCount] — по нему экран и прокручивает страницу к блоку
     * с галочками, и показывает красное предупреждение над кнопкой.
     *
     * Кнопка при этом остаётся живой, а не гаснет до простановки галочек: выключенная «Дальше» на
     * первом же экране свежей установки не объясняет, чего от человека хотят, — нажатие с
     * объяснением объясняет.
     */
    fun onWelcomeNext() {
        val state = _uiState.value
        if (!state.consentImagesAccepted || !state.consentEatingAccepted) {
            _uiState.update { it.copy(consentReminderCount = it.consentReminderCount + 1) }
            return
        }
        _uiState.update { it.copy(step = OnboardingStep.LEGAL, consentReminderCount = 0) }
    }

    fun setConsentImagesAccepted(accepted: Boolean) {
        _uiState.update { it.copy(consentImagesAccepted = accepted).withReminderClearedIfComplete() }
    }

    fun setConsentEatingAccepted(accepted: Boolean) {
        _uiState.update { it.copy(consentEatingAccepted = accepted).withReminderClearedIfComplete() }
    }

    /** Предупреждение гаснет в тот момент, когда встала последняя галочка, — держать его до
     * повторного нажатия «Дальше» значило бы ругаться на уже исправленное. */
    private fun OnboardingUiState.withReminderClearedIfComplete(): OnboardingUiState =
        if (consentImagesAccepted && consentEatingAccepted) copy(consentReminderCount = 0) else this

    /** Кнопка языка (есть и на обзорной странице, и над списком стран) — запоминает, куда
     * возвращаться, см. [OnboardingUiState.languageReturnStep]. */
    fun onOpenLanguagePicker() {
        _uiState.update { it.copy(languageReturnStep = it.step, step = OnboardingStep.LANGUAGE) }
    }

    /** Галочка на языковом экране: применяет язык на всё приложение (Настройки пишут тот же ключ)
     * и возвращает на шаг, с которого его открыли. */
    fun onLanguageConfirmed(language: AppLanguage) {
        viewModelScope.launch { settingsRepository.setLanguage(language) }
        _uiState.update { it.copy(step = it.languageReturnStep) }
    }

    /** Стрелка «назад» на языковом экране — уйти, ничего не меняя. */
    fun onLanguageDismissed() {
        _uiState.update { it.copy(step = it.languageReturnStep) }
    }

    /** «Дальше» с экрана конфиденциальности — к выбору подборок. Никакого флага «согласие
     * получено» в DataStore нет и не нужно: экран сообщает, куда деваются данные, и даёт ссылку на
     * публичную политику — соглашаться там не с чем (почему нет и EULA — см.
     * [leshy.mushrooms.map.ui.screens.LegalScreen]). Дисклеймер о съедобности, единственное, под
     * чем действительно нужна подпись, стоит шагом раньше — двумя обязательными галочками
     * [onWelcomeNext]. */
    fun onLegalNext() {
        _uiState.update { it.copy(step = OnboardingStep.COLLECTIONS) }
    }

    /**
     * Системная «назад» внутри онбординга. Возвращает `false` на первом шаге — там перехватывать
     * нечего, и жест обязан уйти системе (то есть закрыть приложение), а не упереться в экран,
     * с которого не выйти.
     */
    fun onBack(): Boolean {
        val state = _uiState.value
        val previous = when (state.step) {
            OnboardingStep.WELCOME -> return false
            OnboardingStep.LANGUAGE -> state.languageReturnStep
            OnboardingStep.LEGAL -> OnboardingStep.WELCOME
            OnboardingStep.COLLECTIONS -> OnboardingStep.LEGAL
        }
        _uiState.update { it.copy(step = previous) }
        return true
    }

    /** Tri-state click convention: anything short of fully picked selects every member, only a
     * fully-picked collection deselects them all — mirrors [leshy.mushrooms.map.presentation.settings.SettingsViewModel]. */
    fun toggleCollection(item: CollectionPickerItem) {
        val picked = item.pickState != CollectionPickState.ALL
        viewModelScope.launch { setCollectionPickedUseCase(item.collection.id, picked) }
    }

    fun setCategoryPicked(category: Category, picked: Boolean) {
        viewModelScope.launch { setCategoryPickedUseCase(category, picked) }
    }

    /**
     * Plan §"Фаза 3" ("предвыбор по региону устройства при первом запуске") — pre-picks the
     * collection matching the device's region so a fresh install lands with a plausible starting
     * point instead of every species unpicked.
     *
     * Gated on "nothing is picked yet at all" rather than a dedicated one-shot flag: every catalog
     * species is seeded with `isPicked = false` (`EnsureDefaultCategoriesUseCase`), and nothing else
     * writes `isPicked = true` before the user reaches this screen, so a non-empty picked set can
     * only mean this already ran (or, in principle, a restored backup) — either way, re-forcing a
     * region pick on top of a state the user or a previous run already touched would be surprising.
     */
    private suspend fun preselectByDeviceRegion() {
        if (categoryRepository.getAll().any { it.isPicked }) return
        val regionCode = currentDeviceRegionCode() ?: return
        val collection = collectionRepository.getByNameKey(countryCollectionNameKey(regionCode)) ?: return
        setCollectionPickedUseCase(collection.id, true)
    }

    /**
     * «Дальше» с последнего шага онбординга — но только если отмечен хоть один гриб
     * ([OnboardingUiState.hasPickedSpecies]). Иначе шаг не меняется, а растёт
     * [OnboardingUiState.collectionsReminderCount], по которому экран показывает предупреждение
     * над кнопкой — ровно тот же приём, что и с галочками согласия в [onWelcomeNext].
     *
     * Экран при этом гасит кнопку, но нажатие оставляет живым (см.
     * [leshy.mushrooms.map.ui.screens.OnboardingScreen]) — иначе объяснить, чего от человека ждут,
     * было бы нечем: погашенная кнопка на нажатие не отвечает вовсе.
     *
     * Предвыбор по региону устройства ([preselectByDeviceRegion]) обычно приводит сюда с уже
     * отмеченной подборкой, так что предупреждение видит тот, кто её снял, или тот, чьего региона
     * нет среди подборок.
     */
    fun onCollectionsNext() {
        if (!_uiState.value.hasPickedSpecies) {
            _uiState.update { it.copy(collectionsReminderCount = it.collectionsReminderCount + 1) }
            return
        }
        finish()
    }

    /** Предупреждение гаснет в тот момент, когда встала первая галочка, — держать его до повторного
     * нажатия «Дальше» значило бы ругаться на уже исправленное (ср. [withReminderClearedIfComplete]). */
    private fun OnboardingUiState.withCollectionsReminderClearedIfPicked(): OnboardingUiState =
        if (collectionsReminderCount > 0 && hasPickedSpecies) copy(collectionsReminderCount = 0) else this

    /**
     * Fire-and-forget is safe here: nothing tears this ViewModel down in direct response to this
     * call. [leshy.mushrooms.map.App] observes the same [OnboardingRepository] flag independently
     * and only swaps away from the onboarding screen once the write has actually landed and the
     * flow re-emits — see `.claude/plans/mushroom-collections.md`, Phase 3.
     */
    private fun finish() {
        viewModelScope.launch { onboardingRepository.setCollectionPickerCompleted() }
    }
}
