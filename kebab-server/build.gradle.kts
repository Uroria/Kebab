plugins {
    id("de.verklickt.cargowrapper")
}

cargo {
    outputs[System.mapLibraryName("kebab_server")] = projectDir.path + "/../kebab-implementation/src/main/resources"
}