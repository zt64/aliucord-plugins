version = "1.3.2"
description = "Makes it so that emojis, mentions and others are added at the cursor position instead of the end of the message"

aliucord {
    changelog.set(
        """
            
            # 1.2.2
            * Fixed issue where text was inserting twice
            
            # 1.2.1
            * Disabled CursorInput as it has unexpected functionality on V96, and it's most likely an issue with the Pine hooking framework
            
            # 1.2.0
            * Support V96

        """.trimIndent()
    )
}