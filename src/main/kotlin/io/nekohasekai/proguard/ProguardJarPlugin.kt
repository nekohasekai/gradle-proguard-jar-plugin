@file:Suppress("unused")

package io.nekohasekai.proguard

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

class ProguardJarPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        with(project) {

            extensions.create<ProguardJarExtension>("proguardJar").apply {
                proguardFile.set(file("proguard-rules.pro"))
                excludes.set(
                    hashSetOf(
                        "META-INF/MANIFEST.MF",
                        "META-INF/*.txt",
                        "META-INF/NOTICE",
                        "META-INF/LICENSE",
                        "META-INF/INDEX.LIST",
                        "META-INF/com.android.tools/**",
                        "META-INF/maven/**",
                        "META-INF/proguard/**"
                    )
                )
                afterEvaluate {
                    outputFile.set(
                        outputFile.orNull?.asFile
                            ?: file("$buildDir/proguardJar/${project.name}-${project.version}.jar")
                    )
                }
            }

            tasks.register<ProguardJarTask>("proguardJar").configure {
                it.group = "proguard"
            }

        }
    }
}