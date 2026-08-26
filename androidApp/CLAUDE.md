# Релизные конвенции модуля androidApp

## Идентификаторы — не менять никогда

- `applicationId` = `leshy.mushrooms.map`
- `namespace` = `leshy.mushrooms.map`

Оба зафиксированы до первой публикации в Play и после неё неизменяемы. Если задача
выглядит так, что требует их поменять — остановиться и спросить.

## Сборка

- `targetSdk` = 36, понижать нельзя (требование Play для новых приложений и обновлений
  с 31.08.2026)
- формат публикации — AAB
- `versionCode` берётся из `gradle.properties` и только увеличивается; использованные
  значения не переиспользуются
- flavor'ы `play` и `rustore` имеют **одинаковый** `applicationId`, `applicationIdSuffix`
  не использовать
- billing-зависимости RuStore не должны попадать во flavor `play`, и наоборот

## R8

- release-сборка всегда с `isMinifyEnabled = true`
- **отключать R8 для обхода краша запрещено** — ни `isMinifyEnabled = false`, ни
  `android.enableR8.fullMode = false`. Вместо этого точечное правило в
  `proguard-rules.pro` с комментарием, зачем оно
- если правило не находится — задокументировать проблему и остановиться
- маршруты Compose Navigation лежат в `leshy.mushrooms.map.ui.navigation` и защищены
  keep-правилом. При переносе маршрутов в другой пакет обязательно обновить правило
  и проверить через `-printseeds`, что оно с чем-то совпало: R8 молчит о правилах,
  не совпавших ни с одним классом, и краш вылезет только в runtime

## Ресурсы

Изображения категорий грибов (410 файлов, `shared/src/commonMain/composeResources/drawable/`)
грузятся по строковому имени (`Category.iconRef` → `Res.allDrawableResources[iconRef]` в
`ui/components/CategoryIcon.kt`, `ui/map/MushroomMarkerIcon.kt`,
`data/platform/CategoryIconBytes.kt`; ручной путь `Res.readBytes("drawable/$iconRef.webp")`
в `SpeciesFormDialog.kt`). На классическом Android это был бы паттерн, ломаемый
`shrinkResources` молча — но здесь **не ломает**, проверено на собранном release AAB/APK:
Compose Multiplatform кладёт содержимое `composeResources/drawable/` в
`assets/composeResources/...`, а не в таблицу ресурсов `res/`. `shrinkResources` работает
только по таблице ресурсов и до `assets/` не дотягивается — `R.drawable`-паттерна (когда
шринкер реально бьёт по динамическим строковым именам) в проекте нет вообще, всё живёт
за пределами его области действия по конструкции. Подтверждение: `resources.txt` в
`androidApp/build/outputs/mapping/playRelease/` не содержит ни одного упоминания грибов
(они там в принципе не могут появиться — не resource-table записи), и APK после
`assemblePlayRelease` с `--no-build-cache` содержит все 410 файлов из исходников 1:1.

Если каталог видов когда-нибудь переедет с `composeResources` на честный Android
`res/drawable` (например, ради снижения памяти на bitmap-декодинге) — это переносит
изображения обратно в область действия `shrinkResources`, и тогда `keep.xml` с
`tools:keep="@drawable/mushroom_*"` действительно понадобится. До тех пор эта опасность
теоретическая, не текущая.

## Секреты

Пароли keystore читаются из gradle-свойств `LESHY_STORE_FILE`, `LESHY_STORE_PASSWORD`,
`LESHY_KEY_ALIAS`, `LESHY_KEY_PASSWORD`, которые живут в `~/.gradle/gradle.properties`
за пределами репозитория.

Никогда не вставлять их значения в код, в коммиты и в текст вопросов. Если значение
понадобилось — читать свойство по имени, а не спрашивать.

## База данных

`fallbackToDestructiveMigration` в проекте отсутствует намеренно и не должен появиться
даже в debug-сборках. В релизе он не бросает исключение при ошибке миграции, а молча
стирает базу — то есть все находки пользователя.

Подробности по схемам и миграциям — в `shared/CLAUDE.md`.
