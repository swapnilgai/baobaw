apply(from = "gradle/ktlint.gradle.kts")

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("dev.icerock.moko:kswift-gradle-plugin:0.6.1")
        classpath("dev.icerock.moko:resources-generator:0.23.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    // trick: for the same plugin versions in all sub-modules
    id("app-config-android-plugin").apply(false)
    id("com.android.application").version("8.0.2").apply(false)
    id("com.android.library").version("8.0.2").apply(false)
    kotlin("android").version("1.8.21").apply(false)
    kotlin("multiplatform").version("1.8.21").apply(false)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
