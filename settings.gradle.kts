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

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

rootProject.name = "aliucord-plugins"

rootDir
    .resolve("plugin")
    .listFiles { file ->
        file.isDirectory && file.resolve("build.gradle.kts").exists()
    }!!
    .forEach { include(":plugin:${it.name}") }