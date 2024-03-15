@file:Suppress("UnstableApiUsage")

import com.aliucord.gradle.AliucordExtension
import com.android.build.gradle.LibraryExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension

subprojects {
    val libs = rootProject.libs

    apply {
        plugin(libs.plugins.android.library.get().pluginId)
        plugin(libs.plugins.aliucord.get().pluginId)
        plugin(libs.plugins.kotlin.android.get().pluginId)
        plugin(libs.plugins.ktlint.get().pluginId)
    }

    configure<LibraryExtension> {
        namespace = "com.aliucord.plugins"

        compileSdk = 34

        defaultConfig {
            minSdk = 24
        }

        buildFeatures {
            renderScript = false
            shaders = false
            buildConfig = true
            resValues = false
            aidl = false
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }

    configure<AliucordExtension> {
        author("zt", 289556910426816513L)

        updateUrl.set("https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/updater.json")
        buildUrl.set("https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/%s.zip")
    }

    configure<KtlintExtension> {
        version.set(libs.versions.ktlint)

        coloredOutput.set(true)
        outputColorName.set("RED")
        ignoreFailures.set(true)
    }

    dependencies {
        val discord by configurations
        val compileOnly by configurations
        val implementation by configurations

        discord(libs.discord)
        compileOnly(libs.aliucord)
        // compileOnly("com.github.Aliucord:Aliucord:unspecified")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
        }
    }
}

project.gradle.taskGraph.whenReady {
    val readMe = rootProject.file("README.md")
    val content = buildString {
        appendLine("## Plugins for [Aliucord](https://github.com/Aliucord)")
        appendLine()
        appendLine(
            "Click on a plugin name to download, and then move the downloaded file to the `Aliucord/plugins` folder"
        )
        appendLine()

        subprojects
            .filterNot {
                it.extensions.getByType<AliucordExtension>().excludeFromUpdaterJson.get()
            }.joinToString("\n") { subproject ->
                buildString {
                    with(subproject) {
                        appendLine(
                            "- [$name](https://github.com/zt64/aliucord-plugins/raw/builds/$name.zip )"
                        )
                        appendLine(description)
                    }
                }
            }.let(::append)
    }

    readMe.writeText(content)
}