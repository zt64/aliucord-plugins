import android.content.Context
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.patcher.component1
import com.aliucord.patcher.component2
import com.discord.widgets.servers.WidgetServerSettingsBans

@AliucordPlugin
class BanCount : Plugin() {
    override fun start(context: Context) {
        patcher.after<WidgetServerSettingsBans>(
            "configureUI",
            WidgetServerSettingsBans.Model::class.java
        ) { (_, model: WidgetServerSettingsBans.Model) ->
            actionBarTitleLayout.setSubtitle(
                "${model.guildName} (${
                    Utils.pluralise(
                        model.totalBannedUsers,
                        "ban"
                    )
                })"
            )
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}