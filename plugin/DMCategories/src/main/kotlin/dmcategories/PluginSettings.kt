package dmcategories

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.aliucord.api.SettingsAPI
import com.aliucord.settings.delegate
import com.aliucord.widgets.BottomSheet
import com.lytefast.flexinput.R

@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
class PluginSettings(private val settings: SettingsAPI) : BottomSheet() {
    private var SettingsAPI.showSelected: Boolean by settings.delegate(true)
    private var SettingsAPI.showUnread: Boolean by settings.delegate(false)

    @Suppress("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        val ctx = requireContext()

        addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header).apply {
            text = "DM Categories"
        })
        addView(
            Util.createSwitch(ctx, "Show Selected", "Whether selected channels should be visible even if the category is collapsed")
                .apply {
                    isChecked = settings.showSelected
                    setOnCheckedListener { settings.showSelected = it }
                })
        addView(
            Util.createSwitch(ctx, "Show Unread", "Whether unread channels should be visible even if the category is collapsed")
                .apply {
                    isChecked = settings.showUnread
                    setOnCheckedListener { settings.showUnread = it }
                })
    }
}