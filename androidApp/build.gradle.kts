import com.java.cherrypick.addComposeDependencies

plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.java.cherrypick.android"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.java.cherrypick.android"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

addComposeDependencies()

dependencies {
    implementation(project(":shared"))
}
