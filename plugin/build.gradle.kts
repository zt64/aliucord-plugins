@file:Suppress("UnstableApiUsage")

import com.aliucord.gradle.AliucordExtension
import com.android.build.gradle.LibraryExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

subprojects {
    val libs = rootProject.libs

    apply {
        plugin(libs.plugins.android.library.get().pluginId)
        plugin(libs.plugins.aliucord.get().pluginId)
        plugin(libs.plugins.kotlin.android.get().pluginId)
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

    dependencies {
        val discord by configurations
        val compileOnly by configurations

        discord(libs.discord)
        compileOnly(libs.aliucord)
        // compileOnly("com.github.Aliucord:Aliucord:unspecified")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs += listOf(
                "-Xopt-in=kotlin.RequiresOptIn",
            )
        }
    }
}

task("generateReadMe") {
    outputs.file("README.md")
    doLast {
        val readMe = rootProject.file("README.md")

        val header = """
            ## Plugins for [Aliucord](https://github.com/Aliucord)
            
            Click on a plugin name to download, and then move the downloaded file to the `Aliucord/plugins` folder
        """.trimIndent()

        val plugins = subprojects.joinToString("\n") { subproject ->
            with(subproject) {
                val aliucord: AliucordExtension by extensions

                if (aliucord.excludeFromUpdaterJson.get()) return@with ""

                buildString {
                    appendLine("- [$name](https://github.com/zt64/aliucord-plugins/raw/builds/$name.zip )")
                    appendLine(description)
                }
            }
        }

        readMe.writeText("$header\n\n$plugins")
    }
}