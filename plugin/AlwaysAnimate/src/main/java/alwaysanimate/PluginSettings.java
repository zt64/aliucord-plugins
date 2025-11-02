package alwaysanimate;

import android.os.Bundle;
import android.view.View;
import com.aliucord.PluginManager;
import com.aliucord.Utils;
import com.aliucord.api.SettingsAPI;
import com.aliucord.widgets.BottomSheet;
import com.discord.views.CheckedSetting;

@SuppressWarnings("MISSING_DEPENDENCY_SUPERCLASS")
public class PluginSettings extends BottomSheet {
    private final SettingsAPI settings;

    public PluginSettings(SettingsAPI settings) {
        this.settings = settings;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);

        android.content.Context ctx = requireContext();

        createCheckedSetting(ctx, "Server icons", "guildIcons", true);
        createCheckedSetting(ctx, "User avatars", "avatars", true);
        createCheckedSetting(ctx, "Statuses", "status", true);
        createCheckedSetting(ctx, "Round Animated Avatars", "roundedAvatars", true);
        createCheckedSetting(ctx, "Disable animations when battery saver is on", "batterySaver", false);
    }

    private void createCheckedSetting(android.content.Context ctx, String title, String setting, boolean defaultChecked) {
        CheckedSetting checkedSetting = Utils.createCheckedSetting(ctx, CheckedSetting.ViewType.SWITCH, title, null);
        checkedSetting.setIsChecked(settings.getBool(setting, defaultChecked));

        checkedSetting.setOnCheckedListener(isChecked -> {
            settings.setBool(setting, isChecked);
            PluginManager.stopPlugin("AlwaysAnimate");
            PluginManager.startPlugin("AlwaysAnimate");
        });

        addView(checkedSetting);
    }
}
