# Аудит зависимостей перед публикацией — сентябрь 2026

Supply-chain проверка перед отправкой в Google Play / App Store. Чеклист — навык
`pre-release-audit` (`.claude/skills/pre-release-audit/SKILL.md`), там же обоснование,
почему в проекте нет постоянного Gradle dependency locking.

Дата аудита: **2026-09-08**. Состояние: `main` @ `cbabb35`, версия `1.0` (versionCode 3),
AGP 9.0.1, Kotlin 2.4.0, Gradle 9.1.0.

**Вердикт: подозрительного или вредоносного не найдено** — ни в поставке, ни в графе
сборки, ни в собственном коде. Семь известных уязвимостей найдены только на classpath
Gradle-плагинов, в APK/IPA не попадает ни одна. Три пункта оставлены на решение владельца
(раздел 6).

Машина не резолвит DNS (см. память `project-no-dns-use-doh`), поэтому все сетевые шаги
шли через Cloudflare DoH (`https://1.1.1.1/dns-query`) и `curl --resolve`.

---

## 1. Итог в цифрах

| Что | Значение |
|---|---|
| Уникальных координат в поставке | 327 |
| Запросов в OSV.dev (с базовыми именами без KMP-суффиксов) | 330 |
| Известных уязвимостей в поставке | **0** |
| Артефактов, сверенных с апстримом побайтово | все `.jar`/`.aar`/`.klib` графа |
| Расхождений с апстримом | **0** |
| Координат на classpath сборки | 160 |
| Advisories на classpath сборки | 7 (в поставку не едут) |
| Нативных `.so` в APK | 4 (все из проверенных артефактов) |
| Внешних адресов во всём приложении | 2 |

---

## 2. Известные уязвимости — OSV.dev

Граф собран из трёх конфигураций, чтобы покрыть обе платформы и релизный вариант Android:

```bash
./gradlew -q :shared:dependencies    --configuration androidRuntimeClasspath
./gradlew -q :androidApp:dependencies --configuration playReleaseRuntimeClasspath
./gradlew -q :shared:dependencies    --configuration iosArm64CompileKlibraries
```

Из вывода вытащены разрешённые версии (с учётом стрелок `->` конфликт-резолюции), к ним
добавлены базовые имена без KMP-суффиксов (`-android`, `-jvm`, `-iosarm64`, `-uikitarm64`,
`-jdkN`) — уязвимости обычно заводят на корневую координату, а не на платформенную
публикацию. Итого 330 запросов в `https://api.osv.dev/v1/querybatch`, ecosystem `Maven`.

**Результат: `queried: 330, with vulns: 0`.**

Отдельно прогнан позитивный контроль — тем же запросом, заведомо дырявые версии, чтобы
отличить «чисто» от «эндпоинт молча вернул пустой ответ»:

```
org.apache.logging.log4j:log4j-core:2.14.1 → GHSA-3pxv-7cmr-fjr4, GHSA-6hg6-v5c8-fphq, …
com.squareup.okhttp3:okhttp:4.9.0          → GHSA-3cqm-mf7h-prrj
com.google.code.gson:gson:2.8.5            → GHSA-4jrv-ppp4-jm57
```

API отвечает — ноль настоящий.

---

## 3. Происхождение артефактов

### 3.1 Репозитории по-прежнему сужены

`settings.gradle.kts` не изменился по сути с момента постановки правила:

- `dependencyResolutionManagement`: `google()` с `includeGroupAndSubgroups` на `androidx`,
  `com.android`, `com.google` — плюс `mavenCentral()`;
- `pluginManagement`: те же два плюс `gradlePluginPortal()`.

Сторонних, приватных и произвольных `maven { url … }` репозиториев нет. Нет ни `buildSrc`,
ни init-скриптов в `~/.gradle/init.d`, которые могли бы дописать репозиторий мимо файла.

### 3.2 Побайтовая сверка с апстримом

Это сильнее, чем «нет CVE»: проверка ловит подмену в локальном кэше Gradle и MITM при
скачивании. Каждый закэшированный `.jar`/`.aar`/`.klib` из графа сверен по sha1 с
`repo1.maven.org` и `dl.google.com`.

- 271 координата — совпадение с опубликованным `<файл>.sha1`;
- оставшиеся 24 (`androidx.*:*-iosarm64`, 50 файлов) — совпадение с sha1, объявленным
  внутри апстримной Gradle Module Metadata (`<artifact>-<version>.module`); прямых
  `.sha1`-файлов под этими именами Google Maven не отдаёт, поэтому источником истины взят
  сам `.module`;
