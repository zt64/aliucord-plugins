package compactmode

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.utils.DimenUtils.dp
import com.aliucord.widgets.BottomSheet
import com.discord.views.CheckedSetting
import com.lytefast.flexinput.R

class PluginSettings(val settings: SettingsAPI) : BottomSheet() {
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        val ctx = requireContext()

        fun addCheckedSetting(
            title: String, subtext: String, setting: String, default: Boolean = false
        ): CheckedSetting {
            return Utils.createCheckedSetting(ctx, CheckedSetting.ViewType.SWITCH, title, subtext).apply {
                isChecked = settings.getBool(setting, default)
                setOnCheckedListener { settings.setBool(setting, it) }
                linearLayout.addView(this)
            }
        }

        fun createSeekbar(label: String, setting: String, default: Int, min: Int, max: Int): LinearLayout =
            LinearLayout(ctx, null, 0, R.i.UiKit_Settings_Item).apply {
                val settingValue = settings.getInt(setting, default)
                val display = TextView(ctx, null, 0, R.i.UiKit_TextView).apply {
                    text = "$settingValue $label"
                    width = 48.dp
                    addView(this)
                }

                addView(SeekBar(ctx, null, 0, R.i.UiKit_SeekBar).apply {
                    layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    progress = settingValue - min
                    12.dp.let { setPadding(it, 0, it, 0) }
                    setMax(max - min)
                    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                            display.text = "${min + progress} dp"
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar) {}

                        override fun onStopTrackingTouch(seekBar: SeekBar) {
                            settings.setInt(setting, (seekBar.progress + min))
                        }
                    })
                })
            }

        addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header).apply {
            text = "CompactMode"
        })

        addCheckedSetting("Hide Avatar", "Whether the avatar should be hidden", "hideAvatar", false)
        addCheckedSetting("Hide Reply Icon", "Whether the reply icon should be hidden", "hideReplyIcon", true)

        addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Label).apply {
            text = "Avatar Scale"
        })
        addView(createSeekbar("dp", "avatarScale", 28, 32, 40))

        addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Label).apply {
            text = "Header Left Margin"
        })
        addView(createSeekbar("dp", "headerMargin", 12, 0, 32))

        addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Label).apply {
            text = "Content Left Margin"
        })
        addView(createSeekbar("dp", "contentMargin", 18, 0, 32))

        addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Label).apply {
            text = "Message Top Padding"
        })
        addView(createSeekbar("dp", "messagePadding", 10, 0, 14))
    }
}