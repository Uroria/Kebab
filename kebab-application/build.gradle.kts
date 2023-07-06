plugins {
    application
}


application {
    mainClass.set("com.uroria.kebab.application.Bootstrap")
}

val apacheCommonsCLIVersion: String by project.extra
val log4jVersion: String by project.extra

dependencies {
    implementation(project(":kebab-server"))

    implementation("commons-cli:commons-cli:${apacheCommonsCLIVersion}")

    implementation("org.apache.logging.log4j:log4j-api:${log4jVersion}")
    implementation("org.apache.logging.log4j:log4j-core:${log4jVersion}")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:${log4jVersion}")
    implementation("org.apache.logging.log4j:log4j-iostreams:${log4jVersion}")
    implementation("org.apache.logging.log4j:log4j-jul:${log4jVersion}")
}