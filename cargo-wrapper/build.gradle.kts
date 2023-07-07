plugins {
    `java-gradle-plugin`
}

gradlePlugin {
    plugins {
        create("org.kebab.cargo") {
            id = "org.kebab.cargo"
            group = "org.kebab"
            implementationClass = "org.kebab.cargo.CargoWrapperPlugin"
            version = project.version
        }
    }
}