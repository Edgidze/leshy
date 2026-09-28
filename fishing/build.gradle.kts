import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidxRoom)
}

/*
 * Рыбацкий продукт как ДОБАВЛЕНИЕ к репозиторию: модуль зависит на `:shared` и не меняет в нём
 * ничего. Проверяется механически —
 *
 *     git diff --stat main...HEAD -- shared/ androidApp/ gradle.properties
 *
 * должно быть пусто. Это и есть гарантия, что грибные приложения остались теми же: не обещание, а
 * команда. Цена — в рыбацкий APK приезжает грибной каталог с иллюстрациями, потому что у Compose
 * Resources нет флейворов и `:shared` отдаёт свои ресурсы целиком. Разбор и план, как это
 * развязать, — `.claude/plans/product-family.md`.
 *
 * Целей iOS здесь пока нет СОЗНАТЕЛЬНО, а не по забывчивости: iOS-хост требует правки
 * `iosApp.xcodeproj/project.pbxproj` и повторения SPM-обвязки MapLibre из `shared/build.gradle.kts`
 * — отдельный шаг с отдельной проверкой. Код лежит в `commonMain` (не в `androidMain`) именно для
 * того, чтобы добавление `iosArm64` осталось правкой одного build-файла.
 */
kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    androidLibrary {
        namespace = "klev.fishing.map.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            api(projects.shared)

            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.iconsExtended)
            implementation(libs.compose.ui)
            // Отдельным артефактом, не частью `compose.ui`: `BackHandler` нужен, чтобы системное
            // «назад» закрывало боковую панель — KMP-версия `ModalNavigationDrawer` сама этого не
            // делает (разбор — `FishingApp`).
            implementation(libs.compose.ui.backhandler)
            implementation(libs.compose.components.resources)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.maplibre.compose)
            // Фото улова: тот же Coil, что у грибных фото — локальный файл по «file://» он
            // открывает на обеих платформах сам, без своего декодера на каждую.
            implementation(libs.coil.compose)
            implementation(libs.androidx.datastore.preferences.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

compose.resources {
    publicResClass = true
    packageOfResClass = "klev.fishing.generated.resources"
}
