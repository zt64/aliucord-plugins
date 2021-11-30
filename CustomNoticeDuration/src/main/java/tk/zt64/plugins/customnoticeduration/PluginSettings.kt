package tk.zt64.plugins.customnoticeduration

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.utils.DimenUtils.dp
import com.aliucord.widgets.BottomSheet
import com.discord.views.CheckedSetting
import com.lytefast.flexinput.R

class PluginSettings(private val settings: SettingsAPI) : BottomSheet() {
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        val ctx = requireContext()

        val noticeDuration = settings.getLong("noticeDuration", 5000)
        val currentTimeout = TextView(ctx, null, 0, R.i.UiKit_TextView).apply {
            text = "$noticeDuration ms"
            width = 72.dp
        }

        val offset = 1000
        val autoDismissNotice = settings.getBool("autoDismissNotice", true)
        val seekBar = SeekBar(ctx, null, 0, R.i.UiKit_SeekBar).apply {
            isEnabled = autoDismissNotice
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            max = 9000
            progress = (noticeDuration - offset).toInt()
            12.dp.let { setPadding(it, 0, it, 0) }
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    with((progress / 100) * 100) {
                        seekBar.progress = this
                        currentTimeout.text = "${this + offset} ms"
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    settings.setLong("noticeDuration", (seekBar.progress + offset).toLong())
                }
            })
        }

        addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Label).apply {
            text = "Custom Notice Duration"
        })

        addView(
            Utils.createCheckedSetting(ctx, CheckedSetting.ViewType.SWITCH, "Auto-Dismiss Notice", "Whether the notice should automatically dismiss").apply {
                isChecked = autoDismissNotice
                setOnCheckedListener {
                    settings.setBool("autoDismissNotice", it)
                    seekBar.isEnabled = it
                }
            })

        addView(LinearLayout(ctx, null, 0, R.i.UiKit_Settings_Item).apply {
            addView(currentTimeout)
            addView(seekBar)
        })
    }
}