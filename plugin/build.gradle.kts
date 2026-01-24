@file:Suppress("UnstableApiUsage")

import com.aliucord.gradle.AliucordExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension

subprojects {
    val libs = rootProject.libs

    apply {
        plugin(libs.plugins.android.library.get().pluginId)
        plugin(libs.plugins.aliucord.get().pluginId)
        plugin(libs.plugins.kotlin.android.get().pluginId)
        plugin(libs.plugins.ktlint.get().pluginId)
    }

    kotlinExtension.apply {
        jvmToolchain(21)
    }

    configure<LibraryExtension> {
        namespace = "com.aliucord.plugins"

        compileSdk = 36

        defaultConfig {
            minSdk = 21
        }

        buildFeatures {
            renderScript = false
            shaders = false
            buildConfig = true
            resValues = false
            aidl = false
        }

        androidResources {
            enable = false
        }

        lint {
            disable += "SetTextI18n"
        }
    }

    configure<LibraryAndroidComponentsExtension> {
        beforeVariants(selector().withBuildType("release")) { variantBuilder ->
            variantBuilder.enable = false
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
        val compileOnly by configurations

        compileOnly(libs.discord)
        compileOnly(libs.aliucord)
        compileOnly(libs.aliuhook)
        compileOnly(libs.kotlin.stdlib)
    }
}

tasks.register("generateReadMe") {
    group = "aliucord"
    description = "Generates the README.md file with download links for all plugins"

    outputs.file(rootProject.file("README.md"))

    doLast {
        val readMe = rootProject.file("README.md")
        val plugins = subprojects
            .filter { it.extensions.getByType<AliucordExtension>().deploy.get() }
            .sortedBy { it.name }

        val content = buildString {
            // Header with badges
            appendLine("# Aliucord Plugins")
            appendLine()
            appendLine(
                "[![Plugins](https://img.shields.io/badge/Plugins-${plugins.size}-blue?style=for-the-badge)](https://github.com/zt64/aliucord-plugins)"
            )
            appendLine()
            appendLine("A collection of plugins for [Aliucord](https://github.com/Aliucord), a Discord mobile client mod.")
            appendLine()

            // Installation section with emoji
            appendLine("## 📥 Installation")
            appendLine()
            appendLine("Click on a plugin name to download, then move the downloaded file to the `Aliucord/plugins` folder on your device.")
            appendLine()

            // Plugin list in table format
            appendLine("## 🧩 Available Plugins")
            appendLine()
            appendLine("| Plugin | Description |")
            appendLine("|:-------|:------------|")

            plugins.forEach { subproject ->
                val description = subproject.description ?: "No description provided."
                appendLine(
                    "| [**${subproject.name}**](https://github.com/zt64/aliucord-plugins/raw/builds/${subproject.name}.zip) | $description |"
                )
            }
        }

        readMe.writeText(content)
    }
}