package tk.zt64.plugins.dmcategories.sheets

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.widgets.BottomSheet
import com.aliucord.widgets.LinearLayout
import com.discord.stores.StoreStream
import tk.zt64.plugins.DMCategories
import tk.zt64.plugins.dmcategories.DMCategory
import tk.zt64.plugins.dmcategories.DMCategoryViewHolder
import tk.zt64.plugins.dmcategories.Util

class CategoriesSheetAdapter(private val sheet: BottomSheet, private val channelId: Long, private val categories: ArrayList<DMCategory>) : RecyclerView.Adapter<DMCategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DMCategoryViewHolder(this, LinearLayout(parent.context).apply {
        layoutParams = android.widget.LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    })

    override fun onBindViewHolder(holder: DMCategoryViewHolder, position: Int) = run { holder.nameView.text = categories[position].name }
    override fun getItemCount() = categories.size

    fun onClick(adapterPosition: Int) {
        sheet.dismiss()

        DMCategories.categories[adapterPosition].channelIds.add(channelId)
        DMCategories.saveCategories()

        Util.updateChannels()
    }
}