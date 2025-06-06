plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.android)
    id("kotlinx-serialization")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt) // Подключаем Hilt плагин
    kotlin("kapt")
}

android {
    namespace = "com.rodionov.center"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.kotlinx.serialization.json)

    implementation(project(":data:navigation"))
    implementation(project(":domain"))
    implementation(project(":core:designsystem"))

    //compose navigation
    implementation(libs.compose.navigation)
    //compose
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.compose.ui.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    //hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose) // Если используешь Jetpack Compose

    //koin
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.navigation)
    implementation(libs.koin.compose)
}