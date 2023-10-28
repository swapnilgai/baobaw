package com.java.baobaw

import Dependencies
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies

class BuildPlugin : Plugin<Project> {
    override fun apply(target: Project) {
    }
}

fun Project.addComposeDependencies() {
    dependencies {
        implementation(Dependencies.Compose.foundation)
        implementation(Dependencies.Compose.compiler)
        implementation(Dependencies.Compose.runtime)
        implementation(Dependencies.Compose.ui)
        implementation(Dependencies.Compose.tooling)
        implementation(Dependencies.Compose.constraintLayout)
        implementation(Dependencies.Compose.toolingUi)
        implementation(Dependencies.Compose.material)
        implementation(Dependencies.Compose.activity)
        implementation(Dependencies.Compose.materialIconExtended)
    }
}


fun Project.addKtorDependencies() {
    dependencies {
        implementation(Dependencies.Compose.foundation)
        implementation(Dependencies.Compose.compiler)
        implementation(Dependencies.Compose.runtime)
        implementation(Dependencies.Compose.ui)
        implementation(Dependencies.Compose.tooling)
        implementation(Dependencies.Compose.constraintLayout)
        implementation(Dependencies.Compose.toolingUi)
        implementation(Dependencies.Compose.material)
        implementation(Dependencies.Compose.activity)
        implementation(Dependencies.Compose.materialIconExtended)
    }
}

fun DependencyHandlerScope.implementation(dependency: Any) = add("implementation", dependency)