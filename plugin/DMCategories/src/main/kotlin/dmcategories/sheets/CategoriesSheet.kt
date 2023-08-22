package dmcategories.sheets

import DMCategories
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.utils.DimenUtils
import com.aliucord.views.Divider
import com.aliucord.widgets.BottomSheet
import com.discord.utilities.color.ColorCompat
import com.lytefast.flexinput.R
import dmcategories.CategoryDialog
import dmcategories.Util

class CategoriesSheet(private val channelId: Long) : BottomSheet() {
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        val ctx = requireContext()

        addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header).apply {
            text = "Categories"
        })

        addView(RecyclerView(ctx).apply {
            adapter = CategoriesSheetAdapter(this@CategoriesSheet, channelId, DMCategories.categories.filter { Util.getCurrentId() == it.userId })
            layoutManager = LinearLayoutManager(ctx)

            addItemDecoration(DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ShapeDrawable(RectShape()).apply {
                    intrinsicHeight = DimenUtils.defaultPadding
                    setTint(Color.TRANSPARENT)
                })
            })
        })

        addView(Divider(ctx))

        addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
            text = "Create Category"
            setOnClickListener {
                dismiss()
                CategoryDialog(channelId).show(parentFragmentManager, "CreateCategory")
            }
            setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(ctx, R.e.ic_add_24dp)!!.mutate().apply {
                setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal))
            }, null, null, null)
        })
    }
}