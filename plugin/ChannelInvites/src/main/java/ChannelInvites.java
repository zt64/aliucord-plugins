import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import channelinvites.InvitesPage;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.discord.utilities.color.ColorCompat;
import com.discord.widgets.channels.settings.WidgetTextChannelSettings;
import com.lytefast.flexinput.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings("MISSING_DEPENDENCY_SUPERCLASS")
@AliucordPlugin
public class ChannelInvites extends Plugin {
    @Override
    public void start(Context c) {
        int invitesLayoutId = View.generateViewId();
        int scrollViewId = Utils.getResId("scroll_view", "id");

        patcher.after(WidgetTextChannelSettings.class, "configureUI", (callFrame) -> {
            try {
                WidgetTextChannelSettings widget = (WidgetTextChannelSettings) callFrame.thisObject;
                
                // Access binding
                Method getBindingMethod = WidgetTextChannelSettings.class.getDeclaredMethod("access$getBinding$p", WidgetTextChannelSettings.class);
                getBindingMethod.setAccessible(true);
                Object binding = getBindingMethod.invoke(null, widget);
                
                Field rootField = binding.getClass().getField("root");
                View root = (View) rootField.get(binding);
                
                NestedScrollView scrollView = root.findViewById(scrollViewId);
                LinearLayout content = (LinearLayout) scrollView.getChildAt(0);

                if (content.findViewById(invitesLayoutId) != null) return;

                WidgetTextChannelSettings.Model model = (WidgetTextChannelSettings.Model) callFrame.args[0];
                Context context = widget.requireContext();

                LinearLayout invitesLayout = new LinearLayout(context, null, 0, R.i.UiKit_ViewGroup_LinearLayout);
                invitesLayout.setId(invitesLayoutId);

                TextView header = new TextView(context, null, 0, R.i.UiKit_Settings_Item_Header);
                header.setText("Invites");
                invitesLayout.addView(header);

                TextView invitesButton = new TextView(context, null, 0, R.i.UiKit_Settings_Item_Icon);
                Drawable icon = ContextCompat.getDrawable(context, R.e.ic_guild_invite_24dp);
                if (icon != null) {
                    icon = icon.mutate();
                    icon.setTint(ColorCompat.getThemedColor(context, R.b.colorInteractiveNormal));
                }
                invitesButton.setText("Invites");
                invitesButton.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
                invitesLayout.addView(invitesButton);

                invitesLayout.setOnClickListener(v -> {
                    Utils.openPageWithProxy(context, new InvitesPage(model.getChannel()));
                });

                content.addView(invitesLayout);
            } catch (Exception e) {
                logger.error("Failed to configure UI", e);
            }
        }, WidgetTextChannelSettings.Model.class);
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}
