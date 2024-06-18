@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

import android.view.View
import com.aliucord.api.SettingsAPI
import com.aliucord.fragments.SettingsPage

class SystemThemeSettings(settings: SettingsAPI) : SettingsPage() {
    override fun onViewBound(view: View?) {
        super.onViewBound(view)

        val ctx = requireContext()
    }
}