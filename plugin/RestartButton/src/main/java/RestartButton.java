import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import androidx.core.content.ContextCompat;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.discord.utilities.color.ColorCompat;
import com.discord.widgets.settings.WidgetSettings;
import com.lytefast.flexinput.R;

@SuppressWarnings("MISSING_DEPENDENCY_SUPERCLASS")
@AliucordPlugin
public class RestartButton extends Plugin {
    @Override
    public void start(Context context) {
        Drawable icon = ContextCompat.getDrawable(context, com.yalantis.ucrop.R.c.ucrop_rotate);
        if (icon != null) {
            icon = icon.mutate();
        }

        Drawable finalIcon = icon;
        patcher.after(WidgetSettings.class, "configureToolbar", (callFrame) -> {
            WidgetSettings widgetSettings = (WidgetSettings) callFrame.thisObject;
            
            if (finalIcon != null) {
                finalIcon.setTint(ColorCompat.getThemedColor(widgetSettings.requireContext(), R.b.colorInteractiveNormal));
            }

            widgetSettings.requireAppActivity()
                .u
                .menu
                .add("Restart")
                .setIcon(finalIcon)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                .setOnMenuItemClickListener(item -> {
                    Intent intent = context.getPackageManager().getLaunchIntentForPackage(
                        context.getPackageName()
                    );
                    context.startActivity(Intent.makeRestartActivityTask(intent != null ? intent.getComponent() : null));
                    Runtime.getRuntime().exit(0);
                    return false;
                });
        });
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}
