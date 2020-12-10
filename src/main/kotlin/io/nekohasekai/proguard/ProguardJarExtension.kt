@file:Suppress("MemberVisibilityCanBePrivate")

package io.nekohasekai.proguard

import org.gradle.api.Project
import java.io.File

open class ProguardJarExtension(project: Project) {

    var proguardFile = File(project.projectDir, "proguard.pro")

    var excludes = hashSetOf(
        "META-INF/MANIFEST.MF",
        "META-INF/*.txt",
        "META-INF/NOTICE",
        "META-INF/LICENSE",
        "META-INF/INDEX.LIST",
        "META-INF/com/android/tools",
        "META-INF/maven"
    )

    fun exclude(vararg path: String) {
        excludes.addAll(excludes)
    }

    var outputDirectory = File(project.buildDir, "proguardJar")
    var outputFileName = "${project.name}-${project.version}.jar"

    var addLibraryDefinedConfigure = true

}