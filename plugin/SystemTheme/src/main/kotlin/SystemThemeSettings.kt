@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

import android.view.View
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.fragments.SettingsPage
import com.discord.views.CheckedSetting

class SystemThemeSettings(private val settings: SettingsAPI) : SettingsPage() {
    override fun onViewBound(view: View?) {
        super.onViewBound(view)

        setActionBarTitle("System Theme")

        val ctx = requireContext()

        val amoledSwitch = Utils
            .createCheckedSetting(ctx, CheckedSetting.ViewType.SWITCH, "Amoled", null)
            .apply {
                isChecked = settings.getBool("amoled", false)
                setOnCheckedListener {
                    settings.setBool("amoled", it)
                }
            }

        addView(amoledSwitch)
    }
}