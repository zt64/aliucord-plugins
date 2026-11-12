@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()

        maven("https://maven.aliucord.com/releases")
        maven("https://maven.aliucord.com/snapshots")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.aliucord.com/releases")
        maven("https://maven.aliucord.com/snapshots")
    }

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

rootProject.name = "aliucord-plugins"

rootDir
    .resolve("plugin")
    .listFiles { file ->
        file.isDirectory && file.resolve("build.gradle.kts").exists()
    }!!
    .forEach { include(":plugin:${it.name}") }