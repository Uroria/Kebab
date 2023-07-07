plugins {
    id("org.kebab.cargo") version("0.0.1")
}

cargo {
    crate = projectDir.path
    val libraryName: String = System.mapLibraryName("kebab_cargo_wrapper")
    outputs[""] = libraryName
    targets[libraryName] = projectDir.path + "/../kebab-common/src/main/resources"
}