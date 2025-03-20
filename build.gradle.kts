// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
    application
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.serialization") version "1.4.21"
}

extra["koverIncludeClasses"] = listOf(
    "*ViewModel",
    "*ViewModel\$*",
    "*UseCase",
    "*UseCase\$*",
    "*Repository",
    "*Repository\$",
    "*RepositoryImpl",
    "*RepositoryImpl\$"
)