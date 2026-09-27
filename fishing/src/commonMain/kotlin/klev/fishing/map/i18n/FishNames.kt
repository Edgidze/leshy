package klev.fishing.map.i18n

import leshy.mushrooms.map.domain.model.AppLanguage

/**
 * Названия видов каталога. Это ДАННЫЕ, а не интерфейсные строки, — как и у грибов
 * (`i18n/CLAUDE.md`, «Имена грибов каталога»): под них нет `FishStringKey`, и exhaustive `when` по
 * ним невозможен, потому что каталог растёт.
 *
 * Пока 26 видов на двух языках лежат прямо здесь: так их видно глазами и правит компилятор. Когда
 * появятся остальные сорок языков, это переедет в `composeResources/files/catalog/names/<код>.json`
 * ровно по грибному образцу — там это уже работает на 52 языковых файла, и изобретать второй
 * механизм незачем.
 *
 * Нет названия на языке интерфейса — показывается латынь (`FishSpecies.scientificName`), а не
 * английское имя: латынь одинаково опознаваема всюду. Так же поступает грибной каталог.
 */
private val russianNames = mapOf(
    "fish_pike" to "Щука",
    "fish_perch" to "Окунь",
    "fish_zander" to "Судак",
    "fish_bream" to "Лещ",
    "fish_silver_bream" to "Густера",
    "fish_roach" to "Плотва",
    "fish_rudd" to "Краснопёрка",
    "fish_crucian" to "Карась",
    "fish_carp" to "Карп",
    "fish_tench" to "Линь",
    "fish_catfish" to "Сом",
    "fish_burbot" to "Налим",
    "fish_ide" to "Язь",
    "fish_chub" to "Голавль",
    "fish_dace" to "Елец",
    "fish_asp" to "Жерех",
    "fish_bleak" to "Уклейка",
    "fish_gudgeon" to "Пескарь",
    "fish_ruffe" to "Ёрш",
    "fish_eel" to "Угорь",
    "fish_brown_trout" to "Кумжа",
    "fish_rainbow_trout" to "Радужная форель",
    "fish_grayling" to "Хариус",
    "fish_whitefish" to "Сиг",
    "fish_vendace" to "Ряпушка",
    "fish_atlantic_salmon" to "Лосось",
)

private val englishNames = mapOf(
    "fish_pike" to "Northern pike",
    "fish_perch" to "European perch",
    "fish_zander" to "Zander",
    "fish_bream" to "Common bream",
    "fish_silver_bream" to "Silver bream",
    "fish_roach" to "Common roach",
    "fish_rudd" to "Rudd",
    "fish_crucian" to "Crucian carp",
    "fish_carp" to "Common carp",
    "fish_tench" to "Tench",
    "fish_catfish" to "Wels catfish",
    "fish_burbot" to "Burbot",
    "fish_ide" to "Ide",
    "fish_chub" to "Chub",
    "fish_dace" to "Common dace",
    "fish_asp" to "Asp",
    "fish_bleak" to "Bleak",
    "fish_gudgeon" to "Gudgeon",
    "fish_ruffe" to "Ruffe",
    "fish_eel" to "European eel",
    "fish_brown_trout" to "Brown trout",
    "fish_rainbow_trout" to "Rainbow trout",
    "fish_grayling" to "Grayling",
    "fish_whitefish" to "European whitefish",
    "fish_vendace" to "Vendace",
    "fish_atlantic_salmon" to "Atlantic salmon",
)

/** Название вида по ключу каталога на [language], или `null` — тогда зовущий показывает латынь. */
fun fishCatalogName(key: String, language: AppLanguage): String? = when (language) {
    AppLanguage.RU -> russianNames[key]
    AppLanguage.EN -> englishNames[key]
    else -> null
}

/** Ключи, у которых есть имя хоть на одном языке, — для проверки полноты каталога тестом. */
internal val namedFishKeys: Set<String> get() = russianNames.keys + englishNames.keys
