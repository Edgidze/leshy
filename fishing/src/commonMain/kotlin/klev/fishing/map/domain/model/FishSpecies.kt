package klev.fishing.map.domain.model

import leshy.mushrooms.map.domain.model.AppLanguage

/**
 * Вид рыбы в каталоге. Устроен так же, как `Category` у грибов, и намеренно проще: подборок по
 * странам у рыбацкого прототипа пока нет, а иллюстраций нет вообще — их рисует владелец, и до тех
 * пор плитка показывает цветной круг ([colorHex]).
 *
 * [iconRef] уже есть, хотя ещё не заполняется ни у одного вида: это тот самый строковый шов, из-за
 * которого нельзя переходить на сгенерированные аксессоры ресурсов (корневой `CLAUDE.md`). Когда
 * иллюстрации появятся, подключение — это данные в каталоге, а не правка кода.
 */
data class FishSpecies(
    val id: Long,
    /** Ключ каталога: `fish_pike`. По нему же ищется локализованное имя. */
    val key: String,
    val scientificName: String?,
    val colorHex: String,
    val iconRef: String?,
    val order: Int,
    /** Показывать на экране записи. Пользователь прячет то, что у него не водится. */
    val isActive: Boolean,
    val source: SpeciesSource,
    /** Имя, введённое пользователем, по языкам — только для [SpeciesSource.USER]. */
    val customNames: Map<AppLanguage, String> = emptyMap(),
)

enum class SpeciesSource {
    /** Из каталога приложения. */
    APP,

    /** Добавлен пользователем. */
    USER,
}
