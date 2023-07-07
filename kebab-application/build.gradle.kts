plugins {
    application
}

application {
    mainClass.set("org.kebab.application.Bootstrap")
}

dependencies {
    implementation(project(":kebab-implementation"))
}