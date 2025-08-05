plugins {
    java
    `java-library`
}

group = "org.pistonworks"
version = "0.0.1"

// Configure all projects (including this one)
allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")

    group = "org.pistonworks"
    version = "0.0.1"

    repositories {
        mavenCentral()
        // Spigot repository for Bukkit/Spigot API
        maven {
            name = "spigotmc-repo"
            url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        }
    }

    // Configure Java toolchain for all projects
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    // Common test configuration
    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.0")
    }

    tasks.test {
        useJUnitPlatform()
    }
}