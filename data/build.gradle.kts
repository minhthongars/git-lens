import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
}

android {

    namespace = "com.thongars.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        val localPropertiesFile = project.rootProject.file("local.properties")
        if (localPropertiesFile.exists()) { //for github runner
            val properties = Properties().apply {
                load(localPropertiesFile.inputStream())
            }
            buildConfigField("String", "API_KEY", "\"${properties.getProperty("STAGING_API_KEY")}\"")
        } else {
            logger.warn("local.properties does not exist. Please create this file with the required information!")
        }
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
    packaging {
        resources {
            excludes += "/META-INF/*"
            excludes += "META-INF/*"
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {

    implementation(project(":domain"))
    implementation(project(":utilities"))

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutineAndroid)
    implementation(libs.kotlinx.coroutine)

    //koin
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.android)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Room (Database)
    implementation(libs.androix.room.runtime)
    ksp(libs.androidx.room.complier)
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.paging)

    //ktor

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)

    //implementation(libs.ktor.serialization)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.content.negotiation)

    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.android)

}