package com.aliucord.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.PinePatchFn
import com.discord.databinding.WidgetChatListActionsBinding
import com.discord.models.domain.emoji.Emoji
import com.discord.stores.StoreStream
import com.discord.utilities.color.ColorCompat
import com.discord.widgets.chat.list.actions.WidgetChatListActions
import com.lytefast.flexinput.R

@AliucordPlugin
class QuickStar : Plugin() {
    private val getBindingMethod = WidgetChatListActions::class.java.getDeclaredMethod("getBinding").apply {
        isAccessible = true
    }

    private fun WidgetChatListActions.getBinding(): WidgetChatListActionsBinding =
            getBindingMethod.invoke(this) as WidgetChatListActionsBinding

    private fun WidgetChatListActions.addReaction(emoji: Emoji) =
            WidgetChatListActions.`access$addReaction`(this, emoji)

    @SuppressLint("SetTextI18n")
    override fun start(context: Context) {
        val icon = ContextCompat.getDrawable(context, R.d.ic_star_24dp)
        val quickStarId = View.generateViewId()

        with(WidgetChatListActions::class.java, {
            patcher.patch(getDeclaredMethod("configureUI", WidgetChatListActions.Model::class.java), PinePatchFn {
                with (it.thisObject as WidgetChatListActions) {
                    val root = getBinding().root.findViewById<LinearLayout>(Utils.getResId("dialog_chat_actions_container", "id"))
                    val quickStar = root.findViewById<TextView>(quickStarId).apply {
                        visibility = if ((it.args[0] as WidgetChatListActions.Model).manageMessageContext.canAddReactions) View.VISIBLE else View.GONE
                    }

                    quickStar.setOnClickListener {
                        addReaction(StoreStream.getEmojis().unicodeEmojisNamesMap["star"]!!)
                        dismiss()
                    }
                }
            })

            patcher.patch(getDeclaredMethod("onViewCreated", View::class.java, Bundle::class.java), PinePatchFn {
                val linearLayout = (it.args[0] as NestedScrollView).getChildAt(0) as LinearLayout
                val ctx = linearLayout.context

                icon?.setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal))

                val quickStar = TextView(ctx, null, 0, R.h.UiKit_Settings_Item_Icon).apply {
                    text = "Quick Star"
                    id = quickStarId
                    setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null)
                }

                linearLayout.addView(quickStar, 1)
            })
        })
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}