plugins {
    id("org.pistonworks.core") version "0.3.0"
}

repositories {
    maven {
        url = uri("https://nexus.alecj.tk/repository/maven-releases/")
    }
    mavenCentral()
}

group = "com.example"
version = "1.0.0"