- координаты без бинарника (BOM'ы, `*-metadata`, platform-модули) пропущены — сверять
  нечего.

**Расхождений ноль.**

### 3.3 Gradle wrapper

```
gradle-wrapper.jar               76805e32c009c0cf0dd5d206bddc9fb22ea42e84db904b764f3047de095493f3
gradle-9.1.0-wrapper.jar.sha256  76805e32c009c0cf0dd5d206bddc9fb22ea42e84db904b764f3047de095493f3  ✓

distributionSha256Sum            a17ddd85a26b6a7f5ddb71ff8b05fc5104c0202c6e64782429790c933686c806
gradle-9.1.0-bin.zip.sha256      a17ddd85a26b6a7f5ddb71ff8b05fc5104c0202c6e64782429790c933686c806  ✓
```

Официальные суммы взяты с `downloads.gradle.org` (`services.gradle.org` отдаёт на него 301).
`validateDistributionUrl=true`. Wrapper jar — единственный бинарь, лежащий в git
(`git ls-files` по расширениям `jar|zip|so|dylib|a|aar|framework|bin|exe|dex|apk|aab`).

### 3.4 iOS / SPM

Через SPM приезжает ровно один пакет — MapLibre 6.25.1:

```swift
.binaryTarget(
    name: "MapLibre",
    url: "https://github.com/maplibre/maplibre-native/releases/download/ios-v6.25.1/MapLibre.dynamic.xcframework.zip",
    checksum: "57151da66862c3ea2c1f38ce89b41204eee4de34ec706f3d6aca2b2bab66d06e")
```

Релиз официальной организации `maplibre`, checksum `binaryTarget` SPM проверяет сам при
скачивании. `Package.resolved` пинит ревизию `40e1a0db6d055abf8a1b6e2f6127a8bb6e895cf8`.
CocoaPods и других пакетов в проекте нет; в `iosApp.xcodeproj` нет ни одного
`XCRemoteSwiftPackageReference`.

### 3.5 Менее устоявшиеся зависимости

Всё, что не от `androidx.*` / `org.jetbrains.*` / Google / Square / Touchlab:

| Проект | Звёзд | Лицензия | Владелец | Последний пуш |
|---|---|---|---|---|
| `frankois944/spm4Kmp` (spmForKmp 1.9.1) | 395 | MIT | пользователь | 2026-09-01 |
| `maplibre/maplibre-compose` 0.13.0 | 567 | BSD-3-Clause | организация | 2026-09-08 |
| `mikepenz/AboutLibraries` 14.2.1 | 4428 | Apache-2.0 | пользователь, с 2014 | 2026-08-28 |

Обе плагин-jar-ки (`io.github.frankois944:plugin:1.9.1`,
`com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin:14.2.1`) байт-в-байт совпали с
Gradle Plugin Portal (`plugins.gradle.org/m2`).

**spmForKmp — единственная точка, где доверие идёт одному человеку.** Смягчающее
обстоятельство: плагин работает только на сборке, его код в приложение не попадает, а
единственное, чем он управляет — разрешение того самого MapLibre-пакета, у которого своя
контрольная сумма (3.4).

---

## 4. Собственный код

### 4.1 Динамических механизмов нет ни одного

`grep` по `shared/src`, `androidApp/src`, `iosApp` (`*.kt`, `*.swift`, `*.java`) на
`Runtime.getRuntime`, `ProcessBuilder`, `System.load`/`loadLibrary`, `DexClassLoader`,
`PathClassLoader`, `Class.forName`, рефлексию, `WebView`/`loadUrl`/`evaluateJavascript`/
`addJavascriptInterface`, `dlopen`, `NSTask`, `Base64`, `eval(` — **ноль совпадений**.

### 4.2 Нативный код в APK

Четыре `.so`, все из артефактов, проверенных в 3.2:

```
libdatastore_shared_counter.so   androidx.datastore:datastore-core-android:1.2.1
libandroidx.graphics.path.so     androidx.graphics:graphics-path:1.0.1
libsqliteJni.so                  androidx.sqlite:sqlite-bundled-android:2.7.0
libmaplibre.so                   org.maplibre.gl:android-sdk:13.0.2
```

Все четыре — для `arm64-v8a`, `armeabi-v7a`, `x86`, `x86_64`.

### 4.3 Куда приложение ходит

Полный список внешних адресов в исходниках (кроме XML-namespace и справочных ссылок в
сгенерированном `aboutlibraries.json`):

| Адрес | Где | Зачем |
|---|---|---|
| `https://tiles.openfreemap.org/...` | `ui/map/MapStyle.kt`, `data/style/MapStyle*.kt` | стиль и тайлы карты |
| `https://leshy-mapper.github.io/mushrooms-map/privacy.html` | `ui/components/PrivacyPolicyLink.kt` | ссылка на политику конфиденциальности |

**Аналитики, крешрепортера, рекламных и телеметрийных SDK — ни одного.**

Сетевой код весь свой и весь на виду: `AndroidHttpTextFetcher` (`HttpURLConnection`),
`IosHttpTextFetcher` (`NSURLSession`), `AndroidPinnedStyleInterceptor` (подмена OkHttp-клиента
MapLibre) и `IosPinnedStyleInterceptor` (`NSURLProtocol`).

### 4.4 Разрешения смёрженного релизного манифеста

Проверено повторно, изменений с разбора 2026-08-26 нет — детальный разбор каждой строки
живёт в `docs/release/permissions.md`, здесь только сверка состава:

```
своё:      ACCESS_FINE_LOCATION  ACCESS_COARSE_LOCATION  CAMERA
           FOREGROUND_SERVICE  FOREGROUND_SERVICE_LOCATION  POST_NOTIFICATIONS
транзитив: INTERNET  ACCESS_NETWORK_STATE  ACCESS_WIFI_STATE   (org.maplibre.gl:android-sdk)
служебное: leshy.mushrooms.map.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION  (androidx.core)

нет:       AD_ID · ACCESS_BACKGROUND_LOCATION · READ_PHONE_STATE ·
           READ_MEDIA_IMAGES · READ_EXTERNAL_STORAGE · QUERY_ALL_PACKAGES
```

`aboutlibraries.json` перегенерирован (`./gradlew :shared:exportLibraryDefinitions`) —
diff пустой, список лицензий в поставке актуален.

---

## 5. Уязвимости на classpath сборки

Все семь живут на classpath Gradle-плагинов (`buildEnvironment` корня и `:shared`,
160 координат). Проверено отдельно: ни одна из этих координат не встречается в
runtime-classpath, то есть **в APK и IPA их нет**.

| Артефакт | Уровень | Advisory | Суть | Откуда |
|---|---|---|---|---|
| `org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.0` | MODERATE | `GHSA-r937-wjx7-w2jp` | небезопасная десериализация в Kotlin build cache → выполнение кода; фикс 2.4.20-Beta1 | KGP |
| `org.bouncycastle:bcprov-jdk18on:1.79` | CRITICAL | `GHSA-574f-3g2m-x479` | повторное использование гаммы GOST 28147 CTR после 255 блоков | AGP 9.0.1 |
| `org.bouncycastle:bcprov-jdk18on:1.79` | MODERATE | `GHSA-c3fc-8qff-9hwx` | LDAP-инъекция | AGP 9.0.1 |
| `org.bitbucket.b_c:jose4j:0.9.5` | HIGH | `GHSA-3677-xxcr-wjqv` | DoS через сжатое содержимое JWE | AGP 9.0.1 |
| `org.jdom:jdom2:2.0.6` | HIGH | `GHSA-2363-cqg2-863c` | XXE-инъекция при разборе XML | AGP 9.0.1 |
| `org.bouncycastle:bcpkix-jdk18on:1.79` | MODERATE | `GHSA-wg6q-6289-32hp` | ослабленный криптоалгоритм в модулях bcpkix | AGP 9.0.1 |
| `org.apache.commons:commons-lang3:3.16.0` | MODERATE | `GHSA-j288-q9x7-2f5v` | неконтролируемая рекурсия на длинном вводе | AGP 9.0.1 |

**Практически значима одна — KGP.** Она эксплуатируется через *общий или удалённый* build
cache. В проекте `org.gradle.caching=true` без remote-кэша, то есть вектора сейчас нет.
**Появится общий кэш на CI — поднимать Kotlin обязательно, до того как включать кэш.**

Остальные шесть — транзитив AGP, придут сами со следующей его версией; подменять их руками
не стоит: версии зафиксированы внутри AGP, и разъезд с ним ломает сборку чаще, чем чинит.

---

## 6. Что осталось решить владельцу

Ни один из трёх пунктов не является уязвимостью зависимостей — аудит их не закрывает,
только фиксирует.

### 6.1 Облачный бэкап треков включён по умолчанию — решение стора

`androidApp/src/main/AndroidManifest.xml`: `android:allowBackup="true"` без
`dataExtractionRules` и `fullBackupContent` (в `res/xml/` лежит только `file_paths.xml`).
Значит, весь каталог данных — Room с GPS-треками прогулок и фотографии находок — попадает
в облачный бэкап Google и в перенос на новое устройство.

Для приложения, чья единственная чувствительная сущность — координаты грибных мест,
это должно быть **сознательным решением**, а не умолчанием, и отдельной строкой в
декларации Data safety.

### 6.2 `ACCESS_WIFI_STATE` — строка в анкете Play

Приезжает от `org.maplibre.gl:android-sdk` вместе с `uses-feature android.hardware.wifi`
(разобрано в `docs/release/permissions.md`). Само по себе безобидно, но в анкете Play это
отдельный пункт — учесть вместе с 6.1.

### 6.3 Каст, про который компилятор говорит «никогда не сработает» — на карандаш

`shared/src/iosMain/.../IosPinnedStyleInterceptor.kt:77`, `json as NSString`
(`w: This cast can never succeed`). Сейчас работает за счёт toll-free bridging. Если
однажды перестанет — пиннинг стиля на iOS отвалится молча, а `startLoading` уронит запрос
в никуда: ровно тот класс отказа, который не виден ни в сборке, ни в тестах.

---

## 7. Что перестанет работать со временем

Вторая обязательная часть навыка, не заменяемая supply-chain проверкой.

### A. Устаревание — код не менялся, изменился мир вокруг

Собрано `./gradlew --rerun-tasks :shared:compileAndroidMain :shared:compileKotlinIosArm64`,
прочитаны все 15 строк `w:`.

- **Устаревание одно, в трёх местах:** `BackHandler` → `NavigationEventHandler` —
  `App.kt:239`, `ui/screens/ArchiveScreen.kt:54`, `ui/screens/OnboardingScreen.kt:61`.
  Остальные предупреждения — служебные (`Calling a MapLibre Composable…`,
  `CONFLICTING_OVERLOADS` в `IosPinnedStyleInterceptor`, каст из 6.3).
- **Гейтов по версии ОС четыре**, у каждого ветка «ниже» живая:
  `WalkRecordingService.kt:120` (`Q`), `:345` (`O`), `MainActivity.kt:29` (`TIRAMISU`),
  `AndroidLocationTracker.kt:131` (`S`, добавляет `FUSED_PROVIDER`).
- **`targetSdk` = 36**, `compileSdk` = 36 — поднимать в этом релизе нечего.
- **Вшитый внешний адрес один** — `tiles.openfreemap.org`, и он уже прикрыт пиннингом
  TileJSON в `data/style/MapStyleSourceFreezer.kt`: баг с дрейфом скачанного региона закрыт
  по причине, а не по симптому (см. `ui/map/CLAUDE.md`).
- **Формат данных на диске версионирован** — `EXPORT_SCHEMA_VERSION` в
  `data/export/dto`, пишется в экспорт и читается импортом.

### Б. Своя правка сузила запасной путь

Перепроверены обе точки, где приложение выбирает один источник из нескольких.

- **`AndroidLocationTracker`** — регрессия 31.08.2026 закрыта по-настоящему, а не заплаткой:
  подписка ставится на все провайдеры сразу, победитель выбирается по факту
  (`isBetterFix` — по свежести и точности), `isAvailable()` и `track()` согласованы по
  требованию к разрешению, а пустой список подписавшихся слушателей закрывает поток вместо
  того, чтобы висеть молча пустым.
- **`MapStyleSourceFreezer`** — per-source best effort: не ответивший или неразобранный
  TileJSON оставляет источник как был, а не превращает стиль в пустую карту. Возврат
  байт-в-байт того же JSON, если ничего не разрешилось, — чтобы «Обновить данные карты»
  не рапортовало о несуществующем изменении.

Обе точки отвечают на вопрос «источник существует, но молчит», и отвечают внятно.

---

## 8. Как перезапустить

```bash
# 1. граф поставки
./gradlew -q :shared:dependencies    --configuration androidRuntimeClasspath
./gradlew -q :androidApp:dependencies --configuration playReleaseRuntimeClasspath
./gradlew -q :shared:dependencies    --configuration iosArm64CompileKlibraries

# 2. граф сборки
./gradlew -q buildEnvironment
./gradlew -q :shared:buildEnvironment

# 3. предупреждения компилятора
./gradlew --rerun-tasks :shared:compileAndroidMain :shared:compileKotlinIosArm64

# 4. манифест и лицензии
./gradlew :androidApp:processPlayReleaseMainManifest
./gradlew :shared:exportLibraryDefinitions   # diff должен остаться пустым
```

Дальше — координаты в `https://api.osv.dev/v1/querybatch` (ecosystem `Maven`, обязательно
с позитивным контролем), sha1 кэшированных артефактов против `repo1.maven.org` /
`dl.google.com`, суммы wrapper'а против `downloads.gradle.org`. Сеть — только через DoH
`1.1.1.1` и `curl --resolve`.

Это осознанная альтернатива постоянному Gradle dependency locking
(`gradle/verification-metadata.xml`): разовая широкая проверка перед релизом вместо
ongoing maintenance cost. **Пересмотреть решение стоит,** если появится общий build cache
на CI (см. 5) или если в графе прибавится зависимостей от одиночных мейнтейнеров (сейчас
такая одна — 3.5).
