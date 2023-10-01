import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.fragments.SettingsPage
import com.aliucord.patcher.after
import com.aliucord.utils.DimenUtils.dp
import com.aliucord.views.TextInput
import com.discord.databinding.WidgetChatListBinding
import com.discord.widgets.chat.list.WidgetChatList
import com.discord.widgets.chat.list.model.WidgetChatListModel

@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
@AliucordPlugin
class WiderScrollbar : Plugin() {
    private val getBindingMethod = WidgetChatList::class.java
        .getDeclaredMethod("getBinding")
        .apply { isAccessible = true }

    private fun WidgetChatList.getBinding() = getBindingMethod(this) as WidgetChatListBinding

    init {
        settingsTab = SettingsTab(PluginSettings::class.java).withArgs(settings)
    }

    class PluginSettings(private val settings: SettingsAPI) : SettingsPage() {
        override fun onViewBound(view: View) {
            super.onViewBound(view)

            setActionBarTitle("Wider Scrollbar")

            val textInput = TextInput(requireContext()) // .apply { hint = "Scrollbar width in DP" }
            textInput.editText.apply {
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

    override fun start(context: Context) {
        patcher.after<WidgetChatList>("configureUI", WidgetChatListModel::class.java) {
            try {
                getBinding().root.scrollBarSize = settings.getInt("scrollbarWidth", 50.dp)
            } catch (e: Throwable) {
                logger.error(e)
            }
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}