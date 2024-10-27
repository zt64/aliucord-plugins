import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
import charcounter.PluginSettings
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.settings.delegate
import com.aliucord.utils.DimenUtils.dp
import com.discord.api.premium.PremiumTier
import com.discord.databinding.WidgetChatOverlayBinding
import com.discord.stores.StoreStream
import com.discord.utilities.color.ColorCompat
import com.discord.widgets.chat.input.AppFlexInputViewModel
import com.discord.widgets.chat.overlay.`WidgetChatOverlay$binding$2`
import com.lytefast.flexinput.R
import kotlin.properties.Delegates

@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
@AliucordPlugin
class CharCounter : Plugin() {
    private var SettingsAPI.reverse: Boolean by settings.delegate(false)
    private var SettingsAPI.threshold: Int by settings.delegate(0)

    init {
        settingsTab = SettingsTab(PluginSettings::class.java, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings)
    }

    override fun start(context: Context) {
        val textSizeDimenId = Utils.getResId("uikit_textsize_small", "dimen")
        val typingOverlayId = Utils.getResId("chat_overlay_typing", "id")
        var counter: TextView? = null

        var normalColor: Int by Delegates.notNull()
        var redColor: Int by Delegates.notNull()

        patcher.after<`WidgetChatOverlay$binding$2`>("invoke", View::class.java) {
            val root = (it.result as WidgetChatOverlayBinding).root as ConstraintLayout

            normalColor = ColorCompat.getThemedColor(root.context, R.b.colorInteractiveNormal)
            redColor = ColorCompat.getThemedColor(root.context, R.b.colorTextDanger)

            counter = TextView(root.context, null, 0, R.i.UiKit_TextView).apply {
                id = View.generateViewId()
                visibility = View.GONE
                gravity = Gravity.CENTER_VERTICAL
                maxLines = 1
                layoutParams = ConstraintLayout.LayoutParams(WRAP_CONTENT, 24.dp).apply {
                    rightToRight = PARENT_ID
                    bottomToBottom = PARENT_ID
                }
                8.dp.let { dp -> setPadding(dp, 0, dp, 0) }
                setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    root.resources.getDimension(textSizeDimenId)
                )
                setBackgroundColor(ColorCompat.getThemedColor(root.context, R.c.primary_dark_600))
                root.addView(this)
            }

            with(
                root.findViewById<RelativeLayout>(typingOverlayId).layoutParams as ConstraintLayout.LayoutParams
            ) {
                startToStart = PARENT_ID
                endToStart = counter!!.id
                width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
            }
        }

        patcher.after<AppFlexInputViewModel>(
            "onInputTextChanged",
            String::class.java,
            Boolean::class.javaObjectType
        ) {
            val chars = (it.args[0] as String).length
            val maxChars = if (StoreStream.getUsers().me.premiumTier == PremiumTier.TIER_2) {
                4000
            } else {
                2000
            }

            counter?.apply {
                visibility = if (chars >= settings.threshold) View.VISIBLE else View.GONE
                text = "${if (settings.reverse) maxChars - chars else chars}/$maxChars"
                setTextColor(if (chars > maxChars) redColor else normalColor)
            }
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}