
val sentryVersion: String by project.extra

dependencies {
    api(project(":kebab-api"))

    api("io.sentry:sentry:${sentryVersion}")
}