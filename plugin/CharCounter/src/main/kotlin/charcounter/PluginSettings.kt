package charcounter

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.settings.delegate
import com.aliucord.utils.DimenUtils.dp
import com.aliucord.views.TextInput
import com.aliucord.widgets.BottomSheet
import com.discord.views.CheckedSetting
import com.lytefast.flexinput.R

@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
class PluginSettings(private val settings: SettingsAPI) : BottomSheet() {
    private var SettingsAPI.reverse: Boolean by settings.delegate(false)
    private var SettingsAPI.threshold: Int by settings.delegate(1)

    @Suppress("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        val ctx = requireContext()

        addView(
            Utils.createCheckedSetting(ctx, CheckedSetting.ViewType.SWITCH, "Reverse", "Whether the counter goes in reverse, counting down how many chars remain")
                .apply {
                    isChecked = settings.reverse
                    setOnCheckedListener { settings.reverse = it }
                }
        )

        addView(TextInput(ctx).apply {
            editText.apply {
                inputType = InputType.TYPE_CLASS_NUMBER
                setText(settings.threshold.toString())
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable) {
                        s.toString().toIntOrNull()?.let { settings.threshold = it }
                    }
                })
            }
            setHint("Threshold")
            8.dp.let { setPadding(it, it, it, it) }
        })
        addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Addition).apply {
            text = "Minimum number of characters for the counter to appear. Set to zero for it to always be visible"
        })
    }
}