plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "dam_a46104.catsndogs"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "dam_a46104.catsndogs.xml"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    // Bloco room{} removido — Room é responsabilidade do :core.
    // Plugins ksp e room.gradle.plugin removidos pelo mesmo motivo.
}

dependencies {
    // Data layer e business logic via :core
    implementation(project(":core"))

    // Base Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Concorrência (viewModelScope nas Activities/ViewModels)
    implementation(libs.kotlinx.coroutines.android)

    // Lifecycle — ViewModels do :app-xml + .asLiveData() para converter Flow→LiveData
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)

    // UI
    implementation(libs.androidx.recyclerview)
    implementation(libs.glide)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}