plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()

    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
        }
    }

    sourceSets {

        all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
            }
        }

        val commonMain by getting {
            dependencies {
                Dependencies.Shared.commonMain.forEach {
                    implementation(it)
                }
                Dependencies.Shared.commonKotlin.forEach {
                    implementation(kotlin(it))
                }
            }
        }
        val commonTest by getting {
            dependencies {
                Dependencies.Shared.commonTest.forEach {
                    implementation(it)
                }
                Dependencies.Shared.commonKotlinTest.forEach {
                    implementation(kotlin(it))
                }
            }
        }
        val androidMain by getting {
            dependencies {
                Dependencies.Shared.androidMain.forEach {
                    implementation(it)
                }
            }
        }
        val iosMain by getting {
            dependencies {
                Dependencies.Shared.iosMain.forEach {
                    implementation(it)
                }
            }
        }
        val iosTest by getting
    }
}

android {
    namespace = "com.java.cherrypick"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
    }
}
