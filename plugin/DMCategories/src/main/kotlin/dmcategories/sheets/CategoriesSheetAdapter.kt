package dmcategories.sheets

import DMCategories
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.widgets.BottomSheet
import com.aliucord.widgets.LinearLayout
import dmcategories.DMCategory
import dmcategories.DMCategoryViewHolder
import dmcategories.Util

@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
class CategoriesSheetAdapter(
    private val sheet: BottomSheet,
    private val channelId: Long,
    private val categories: List<DMCategory>
) : RecyclerView.Adapter<DMCategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DMCategoryViewHolder {
        return DMCategoryViewHolder(this, LinearLayout(parent.context).apply {
            layoutParams = android.widget.LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        })
    }

    override fun onBindViewHolder(holder: DMCategoryViewHolder, position: Int) {
        holder.nameView.text = categories[position].name
    }

    override fun getItemCount() = categories.size

    fun onClick(adapterPosition: Int) {
        sheet.dismiss()

        DMCategories.categories[adapterPosition].channelIds.add(channelId)
        DMCategories.saveCategories()

        Util.updateChannels()
    }
}