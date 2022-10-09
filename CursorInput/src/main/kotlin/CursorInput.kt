import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.patcher.instead
import com.discord.widgets.chat.input.AppFlexInputViewModel
import com.lytefast.flexinput.R
import com.lytefast.flexinput.fragment.`FlexInputFragment$c`
import com.lytefast.flexinput.widget.FlexEditText

@AliucordPlugin
class CursorInput : Plugin() {
    private var flexEditText: FlexEditText? = null

    override fun start(context: Context) {
        patcher.after<`FlexInputFragment$c`>("invoke", Object::class.java) {
            flexEditText = (it.result as b.b.a.e.a).root.findViewById(R.f.text_input)
        }

        patcher.instead<AppFlexInputViewModel>("onInputTextAppended", String::class.java) {
            val baseString = requireViewState().a
            var str = it.args[0] as String

            if (flexEditText == null) {
                onInputTextChanged(baseString + str, null)
            } else {
                val selectionEnd = flexEditText!!.selectionEnd

                if (selectionEnd != baseString.length) str = str.trim()

                onInputTextChanged(StringBuilder(baseString).insert(selectionEnd, str).toString(), null)
                flexEditText!!.setSelection(selectionEnd + str.length)
            }
        }
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        flexEditText = null
    }
}