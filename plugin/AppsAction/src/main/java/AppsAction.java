import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.discord.databinding.WidgetChatListActionsBinding;
import com.discord.utilities.color.ColorCompat;
import com.lytefast.flexinput.R;

@AliucordPlugin
public class AppsAction extends Plugin {
    @Override
    public void start(Context ctx) {
        // Find the inner class for binding
        Class<?> bindingClass = null;
        for (Class<?> clazz : com.discord.widgets.chat.list.actions.WidgetChatListActions.class.getDeclaredClasses()) {
            if (clazz.getName().contains("$binding$")) {
                bindingClass = clazz;
                break;
            }
        }

        if (bindingClass != null) {
            patcher.after(bindingClass, "invoke", (callFrame) -> {
                WidgetChatListActionsBinding binding = (WidgetChatListActionsBinding) callFrame.getResult();
                Context context = binding.getRoot().getContext();
                ViewGroup layout = (ViewGroup) binding.a.getChildAt(0);

                TextView apps = new TextView(context, null, 0, R.i.UiKit_Settings_Item_Icon);
                apps.setText("Apps");
                apps.setVisibility(View.VISIBLE);

                Drawable drawable = ContextCompat.getDrawable(context, R.e.ic_authed_apps_24dp);
                if (drawable != null) {
                    drawable = drawable.mutate();
                    drawable.setTint(ColorCompat.getThemedColor(context, R.b.colorInteractiveNormal));
                }

                apps.setCompoundDrawables(drawable, null, null, null);
                layout.addView(apps);
            }, View.class);
        }
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}
