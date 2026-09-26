import Shared

/// Редакция, которую этот таргет объявляет `shared` при старте (`MainViewController(edition:)`).
///
/// Ровно та же роль, что у `androidApp/src/world/kotlin/.../HostEdition.kt` во флейворном source
/// set Android: значение редакции приходит **от хоста** и внутри `shared` нигде не вычисляется.
/// На iOS иначе и нельзя — обе редакции стоят поверх ОДНОГО И ТОГО ЖЕ `Shared.framework`
/// (второго фреймворка на редакцию не существует), то есть сборочной константы там взять неоткуда.
///
/// Файл лежит в папке таргета, а не в общей `Sources/`: различия редакций — новые файлы, а не
/// `if` в общем коде (`docs/russia-edition/README.md`). Пара к `gribnye/HostEdition.swift`; обе
/// объявляют одно и то же имя, и `Sources/iOSApp.swift` пользуется им, не зная, какая сборка идёт.
let hostEdition: Edition = .world
