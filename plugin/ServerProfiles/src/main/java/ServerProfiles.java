import android.content.Context;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.discord.widgets.guilds.profile.WidgetChangeGuildIdentityViewModel;
import com.discord.widgets.guilds.profile.WidgetChangeGuildIdentityViewModel.StoreState;

@AliucordPlugin
public class ServerProfiles extends Plugin {
    @Override
    public void start(Context content) {
        patcher.after(WidgetChangeGuildIdentityViewModel.class, "handleStoreState", (callFrame) -> {
            StoreState state = (StoreState) callFrame.args[0];
            logger.info(state.getMember().getBio());
        }, StoreState.class);
    }

    @Override
    public void stop(Context content) {
        patcher.unpatchAll();
    }
}
