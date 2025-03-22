plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.thongars.gitlens"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.thongars.gitlens"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }
    }

    signingConfigs {

        create("release") {
            storeFile = file("gl-release-key.jks")
            storePassword = "andro1d-mt"
            keyAlias = "gl-release"
            keyPassword = "andro1d-mt"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isDebuggable = true
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
    packaging {
        resources {
            excludes += "/META-INF/*"
            excludes += "META-INF/*"
        }
    }

}

dependencies {

    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":presentation"))
    implementation(project(":utilities"))
    implementation(project(":test"))

    //koin
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.android)

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

    // Image Loading
    implementation(libs.io.coil)
}