package tk.zt64.plugins

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.PineInsteadFn
import com.aliucord.patcher.PinePatchFn
import com.discord.widgets.chat.input.AppFlexInputViewModel
import com.lytefast.flexinput.R
import com.lytefast.flexinput.fragment.FlexInputFragment
import com.lytefast.flexinput.widget.FlexEditText

@AliucordPlugin
class CursorInput: Plugin() {
    override fun start(context: Context) {
        var flexEditText: FlexEditText? = null

        patcher.patch(FlexInputFragment::class.java.getDeclaredMethod("onCreateView", LayoutInflater::class.java, ViewGroup::class.java, Bundle::class.java), PinePatchFn {
            flexEditText = (it.result as View).findViewById(R.e.text_input)
        })

        patcher.patch(AppFlexInputViewModel::class.java.getDeclaredMethod("onInputTextAppended", String::class.java), PineInsteadFn {
            val str = (it.args[0] as String).trim()

            with(it.thisObject as AppFlexInputViewModel) {
                if (flexEditText != null) {
                    val selectionEnd = flexEditText!!.selectionEnd
                    val sb = StringBuilder(requireViewState().a).insert(selectionEnd, str)

                    onInputTextChanged(sb.toString(), null)
                    flexEditText!!.setSelection(selectionEnd + str.length)
                } else onInputTextChanged(requireViewState().a + str, null)
            }
        })
    }

    override fun stop(context: Context?) = patcher.unpatchAll()
}