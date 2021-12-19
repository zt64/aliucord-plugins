import com.aliucord.gradle.AliucordExtension
import com.android.build.gradle.BaseExtension

buildscript {
    repositories {
        google()
        mavenCentral()
//        mavenLocal()
        maven("https://maven.aliucord.com/snapshots")
        maven("https://jitpack.io")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.4")
        classpath("com.github.Aliucord:gradle:main-SNAPSHOT")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
//        mavenLocal()
        maven("https://maven.aliucord.com/snapshots")
        maven("https://jitpack.io")
    }
}

fun Project.android(configuration: BaseExtension.() -> Unit) = extensions.getByName<BaseExtension>("android").configuration()

fun Project.aliucord(configuration: AliucordExtension.() -> Unit) = extensions.getByName<AliucordExtension>("aliucord").configuration()

subprojects {
    apply(plugin = "com.android.library")
    apply(plugin = "com.aliucord.gradle")
    apply(plugin = "kotlin-android")

    aliucord {
        author("zt", 289556910426816513L)
        updateUrl.set("https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/updater.json")
        buildUrl.set("https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/%s.zip")
    }

    android {
        compileSdkVersion(30)

        defaultConfig {
            minSdk = 24
            targetSdk = 30
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }

        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                jvmTarget = "11"
                freeCompilerArgs = freeCompilerArgs + "-Xno-call-assertions" + "-Xno-param-assertions" + "-Xno-receiver-assertions"
            }
        }
    }

    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }

    dependencies {
        val discord by configurations
        val compileOnly by configurations

        discord("com.discord:discord:aliucord-SNAPSHOT")
        compileOnly("com.aliucord:Aliucord:main-SNAPSHOT")
//        compileOnly("com.github.Aliucord:Aliucord:unspecified")
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}