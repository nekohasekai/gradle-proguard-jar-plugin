@file:Suppress("unused")

package io.nekohasekai.proguard

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class ProguardJarPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        project.extensions.create<ProguardJarExtension>("proguard", project)

        project.tasks.register("proguardJar", ProguardJarTask::class.java).configure {
            it.group = "proguard"
        }

    }
}
