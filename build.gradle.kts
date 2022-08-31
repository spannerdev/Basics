import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar


plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

group = "com.spanner"
version = "0.0.1"

apply(plugin = "com.github.johnrengelman.shadow")

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
    maven(url = "https://repo.hypera.dev/snapshots/")
}

dependencies {
    compileOnly("com.github.Minestom:Minestom:c694c4074e")
    compileOnly("net.kyori:adventure-text-minimessage:4.11.0")
    implementation("net.kyori:adventure-text-minimessage:4.11.0")
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    manifest {
        attributes("Main-Class" to "com.spanner.basics.Basics")
    }
}