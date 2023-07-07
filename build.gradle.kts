import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

allprojects {
    group = project.properties["group"].toString()
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

    val lombok: String by project.extra
    val jetbrainsAnnotations: String by project.extra
    val googleGuava: String by project.extra
    val slf4j: String by project.extra
    val adventure: String by project.extra
    dependencies {
        val lombokDependency = "org.projectlombok:lombok:${lombok}"
        compileOnly(lombokDependency)
        annotationProcessor(lombokDependency)

        val jetbrainsAnnotationsDependency = "org.jetbrains:annotations:${jetbrainsAnnotations}"
        compileOnly(jetbrainsAnnotationsDependency)
        annotationProcessor(jetbrainsAnnotationsDependency)

        implementation("com.google.guava:guava:${googleGuava}")

        api("org.slf4j:slf4j-api:$slf4j")

        compileOnlyApi("net.kyori:adventure-api:${adventure}")
        compileOnlyApi("net.kyori:adventure-text-serializer-gson:${adventure}")
        compileOnlyApi("net.kyori:adventure-text-minimessage:${adventure}")
        compileOnlyApi("net.kyori:adventure-text-serializer-plain:${adventure}")
        compileOnlyApi("net.kyori:adventure-text-serializer-legacy:${adventure}")
        compileOnlyApi("net.kyori:adventure-nbt:${adventure}")
    }

    java.toolchain {
        languageVersion.set(JavaLanguageVersion.of(16))
    }
}