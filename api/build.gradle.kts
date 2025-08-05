// This project is a contract and has no special dependencies.
// It relies only on the common configuration from the root build file.

// Configure JAR output to root directory
tasks.jar {
    destinationDirectory.set(rootProject.projectDir.resolve("build/libs"))
}
