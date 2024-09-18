package dev.zt64.aliucord.plugins.favoritechannels

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.patcher.before
import com.aliucord.patcher.component1
import com.aliucord.patcher.component2
import com.aliucord.patcher.component3
import com.aliucord.wrappers.ChannelWrapper.Companion.id
import com.aliucord.wrappers.ChannelWrapper.Companion.isDM
import com.aliucord.wrappers.ChannelWrapper.Companion.parentId
import com.discord.databinding.WidgetChannelsListItemActionsBinding
import com.discord.restapi.RestAPIParams
import com.discord.stores.StoreStream
import com.discord.stores.StoreUserGuildSettings
import com.discord.utilities.color.ColorCompat
import com.discord.widgets.channels.list.WidgetChannelListModel
import com.discord.widgets.channels.list.WidgetChannelsList
import com.discord.widgets.channels.list.WidgetChannelsListAdapter
import com.discord.widgets.channels.list.WidgetChannelsListItemChannelActions
import com.discord.widgets.channels.list.items.ChannelListItemTextChannel
import com.discord.widgets.channels.list.items.ChannelListItemThread
import com.lytefast.flexinput.R
import dev.zt64.aliucord.plugins.favoritechannels.items.ChannelListItemDivider
import dev.zt64.aliucord.plugins.favoritechannels.items.ChannelListItemFavoriteCategory
import dev.zt64.aliucord.plugins.favoritechannels.items.ItemDivider
import dev.zt64.aliucord.plugins.favoritechannels.items.ItemFavoriteCategory

private const val FLAG_FAVORITE = 1 shl 11

@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
@AliucordPlugin(requiresRestart = true)
class FavoriteChannels : Plugin() {
    init {
        settingsTab = SettingsTab(PluginSettings::class.java, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings)
    }

    override fun start(context: Context) {
        val userGuildSettings = StoreStream.getUserGuildSettings()

        val getBindingMethod = WidgetChannelsListItemChannelActions::class.java
            .getDeclaredMethod("getBinding")
            .apply { isAccessible = true }

        patcher.after<WidgetChannelsListItemChannelActions>(
            "configureUI",
            WidgetChannelsListItemChannelActions.Model::class.java
        ) { (_, model: WidgetChannelsListItemChannelActions.Model) ->
            // Only run in servers
            if (model.channel.isDM()) return@after

            val flags = userGuildSettings.guildSettings[model.guild.id]
                ?.channelOverrides
                ?.find { it.channelId == model.channel.id }
                ?.flags ?: 0

            val root = (getBindingMethod(this) as WidgetChannelsListItemActionsBinding).root as ViewGroup
            val ctx = root.context

            (root.getChildAt(0) as LinearLayout).addView(
                TextView(
                    ctx,
                    null,
                    0,
                    R.i.UiKit_Settings_Item_Icon
                ).apply {
                    if (flags and (1 shl 11) != 0) {
                        text = "Remove from favorites"
                        setOnClickListener {
                            dismiss()

                            StoreUserGuildSettings.`access$updateUserGuildSettings`(
                                StoreStream.getUserGuildSettings(),
                                ctx,
                                model.guild.id,
                                RestAPIParams.UserGuildSettings(
                                    model.channel.id,
                                    RestAPIParams.UserGuildSettings.ChannelOverride(null, flags and (FLAG_FAVORITE).inv())
                                ),
                                StoreUserGuildSettings.SettingsUpdateType.CHANNEL
                            )
                        }
                        setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat
                                .getDrawable(ctx, R.e.ic_remove_circle_outline_red_24dp)!!
                                .mutate()
                                .apply {
                                    setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal))
                                },
                            null,
                            null,
                            null
                        )
                    } else {
                        text = "Add to favorites"
                        setOnClickListener {
                            dismiss()

                            StoreUserGuildSettings.`access$updateUserGuildSettings`(
                                StoreStream.getUserGuildSettings(),
                                ctx,
                                model.guild.id,
                                RestAPIParams.UserGuildSettings(
                                    model.channel.id,
                                    RestAPIParams.UserGuildSettings.ChannelOverride(null, flags or FLAG_FAVORITE)
                                ),
                                StoreUserGuildSettings.SettingsUpdateType.CHANNEL
                            )
                        }
                        setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat
                                .getDrawable(ctx, R.e.ic_group_add_white_24dp)!!
                                .mutate()
                                .apply {
                                    setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal))
                                },
                            null,
                            null,
                            null
                        )
                    }
                }
            )
        }

        patcher.before<WidgetChannelsList>(
            "configureUI",
            WidgetChannelListModel::class.java
        ) { (_, model: WidgetChannelListModel) ->
            // Only run in servers and when there are favorites
            if (!model.isGuildSelected) return@before

            val favoriteChannels = userGuildSettings
                .guildSettings[model.selectedGuild.id]
                ?.channelOverrides
                ?.mapNotNull { c -> c.channelId.takeIf { c.flags and FLAG_FAVORITE != 0 } }

            if (favoriteChannels.isNullOrEmpty()) return@before

            val channels = model.items.filter { item ->
                when (item) {
                    is ChannelListItemTextChannel -> item.channel.id
                    is ChannelListItemThread -> item.channel.parentId
                    else -> return@filter false
                } in favoriteChannels
            }

            model.items.removeAll(channels)

            model.items.addAll(
                0,
                listOf(
                    ChannelListItemFavoriteCategory,
                    *channels.toTypedArray(),
                    ChannelListItemDivider
                )
            )
        }

        patcher.before<WidgetChannelsListAdapter>(
            "onCreateViewHolder",
            ViewGroup::class.java,
            Int::class.java
        ) { (param, _: Any, type: Int) ->
            param.result = when (type) {
                ChannelListItemFavoriteCategory.type -> ItemFavoriteCategory(this)
                ChannelListItemDivider.type -> ItemDivider(this)
                else -> return@before
            }
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}