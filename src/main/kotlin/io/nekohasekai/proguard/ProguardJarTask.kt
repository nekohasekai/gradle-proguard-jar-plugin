package io.nekohasekai.proguard

import org.gradle.api.JavaVersion
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.internal.jvm.Jvm
import org.gradle.kotlin.dsl.getByType
import proguard.gradle.ProGuardTask

open class ProguardJarTask : ProGuardTask() {

    init {
        dependsOn.add("jar")
        // outputs.upToDateWhen { false }
    }

    @TaskAction
    override fun proguard() {

        val extension = project.extensions.getByType<ProguardJarExtension>()

        val mainSourceSet =
            (project.extensions.getByName("sourceSets") as SourceSetContainer).getByName("main")

        val proguardFile = extension.proguardFile.get().asFile

        if (proguardFile.isFile) {
            println("load proguard configure file ${proguardFile}")
            configuration(proguardFile)
        }

        val jarOutput = (project.tasks.getByName(
            extension.jarTaskName.orNull ?: mainSourceSet.jarTaskName
        ) as AbstractArchiveTask).archiveFile

        injars(jarOutput)

        val processConfigures = extension.processConfigures.getOrElse(true)
        val keepServices = extension.keepServices.getOrElse(true)

        if (processConfigures || keepServices) {
            project.zipTree(jarOutput).visit { node ->
                if (node.isDirectory) return@visit
                if (processConfigures && node.relativePath.startsWith("META-INF/proguard/") &&
                    node.name.endsWith(".pro")
                ) {
                    println("load proguard configure file ${node.name}")
                    configuration(node.file)
                } else if (keepServices && node.relativePath.startsWith("META-INF/services/")) {
                    println("keep service ${node.name}")
                    keep("class ${node.name} { *; }")
                    node.open().bufferedReader().forEachLine {
                        println("keep service $it")
                        keep("class $it { *; }")
                    }
                }
            }
        }

        val libraries = mainSourceSet.runtimeClasspath.files.filter { it.isFile }
        if (libraries.isNotEmpty()) {
            val excludes = extension.excludes.get()
            if (excludes.isNotEmpty()) {
                injars(
                    mapOf("filter" to excludes.joinToString(",") { "!$it" }),
                    libraries
                )
            } else {
                injars(libraries)
            }
            if (processConfigures || keepServices) {
                for (library in libraries) {
                    project.zipTree(library).visit { node ->
                        if (node.isDirectory) return@visit
                        if (processConfigures && node.relativePath.startsWith("META-INF/proguard/") &&
                            node.name.endsWith(".pro")
                        ) {
                            println("load proguard configure file ${node.name} from library ${library.name}")
                            configuration(node.file)
                        } else if (keepServices && node.relativePath.startsWith("META-INF/services/")) {
                            println("keep service ${node.name} from library ${library.name}")
                            keep("class ${node.name} { *; }")
                            node.open().bufferedReader().forEachLine {
                                println("keep service $it from library ${library.name}")
                                keep("class $it { *; }")
                            }
                        }

                    }
                }
            }
        }

        if (JavaVersion.current().isJava9Compatible) {
            libraryjars(Jvm.current().javaHome.path + "/jmods")
        } else {
            libraryjars(Jvm.current().javaHome.path + "/lib/rt.jar")
            libraryjars(Jvm.current().javaHome.path + "/lib/jce.jar")
        }

        val outputJar = extension.outputFile.get().asFile

        outjars(outputJar)

        super.proguard()

    }

}