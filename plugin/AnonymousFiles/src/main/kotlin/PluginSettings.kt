import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.utils.DimenUtils
import com.aliucord.views.TextInput
import com.aliucord.widgets.BottomSheet
import com.discord.utilities.color.ColorCompat
import com.discord.views.CheckedSetting

class PluginSettings(private val settings: SettingsAPI) : BottomSheet() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()
        setPadding(DimenUtils.dpToPx(16))

        val restartNotice = TextView(context).apply {
            text = "Note: Changes require restarting Aliucord to take effect."
            textSize = 12f
            val noticeAttr = Utils.getResId("colorTextMuted", "attr")
            if (noticeAttr != 0) {
                setTextColor(ColorCompat.getThemedColor(context, noticeAttr))
            }
            setPadding(0, 0, 0, DimenUtils.dpToPx(12))
        }
        addView(restartNotice)

        val promptToggle = Utils.createCheckedSetting(
            context,
            CheckedSetting.ViewType.CHECK,
            "Prompt on Send",
            "Show a selection bottom sheet when sending attachments to choose which files to anonymize."
        ).apply {
            isChecked = settings.getBool(KEY_PROMPT_ON_SEND, DEFAULT_PROMPT_ON_SEND)
        }
        addView(promptToggle)

        val mediaOnlyToggle = Utils.createCheckedSetting(
            context,
            CheckedSetting.ViewType.CHECK,
            "Media Files Only",
            "Only anonymize media files (images, videos, audio), ignoring documents and archives."
        ).apply {
            isChecked = settings.getBool(KEY_MEDIA_ONLY, DEFAULT_MEDIA_ONLY)
            setOnCheckedListener {
                settings.setBool(KEY_MEDIA_ONLY, it)
            }
        }
        addView(mediaOnlyToggle)

        val extInput = TextInput(
            context,
            "Excluded Extensions (e.g. pdf, zip, apk)",
            settings.getString(KEY_EXCLUDED_EXTS, DEFAULT_EXCLUDED_EXTS),
            object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    settings.setString(KEY_EXCLUDED_EXTS, s.toString())
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }
        )
        addView(extInput)

        fun updateFilterStates(isPrompt: Boolean) {
            val enabled = !isPrompt
            val alpha = if (enabled) 1.0f else 0.4f

            mediaOnlyToggle.setViewAndChildrenEnabled(enabled)
            mediaOnlyToggle.alpha = alpha

            extInput.setViewAndChildrenEnabled(enabled)
            extInput.editText.isEnabled = enabled
            extInput.alpha = alpha
        }

        promptToggle.setOnCheckedListener {
            settings.setBool(KEY_PROMPT_ON_SEND, it)
            updateFilterStates(it)
        }

        updateFilterStates(promptToggle.isChecked)

        val lengthInput = TextInput(
            context,
            "Random Name Length (default: 8)",
            settings.getInt(KEY_NAME_LENGTH, DEFAULT_NAME_LENGTH).toString(),
            object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    val len = s.toString().toIntOrNull()
                    if (len != null && len in 1..64) {
                        settings.setInt(KEY_NAME_LENGTH, len)
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }
        ).apply {
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }
        addView(lengthInput)
    }

    private fun View.setViewAndChildrenEnabled(enabled: Boolean) {
        isEnabled = enabled
        if (this is ViewGroup) {
            for (i in 0 until childCount) {
                getChildAt(i).setViewAndChildrenEnabled(enabled)
            }
        }
    }

    companion object {
        const val KEY_PROMPT_ON_SEND = "prompt_on_send"
        const val DEFAULT_PROMPT_ON_SEND = false

        const val KEY_MEDIA_ONLY = "media_only"
        const val DEFAULT_MEDIA_ONLY = false

        const val KEY_EXCLUDED_EXTS = "excluded_extensions"
        const val DEFAULT_EXCLUDED_EXTS = ""

        const val KEY_NAME_LENGTH = "name_length"
        const val DEFAULT_NAME_LENGTH = 8
    }
}
