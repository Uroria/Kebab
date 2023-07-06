
val sentryVersion: String by project.extra

dependencies {
    implementation(project(":kebab-common"))
    implementation(project(":kebab-protocol"))

    api("io.sentry:sentry:${sentryVersion}")
}