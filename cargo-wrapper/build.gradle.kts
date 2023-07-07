plugins {
    `java-gradle-plugin`
}

gradlePlugin {
    plugins {
        create("de.verklickt.cargowrapper") {
            id = "de.verklickt.cargowrapper"
            group = "de.verklickt"
            implementationClass = "de.verklickt.cargowrapper.CargoWrapperPlugin"
            version = project.version
        }
    }
}