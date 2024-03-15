package rolecoloreverywhere

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.widgets.BottomSheet
import com.discord.views.CheckedSetting
import com.lytefast.flexinput.R

@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
class PluginSettings(private val settings: SettingsAPI) : BottomSheet() {
    @Suppress("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        val ctx = requireContext()

        fun addCheckedSetting(
            title: String,
            subtext: String,
            setting: String,
            default: Boolean = true
        ) {
            addView(
                Utils
                    .createCheckedSetting(
                        ctx,
                        CheckedSetting.ViewType.SWITCH,
                        title,
                        subtext
                    ).apply {
                        isChecked = settings.getBool(setting, default)
                        setOnCheckedListener {
                            settings.setBool(setting, it)
                            PluginManager.stopPlugin("RoleColorEverywhere")
                            PluginManager.startPlugin("RoleColorEverywhere")
                        }
                    }
            )
        }

        addView(
            TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header).apply {
                text = "Role Color Everywhere"
            }
        )
        addView(
            TextView(ctx, null, 0, R.i.UiKit_Settings_Item_SubText).apply {
                text =
                    "Note: Some of these options will require a restart to colorize already rendered elements"
            }
        )
        addCheckedSetting("User mentions", "Whether mentions are colored", "userMentions")
        addCheckedSetting("Typing text", "Whether typing users are colored", "typingText")
        addCheckedSetting(
            "Voice users",
            "Whether usernames in voice channels are colored",
            "voiceChannel"
        )
        addCheckedSetting(
            "Reaction lists",
            "Whether usernames on reaction lists are colored",
            "reactionList"
        )
        addCheckedSetting(
            "Profile name",
            "Whether usernames on profiles are colored",
            "profileName"
        )
        addCheckedSetting("Profile name tag", "Whether tags on profiles are colored", "profileTag")
        addCheckedSetting(
            "Statuses",
            "Whether statuses on sidebars and profiles are colored",
            "status"
        )
        addCheckedSetting("Messages", "Whether messages are colored", "messages", false)
    }
}