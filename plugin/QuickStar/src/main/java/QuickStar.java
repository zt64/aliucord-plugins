import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.discord.databinding.WidgetChatListActionsBinding;
import com.discord.models.domain.emoji.Emoji;
import com.discord.stores.StoreStream;
import com.discord.utilities.color.ColorCompat;
import com.discord.widgets.chat.list.actions.WidgetChatListActions;
import com.lytefast.flexinput.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings("MISSING_DEPENDENCY_SUPERCLASS")
@AliucordPlugin
public class QuickStar extends Plugin {
    private Method getBindingMethod;
    private Method addReactionMethod;

    @Override
    public void start(Context context) {
        try {
            getBindingMethod = WidgetChatListActions.class.getDeclaredMethod("getBinding");
            getBindingMethod.setAccessible(true);
            
            addReactionMethod = WidgetChatListActions.class.getDeclaredMethod("access$addReaction", WidgetChatListActions.class, Emoji.class);
            addReactionMethod.setAccessible(true);
        } catch (Exception e) {
            logger.error("Failed to get reflection members", e);
            return;
        }

        int actionsContainerId = Utils.getResId("dialog_chat_actions_container", "id");
        int quickStarId = View.generateViewId();
        Emoji starEmoji = StoreStream.getEmojis().getUnicodeEmojisNamesMap().get("star");
        Drawable icon = ContextCompat.getDrawable(context, R.e.ic_star_24dp);

        patcher.after(WidgetChatListActions.class, "configureUI", (callFrame) -> {
            try {
                WidgetChatListActions widget = (WidgetChatListActions) callFrame.thisObject;
                WidgetChatListActions.Model model = (WidgetChatListActions.Model) callFrame.args[0];
                
                WidgetChatListActionsBinding binding = (WidgetChatListActionsBinding) getBindingMethod.invoke(widget);
                LinearLayout root = binding.getRoot().findViewById(actionsContainerId);

                TextView quickStar = root.findViewById(quickStarId);
                if (quickStar != null) {
                    quickStar.setVisibility(model.getManageMessageContext().getCanAddReactions() ? View.VISIBLE : View.GONE);
                    quickStar.setOnClickListener(v -> {
                        try {
                            addReactionMethod.invoke(null, widget, starEmoji);
                            widget.dismiss();
                        } catch (Exception e) {
                            logger.error("Failed to add reaction", e);
                        }
                    });
                }
            } catch (Exception e) {
                logger.error("Failed to configure UI", e);
            }
        }, WidgetChatListActions.Model.class);

        patcher.after(WidgetChatListActions.class, "onViewCreated", (callFrame) -> {
            try {
                View view = (View) callFrame.args[0];
                LinearLayout linearLayout = (LinearLayout) ((NestedScrollView) view).getChildAt(0);
                Context ctx = linearLayout.getContext();

                if (icon != null) {
                    icon.setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal));
                }

                TextView quickStar = new TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon);
                quickStar.setId(quickStarId);
                quickStar.setText("Quick Star");
                quickStar.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null);

                linearLayout.addView(quickStar, 1);
            } catch (Exception e) {
                logger.error("Failed to create quick star button", e);
            }
        }, View.class, Bundle.class);
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}
