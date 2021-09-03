package com.aliucord.plugins

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import com.aliucord.Logger
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.fragments.SettingsPage
import com.aliucord.patcher.PinePatchFn
import com.aliucord.views.TextInput
import com.discord.databinding.WidgetChatListBinding
import com.discord.widgets.chat.list.WidgetChatList
import com.discord.widgets.chat.list.model.WidgetChatListModel
import java.lang.reflect.InvocationTargetException

@AliucordPlugin
class RotatedChat : Plugin() {
    private var logger = Logger("RotatedChat")

    init {
        settingsTab = SettingsTab(PluginSettings::class.java).withArgs(settings)
    }

    class PluginSettings(private val settings: SettingsAPI) : SettingsPage() {
        override fun onViewBound(view: View) {
            super.onViewBound(view)

            setActionBarTitle("Rotated Chat")

            val textInput = TextInput(requireContext()).apply { hint = "Rotation of chat in degrees" }
            textInput.editText?.apply {
                maxLines = 1
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                setText(settings.getFloat("degrees", 0f).toString())
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable) {
                        try {
                            settings.setFloat("degrees", s.toString().toFloat())
                        } catch (ignored: Throwable) {
                            settings.setFloat("degrees", 0f)
                        }
                    }
                })
            }
            addView(textInput)
        }
    }

    override fun start(context: Context) {
        val getBindingMethod = WidgetChatList::class.java.getDeclaredMethod("getBinding").apply { isAccessible = true }

        patcher.patch(WidgetChatList::class.java.getDeclaredMethod("configureUI", WidgetChatListModel::class.java), PinePatchFn {
            try {
                with(getBindingMethod.invoke(it.thisObject) as WidgetChatListBinding) {
                    root.rotation = settings.getFloat("degrees", 0f)
                }
            } catch (e: IllegalAccessException) {
                logger.error(e)
            } catch (e: InvocationTargetException) {
                logger.error(e)
            }
        })
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}