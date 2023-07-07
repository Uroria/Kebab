# Gradle Cargo wrapper
**This Gradle plugin makes it possible to use Rust libraries in Java with Rust's compiler Cargo.**

Multi-Module-Project use only. No Gradle dependency provided.

## Setup

1. Clone this repository in your Gradle multi-module project as another module
2. Add the Gradle plugin module into your settings.gradle like this 
```kotlin
pluginManagement {
    includeBuild("MODULE_NAME")
}
```
Make sure to replace `MODULE_NAME` with the directory name of the Gradle plugin module.

3. Add the plugin to a new module made for rust and configure it
```kotlin
plugins {
    id("de.verklickt.cargowrapper")
}

cargo {
    outputs[System.mapLibraryName("cargo_library")] = "some/target/for/the/binaries"
}
```

4. Finished: You have rust now in Java. More coming soon :D