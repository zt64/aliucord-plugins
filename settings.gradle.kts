pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        maven("https://maven.aliucord.com/snapshots")
        maven("https://jitpack.io")
    }
}

rootProject.name = "plugins"

rootDir.listFiles { file ->
    file.isDirectory && file.resolve("build.gradle.kts").exists()
}!!.forEach {
    include(it.name)
}