plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.devtools.ksp")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.thongars.utilities"
    compileSdk = 34

    defaultConfig {

        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Coroutines
    implementation(libs.kotlinx.coroutineAndroid)
    implementation(libs.kotlinx.coroutine)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.squareup.retrofit2)

    // Testing
    api(libs.junit)
    api(libs.androidx.junit)
    api(libs.androidx.espresso.core)
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.ui.test.junit4)

    api(libs.kotlin)
    api(libs.junit)
    api(libs.mockito.core)
    api(libs.powermock.core)
    api(libs.powermock.junit4)
    api(libs.powermock.mockito2)
    api(libs.core.testing)
    api(libs.io.mockk)
    api(libs.assertj.core)
    api(libs.androidx.test.runner)
    api(libs.androidx.test.rules)
    api(libs.kotlinx.coroutines.test)

    api(libs.androidx.test.core)
    api(libs.androidx.junit)
    api(libs.androidx.test.runner)
    api(libs.androidx.test.rules)
    api(libs.androidx.paging.test)
    api("app.cash.turbine:turbine:0.7.0")

}

kapt {
    correctErrorTypes = true
}