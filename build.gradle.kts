import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

allprojects {
    group = "org.kebab"
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
    val googleGuavaVersion: String by project.extra
    val adventureVersion: String by project.extra
    dependencies {
        compileOnly("com.google.guava:guava:${googleGuavaVersion}")

        compileOnly("org.jetbrains:annotations:${jetbrainsAnnotationsVersion}")
        compileOnly("org.projectlombok:lombok:${lombokVersion}")
        annotationProcessor("org.jetbrains:annotations:${jetbrainsAnnotationsVersion}")
        annotationProcessor("org.projectlombok:lombok:${lombokVersion}")

        api("org.slf4j:slf4j-api:$slf4jVersion")

        compileOnlyApi("net.kyori:adventure-api:${adventureVersion}")
        compileOnlyApi("net.kyori:adventure-text-serializer-gson:${adventureVersion}")
        compileOnlyApi("net.kyori:adventure-text-minimessage:${adventureVersion}")
        compileOnlyApi("net.kyori:adventure-text-serializer-plain:${adventureVersion}")
        compileOnlyApi("net.kyori:adventure-text-serializer-legacy:${adventureVersion}")
        compileOnlyApi("net.kyori:adventure-nbt:${adventureVersion}")
    }

    java.toolchain {
        languageVersion.set(JavaLanguageVersion.of(16))
    }

    tasks.register("buildApplication") {
        doFirst {
            println("Building application")
        }
    }
}
