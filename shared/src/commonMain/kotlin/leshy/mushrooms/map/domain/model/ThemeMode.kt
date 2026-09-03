package leshy.mushrooms.map.domain.model

/**
 * Пользовательский выбор оформления. В отличие от соседнего [AppLanguage], своего
 * `displayName` не несёт: названия языков не переводятся (каждый показывается на самом себе),
 * а «Светлая»/«Тёмная»/«Системная» переводятся — значит, идут через `StringKey`, как варианты
 * `DataMode` на экране «Экспорт/Импорт».
 */
enum class ThemeMode { LIGHT, DARK, SYSTEM }
