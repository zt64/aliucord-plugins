import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import channelinvites.InvitesPage
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.discord.utilities.color.ColorCompat
import com.discord.widgets.channels.settings.WidgetTextChannelSettings
import com.lytefast.flexinput.R

@AliucordPlugin
class ChannelInvites : Plugin() {
    @SuppressLint("SetTextI18n")
    override fun start(c: Context) {
        val invitesLayoutId = View.generateViewId()
        val scrollViewId = Utils.getResId("scroll_view", "id")

        patcher.after<WidgetTextChannelSettings>("configureUI", WidgetTextChannelSettings.Model::class.java) {
            val root = WidgetTextChannelSettings.`access$getBinding$p`(this).root
            val content = root.findViewById<NestedScrollView>(scrollViewId).getChildAt(0) as LinearLayout

            if (content.findViewById<LinearLayout>(invitesLayoutId) != null) return@after

            val model = it.args[0] as WidgetTextChannelSettings.Model

            content.addView(LinearLayout(context, null, 0, R.i.UiKit_ViewGroup_LinearLayout).apply {
                id = invitesLayoutId

                addView(TextView(context, null, 0, R.i.UiKit_Settings_Item_Header).apply {
                    text = "Invites"
                })

                addView(TextView(context, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
                    val icon = ContextCompat.getDrawable(context, R.e.ic_guild_invite_24dp)!!.mutate().apply {
                        setTint(ColorCompat.getThemedColor(context, R.b.colorInteractiveNormal))
                    }

                    text = "Invites"

                    setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
                })

                setOnClickListener {
                    Utils.openPageWithProxy(context, InvitesPage(model.channel))
                }
            })
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}