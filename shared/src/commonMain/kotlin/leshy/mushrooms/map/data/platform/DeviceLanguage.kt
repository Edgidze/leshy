package leshy.mushrooms.map.data.platform

import androidx.compose.ui.text.intl.Locale
import leshy.mushrooms.map.domain.model.AppLanguage

/**
 * Язык интерфейса, на котором приложение открывается, пока пользователь ничего не выбирал сам, —
 * язык системы, если он среди [AppLanguage], иначе английский.
 *
 * Без `expect`/`actual`, в отличие от соседнего [currentDeviceRegionCode]: `Locale.current`
 * (`androidx.compose.ui.text.intl`) — общий API Compose Multiplatform, и на Android, и на iOS он
 * отдаёт язык системы сам (правило 6 корневого `CLAUDE.md` — кроссплатформенная библиотека
 * вперёд нативного кода). Это не composable-функция: `Locale.current` читает платформенную локаль
 * значением, а не через композицию, поэтому её зовёт и `SettingsRepositoryImpl` из data-слоя.
 *
 * Смена языка системы «на лету» здесь не отслеживается специально: значение читается там, где
 * его отсутствие в DataStore и так означает первый запуск, а на первом запуске язык системы
 * поменяться посреди экрана не успевает.
 */
fun currentDeviceLanguage(): AppLanguage {
    // Locale.current.language — двухбуквенный код без региона и письменности ("sr" для "sr-Latn-RS"),
    // то есть ровно то, чем является AppLanguage.code. Регистр приводится на всякий случай: iOS
    // отдаёт язык как есть из NSLocale, и "ru_RU"-подобные значения с верхним регистром
    // исторически встречались на обеих платформах.
    val code = Locale.current.language.lowercase()
    return AppLanguage.entries.find { it.code == code } ?: AppLanguage.EN
}
