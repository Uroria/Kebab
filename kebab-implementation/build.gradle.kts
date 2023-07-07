val simplixStorage: String by project.extra
dependencies {
    api(project(":kebab-api"))
    api(project(":kebab-server"))

    api("com.github.simplix-softworks:simplixstorage:${simplixStorage}")
}

tasks.shadowJar {
    relocate("de.leonhard.storage", "org.kebab.storage")
}