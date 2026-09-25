import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}
dependencies {
    implementation(projects.shared)

    implementation(libs.androidx.activity.compose)

    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
}

val releaseStoreFile = findProperty("LESHY_STORE_FILE") as String?
val releaseStorePassword = findProperty("LESHY_STORE_PASSWORD") as String?
val releaseKeyAlias = findProperty("LESHY_KEY_ALIAS") as String?
val releaseKeyPassword = findProperty("LESHY_KEY_PASSWORD") as String?
val hasReleaseSigningConfig = releaseStoreFile != null && releaseStorePassword != null &&
    releaseKeyAlias != null && releaseKeyPassword != null

android {
    namespace = "leshy.mushrooms.map"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    ndkVersion ="29.0.14206865"

    defaultConfig {
        applicationId = "leshy.mushrooms.map"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = property("leshy.versionCode").toString().toInt()
        versionName = property("leshy.versionName").toString()
    }
    // Два независимых измерения, и порядок в списке — это порядок слов в имени варианта:
    // `worldPlayRelease`, `russiaRustoreRelease`. `edition` первым, потому что это РАЗНЫЕ
    // продукты с разными applicationId, а `store` — всего лишь витрина одного продукта.
    //
    // Разница между измерениями принципиальна и объясняет, почему второго продукта нельзя
    // было добиться флейвором `rustore`: у `play`/`rustore` applicationId ОДИН (правило
    // androidApp/CLAUDE.md), то есть на телефоне это одно приложение, и пользователь с
    // «Лешим» из Play получил бы из RuStore не второе приложение, а молчаливое обновление
    // первого — с другой картой. Разбор — docs/russia-edition/README.md.
    flavorDimensions += listOf("edition", "store")
    productFlavors {
        // Мировой «Леший». Всё берётся из defaultConfig и не переопределяется здесь ничем:
        // applicationId `leshy.mushrooms.map` и нумерация версий обязаны остаться ровно теми,
        // что были до появления измерения.
        create("world") { dimension = "edition" }
        // «Грибные прогулки: карта России». Свой applicationId — после публикации он не
        // меняется никогда, как и мировой.
        create("russia") {
            dimension = "edition"
            applicationId = "ru.gribnyeprogulki.map"
            // Своя нумерация, с единицы: это новое приложение в магазине, чужой versionCode
            // ему не наследуется. Мировой `leshy.versionCode` продолжает жить своей жизнью.
            versionCode = property("gribnye.versionCode").toString().toInt()
            versionName = property("gribnye.versionName").toString()
        }
        create("play") { dimension = "store" }
        create("rustore") { dimension = "store" }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    signingConfigs {
        if (hasReleaseSigningConfig) {
            create("release") {
                storeFile = file(releaseStoreFile!!)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }
    buildTypes {
        // Debug-сборка ставится РЯДОМ с той, что пришла из Play, а не поверх неё. Без суффикса
        // это невозможно в принципе: подписи разные, и система требует сначала удалить
        // установленное приложение — то есть стереть все прогулки, чтобы проверить правку.
        // С суффиксом на телефоне живут два независимых приложения, каждое со своими данными.
        //
        // Правило «applicationIdSuffix не использовать» (androidApp/CLAUDE.md) касается
        // flavor'ов play/rustore — те обязаны иметь ОДИН applicationId. Сборочного типа debug
        // оно не касается и никогда не касалось: в магазины он не попадает, а applicationId
        // релизных сборок остаётся ровно `leshy.mushrooms.map`.
        //
        // На applicationId в проекте завязан только FileProvider
        // (`${applicationId}.fileprovider` в манифесте — подставляется сам), так что суффикс
        // ничего не ломает. Ярлык тоже отличается — иначе в лаунчере два одинаковых значка;
        // он переопределён ресурсом в src/debug/res, а не `resValue`: `app_name` уже лежит в
        // src/main/res (и переведён на сорок локалей), и `resValue` столкнулся бы с ним
        // дублем ресурса, тогда как source set сборочного типа штатно перекрывает main.
        getByName("debug") {
            applicationIdSuffix = ".dev"
        }
        getByName("release") {
            if (hasReleaseSigningConfig) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                file("proguard-rules.pro")
            )
            ndk {
                debugSymbolLevel = "SYMBOL_TABLE"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}