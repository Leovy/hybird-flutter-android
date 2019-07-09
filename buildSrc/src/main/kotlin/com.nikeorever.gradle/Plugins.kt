package com.nikeorever.gradle

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.*

class FlutterModuleApplicationPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        if (isFlutterModuleLinkEnable(project)) {
            project.dependencies.apply {
                add("implementation", project(mapOf("path" to ":flutter")))
            }
        }
    }

    private fun isFlutterModuleLinkEnable(project: Project): Boolean {
        val gradleProperties = project.rootProject.file("gradle.properties")
        if (!gradleProperties.exists()) {
            throw GradleException("gradle.properties is not present!")
        }
        return Properties().run {
            load(gradleProperties.inputStream())
            getProperty("flutter_module_link_enable") == "true"
        }
    }
}