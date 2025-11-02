import android.view.View;
import com.aliucord.Utils;
import com.aliucord.api.SettingsAPI;
import com.aliucord.fragments.SettingsPage;
import com.discord.views.CheckedSetting;

@SuppressWarnings("MISSING_DEPENDENCY_SUPERCLASS")
public class SystemThemeSettings extends SettingsPage {
    private final SettingsAPI settings;

    public SystemThemeSettings(SettingsAPI settings) {
        this.settings = settings;
    }

    @Override
    public void onViewBound(View view) {
        super.onViewBound(view);

        setActionBarTitle("System Theme");

        android.content.Context ctx = requireContext();

        CheckedSetting amoledSwitch = Utils.createCheckedSetting(
            ctx,
            CheckedSetting.ViewType.SWITCH,
            "Amoled",
            null
        );
        amoledSwitch.setIsChecked(settings.getBool("amoled", false));
        amoledSwitch.setOnCheckedListener(isChecked -> settings.setBool("amoled", isChecked));

        addView(amoledSwitch);
    }
}
