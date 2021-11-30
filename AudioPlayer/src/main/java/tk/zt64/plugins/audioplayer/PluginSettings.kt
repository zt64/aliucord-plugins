package tk.zt64.plugins.audioplayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.aliucord.api.SettingsAPI
import com.aliucord.widgets.BottomSheet

class PluginSettings(val settings: SettingsAPI): BottomSheet() {
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        val ctx = requireContext()
    }
}