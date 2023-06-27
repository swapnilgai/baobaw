pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "cherrypick"
includeBuild("app-config-android-plugin")
include(":androidApp")
include(":shared")
include(":interactor")
