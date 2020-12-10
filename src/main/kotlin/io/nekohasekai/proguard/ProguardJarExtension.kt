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
        "META-INF/com.android.tools/**",
        "META-INF/maven/**",
        "META-INF/proguard/**"
    )

    fun exclude(vararg path: String) {
        excludes.addAll(path)
    }

    var outputFile = File(File(project.buildDir, "proguardJar"), "projectName-projectVersion.jar")
    var addLibraryDefinedConfigure = true

}