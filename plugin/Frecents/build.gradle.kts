import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.wire)
    alias(libs.plugins.shadow)
}

wire {
    kotlin { }
}

val shadowDir = layout.buildDirectory.file("intermediates/shadowed")

val relocateJar by tasks.register<ShadowJar>("relocateJar") {
    from(
        tasks.named("compileDebugJavaWithJavac"),
        tasks.named("compileDebugKotlin")
    )

    from(
        project.configurations.named("implementationArtifacts").map { config ->
            config.incoming.artifactView {
                attributes.attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, ArtifactTypeDefinition.JAR_TYPE)
            }.files
        }
    )

    relocate("okio", "com.o.okio.okio")
    archiveClassifier = "shadowed"
    destinationDirectory = layout.buildDirectory.dir("intermediates")
    outputs.upToDateWhen { false }
}

val copyShadowed by tasks.register<Sync>("copyShadowed") {
    dependsOn(relocateJar)
    from(zipTree(relocateJar.archiveFile))
    into(shadowDir)
}

afterEvaluate {
    tasks.compileDex {
        dependsOn(copyShadowed)
        input.setFrom(shadowDir)
    }
}

version = "1.1.7"
description = "Adds support for favorite GIFs, emojis, stickers, and recent items"

aliucord.changelog.set(
    """
    # 1.1.7
    - Switched frecents code generation to be more maintainable. Shouldn't have any noticeable affects.
        
    # 1.1.6
    - Fix webm GIFs not showing up in favorite GIFs (credit to aubymori)
    
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