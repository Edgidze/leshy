package leshy.mushrooms.map.i18n

import leshy.mushrooms.map.domain.model.AppLanguage
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import leshy.shared.generated.resources.Res

private val AliasesJson = Json { ignoreUnknownKeys = true }

/**
 * `composeResources/files/catalog/aliases/<lang>.json` (`{key: [имя, ...]}`) — вторые народные
 * названия видов, **только для поиска**: показывается по-прежнему одно основное имя из
 * [MushroomNames]. Зеркалит его буква в букву — ленивый разбор по языку, Koin-синглтон
 * (`di/DataModule.kt`), `runCatching` на отсутствующий файл.
 *
 * Зачем. У грибов народных имён больше, чем одно на вид, и ищут именно по ним: подосиновик — как
 * «красный», подберёзовик — как «обабок». Поиск при этом ранжировал по единственной строке
 * основного имени, а `alt_names` дампа каталога (121 запись у английского, 64 у русского, есть
 * ещё у двух десятков языков) не выгружались в приложение вовсе. Репорт владельца 2026-09-17.
 *
 * Пустая карта — норма, а не сбой: у шестнадцати из сорока двух языков интерфейса синонимов в
 * источнике нет ни одного, и файл у них пустой. Поиск в таком языке работает как раньше, по
 * основному имени.
 */
class MushroomAliases {
    private val cache = mutableMapOf<AppLanguage, Map<String, List<String>>>()

    fun aliasesFor(language: AppLanguage): Map<String, List<String>> =
        cache.getOrPut(language) {
            runCatching {
                runBlocking {
                    AliasesJson.decodeFromString<Map<String, List<String>>>(
                        Res.readBytes("files/catalog/aliases/${language.code}.json").decodeToString(),
                    )
                }
            }.getOrDefault(emptyMap())
        }
}
