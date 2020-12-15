import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.72"
    `java-gradle-plugin`
}

group = "io.nekohasekai"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    compileOnly(gradleApi())
    compileOnly(gradleKotlinDsl())
    implementation("com.guardsquare:proguard-gradle:7.0.1") {
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
            displayName = "Proguard Jar Plugin"
            implementationClass = "io.nekohasekai.proguard.ProguardJarPlugin"
        }
    }
}