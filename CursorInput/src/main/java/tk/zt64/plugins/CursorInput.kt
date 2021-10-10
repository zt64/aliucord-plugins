package tk.zt64.plugins

import android.content.Context
import com.aliucord.Logger
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.lytefast.flexinput.widget.FlexEditText

@AliucordPlugin
class CursorInput: Plugin() {
    private var flexEditText: FlexEditText? = null

    override fun start(context: Context) {
        return Logger(this.getName()).warn("CursorInput is temporarily disabled due to issues with patching")

//        patcher.patch(`FlexInputFragment$c`::class.java.getDeclaredMethod("invoke", Object::class.java), Hook {
//            flexEditText = (it.result as c.b.a.e.a).root.findViewById(R.e.text_input)
//        })
//
//        patcher.patch(AppFlexInputViewModel::class.java.getDeclaredMethod("onInputTextAppended", String::class.java), Hook {
//            with(it.thisObject as AppFlexInputViewModel) {
//                val baseString = requireViewState().a
//                var str = it.args[0] as String
//
//                if (flexEditText != null) {
//                    val selectionEnd = flexEditText!!.selectionEnd
//
//                    if (selectionEnd != baseString.length) str = str.trim()
//
//                    onInputTextChanged(StringBuilder(baseString).insert(selectionEnd, str).toString(), null)
//                    flexEditText!!.setSelection(selectionEnd + str.length)
//                } else {
//                    onInputTextChanged(baseString + str, null)
//                }
//            }
//        })
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        flexEditText = null
    }
}