import io.github.frankois944.spmForKmp.swiftPackageConfig
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidxRoom)
    alias(libs.plugins.spmForKmp)
    alias(libs.plugins.aboutlibraries)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }

        iosTarget.swiftPackageConfig {
            dependency {
                remotePackageVersion(
                    url = uri("https://github.com/maplibre/maplibre-gl-native-distribution.git"),
                    packageName = "maplibre-gl-native-distribution",
                    version = libs.versions.maplibreIos.get(),
                    products = { add("MapLibre", exportToKotlin = true) },
                )
            }
        }

        // The SPM-built MapLibre.xcframework isn't on the app's default framework
        // search/runtime path, so both the Gradle-built framework and the final
        // Xcode app binary need to be pointed at the plugin's scratch output.
        val variant = when (iosTarget.targetName) {
            "iosArm64" -> "arm64-apple-ios"
            "iosSimulatorArm64" -> "arm64-apple-ios-simulator"
            else -> error("Unrecognized target: ${iosTarget.targetName}")
        }
        val rpath = "${layout.buildDirectory.get()}/spmKmpPlugin/${iosTarget.targetName}/scratch/$variant/release/"
        iosTarget.binaries.all { linkerOpts("-F$rpath", "-rpath", rpath) }
    }

    androidLibrary {
        namespace = "leshy.mushrooms.map.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
        withDeviceTest {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.koin.android)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.activity.compose)
            // org.maplibre.gl:android-sdk pulls this in at runtime already (it's what backs
            // HttpRequestUtil.setOkHttpClient) but only as an implementation dep of its own, so it
            // isn't visible on our compile classpath without declaring it directly — needed to build
            // the Call.Factory/Interceptor/Response types for AndroidPinnedStyleInterceptor. Version
            // pinned to match what's already resolved at runtime (see :shared:dependencies
            // androidRuntimeClasspath) to avoid a second copy on the classpath.
            implementation(libs.okhttp)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.iconsExtended)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.backhandler)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.maplibre.compose)
            implementation(libs.androidx.datastore.preferences.core)
            implementation(libs.coil.compose)
            implementation(libs.okio)
            implementation(libs.aboutlibraries.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.okio.fakefilesystem)
        }
        getByName("androidDeviceTest").dependencies {
            implementation(libs.androidx.room.testing)
            implementation(libs.androidx.testExt.junit)
            implementation(libs.androidx.test.runner)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

/**
 * Версия приложения в общем коде — «О приложении» показывает её на обеих платформах, а брать её
 * там неоткуда: `BuildConfig` есть только у Android, `CFBundleShortVersionString` — только у iOS.
 * Вместо `expect`/`actual` под две однострочные реализации генерируется один общий файл из тех же
 * свойств `gradle.properties`, что читает `androidApp` (`leshy.versionName`/`leshy.versionCode`), —
 * так значение заведомо одно и то же во всех трёх местах.
 */
val generateAppVersion by tasks.registering {
    // Значения и каталог — локальные переменные ВНУТРИ блока регистрации, а не поля скрипта:
    // `doLast` иначе захватывает объект build-скрипта целиком, и configuration cache отказывается
    // его сериализовать («cannot serialize Gradle script object references»).
    val versionName = providers.gradleProperty("leshy.versionName")
    val versionCode = providers.gradleProperty("leshy.versionCode")
    val outputDir = layout.buildDirectory.dir("generated/appVersion/kotlin")
    inputs.property("versionName", versionName)
    inputs.property("versionCode", versionCode)
    outputs.dir(outputDir)
    doLast {
        val dir = outputDir.get().asFile.resolve("leshy/mushrooms/map")
        dir.mkdirs()
        dir.resolve("AppVersion.kt").writeText(
            """
            |// Сгенерировано задачей generateAppVersion (shared/build.gradle.kts). Не редактировать.
            |package leshy.mushrooms.map
            |
            |const val APP_VERSION_NAME: String = "${versionName.get()}"
            |const val APP_VERSION_CODE: Int = ${versionCode.get()}
            |
            """.trimMargin()
        )
    }
}

kotlin.sourceSets.commonMain { kotlin.srcDir(generateAppVersion) }

/**
 * Список зависимостей и их лицензий для экрана «О приложении» — обязательство Apache 2.0 §4(a) и
 * BSD («in the documentation and/or other materials provided with the distribution») выполняется
 * только тем, что доехало до пользователя вместе с приложением.
 *
 * Плагин обходит РЕАЛЬНЫЙ граф зависимостей, включая транзитивные, — в этом и был смысл его брать,
 * а не вести список руками. Генерация НЕ автоматическая (это обычный плагин, а не его
 * `.android`-вариант): результат — `composeResources/files/aboutlibraries.json` — коммитится, и
 * после любого изменения зависимостей его надо пересобрать:
 *
 *     ./gradlew :shared:exportLibraryDefinitions
 *
 * **Версия плагина и библиотеки — 14.2.1, а не последняя 15.x, и это вынужденно:**
 * `aboutlibraries-core-android` начиная с 15.0 требует `compileSdk 37`, а проект собирается на 36
 * (AGP 9.0.1 больше 36 и не рекомендует). Поднимать compileSdk всему приложению ради экрана с
 * лицензиями — несоразмерный риск перед релизом; из 14.2.1 берётся только разбор JSON, UI-модули
 * (где и живёт привязка к версии Compose) не подключены вовсе. Появится AGP с поддержкой 37 —
 * можно вернуться на 15.x и включить там `library.mergePlatformArtifacts`, схлопывающий
 * KMP-публикации в корневую координату (в 14.2.1 этой опции ещё нет, поэтому в списке соседствуют
 * `...-android` и `...-jvm` варианты одной библиотеки — шумно, но не неверно).
 */
aboutLibraries {
    collect {
        // Ручные дополнения к графу — см. `shared/config/README.md` (там MapLibre для iOS,
        // который приезжает через SPM и в classpath Gradle не виден).
        configPath = file("config")
        // Ходить в API GitHub за лицензиями, которых нет в POM, не нужно: весь набор — Apache 2.0
        // и BSD, они определяются из метаданных, а сетевой шаг сделал бы генерацию невоспроизводимой.
        fetchRemoteLicense = false
        fetchRemoteFunding = false
        // BOM'ы (`koin-bom`, `kotlinx-coroutines-bom`) — не код, а таблица версий; в поставку они
        // не попадают, и в списке лицензий им делать нечего.
        includePlatform = false
    }
    export {
        outputFile = file("src/commonMain/composeResources/files/aboutlibraries.json")
        prettyPrint = true
        // Экран показывает имя, версию, копирайт и текст лицензии — всё прочее только раздувает
        // ресурс, который целиком лежит в APK/IPA.
        excludeFields.addAll("funding", "description", "organization", "scm", "developers")
    }
}