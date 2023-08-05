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
    plugins.register("app-config-plugin") {
        id = "app-config-plugin"
        implementationClass = "com.java.cherrypick.BuildPlugin"
    }
}
