import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.aliucord.Utils
import com.aliucord.utils.DimenUtils
import com.aliucord.views.Button
import com.aliucord.views.ToolbarButton
import com.aliucord.widgets.BottomSheet
import com.discord.utilities.color.ColorCompat
import com.discord.views.CheckedSetting
import com.google.android.material.button.MaterialButton
import com.lytefast.flexinput.model.Attachment

/**
 * BottomSheet presented at send time to select which attachments should be anonymized.
 */
class SendPromptBottomSheet(
    private val attachments: List<Attachment<*>>,
    private val onConfirm: (selected: List<Attachment<*>>) -> Unit,
    private val onCancel: () -> Unit
) : BottomSheet() {

    private val checkedSettings = ArrayList<CheckedSetting>()
    private var isConfirmed = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()
        setPadding(DimenUtils.dpToPx(16))

        val headerRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            val headerText = TextView(context).apply {
                text = "Anonymize Attachments"
                textSize = 18f
                typeface = Typeface.DEFAULT_BOLD
                val headerAttr = Utils.getResId("colorHeaderPrimary", "attr")
                if (headerAttr != 0) {
                    setTextColor(ColorCompat.getThemedColor(context, headerAttr))
                }
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            }
            addView(headerText)

            val actionsLayout = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                addView(createActionButton(context, "Select All", "ic_check_white_24dp") {
                    checkedSettings.forEach { it.isChecked = true }
                })
                addView(createActionButton(context, "Clear", "ic_close_white_24dp") {
                    checkedSettings.forEach { it.isChecked = false }
                })
                addView(createActionButton(context, "Invert", "ic_transfer_red_24dp", "ic_swap_vertical_circle_white_a60_24dp") {
                    checkedSettings.forEach { it.isChecked = !it.isChecked }
                })
            }
            addView(actionsLayout)
        }
        addView(headerRow)

        val subtext = TextView(context).apply {
            text = "Select which files to randomize before sending."
            textSize = 13f
            val subtextAttr = Utils.getResId("colorHeaderSecondary", "attr")
            if (subtextAttr != 0) {
                setTextColor(ColorCompat.getThemedColor(context, subtextAttr))
            }
            setPadding(0, DimenUtils.dpToPx(4), 0, DimenUtils.dpToPx(12))
        }
        addView(subtext)

        attachments.forEachIndexed { index, attachment ->
            val fileName = attachment.displayName ?: "File ${index + 1}"
            val setting = Utils.createCheckedSetting(
                context,
                CheckedSetting.ViewType.CHECK,
                fileName,
                "Attachment #${index + 1}"
            ).apply {
                isChecked = false
            }
            checkedSettings.add(setting)
            addView(setting)
        }

        val sendButton = Button(context).apply {
            text = "Send"
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = DimenUtils.dpToPx(16)
            }
            layoutParams = params
            setOnClickListener {
                isConfirmed = true
                val selected = attachments.filterIndexed { index, _ ->
                    checkedSettings.getOrNull(index)?.isChecked == true
                }
                dismiss()
                onConfirm(selected)
            }
        }
        addView(sendButton)

        val cancelStyle = Utils.getResId("UiKit_Material_Button_Transparent", "style")
        val cancelContext = if (cancelStyle != 0) ContextThemeWrapper(context, cancelStyle) else context
        val cancelButton = MaterialButton(cancelContext, null, 0).apply {
            text = "Cancel"
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = DimenUtils.dpToPx(8)
            }
            layoutParams = params
            setOnClickListener {
                dismiss()
            }
        }
        addView(cancelButton)
    }

    private fun createActionButton(
        context: Context,
        label: String,
        iconName: String,
        fallbackIconName: String? = null,
        onClick: () -> Unit
    ): ToolbarButton {
        return ToolbarButton(context).apply {
            var iconRes = Utils.getResId(iconName, "drawable")
            if (iconRes == 0 && fallbackIconName != null) {
                iconRes = Utils.getResId(fallbackIconName, "drawable")
            }
            if (iconRes != 0) {
                setImageDrawable(ContextCompat.getDrawable(context, iconRes))
            }

            val size = DimenUtils.dpToPx(40)
            val margin = DimenUtils.dpToPx(4)
            layoutParams = LinearLayout.LayoutParams(size, size).apply {
                marginStart = margin
            }

            contentDescription = label

            setOnClickListener { onClick() }
            setOnLongClickListener {
                Utils.showToast(label)
                true
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (!isConfirmed) {
            onCancel()
        }
    }
}
