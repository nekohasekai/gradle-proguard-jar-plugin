@file:Suppress("unused")

package io.nekohasekai.proguard

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.internal.jvm.Jvm
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.task
import proguard.gradle.ProGuardTask
import java.io.File

class ProguardJarPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create<ProguardJarExtension>("proguard", project)

        project.task<ProGuardTask>("proguardJar") {

            val mainSourceSet = (extensions.getByName("sourceSets") as SourceSetContainer).getByName("main")
            injars(mainSourceSet.output.files)

            if (extension.proguardFile.isFile) configuration(extension.proguardFile)

            val libraries = mainSourceSet.runtimeClasspath.files.filter { it.isFile }
            if (libraries.isNotEmpty()) {
                if (extension.excludes.isNotEmpty()) {
                    injars(
                        mapOf("filter" to extension.excludes.joinToString(",") { "!$it" }),
                        libraries
                    )
                } else {
                    injars(libraries)
                }
                if (extension.addLibraryDefinedConfigure) {
                    for (library in libraries) {
                        project.zipTree(library).visit {
                            if (it.isDirectory || !it.name.startsWith("META-INF/proguard/") || !it.name.endsWith(".pro")) return@visit
                            configuration(it.file)
                        }
                    }
                }
            }

            if (JavaVersion.current().isJava11Compatible) {
                libraryjars(Jvm.current().javaHome.path + "/jmods")
            } else {
                libraryjars(Jvm.current().javaHome.path + "/lib/rt.jar")
            }

            outjars(File(extension.outputDirectory, extension.outputFileName))

        }
    }
}