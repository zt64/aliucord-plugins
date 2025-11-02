package dev.zt64.aliucord.plugins.frecents

import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.aliucord.Constants
import com.aliucord.fragments.SettingsPage
import com.aliucord.views.Button
import com.lytefast.flexinput.R
import java.io.File

class FrecentsSettings(private val frecencySettingsManager: FrecencySettingsManager) : SettingsPage() {
    override fun onViewBound(view: View) {
        super.onViewBound(view)

        setActionBarTitle("Frecents")

        val ctx = requireContext()

        addView(
            TextView(ctx, null, 0, R.i.UiKit_TextView).apply {
                text =
                    "If you are experiencing issues with the Frecents plugin, you can use the button below to export your Frecents data" +
                    "to a file named Frecents.bin in the Aliucord folder. You can then share this file with me (.zooter.) on Discord for " +
                    "further assistance."
            }
        )

        addView(
            TextView(ctx, null, 0, R.i.UiKit_TextView_Bold).apply {
                text = "NOTE: All your favorites and recents data will be included in this file. Be cautious when sharing it."
            }
        )

        addView(
            Button(ctx).apply {
                text = "Export debug information"
                setBackgroundColor(
                    ResourcesCompat.getColor(
                        ctx.resources,
                        R.c.uikit_btn_bg_color_selector_red,
                        ctx.theme
                    )
                )
                setOnClickListener {
                    try {
                        val file = File(Constants.BASE_PATH, "Frecents.bin").apply {
                            writeBytes(frecencySettingsManager.settings.toByteArray())
                        }

                        Toast.makeText(ctx, "Succesfully wrote to ${file.absolutePath}", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(ctx, "Failed to write debug information: ${e.message}", Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                    }
                }
            }
        )
    }
}