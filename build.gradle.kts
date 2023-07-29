@file:Suppress("UnstableApiUsage")

import com.aliucord.gradle.AliucordExtension
import com.android.build.gradle.LibraryExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.aliucord) apply false
}

subprojects {
    apply {
        plugin("com.android.library")
        plugin("com.aliucord.gradle")
        plugin("kotlin-android")
    }

    repositories {
        google()
        mavenCentral()
//        mavenLocal()
        maven("https://maven.aliucord.com/snapshots")
    }

    configure<LibraryExtension> {
        namespace = "com.aliucord.plugins"

        compileSdk = 34

        defaultConfig {
            minSdk = 24
            targetSdk = 34
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

        discord(rootProject.libs.discord)
        compileOnly(rootProject.libs.aliucord)
        // compileOnly("com.github.Aliucord:Aliucord:unspecified")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-Xno-call-assertions",
                "-Xno-param-assertions",
                "-Xno-receiver-assertions",
                "-Xopt-in=kotlin.RequiresOptIn"
            )
        }
    }
}

task("generateReadMe") {
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