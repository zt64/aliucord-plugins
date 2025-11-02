import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import charcounter.PluginSettings;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.aliucord.utils.DimenUtils;
import com.discord.api.premium.PremiumTier;
import com.discord.databinding.WidgetChatOverlayBinding;
import com.discord.stores.StoreStream;
import com.discord.utilities.color.ColorCompat;
import com.discord.widgets.chat.input.AppFlexInputViewModel;
import com.lytefast.flexinput.R;

import java.lang.reflect.Method;

@SuppressWarnings("MISSING_DEPENDENCY_SUPERCLASS")
@AliucordPlugin
public class CharCounter extends Plugin {
    private TextView counter;
    private int normalColor;
    private int redColor;

    public CharCounter() {
        settingsTab = new SettingsTab(PluginSettings.class, SettingsTab.Type.BOTTOM_SHEET, settings);
    }

    @Override
    public void start(Context context) {
        int textSizeDimenId = Utils.getResId("uikit_textsize_small", "dimen");
        int typingOverlayId = Utils.getResId("chat_overlay_typing", "id");

        // Find the binding inner class
        try {
            Class<?> bindingClass = null;
            for (Class<?> clazz : com.discord.widgets.chat.overlay.WidgetChatOverlay.class.getDeclaredClasses()) {
                if (clazz.getName().contains("$binding$")) {
                    bindingClass = clazz;
                    break;
                }
            }

            if (bindingClass != null) {
                patcher.after(bindingClass, "invoke", (callFrame) -> {
                    WidgetChatOverlayBinding binding = (WidgetChatOverlayBinding) callFrame.getResult();
                    ConstraintLayout root = (ConstraintLayout) binding.getRoot();

                    normalColor = ColorCompat.getThemedColor(root.getContext(), R.b.colorInteractiveNormal);
                    redColor = ColorCompat.getThemedColor(root.getContext(), R.b.colorTextDanger);

                    counter = new TextView(root.getContext(), null, 0, R.i.UiKit_TextView);
                    counter.setId(View.generateViewId());
                    counter.setVisibility(View.GONE);
                    counter.setGravity(Gravity.CENTER_VERTICAL);
                    counter.setMaxLines(1);
                    
                    ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        DimenUtils.dpToPx(24)
                    );
                    params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
                    params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                    counter.setLayoutParams(params);
                    
                    int padding = DimenUtils.dpToPx(8);
                    counter.setPadding(padding, 0, padding, 0);
                    counter.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        root.getResources().getDimension(textSizeDimenId)
                    );
                    counter.setBackgroundColor(ColorCompat.getThemedColor(root.getContext(), R.c.primary_dark_600));
                    root.addView(counter);

                    // Update typing overlay layout
                    RelativeLayout typingOverlay = root.findViewById(typingOverlayId);
                    ConstraintLayout.LayoutParams typingParams = (ConstraintLayout.LayoutParams) typingOverlay.getLayoutParams();
                    typingParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                    typingParams.endToStart = counter.getId();
                    typingParams.width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
                }, View.class);
            }
        } catch (Exception e) {
            logger.error("Failed to patch WidgetChatOverlay", e);
        }

        patcher.after(AppFlexInputViewModel.class, "onInputTextChanged", (callFrame) -> {
            String text = (String) callFrame.args[0];
            int chars = text.length();
            int maxChars = StoreStream.getUsers().getMe().getPremiumTier() == PremiumTier.TIER_2 ? 4000 : 2000;

            if (counter != null) {
                int threshold = settings.getInt("threshold", 0);
                counter.setVisibility(chars >= threshold ? View.VISIBLE : View.GONE);
                
                boolean reverse = settings.getBool("reverse", false);
                counter.setText((reverse ? (maxChars - chars) : chars) + "/" + maxChars);
                counter.setTextColor(chars > maxChars ? redColor : normalColor);
            }
        }, String.class, Boolean.class);
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}
