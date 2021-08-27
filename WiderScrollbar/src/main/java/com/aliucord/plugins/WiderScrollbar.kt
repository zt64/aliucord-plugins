package com.aliucord.plugins

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import com.aliucord.Logger
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.entities.Plugin.Manifest.Author
import com.aliucord.fragments.SettingsPage
import com.aliucord.patcher.PinePatchFn
import com.aliucord.utils.DimenUtils
import com.aliucord.views.TextInput
import com.discord.databinding.WidgetChatListBinding
import com.discord.widgets.chat.list.WidgetChatList
import com.discord.widgets.chat.list.model.WidgetChatListModel

class WiderScrollbar : Plugin() {
    private val logger = Logger("WiderScrollbar")

    init {
        settingsTab = SettingsTab(PluginSettings::class.java).withArgs(settings)
    }

    class PluginSettings(private val settings: SettingsAPI) : SettingsPage() {
        override fun onViewBound(view: View) {
            super.onViewBound(view)

            setActionBarTitle("Wider Scrollbar")

            val textInput = TextInput(requireContext()).apply { hint = "Scrollbar width in DP" }
            textInput.editText?.apply {
                maxLines = 1
                inputType = InputType.TYPE_CLASS_NUMBER
                setText(settings.getInt("scrollbarWidth", 50).toString())
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable) {
                        try {
                            val width = s.toString().toInt()
                            if (width != 0) settings.setInt("scrollbarWidth", width)
                        } catch (ignored: Throwable) {
                            settings.setInt("scrollbarWidth", 50)
                        }
                    }
                })
            }
            addView(textInput)
        }
    }

    override fun getManifest(): Manifest {
        return Manifest().apply {
            authors = arrayOf(Author("zt", 289556910426816513L))
            description = "Allows changing the scrollbar width to make it easier to drag."
            version = "1.1.5"
            updateUrl = "https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/updater.json"
        }
    }

    override fun start(context: Context) {
        val getBinding = WidgetChatList::class.java.getDeclaredMethod("getBinding").apply { isAccessible = true }

        patcher.patch(WidgetChatList::class.java.getDeclaredMethod("configureUI", WidgetChatListModel::class.java), PinePatchFn {
            try {
                val binding = getBinding.invoke(it.thisObject) as WidgetChatListBinding
                binding.root.scrollBarSize = settings.getInt("scrollbarWidth", DimenUtils.dpToPx(50))
            } catch (e: Throwable) {
                logger.error(e)
            }
        })
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}