import android.content.Context;
import android.content.res.Configuration;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.discord.app.AppActivity;
import com.discord.stores.StoreStream;
import com.discord.stores.StoreUserSettingsSystem;

@SuppressWarnings("MISSING_DEPENDENCY_SUPERCLASS")
@AliucordPlugin
public class SystemTheme extends Plugin {
    private StoreUserSettingsSystem userSettingsSystem;

    public SystemTheme() {
        settingsTab = new SettingsTab(SystemThemeSettings.class, settings);
    }

    @Override
    public void start(Context ctx) {
        userSettingsSystem = StoreStream.getUserSettingsSystem();

        // Save the current value of the sync theme setting then restore when plugin is stopped
        if (!settings.exists("sync_theme")) {
            settings.setBool("sync_theme", userSettingsSystem.isThemeSyncEnabled());
        }
        userSettingsSystem.setIsSyncThemeEnabled(false);

        patcher.after(AppActivity.class, "onResume", (callFrame) -> {
            AppActivity activity = (AppActivity) callFrame.thisObject;
            Configuration configuration = activity.getApplicationContext().getResources().getConfiguration();
            int currentNightMode = configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK;

            switch (currentNightMode) {
                case Configuration.UI_MODE_NIGHT_NO:
                    userSettingsSystem.setTheme("light", false, null);
                    break;
                case Configuration.UI_MODE_NIGHT_YES:
                    String theme = settings.getBool("amoled", false) ? "pureEvil" : "dark";
                    userSettingsSystem.setTheme(theme, false, null);
                    break;
            }
        });
    }

    @Override
    public void stop(Context ctx) {
        patcher.unpatchAll();

        // Restore the sync theme setting to its original value
        if (userSettingsSystem != null) {
            userSettingsSystem.setIsSyncThemeEnabled(settings.getBool("sync_theme", false));
        }
    }
}
