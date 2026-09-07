# `config/` — ручные дополнения к списку зависимостей AboutLibraries

Плагин строит список из графа зависимостей Gradle. Всё, что приезжает в приложение мимо
Gradle, он не видит и увидеть не может — такие записи добавляются сюда файлом на библиотеку
(схема — как в выводе `exportLibraryDefinitions`; при совпадении `uniqueId` происходит слияние).

Сейчас здесь одна запись: **MapLibre Native для iOS**. Он приходит через Swift Package Manager
(`spmForKmp`, см. `shared/build.gradle.kts`), в classpath Gradle его нет, а в IPA он есть — то
есть обязательство BSD-2-Clause по нему живое. Android-сборка того же движка
(`org.maplibre.gl:android-sdk`) в граф попадает сама и здесь не нужна.

Версию править вместе с `maplibreIos` в `gradle/libs.versions.toml` — они обязаны совпадать,
и ничто, кроме внимательности, их не сверяет.
