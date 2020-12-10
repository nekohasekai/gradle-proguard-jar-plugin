import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
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
    implementation("com.guardsquare:proguard-gradle:7.0.1") {
        exclude("com.android.tools.build")
        exclude("org.jetbrains.kotlin")
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