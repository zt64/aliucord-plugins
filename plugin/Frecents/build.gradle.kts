import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.proto

plugins {
    id("com.google.protobuf") version "0.9.6"
}

version = "1.1.5"
description = "Adds support for favorite GIFs, emojis, stickers, and recent items"

aliucord.changelog.set(
    """
    # 1.1.5
    - Fixed a potential error caused by unsupported emojis in favorites or recents.

    # 1.1.4
    - Fixed an issue causing non-frecents events to be parsed, resulting in errors.
    
    # 1.1.3
    - Added settings page with export debug information
    - If you are having issues with Frecents, please use this button and share the exported file with 
    as much detail as possible

    # 1.1.2
    - Temporarily disabled favoriting gifs sent in chat to workaround a bug preventing clicking images
    
    # 1.1.1
    - Reworked emoji sorting logic as a potential fix for recent emojis not appearing

    # 1.1.0
    - Added support for favorite and recent emojis
    - Added support for recent stickers
    
    * Note: Using an emoji or sticker will not update the recents list. This will come in a future update.
    
    # 1.0.5
    - Fixed gifs not updating in real time
    
    # 1.0.4
    - Fixed preview gif sometimes not appearing
    
    # 1.0.3
    - Fixed favorites icon disappearing
    
    # 1.0.2
    - Fixed crash caused by having no favorites
    
    # 1.0.1
    - Fixed mixup causing favorites category to not appear

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