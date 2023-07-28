
plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.codingfeline.buildkonfig") version Version.buildkonfig
    kotlin("plugin.serialization") version Version.kotlinVersion
    id("dev.icerock.moko.kswift")
    id("dev.icerock.mobile.multiplatform-resources")
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

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            export("dev.icerock.moko:resources:${Version.mokoResourcesGenerator}")
            export("dev.icerock.moko:graphics:${Version.mokoGraphics}")
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
                Dependencies.Shared.supabase.forEach {
                    implementation(it)
                }
                Dependencies.Shared.iceRock.forEach {
                    api(it)
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
                implementation("com.onesignal:OneSignal:[4.0.0, 4.99.99]")
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting

        val iosMain by creating {
            dependencies {
                Dependencies.Shared.iosMain.forEach {
                    implementation(it)
                }
            }
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }

        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting

        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }

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
    }

    val onesignalAppId = extra["onesignal.app.id"] as String

    defaultConfigs("prod") {
        val apiKey = extra["api.key"] as String
        val apiUrl = extra["api.url"] as String
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "apiKey", apiKey)
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "apiUrl", apiUrl)
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "environment", "prod")
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "onesignalAppId", onesignalAppId)
    }

    defaultConfigs("dev") {
        val apiKey = extra["dev.api.key"] as String
        val apiUrl = extra["dev.api.url"] as String
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "apiKey", apiKey)
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "apiUrl", apiUrl)
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "environment", "dev")
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "onesignalAppId", onesignalAppId)
    }
}

kswift {
    install(dev.icerock.moko.kswift.plugin.feature.SealedToSwiftEnumFeature) {
        filter = includeFilter("ClassContext/cherrypick:shared/com/java/cherrypick/presentationInfra/UiEvent")
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "com.java.cherrypick"
    multiplatformResourcesClassName = "SharedRes"
}
