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
        implementation("org.jetbrains:annotations:${jetbrainsAnnotationsVersion}")
        implementation("org.projectlombok:lombok:${lombokVersion}")

        api("org.slf4j:slf4j-api:$slf4jVersion")
    }

    java.toolchain {
        languageVersion.set(JavaLanguageVersion.of(16))
    }
}

