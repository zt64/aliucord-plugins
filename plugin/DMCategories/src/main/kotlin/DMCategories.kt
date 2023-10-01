
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
import com.aliucord.patcher.*
import com.aliucord.settings.delegate
import com.aliucord.wrappers.ChannelWrapper.Companion.id
import com.aliucord.wrappers.ChannelWrapper.Companion.isDM
import com.discord.databinding.WidgetChannelsListItemActionsBinding
import com.discord.utilities.color.ColorCompat
import com.discord.widgets.channels.list.*
import com.discord.widgets.channels.list.items.ChannelListItemPrivate
import com.google.gson.reflect.TypeToken
import com.lytefast.flexinput.R
import dmcategories.DMCategory
import dmcategories.PluginSettings
import dmcategories.Util
import dmcategories.items.*
import dmcategories.sheets.CategoriesSheet

private val categoryType = TypeToken.getParameterized(ArrayList::class.java, DMCategory::class.javaObjectType).getType()

@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
@AliucordPlugin
class DMCategories : Plugin() {
    private val getBindingMethod = WidgetChannelsListItemChannelActions::class.java
        .getDeclaredMethod("getBinding")
        .apply { isAccessible = true }

    private fun WidgetChannelsListItemChannelActions.getBinding() =
        getBindingMethod(this) as WidgetChannelsListItemActionsBinding

    private val SettingsAPI.showSelected: Boolean by settings.delegate(true)
    private val SettingsAPI.showUnread: Boolean by settings.delegate(false)

    init {
        settingsTab = SettingsTab(PluginSettings::class.java, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings)
    }

    companion object {
        private lateinit var mSettings: SettingsAPI
        val categories: MutableList<DMCategory> by lazy { mSettings.getObject("categories", mutableListOf(), categoryType) }

        fun saveCategories() = mSettings.setObject("categories", categories)
        fun addCategory(name: String, channelIds: ArrayList<Long> = ArrayList()) {
            categories.add(DMCategory(Util.getCurrentId(), name, channelIds)).also { if (it) saveCategories() }
        }

        fun removeCategory(category: DMCategory) = categories.remove(category).also { if (it) saveCategories() }
        fun getCategory(name: String) = categories.find { dmCategory -> dmCategory.name == name }
    }

    @Suppress("SetTextI18n")
    override fun start(context: Context) {
        val categoryLayoutId = Utils.getResId("widget_channels_list_item_category", "layout")
        val stageEventsSeparatorId =
            Utils.getResId("widget_channels_list_item_stage_events_separator", "layout")

        mSettings = settings

        patcher.after<WidgetChannelsListItemChannelActions>(
            "configureUI",
            WidgetChannelsListItemChannelActions.Model::class.java
        ) { (_, model: WidgetChannelsListItemChannelActions.Model) ->
            if (!model.channel.isDM()) return@after

            val root = getBinding().root as NestedScrollView
            val ctx = root.context

            (root.getChildAt(0) as LinearLayout).addView(
                TextView(
                    ctx,
                    null,
                    0,
                    R.i.UiKit_Settings_Item_Icon
                ).apply {
                    categories.find { category -> category.channelIds.contains(model.channel.id) }
                        ?.let { category ->
                            text = "Remove from category"
                            setOnClickListener {
                                dismiss()

                                category.channelIds.remove(model.channel.id)

                                Util.updateChannels()

                                saveCategories()
                            }
                            setCompoundDrawablesWithIntrinsicBounds(
                                ContextCompat.getDrawable(
                                    ctx,
                                    R.e.ic_remove_circle_outline_red_24dp
                                )!!
                                    .mutate()
                            .apply {
                                setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal))
                            }, null, null, null
                    )
                } ?: run {
                    text = "Set Category"
                    setOnClickListener {
                        dismiss()
                        CategoriesSheet(model.channel.id).show(parentFragmentManager, "Categories")
                    }
                        setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(ctx, R.e.ic_group_add_white_24dp)!!
                                .mutate()
                                .apply {
                                    setTint(
                                        ColorCompat.getThemedColor(
                                            ctx,
                                            R.b.colorInteractiveNormal
                                        )
                                    )
                                }, null, null, null
                        )
                    }
                })
        }

        @OptIn(ExperimentalStdlibApi::class)
        patcher.before<WidgetChannelsList>(
            "configureUI",
            WidgetChannelListModel::class.java
        ) { (_, model: WidgetChannelListModel) ->
            if (model.selectedGuild != null) return@before

            // I hate this but it works
            if (categories.none { (userId) -> userId == Util.getCurrentId() }) return@before

            val privateChannels = model.items.filterIsInstance<ChannelListItemPrivate>()
            val items = buildList(1000) {
                categories.forEach { category ->
                    add(ChannelListItemDMCategory(category))

                    val channels = privateChannels.filter { channel ->
                        category.channelIds.contains(channel.channel.id)
                    }

                    model.items.removeAll(channels)

                    addAll(
                        elements = if (category.collapsed) {
                            if (!settings.showSelected && !settings.showUnread) return@forEach

                            channels.filter { channel ->
                                settings.showSelected && channel.selected || settings.showUnread && !channel.muted && channel.mentionCount > 0
                            }
                        } else channels
                    )
                }
            } + ChannelListItemDivider

            model.items.addAll(0, items)
        }

        patcher.after<WidgetChannelsListAdapter>(
            "onCreateViewHolder",
            ViewGroup::class.java,
            Int::class.java
        ) { (param, type: Int) ->
            param.result = when (type) {
                ChannelListItemDMCategory.TYPE -> ItemDMCategory(categoryLayoutId, this)
                ChannelListItemDivider.TYPE -> ItemDivider(stageEventsSeparatorId, this)
                else -> param.result
            }
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}