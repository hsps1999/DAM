plugins {
    id("com.android.library")
    alias(libs.plugins.ksp)
    alias(libs.plugins.room.gradle.plugin)
    // kotlin("android") NÃO declarado — AGP 9.x aplica automaticamente (built-in Kotlin)
    // kotlinOptions { } NÃO usado — causaria erro com AGP 9.x
}

android {
    namespace = "dam_a46104.catsndogs.core"

    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    room {
        // Room Gradle Plugin (2.7+) requer declaração do schemaDirectory.
        // exportSchema = false em AppDatabase — a pasta ficará vazia, mas a declaração é obrigatória.
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // Concurrency
    implementation(libs.kotlinx.coroutines.android)

    // Lifecycle — necessário para .asLiveData() nos Flow expostos pelo Repository
    implementation(libs.lifecycle.livedata.ktx)

    // Local DB (Room)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Testes
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
