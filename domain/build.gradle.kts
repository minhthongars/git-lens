plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kover)
}

android {
    namespace = "com.thongars.domain"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

}

dependencies {

    implementation(project(":utilities"))
    testImplementation(project(":test"))

    // Coroutines
    implementation(libs.kotlinx.coroutineAndroid)
    implementation(libs.kotlinx.coroutine)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    //koin
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.android)

    // Room (Database)
    implementation(libs.androix.room.runtime)
    ksp(libs.androidx.room.complier)
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.paging)
}

val koverIncludeClasses: List<String> by rootProject.extra
kover {
    instrumentation {
        excludeTasks += "testReleaseUnitTest"
    }

    filters {
        classes {
            includes.addAll(koverIncludeClasses)
        }
    }
}