import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.proto

plugins {
    id("com.google.protobuf") version "0.9.5"
}

version = "1.1.2"
description = "Adds support for favorite GIFs, emojis, stickers, and recent items"

aliucord.changelog.set(
    """
    # 1.1.2
    - Temporarily disabled favoriting gifs sent in chat to workaround a bug preventing clicking images
    
    # 1.1.1
    - Rework emoji sorting logic as a potential fix for recent emojis not appearing

    # 1.1.0
    - Add support for favorite and recent emojis
    - Add support for recent stickers
    
    * Note: Using an emoji or sticker will not update the recents list. This will come in a future update.
    
    # 1.0.5
    - Fix gifs not updating in real time
    
    # 1.0.4
    - Fix preview gif sometimes not appearing
    
    # 1.0.3
    - Fix favorites icon disappearing
    
    # 1.0.2
    - Fix crash caused by having no favorites
    
    # 1.0.1
    - Fix mixup causing favorites category to not appear

    # 1.0.0
    - Initial release
    """.trimIndent()
)

android {
    sourceSets {
        named("main") {
            proto { }

            java {
                srcDirs("${protobuf.generatedFilesBaseDir}/main/javalite")
            }
        }
    }
}

protobuf {
    val version = "4.28.3"
    protoc {
        artifact = "com.google.protobuf:protoc:$version"
    }

    plugins {
        id("javalite") {
            artifact = "com.google.protobuf:protoc-gen-javalite:$version"
        }
    }

    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("java") {
                    option("lite")
                }

                create("kotlin") {
                    option("lite")
                }
            }
        }
    }
}