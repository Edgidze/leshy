# Релизная подготовка Android — инструкция для агента

Путь в репозитории: `docs/release/android-release-setup.md`

Приложение нигде не опубликовано. Цель — довести проект до состояния, когда подписанный
минифицированный AAB грузится в Play Console на internal testing и не падает на реальном
устройстве.

Задачи выполнять **строго по порядку**, каждая — отдельная сессия агента (`/clear` между
задачами). Этот файл в контекст автоматически не подтягивается: ссылайтесь на него явно,
например «выполни задачу 3 из `docs/release/android-release-setup.md`».

---

## Состояние на момент составления

- переименование пакета и namespace завершено, ветка слита
- история схем Room цела: `1.json … 12.json` без пропусков, каталог назван по актуальному
  FQN `leshy.mushrooms.map.data.local.LeshyDatabase`
- **версия 12 — первая, которая будет опубликована**; баз версий 1–11 вне машины
  разработчика не существует и не появится
- инструментальные тесты миграций написаны и проходят: цепочечный v1→v12 плюс тесты
  сохранности данных для `MIGRATION_7_8` и `MIGRATION_9_10`
- `fallbackToDestructiveMigration` отсутствует в проекте намеренно

---

## Инварианты проекта

Эти решения приняты и **не подлежат изменению агентом**. Если задача выглядит так, что
требует их нарушить — остановиться и спросить.

| Параметр | Значение | Почему нельзя менять |
|---|---|---|
| `applicationId` | `leshy.mushrooms.map` | привязан к листингу Play навсегда |
| `namespace` (androidApp) | `leshy.mushrooms.map` | совпадает с applicationId |
| `namespace` (shared) | `leshy.mushrooms.map.shared` | namespace обязан быть уникален в проекте |
| `targetSdk` | `36` | требование Play для новых приложений и обновлений с 31.08.2026 |
| Формат публикации | AAB | требование Play для новых приложений |
| Подпись | собственный app signing key, один и тот же для Play и RuStore | иначе сборки из разных сторов несовместимы |
| `versionCode` | монотонно растёт, значения не переиспользуются | Play навсегда запоминает использованные |
| Flavor'ы | `play` и `rustore`, **одинаковый** `applicationId`, без `applicationIdSuffix` | иначе получатся два разных приложения |
| Keystore | никогда не в репозитории и не в контексте агента | компрометация ключа необратима |
| `fallbackToDestructiveMigration` | отсутствует, в том числе в debug | в релизе молча стирает базу пользователя при ошибке миграции |

**Запрещённый способ «починки»:** отключать R8 (`android.enableR8.fullMode=false`,
`isMinifyEnabled=false`) для обхода краша. Если правило не находится — задокументировать
проблему и остановиться, не отключать минификацию.

---

## Задача 1. Каркас релизной конфигурации

**Модель: Sonnet.** Механическая правка конфигов.

1. В `androidApp/build.gradle.kts`:
   - `targetSdk = 36` и `compileSdk = 36` — поднять значения в `libs.versions.toml`,
     способ чтения через version catalog оставить как есть; `minSdk` не трогать
   - `versionCode` и `versionName` вынести в `gradle.properties`
     (`leshy.versionCode`, `leshy.versionName`) и читать оттуда
2. Добавить flavor-измерение:

```kotlin
flavorDimensions += "store"
productFlavors {
    create("play")    { dimension = "store" }
    create("rustore") { dimension = "store" }
}
```

   `applicationId` остаётся в `defaultConfig`, во flavor'ах его **не переопределять**
   и `applicationIdSuffix` не использовать.

3. В `buildTypes.release`:

```kotlin
isMinifyEnabled = true
isShrinkResources = true
proguardFiles(
    getDefaultProguardFile("proguard-android-optimize.txt"),
    file("proguard-rules.pro")
)
ndk { debugSymbolLevel = "SYMBOL_TABLE" }
```

