val simplixStorageVersion: String by project.extra
dependencies {
    api("com.github.simplix-softworks:simplixstorage:${simplixStorageVersion}")
    api(project(":kebab-libs"))
}