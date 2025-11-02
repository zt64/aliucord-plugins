import android.content.Context;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.discord.widgets.servers.WidgetServerSettingsBans;

@SuppressWarnings("MISSING_DEPENDENCY_SUPERCLASS")
@AliucordPlugin
public class BanCount extends Plugin {
    @Override
    public void start(Context context) {
        patcher.after(WidgetServerSettingsBans.class, "configureUI", (callFrame) -> {
            WidgetServerSettingsBans widget = (WidgetServerSettingsBans) callFrame.thisObject;
            WidgetServerSettingsBans.Model model = (WidgetServerSettingsBans.Model) callFrame.args[0];
            
            widget.actionBarTitleLayout.setSubtitle(
                model.getGuildName() + " (" +
                Utils.pluralise(model.getTotalBannedUsers(), "ban") +
                ")"
            );
        }, WidgetServerSettingsBans.Model.class);
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}
