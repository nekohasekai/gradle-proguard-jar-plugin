@file:Suppress("MemberVisibilityCanBePrivate", "HasPlatformType", "UnstableApiUsage")

package io.nekohasekai.proguard

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.*

abstract class ProguardJarExtension {

    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:Optional
    @get:InputFile
    abstract val proguardFile: RegularFileProperty

    @get:Optional
    @get:Input
    abstract val jarTaskName: Property<String>

    @get:Optional
    @get:Input
    abstract val excludes: SetProperty<String>

    fun exclude(vararg path: String) {
        excludes.get().addAll(path)
    }

    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:Optional
    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:Optional
    @get:Input
    abstract val processConfigures: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val keepServices: Property<Boolean>

}