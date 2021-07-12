package com.aliucord.plugins;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliucord.api.SettingsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.views.TextInput;
import com.discord.databinding.WidgetChatListBinding;
import com.discord.widgets.chat.list.WidgetChatList;
import com.discord.widgets.chat.list.model.WidgetChatListModel;

import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("unused")
public class RotatedChat extends Plugin {
    public RotatedChat () {
        settingsTab = new SettingsTab(PluginSettings.class).withArgs(settings);
    }

    public static class PluginSettings extends SettingsPage {
        private final SettingsAPI settings;
        public PluginSettings(SettingsAPI settings) {
            this.settings = settings;
        }

        @Override
        @SuppressWarnings("ResultOfMethodCallIgnored")
        public void onViewBound(View view) {
            super.onViewBound(view);
            setActionBarTitle("Wider Scrollbar");

            var context = view.getContext();
            var input = new TextInput(context);

            input.setHint("Rotation of chat in degrees");

            var editText = input.getEditText();
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
        manifest.authors = new Manifest.Author[]{ new Manifest.Author("Möth", 289556910426816513L) };
        manifest.description = "Rotate your chat!";
        manifest.version = "1.0.0";
        manifest.updateUrl = "https://raw.githubusercontent.com/litleck/aliucord-plugins/builds/updater.json";
        return manifest;
    }

    @Override
    public void start(Context context) throws Throwable {
        final var getBindingMethod = WidgetChatList.class.getDeclaredMethod("getBinding");
        getBindingMethod.setAccessible(true);

        patcher.patch(WidgetChatList.class.getDeclaredMethod("configureUI", WidgetChatListModel.class), new PinePatchFn(callFrame -> {
            WidgetChatListBinding widgetChatListBinding = null;

            try {
                widgetChatListBinding = (WidgetChatListBinding) getBindingMethod.invoke(callFrame.thisObject);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

            if (widgetChatListBinding == null) return;
            var root = (RecyclerView) widgetChatListBinding.getRoot();
            widgetChatListBinding.getRoot().setRotation(settings.getFloat("degrees", 0));
        }));
    }

    @Override
    public void stop(Context context) { patcher.unpatchAll(); }
}
