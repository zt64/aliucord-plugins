version = "1.4.0"
description = "Adds the ability to quickly switch accounts"

aliucord.changelog.set(
    """
    # 1.4.0
    * Added an export and import button to the settings page, allowing users to export their accounts to a file and import them back.
    * The file is not encrypted, so keep it private, it is your responsibility to keep it safe.
    
    # 1.3.1
    * Added automatic migration for accounts from the old storage format to the new one.
    
    # 1.3.0
    * Redesigned how accounts are stored, now using a SharedPreferences backed map, which allows for better security and less error prone code.
    * Added helper text to explain how to access the switcher and add accounts.
    * Changed the current account to be greyed out, rather than removed, to prevent confusion.
    * Added an indicator to show which account is currently active.
    * Temporarily disable "Edit" functionality, as it is basic in its current state and can lead to confusion. It will be reintroduced in a future update.
    
    # 1.2.6
    * Fix "add token" button not working even if the token is valid
    * "Add Current Account" button now actually works after the first try -serinova
    
    # 1.2.5
    * Fix out of date token regex
    
    # 1.2.3
    * Fix for new discord version
    
    # 1.2.2
    * Fixed users failing to load bug
    
    # 1.2.1
    * Switched to alternate method of logging out, that doesn't reset the token
    
    # 1.2.0
    * Support V105.12
    
    # 1.1.2
    * Fixed 2FA token bug
    
    # 1.0.1
    * Minor bug fixes
    """.trimIndent()
)