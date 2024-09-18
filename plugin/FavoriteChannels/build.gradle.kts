version = "1.1.1"
description = "Add your favorite channels to the top of the channel list for easy access."

aliucord.changelog.set(
    """
        # 1.1.1
        - Fixed improper handling of channels causing the button to not get displayed
        
        # 1.1.0
        - Switched to using Discord's API for syncing favorite channels
        
        *Note: There is currently a bug where channels will not show if the category they're in is collapsed*
        
        # 1.0.1
        - Fixed threads not moving along with their parent channels
        
        # 1.0.0
        - Initial release
    """.trimIndent()
)