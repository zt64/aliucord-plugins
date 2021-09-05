package com.aliucord.plugins.bettermediaviewer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.fragments.SettingsPage
import com.aliucord.utils.DimenUtils
import com.aliucord.views.Button
import com.aliucord.views.Divider
import com.discord.views.CheckedSetting
import com.discord.views.RadioManager
import com.lytefast.flexinput.R
import java.util.*

class PluginSettings(private val settingsAPI: SettingsAPI) : SettingsPage() {
    private var launcher: ActivityResultLauncher<Intent>? = null

    @SuppressLint("SetTextI18n")
    override fun onViewBound(view: View) {
        super.onViewBound(view)

        setActionBarTitle("Better Media Viewer")

        val ctx = requireContext()

        // Create settings for changing auto hiding behaviour
        val offset = 500
        val controlsTimeout = settingsAPI.getInt("controlsTimeout", 3000)
        val currentTimeout = TextView(ctx, null, 0, R.h.UiKit_TextView).apply {
            text = "$controlsTimeout ms"
            width = DimenUtils.dpToPx(72)
        }
        val seekbar = SeekBar(ctx, null, 0, R.h.UiKit_SeekBar).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            max = 4500
            progress = controlsTimeout - offset
            setPadding(DimenUtils.dpToPx(12), 0, DimenUtils.dpToPx(12), 0)
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    with((progress / 100) * 100) {
                        seekBar.progress = this
                        currentTimeout.text = "${this + offset} ms"
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    settingsAPI.setInt("controlsTimeout", seekBar.progress + offset)
                }
            })
        }
        val timeoutSection = LinearLayout(ctx, null, 0, R.h.UiKit_Settings_Item).apply {
            visibility = if (settingsAPI.getBool("autoHideControls", true)) View.VISIBLE else View.GONE
            addView(currentTimeout)
            addView(seekbar)
        }

        addView(createCheckedSetting(ctx, "Auto-hide controls", "Hide the top and bottom bar after a delay", "autoHideControls", true).apply {
            setOnCheckedListener {
                settingsAPI.setBool("autoHideControls", it)
                timeoutSection.visibility = if (it) View.VISIBLE else View.GONE
            }
        })
        addView(timeoutSection)
        addView(Divider(ctx))

        val radios = listOf(Utils.createCheckedSetting(ctx, CheckedSetting.ViewType.RADIO, "Status bar", null), Utils.createCheckedSetting(ctx, CheckedSetting.ViewType.RADIO, "Navigation bar", null), Utils.createCheckedSetting(ctx, CheckedSetting.ViewType.RADIO, "Both bars", null))
        val radioManager = RadioManager(radios)
        val radioGroup = RadioGroup(ctx).apply { visibility = if (settingsAPI.getBool("immersiveModeState", true)) View.VISIBLE else View.GONE }
        for (i in radios.indices) {
            val radio = radios[i]
            radio.e {
                settingsAPI.setInt("immersiveModeType", i)
                radioManager.a(radio)
            }
            radioGroup.addView(radio)
            if (i == settingsAPI.getInt("immersiveModeType", 0)) radioManager.a(radio)
        }

        addView(createCheckedSetting(ctx, "Immersive mode", "Hide the status bar, navigation bar, " + "or both when viewing media", "immersiveModeState", false).apply {
            setOnCheckedListener {
                settingsAPI.setBool("immersiveModeState", it)
                radioGroup.visibility = if (it) View.VISIBLE else View.GONE
            }
        })
        addView(radioGroup)
        addView(Divider(ctx))
        addView(createCheckedSetting(ctx, "Hide back button", "Hide the back button from the toolbar", "hideBackButton", false))
        addView(Divider(ctx))
        addView(createCheckedSetting(ctx, "Bottom toolbar", "Moves the bar to the bottom of the screen for easier one handed use. Only supports images", "bottomToolbar", false))
        addView(Divider(ctx))
        addView(createCheckedSetting(ctx, "Remove zoom limit", "Removes the zoom limit for images", "removeZoomLimit", true))
        addView(Divider(ctx))
        addView(createCheckedSetting(ctx, "Show Open In Browser", "Shows the open in browser button on the toolbar", "showOpenInBrowser", true))
        addView(Divider(ctx))
        val downloadDir = TextView(ctx, null, 0, R.h.UiKit_TextView_Subtext).apply {
            text = "Current Directory: " + settingsAPI.getString("downloadDir", Environment.getExternalStorageDirectory().absolutePath + "/Downloads")
        }

        if (launcher == null) launcher = requireActivity().registerForActivityResult(StartActivityForResult()) { res: ActivityResult ->
            if (res.resultCode == Activity.RESULT_OK) {
                val data = res.data ?: return@registerForActivityResult
                val dir = data.data.toString()
                settingsAPI.setString("downloadDir", dir)
                downloadDir.text = String.format("Current Directory: %s", dir)
            }
        }

        addView(Button(ctx).apply {
            text = "Set Download Directory"
            setOnClickListener {
                launcher?.launch(Intent.createChooser(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), "Choose a download directory"))
            }
        })
        addView(downloadDir)
    }

    private fun createCheckedSetting(ctx: Context, title: String, subtitle: String, setting: String, defValue: Boolean): CheckedSetting {
        return Utils.createCheckedSetting(ctx, CheckedSetting.ViewType.SWITCH, title, subtitle).apply {
            isChecked = settingsAPI.getBool(setting, defValue)
            setOnCheckedListener {
                settingsAPI.setBool(setting, it)
                PluginManager.stopPlugin("BetterMediaViewer")
                PluginManager.startPlugin("BetterMediaViewer")
            }
        }
    }
}