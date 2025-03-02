import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.devtools.ksp")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {

    namespace = "com.thongars.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField(type = "String",name = "API_KEY", "\"${properties.getProperty("STAGING_API_KEY")}\"")
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

    implementation(project(":domain"))
    implementation(project(":utilities"))

    // Hilt (Dependency Injection)
    implementation(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.android.compiler)
    implementation(libs.androix.hiltNavigationCompose)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutineAndroid)
    implementation(libs.kotlinx.coroutine)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Room (Database)
    implementation(libs.androix.room.runtime)
    ksp(libs.androidx.room.complier)
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.paging)

    // Networking
    implementation(libs.squareup.retrofit2)
    implementation(libs.squareup.retrofit2.converter.moshi)
    implementation(libs.squareup.okhttp.logging.interceptor)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

}

kapt {
    correctErrorTypes = true
}