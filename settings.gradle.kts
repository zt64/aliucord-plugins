include(":AlwaysAnimate")
include(":AvatarMention")
include(":CharCounter")
include(":QuickStar")
include(":DataSaver")
include(":RotatedChat")
include(":WiderScrollbar")
include(":Token")
include(":Weather")
include(":SystemInfo")
rootProject.name = "plugins"

include(":Aliucord")
project(":Aliucord").projectDir = File("../Aliucord/Aliucord")

include(":DiscordStubs")
project(":DiscordStubs").projectDir = File("../Aliucord/DiscordStubs")