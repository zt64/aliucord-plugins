package accountswitcher.settings

import android.content.Context
import android.view.Gravity
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.utils.DimenUtils
import com.lytefast.flexinput.R

class EmptyViewHolder(ctx: Context) :
    RecyclerView.ViewHolder(
        TextView(ctx, null, 0, R.i.UiKit_TextView_Medium).apply {
            text = "No accounts have been added."
            textSize = 16f
            gravity = Gravity.CENTER
            val p = DimenUtils.dpToPx(16)
            setPadding(p, p, p, p)
        }
    )