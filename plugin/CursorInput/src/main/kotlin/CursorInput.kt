import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.*
import com.discord.widgets.chat.input.AppFlexInputViewModel
import com.discord.widgets.chat.input.autocomplete.InputAutocomplete
import com.discord.widgets.chat.input.autocomplete.`InputAutocomplete$1`

@AliucordPlugin
class CursorInput : Plugin() {
    override fun start(context: Context) {
        patcher.instead<AppFlexInputViewModel>(
            "onInputTextAppended",
            String::class.java
        ) { (_, str: String) ->
            val listener = singleAttachmentSelectedListener as? `InputAutocomplete$1`
            val flexEditText = listener?.`this$0`?.let { InputAutocomplete.`access$getEditText$p`(it) }
            val baseString = requireViewState().a

            if (flexEditText == null) {
                onInputTextChanged(baseString + str, null)
            } else {
                val selectionEnd = flexEditText.selectionEnd

                val trimmed = if (selectionEnd != baseString.length) str.trim() else str

                onInputTextChanged(
                    StringBuilder(baseString).insert(selectionEnd, trimmed).toString(),
                    null
                )
                flexEditText.setSelection(selectionEnd + trimmed.length)
            }
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}