dependencies {
    // The Spigot implementation depends on the API and common modules.
    implementation(project(":api"))
    implementation(project(":common"))

    // Add the Spigot API as a provided dependency.
    // This is crucial, as the server provides this library at runtime.
    compileOnly("org.spigotmc:spigot-api:1.21.8-R0.1-SNAPSHOT")
}

// Configure the JAR task to create a proper plugin JAR
tasks.jar {
    archiveBaseName.set("piston-core-spigot")

    // Output to root directory
    destinationDirectory.set(rootProject.projectDir.resolve("build/libs"))

    // Include the compiled classes from dependent projects directly
    from(project(":api").sourceSets.main.get().output)
    from(project(":common").sourceSets.main.get().output)

    // Include only external dependencies (not project dependencies)
    from(configurations.runtimeClasspath.get().filter {
        !it.absolutePath.contains("piston-core")
    }.map { if (it.isDirectory) it else zipTree(it) })

    // Ensure duplicate files are handled properly
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
