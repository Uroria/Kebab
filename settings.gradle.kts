rootProject.name = "kebab"

val modules = listOf(
        "kebab-api",
        "kebab-common",
        "kebab-application",
        "kebab-server",
        "kebab-protocol"
)

include(modules)
modules.forEach { module -> findProject(":${module}")?.name = module }