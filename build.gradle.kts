import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.gradle.plugin-publish") version "0.12.0"
    `java-gradle-plugin`
    kotlin("jvm") version "1.4.21"
}

group = "io.nekohasekai"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    compileOnly(gradleApi())
    compileOnly(gradleKotlinDsl())
    implementation("com.guardsquare:proguard-gradle:7.0.0") {
        exclude("com.android.tools.build")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

gradlePlugin {
    plugins {
        create("proguard-jar-plugin") {
            id = "io.nekohasekai.proguard-jar"
            implementationClass = "io.nekohasekai.proguard.ProguardJarPlugin"
        }
    }
}


pluginBundle {
    website = "https://github.com/nekohasekai/gradle-proguard-jar-plugin"
    vcsUrl = "https://github.com/nekohasekai/gradle-proguard-jar-plugin.git"
    description = "Proguard plugin for java applications"

    (plugins) {
        "proguard-jar-plugin" {
            id = "io.nekohasekai.proguard-jar"
            displayName = "Proguard Jar Plugin"
            tags = listOf("proguard", "jar")
            version = "${project.version}"
        }
    }

    mavenCoordinates {
        groupId = "${project.group}"
        artifactId = "proguard"
        version = "${project.version}"
    }

}