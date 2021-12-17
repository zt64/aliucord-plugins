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
import com.discord.utilities.color.ColorCompat
import com.discord.widgets.channels.list.WidgetChannelListModel
import com.discord.widgets.channels.list.WidgetChannelsList
import com.discord.widgets.channels.list.WidgetChannelsListAdapter
import com.discord.widgets.channels.list.WidgetChannelsListItemChannelActions
import com.discord.widgets.channels.list.items.ChannelListItemPrivate
import com.google.gson.reflect.TypeToken
import com.lytefast.flexinput.R
import tk.zt64.plugins.dmcategories.DMCategory
import tk.zt64.plugins.dmcategories.Util
import tk.zt64.plugins.dmcategories.items.ChannelListItemDMCategory
import tk.zt64.plugins.dmcategories.items.ChannelListItemDivider
import tk.zt64.plugins.dmcategories.items.ItemDMCategory
import tk.zt64.plugins.dmcategories.items.ItemDivider
import tk.zt64.plugins.dmcategories.sheets.CategoriesSheet
import java.util.*
import kotlin.collections.ArrayList

@AliucordPlugin
class DMCategories : Plugin() {
    private val categoryType = TypeToken.getParameterized(ArrayList::class.java, DMCategory::class.javaObjectType).getType()

    private val getBindingMethod = WidgetChannelsListItemChannelActions::class.java.getDeclaredMethod("getBinding").apply { isAccessible = true }
    private fun WidgetChannelsListItemChannelActions.getBinding() = getBindingMethod(this) as WidgetChannelsListItemActionsBinding

    companion object {
        private lateinit var mSettings: SettingsAPI
        lateinit var categories: ArrayList<DMCategory>

        fun saveCategories() = mSettings.setObject("categories", categories)
        fun addCategory(name: String, channelIds: ArrayList<Long> = ArrayList()) = categories.add(DMCategory(Util.getCurrentId(), name, channelIds)).also { if (it) saveCategories() }
        fun removeCategory(category: DMCategory) = categories.remove(category).also { if (it) saveCategories() }
        fun getCategory(name: String) = categories.find { dmCategory -> dmCategory.name == name }
    }

    @SuppressLint("SetTextI18n")
    override fun start(context: Context) {
        val categoryLayoutId = Utils.getResId("widget_channels_list_item_category", "layout")
        val stageEventsSeparatorId = Utils.getResId("widget_channels_list_item_stage_events_separator", "layout")

        
        mSettings = settings
        categories = settings.getObject("categories", ArrayList(), categoryType)

        patcher.after<WidgetChannelsListItemChannelActions>("configureUI", WidgetChannelsListItemChannelActions.Model::class.java) {
            val model = it.args[0] as WidgetChannelsListItemChannelActions.Model

            if (!model.channel.isDM()) return@after

            val root = getBinding().root as NestedScrollView
            val ctx = root.context

            (root.getChildAt(0) as LinearLayout).addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
                categories.find { category -> category.channelIds.contains(model.channel.id) }?.let { category ->
                    text = "Remove from category"
                    setOnClickListener {
                        dismiss()

                        category.channelIds.remove(model.channel.id)

                        Util.updateChannels()

                        saveCategories()
                    }
                    setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(ctx, R.e.ic_remove_circle_outline_red_24dp)!!.mutate().apply {
                        setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal))
                    }, null, null, null)
                } ?: run {
                    text = "Set Category"
                    setOnClickListener {
                        dismiss()
                        CategoriesSheet(model.channel.id).show(parentFragmentManager, "Categories")
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

            settings.getObject("categories", ArrayList<DMCategory>(), categoryType).filter { category -> category.userId == Util.getCurrentId() }
                    .reversed().forEach { category ->
                        val privateChannels = model.items.filterIsInstance<ChannelListItemPrivate>().filter { item ->
                            category.channelIds.contains(item.channel.id)
                        }

                        model.items.removeAll(privateChannels)

                        if (category.collapsed) return@forEach run { model.items.addAll(0, listOf(ChannelListItemDMCategory(category)) + ChannelListItemDivider) }

                        model.items.addAll(0, listOf(ChannelListItemDMCategory(category)) + privateChannels + ChannelListItemDivider)
                    }
        }

        patcher.after<WidgetChannelsListAdapter>("onCreateViewHolder", ViewGroup::class.java, Int::class.java) {
            it.result = when (it.args[1]) {
                400 -> ItemDMCategory(categoryLayoutId, this)
                401 -> ItemDivider(stageEventsSeparatorId, this)
                else -> it.result
            }
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}