package com.aliucord.plugins

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import com.aliucord.Constants
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.utils.DimenUtils
import com.discord.api.premium.PremiumTier
import com.discord.databinding.WidgetChatOverlayBinding
import com.discord.stores.StoreStream
import com.discord.widgets.chat.input.AppFlexInputViewModel
import com.discord.widgets.chat.overlay.`WidgetChatOverlay$binding$2`
import com.google.android.material.floatingactionbutton.FloatingActionButton

@AliucordPlugin
class CharCounter : Plugin() {
    override fun start(context: Context) {
        val counter = TextView(context).apply {
            typeface = ResourcesCompat.getFont(context, Constants.Fonts.whitney_medium)
            textSize = DimenUtils.dpToPx(4).toFloat()
            visibility = View.GONE
            layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
                .apply { rightToRight = ConstraintLayout.LayoutParams.PARENT_ID }
            setTextColor(Color.WHITE)
        }

        patcher.patch(`WidgetChatOverlay$binding$2`::class.java.getDeclaredMethod("invoke", View::class.java), Hook {
            val root = (it.result as WidgetChatOverlayBinding).root as ConstraintLayout
            if (counter.parent == null) root.addView(counter)
            val floatingActionButton = root.findViewById<FloatingActionButton>(Utils.getResId("chat_overlay_old_messages_fab", "id"))
            ConstraintSet().apply {
                clone(root)
                connect(floatingActionButton.id, ConstraintSet.BOTTOM, counter.id, ConstraintSet.TOP)
                applyTo(root)
            }
        })

        patcher.patch(AppFlexInputViewModel::class.java.getDeclaredMethod("onInputTextChanged", String::class.java, Boolean::class.javaObjectType), Hook {
            val str = it.args[0] as String
            val maxChars = if (StoreStream.getUsers().me.premiumTier == PremiumTier.TIER_2) "4000" else "2000"

            counter.apply {
                visibility = if (str == "") View.GONE else View.VISIBLE
                text = String.format("%s/%s", str.length, maxChars)
            }
        })
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}