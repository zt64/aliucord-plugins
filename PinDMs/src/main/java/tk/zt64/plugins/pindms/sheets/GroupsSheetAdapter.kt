package tk.zt64.plugins.pindms.sheets

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.widgets.BottomSheet
import com.aliucord.widgets.LinearLayout
import com.discord.stores.StoreStream
import tk.zt64.plugins.PinDMs
import tk.zt64.plugins.pindms.DMGroup
import tk.zt64.plugins.pindms.GroupViewHolder

class GroupsSheetAdapter(private val sheet: BottomSheet, private val channelId: Long, private val groups : ArrayList<DMGroup>) : RecyclerView.Adapter<GroupViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GroupViewHolder(this, LinearLayout(parent.context).apply {
        layoutParams = android.widget.LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    })

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) = groups[position].let {
        holder.nameView.text = it.name
    }

    override fun getItemCount() = groups.size

    fun onClick(adapterPosition: Int) {
        sheet.dismiss()

        PinDMs.groups[adapterPosition].channelIds.add(channelId)
        PinDMs.saveGroups()

        StoreStream.`access$getDispatcher$p`(StoreStream.getPresences().stream).schedule {
            StoreStream.getChannels().markChanged()
        }
    }
}