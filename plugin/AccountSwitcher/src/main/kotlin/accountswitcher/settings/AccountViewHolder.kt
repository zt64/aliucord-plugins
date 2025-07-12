package accountswitcher.settings

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.utils.DimenUtils
import com.aliucord.views.ToolbarButton
import com.discord.utilities.color.ColorCompat
import com.facebook.drawee.view.SimpleDraweeView
import com.lytefast.flexinput.R

class AccountViewHolder(
    private val adapter: AccountAdapter,
    layout: LinearLayout,
    edit: Boolean
) : RecyclerView.ViewHolder(layout) {
    val avatar: SimpleDraweeView
    val name: TextView
    val userId: TextView
    val activeIndicator: View
    private val info: LinearLayout

    init {
        val ctx = layout.context
        val p = DimenUtils.dpToPx(2)

        layout.apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = GradientDrawable().apply {
                cornerRadius = 16f
                setColor(ColorCompat.getThemedColor(ctx, R.b.colorBackgroundSecondary))
            }
            DimenUtils.dpToPx(8).let { setPadding(it, it, it, it) }
        }

        avatar = SimpleDraweeView(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(DimenUtils.dpToPx(52), DimenUtils.dpToPx(52))
            clipToOutline = true
            background = ShapeDrawable(OvalShape()).apply { paint.color = Color.TRANSPARENT }
            layout.addView(this)
        }

        name = TextView(ctx, null, 0, R.i.UiKit_Settings_Item).apply {
            setPadding(p, p, p, p)
        }

        activeIndicator = TextView(ctx, null, 0, R.i.UiKit_TextView_Bold).apply {
            text = "Active"
            setTextColor(ContextCompat.getColor(ctx, R.c.status_green))
            visibility = View.GONE
        }

        userId = TextView(ctx, null, 0, R.i.UiKit_TextView_SingleLine).apply {
            textSize = 12f
            setPadding(p, p, p, p)
        }

        info = LinearLayout(ctx, null, 0, R.i.UiKit_Settings_Item).apply {
            layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                weight = 0.6f
            }
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            addView(activeIndicator)
            addView(name)
            addView(userId)
            layout.addView(this)
        }

        if (edit) {
            // Potentially enable when there are more settings for accounts
            // val editButton = ToolbarButton(ctx).apply {
            //     setPadding(p, p, p * 4, p)
            //     layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
            //     ContextCompat.getDrawable(ctx, R.e.ic_edit_24dp)!!.mutate().let {
            //         Utils.tintToTheme(it)
            //         setImageDrawable(it, false)
            //     }
            //     setOnClickListener { adapter.onEdit(adapterPosition) }
            // }
            // layout.addView(editButton)

            val removeButton = ToolbarButton(ctx).apply {
                setPadding(p * 4, p, p, p)
                layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
                ContextCompat.getDrawable(ctx, R.e.ic_delete_24dp)!!.mutate().let {
                    Utils.tintToTheme(it)
                    setImageDrawable(it, false)
                }
                setOnClickListener { adapter.onRemove(adapterPosition) }
            }
            layout.addView(removeButton)
        } else {
            layout.setOnClickListener { adapter.onClick(ctx, adapterPosition) }
        }
    }
}