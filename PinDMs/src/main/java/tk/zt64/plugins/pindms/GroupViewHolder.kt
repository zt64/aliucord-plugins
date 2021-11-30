package tk.zt64.plugins.pindms

import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lytefast.flexinput.R
import tk.zt64.plugins.pindms.sheets.GroupsSheetAdapter

class GroupViewHolder(private val adapter: GroupsSheetAdapter, layout: LinearLayout) : RecyclerView.ViewHolder(layout) {
    val nameView: TextView

    init {
        val ctx = layout.context

        layout.setOnClickListener { adapter.onClick(adapterPosition) }

        nameView = TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Label).apply {
            layout.addView(this)
        }
    }
}