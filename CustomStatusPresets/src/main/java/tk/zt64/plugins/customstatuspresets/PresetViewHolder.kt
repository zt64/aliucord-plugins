package tk.zt64.plugins.customstatuspresets

import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.facebook.drawee.view.SimpleDraweeView

class PresetViewHolder(private val adapter: PresetAdapter, statusView: LinearLayout) : RecyclerView.ViewHolder(statusView) {
    private val customEmojiId = Utils.getResId("user_status_presence_custom_emoji", "id")
    private val customTextId = Utils.getResId("user_status_presence_custom_text", "id")
    private val clearButtonId = Utils.getResId("user_status_presence_custom_clear", "id")

    val customEmoji: SimpleDraweeView = statusView.findViewById(customEmojiId)
    val customText: TextView = statusView.findViewById(customTextId)

    init {
        statusView.setOnClickListener { adapter.onClick(adapterPosition) }
        statusView.findViewById<AppCompatImageView>(clearButtonId).setOnClickListener { adapter.onRemove(adapterPosition) }

        customEmoji.setOnClickListener { adapter.editEmoji(customEmoji, adapterPosition) }
    }
}