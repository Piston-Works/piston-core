package org.pistonworks.core.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.yaml.snakeyaml.Yaml
import java.io.File

class PistonCorePlugin : Plugin<Project> {

    companion object {
        private const val PISTON_CORE_VERSION = "0.6.0"
    }

    override fun apply(project: Project) {
        // Apply Java plugin
        project.plugins.apply("java")

        // Add repositories
        project.repositories.apply {
            mavenCentral()
            maven {
                name = "spigotmc-repo"
                setUrl("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
            }
            maven {
                name = "nexus"
                setUrl("https://nexus.alecj.tk/repository/maven-releases/")
            }
        }

        // Add Piston Core dependencies
        project.dependencies.apply {
            add("implementation", "org.pistonworks:api:$PISTON_CORE_VERSION")
            add("implementation", "org.pistonworks:common:$PISTON_CORE_VERSION")
        }

        // Create extension for configuration
        project.extensions.create("pistonCore", PistonCoreExtension::class.java)

        // Register platform-specific build tasks
        registerSpigotTask(project)
        registerFabricTask(project)

        // Register buildAll task
        val buildAllTask = project.tasks.create("buildAll")
        buildAllTask.group = "piston core"
        buildAllTask.description = "Builds all platform versions of the plugin"
        buildAllTask.dependsOn("buildSpigot")
        // buildAllTask.dependsOn("buildFabric") // Enable when ready

        // Make build depend on buildAll
        val buildTask = project.tasks.getByName("build")
        buildTask.dependsOn("buildAll")
    }

    private fun registerSpigotTask(project: Project) {
        val spigotTask = project.tasks.create("buildSpigot", Jar::class.java)
        spigotTask.group = "piston core"
        spigotTask.description = "Builds Spigot version of the plugin"
        spigotTask.archiveClassifier.set("spigot")
        spigotTask.duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        val sourceSets = project.extensions.getByName("sourceSets") as SourceSetContainer
        spigotTask.from(sourceSets.getByName("main").output)

        // Include runtime dependencies
        spigotTask.from(project.configurations.getByName("runtimeClasspath").map { file ->
            if (file.isDirectory) file else project.zipTree(file)
        })

        // Include Spigot implementation
        val spigotConfig = project.configurations.create("spigotImplementation")
        project.dependencies.add("spigotImplementation", "org.pistonworks:piston-core-spigot:$PISTON_CORE_VERSION")
        spigotTask.from(spigotConfig.map { file ->
            if (file.isDirectory) file else project.zipTree(file)
        })

        // Generate plugin.yml
        spigotTask.doFirst {
            val metadata = loadPluginMetadata(project)
            val outputDir = File(project.layout.buildDirectory.asFile.get(), "generated/resources/spigot")
            outputDir.mkdirs()
            val pluginYml = File(outputDir, "plugin.yml")

            pluginYml.writeText(
                """
name: ${metadata["name"]}
version: ${metadata["version"]}
description: ${metadata["description"]}
main: org.pistonworks.core.spigot.PistonCoreSpigotPlugin
authors: ${metadata["authors"]}
api-version: 1.19
depend: []
""".trimIndent()
            )

            spigotTask.from(outputDir)
        }
    }

    private fun registerFabricTask(project: Project) {
        val fabricTask = project.tasks.create("buildFabric", Jar::class.java)
        fabricTask.group = "piston core"
        fabricTask.description = "Builds Fabric version of the plugin"
        fabricTask.archiveClassifier.set("fabric")
        fabricTask.duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        val sourceSets = project.extensions.getByName("sourceSets") as SourceSetContainer
        fabricTask.from(sourceSets.getByName("main").output)

        // Include runtime dependencies
        fabricTask.from(project.configurations.getByName("runtimeClasspath").map { file ->
            if (file.isDirectory) file else project.zipTree(file)
        })

        // Generate fabric.mod.json
        fabricTask.doFirst {
            val metadata = loadPluginMetadata(project)
            val outputDir = File(project.layout.buildDirectory.asFile.get(), "generated/resources/fabric")
            outputDir.mkdirs()
            val fabricJson = File(outputDir, "fabric.mod.json")

            fabricJson.writeText(
                """
{
  "schemaVersion": 1,
  "id": "${(metadata["name"] as String).lowercase()}",
  "version": "${metadata["version"]}",
  "name": "${metadata["name"]}",
  "description": "${metadata["description"]}",
  "environment": "server",
  "entrypoints": {
    "server": ["org.pistonworks.core.fabric.PistonCoreFabricMod"]
  },
  "depends": {
    "fabricloader": ">=0.14.0",
    "minecraft": ">=1.19"
  }
}
""".trimIndent()
            )

            fabricTask.from(outputDir)
        }
    }

    private fun loadPluginMetadata(project: Project): Map<String, Any> {
        val pistonYmlFile = project.file("src/main/resources/piston-core.yml")
        if (!pistonYmlFile.exists()) {
            throw IllegalStateException("piston-core.yml not found in src/main/resources/")
        }

        val yaml = Yaml()
        return yaml.load(pistonYmlFile.inputStream()) as Map<String, Any>
    }
}

open class PistonCoreExtension {
    // Extension properties can be added here as needed
}
