package io.nekohasekai.proguard

import org.gradle.api.JavaVersion
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.jvm.Jvm
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.getByType
import proguard.gradle.ProGuardTask
import java.io.File

open class ProguardJarTask : ProGuardTask() {

    init {
        dependsOn.add("jar")
    }

    @TaskAction
    override fun proguard() {
        val mainSourceSet =
            (project.extensions.getByName("sourceSets") as SourceSetContainer).getByName("main")
        injars((project.tasks.getByName(mainSourceSet.jarTaskName) as Jar).archiveFile)

        val extension = project.extensions.getByType<ProguardJarExtension>()

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
                        if (it.isDirectory || !it.relativePath.startsWith("META-INF/proguard/") || !it.name.endsWith(".pro")) return@visit
                        println("load proguard configure file ${it.name} from ${library.name}")
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

        var outFileName = extension.outputFileName

        if (outFileName.isBlank()) {
            outFileName = project.name + "-" + project.version + ".jar"
        }

        outjars(File(extension.outputDirectory, outFileName))

        super.proguard()
    }

}