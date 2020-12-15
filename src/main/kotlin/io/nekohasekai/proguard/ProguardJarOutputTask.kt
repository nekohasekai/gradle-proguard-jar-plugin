package io.nekohasekai.proguard

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.getByType

open class ProguardJarOutputTask : Jar() {
    init {
        dependsOn.add("proguardJar")
    }

    override fun copy() {
        (archiveFile as RegularFileProperty).set(project.extensions.getByType<ProguardJarExtension>().outputFile)
    }
}