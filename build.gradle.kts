import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar


plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

group = "com.spanner"
version = "0.4.0"

apply(plugin = "com.github.johnrengelman.shadow")

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
    maven(url = "https://repo.hypera.dev/snapshots/")
}

dependencies {
    compileOnly("com.github.Minestom:Minestom:c694c4074e")
    implementation("net.kyori:adventure-text-minimessage:4.11.0")
    implementation("com.jayway.jsonpath:json-path:2.7.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    manifest {
        attributes("Main-Class" to "com.spanner.basics.Basics")
    }
}