4. Проверить, что появление flavor'ов не сломало таски инструментальных тестов миграций —
   они теперь называются иначе (с именем flavor'а в середине).

**Критерий приёмки:** `./gradlew :androidApp:assemblePlayRelease` доходит до этапа R8
(падение на самом R8 на этом шаге ожидаемо и допустимо); `./gradlew :androidApp:tasks`
показывает таски обоих flavor'ов; тесты миграций по-прежнему запускаются и проходят.

---

## Задача 2. Подпись без утечки секретов

**Модель: Sonnet.** Keystore генерирует человек, не агент.

Ключ уже создан разработчиком, параметры лежат в `~/.gradle/gradle.properties`.
Агент пишет только обвязку.

1. Добавить в `.gitignore`: `*.jks`, `*.keystore`, `keystore.properties`,
   `local.properties`
2. В `build.gradle.kts` читать параметры из gradle-свойств по именам
   `LESHY_STORE_FILE`, `LESHY_STORE_PASSWORD`, `LESHY_KEY_ALIAS`, `LESHY_KEY_PASSWORD` —
   с graceful fallback: если свойств нет, release собирается неподписанным и сборка
   не падает
3. **Не запрашивать и не логировать значения** этих свойств
4. Добавить в README раздел о выпуске ключа — команда `keytool` с плейсхолдерами вместо
   реальных паролей

**Критерий приёмки:** `git status` чист после сборки; `grep -r` по репозиторию не находит
ни пароля, ни пути к `.jks`; подписанный AAB собирается; сборка без gradle-свойств
проходит и даёт неподписанный артефакт.

---

## Задача 3. Правила R8 — стартовый набор

**Модель: Sonnet для написания, Opus если после двух итераций краш не локализуется.**

Записать в `androidApp/proguard-rules.pro`:

```proguard
# --- атрибуты, нужные serialization / Koin / Room ---
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
-keepattributes Signature,InnerClasses,EnclosingMethod
-keepattributes SourceFile,LineNumberTable

# --- kotlinx.serialization ---
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# --- маршруты Compose Navigation (type-safe routes сериализуются,
#     имена классов уходят в serial name и в сохраняемое состояние) ---
-keep class leshy.mushrooms.map.ui.navigation.** { *; }

# --- Room ---
-keep class * extends androidx.room.RoomDatabase { <init>(); }
-keep @androidx.room.Entity class *
-keepclassmembers class * {
    @androidx.room.TypeConverter <methods>;
}

# --- MapLibre (JNI) ---
-dontwarn org.maplibre.**

# --- временно, для проверки; убрать в конце задачи 3 ---
-printseeds
```

**Обязательная проверка, что keep-правило сработало.** R8 молчит, если правило не совпало
ни с одним классом, — краш вылезет только в runtime:

```bash
./gradlew :androidApp:assemblePlayRelease
grep "ui.navigation" androidApp/build/outputs/mapping/playRelease/seeds.txt
```

Пусто — пакет указан неверно, остановиться и разобраться. Есть совпадения — убрать
`-printseeds`.

**Критерий приёмки:** `seeds.txt` содержит классы маршрутов; файл не содержит заглушек
вида `-keep class ** { *; }`; каждое правило снабжено комментарием.

---

## Задача 4. Прогон release-билда и итеративная починка

**Модель: Sonnet для цикла собрать → прочитать стектрейс → добавить правило.
Opus — если один и тот же краш не чинится за две-три итерации.**

1. Собрать `assemblePlayRelease`, поставить на устройство
2. Пройти вручную, обязательно включая:
   - холодный старт и создание БД с нуля
   - **миграцию Room** — подложить БД предыдущей версии
   - открытие карты MapLibre, добавление места
   - навигацию по всем экранам (type-safe routes — главный кандидат на падение)
   - отображение изображений категорий грибов
   - восстановление состояния после убийства процесса системой
3. Каждый краш: `--stacktrace`, деобфускация через `retrace mapping.txt stacktrace.txt`,
   точечное правило в `proguard-rules.pro`

Помнить: `fallbackToDestructiveMigration` в проекте отсутствует, поэтому ошибка миграции
проявится исключением, а не тихой потерей данных. Это намеренно — не «чинить» добавлением
fallback'а.

**Критерий приёмки:** все сценарии проходят на минифицированной сборке; `mapping.txt`
существует в `androidApp/build/outputs/mapping/playRelease/`.

---

## Задача 5. Аудит ресурсов, вырезаемых shrinkResources

**Модель: Sonnet.** Сначала разведка отдельной сессией, потом правки.

Самый вероятный тихий баг: ~408 изображений категорий грибов. Если они грузятся по
строковому имени, ресурс-шринкер удалит их без единой ошибки при сборке.

1. Найти все динамические обращения к ресурсам: `getIdentifier(`, `"drawable/" +`,
   конкатенацию имён, чтение имени картинки из JSON
2. Для каждого места — одно из двух:
   - **предпочтительно:** держать изображения в `composeResources/files/` или `assets/`
     и обращаться по пути, а не через `R.drawable`
   - иначе: `keep.xml` в ресурсах android-таргета с
     `tools:keep="@drawable/mushroom_*"` и явным перечислением префиксов
3. Проверить `androidApp/build/outputs/mapping/playRelease/resources.txt` — там список удалённого.
   Изображений грибов там быть не должно

**Критерий приёмки:** на минифицированной сборке открываются карточки минимум 20 случайных
категорий, включая по одной из начала и конца алфавита; `resources.txt` не содержит
удалённых картинок грибов.

---

## Задача 6. Аудит merged manifest

**Модель: Sonnet.**

1. Прочитать merged manifest из
   `androidApp/build/intermediates/merged_manifests/playRelease/`
2. Для каждого `uses-permission`: нужен ли, из какой библиотеки пришёл, можно ли убрать
3. Красные флаги:
   - `ACCESS_BACKGROUND_LOCATION` — отдельная форма-декларация Google с видеодемонстрацией
     и высокий шанс отказа. Для дневника находок почти наверняка не нужен
   - `READ_MEDIA_IMAGES` / `READ_EXTERNAL_STORAGE` — по возможности заменить на Photo
     Picker и убрать разрешение совсем
   - `MANAGE_EXTERNAL_STORAGE` — отказ практически гарантирован
   - `QUERY_ALL_PACKAGES` — требует обоснования
4. Лишнее вырезать через `tools:node="remove"`

**Критерий приёмки:** `docs/release/permissions.md` со списком разрешений и обоснованием
каждого.

---

## Задача 7. Юридический и лицензионный минимум

**Модель: Sonnet.**

1. Атрибуция OSM / OpenFreeMap **видимая на экране карты** — условие лицензии ODbL,
   не рекомендация. Проверить, что не скрыта и не обрезана
2. Дисклеймер о съедобности — в самом приложении (экран «О приложении» плюс ненавязчиво
   в карточке вида), не только в описании в сторе
3. Найти и заменить изображение с лицензией CC-BY-NC — несовместимо с платным
   распространением
4. Экран «Лицензии» со списком OSS-зависимостей

**Критерий приёмки:** `docs/release/licenses.md` со сводкой источников данных и
изображений и их лицензий; ни одной NC-лицензии в поставке.

---

## Задача 8. iOS-заготовка

**Модель: Sonnet.** Делать только когда Android дошёл до closed testing.

1. `ITSAppUsesNonExemptEncryption = false` в `Info.plist` (если не используется
   нестандартная криптография) — иначе каждый билд будет спрашивать про экспорт
2. `PrivacyInfo.xcprivacy` с required-reason API: `UserDefaults`, работа с файлами,
   `systemUptime` — перечислить фактически используемые
3. Проверить, что все сторонние SDK в KMP-цепочке поставляют собственные privacy
   manifest'ы и подписи, иначе App Store Connect отклонит загрузку
4. Bundle ID — `leshy.mushrooms.map`, тот же, что `applicationId`

---

## Порядок выхода в Play Console

Не часть задач агента, но определяет, когда их делать.

1. **Создание приложения в консоли.** В момент создания выбрать загрузку **собственного**
   app signing key через утилиту PEPK. Позже эта опция недоступна. Если дать Google
   сгенерировать ключ, подписи Play и RuStore разойдутся навсегда.
2. **Internal testing** — сразу после первого рабочего минифицированного билда. Ревью нет,
   в требование 12/14 не засчитывается. Здесь ловятся R8-краши.
3. **Closed testing** — когда приложение стабильно. 12 тестировщиков, 14 дней подряд
   непрерывного участия. Обновления в течение окна часы не сбрасывают и приветствуются,
   но краш, из-за которого тестировщик удалит приложение, стоит потерянного участника.
4. **Production** — заявка после закрытия окна.

---

## Сводка по моделям

| Задача | Модель | Обоснование |
|---|---|---|
| 1. Каркас конфигурации | Sonnet | механическая правка |
| 2. Подпись | Sonnet | обвязка, ключ делает человек |
| 3. Правила R8 | Sonnet | известный шаблон + проверка seeds.txt |
| 4. Итеративная починка крашей | Sonnet → Opus | эскалация после 2–3 неудачных итераций |
| 5. Аудит ресурсов | Sonnet | grep и рефакторинг |
| 6. Аудит манифеста | Sonnet | чтение и вычёркивание |
| 7. Лицензии и дисклеймеры | Sonnet | тексты и проверки |
| 8. iOS-заготовка | Sonnet | конфигурационные файлы |
| Планирование, если что-то пошло не так | Opus | межмодульные эффекты |

**Практическое правило:** планирующий проход — Opus, исполнение — Sonnet. Opus нужен там,
где проблема размазана по нескольким слоям сразу: например взаимодействие R8 full mode,
`kotlinx.serialization` и type-safe routes Compose Navigation, где стектрейс указывает
не туда, где реальная причина.
