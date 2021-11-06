package com.aliucord.plugins

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.plugins.charcounter.PluginSettings
import com.aliucord.utils.DimenUtils.dp
import com.discord.api.premium.PremiumTier
import com.discord.databinding.WidgetChatOverlayBinding
import com.discord.stores.StoreStream
import com.discord.utilities.color.ColorCompat
import com.discord.widgets.chat.input.AppFlexInputViewModel
import com.discord.widgets.chat.overlay.`WidgetChatOverlay$binding$2`
import com.lytefast.flexinput.R

@AliucordPlugin
class CharCounter : Plugin() {
    init {
        settingsTab = SettingsTab(PluginSettings::class.java, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings)
    }

    override fun start(context: Context) {
        val textSizeDimenId = Utils.getResId("uikit_textsize_small", "dimen")
        val typingOverlayId = Utils.getResId("chat_overlay_typing", "id")
        var counter: TextView? = null

        patcher.patch(`WidgetChatOverlay$binding$2`::class.java.getDeclaredMethod("invoke", View::class.java), Hook {
            val root = (it.result as WidgetChatOverlayBinding).root as ConstraintLayout

            counter = TextView(root.context, null, 0, R.i.UiKit_TextView).apply {
                id = View.generateViewId()
                visibility = View.GONE
                gravity = Gravity.CENTER_VERTICAL
                maxLines = 1
                layoutParams = ConstraintLayout.LayoutParams(WRAP_CONTENT, 24.dp).apply {
                    rightToRight = PARENT_ID
                    bottomToBottom = PARENT_ID
                }
                8.dp.let { dp -> setPadding(dp, 0, dp, 0) }
                setTextSize(TypedValue.COMPLEX_UNIT_PX, root.resources.getDimension(textSizeDimenId))
                setBackgroundColor(ColorCompat.getThemedColor(root.context, R.b.primary_630))
                root.addView(this)
            }

            (root.findViewById<RelativeLayout>(typingOverlayId).layoutParams as ConstraintLayout.LayoutParams).apply {
                startToStart = PARENT_ID
                endToStart = counter!!.id
                width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
            }
        })

        patcher.patch(AppFlexInputViewModel::class.java.getDeclaredMethod("onInputTextChanged", String::class.java, Boolean::class.javaObjectType), Hook {
            val str = it.args[0] as String
            val maxChars = if (StoreStream.getUsers().me.premiumTier == PremiumTier.TIER_2) "4000" else "2000"

            counter?.apply {
                visibility = if (str.isEmpty() && !settings.getBool("alwaysVisible", false)) View.GONE else View.VISIBLE
                text = String.format("%s/%s", str.length, maxChars)
            }
        })
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}