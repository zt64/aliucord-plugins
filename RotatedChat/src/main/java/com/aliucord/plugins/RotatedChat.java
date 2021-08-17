package com.aliucord.plugins;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.aliucord.api.SettingsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.views.TextInput;
import com.discord.databinding.WidgetChatListBinding;
import com.discord.widgets.chat.list.WidgetChatList;
import com.discord.widgets.chat.list.model.WidgetChatListModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RotatedChat extends Plugin {
    public RotatedChat () { settingsTab = new SettingsTab(PluginSettings.class).withArgs(settings); }

    public static class PluginSettings extends SettingsPage {
        private final SettingsAPI settings;

        public PluginSettings(SettingsAPI settings) { this.settings = settings; }

        @Override
        public void onViewBound(View view) {
            super.onViewBound(view);

            setActionBarTitle("Wider Scrollbar");

            TextInput input = new TextInput(requireContext());
            input.setHint("Rotation of chat in degrees");

            EditText editText = input.getEditText();
            assert editText != null;

            editText.setMaxLines(1);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            editText.setText(String.valueOf(settings.getFloat("degrees", 0)));
            editText.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                public void onTextChanged(CharSequence s, int start, int before, int count) { }
                public void afterTextChanged(Editable s) {
                    try {
                        settings.setFloat("degrees", Float.parseFloat(s.toString()));
                    } catch (Throwable ignored) {
                        settings.setFloat("degrees", 0);
                    }
                }
            });

            addView(input);
        }
    }

    @NonNull
    @Override
    public Manifest getManifest() {
        Manifest manifest = new Manifest();
        manifest.authors = new Manifest.Author[]{ new Manifest.Author("zt", 289556910426816513L) };
        manifest.description = "Rotate your chat!";
        manifest.version = "1.0.2";
        manifest.updateUrl = "https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/updater.json";
        return manifest;
    }

    @Override
    public void start(Context context) throws Throwable {
        final Method getBindingMethod = WidgetChatList.class.getDeclaredMethod("getBinding");
        getBindingMethod.setAccessible(true);

        patcher.patch(WidgetChatList.class.getDeclaredMethod("configureUI", WidgetChatListModel.class), new PinePatchFn(callFrame -> {
            try {
                WidgetChatListBinding widgetChatListBinding = (WidgetChatListBinding) getBindingMethod.invoke(callFrame.thisObject);
                if (widgetChatListBinding != null) widgetChatListBinding.getRoot().setRotation(settings.getFloat("degrees", 0));
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    public void stop(Context context) { patcher.unpatchAll(); }
}
