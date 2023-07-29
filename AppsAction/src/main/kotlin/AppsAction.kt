import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.discord.databinding.WidgetChatListActionsBinding
import com.discord.utilities.color.ColorCompat
import com.discord.widgets.chat.list.actions.`WidgetChatListActions$binding$2`
import com.lytefast.flexinput.R
import kotlin.properties.Delegates

@AliucordPlugin
class AppsAction : Plugin() {
    private var WidgetChatListActionsBinding.apps by Delegates.notNull<TextView>()

    override fun start(ctx: Context) {
        patcher.after<`WidgetChatListActions$binding$2`>("invoke", View::class.java) {
            val binding = it.result as WidgetChatListActionsBinding
            val context = binding.root.context
            val layout = binding.a.getChildAt(0) as ViewGroup

            binding.apps = TextView(context, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
                @Suppress("SetTextI18n")
                text = "Apps"
                visibility = View.VISIBLE

                val drawable = ContextCompat.getDrawable(context, R.e.ic_authed_apps_24dp)!!
                    .mutate()
                    .apply {
                        setTint(ColorCompat.getThemedColor(context, R.b.colorInteractiveNormal))
                    }

                setCompoundDrawables(drawable, null, null, null)
            }
            layout.addView(binding.apps)
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}