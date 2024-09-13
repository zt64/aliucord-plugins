package dev.zt64.aliucord.plugins.favoritechannels

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.aliucord.api.SettingsAPI
import com.aliucord.widgets.BottomSheet
import com.lytefast.flexinput.R

@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
class PluginSettings(private val settings: SettingsAPI) : BottomSheet() {
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        val ctx = requireContext()

        addView(
            TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header).apply {
                text = "Favorite Channels"
            }
        )

        addView(
            TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Label).apply {
                text = "Nothing here yet!"
            }
        )
    }
}