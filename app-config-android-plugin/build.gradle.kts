import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

gradlePlugin {
    plugins.register("app-config-android-plugin") {
        id = "app-config-android-plugin"
        implementationClass = "com.java.cherrypick.BuildPlugin"
    }
}
