import android.animation.ValueAnimator;
import android.content.Context;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.discord.widgets.notice.NoticePopup;
import customnoticeduration.PluginSettings;

@AliucordPlugin
public class CustomNoticeDuration extends Plugin {
    
    public CustomNoticeDuration() {
        settingsTab = new SettingsTab(PluginSettings.class, SettingsTab.Type.BOTTOM_SHEET, settings);
    }

    @Override
    public void start(Context context) {
        patcher.after(NoticePopup.class, "getAutoDismissAnimator", (callFrame) -> {
            ValueAnimator result = (ValueAnimator) callFrame.getResult();

            if (settings.getBool("autoDismissNotice", true)) {
                result.setDuration(settings.getLong("noticeDuration", 5000));
            } else {
                result.cancel();
            }
        }, Integer.class, kotlin.jvm.functions.Function0.class);
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}
