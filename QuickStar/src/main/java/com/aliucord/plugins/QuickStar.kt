package com.aliucord.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.aliucord.entities.Plugin
import com.aliucord.entities.Plugin.Manifest.Author
import com.aliucord.patcher.PinePatchFn
import com.discord.databinding.WidgetChatListActionsBinding
import com.discord.models.domain.emoji.Emoji
import com.discord.stores.StoreStream
import com.discord.utilities.color.ColorCompat
import com.discord.widgets.chat.list.actions.WidgetChatListActions
import com.lytefast.flexinput.R
import top.canyie.pine.Pine.CallFrame
import java.lang.reflect.InvocationTargetException

class QuickStar : Plugin() {
    override fun getManifest(): Manifest {
        return Manifest().apply {
            authors = arrayOf(Author("zt", 289556910426816513L))
            description = "Adds a star button to the message context menu that reacts to the message with the star emoji."
            version = "1.2.3"
            updateUrl = "https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/updater.json"
        }
    }

    @SuppressLint("SetTextI18n")
    override fun start(context: Context) {
        val icon = ContextCompat.getDrawable(context, R.d.ic_star_24dp)
        val quickStarId = View.generateViewId()

        with(WidgetChatListActions::class.java, {
            val getBinding = getDeclaredMethod("getBinding").apply { isAccessible = true }
            val addReaction = getDeclaredMethod("addReaction", Emoji::class.java).apply { isAccessible = true }

            patcher.patch(getDeclaredMethod("configureUI", WidgetChatListActions.Model::class.java), PinePatchFn { callFrame: CallFrame ->
                try {
                    val binding = getBinding.invoke(callFrame.thisObject) as WidgetChatListActionsBinding
                    val quickStar = binding.a.findViewById<TextView>(quickStarId).apply {
                        visibility = if ((callFrame.args[0] as WidgetChatListActions.Model).manageMessageContext.canAddReactions) View.VISIBLE else View.GONE
                    }

                    if (!quickStar.hasOnClickListeners()) quickStar.setOnClickListener {
                        try {
                            addReaction.invoke(callFrame.thisObject, StoreStream.getEmojis().unicodeEmojisNamesMap["star"])
                            (callFrame.thisObject as WidgetChatListActions).dismiss()
                        } catch (e: IllegalAccessException) {
                            e.printStackTrace()
                        } catch (e: InvocationTargetException) {
                            e.printStackTrace()
                        }
                    }
                } catch (ignored: Throwable) {
                }
            })

            patcher.patch(getDeclaredMethod("onViewCreated", View::class.java, Bundle::class.java), PinePatchFn { callFrame: CallFrame ->
                val linearLayout = (callFrame.args[0] as NestedScrollView).getChildAt(0) as LinearLayout
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