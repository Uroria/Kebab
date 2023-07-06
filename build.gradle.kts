import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

allprojects {
    group = "com.uroria.kebab"
    version = project.properties["version"].toString()
}

subprojects {
    apply<JavaLibraryPlugin>()
    apply<ShadowPlugin>()
    apply<MavenPublishPlugin>()

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
    }

    val jetbrainsAnnotationsVersion: String by project.extra
    val lombokVersion: String by project.extra
    val slf4jVersion: String by project.extra
    dependencies {
        compileOnly("org.jetbrains:annotations:${jetbrainsAnnotationsVersion}")
        compileOnly("org.projectlombok:lombok:${lombokVersion}")
        annotationProcessor("org.jetbrains:annotations:${jetbrainsAnnotationsVersion}")
        annotationProcessor("org.projectlombok:lombok:${lombokVersion}")

        api("org.slf4j:slf4j-api:$slf4jVersion")
    }

    java.toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
    }

    tasks.register("buildApplication") {
        doFirst {
            println("Building application")
        }
    }
}
