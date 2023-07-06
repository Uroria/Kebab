
val sentryVersion: String by project.extra

dependencies {
    implementation(project(":kebab-api"))
    implementation(project(":kebab-common"))

    api("io.sentry:sentry:${sentryVersion}")
}