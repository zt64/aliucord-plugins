package tk.zt64.plugins.pindms

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.aliucord.api.SettingsAPI
import com.aliucord.widgets.BottomSheet
import com.lytefast.flexinput.R

class PluginSettings(val settings: SettingsAPI): BottomSheet() {
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        val ctx = requireContext()

        addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header).apply {
            text = "PinDMs"
        })
    }
}