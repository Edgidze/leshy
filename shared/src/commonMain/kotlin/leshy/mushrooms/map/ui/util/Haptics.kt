package leshy.mushrooms.map.ui.util

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

/**
 * Словарь тактильной отдачи приложения — все вибро-отклики зовутся отсюда и только отсюда.
 *
 * Файл существует ради одной строчки на событие: сила отклика подбирается вслепую по описанию, а
 * проверяется рукой на устройстве, и когда «плюс» окажется слишком слабым под перчаткой, править
 * надо в одном месте, а не искать `performHapticFeedback` по экранам.
 *
 * **Своего кода под платформы здесь нет и не нужно** (правило 6 корневого `CLAUDE.md`): у Compose
 * Multiplatform 1.11 есть `CupertinoHapticFeedback` — реализация `LocalHapticFeedback` поверх
 * `UIImpactFeedbackGenerator`/`UISelectionFeedbackGenerator`/`UINotificationFeedbackGenerator`, то
 * есть общий `HapticFeedbackType` работает на обеих платформах без `expect`/`actual`.
 *
 * Что физически произойдёт от каждого типа (сверено по исходникам обеих реализаций, чтобы отклик
 * подбирался не по названию константы, а по ощущению):
 *
 * | Тип | iOS | Android |
 * |---|---|---|
 * | `LongPress` | средний удар (`UIImpactFeedbackStyleMedium`) | `LONG_PRESS`, есть с API 3 |
 * | `SegmentTick` | щелчок выбора (`selectionChanged`) | `SEGMENT_TICK` (API 34+), ниже — `CONTEXT_CLICK` |
 * | `ToggleOn`/`ToggleOff` | лёгкий удар (`UIImpactFeedbackStyleLight`) | `TOGGLE_*` (API 34+), ниже — `CONTEXT_CLICK`/`CLOCK_TICK` |
 * | `Confirm` | «успех» — тройной паттерн уведомления | `CONFIRM` (API 30+), ниже — `VIRTUAL_KEY` |
 *
 * Понижения для старых Android делает сам `HapticFeedbackConstantsCompat`, так что `minSdk 24`
 * ничего здесь не ограничивает — беззвучным ни одно из событий не остаётся ни на одной версии.
 *
 * **Системную настройку «вибрация при касании» приложение уважает само, без единой строки кода
 * здесь.** Проверено замером, а не чтением документации: на Android при
 * `settings put system haptic_feedback_enabled 0` вызов доходит до `VibratorManagerService` и
 * записывается в его историю как `ignored_for_settings`, длительность 0 мс, — то есть система
 * гасит его сама, приложению об этом знать не нужно. Так работает любой отклик, проходящий через
 * `View.performHapticFeedback` без флага `FLAG_IGNORE_GLOBAL_SETTING`, а Compose его не ставит.
 * На iOS то же самое делает переключатель «Системные эффекты» («System Haptics») в настройках
 * звука: `UIFeedbackGenerator` при выключенном молчит, и то же происходит в режиме энергосбережения.
 *
 * Отсюда следствие для будущих правок: **собственной настройки «вибрация вкл/выкл» в приложении
 * нет намеренно.** Она понадобится только если появится запрос отключить отклик ИМЕННО здесь,
 * оставив его в остальной системе; общий отказ от вибрации пользователь уже может выразить
 * системным переключателем, и дублировать его своим — значит завести второе место, где то же
 * самое можно выключить, и обязанность их согласовывать.
 *
 * **Ощущение важнее имени константы.** `Confirm` на iOS — тройной паттерн длиной около полусекунды:
 * он уместен на старте и финише прогулки (событие редкое и важное), но не на «+», по которому за
 * минуту жмут двадцать раз. Поэтому находка — одиночный средний удар (`LongPress`), а не «успех».
 */
private object HapticVocabulary {
    /** Находка записана в Room. Средний удар: это событие обязано читаться сквозь перчатку. */
    val FIND_ADDED = HapticFeedbackType.LongPress

    /** Отмена находки. Заметно слабее записи — исправление ошибки не должно ощущаться как добыча. */
    val FIND_REMOVED = HapticFeedbackType.SegmentTick

    /** Порог удержания взят (2 с на «+», 5 с на карточке архива). Тот же средний удар. */
    val HOLD_COMPLETED = HapticFeedbackType.LongPress

    /** Прогулка начата или завершена — событие дня, единственное место для отчётливого «успеха». */
    val WALK_BOUNDARY = HapticFeedbackType.Confirm

    /** Пауза и продолжение — переключение, а не рубеж: лёгкий удар. */
    val WALK_PAUSED = HapticFeedbackType.ToggleOff
    val WALK_RESUMED = HapticFeedbackType.ToggleOn
}

fun HapticFeedback.findAdded() = performHapticFeedback(HapticVocabulary.FIND_ADDED)

fun HapticFeedback.findRemoved() = performHapticFeedback(HapticVocabulary.FIND_REMOVED)

fun HapticFeedback.holdCompleted() = performHapticFeedback(HapticVocabulary.HOLD_COMPLETED)

fun HapticFeedback.walkStarted() = performHapticFeedback(HapticVocabulary.WALK_BOUNDARY)

fun HapticFeedback.walkFinished() = performHapticFeedback(HapticVocabulary.WALK_BOUNDARY)

fun HapticFeedback.walkPaused() = performHapticFeedback(HapticVocabulary.WALK_PAUSED)

fun HapticFeedback.walkResumed() = performHapticFeedback(HapticVocabulary.WALK_RESUMED)
