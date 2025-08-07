plugins {
    java
    `java-library`
    `maven-publish`
}

// Configure all projects (including this one)
allprojects {
    group = "org.pistonworks"
    version = "0.4.1"

    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

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
        withSourcesJar()
        withJavadocJar()
    }

    // Common test configuration
    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.0")
    }

    tasks.test {
        useJUnitPlatform()
    }

    // Configure publishing for all projects except gradle-plugin
    if (project.name != "gradle-plugin") {
        publishing {
            publications {
                create<MavenPublication>("maven") {
                    from(components["java"])

                    pom {
                        name.set(project.name)
                        description.set("Piston Core is a cross-platform API for building Minecraft server-side mods/plugins")
                        url.set("https://github.com/Piston-Works/piston-core")

                        licenses {
                            license {
                                name.set("GNU General Public License v3.0")
                                url.set("https://opensource.org/licenses/GPL-3.0")
                            }
                        }

                        developers {
                            developer {
                                id.set("alecj")
                                name.set("Alec Jensen")
                                email.set("alec@alecj.com")
                            }
                        }

                        scm {
                            connection.set("scm:git:git://github.com/Piston-Works/piston-core.git")
                            developerConnection.set("scm:git:ssh://github.com/Piston-Works/piston-core.git")
                            url.set("https://github.com/Piston-Works/piston-core")
                        }
                    }
                }
            }

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
    }
}