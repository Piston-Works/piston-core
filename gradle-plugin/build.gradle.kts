plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
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
