package tk.zt64.plugins

import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.PineInsteadFn
import com.aliucord.patcher.PinePatchFn
import com.discord.widgets.chat.input.AppFlexInputViewModel
import com.lytefast.flexinput.R
import com.lytefast.flexinput.fragment.`FlexInputFragment$c`
import com.lytefast.flexinput.widget.FlexEditText

@AliucordPlugin
class CursorInput: Plugin() {
    private var flexEditText: FlexEditText? = null

    override fun start(context: Context) {
        patcher.patch(`FlexInputFragment$c`::class.java.getDeclaredMethod("invoke", Object::class.java), PinePatchFn {
            flexEditText = (it.result as c.b.a.e.a).root.findViewById(R.e.text_input)
        })

        patcher.patch(AppFlexInputViewModel::class.java.getDeclaredMethod("onInputTextAppended", String::class.java), PineInsteadFn {
            with(it.thisObject as AppFlexInputViewModel) {
                val baseString = requireViewState().a
                var str = it.args[0] as String

                if (flexEditText != null) {
                    val selectionEnd = flexEditText!!.selectionEnd

                    if (selectionEnd != baseString.length) str = str.trim()

                    onInputTextChanged(StringBuilder(baseString).insert(selectionEnd, str).toString(), null)
                    flexEditText!!.setSelection(selectionEnd + str.length)
                } else onInputTextChanged(baseString + str, null)
            }
        })
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        flexEditText = null
    }
}