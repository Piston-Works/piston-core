plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
}

gradlePlugin {
    plugins {
        create("pistonCore") {
            id = "org.pistonworks.core"
            implementationClass = "org.pistonworks.core.gradle.PistonCorePlugin"
            displayName = "Piston Core Plugin"
            description = "Gradle plugin for building cross-platform Minecraft plugins with Piston Core"
        }
    }
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(gradleApi())
    implementation("org.yaml:snakeyaml:2.0")
}

// Configure publishing for gradle plugin
publishing {
    repositories {
        maven {
            name = "nexus"
            url = uri("https://nexus.alecj.tk/repository/maven-releases/")
            credentials {
                username = findProperty("nexusUsername") as String? ?: System.getenv("NEXUS_USERNAME")
                password = findProperty("nexusPassword") as String? ?: System.getenv("NEXUS_PASSWORD")
            }
        }
    }
}
