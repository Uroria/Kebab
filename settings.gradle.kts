rootProject.name = "kebab"

pluginManagement {
    includeBuild("cargo-wrapper")
}

val modules = listOf(
        "kebab-api",
        "kebab-application",
        "kebab-implementation",
        "kebab-server"
)

include(modules)
modules.forEach { module -> findProject(":${module}")?.name = module }