import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.*
import com.discord.widgets.chat.input.AppFlexInputViewModel
import com.lytefast.flexinput.R
import com.lytefast.flexinput.fragment.`FlexInputFragment$c`
import com.lytefast.flexinput.widget.FlexEditText

@AliucordPlugin
class CursorInput : Plugin() {
    private lateinit var flexEditText: FlexEditText

    override fun start(context: Context) {
        patcher.after<`FlexInputFragment$c`>("invoke", Object::class.java) {
            flexEditText = (it.result as b.b.a.e.a).root.findViewById(R.f.text_input)
        }

        patcher.instead<AppFlexInputViewModel>(
            "onInputTextAppended",
            String::class.java
        ) { (_, str: String) ->
            val baseString = requireViewState().a

            if (!::flexEditText.isInitialized) {
                onInputTextChanged(baseString + str, null)
            } else {
                val selectionEnd = flexEditText.selectionEnd

                val trimmed = if (selectionEnd != baseString.length) str.trim() else str

                onInputTextChanged(
                    StringBuilder(baseString).insert(selectionEnd, trimmed).toString(), null
                )
                flexEditText.setSelection(selectionEnd + trimmed.length)
            }
        }
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
    }
}