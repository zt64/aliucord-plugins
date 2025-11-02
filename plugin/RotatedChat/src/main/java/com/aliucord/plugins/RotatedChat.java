package com.aliucord.plugins;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.api.SettingsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.views.TextInput;
import com.discord.databinding.WidgetChatListBinding;
import com.discord.widgets.chat.list.WidgetChatList;
import com.discord.widgets.chat.list.model.WidgetChatListModel;

import java.lang.reflect.Method;

@SuppressWarnings("MISSING_DEPENDENCY_SUPERCLASS")
@AliucordPlugin
public class RotatedChat extends Plugin {
    private Method getBindingMethod;

    public RotatedChat() {
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

            setActionBarTitle("Rotated Chat");

            TextInput textInput = new TextInput(requireContext());
            textInput.getEditText().setMaxLines(1);
            textInput.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            textInput.getEditText().setText(String.valueOf(settings.getFloat("degrees", 0f)));
            textInput.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        settings.setFloat("degrees", Float.parseFloat(s.toString()));
                    } catch (Throwable ignored) {
                        settings.setFloat("degrees", 0f);
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
                binding.getRoot().setRotation(settings.getFloat("degrees", 0f));
            } catch (Exception e) {
                logger.error("Failed to set rotation", e);
            }
        }, WidgetChatListModel.class);
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}
