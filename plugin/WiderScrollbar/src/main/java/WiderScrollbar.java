import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.api.SettingsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.utils.DimenUtils;
import com.aliucord.views.TextInput;
import com.discord.databinding.WidgetChatListBinding;
import com.discord.widgets.chat.list.WidgetChatList;
import com.discord.widgets.chat.list.model.WidgetChatListModel;

import java.lang.reflect.Method;

@SuppressWarnings("MISSING_DEPENDENCY_SUPERCLASS")
@AliucordPlugin
public class WiderScrollbar extends Plugin {
    private Method getBindingMethod;

    public WiderScrollbar() {
        settingsTab = new SettingsTab(PluginSettings.class, settings);
    }

    public static class PluginSettings extends SettingsPage {
        private final SettingsAPI settings;

        public PluginSettings(SettingsAPI settings) {
            this.settings = settings;
        }

        @Override
        public void onViewBound(View view) {
            super.onViewBound(view);

            setActionBarTitle("Wider Scrollbar");

            TextInput textInput = new TextInput(requireContext());
            textInput.getEditText().setMaxLines(1);
            textInput.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
            textInput.getEditText().setText(String.valueOf(settings.getInt("scrollbarWidth", 50)));
            textInput.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        int width = Integer.parseInt(s.toString());
                        if (width != 0) {
                            settings.setInt("scrollbarWidth", width);
                        }
                    } catch (Throwable ignored) {
                        settings.setInt("scrollbarWidth", 50);
                    }
                }
            });
            addView(textInput);
        }
    }

    @Override
    public void start(Context context) {
        try {
            getBindingMethod = WidgetChatList.class.getDeclaredMethod("getBinding");
            getBindingMethod.setAccessible(true);
        } catch (Exception e) {
            logger.error("Failed to get binding method", e);
            return;
        }

        patcher.after(WidgetChatList.class, "configureUI", (callFrame) -> {
            try {
                WidgetChatList widget = (WidgetChatList) callFrame.thisObject;
                WidgetChatListBinding binding = (WidgetChatListBinding) getBindingMethod.invoke(widget);
                binding.getRoot().setScrollBarSize(settings.getInt("scrollbarWidth", DimenUtils.dpToPx(50)));
            } catch (Exception e) {
                logger.error("Failed to set scrollbar width", e);
            }
        }, WidgetChatListModel.class);
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}
