import alwaysanimate.PluginSettings;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.PowerManager;
import android.widget.ImageView;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.aliucord.utils.DimenUtils;
import com.discord.models.presence.Presence;
import com.discord.utilities.icon.IconUtils;
import com.discord.utilities.images.MGImages;
import com.discord.utilities.presence.PresenceUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.lang.reflect.Method;

@AliucordPlugin
public class AlwaysAnimate extends Plugin {
    
    public AlwaysAnimate() {
        settingsTab = new SettingsTab(PluginSettings.class, SettingsTab.Type.BOTTOM_SHEET, settings);
    }

    @Override
    public void start(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        if (settings.getBool("batterySaver", false) && powerManager.isPowerSaveMode()) return;

        if (settings.getBool("guildIcons", true)) {
            try {
                Method method = IconUtils.class.getDeclaredMethod(
                    "getForGuild",
                    Long.class,
                    String.class,
                    String.class,
                    Boolean.class,
                    Integer.class
                );
                patcher.patch(method, (callFrame) -> {
                    callFrame.args[3] = true;
                });
            } catch (Exception e) {
                logger.error("Failed to patch getForGuild", e);
            }
        }

        if (settings.getBool("avatars", true)) {
            patcher.before(IconUtils.class, "getForUser", (callFrame) -> {
                callFrame.args[3] = true;
            }, Long.class, String.class, Integer.class, Boolean.class, Integer.class);

            patcher.after(IconUtils.class, "setIcon", (callFrame) -> {
                SimpleDraweeView view = (SimpleDraweeView) callFrame.args[0];
                view.setClipToOutline(true);

                if (settings.getBool("roundedAvatars", true)) {
                    ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
                    drawable.getPaint().setColor(Color.TRANSPARENT);
                    view.setBackground(drawable);
                } else {
                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setShape(GradientDrawable.RECTANGLE);
                    drawable.setCornerRadius(DimenUtils.dpToPx(3));
                    drawable.setColor(Color.TRANSPARENT);
                    view.setBackground(drawable);
                }
            }, ImageView.class, String.class, int.class, int.class, Boolean.class, kotlin.jvm.functions.Function1.class, MGImages.ChangeDetector.class);
        }

        if (settings.getBool("status", true)) {
            patcher.before(PresenceUtils.class, "getStatusDraweeSpanStringBuilder", (callFrame) -> {
                callFrame.args[5] = true;
            }, Context.class, Presence.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class);
        }
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}
