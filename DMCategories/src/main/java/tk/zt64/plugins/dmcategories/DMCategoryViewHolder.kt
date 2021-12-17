package tk.zt64.plugins.dmcategories

import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lytefast.flexinput.R
import tk.zt64.plugins.dmcategories.sheets.CategoriesSheetAdapter

class DMCategoryViewHolder(private val adapter: CategoriesSheetAdapter, layout: LinearLayout) : RecyclerView.ViewHolder(layout) {
    val nameView: TextView

    init {
        layout.setOnClickListener { adapter.onClick(adapterPosition) }

        nameView = TextView(layout.context, null, 0, R.i.UiKit_Settings_Item_Label).apply {
            layout.addView(this)
        }
    }
}