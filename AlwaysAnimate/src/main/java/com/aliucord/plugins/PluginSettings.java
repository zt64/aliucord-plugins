package com.aliucord.plugins;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.aliucord.PluginManager;
import com.aliucord.Utils;
import com.aliucord.api.SettingsAPI;
import com.aliucord.widgets.BottomSheet;
import com.discord.views.CheckedSetting;

public class PluginSettings extends BottomSheet {
    private final SettingsAPI settings;

    public PluginSettings(SettingsAPI settings) {
        this.settings = settings;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);

        Context ctx = requireContext();
        addView(createCheckedSetting(ctx, "Server icon", "guildIcon"));
        addView(createCheckedSetting(ctx, "Message author avatar", "authorAvatar"));
        addView(createCheckedSetting(ctx, "Members list avatar", "memberListAvatar"));
    }

    private CheckedSetting createCheckedSetting(Context ctx, String title, String setting) {
        var cs = Utils.createCheckedSetting(ctx, CheckedSetting.ViewType.SWITCH, title, null);
        cs.setChecked(settings.getBool(setting, true));
        cs.setOnCheckedListener(checked -> {
            settings.setBool(setting, checked);
            PluginManager.stopPlugin("AlwaysAnimate");
            PluginManager.startPlugin("AlwaysAnimate");
        });
        return cs;
    }
}
