# Релизные конвенции модуля androidApp

## Идентификаторы — не менять никогда

- `applicationId` мирового «Лешего» (флейвор `edition=world`) = `leshy.mushrooms.map`
- `applicationId` «Грибных прогулок» (флейвор `edition=russia`) = `ru.gribnyeprogulki.map`
- `namespace` = `leshy.mushrooms.map` — **один на оба продукта** (это пакет генерируемых `R`
  и `BuildConfig`, к идентификатору в магазине отношения не имеет)

Все зафиксированы до первой публикации и после неё неизменяемы. Если задача выглядит так,
что требует их поменять — остановиться и спросить.

## Два измерения флейворов: `edition` и `store`

Порядок в `flavorDimensions` — это порядок слов в имени варианта: `worldPlayRelease`,
`russiaRustoreRelease`.

Измерения отвечают на разные вопросы, и путать их нельзя:

- **`edition` (`world`/`russia`) — это РАЗНЫЕ продукты.** Свой `applicationId`, свой ярлык,
  своя иконка, своя нумерация версий. На телефоне стоят одновременно и друг друга не
  обновляют.
- **`store` (`play`/`rustore`) — это витрины ОДНОГО продукта**, с обязательно одинаковым
  `applicationId` (правило ниже). Второго приложения этим измерением не получить: пользователь
  с «Лешим» из Play получил бы из RuStore не второе приложение, а молчаливое обновление
  первого. Именно поэтому российская редакция — ось `edition`, а не флейвор `rustore`.

**Что принадлежит редакции и обязано лежать во флейворном source set, а не в `src/main`:**

- `app_name` со всеми переводами — `src/world/res/values*/` и `src/russia/res/values*/`.
  В `src/main` его быть не должно **ни в одной локали**: флейвор перекрывает `main`
  ПОКОНФИГУРАЦИОННО, поэтому `values/` из `src/russia` перебивает только значение по
  умолчанию, а `values-de/` из `src/main` продолжало бы действовать. Живьём это дало
  российскую сборку с ярлыком «Pilzkarte» на немецкой локали (поймано `aapt2 dump badging`
  на первой релизной сборке 2026-09-25).
- **иконка целиком, у обеих редакций** — `src/world/res/` и `src/russia/res/`, включая
  `mipmap-anydpi-v26/ic_launcher*.xml`. Держать XML адаптивной иконки в `src/main` не вышло:
  редакции ссылаются на РАЗНЫЕ типы ресурсов — у мировой фон векторный (`@drawable`), у
  российской растровый (`@mipmap`, деревянное поле с волокном), — и один общий XML на оба
  случая не написать. Побочная польза: мировой зелёный вектор больше не едет в российский APK
  мёртвым грузом.
  Российская иконка генерируется `tools/generate_russia_app_icons.py` из `gribnye_icon.png`
  и `gribnye_wood.png`; **рама у неё — фоновый слой во всю площадь**, разбор почему — в KDoc
  скрипта.
- `HOST_EDITION` — `src/world/kotlin/…/HostEdition.kt` и `src/russia/kotlin/…/HostEdition.kt`.

**Ярлык российской debug-сборки** лежит в `src/russiaPlayDebug/res` и `src/russiaRustoreDebug/res`
— полными именами вариантов, дублем на два варианта. Source set «флейвор одного измерения +
сборочный тип» при двух измерениях не существует, а вариант нужен ещё и по приоритету: он
перебивает `src/debug/res`, иначе российская debug-сборка звалась бы «Леший dev».

## Редакция — параметр хоста, а не `BuildConfig` внутри `shared`

`LeshyApplication.onCreate` передаёт `HOST_EDITION` в `initKoin(edition)`, и это единственный
канал. Внутри `shared` редакция нигде не вычисляется.

Причина не в чистоте, а в переносимости: на iOS обе редакции стоят поверх ОДНОГО
`Shared.framework`, отдельного фреймворка на редакцию не существует, и сборочной константы там
взять неоткуда — роль `HostEdition.kt` играет аргумент `MainViewController(edition:)` из Swift.
Способ через `BuildConfig` работает на Android и не переносится на iOS вообще. Разбор —
`docs/russia-edition/README.md`, раздел «Почему iOS откладывается».

## Сборка

- `targetSdk` = 36, понижать нельзя (требование Play для новых приложений и обновлений
  с 31.08.2026)
- формат публикации — AAB
- `versionCode` берётся из `gradle.properties` и только увеличивается; использованные
  значения не переиспользуются. **Нумерации две и они независимы:** `leshy.versionCode`/
  `leshy.versionName` — мировой продукт, `gribnye.versionCode`/`gribnye.versionName` —
  российский (начат с 1: это новое приложение, чужой `versionCode` ему не наследуется)
- flavor'ы `play` и `rustore` имеют **одинаковый** `applicationId`, `applicationIdSuffix`
  у них не использовать
- **исключение — сборочный тип `debug`**: у него `applicationIdSuffix = ".dev"`, чтобы
  локальная сборка вставала на телефон РЯДОМ с версией из Play, а не требовала удалить её
  (подписи разные) вместе со всеми прогулками. В магазины debug не попадает,
  `applicationId` релизных сборок правило не затрагивает. Ярлык отличается ресурсом в
  `src/debug/res`
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
