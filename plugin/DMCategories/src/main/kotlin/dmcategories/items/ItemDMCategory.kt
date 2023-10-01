package dmcategories.items

import DMCategories
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.aliucord.Utils
import com.discord.widgets.channels.list.WidgetChannelsListAdapter
import com.discord.widgets.channels.list.items.ChannelListItem
import com.lytefast.flexinput.R
import dmcategories.DMCategory
import dmcategories.Util
import dmcategories.sheets.CategorySheet

class ChannelListItemDMCategory(val category: DMCategory) : ChannelListItem {
    override fun getKey(): String = ""
    override fun getType(): Int = TYPE
    operator fun component1() = category

    companion object {
        const val TYPE = 400
    }
}

@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
class ItemDMCategory(
    @LayoutRes i: Int,
    adapter: WidgetChannelsListAdapter
) : WidgetChannelsListAdapter.Item(i, adapter) {
    private val nameView = itemView.findViewById<TextView>(Utils.getResId("channels_item_category_name", "id"))
    private val arrowView = itemView.findViewById<ImageView>(Utils.getResId("channels_item_category_arrow", "id"))
    private val collapsedDrawable = AppCompatResources.getDrawable(adapter.context, R.e.ic_chevron_right_grey_12dp)
    private val openedDrawable = AppCompatResources.getDrawable(adapter.context, R.e.ic_chevron_down_grey_12dp)
    private var isCollapsed = false

    init {
        itemView.findViewById<ImageView>(Utils.getResId("channels_item_category_add", "id")).visibility = View.GONE
    }

    override fun onConfigure(i: Int, channelListItem: ChannelListItem) {
        super.onConfigure(i, channelListItem)

        channelListItem as ChannelListItemDMCategory

        val category = channelListItem.category

        nameView?.text = category.name
        arrowView.setImageDrawable(if (category.collapsed) collapsedDrawable else openedDrawable)

        if (isCollapsed != category.collapsed) {
            isCollapsed = category.collapsed
            arrowView.startAnimation(if (category.collapsed) Util.collapseAnimation else Util.expandAnimation)
        }

        itemView.setOnClickListener {
            category.collapsed = !category.collapsed

            DMCategories.categories[DMCategories.categories.indexOf(DMCategories.getCategory(category.name))] = category

            Util.updateChannels()

            DMCategories.saveCategories()
        }

        itemView.setOnLongClickListener {
            CategorySheet(category).show((itemView.context as AppCompatActivity).supportFragmentManager, "CategoriesSheet")
            true
        }
    }
}