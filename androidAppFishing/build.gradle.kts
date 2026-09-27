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
    implementation(projects.fishing)

    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
}

android {
    namespace = "klev.fishing.map"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    ndkVersion = "29.0.14206865"

    defaultConfig {
        // РАБОЧЕЕ имя продукта, не окончательное. applicationId после публикации не меняется
        // никогда (правило androidApp/CLAUDE.md), поэтому до первой подачи в магазин его обязан
        // утвердить владелец.
        applicationId = "klev.fishing.map"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        // Версия задана здесь, а не свойствами в gradle.properties, СОЗНАТЕЛЬНО: аддитивность
        // прототипа проверяется тем, что `git diff -- gradle.properties` пуст. Переедет в
        // свойства, когда продукт перестанет быть прототипом.
        versionCode = 1
        versionName = "0.1"
    }
    // Осей флейворов нет: по решению владельца собирается только мировая редакция. Российская
    // добавляется тем же способом, что у грибов (ось `edition`), когда до неё дойдёт.
    buildTypes {
        // Как у грибного хоста: debug ставится РЯДОМ с релизной сборкой, а не поверх неё.
        getByName("debug") {
            applicationIdSuffix = ".dev"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
