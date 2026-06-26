import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.android)
}

// Базовый URL тестовой (debug) сборки можно переопределить локально через
// local.properties (BASE_URL_DEBUG) — например, на dev-сервер разработчика.
// По умолчанию debug ходит на тот же прод-бэкенд, что и release.
val prodBaseUrl = "https://competra.ru/api/"
val remoteProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) load(f.inputStream())
}
val baseUrlDebug: String = remoteProps.getProperty("BASE_URL_DEBUG", prodBaseUrl)

android {
    namespace = "com.competra.remote"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"$baseUrlDebug\"")
            buildConfigField("boolean", "IS_TEST_BUILD", "true")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", "\"$prodBaseUrl\"")
            buildConfigField("boolean", "IS_TEST_BUILD", "false")
        }
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(project(":domain"))
    implementation(project(":utils"))

    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.converter.scalars)
    implementation(libs.logging.interceptor)

    //koin
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    debugImplementation(libs.chucker.library)
    releaseImplementation(libs.library.no.op)
}