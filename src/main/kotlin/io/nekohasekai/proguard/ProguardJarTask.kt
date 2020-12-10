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
        outputs.upToDateWhen { false }
    }

    @TaskAction
    override fun proguard() {
        val mainSourceSet =
            (project.extensions.getByName("sourceSets") as SourceSetContainer).getByName("main")
        injars((project.tasks.getByName(mainSourceSet.jarTaskName) as Jar).archiveFile)

        val extension = project.extensions.getByType<ProguardJarExtension>()

        if (extension.proguardFile.isFile) {
            println("load proguard configure file ${extension.proguardFile}")
            configuration(extension.proguardFile)
        }

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
                    project.zipTree(library).visit { node ->
                        if (node.isDirectory) return@visit

                        if (node.relativePath.startsWith("META-INF/proguard/") && node.name.endsWith(".pro")) {
                            println("load proguard configure file ${node.name} from ${library.name}")
                            configuration(node.file)
                        } else if (node.relativePath.startsWith("META-INF/services/")) {
                            println("process service ${node.name} from ${library.name}")
                            node.open().bufferedReader().forEachLine {
                                keep("class $it { *; }")
                            }
                        }

                    }
                }
            }
        }

        if (JavaVersion.current().isJava11Compatible) {
            libraryjars(Jvm.current().javaHome.path + "/jmods")
        } else {
            libraryjars(Jvm.current().javaHome.path + "/lib/rt.jar")
        }

        var outputFile = extension.outputFile

        if (outputFile.name == "projectName-projectVersion.jar") {
            outputFile = File(outputFile.parentFile, "${project.name}-${project.version}.jar")
        }

        outjars(outputFile)

        super.proguard()
    }

}