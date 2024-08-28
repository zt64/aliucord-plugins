@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

package dev.zt64.aliucord.plugins.customsounds

import android.app.Activity
import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.ContextCompat
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.fragments.SettingsPage
import com.aliucord.utils.DimenUtils
import com.aliucord.views.ToolbarButton
import com.lytefast.flexinput.R

class SoundSettings(private val settings: SettingsAPI) : SettingsPage() {
    @Suppress("SetTextI18n")
    override fun onViewBound(p0: View?) {
        super.onViewBound(p0)

        setActionBarTitle("Custom Sounds")

        val ctx = requireContext()
        val activity = requireActivity()
        val p = DimenUtils.dpToPx(2)

        soundMap.forEach { _, (title, setting) ->
            val header = TextView(ctx, null, 0, R.i.UiKit_Settings_Item).apply {
                setPadding(p, p, p, p)
                text = "$title: ${settings.getString(setting, "Default")}"
            }

            val subtext = TextView(ctx, null, 0, R.i.UiKit_Settings_Item_SubText).apply {
                setPadding(p, p, p, p)
                text = "Click to change"
            }

            val layout = com.aliucord.widgets.LinearLayout(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                orientation = LinearLayout.HORIZONTAL

                val launcher =
                    activity.registerForActivityResult(StartActivityForResult()) { res ->
                        if (res.resultCode == Activity.RESULT_OK) {
                            val uri = res.data?.data

                            if (uri == null) {
                                header.text = "$title: Default"
                            } else {
                                context.contentResolver.takePersistableUriPermission(
                                    uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                                )

                                settings.setString(setting, uri.toString())
                                header.text = "$title: $uri"
                            }
                        }
                    }

                setOnClickListener {
                    launcher.launch(
                        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            addFlags(
                                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                                    Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
                            )
                            type = "audio/*"
                        }
                    )
                }
            }

            LinearLayout(ctx, null, 0, R.i.UiKit_Settings_Item).apply {
                layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                    weight = 0.6f
                }
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_VERTICAL

                addView(header)
                addView(subtext)
                layout.addView(this)
            }

            ToolbarButton(ctx).run {
                setPadding(p, p, p * 4, p)
                layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
                ContextCompat.getDrawable(ctx, R.e.ic_refresh_white_a60_24dp)!!.mutate().let {
                    Utils.tintToTheme(it)
                    setImageDrawable(it, false)
                }
                setOnClickListener {
                    settings.setString(setting, null)
                    header.text = "$title: Default"
                }
                layout.addView(this)
            }

            addView(layout)
        }

        addDivider(ctx)

        addView(
            com.aliucord.views.Button(ctx).apply {
                text = "Reset to Default"
                setOnClickListener {
                    settings.resetSettings()
                    this@SoundSettings.close()
                }
            }
        )
    }
}