package tk.zt64.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.patcher.before
import com.aliucord.wrappers.ChannelWrapper.Companion.id
import com.aliucord.wrappers.ChannelWrapper.Companion.isDM
import com.discord.databinding.WidgetChannelsListItemActionsBinding
import com.discord.stores.StoreStream
import com.discord.utilities.color.ColorCompat
import com.discord.widgets.channels.list.WidgetChannelListModel
import com.discord.widgets.channels.list.WidgetChannelsList
import com.discord.widgets.channels.list.WidgetChannelsListAdapter
import com.discord.widgets.channels.list.WidgetChannelsListItemChannelActions
import com.discord.widgets.channels.list.items.ChannelListItemPrivate
import com.google.gson.reflect.TypeToken
import com.lytefast.flexinput.R
import tk.zt64.plugins.pindms.DMGroup
import tk.zt64.plugins.pindms.items.ChannelListItemDMGroup
import tk.zt64.plugins.pindms.items.ItemDMGroup
import tk.zt64.plugins.pindms.items.ItemDivider
import tk.zt64.plugins.pindms.sheets.GroupsSheet
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.ArrayList

@AliucordPlugin
class PinDMs : Plugin() {
    private val groupType = TypeToken.getParameterized(ArrayList::class.java, DMGroup::class.javaObjectType).getType()

    private val getBindingMethod: Method by lazy {
        WidgetChannelsListItemChannelActions::class.java.getDeclaredMethod("getBinding").apply {
            isAccessible = true
        }
    }

    private fun WidgetChannelsListItemChannelActions.getBinding() = getBindingMethod.invoke(this) as WidgetChannelsListItemActionsBinding

    companion object {
        private lateinit var mSettings: SettingsAPI
        lateinit var groups: ArrayList<DMGroup>

        fun saveGroups() = mSettings.setObject("groups", groups)

        fun addGroup(name: String, channelIds: ArrayList<Long> = ArrayList()) = groups.add(DMGroup(name, channelIds)).also { saveGroups() }
        fun removeGroup(group: DMGroup) = groups.remove(group).also { saveGroups() }
        fun getGroup(name: String) = groups.find { dmGroup -> dmGroup.name == name }
    }

    @SuppressLint("SetTextI18n")
    override fun start(context: Context) {
        val categoryLayoutId = Utils.getResId("widget_channels_list_item_category", "layout")
        val headerLayoutId = Utils.getResId("widget_channels_list_item_header", "layout")

        mSettings = settings
        groups = settings.getObject("groups", ArrayList(), groupType)

        patcher.after<WidgetChannelsListItemChannelActions>("configureUI", WidgetChannelsListItemChannelActions.Model::class.java) {
            val model = it.args[0] as WidgetChannelsListItemChannelActions.Model

            if (!model.channel.isDM()) return@after

            val root = getBinding().root as NestedScrollView
            val ctx = root.context

            (root.getChildAt(0) as LinearLayout).addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
                groups.find { group -> group.channelIds.contains(model.channel.id) }?.let { group ->
                    text = "Remove from group"
                    setOnClickListener {
                        dismiss()

                        group.channelIds.remove(model.channel.id)

                        StoreStream.`access$getDispatcher$p`(StoreStream.getPresences().stream).schedule {
                            StoreStream.getChannels().markChanged()
                        }

                        saveGroups()
                    }
                    setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(ctx, R.e.ic_remove_circle_outline_red_24dp)!!.mutate().apply {
                        setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal))
                    }, null, null, null)
                } ?: run {
                    text = "Set Group"
                    setOnClickListener {
                        dismiss()
                        GroupsSheet(model.channel.id).show(parentFragmentManager, "Groups")
                    }
                    setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(ctx, R.e.ic_group_add_white_24dp)!!.mutate().apply {
                        setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal))
                    }, null, null, null)
                }
            })
        }

        patcher.before<WidgetChannelsList>("configureUI", WidgetChannelListModel::class.java) {
            val model = it.args[0] as WidgetChannelListModel

            if (model.selectedGuild != null) return@before

            settings.getObject("groups", ArrayList<DMGroup>(), groupType).reversed().forEach { group ->
                val items = model.items.filterIsInstance<ChannelListItemPrivate>().filter { item ->
                    group.channelIds.contains(item.channel.id)
                }

                model.items.removeAll(items)

                if (group.collapsed) return@forEach run { model.items.add(0, ChannelListItemDMGroup(group)) }

                model.items.addAll(0, listOf(ChannelListItemDMGroup(group)) + items)
            }
        }

        patcher.after<WidgetChannelsListAdapter>("onCreateViewHolder", ViewGroup::class.java, Int::class.java) {
            it.result = when (it.args[1]) {
                400 -> ItemDMGroup(categoryLayoutId, this)
                401 -> ItemDivider(headerLayoutId, this)
                else -> it.result
            }
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}