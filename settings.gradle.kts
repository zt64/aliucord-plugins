@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        maven("https://maven.aliucord.com/snapshots")
        maven("https://jitpack.io")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.aliucord.com/snapshots")
    }
}

rootProject.name = "plugins"

rootDir.resolve("plugin").listFiles { file ->
    file.isDirectory && file.resolve("build.gradle.kts").exists()
}!!.map { ":plugin:${it.name}" }.let(::include)