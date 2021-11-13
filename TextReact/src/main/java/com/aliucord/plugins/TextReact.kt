package com.aliucord.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.fragments.InputDialog
import com.aliucord.patcher.Hook 
import com.discord.databinding.WidgetChatListActionsBinding
import com.discord.models.domain.emoji.Emoji
import com.discord.utilities.color.ColorCompat
import com.discord.widgets.chat.list.actions.WidgetChatListActions
import com.lytefast.flexinput.R
import java.lang.reflect.InvocationTargetException
import com.aliucord.plugins.textReactHelper.helper
import com.discord.stores.StoreStream
import com.aliucord.fragments.ConfirmDialog
import com.aliucord.Utils
import com.aliucord.Constants
import androidx.core.content.res.ResourcesCompat

@AliucordPlugin
class TextReact : Plugin() {
    @SuppressLint("SetTextI18n")
    override fun start(context: Context) {
        val icon = ContextCompat.getDrawable(context, R.e.ic_keyboard_black_24dp)
        val textReactId = View.generateViewId()

        with(WidgetChatListActions::class.java, {
            val getBinding = getDeclaredMethod("getBinding").apply { isAccessible = true }
            val addReaction = getDeclaredMethod("addReaction", Emoji::class.java).apply { isAccessible = true }

            patcher.patch(getDeclaredMethod("configureUI", WidgetChatListActions.Model::class.java), Hook  { callFrame ->
                try {
                    val binding = getBinding.invoke(callFrame.thisObject) as WidgetChatListActionsBinding
                    val textReact = binding.a.findViewById<TextView>(textReactId).apply {
                        visibility = if ((callFrame.args[0] as WidgetChatListActions.Model).manageMessageContext.canAddReactions) View.VISIBLE else View.GONE
                    }

                    if (!textReact.hasOnClickListeners()) textReact.setOnClickListener {
                        try {
                            val fragmentManager = (callFrame.thisObject as WidgetChatListActions).parentFragmentManager
                            val inDialog = InputDialog()
                                .setTitle("Text React!")
                                .setDescription("Enter some text to send as reactions.")
                                .setPlaceholderText("Enter text...")
                            inDialog.setOnOkListener {
                                val result = helper().generateEmojiArray(inDialog.input.toString().trim())
                                inDialog.dismiss()

                                if (result.second) {
                                    // show prompt that the text is incomplete
                                    val coDialog = ConfirmDialog()
                                        .setTitle("Text React!")
                                        .setDescription("Warning: The given input could not be 100% translated to reactions, do you still want to continue?")
                                    coDialog.setOnOkListener {
                                        coDialog.dismiss()
                                        Utils.threadPool.execute {
                                            result.first.forEach { emoji ->
                                               addReaction.invoke(callFrame.thisObject, StoreStream.getEmojis().unicodeEmojiSurrogateMap[emoji]!!)
                                                    Thread.sleep(1000)
                                            }
                                        }
                                        
                                    }
                                    coDialog.setOnCancelListener {
                                        coDialog.dismiss()
                                        Utils.showToast("Action cancelled by the user.")
                                    }
                                    coDialog.show(fragmentManager, "bbbbbb")
                                } else {
                                    Utils.threadPool.execute {
                                        result.first.forEach { emoji ->
                                            addReaction.invoke(callFrame.thisObject, StoreStream.getEmojis().unicodeEmojiSurrogateMap[emoji]!!)
                                                Thread.sleep(1000)
                                        }
                                    }
                                }
                            }
                            (callFrame.thisObject as WidgetChatListActions).dismiss()
                            inDialog.show(fragmentManager, "aaaaaa")
                        } catch (e: IllegalAccessException) {
                            e.printStackTrace()
                        } catch (e: InvocationTargetException) {
                            e.printStackTrace()
                        }
                    }
                } catch (ignored: Throwable) {
                }
            })

            patcher.patch(getDeclaredMethod("onViewCreated", View::class.java, Bundle::class.java), Hook  { callFrame ->
                val linearLayout = (callFrame.args[0] as NestedScrollView).getChildAt(0) as LinearLayout
                val ctx = linearLayout.context

                icon?.setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal))

                val textReact = TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
                    text = "Text React"
                    id = textReactId
                    typeface = ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_medium)
                    setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null)
                }

                linearLayout.addView(textReact, 1)
            })
        })
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}
