dependencies {
    // The common module depends on the API to use its interfaces.
    implementation(project(":api"))
}

// Configure JAR output to root directory
tasks.jar {
    destinationDirectory.set(rootProject.projectDir.resolve("build/libs"))
}
