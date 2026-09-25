package leshy.mushrooms.map.domain.model

/**
 * Редакция продукта. Двух редакций два **разных приложения** в магазинах, с разными
 * `applicationId`/bundle id, а не один продукт с переключателем внутри: см.
 * `docs/russia-edition/README.md`, раздел «Почему продуктов всё-таки два».
 *
 * **Значение приходит от хоста ровно один раз, при старте** — `initKoin(edition)` из
 * `LeshyApplication.onCreate` на Android и из `MainViewController(edition)` на iOS, — и дальше
 * никем не меняется. Внутри `shared` его неоткуда вычислить, и это не небрежность, а
 * требование к переносимости на iOS: там обе редакции стоят поверх ОДНОГО И ТОГО ЖЕ
 * `Shared.framework` (второй таргет отличается только bundle id, `Info.plist` и каталогом
 * ассетов), отдельного фреймворка на редакцию не существует, и сборочной константы вроде
 * `BuildConfig` внутри `shared` там просто негде взять. Способ через `BuildConfig` работал бы
 * на Android и не переносился на iOS вообще.
 *
 * На Android редакцию выбирает измерение флейворов `edition` (`androidApp/build.gradle.kts`),
 * а конкретное значение приходит из флейворного source set — `androidApp/src/world/kotlin` и
 * `androidApp/src/russia/kotlin`.
 */
enum class Edition {
    /** «Леший: карта грибов», `leshy.mushrooms.map`. Мировой продукт, OpenFreeMap, 42 языка. */
    WORLD,

    /** «Грибные прогулки: карта России», `ru.gribnyeprogulki.map`. Свой тайл-хост, ru+en. */
    RUSSIA,
}
