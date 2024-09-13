package dev.zt64.aliucord.plugins.favoritechannels

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.patcher.before
import com.aliucord.patcher.component1
import com.aliucord.patcher.component2
import com.aliucord.patcher.component3
import com.aliucord.wrappers.ChannelWrapper.Companion.guildId
import com.aliucord.wrappers.ChannelWrapper.Companion.id
import com.aliucord.wrappers.ChannelWrapper.Companion.isDM
import com.discord.api.channel.Channel
import com.discord.databinding.WidgetChannelsListItemActionsBinding
import com.discord.utilities.color.ColorCompat
import com.discord.widgets.channels.list.WidgetChannelListModel
import com.discord.widgets.channels.list.WidgetChannelsList
import com.discord.widgets.channels.list.WidgetChannelsListAdapter
import com.discord.widgets.channels.list.WidgetChannelsListItemChannelActions
import com.discord.widgets.channels.list.items.ChannelListItemTextChannel
import com.google.gson.reflect.TypeToken
import com.lytefast.flexinput.R
import dev.zt64.aliucord.plugins.favoritechannels.items.ChannelListItemDivider
import dev.zt64.aliucord.plugins.favoritechannels.items.ChannelListItemFavoriteCategory
import dev.zt64.aliucord.plugins.favoritechannels.items.ItemDivider
import dev.zt64.aliucord.plugins.favoritechannels.items.ItemFavoriteCategory

@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
@AliucordPlugin(requiresRestart = true)
class FavoriteChannels : Plugin() {
    private val getBindingMethod = WidgetChannelsListItemChannelActions::class.java
        .getDeclaredMethod("getBinding")
        .apply { isAccessible = true }

    private fun WidgetChannelsListItemChannelActions.getBinding() = getBindingMethod(this) as WidgetChannelsListItemActionsBinding

    init {
        settingsTab = SettingsTab(PluginSettings::class.java, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings)
    }

    companion object {
        private lateinit var mSettings: SettingsAPI

        private val favoritesType by lazy {
            object : TypeToken<Map<Long, List<Long>>>() {}.type
        }

        // Map of guilds to favorite channels
        private val favorites: MutableMap<Long, MutableList<Long>> by lazy {
            mSettings.getObject("favorites", mutableMapOf(), favoritesType)
        }

        fun favoriteChannel(channel: Channel) {
            favorites
                .getOrPut(channel.guildId) { mutableListOf() }
                .add(channel.id)

            saveSettings()
            Util.updateChannels()
        }

        fun unfavoriteChannel(channel: Channel) {
            favorites[channel.guildId]?.remove(channel.id)

            saveSettings()
            Util.updateChannels()
        }

        private fun saveSettings() {
            mSettings.setObject("favorites", favorites)
        }
    }

    override fun start(context: Context) {
        mSettings = settings

        patcher.after<WidgetChannelsListItemChannelActions>(
            "configureUI",
            WidgetChannelsListItemChannelActions.Model::class.java
        ) { (_, model: WidgetChannelsListItemChannelActions.Model) ->
            // Only run in servers
            if (model.channel.isDM()) return@after

            val root = getBinding().root as NestedScrollView
            val ctx = root.context

            (root.getChildAt(0) as LinearLayout).addView(
                TextView(
                    ctx,
                    null,
                    0,
                    R.i.UiKit_Settings_Item_Icon
                ).apply {
                    if (model.channel.id in favorites[model.guild.id].orEmpty()) {
                        text = "Unfavorite"
                        setOnClickListener {
                            dismiss()

                            unfavoriteChannel(model.channel)
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
                        text = "Favorite"
                        setOnClickListener {
                            dismiss()

                            favoriteChannel(model.channel)
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

        @OptIn(ExperimentalStdlibApi::class)
        patcher.before<WidgetChannelsList>(
            "configureUI",
            WidgetChannelListModel::class.java
        ) { (_, model: WidgetChannelListModel) ->
            // Only run in servers and when there are favorites
            if (!model.isGuildSelected) return@before

            val favoriteChannels = favorites[model.selectedGuild.id]

            if (favoriteChannels.isNullOrEmpty()) return@before

            val channelItems = model.items.filterIsInstance<ChannelListItemTextChannel>()
            val items = buildList(channelItems.size) {
                add(ChannelListItemFavoriteCategory)

                val channels = channelItems.filter { (channel) -> channel.id in favoriteChannels }

                model.items.removeAll(channels)

                addAll(channels)
            } + ChannelListItemDivider

            model.items.addAll(0, items)
        }

        patcher.after<WidgetChannelsListAdapter>(
            "onCreateViewHolder",
            ViewGroup::class.java,
            Int::class.java
        ) { (param, _: Any, type: Int) ->
            param.result = when (type) {
                ChannelListItemFavoriteCategory.type -> ItemFavoriteCategory(this)
                ChannelListItemDivider.type -> ItemDivider(this)
                else -> param.result
            }
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}