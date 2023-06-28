apply(from = "gradle/ktlint.gradle.kts")

plugins {
    // trick: for the same plugin versions in all sub-modules
    id("app-config-android-plugin").apply(false)
    kotlin("multiplatform").apply(false)
    id("com.android.application").apply(false)
    id("com.android.library").apply(false)
    id("org.jetbrains.compose").apply(false)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
