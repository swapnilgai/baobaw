plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.codingfeline.buildkonfig") version "0.13.3"
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    ios {
        binaries {
            framework {
                baseName = "shared"
            }
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

        val appMain by creating {
            dependsOn(commonMain)
        }
    }
}

android {
    namespace = "com.java.cherrypick"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
    }
}

buildkonfig {
    packageName = "com.java.cherrypick"
    defaultConfigs {
        val apiKey = extra["api.key"] as String
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "apiKey", apiKey)
    }
}
