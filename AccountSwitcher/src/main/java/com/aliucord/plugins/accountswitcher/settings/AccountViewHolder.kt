package com.aliucord.plugins.accountswitcher.settings

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.utils.DimenUtils
import com.aliucord.views.ToolbarButton
import com.discord.utilities.color.ColorCompat
import com.facebook.drawee.view.SimpleDraweeView
import com.lytefast.flexinput.R

class AccountViewHolder(private val adapter: AccountAdapter, layout: LinearLayout, isSettings: Boolean) : RecyclerView.ViewHolder(layout) {
    val avatar: SimpleDraweeView
    val name: TextView
    // val id: TextView

    init {
        val ctx = layout.context
        val p = DimenUtils.dpToPx(2)

        layout.orientation = LinearLayout.HORIZONTAL
        layout.gravity = Gravity.CENTER_VERTICAL
        layout.background = GradientDrawable().apply {
            cornerRadius = 16f
            setColor(ColorCompat.getThemedColor(ctx, R.b.colorBackgroundSecondary))
        }

        DimenUtils.dpToPx(8).let { layout.setPadding(it, it, it, it) }

        avatar = SimpleDraweeView(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(DimenUtils.dpToPx(48), DimenUtils.dpToPx(48))
            clipToOutline = true
            background = ShapeDrawable(OvalShape()).apply { paint.color = Color.TRANSPARENT }
            layout.addView(this)
        }

        name = TextView(ctx, null, 0, R.h.UiKit_Settings_Item).apply {
            layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                weight = 0.6f
            }
            gravity = Gravity.CENTER_VERTICAL
            layout.addView(this)
        }

//        id = TextView(ctx, null, 0, R.h.UiKit_Settings_Item_SubText).apply {
//            layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
//                weight = 0.6f
//            }
//            gravity = Gravity.CENTER_VERTICAL
//            layout.addView(this)
//        }

        if (isSettings) {
            ToolbarButton(ctx).run {
                setPadding(p, p, p * 4, p)
                layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
                ContextCompat.getDrawable(ctx, R.d.ic_edit_24dp)!!.mutate().let {
                    Utils.tintToTheme(it)
                    setImageDrawable(it, false)
                }
                setOnClickListener { adapter.onEdit(adapterPosition) }
                layout.addView(this)
            }

            ToolbarButton(ctx).run {
                setPadding(p * 4, p, p, p)
                layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
                ContextCompat.getDrawable(ctx, R.d.ic_delete_24dp)!!.mutate().let {
                    Utils.tintToTheme(it)
                    setImageDrawable(it, false)
                }
                setOnClickListener { adapter.onRemove(adapterPosition) }
                layout.addView(this)
            }
        } else {
            layout.setOnClickListener { adapter.onClick(ctx, adapterPosition) }
        }
    }
}