import android.content.Context
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_PARENT
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT
import androidx.constraintlayout.widget.ConstraintSet
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.utils.DimenUtils.dp
import com.aliucord.wrappers.GuildWrapper.Companion.banner
import com.aliucord.wrappers.GuildWrapper.Companion.description
import com.aliucord.wrappers.GuildWrapper.Companion.id
import com.aliucord.wrappers.GuildWrapper.Companion.name
import com.discord.models.guild.Guild
import com.discord.utilities.icon.IconUtils
import com.discord.utilities.images.MGImages
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemInvite
import com.discord.widgets.guilds.profile.WidgetGuildProfileSheet
import com.facebook.drawee.view.SimpleDraweeView
import com.lytefast.flexinput.R

@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
@AliucordPlugin
class InviteDetails : Plugin() {
    override fun start(context: Context) {
        val viewIds = object {
            val infoButton = View.generateViewId()
            val banner = View.generateViewId()
            val description = View.generateViewId()

            val barrierHeader = Utils.getResId("barrier_header", "id")
            val itemInviteHeader = Utils.getResId("item_invite_header", "id")
            val itemInviteImage = Utils.getResId("item_invite_image", "id")
            val itemInviteName = Utils.getResId("item_invite_name", "id")
            val itemInviteMemberContainer = Utils.getResId("item_invite_member_container", "id")
            val barrierButton = Utils.getResId("barrier_button", "id")
        }

        patcher.after<WidgetChatListAdapterItemInvite>(
            "configureResolvedUI",
            WidgetChatListAdapterItemInvite.Model.Resolved::class.java
        ) {
            val (invite) = it.args[0] as WidgetChatListAdapterItemInvite.Model.Resolved
            val guild = invite.guild
            val layout = itemView as ConstraintLayout
            val ctx = layout.context

            ConstraintSet().run {
                clone(layout)

                // Info button
                (
                    layout.findViewById(viewIds.infoButton) ?: ImageButton(
                        ctx,
                        null,
                        0,
                        R.i.UiKit_ImageButton
                    ).apply {
                        id = viewIds.infoButton
                        layoutParams = LayoutParams(0.dp, MATCH_PARENT).apply {
                            topToTop = viewIds.itemInviteName
                            bottomToBottom = viewIds.itemInviteMemberContainer
                            endToEnd = ConstraintSet.PARENT_ID
                            marginEnd = 16.dp
                        }

                        setPadding(0.dp, 0.dp, 0.dp, 0.dp)

                        setImageResource(R.e.ic_info_outline_white_24dp)

                        connect(
                            viewIds.itemInviteName,
                            ConstraintSet.END,
                            id,
                            ConstraintSet.START
                        )
                        connect(
                            viewIds.itemInviteMemberContainer,
                            ConstraintSet.END,
                            id,
                            ConstraintSet.START
                        )

                        layout.addView(this)
                    }
                ).run {
                    setOnClickListener {
                        WidgetGuildProfileSheet.show(
                            adapter.fragmentManager,
                            false,
                            guild.id,
                            0,
                            false
                        )
                    }
                }

                // Banner
                (
                    layout.findViewById(viewIds.banner) ?: SimpleDraweeView(ctx).apply {
                        id = viewIds.banner
                        adjustViewBounds = true
                        layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                            height = 150.dp

                            topToBottom = viewIds.itemInviteHeader
                            bottomToTop = viewIds.barrierHeader

                            setMargins(16.dp, 0, 16.dp, 0)
                            setPadding(0, 0, 0, 8.dp)
                        }

                        connect(
                            viewIds.itemInviteHeader,
                            ConstraintSet.BOTTOM,
                            viewIds.banner,
                            ConstraintSet.TOP
                        )

                        layout.addView(this)
                    }
                ).run {
                    visibility = if (guild.banner != null) {
                        val banner = IconUtils.INSTANCE.getBannerForGuild(
                            Guild(guild),
                            layout.width,
                            false
                        )

                        setOnClickListener {
                            Utils.openMediaViewer(banner, guild.name)
                        }

                        MGImages.setImage(this, banner)
                        MGImages.setRoundingParams(this, 20f, false, null, null, 0f)

                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                }

                // Description
                (
                    layout.findViewById(viewIds.description) ?: TextView(
                        ctx,
                        null,
                        0,
                        R.i.UiKit_Chat_Embed_Subtext
                    ).apply {
                        id = viewIds.description
                        layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                            topToBottom = viewIds.itemInviteMemberContainer
                            bottomToTop = viewIds.barrierButton

                            setMargins(16.dp, 8.dp, 16.dp, 0)
                        }

                        connect(
                            viewIds.itemInviteMemberContainer,
                            ConstraintSet.BOTTOM,
                            viewIds.description,
                            ConstraintSet.TOP
                        )
                        connect(
                            viewIds.itemInviteImage,
                            ConstraintSet.BOTTOM,
                            viewIds.description,
                            ConstraintSet.TOP
                        )

                        layout.addView(this)
                    }
                ).run {
                    visibility = guild.description?.let { description ->
                        text = description

                        View.VISIBLE
                    } ?: View.GONE
                }

                applyTo(layout)
            }
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}