import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.patcher.component1
import com.aliucord.patcher.component2
import com.discord.widgets.guilds.profile.WidgetChangeGuildIdentityViewModel
import com.discord.widgets.guilds.profile.WidgetChangeGuildIdentityViewModel.StoreState

@AliucordPlugin
class ServerProfiles : Plugin() {
    override fun start(content: Context) {
        patcher.after<WidgetChangeGuildIdentityViewModel>(
            "handleStoreState",
            StoreState::class.java
        ) { (_, state: StoreState) ->
            logger.info(state.member.bio)
        }
    }

    override fun stop(content: Context) = patcher.unpatchAll()
}