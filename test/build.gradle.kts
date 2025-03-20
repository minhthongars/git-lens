plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.thongars.test"
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // Testing
    api(libs.junit)
    api(libs.androidx.junit)
    api(libs.androidx.espresso.core)
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.ui.test.junit4)

    api(libs.kotlin)
    api(libs.mockito.core)
    api(libs.powermock.core)
    api(libs.powermock.junit4)
    api(libs.powermock.mockito2)
    api(libs.core.testing)
    api(libs.io.mockk)
    api(libs.assertj.core)
    api(libs.androidx.test.runner)
    api(libs.kotlinx.coroutines.test)

    api(libs.androidx.test.core)
    api(libs.androidx.test.rules)
    api(libs.androidx.paging.test)
}